#!/usr/bin/env python3
"""Exports the flavour routes of batch 5 (I31) from docs/additives 18 §2 and 19 §1 (and the intermediates whose
route is one template plus its inputs, 19 §2) to src/main/resources/foodindustrial/flavors.json, read by the
data generation (content.flavors).

A route row is "templates + inputs" (18 §1): the inputs are flavour codes (N###, S####), additive numbers
(existing materials), material ids and tags in backticks. Variants A / B are split on "；". Parenthetical notes
are not inputs, except the catalysts named in them. Rows that only point at an existing material (ST26, "见",
"已有") become aliases; rows put off by the designer (11 问题 36) are skipped; rows whose inputs cannot be read
as tokens are left to hand-written recipes (listed under "manual").

Usage: python3 tools/flavors/export_flavors.py   (from the repository root; needs materials.json exported)
"""
import json
import math
import pathlib
import re
import sys
from fractions import Fraction

sys.dont_write_bytecode = True
ROOT = pathlib.Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'tools' / 'materials'))
from export_materials import DOCS, tables  # noqa: E402

RESOURCES = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial'
OUT = RESOURCES / 'flavors.json'
TEMPLATE = re.compile(r'\b((?:NT|ST)\d+)\b')
TOKEN = re.compile(r'`([^`]+)`|\b([NS]\d{3,4})\b|(?<![\w.-])(\d{1,3})(?:\s?([A-D]))?(?![\w.%])')
CAT_PREFIXES = ('catalysts/', 'cultures/', 'enzymes/')
# Checklist rows of earlier plans that list no material id (the product had none of its own then).
NUMBER_IDS = {'1': ['acetic_acid'], '131': ['furaneol']}


def numbers():
    """Additive number -> material ids, from the checklists of the content plans."""
    result = dict(NUMBER_IDS)
    for path in sorted((ROOT / 'docs' / 'implementations').glob('I*.md')):
        for header, rows in tables(path, '| 编号 |', '物料 ID'):
            column = next(c for c in header if c.startswith('物料 ID'))
            for row in rows:
                ids = re.findall(r'`([^`]+)`', row[column])
                if ids:
                    result.setdefault(row['编号'], ids)
    return result


def tier_of(cell, label, count):
    """'T2', 'T2 / T3', 'T2（437 为 T3）', 'T2 / T3（B 为 T3）' -> tier of this variant's own step."""
    head = re.sub(r'（.*?）', '', cell)
    found = [int(t) for t in re.findall(r'T(\d)', head)]
    if not found:
        return None
    if len(found) > 1 and label and count > 1:
        index = 'ABCD'.index(label)
        return found[min(index, len(found) - 1)]
    return min(found)


TERM = re.compile(r'^(\d+(?:\.\d+)?|\d+/\d+)?\s*([A-Z(][A-Za-z0-9()·.]*)$')
# Small molecules that come and go between the steps of a reaction without making one step feed the next.
LOOSE = {'H2O', 'CO2', 'H2', 'O2', 'N2', 'HCl', 'NaCl', 'NH3'}


def reaction_steps(reaction, label):
    """The steps of this variant's key reaction ('A：…；B：…'): lists of (left, right) term dicts formula -> count."""
    if not reaction or reaction == '—':
        return []
    parts = split_top(reaction)
    if label:
        mine = [p for p in parts if p.startswith(label + '：')]
        if mine:
            parts = [mine[0][2:]] + [p for p in parts[parts.index(mine[0]) + 1:] if not re.match(r'^[A-D]：', p)]
    steps = []
    for part in parts:
        part = unnote(re.sub(r'^[A-D]：', '', part), '')
        if '→' not in part:
            continue
        part = part.split('：')[-1]  # a note before the equation ('以蓖麻油酸计：')
        sides = [side.strip() for side in part.split('→')]
        for left, right in zip(sides, sides[1:]):
            steps.append((terms(left), terms(right)))
    return steps


