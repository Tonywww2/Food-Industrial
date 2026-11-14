#!/usr/bin/env python3
"""Exports the material master tables and the tag contents from docs/additives.

materials.json (I05): 09 tables A/B/C and the 08 §C tables (new raw materials, cultures and
enzymes, catalysts). Rows marked "见 09" (described in 09) or "（I20）", and rows whose type is not
物品 or 流体 (ores, crops, plants and trees, registered as blocks by I07 code) are skipped, as is
vanilla water.

Names: in 09 a trailing parenthesis with Han characters is the common name (专业名称（日常名称）,
text-standard §2.1); in 08 parentheses are part of the name (qualifiers such as 工程菌（漆酶）,
2026-11-05).

Purity (I28, I29): the 09 "纯度类别" column ("—" = no purity: enzymes, distilled water, mixtures of
several substances); in 08 §C cultures are K11, enzymes, consumables and the other rows carry no
purity (2026-11-05); the 09 §D table of source purities and single-material overrides (checked
against 10 §1.5), and the category bounds of 10 §1.4, exported to purity_categories.json. A material
bears a stored purity when it has a category other than K0, or K0 with a source purity
(additives/10 §1.3).

tag_imports.json (I06): the 08 §A/§B tables (one tag per row, per-node content columns) plus the
catalyst tags of 09 chemicals listed in the 08 §C catalyst table, and an empty tag for each crop or ore
of 08 §C that has no source yet (I30), so recipes can already name it. Only backticked entries are
exported; "本模组……" entries come from the master table at data generation.

Usage: python3 tools/materials/export_materials.py   (from the repository root)
"""
import json
import pathlib
import re
import sys

sys.dont_write_bytecode = True
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parents[1] / 'textures'))
import groups  # noqa: E402  (tools/textures: texture kinds, I33)

ROOT = pathlib.Path(__file__).resolve().parents[2]
DOCS = ROOT / 'docs' / 'additives'
OUT = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'materials.json'
TAGS_OUT = OUT.parent / 'tag_imports.json'
CATEGORIES_OUT = OUT.parent / 'purity_categories.json'

STATES = {'固': 'solid', '液': 'liquid', '气': 'gas', '溶液': 'solution', '物品': 'solid', '流体': 'liquid'}
CATEGORIES = {'化学品': 'chemical', '催化剂': 'catalyst', '原料': 'raw', '菌种': 'culture', '酶': 'enzyme', '耗材': 'consumable',
              '原版': 'vanilla', '香料': 'flavor'}
# Fluid colours left blank in 09 and 08 §C (batch 5, the implementation chooses them, I31, pending review): by a
# kind named in the name, else by state (single substances nearly colourless).
FLAVOR_COLOURS = (('血', '#7A1414'), ('胶乳', '#F4F0E4'), ('糖蜜', '#3A2410'), ('发酵液', '#C8B070'), ('酶解液', '#D8C8A0'),
                  ('石脑油', '#F0ECD8'), ('石油醚', '#ECEEF0'), ('溴', '#8A2A10'), ('酊', '#C89850'), ('浸膏', '#8A5A2B'), ('净油', '#A8783A'), ('油树脂', '#9A4A20'), ('香脂', '#7A4A22'),
                  ('提取物', '#B08850'), ('浓缩液', '#B08850'), ('馏出液', '#E8E4D8'), ('蒸馏物', '#E8E4D8'), ('烟熏液', '#8A4A20'),
                  ('油', '#F0DC90'))


# 09 颜色的取值顺序 ② (11 问题 42): natural flavours by product form, the template of their route (18 §2, 19 §5).
FORM_COLOURS = {'NT1': '#F2E3A0', 'NT2': '#F2E3A0', 'NT12': '#F2E3A0', 'NT4': '#5C4A2E', 'NT5': '#8A5A24', 'NT6': '#5A3418',
                'NT3': '#7A4E26', 'NT7': '#7A4E26', 'NT8': '#9A6A30'}


def natural_forms():
    """Material id -> the form template (NT1–NT8, NT12) its route names first, for natural flavours and concretes."""
    result = {}
    for _, rows in tables(DOCS / '18-batch-5-flavors.md', '| 编码 |', '物料 ID'):
        for row in rows:
            found = [t for t in re.findall(r'\b(NT\d+)\b', row['模板']) if t in FORM_COLOURS]
            if found:
                result.setdefault(row['物料 ID'].strip('`'), found[0])
    for _, rows in tables(DOCS / '19-batch-5-synthetic-flavors.md', '| 建议 ID |', '路线'):
        for row in rows:
            match = re.match(r'^(NT\d+)', row['路线'])
            if match and match.group(1) in FORM_COLOURS:
                result.setdefault(row['建议 ID'].strip('`'), match.group(1))
    return result


