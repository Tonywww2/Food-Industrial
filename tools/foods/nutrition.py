#!/usr/bin/env python3
"""Builds the finished products' nutrition (I45) from the batch inputs tools/foods/nutrition/batch*.json and the
composition sources tools/foods/nutrition/sources.json (fetch_sources.py), checks it, and writes:

- src/main/resources/foodindustrial/food_nutrition.json: per product the serving, energy, hunger, saturation
  coefficient and per-serving components, read by the mod (food values at registration, nutrition data generation);
- build/reports/foodindustrial/food_nutrition.md: the review table, warnings and the coverage report.

Rules (foods/01 §4, foods/04 §5, foods/02 §4, foods/03 §2; decisions of 2026-11-19 in the I45 plan):
- serving: grams, or millilitres × density (beverages 1.0 g/mL, foods/21 §1; alcoholic drinks by their density);
- hunger = round(20 × √(kcal / 2,000)), checked against the design value;
- saturation coefficient by the energy shares of protein and free sugar (4 kcal/g each): free sugar ≥ 25 % → 0.1,
  protein ≥ 30 % → 0.8, ≥ 10 % → 0.6, else 0.3;
- vitamins and minerals from the composition entry the card uses (scaled to the serving), or from a recipe mix of
  entries with USDA retention factors; a value the source does not give stays out (never 0); in a mix an ingredient
  without the value counts as 0, and a value no ingredient gives stays out (I46, 2026-11-19);
- fast sugar = free sugar (WHO), from the card's numbers or from the card's recipe (sweeteners' sugar share of all
  raw ingredients × serving, losses not counted);
- caffeine and alcohol from the card (alcohol = % vol × mL × 0.789); nitrite 0, plus the colour-retention agent's
  residue and the misused nitrite (= the serving's salt) as conditional amounts.

Usage: python3 tools/foods/nutrition.py      (exit 1 on errors)
"""
import json
import math
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[2]
INPUTS = ROOT / 'tools' / 'foods' / 'nutrition'
SOURCES = INPUTS / 'sources.json'
FOODS = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'foods.json'
OUT = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'food_nutrition.json'
REPORT = ROOT / 'build' / 'reports' / 'foodindustrial' / 'food_nutrition.md'
NO_SOURCE = ROOT / 'build' / 'reports' / 'foodindustrial' / 'no_source.md'

# per_serving keys (foods/03 §2) in order: the acute components, then the nutrients (I46, 2026-11-19: vitamins C, B1,
# D, A — preformed, plus provitamin A as µg RAE — and calcium, iron, zinc)
DESIGN_KEYS = ['caffeine_mg', 'alcohol_g', 'fast_sugar_g', 'nitrite_mg']
COMPOSITION_KEYS = ['vitamin_c_mg', 'vitamin_b1_mg', 'vitamin_d_ug', 'vitamin_a_ug', 'provitamin_a_ug', 'calcium_mg', 'iron_mg',
                    'zinc_mg']
KEYS = DESIGN_KEYS + COMPOSITION_KEYS
# computed from the composition too: caffeine (FDC gives it; used when the card gives no number)
CARRIED = COMPOSITION_KEYS + ['caffeine_mg']
ENTRY_FIELDS = {'id', 'name', 'card', 'serving_g', 'serving_ml', 'density', 'serving_note', 'energy_kcal', 'energy_kj', 'hunger',
                'macros_100g', 'composition', 'composition_note', 'free_sugar', 'free_sugar_note', 'caffeine_mg', 'caffeine_note',
                'alcohol_g', 'alcohol_note', 'curing_nitrite_mg', 'misuse_salt_g', 'misuse_note', 'misuse_check', 'override',
                'override_note', 'notes'}
ETHANOL_DENSITY = 0.789

