#!/usr/bin/env python3
"""Collects the per-100 g composition of every food-composition entry the product inputs name (I45) into
tools/foods/nutrition/sources.json, so the exporter (tools/foods/nutrition.py) works offline and every number has
its source on record.

Sources (foods/03 §5 order, foods/agents/food-nutrition-data.md §5.1):
- cn:<n>   中国食物成分表, via 食物营养成分查询平台 https://nlc.chinanutri.cn/fq/foodinfo/<n>.html
- fdc:<id> USDA FoodData Central bulk CSV (SR Legacy 2018-04, Survey 2024-10-31, Foundation 2025-04-24; Branded
           entries through the FDC API when FDC_API_KEY is set)
- jp:<code> 日本食品標準成分表2020年版（八訂）本表, 文部科学省 Excel 20201225-mxt_kagsei-mext_01110_012.xlsx
- retn:<code> USDA Table of Nutrient Retention Factors, Release 6 (2007), retn06.txt: per-nutrient retention (%)

Values: a number; 0 where the source reports zero or a trace (Tr, 未检出); null where the source has no value
(not analysed, “—”, “-”, blank). Downloads are cached in build/cache/nutrition/.

Usage: python3 tools/foods/nutrition/fetch_sources.py   (needs network for anything not cached yet)
"""
import csv
import html
import json
import os
import pathlib
import re
import subprocess
import sys
import time
import zipfile

HERE = pathlib.Path(__file__).resolve().parent
ROOT = HERE.parents[2]
CACHE = ROOT / 'build' / 'cache' / 'nutrition'
OUT = HERE / 'sources.json'
FDC_SETS = ['FoodData_Central_sr_legacy_food_csv_2018-04', 'FoodData_Central_survey_food_csv_2024-10-31',
            'FoodData_Central_foundation_food_csv_2025-04-24']
MEXT = '20201225-mxt_kagsei-mext_01110_012.xlsx'

# our key -> (unit factor applied to the source value); the nutrients of foods/02 §5.3–5.6 (I46, 2026-11-19: vitamins
# C, B1, D, A and calcium, iron, zinc). Vitamin A is preformed (retinol); provitamin A is computed per source below.
FDC_KEYS = {'caffeine_mg': ('262', 1), 'vitamin_c_mg': ('401', 1), 'vitamin_b1_mg': ('404', 1), 'vitamin_d_ug': ('328', 1),
            'vitamin_a_ug': ('319', 1), 'calcium_mg': ('301', 1), 'iron_mg': ('303', 1), 'zinc_mg': ('309', 1)}
# provitamin A as µg RAE: β-carotene ÷ 12, α-carotene and β-cryptoxanthin ÷ 24 (IOM 2001)
FDC_CAROTENOIDS = (('321', 12), ('322', 24), ('334', 24))
FDC_MACROS = {'energy_kcal': '208', 'protein_g': '203', 'fat_g': '204', 'carb_g': '205', 'sugars_g': '269', 'water_g': '255',
              'alcohol_g': '221'}
CN_KEYS = {'vitamin_c_mg': '维生素C(Vitamin C)', 'vitamin_b1_mg': '硫胺素(Thiamin)', 'calcium_mg': '钙(Ca)', 'iron_mg': '铁(Fe)',
           'zinc_mg': '锌(Zn)'}
CN_MACROS = {'energy_kj': '能量(Energy)', 'protein_g': '蛋白质(Protein)', 'fat_g': '脂肪(Fat)', 'carb_g': '碳水化合物(CHO)',
             'water_g': '水分(Water)'}
JP_KEYS = {'vitamin_c_mg': 'VITC', 'vitamin_b1_mg': 'THIA', 'vitamin_d_ug': 'VITD', 'vitamin_a_ug': 'RETOL', 'calcium_mg': 'CA',
           'iron_mg': 'FE', 'zinc_mg': 'ZN'}
JP_MACROS = {'energy_kcal': 'ENERC_KCAL', 'protein_g': 'PROT-', 'fat_g': 'FAT-', 'carb_g': 'CHOCDF-', 'water_g': 'WATER',
             'alcohol_g': 'ALC'}


def download(url, target):
    if not target.exists():
        target.parent.mkdir(parents=True, exist_ok=True)
        subprocess.run(['curl', '-sS', '-f', '-m', '600', '-o', str(target), url], check=True)
        time.sleep(0.3)
    return target


# ---- USDA FoodData Central ----
_fdc = None


def fdc_index():
    global _fdc
    if _fdc is not None:
        return _fdc
    foods = {}
    for name in FDC_SETS:
        z = download(f'https://fdc.nal.usda.gov/fdc-datasets/{name}.zip', CACHE / f'{name}.zip')
        with zipfile.ZipFile(z) as archive:
            def table(file):
                member = next(m for m in archive.namelist() if m.endswith('/' + file) or m == file)
                return csv.DictReader(archive.open(member).read().decode('utf-8').splitlines())
            nutrients = {r['id']: r['nutrient_nbr'] for r in table('nutrient.csv')}
            for r in table('food.csv'):
                foods[r['fdc_id']] = {'name': r['description'], 'type': r['data_type'], 'n': {}}
            for r in table('food_nutrient.csv'):
                food = foods.get(r['fdc_id'])
                if food is not None and r['amount'] != '':
                    # the Survey (FNDDS) file gives the nutrient number where the others give the nutrient id
                    food['n'][nutrients.get(r['nutrient_id'], r['nutrient_id'])] = float(r['amount'])
    _fdc = foods
    return foods