def flavor_colour(zh, state, purity_category):
    if state == 'gas':
        return '#ECECF4'
    if purity_category is None:
        for key, colour in FLAVOR_COLOURS:
            if key in zh:
                return colour
        return '#D8C088'
    return '#F2EFE2'
# Purity categories of additives/10 §1.4 and §4.7.
PURITY_CATEGORY_IDS = {
    'K0': 'k0_raw', 'K1': 'k1_inorganic', 'K2': 'k2_sugar', 'K3': 'k3_organic_solid',
    'K4': 'k4_organic_liquid', 'K5': 'k5_natural_extract', 'K6': 'k6_colloid', 'K7': 'k7_fat',
    'K8': 'k8_emulsifier', 'K9': 'k9_gas', 'K10': 'k10_synthetic_color', 'K11': 'k11_culture',
    'K12': 'k12_compound',
}
UNGRADED = 'K0'
GRADES = ('crude', 'refined', 'food', 'lab')
GRADE_COLUMNS = ('粗制', '精制', '食品级', '实验室级')
HAN = re.compile(r'[一-鿿]')


def cells(line):
    return [c.strip() for c in line.strip().strip('|').split('|')]


def tables(path, header_prefix, marker='ID'):
    """Yields (header, rows) for every table whose header starts with header_prefix and has a marker column."""
    lines = path.read_text(encoding='utf-8').split('\n')
    i = 0
    while i < len(lines):
        if lines[i].startswith(header_prefix) and any(marker in c for c in cells(lines[i])):
            header = cells(lines[i])
            rows = []
            i += 2
            while i < len(lines) and lines[i].startswith('|'):
                rows.append(dict(zip(header, cells(lines[i]))))
                i += 1
            yield header, rows
        i += 1


def formula(cell):
    match = re.search(r'`([^`]+)`', cell)
    return match.group(1) if match else None


def split_common(zh, en):
    """'氯化钠（盐）', 'Sodium Chloride (Salt)' -> professional and common names.

    Only a trailing parenthesis whose Chinese text contains Han characters is a common name;
    others ('三孢布拉霉（+）') are part of the name. An English name without a parenthesis has no
    separate English common name ('过氧化氢（双氧水）', 'Hydrogen Peroxide').
    """
    match = re.fullmatch(r'(.+)（([^（）]+)）', zh)
    if not match or not HAN.search(match.group(2)):
        return {'zh': zh, 'en': en}, None
    en_match = re.fullmatch(r'(.+) \(([^()]+)\)', en)
    if not en_match:
        return {'zh': match.group(1), 'en': en}, {'zh': match.group(2), 'en': en}
    return ({'zh': match.group(1), 'en': en_match.group(1)},
            {'zh': match.group(2), 'en': en_match.group(2)})


def written_percent(cell, where):
    """'83%' -> 83, '3.5%' -> 3.5; checks the decimals allowed by the display rule (10 §1.2)."""
    match = re.fullmatch(r'(\d+(?:\.\d+)?)%', cell)
    if not match:
        sys.exit(f'{where}: expected a percentage, got {cell!r}')
    text = match.group(1)
    value = float(text)
    decimals = len(text.split('.')[1].rstrip('0')) if '.' in text else 0
    allowed = 2 if value < 1 else 1 if value < 10 else 0 if value < 90 else 1 if value < 99.9 else 2
    if value > 100 or decimals > allowed:
        sys.exit(f'{where}: {cell} has more decimals than the display shows in its range ({allowed})')
    number = float(text) if decimals else int(float(text))
    return number


def grade_cell(cell, where):
    """Override cell of 09 §D / bound cell of 10: '—' inherits, '不适用' means no such grade."""
    if cell == '—':
        return ...
    if cell == '不适用':
        return None
    return written_percent(cell, where)