# Salt of the seasonings with a salt position (foods/10 §11, §12; 11 问题 70): g of sodium chloride per 100 g, from
# the sodium of the nutrition data (× 2.54 / 1,000) or from the ratio.
SALT = {
    'salt': (99.8, '精盐：表值 条目 1565 钠 39,311 mg'),
    'iodized_salt': (99.8, '表值 精盐，条目 1565'),
    'low_sodium_salt': (75.0, '按配比（氯化钠 75、氯化钾 25，foods/10 §2.2）'),
    'sea_salt': (99.8, '同精盐（foods/10 §2.3）'),
    'light_soy_sauce': (6384.7 * 2.54 / 1000, '表值 生抽，条目 1611 钠 6,384.7 mg'),
    'dark_soy_sauce': (6910.4 * 2.54 / 1000, '表值 老抽，条目 1581 钠 6,910.4 mg（foods/10 §12）'),
    'premium_soy_sauce': (5000 * 2.54 / 1000, 'FDC 品牌条目 1910971 钠 5,000 mg（foods/10 §12）'),
    'doubanjiang': (6012.0 * 2.54 / 1000, '表值 豆瓣酱，条目 1523'),
    'sweet_bean_sauce': (2097.2 * 2.54 / 1000, '表值 甜面酱，条目 1527'),
    'soybean_paste': (3606.1 * 2.54 / 1000, '表值 黄酱（大酱），条目 1525'),
    'ketchup': (1046.8 * 2.54 / 1000, '表值 番茄沙司，条目 1544'),
    'mayonnaise': (733.6 * 2.54 / 1000, '表值 沙拉酱，条目 1536'),
    'mustard': (1100 * 2.54 / 1000, 'USDA FDC 172234'),
    'chili_sauce': (8027.6 * 2.54 / 1000, '表值 辣椒酱（辣椒糊），条目 1526'),
    'oyster_sauce': (2730 * 2.54 / 1000, 'USDA FDC 174529'),
    'chicken_bouillon_powder': (18864.4 * 2.54 / 1000, '表值 鸡精，条目 1567'),
    'stock_cube': (24000 * 2.54 / 1000, 'USDA FDC 171561'),
    'cooking_wine': (270 * 2.54 / 1000, '表值 料酒，条目 1570'),
    'curry_roux': (4950 * 2.54 / 1000, '商品标签 USDA FDC 2031609'),
    'master_stock': (539 * 2.54 / 1000, '卤汁卡片算出的钠 539 mg（foods/10 §12）'),
}

errors, warnings = [], []


def hunger(kcal):
    """FoodValues.hunger: round(20 × √(kcal / 2,000)), 0–20 (Java rounds half up)."""
    if not kcal > 0:
        return 0
    return min(20, math.floor(20 * math.sqrt(kcal / 2000) + 0.5))


def coefficient(protein_share, sugar_share):
    """FoodValues.coefficient (foods/04 §5.3)."""
    if sugar_share >= 0.25:
        return 0.1
    if protein_share >= 0.30:
        return 0.8
    if protein_share >= 0.10:
        return 0.6
    return 0.3


def round_sig(value):
    if value is None:
        return None
    if value == 0:
        return 0
    digits = 4 - int(math.floor(math.log10(abs(value)))) - 1
    return round(value, max(0, min(6, digits)))


class Sources:
    def __init__(self, data):
        self.data = data

    def entry(self, ref, where):
        if ref not in self.data:
            errors.append(f'{where}: source {ref} not in sources.json (run fetch_sources.py)')
            return None
        return self.data[ref]

    def per100(self, ref, where, fill=None):
        """Per-100 g values of an entry; keys it lacks taken from {@code fill} when given."""
        entry = self.entry(ref, where)
        if entry is None:
            return {key: None for key in COMPOSITION_KEYS + ['caffeine_mg']}
        values = dict(entry['per100g'])
        if fill:
            other = self.entry(fill, where)
            if other is not None:
                for key, value in other['per100g'].items():
                    if values.get(key) is None:
                        values[key] = value
        return values

    def sugar_fraction(self, ref, where):
        """Share of free sugar in a sweetener entry: sugars (FDC, MEXT) or carbohydrate (中国食物成分表)."""
        entry = self.entry(ref, where)
        if entry is None:
            return 0
        macros = entry['macros']
        value = macros.get('sugars_g')
        if value is None:
            value = macros.get('carb_g')
        if value is None:
            errors.append(f'{where}: {ref} gives neither sugars nor carbohydrate')
            return 0
        return value / 100


