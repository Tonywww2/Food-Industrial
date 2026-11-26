#!/usr/bin/env python3
"""Exports the flavour groups of docs/foods/12 to src/main/resources/foodindustrial/flavor_notes.json (I48).

Each row of tables B.2 and B.3 is one flavouring material with its flavour groups (foods/11 §2, §3) in the order the
table lists them; the first is the group the crafting table makes from it as the main note (I48, 2026-11-19). The
material id is the row's own, or for an additive written as "NN 号" the id of that additive (the checklists of the
content plans, as tools/flavors/export_flavors.py reads them). Every id must be a material of materials.json.

Usage: python3 tools/foods/export_flavor_notes.py   (from the repository root)
"""
import json
import pathlib
import re
import sys

sys.dont_write_bytecode = True
ROOT = pathlib.Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'tools' / 'flavors'))
from export_flavors import numbers  # noqa: E402

SOURCE = ROOT / 'docs' / 'foods' / '12-flavor-groups.md'
RESOURCES = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial'
OUT = RESOURCES / 'flavor_notes.json'
# foods/11 §2, in table order
TYPES = [('果香', 'fruity'), ('乳香', 'dairy'), ('甜香', 'sweet'), ('坚果与焙烤', 'nutty_roasted'), ('肉味与咸味', 'savory'),
         ('辛香', 'spicy'), ('蔬菜与青香', 'green'), ('酒香', 'alcoholic'), ('花香', 'floral'), ('清凉', 'cooling'), ('烟熏', 'smoky')]
# Rows the additive numbers do not settle: "78 号" stands for propylene glycol and glycerol together; 52 has no checklist
# row; S1358 is written "≈137（近似，设计者待定）" (rosin glycerol ester, foods/12)
OVERRIDES = {'S0001': 'propylene_glycol', 'S0002': 'glycerol', 'S0929': 'sodium_hexametaphosphate', 'S1358': 'rosin_glycerol_ester'}
ROW = re.compile(r'^\| ([NS]\d{3,4}) \| (.*?) \| (.*?) \| (.*?) \| (.*?) \|$')


def main():
    by_number = numbers()
    by_zh = dict(TYPES)
    materials = {m['id']: m for m in json.loads((RESOURCES / 'materials.json').read_text(encoding='utf-8'))['materials']}
    flavors = []
    problems = []
    for line in SOURCE.read_text(encoding='utf-8').splitlines():
        match = ROW.match(line.strip())
        if not match:
            continue
        code, name, cell, groups, _ = match.groups()
        ids = re.findall(r'`([^`]+)`', cell)
        number = re.match(r'^(\d+) 号$', cell)
        if code in OVERRIDES:
            ids = [OVERRIDES[code]]
        elif number:
            ids = by_number.get(number.group(1), [])
        if len(ids) != 1:
            problems.append(f'{code} {name}: material id {cell!r} -> {ids}')
            continue
        material = ids[0]
        if material not in materials:
            problems.append(f'{code} {name}: {material} is not a material')
            continue
        group_ids = [] if groups == '—' else [by_zh[g] for g in groups.split('、')]
        flavors.append({'code': code, 'id': material, 'groups': group_ids})
    if problems:
        raise SystemExit('\n'.join(problems))
    seen = {}
    for flavor in flavors:
        if flavor['id'] in seen:
            # one material listed under two codes (natural and synthetic menthol): groups joined in first-seen order
            first = seen[flavor['id']]
            first['groups'] += [g for g in flavor['groups'] if g not in first['groups']]
            first['code'] += ',' + flavor['code']
        else:
            seen[flavor['id']] = flavor
    unique = list(seen.values())
    OUT.write_text(json.dumps({'types': [t for _, t in TYPES], 'flavors': unique}, ensure_ascii=False, indent=1) + '\n',
                   encoding='utf-8')
    counts = {t: sum(t in f['groups'] for f in unique) for _, t in TYPES}
    print(f'{len(flavors)} rows, {len(unique)} materials, {sum(1 for f in unique if f["groups"])} grouped -> {OUT.relative_to(ROOT)}')
    print(counts)


if __name__ == '__main__':
    main()