def terms(side):
    result = {}
    for term in side.split(' + '):
        match = TERM.match(term.strip())
        if match:
            result[match.group(2)] = result.get(match.group(2), 0) + Fraction(match.group(1) or 1)
    return result


def balance(reaction, label, formulas, product):
    """Integer amounts by the key reaction (18 §1.4, 11 问题 42): the inputs (material id -> count, from their
    formulas) and the products (formula -> count). Steps are chained: a step that makes what a later step takes
    runs as often as needed; the amounts are the net of all steps, scaled to whole numbers."""
    steps = reaction_steps(reaction, label)
    if not steps:
        return {}, {}
    inputs = set(formulas.values())
    runs = [Fraction(1)] * len(steps)
    for i in range(len(steps) - 2, -1, -1):
        need = []
        for formula, made in steps[i][1].items():
            if formula in inputs or formula in LOOSE:
                continue
            taken = sum(steps[j][0].get(formula, 0) * runs[j] for j in range(i + 1, len(steps)))
            if taken:
                need.append(taken / made)
        if need:
            runs[i] = max(need)
    net = {}
    for (left, right), times in zip(steps, runs):
        for formula, count in left.items():
            net[formula] = net.get(formula, 0) + count * times
        for formula, count in right.items():
            net[formula] = net.get(formula, 0) - count * times
    taken, made = {}, {}
    for mid, formula in formulas.items():
        if formula == product:  # an isomerization: the input as the first step takes it
            first = next((left[formula] * times for (left, _), times in zip(steps, runs) if formula in left), 0)
            if first:
                taken[mid] = first
        elif net.get(formula, 0) > 0:
            taken[mid] = net[formula]
    for formula, value in net.items():
        if value < 0 and formula not in inputs:
            made[formula] = -value
    if product in inputs:
        last = next((right[product] * times for (_, right), times in reversed(list(zip(steps, runs))) if product in right), 0)
        if last:
            made[product] = last
    scale = 1
    for value in list(taken.values()) + list(made.values()):
        scale = scale * value.denominator // math.gcd(scale, value.denominator)
    return ({k: int(v * scale) for k, v in taken.items()}, {k: int(v * scale) for k, v in made.items()})


def unnote(text, keep=' '):
    """The text without its notes in full-width parentheses, nested ones too."""
    while True:
        stripped = re.sub(r'（[^（）]*）', keep, text)
        if stripped == text:
            return text
        text = stripped


def notes_of(text):
    """The outermost notes in full-width parentheses (with any nested notes inside)."""
    found, depth, current = [], 0, ''
    for char in text:
        if char == '（':
            depth += 1
            if depth == 1:
                current = ''
                continue
        elif char == '）':
            depth -= 1
            if depth == 0:
                found.append(current)
                continue
        if depth > 0:
            current += char
    return found


def plain(text):
    """Whether a route cell is only tokens joined by '+' (notes in parentheses allowed)."""
    rest = unnote(text)
    rest = TOKEN.sub(' ', rest)
    return not re.sub(r'[\s+、]', '', rest)


def split_top(text):
    """Splits on '；' outside full-width parentheses."""
    parts, depth, current = [], 0, ''
    for char in text:
        depth += char == '（'
        depth -= char == '）'
        if char in '；;' and depth == 0:
            parts.append(current)
            current = ''
        else:
            current += char
    parts.append(current)
    return [p.strip() for p in parts if p.strip()]


def split_variants(text):
    parts = split_top(text)
    result = []
    for part in parts:
        match = re.match(r'^([A-D])\s+(.*)$', part)
        result.append((match.group(1), match.group(2)) if match else (None, part))
    return result