def composition(entry, sources, serving_g):
    """Per-serving composition values (None where unknown), and the sources used."""
    spec = entry.get('composition')
    where = entry['id']
    used = []
    if spec is None:
        return {key: None for key in CARRIED}, used, None
    if isinstance(spec, str):
        values = sources.per100(spec, where)
        used.append(spec)
        return {key: (None if values.get(key) is None else values[key] * serving_g / 100) for key in CARRIED}, used, None
    if isinstance(spec, dict) and 'per100g' in spec:
        # a source outside the composition tables (a label, a popular-science page), named in spec['source']
        values = spec['per100g']
        unknown = set(values) - set(COMPOSITION_KEYS)
        if unknown or not spec.get('source'):
            errors.append(f'{where}: per100g composition needs known keys and a source')
        used.append('text:' + spec.get('source', ''))
        return {key: (None if values.get(key) is None else values[key] * serving_g / 100) for key in CARRIED}, used, None
    if not isinstance(spec, dict) or 'mix' not in spec:
        errors.append(f'{where}: composition must be a source ref, a mix, per100g or null')
        return {key: None for key in CARRIED}, used, None
    total = 0.0
    sums = {key: 0.0 for key in CARRIED}
    # an ingredient without a value counts as 0; a key no ingredient gives stays out (I46, user decision 2026-11-19)
    given = {key: False for key in CARRIED}
    for item in spec['mix']:
        label, grams = item[0], float(item[1])
        options = item[2] if len(item) > 2 else {}
        total += grams
        if label == 'water':
            continue
        values = sources.per100(label, where, options.get('fill'))
        used.append(label)
        if options.get('fill'):
            used.append(options['fill'])
        retention = None
        if options.get('retention') or spec.get('retention'):
            code = options.get('retention') or spec.get('retention')
            used.append(code)
            retention_entry = sources.entry(code, where)
            retention = retention_entry['retention'] if retention_entry else {}
        for key in CARRIED:
            value = values.get(key)
            if value is None:
                continue
            given[key] = True
            factor = 1.0
            if retention is not None and retention.get(key) is not None:
                factor = retention[key] / 100
            sums[key] += value * grams / 100 * factor
    product = float(spec.get('yield_g', total))
    if product <= 0:
        errors.append(f'{where}: mix yield must be above 0')
        return {key: None for key in CARRIED}, used, None
    per_serving = {key: (sums[key] / product * serving_g if given[key] else None) for key in CARRIED}
    return per_serving, used, product


def free_sugar(entry, sources, serving_g):
    spec = entry.get('free_sugar')
    where = entry['id']
    if spec is None:
        errors.append(f'{where}: no free_sugar')
        return 0.0, ''
    if 'g' in spec:
        return float(spec['g']), '卡片给出每份 {} g'.format(spec['g'])
    if 'per100g' in spec:
        return float(spec['per100g']) * serving_g / 100, '每 100 g {} g'.format(spec['per100g'])
    if 'parts' in spec:
        sugar = 0.0
        terms = []
        for part in spec['parts']:
            label, grams, how = part[0], float(part[1]), part[2]
            fraction = how if isinstance(how, (int, float)) else sources.sugar_fraction(how, where)
            sugar += grams * fraction
            terms.append(f'{label} {grams:g} g × {fraction * 100:.1f}%')
        total = float(spec['total_g'])
        if total <= 0 or sugar > total:
            errors.append(f'{where}: free sugar parts exceed the total')
            return 0.0, ''
        return sugar / total * serving_g, '（{}）÷ 原料 {:g} g'.format('＋'.join(terms), total)
    if 'from_source' in spec:
        ref = spec['from_source']
        fraction = sources.sugar_fraction(ref, where)
        return fraction * serving_g, f'{ref} 的糖 {fraction * 100:.1f} g/100 g'
    errors.append(f'{where}: free_sugar needs g, per100g, parts or from_source')
    return 0.0, ''


def misuse_check(entry, serving_g):
    spec = entry.get('misuse_check')
    if not spec:
        return
    salt = 0.0
    for name, grams in spec['parts']:
        if name not in SALT:
            errors.append(f"{entry['id']}: misuse_check names {name}, not in the salt table")
            return
        salt += SALT[name][0] / 100 * grams
    expected = salt / spec['total_g'] * spec.get('basis_g', serving_g)
    card = entry.get('misuse_salt_g')
    if card is None or abs(expected - card) > max(0.015, 0.05 * card):
        errors.append(f"{entry['id']}: misused nitrite {card} g on the card, {expected:.2f} g from the salt table")


