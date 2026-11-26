#!/usr/bin/env python3
"""Fills vitamin A, provitamin A, calcium, iron and zinc (I46) into the hand-made nutrition data of vanilla and
Farmer's Delight foods (tools/nutrition/<mod>.json; this folder is not one of them, so data generation skips it), from the same USDA FoodData Central entries, servings, recipes
and retention codes the approved I36 data uses (user decision of 2026-11-19).

recipes.json (next to this script) restates, per item, how I36 computed it, in a form a program reads:
- {"fdc": "fdc:<id>"}: the entry × the item's serving; optional "g" (grams of the source food when the serving is a
  processed weight, e.g. dried kelp from 155 g of fresh kelp), "retention" (USDA retention code) and "d_from" (the
  entry vitamin D was borrowed from);
- {"mix": [[source, grams, retention code or null], ...], "divide": n}: a recipe summed and divided into n servings;
  a source is "fdc:<id>" or "item:<mod:item>" (that item's per-serving values per gram);
- {"none": true}: no source (fantasy or inedible-in-reality foods, all 0 by the user's decision in I36).

Before writing anything the script recomputes vitamins C, B1 and D from these specs and stops when any differs from
the approved values, so the new nutrients come out of exactly the approved recipes. A key the sources do not give
stays out of the file (never 0); in a mix an ingredient lacking a key counts as 0, and a key no ingredient gives
stays out (user decision of 2026-11-19).

Usage: python3 tools/nutrition/micronutrients/micronutrients.py   (reads the FDC bulk files cached by fetch_sources.py)
"""
import importlib.util
import json
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
DATA = HERE.parent
ROOT = HERE.parents[2]
FILES = {'minecraft': DATA / 'minecraft.json', 'farmersdelight': DATA / 'farmersdelight.json'}
RECIPES = HERE / 'recipes.json'
CHECKED = ['vitamin_c_mg', 'vitamin_b1_mg', 'vitamin_d_ug']
ADDED = ['vitamin_a_ug', 'provitamin_a_ug', 'calcium_mg', 'iron_mg', 'zinc_mg']
KEYS = CHECKED + ADDED
OLD_NOTES = ['维生素 A、钙、铁、锌按 I36 已批准的条目、份量、配方与保留率补齐，来源没有的项留空（用户决定，2026-11-19，I46）']
NOTE_SOURCE = ('维生素 A（预成型）、维生素 A 原（β-胡萝卜素 ÷ 12、α-胡萝卜素与 β-隐黄质 ÷ 24，折 µg RAE）、钙、铁、锌：'
               '取上列同一批 USDA FoodData Central 条目、份量与配方（I46，2026-11-19）')
NOTE_PROCESSING = '维生素 A、维生素 A 原、钙、铁、锌按同一保留率代码的对应项计（没有该项的按不损失）'
NOTE_ASSUMPTION = '维生素 A、钙、铁、锌按 I36 已批准的条目、份量、配方与保留率补齐；配料缺的项按 0，全部配料都没有的项留空（用户决定，2026-11-19，I46）'


def load_fetcher():
    path = ROOT / 'tools' / 'foods' / 'nutrition' / 'fetch_sources.py'
    spec = importlib.util.spec_from_file_location('fetch_sources', path)
    module = importlib.util.module_from_spec(spec)
    spec.loader.exec_module(module)
    return module