class Resolver:
    def __init__(self, materials, codes, number_map):
        self.materials = materials
        self.codes = codes
        self.numbers = number_map
        self.errors = []

    def material(self, mid, where):
        if mid == 'water':
            return {'ref': 'water', 'kind': 'fluid'}
        m = self.materials.get(mid)
        if m is None:
            self.errors.append(f'{where}: unknown material {mid}')
            return None
        kind = 'item' if m['state'] == 'solid' else 'fluid'
        return {'ref': m['tag'], 'kind': kind, 'id': mid, 'bearer': m['bearer']}

    def token(self, groups, where):
        tick, code, number, letter = groups
        if tick:
            if tick.startswith(CAT_PREFIXES):
                return {'ref': tick, 'kind': 'catalyst'}
            if tick.startswith(('materials/', 'foods/')):
                return {'ref': tick, 'kind': 'item'}
            return self.material(tick, where)
        if code:
            mid = self.codes.get(code)
            if mid is None:
                self.errors.append(f'{where}: unknown code {code}')
                return None
            return self.material(mid, where)
        ids = self.numbers.get(number)
        if not ids:
            self.errors.append(f'{where}: unknown number {number}')
            return None
        return self.material(ids[0], where)


def inputs_of(text, resolver, where):
    """Inputs and catalysts of one variant; notes in parentheses are dropped except their catalysts."""
    notes = notes_of(text)
    body = unnote(text)
    found = []
    for match in TOKEN.finditer(body):
        item = resolver.token(match.groups(), where)
        if item is not None and item not in found:
            found.append(item)
    for note in notes:
        for tick in re.findall(r'`([^`]+)`', note):
            if tick.startswith(CAT_PREFIXES):
                item = {'ref': tick, 'kind': 'catalyst'}
                if item not in found:
                    found.append(item)
    return found


def implementations():
    """19 §6: how a row of several templates is written (11 问题 40): device, and mode for M23."""
    result = {}
    for _, found in tables(DOCS / '19-batch-5-synthetic-flavors.md', '| 编码 / 中间体 |', '实装'):
        for row in found:
            key = row['编码 / 中间体'].strip('`')
            text = row['实装']
            device = re.search(r'M\d\d', text).group(0)
            mode = 'hydrogenation' if '加氢模式' in text else None
            result[key] = {'device': device, 'mode': mode}
    return result


def raw_tiers():
    """18 §1.4 原料用量: natural raw material -> its tier (16 / 8 / 4 / 2 / 1) by the real oil yield."""
    result = {}
    for line in (DOCS / '18-batch-5-flavors.md').read_text(encoding='utf-8').splitlines():
        match = re.match(r'^\| (16|8|4|2|1) \| [^|]+\| (.+) \|$', line)
        if match:
            for ref in re.findall(r'`([^`]+)`', match.group(2)):
                result[ref if '/' in ref else 'materials/' + ref] = int(match.group(1))
    return result


def product_of(m):
    category = m['purity']['category'].split(':')[1][:3].rstrip('_').upper() if m['purity'] else None
    return {'kind': 'item' if m['state'] == 'solid' else 'fluid', 'state': m['state'], 'bearer': m['bearer'], 'category': category}


