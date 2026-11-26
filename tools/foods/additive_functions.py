#!/usr/bin/env python3
"""
GB 2760 function classes of the additives (foods/04 §1, I39): reads the additive list docs/additives/02 (sections D.1–D.23,
one row per CNS entry), finds each entry's material in src/main/resources/foodindustrial/materials.json by name, and
writes src/main/resources/foodindustrial/additive_functions.json:

    { "classes": { "acidity_regulator": [ "citric_acid", ... ], ... } }

The mod turns each class into the item tag foodindustrial:additive_functions/<class>; the quality items accept the
classes foods/04 §1 maps to them. Entries whose name finds no material are resolved by OVERRIDES (the material the
batch recipes make for them); the script fails when an entry stays unresolved.

    python tools/foods/additive_functions.py
"""
import json
import os
import re
import sys

ROOT = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..'))
LIST = os.path.join(ROOT, 'docs', 'additives', '02-gb2760-additive-list.md')
MATERIALS = os.path.join(ROOT, 'src', 'main', 'resources', 'foodindustrial', 'materials.json')
OUT = os.path.join(ROOT, 'src', 'main', 'resources', 'foodindustrial', 'additive_functions.json')

CLASSES = {
    'D.1': 'acidity_regulator', 'D.2': 'anticaking', 'D.3': 'antifoaming', 'D.4': 'antioxidant', 'D.5': 'bleaching',
    'D.6': 'leavening', 'D.7': 'gum_base', 'D.8': 'colorant', 'D.9': 'color_retention', 'D.10': 'emulsifier',
    'D.11': 'enzyme', 'D.12': 'flavor_enhancer', 'D.13': 'flour_treatment', 'D.14': 'glazing', 'D.15': 'humectant',
    'D.16': 'nutrient', 'D.17': 'preservative', 'D.18': 'stabilizer', 'D.19': 'sweetener', 'D.20': 'thickener',
    'D.21': 'flavoring', 'D.22': 'processing_aid', 'D.23': 'other',
}

# CNS entry → materials, where the name finds none or the wrong one (the material the batch recipes make, I18–I31)
OVERRIDES = {
    '01.107': ['acetic_acid'], '01.112': ['acetic_acid'], '01.111': ['tartaric_acid'],
    '03.007': ['polydimethylsiloxane', 'polydimethylsiloxane_emulsion'],
    '04.005': ['epigallocatechin_gallate'], '04.016': ['alpha_tocopherol', 'dl_alpha_tocopherol'], '04.017': ['carnosic_acid'],
    '08.001': ['amaranth', 'amaranth_lake'], '08.002': ['ponceau_4r', 'ponceau_4r_lake'], '08.003': ['erythrosine', 'erythrosine_lake'],
    '08.004': ['new_red', 'new_red_lake'], '08.005': ['tartrazine', 'tartrazine_lake'], '08.006': ['sunset_yellow', 'sunset_yellow_lake'],
    '08.007': ['brilliant_blue', 'brilliant_blue_lake'], '08.008': ['indigo_carmine', 'indigo_carmine_lake'],
    '08.012': ['allura_red', 'allura_red_lake'], '08.016': ['quinoline_yellow', 'quinoline_yellow_lake'],
    '08.101': ['betanin'], '08.106': ['capsanthin'], '08.120': ['monascorubrin'], '08.145': ['carminic_acid', 'carmine'],
    '10.001': ['sucrose_monostearate'], '10.006': ['glyceryl_monostearate'],
    '10.015': ['polysorbate_60'], '10.016': ['polysorbate_80'], '10.025': ['polysorbate_20'], '10.026': ['polysorbate_40'],
    '12.004': ['inosinic_disodium', 'guanylic_disodium'], '17.034': ['carbon_dioxide'], '18.004': ['propylene_glycol'],
}


def norm(text):
    return re.sub(r'\s+', '', text.replace('★', '')).strip()


def main():
    materials = json.load(open(MATERIALS, encoding='utf-8'))['materials']
    names = {}
    ids = set()
    for material in materials:
        ids.add(material['id'])
        for key in ('name', 'professional', 'common'):
            value = material.get(key)
            if isinstance(value, dict) and value.get('zh'):
                names.setdefault(norm(value['zh']), set()).add(material['id'])
                for part in re.split(r'[、/]', value['zh']):
                    names.setdefault(norm(part), set()).add(material['id'])
    current = None
    rows = []
    for line in open(LIST, encoding='utf-8'):
        heading = re.match(r'### (D\.\d+) ', line)
        if heading:
            current = heading.group(1)
            continue
        if line.startswith('## '):
            current = None if '分类明细' not in line else current
        row = re.match(r'\| (\d\d\.\d+[a-z]?) \| ([^|]+) \|', line)
        if row and current:
            rows.append((current, row.group(1), row.group(2).strip()))
    classes = {name: set() for name in CLASSES.values()}
    unresolved = []
    for code, cns, name in rows:
        found = OVERRIDES.get(cns)
        if found is None:
            whole = names.get(norm(name))
            if whole:
                found = sorted(whole)
            else:
                found = set()
                for part in re.split(r'[（）()、，,]', name):
                    if part.strip():
                        found |= names.get(norm(part), set())
                found = sorted(found)
        for material in found:
            if material not in ids:
                unresolved.append((cns, name, 'unknown material ' + material))
        if not found:
            unresolved.append((cns, name, 'no material'))
        classes[CLASSES[code]].update(found)
    if unresolved:
        for entry in unresolved:
            print('UNRESOLVED', *entry, file=sys.stderr)
        sys.exit(1)
    result = {'source': 'docs/additives/02-gb2760-additive-list.md', 'entries': len(rows),
              'classes': {name: sorted(members) for name, members in classes.items() if members}}
    with open(OUT, 'w', encoding='utf-8', newline='\n') as out:
        json.dump(result, out, ensure_ascii=False, indent=1)
        out.write('\n')
    print('entries', len(rows), 'materials', sum(len(v) for v in result['classes'].values()), '->', os.path.relpath(OUT, ROOT))


if __name__ == '__main__':
    main()