def purity_categories():
    """10 §1.4: lower bounds of each category ('—' = no grade, K0)."""
    categories = []
    for _, rows in tables(DOCS / '10-purity-jei-ae2.md', '| 类别 |', '粗制'):
        for row in rows:
            key = row['类别'].split()[0]
            if key not in PURITY_CATEGORY_IDS:
                sys.exit(f'Unknown purity category {row["类别"]}')
            grades = {}
            for grade, column in zip(GRADES, GRADE_COLUMNS):
                value = grade_cell(row[column], f'10 §1.4 {key} {column}')
                grades[grade] = None if value is ... else value
            defined = [v for v in grades.values() if v is not None]
            if defined != sorted(set(defined)):
                sys.exit(f'10 §1.4 {key}: bounds must increase strictly')
            if key == UNGRADED and defined:
                sys.exit('10 §1.4 K0 must have no grades')
            categories.append({'id': 'foodindustrial:' + PURITY_CATEGORY_IDS[key], 'grades': grades})
    if len(categories) != len(PURITY_CATEGORY_IDS):
        sys.exit(f'10 §1.4 lists {len(categories)} categories, expected {len(PURITY_CATEGORY_IDS)}')
    return categories


def purity_table():
    """09 §D: source purity and overrides per material id."""
    result = {}
    for _, rows in tables(DOCS / '09-chemical-formulas.md', '| 建议 ID |', '来源纯度'):
        for row in rows:
            mid = row['建议 ID'].strip('`')
            source = None if row['来源纯度'] == '—' else written_percent(row['来源纯度'], f'09 §D {mid} 来源纯度')
            overrides = {}
            for grade, column in zip(GRADES, GRADE_COLUMNS):
                value = grade_cell(row[column], f'09 §D {mid} {column}')
                if value is not ...:
                    overrides[grade] = value
            if mid in result:
                sys.exit(f'09 §D lists {mid} twice')
            result[mid] = (source, overrides)
    return result


def check_overrides(materials, table):
    """Every 10 §1.5 row must match 09 §D for the materials that exist; others are reported."""
    by_id = {m['id']: m for m in materials}
    pending = []
    for _, rows in tables(DOCS / '10-purity-jei-ae2.md', '| 物料 | ID |', '粗制'):
        for row in rows:
            ids = ticks(row['ID'])
            expected = {}
            for grade, column in zip(GRADES, GRADE_COLUMNS):
                expected[grade] = grade_cell(row[column], f'10 §1.5 {row["物料"]} {column}')
            pending.extend(part for part in re.split('、', row['ID']) if not ticks(part))
            for mid in ids:
                if mid not in by_id:
                    pending.append(mid)
                    continue
                got = table.get(mid, (None, {}))[1]
                if got != expected:
                    sys.exit(f'Overrides of {mid}: 09 §D has {got}, 10 §1.5 has {expected}')
    return pending


def ticks(cell):
    return re.findall(r'`([^`]+)`', cell)


TAG_ENTRY = re.compile(r'#?[a-z0-9_.-]+:[a-z0-9_./-]+')


def tag_imports(eight, declared=()):
    """08 §A/§B rows, the catalyst tags of 09 chemicals (08 §C) and the declared empty tags of the crops
    and ores that have no source yet (08 §C, I30): those are written empty, for packs to fill."""
    merged = {}

    def add(tag, kind, forge, c):
        for entry in forge + c:
            if not TAG_ENTRY.fullmatch(entry):
                sys.exit(f'Invalid tag entry in {tag}: {entry}')
        key = (tag, kind)
        if key not in merged:
            merged[key] = {'tag': tag, 'kind': kind, 'forge': [], 'c': []}
        for name, entries in (('forge', forge), ('c', c)):
            for entry in entries:
                if entry not in merged[key][name]:
                    merged[key][name].append(entry)

    for _, rows in tables(eight, '|', '默认内容（1.20.1）'):
        for row in rows:
            ref = row['配方引用标签']
            add(ticks(ref)[0], 'fluid' if '（流体）' in ref else 'item',
                ticks(row['默认内容（1.20.1）']), ticks(row['默认内容（1.21 起）']))
    for _, rows in tables(eight, '| 催化剂 | 配方引用标签 |'):
        for row in rows:
            if '见 09' in row['ID']:
                item = 'foodindustrial:' + row['ID'].split('（')[0]
                add(ticks(row['配方引用标签'])[0], 'item', [item], [item])
    for tag in declared:
        add(tag, 'item', [], [])
    return [entry for entry in merged.values() if entry['forge'] or entry['c'] or entry['tag'] in declared]


QUALIFIER = re.compile(r'\(([^()（）]*)\)$')