def main():
    data = json.loads((RESOURCES / 'materials.json').read_text(encoding='utf-8'))
    materials = {m['id']: m for m in data['materials']}
    rows = []
    for name in ('18-batch-5-flavors.md', '19-batch-5-synthetic-flavors.md'):
        for _, found in tables(DOCS / name, '| 编码 |', '物料 ID'):
            rows.extend(found)
    codes = {r['编码']: r['物料 ID'].strip('`') for r in rows}
    number_map = numbers()
    for _, found in tables(DOCS / '18-batch-5-flavors.md', '| 编码 |', '已有'):
        for row in found:  # 18 §3: flavours made in earlier batches
            tick = re.search(r'`([^`]+)`', row['已有'])
            number = re.match(r'(\d+)', row['已有'])
            if tick:
                codes.setdefault(row['编码'], tick.group(1))
            elif number and number_map.get(number.group(1)):
                codes.setdefault(row['编码'], number_map[number.group(1)][0])
    resolver = Resolver(materials, codes, number_map)
    implementation = implementations()
    routes, aliases, manual, deferred = [], [], [], []
    for row in rows:
        code, mid, source = row['编码'], row['物料 ID'].strip('`'), row['原料']
        templates_cell = row['模板']
        if '暂缓' in source:
            deferred.append(code)
            continue
        if templates_cell.startswith('ST26') or source.startswith(('见', '已有')) or re.match(r'^见\s', source):
            m = materials.get(mid)
            aliases.append({'code': code, 'id': mid, 'fluid': bool(m) and m['state'] != 'solid'})
            continue
        if mid not in materials:
            resolver.errors.append(f'{code}: product {mid} not in materials.json')
            continue
        template_variants = split_variants(templates_cell)
        source_variants = split_variants(source)
        labelled = len(source_variants) > 1 or source_variants[0][0] is not None
        variants = []
        for index, (label, text) in enumerate(source_variants):
            if labelled and len(template_variants) == len(source_variants):
                template_text = template_variants[index][1]
            else:
                template_text = templates_cell if not labelled else template_variants[min(index, len(template_variants) - 1)][1]
            where = f'{code}{label or ""}'
            templates = TEMPLATE.findall(template_text)
            if not templates or not plain(text):
                manual.append({'code': code, 'id': mid, 'label': label, 'templates': templates, 'text': text})
                continue
            note = ' '.join(notes_of(text))
            variant = {
                'alternatives': '/' in template_text,
                'label': label,
                'templates': templates,
                'tier': tier_of(row['阶段'], label, len(source_variants)),
                'inputs': inputs_of(text, resolver, where),
                'note': note,
                'note_ids': re.findall(r'`([a-z0-9_]+)`', note),
            }
            formulas = {i['id']: materials[i['id']]['formula'] for i in variant['inputs']
                        if i.get('id') and materials.get(i['id'], {}).get('formula')}
            if any(i['ref'] == 'water' for i in variant['inputs']):
                formulas['water'] = 'H2O'
            variant['coefficients'], variant['products'] = balance(row.get('关键反应'), label, formulas,
                                                                   materials[mid].get('formula'))
            if len(templates) > 1 and code in implementation:
                variant.update(implementation[code])
            variants.append(variant)
        if variants:
            routes.append({'code': code, 'id': mid, 'product': product_of(materials[mid]), 'variants': variants})
    intermediates = []
    for _, found in tables(DOCS / '19-batch-5-synthetic-flavors.md', '| 建议 ID |', '路线'):
        for row in found:
            mid, text = row['建议 ID'].strip('`'), row['路线']
            match = re.match(r'^((?:ST|NT)\d+(?:\s*[+、]\s*(?:ST|NT)\d+)*)(?:\s*：\s*|\s+)(.*)$', text)
            if mid not in materials:
                continue  # used only by flavours put off by the designer (11 问题 36): not registered
            if not match or not plain(match.group(2)):
                manual.append({'code': None, 'id': mid, 'label': None, 'templates': TEMPLATE.findall(text), 'text': text})
                continue
            note = ' '.join(notes_of(text))
            variant = {'label': None, 'templates': TEMPLATE.findall(match.group(1)), 'tier': tier_of(row['阶段'], None, 1),
                       'inputs': inputs_of(match.group(2), resolver, mid), 'note': note,
                       'note_ids': re.findall(r'`([a-z0-9_]+)`', note)}
            if len(variant['templates']) > 1 and mid in implementation:
                variant.update(implementation[mid])
            intermediates.append({'code': None, 'id': mid, 'product': product_of(materials[mid]), 'variants': [variant]})
    if resolver.errors:
        print('\n'.join(resolver.errors))
        sys.exit(f'{len(resolver.errors)} unresolved references')
    result = {'routes': routes + intermediates, 'aliases': aliases, 'deferred': deferred, 'raw_tiers': raw_tiers(),
              'manual': [{k: v for k, v in m.items() if v is not None} for m in manual]}
    OUT.write_text(json.dumps(result, ensure_ascii=False, indent=1) + '\n', encoding='utf-8')
    print(f'{len(routes)} flavour routes, {len(intermediates)} intermediates, {len(aliases)} aliases, '
          f'{len(deferred)} deferred, {len(manual)} hand-written -> {OUT.relative_to(ROOT)}')


if __name__ == '__main__':
    main()