def build():
    foods = {f['id']: f for f in json.loads(FOODS.read_text(encoding='utf-8'))['foods']}
    sources = Sources(json.loads(SOURCES.read_text(encoding='utf-8')) if SOURCES.exists() else {})
    out, rows = {}, []
    batch_files = sorted(INPUTS.glob('batch*.json'))
    for path in batch_files:
        batch = json.loads(path.read_text(encoding='utf-8'))
        for entry in batch['entries']:
            where = entry.get('id', '?')
            unknown = set(entry) - ENTRY_FIELDS
            if unknown:
                errors.append(f'{path.name} {where}: unknown fields {sorted(unknown)}')
            if where not in foods:
                errors.append(f'{path.name}: {where} is not a registered product')
                continue
            if where in out:
                errors.append(f'{where} appears twice')
                continue
            if 'serving_g' in entry:
                serving_g = float(entry['serving_g'])
            elif 'serving_ml' in entry:
                serving_g = float(entry['serving_ml']) * float(entry.get('density', 1.0))
            else:
                errors.append(f'{where}: no serving')
                continue
            kcal = entry.get('energy_kcal')
            if kcal is None or kcal < 0 or serving_g <= 0:
                errors.append(f'{where}: energy and serving must be given and not negative')
                continue
            h = hunger(kcal)
            if entry.get('hunger') is not None and entry['hunger'] != h:
                errors.append(f"{where}: hunger {h} from {kcal} kcal, the card says {entry['hunger']}")
            macros = entry.get('macros_100g') or {}
            comp, used, product = composition(entry, sources, serving_g)
            protein = macros.get('protein_g')
            protein_note = '卡片'
            if protein is None:
                spec = entry.get('composition')
                src = sources.data.get(spec) if isinstance(spec, str) else None
                if src and src['macros'].get('protein_g') is not None:
                    protein, protein_note = src['macros']['protein_g'], spec
                else:
                    protein, protein_note = 0.0, '卡片未给，按 0'
            protein_g = protein * serving_g / 100
            sugar_g, sugar_note = free_sugar(entry, sources, serving_g)
            protein_share = protein_g * 4 / kcal if kcal > 0 else 0
            sugar_share = sugar_g * 4 / kcal if kcal > 0 else 0
            coef = coefficient(protein_share, sugar_share)
            if sugar_share > 1.02:
                warnings.append(f'{where}: 游离糖 {sugar_g:.1f} g 折 {sugar_g * 4:.0f} kcal，超过每份能量 {kcal:g} kcal')
            # energy check from the card's macros (17 / 37 / 17 kJ per g, alcohol 29)
            if all(macros.get(k) is not None for k in ('protein_g', 'fat_g', 'carb_g')) and kcal > 0:
                alcohol100 = float(entry.get('alcohol_g', 0)) / serving_g * 100
                kj = 17 * macros['protein_g'] + 37 * macros['fat_g'] + 17 * macros['carb_g'] + 29 * alcohol100
                per100 = kcal / serving_g * 100
                if abs(kj / 4.184 - per100) > 0.05 * per100:
                    warnings.append(f'{where}: 三大营养素折 {kj / 4.184:.0f} kcal/100 g，卡片 {per100:.0f} kcal/100 g（差 {abs(kj / 4.184 - per100) / per100 * 100:.0f}%）')
            per_serving = {}
            caffeine = entry.get('caffeine_mg')
            if caffeine is None:
                # the composition entry's caffeine (FDC), else none: the card lists no caffeine
                caffeine = comp.get('caffeine_mg') or 0.0
            per_serving['caffeine_mg'] = float(caffeine)
            per_serving['alcohol_g'] = float(entry.get('alcohol_g', 0))
            per_serving['fast_sugar_g'] = sugar_g
            per_serving['nitrite_mg'] = 0.0
            for key in COMPOSITION_KEYS:
                if comp[key] is not None:
                    per_serving[key] = comp[key]
            for key, value in (entry.get('override') or {}).items():
                if key not in KEYS:
                    errors.append(f'{where}: override of unknown key {key}')
                    continue
                per_serving[key] = float(value)
            for key, value in per_serving.items():
                if value is None or value < 0:
                    errors.append(f'{where}: {key} is negative')
            misuse_check(entry, serving_g)
            missing = [key for key in COMPOSITION_KEYS if key not in per_serving]
            record = {
                'serving_g': round_sig(serving_g),
                'energy_kcal': kcal,
                'hunger': h,
                'saturation_coefficient': coef,
                'protein_g': round_sig(protein_g),
                'per_serving': {key: round_sig(per_serving[key]) for key in KEYS if key in per_serving},
            }
            if entry.get('curing_nitrite_mg'):
                record['curing_nitrite_mg'] = entry['curing_nitrite_mg']
            if entry.get('misuse_salt_g'):
                record['misuse_nitrite_mg'] = round_sig(entry['misuse_salt_g'] * 1000)
            basis_sources = [sources.data[r]['source'] + '：' + sources.data[r]['name'] if r in sources.data else r[5:]
                             for r in dict.fromkeys(used) if r in sources.data or r.startswith('text:')]
            processing = []
            if isinstance(entry.get('composition'), dict) and 'mix' in entry['composition']:
                spec = entry['composition']
                processing.append('按配料折算（{}）'.format('、'.join(f'{m[0]} {m[1]:g} g' for m in spec['mix']))
                                  + (f'，出品 {product:g} g' if 'yield_g' in spec else ''))
            record['basis'] = {
                'food': entry.get('name', where),
                'card': entry.get('card', ''),
                'sources': basis_sources,
                'processing': '；'.join(processing + ([entry['composition_note']] if entry.get('composition_note') else [])),
                'assumptions': [n for n in [sugar_note and '快吸收糖：' + sugar_note, entry.get('free_sugar_note'), entry.get('caffeine_note'),
                                            entry.get('alcohol_note'), entry.get('override_note'), entry.get('misuse_note')] + entry.get('notes', []) if n],
            }
            out[where] = record
            rows.append((path.stem, where, entry.get('name', ''), serving_g, kcal, h, coef, protein_share, sugar_share, per_serving,
                         record.get('curing_nitrite_mg', 0), record.get('misuse_nitrite_mg', 0), missing, protein_note))
    return foods, out, rows