def full_width_qualifier(zh):
    """A trailing half-width group of Chinese words in a GB name is a qualifier, not a common name (丁香花蕾酊(提取物)):
    it stays part of the name and is written full-width (text standard §1.4, I31 2026-11-11)."""
    match = QUALIFIER.search(zh)
    if match and HAN.search(match.group(1)) and not re.search(r'[0-9A-Za-z+±/,αβγδεω-]', match.group(1)) \
            and not match.group(1).startswith('或'):
        return zh[:match.start()] + '（' + match.group(1) + '）'
    return zh


def material(mid, zh, en, state, category, color, formula_text, gtceu, tag, source, purity_category=None, split=True):
    """split: whether a trailing parenthesis can be a common name (09 rows); in 08 it is part of the name."""
    category = CATEGORIES[category]
    professional, common = split_common(zh, en) if split else ({'zh': zh, 'en': en}, None)
    zh = full_width_qualifier(zh)
    professional = {'zh': full_width_qualifier(professional['zh']), 'en': professional['en']}
    if purity_category is not None and purity_category not in PURITY_CATEGORY_IDS:
        sys.exit(f'Unknown purity category of {mid}: {purity_category}')
    return {
        'id': mid,
        'name': {'zh': zh, 'en': en},
        'professional': professional,
        'common': common,
        'formula': formula_text,
        'state': state,
        'category': category,
        'purity_category': purity_category,
        'color': None if color in (None, '—', '') else color,
        'gtceu': gtceu,
        'tag': tag,
        'hazards': [],
        'source': source,
    }


def deferred_codes():
    """Flavours put off by the designer (11 问题 36): rows of 18 / 19 whose source cell says 暂缓."""
    codes = set()
    for name in ('18-batch-5-flavors.md', '19-batch-5-synthetic-flavors.md'):
        for _, rows in tables(DOCS / name, '| 编码 |', '物料 ID'):
            codes.update(row['编码'] for row in rows if '暂缓' in row['原料'])
    return codes


def only_deferred(note, deferred):
    """A batch-5 row used only by put-off flavours: neither it nor its own intermediates are registered (I31)."""
    if '第五批' not in note:
        return False
    codes = set(re.findall(r'\b[NS]\d{3,4}\b', note))
    others = re.findall(r'`[a-z0-9_]+`|\b\d{3}\b', note)
    return bool(codes) and codes <= deferred and not others