BRANDED = 'FoodData_Central_branded_food_csv_2025-04-24'
_branded = {}


def fdc_branded(fdc_id):
    """A Branded entry from the bulk file (442 MB, downloaded once; only the rows of wanted ids are read)."""
    if not _branded:
        wanted = {ref.split(':')[1] for ref in refs() if ref.startswith('fdc:')} - set(fdc_index())
        z = download(f'https://fdc.nal.usda.gov/fdc-datasets/{BRANDED}.zip', CACHE / f'{BRANDED}.zip')
        with zipfile.ZipFile(z) as archive:
            def rows(file):
                member = next(m for m in archive.namelist() if m.endswith('/' + file) or m == file)
                with archive.open(member) as raw:
                    import io
                    yield from csv.DictReader(io.TextIOWrapper(raw, encoding='utf-8'))
            nutrients = {r['id']: r['nutrient_nbr'] for r in rows('nutrient.csv')}
            for r in rows('food.csv'):
                if r['fdc_id'] in wanted:
                    _branded[r['fdc_id']] = {'name': r['description'], 'type': r['data_type'], 'n': {}}
            for r in rows('food_nutrient.csv'):
                food = _branded.get(r['fdc_id'])
                if food is not None and r['amount'] != '' and r['nutrient_id'] in nutrients:
                    food['n'][nutrients[r['nutrient_id']]] = float(r['amount'])
    return _branded.get(str(fdc_id))


def fdc_api(fdc_id):
    key = os.environ.get('FDC_API_KEY')
    cached = CACHE / 'fdc_api' / f'{fdc_id}.json'
    if not cached.exists():
        if not key:
            raise SystemExit(f'fdc:{fdc_id} is not in the bulk files; set FDC_API_KEY to fetch it')
        download(f'https://api.nal.usda.gov/fdc/v1/food/{fdc_id}?api_key={key}', cached)
    data = json.loads(cached.read_text(encoding='utf-8'))
    n = {}
    for item in data.get('foodNutrients', []):
        nutrient = item.get('nutrient', {})
        if 'amount' in item and nutrient.get('number'):
            n[nutrient['number']] = float(item['amount'])
    return {'name': data.get('description'), 'type': data.get('dataType'), 'n': n}


def fdc_entry(fdc_id):
    food = fdc_index().get(str(fdc_id)) or fdc_branded(fdc_id) or fdc_api(fdc_id)
    values = {key: (round(food['n'][nbr] * factor, 6) if nbr in food['n'] else None) for key, (nbr, factor) in FDC_KEYS.items()}
    # provitamin A needs β-carotene; α-carotene and β-cryptoxanthin count as 0 when not given
    values['provitamin_a_ug'] = (round(sum(food['n'].get(nbr, 0.0) / divisor for nbr, divisor in FDC_CAROTENOIDS), 6)
                                 if '321' in food['n'] else None)
    macros = {key: food['n'].get(nbr) for key, nbr in FDC_MACROS.items()}
    return {'name': food['name'], 'source': f'USDA FoodData Central {fdc_id}（{food["type"]}）',
            'url': f'https://fdc.nal.usda.gov/food-details/{fdc_id}/nutrients', 'per100g': values, 'macros': macros}


# ---- 中国食物成分表 ----
def cn_value(cell):
    if cell is None:
        return None
    value = html.unescape(cell).strip()
    if value in ('', '—', '-', 'un'):
        return None
    if value in ('Tr', '0'):
        return 0.0
    m = re.match(r'^([\d.]+)\s*(kJ|kcal|mg|μg|g|%)?$', value)
    return float(m.group(1)) if m else None