class Calculator:
    def __init__(self, entries, recipes, fetcher):
        self.entries = entries
        self.recipes = recipes
        self.fetcher = fetcher
        self.fdc_cache = {}
        self.retention_cache = {}
        self.results = {}

    def per100(self, ref):
        fdc_id = ref.split(':', 1)[1]
        if fdc_id not in self.fdc_cache:
            self.fdc_cache[fdc_id] = self.fetcher.fdc_entry(fdc_id)['per100g']
        return self.fdc_cache[fdc_id]

    def retention(self, code):
        if not code:
            return {}
        if code not in self.retention_cache:
            self.retention_cache[code] = self.fetcher.retn_entry(code)['retention']
        return self.retention_cache[code]

    @staticmethod
    def factor(retention, key):
        # vitamin D: no loss (foods/03 §5 item 3); a nutrient the code does not list: no loss
        if key == 'vitamin_d_ug' or retention.get(key) is None:
            return 1.0
        return retention[key] / 100

    def compute(self, item):
        if item in self.results:
            return self.results[item]
        spec = self.recipes[item]
        serving = self.entries[item]['serving_g']
        if spec.get('none'):
            result = {key: None for key in KEYS}
        elif 'fdc' in spec:
            values = self.per100(spec['fdc'])
            grams = spec.get('g', serving)
            retention = self.retention(spec.get('retention'))
            result = {}
            for key in KEYS:
                value = values.get(key)
                if key == 'vitamin_d_ug' and spec.get('d_from'):
                    value = self.per100(spec['d_from']).get(key)
                result[key] = None if value is None else value * grams / 100 * self.factor(retention, key)
        else:
            sums = {key: 0.0 for key in KEYS}
            given = {key: False for key in KEYS}
            for source, grams, code in spec['mix']:
                if source.startswith('item:'):
                    other = source[5:]
                    sub = self.compute(other)
                    sub_serving = self.entries[other]['serving_g']
                    values = {key: None if sub[key] is None else sub[key] / sub_serving * 100 for key in KEYS}
                    if self.recipes[other].get('none'):
                        values = {key: 0.0 for key in KEYS}
                else:
                    values = self.per100(source)
                retention = self.retention(code)
                for key in KEYS:
                    value = values.get(key)
                    if value is not None:
                        given[key] = True
                        sums[key] += value * grams / 100 * self.factor(retention, key)
            divide = spec.get('divide', 1)
            result = {key: sums[key] / divide if given[key] else None for key in KEYS}
        self.results[item] = result
        return result


def round_sig(value):
    if value == 0:
        return 0
    import math
    digits = 4 - int(math.floor(math.log10(abs(value)))) - 1
    return round(value, max(0, min(6, digits)))


def main():
    data = {mod: json.loads(path.read_text(encoding='utf-8')) for mod, path in FILES.items()}
    entries = {entry['items'][0]: entry for mod in data for entry in data[mod]['entries']}
    recipes = json.loads(RECIPES.read_text(encoding='utf-8'))
    missing = set(entries) - set(recipes)
    extra = set(recipes) - set(entries)
    if missing or extra:
        sys.exit(f'recipes.json and the data disagree: missing {sorted(missing)}, extra {sorted(extra)}')
    calculator = Calculator(entries, recipes, load_fetcher())
    errors = []
    for item, entry in entries.items():
        result = calculator.compute(item)
        if recipes[item].get('none'):
            continue
        for key in CHECKED:
            approved, value = entry['per_serving'][key], result[key]
            if value is None:
                if key != 'vitamin_d_ug':
                    errors.append(f'{item}: {key} has no source value (approved {approved})')
            elif abs(approved - value) > max(0.0006, 0.004 * abs(approved)):
                errors.append(f'{item}: {key} computes to {value:.5g}, approved {approved}')
    if errors:
        sys.exit('recipes.json does not reproduce the approved I36 values:\n' + '\n'.join(errors))
    written = 0
    for mod, content in data.items():
        for entry in content['entries']:
            item = entry['items'][0]
            if recipes[item].get('none'):
                continue
            result = calculator.results[item]
            per_serving = entry['per_serving']
            for key in ADDED:
                per_serving.pop(key, None)
                if result[key] is not None:
                    per_serving[key] = round_sig(result[key])
                    written += 1
            basis = entry['basis']
            if NOTE_SOURCE not in basis['sources']:
                basis['sources'].append(NOTE_SOURCE)
            if any(recipe[2] for recipe in recipes[item].get('mix', [])) or recipes[item].get('retention'):
                if NOTE_PROCESSING not in basis.get('processing', ''):
                    basis['processing'] = (basis.get('processing', '') + '；' + NOTE_PROCESSING).lstrip('；')
            # the first run's note (a mix lacking a key in one ingredient lacked it) was replaced on 2026-11-19
            basis['assumptions'] = [note for note in basis['assumptions'] if note not in OLD_NOTES]
            if NOTE_ASSUMPTION not in basis['assumptions']:
                basis['assumptions'].append(NOTE_ASSUMPTION)
        FILES[mod].write_text(json.dumps(content, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')
    print(f'{len(entries)} entries checked against the approved vitamins; {written} values of {", ".join(ADDED)} written')


if __name__ == '__main__':
    main()