def main():
    materials = []
    deferred = deferred_codes()
    forms = natural_forms()
    for _, rows in tables(DOCS / '09-chemical-formulas.md', '| 建议 ID |', '纯度类别'):
        for row in rows:
            mid = row['建议 ID'].strip('`')
            category = row['分类']
            if category == '原版' or only_deferred(row['备注'], deferred):
                continue
            state = STATES[row['状态']]
            gt = row['GTCEu 材料']
            gtceu = None if gt.startswith('—') else re.split(r'[（ /]', gt)[0]
            tag = f'chemicals/{mid}' if state == 'solid' else mid
            purity_category = None if row['纯度类别'] == '—' else row['纯度类别']
            colour = row['颜色']
            if colour in ('—', '') and mid in forms and category == '香料':
                colour = FORM_COLOURS[forms[mid]]
            elif state != 'solid' and colour in ('—', ''):
                colour = flavor_colour(row['名称'], state, purity_category)
            materials.append(material(mid, row['名称'], row['英文名（待审）'], state, category, colour,
                                      formula(row['化学式 / 组成']), gtceu, tag, '09', purity_category))
    eight = DOCS / '08-raw-materials.md'
    declared = []  # crops and ores without a source yet: empty tags, so recipes can name them (I30)
    no_source = []  # every raw material without a source yet (08 §C); the purity report lists their routes apart (I30)
    for _, rows in tables(eight, '| 原料 | 配方引用标签 |'):
        for row in rows:
            mid = row['ID']
            if '见 09' in mid or mid.startswith('（') or row['配方引用标签'].startswith('`foods/'):
                continue  # described in 09, or a finished product of foods.json (I21)
            if '暂无来源' in row['获取']:
                no_source.append(re.search(r'`([^`]+)`', row['配方引用标签']).group(1))
            if row['类型'] not in ('物品', '流体'):
                if '暂无来源' in row['获取']:
                    declared.append(re.search(r'`([^`]+)`', row['配方引用标签']).group(1))
                continue  # ores, crops, plants and trees: blocks registered by I07 code
            state = STATES[row['类型']]
            tag = re.search(r'`([^`]+)`', row['配方引用标签']).group(1)
            colour = row['颜色']
            if state != 'solid' and colour in ('—', ''):
                colour = flavor_colour(row['原料'], state, None)
            materials.append(material(mid, row['原料'], row['英文名（待审）'], state, row['分类'], colour,
                                      None, None, tag, '08 §C', split=False))
    for _, rows in tables(eight, '| 菌种 / 酶 | 配方引用标签 |'):
        for row in rows:
            mid = row['ID']
            if mid.startswith('（'):
                continue
            tag = re.search(r'`([^`]+)`', row['配方引用标签']).group(1)
            purity_category = 'K11' if row['分类'] == '菌种' else None  # enzymes and consumables: none
            materials.append(material(mid, row['菌种 / 酶'], row['英文名（待审）'], 'solid', row['分类'], None,
                                      None, None, tag, '08 §C', purity_category, split=False))
    for _, rows in tables(eight, '| 催化剂 | 配方引用标签 |'):
        for row in rows:
            mid = row['ID']
            if '见 09' in mid:
                continue
            tag = re.search(r'`([^`]+)`', row['配方引用标签']).group(1)
            materials.append(material(mid, row['催化剂'], row['英文名（待审）'], 'solid', row['分类'], None,
                                      formula(row['显示化学式']), None, tag, '08 §C', split=False))
    ids = [m['id'] for m in materials]
    duplicates = sorted({i for i in ids if ids.count(i) > 1})
    if duplicates:
        sys.exit(f'Duplicate material ids: {duplicates}')
    table = purity_table()
    by_id = {m['id']: m for m in materials}
    for mid in table:
        if mid not in by_id:
            sys.exit(f'09 §D lists {mid}, which is not in the master table')
        if by_id[mid]['purity_category'] is None:
            sys.exit(f'09 §D lists {mid}, which carries no purity')
    pending = check_overrides(materials, table)
    for m in materials:
        key = m.pop('purity_category')
        if key is None:
            m['purity'] = None
            m['bearer'] = False
            continue
        source, overrides = table.get(m['id'], (None, {}))
        if key == UNGRADED and overrides:
            sys.exit(f'{m["id"]} is K0 and cannot override grades')
        data = {'category': 'foodindustrial:' + PURITY_CATEGORY_IDS[key]}
        if source is not None:
            data['source_purity'] = source
        if overrides:
            data['grades'] = overrides
        m['purity'] = data
        m['bearer'] = key != UNGRADED or source is not None
    for m in materials:
        # texture kind (I33): fluids liquid, viscous or gas; solids the template their item texture is drawn from
        m['texture'] = groups.material(m)['kind']
    for m in materials:
        if not re.fullmatch(r'[a-z0-9_]+', m['id']):
            sys.exit(f'Invalid id: {m["id"]}')
        if m['state'] != 'solid' and m['color'] is None:
            sys.exit(f'Fluid without colour: {m["id"]}')
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text(json.dumps({'materials': materials, 'pending_sources': sorted(set(no_source))}, ensure_ascii=False, indent=1) + '\n',
                   encoding='utf-8')
    categories = purity_categories()
    CATEGORIES_OUT.write_text(json.dumps({'categories': categories}, ensure_ascii=False, indent=1) + '\n', encoding='utf-8')
    print(f'{len(categories)} purity categories -> {CATEGORIES_OUT.relative_to(ROOT)}')
    print(f'{len(table)} materials with source purity or overrides (09 §D)')
    if pending:
        print('10 §1.5 rows without a registered material (applied once registered): ' + '、'.join(pending))
    counts = {}
    for m in materials:
        key = (m['category'] + ('*' if m['bearer'] else ''), m['state'])
        counts[key] = counts.get(key, 0) + 1
    imports = tag_imports(eight, declared)
    # foodindustrial: entries may also be I07 blocks; data generation checks that they exist.
    TAGS_OUT.write_text(json.dumps({'tags': imports}, ensure_ascii=False, indent=1) + '\n', encoding='utf-8')
    print(f'{len(imports)} tags -> {TAGS_OUT.relative_to(ROOT)}')
    print(f'{len(materials)} materials -> {OUT.relative_to(ROOT)}')
    for key in sorted(counts):
        print(f'  {key[0]:9} {key[1]:9} {counts[key]}')


if __name__ == '__main__':
    main()