def report(foods, out, rows):
    lines = ['# 成品的营养与代谢数据（I45，生成）', '', f'成品 {len(foods)} 种，有数据 {len(out)} 种。', '']
    lines += ['## 错误', ''] + ([f'- {e}' for e in errors] or ['- 无']) + ['']
    lines += ['## 警告', ''] + ([f'- {w}' for w in warnings] or ['- 无']) + ['']
    lines += ['## 每份数据', '', '| 批次 | ID | 名称 | 一份 g | kcal | 饱食度 | 系数 | 蛋白质能量% | 游离糖能量% | 快吸收糖 g | 咖啡因 mg | 酒精 g | 护色剂 mg | 误用 mg | 维C mg | B1 mg | D µg | 缺项 |',
              '|' + '---|' * 18]
    for (batch, pid, name, serving, kcal, h, coef, ps, ss, per, curing, misuse, missing, pnote) in rows:
        def v(key):
            value = per.get(key)
            return '—' if value is None else f'{round_sig(value):g}'
        lines.append(f'| {batch} | {pid} | {name} | {serving:g} | {kcal:g} | {h} | {coef} | {ps * 100:.0f} | {ss * 100:.0f} | {v("fast_sugar_g")} | '
                     f'{v("caffeine_mg")} | {v("alcohol_g")} | {curing or "—"} | {misuse or "—"} | {v("vitamin_c_mg")} | {v("vitamin_b1_mg")} | '
                     f'{v("vitamin_d_ug")} | {len(missing)} |')
    lines += ['', '## 覆盖', '']
    no_data = [fid for fid in foods if fid not in out]
    lines.append(f'- 没有营养数据的成品（不能食用）：{len(no_data)} 种：' + '、'.join(no_data))
    unused = [key for key in KEYS if not any(key in r['per_serving'] and r['per_serving'][key] > 0 for r in out.values())]
    if any(r.get('curing_nitrite_mg') or r.get('misuse_nitrite_mg') for r in out.values()):
        unused = [key for key in unused if key != 'nitrite_mg']   # nitrite comes as the conditional amounts
    lines.append('- 没有成品提供的成分键：' + ('、'.join(unused) if unused else '无（亚硝酸盐只在放护色剂或误用时才有）'))
    for key in COMPOSITION_KEYS:
        count = sum(1 for r in out.values() if key not in r['per_serving'])
        if count:
            lines.append(f'  - `{key}` 来源未给的成品 {count} 种')
    if NO_SOURCE.exists():
        first = [l for l in NO_SOURCE.read_text(encoding='utf-8').splitlines() if l.startswith('- ')]
        lines.append(f'- I44 无来源报表：见 {NO_SOURCE.relative_to(ROOT)}')
    lines += ['', '## 盐分表（foods/10 §11、§12）', '', '| 调味品 | 盐分 g/100 g | 来源 |', '|---|---|---|']
    lines += [f'| {k} | {v[0]:.2f} | {v[1]} |' for k, v in SALT.items()]
    REPORT.parent.mkdir(parents=True, exist_ok=True)
    REPORT.write_text('\n'.join(lines) + '\n', encoding='utf-8')


def main():
    foods, out, rows = build()
    report(foods, out, rows)
    if not errors:
        data = {'foods': out, 'salt': {k: round(v[0], 3) for k, v in SALT.items()}}
        OUT.write_text(json.dumps(data, ensure_ascii=False, indent=1) + '\n', encoding='utf-8')
    print(f'{len(out)} products, {len(errors)} errors, {len(warnings)} warnings -> {REPORT.relative_to(ROOT)}')
    for e in errors[:40]:
        print('ERROR', e)
    sys.exit(1 if errors else 0)


if __name__ == '__main__':
    main()