def cn_entry(n):
    page = download(f'https://nlc.chinanutri.cn/fq/foodinfo/{n}.html', CACHE / 'cn' / f'{n}.html').read_text(encoding='utf-8', errors='replace')
    name = re.search(r'<title>(.*?)-食物营养成分查询平台</title>', page).group(1)
    cells = {label.strip(): value for label, value in re.findall(r'class="td_left">([^<]+)</td>\s*<td>([^<]*)</td>', page)}
    values = {key: cn_value(cells.get(label)) for key, label in CN_KEYS.items()}
    # The platform's “维生素A” leaves carotene out (I46, 2026-11-19: 海苔 条目 1351 carotene 9,573 µg with vitamin A
    # 0.0, 月饼 1227 and 1228 carotene 40 µg with 0.0), so it is preformed vitamin A (retinol); provitamin A is the
    # carotene (µg) as β-carotene, ÷ 12 for µg RAE.
    carotene = cn_value(cells.get('胡萝卜素(Carotene)'))
    values['vitamin_a_ug'] = cn_value(cells.get('维生素A(Vitamin)'))
    values['provitamin_a_ug'] = None if carotene is None else round(carotene / 12, 6)
    macros = {key: cn_value(cells.get(label)) for key, label in CN_MACROS.items()}
    return {'name': name, 'source': f'中国食物成分表（食物营养成分查询平台）条目 {n}',
            'url': f'https://nlc.chinanutri.cn/fq/foodinfo/{n}.html', 'per100g': values, 'macros': macros,
            'raw': {label: html.unescape(cells[label]).strip() for label in cells if label in set(CN_KEYS.values()) | {'胡萝卜素(Carotene)', '维生素A(Vitamin)'}}}


# ---- 日本食品標準成分表 ----
_jp = None


def jp_value(cell):
    if cell is None:
        return None
    value = str(cell).strip().strip('()')
    if value in ('', '-', '…'):
        return None
    if value in ('Tr', '0'):
        return 0.0
    try:
        return float(value)
    except ValueError:
        return None


def jp_index():
    global _jp
    if _jp is None:
        import openpyxl
        book = openpyxl.load_workbook(download(f'https://www.mext.go.jp/content/{MEXT}', CACHE / MEXT), read_only=True)
        rows = list(book['表全体'].iter_rows(values_only=True))
        ids = {str(code).strip(): i for i, code in enumerate(rows[11]) if code}
        _jp = {'ids': ids, 'rows': {str(r[1]): r for r in rows[12:] if r[1]}}
    return _jp


def jp_entry(code):
    index = jp_index()
    row = index['rows'][code]
    values = {key: jp_value(row[index['ids'][col]]) for key, col in JP_KEYS.items()}
    # provitamin A: β-carotene equivalents (β-carotene + ½ α-carotene + ½ β-cryptoxanthin) ÷ 12 for µg RAE
    equivalents = jp_value(row[index['ids']['CARTBEQ']])
    values['provitamin_a_ug'] = None if equivalents is None else round(equivalents / 12, 6)
    macros = {key: jp_value(row[index['ids'][col]]) for key, col in JP_MACROS.items()}
    return {'name': row[3].replace('　', ' '), 'source': f'日本食品標準成分表2020年版（八訂）{code}',
            'url': f'https://www.mext.go.jp/content/{MEXT}', 'per100g': values, 'macros': macros}


# ---- USDA retention factors ----
RETN_KEYS = {'vitamin_c_mg': '401', 'vitamin_b1_mg': '404', 'vitamin_a_ug': '392', 'provitamin_a_ug': '321', 'calcium_mg': '301',
             'iron_mg': '303', 'zinc_mg': '309'}


def retn_entry(code):
    text = download('https://www.ars.usda.gov/ARSUserFiles/80400525/Data/retn/retn06.txt', CACHE / 'retn06.txt').read_text(encoding='latin-1')
    factors, name = {}, None
    for line in text.splitlines():
        cells = [c.strip('~') for c in line.split('^')]
        if cells[0] == code:
            name = cells[2]
            factors[cells[3]] = float(cells[5])
    if not factors:
        raise SystemExit(f'retn:{code} not in retn06.txt')
    return {'name': name, 'source': f'USDA Table of Nutrient Retention Factors, Release 6 (2007) {code}',
            'url': 'https://www.ars.usda.gov/ARSUserFiles/80400525/Data/retn/retn06.pdf',
            'retention': {key: factors.get(nbr) for key, nbr in RETN_KEYS.items()}}


def refs():
    """Every source reference the batch inputs name."""
    found = set()

    def walk(value):
        if isinstance(value, str):
            if re.fullmatch(r'(cn|fdc|jp|retn):[0-9]+', value):
                found.add(value)
        elif isinstance(value, dict):
            for inner in value.values():
                walk(inner)
        elif isinstance(value, list):
            for inner in value:
                walk(inner)
    for path in sorted(HERE.glob('batch*.json')):
        walk(json.loads(path.read_text(encoding='utf-8')))
    return found


def main():
    old = json.loads(OUT.read_text(encoding='utf-8')) if OUT.exists() else {}
    result = {}
    for ref in sorted(refs(), key=lambda r: (r.split(':')[0], int(r.split(':')[1]))):
        if ref in old and '--refresh' not in sys.argv:
            result[ref] = old[ref]
            continue
        kind, key = ref.split(':')
        result[ref] = {'cn': cn_entry, 'fdc': fdc_entry, 'jp': jp_entry, 'retn': retn_entry}[kind](key)
        print('fetched', ref, result[ref]['name'])
    OUT.write_text(json.dumps(result, ensure_ascii=False, indent=1) + '\n', encoding='utf-8')
    print(f'{len(result)} entries -> {OUT.relative_to(ROOT)}')


if __name__ == '__main__':
    main()
