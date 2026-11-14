#!/usr/bin/env python3
"""Exports the finished product table of docs/additives/12 to src/main/resources/foodindustrial/foods.json (I21).

Every table row with an ID becomes one product: its Chinese name, the English name column and the food
category (the "## NN" heading, GB 2760 appendix E). Rows whose ID cell is not a plain ID (sucrose, salt,
baking powder: registered as chemicals) are skipped.

Usage: python3 tools/foods/export_foods.py   (from the repository root)
"""
import json
import pathlib
import re

ROOT = pathlib.Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'docs' / 'additives' / '12-finished-products.md'
OUT = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'foods.json'

# Category tags foodindustrial:food_categories/<slug>, in the order of the 16 headings (12, 2026-11-11).
CATEGORIES = ['dairy', 'fats_oils', 'frozen_desserts', 'fruits_vegetables', 'cocoa_confectionery', 'cereals', 'bakery', 'meat',
              'aquatic', 'eggs', 'sweeteners', 'condiments', 'special_dietary', 'beverages', 'alcoholic', 'other']
ID = re.compile(r'^[a-z][a-z0-9_]*$')


def main():
    foods = []
    category = None
    for line in SOURCE.read_text(encoding='utf-8').splitlines():
        heading = re.match(r'^## (\d\d) (.+)$', line)
        if heading:
            category = (int(heading.group(1)), heading.group(2))
            continue
        if line.startswith('## '):
            category = None
            continue
        if category is None or not line.startswith('| ') or line.startswith('| 成品') or line.startswith('| ---'):
            continue
        cells = [cell.strip() for cell in line.strip().strip('|').split('|')]
        if not ID.match(cells[1]):
            continue
        if cells[2] in ('', '—'):
            raise SystemExit(f'{cells[1]}: missing English name')
        foods.append({'id': cells[1], 'name': {'zh': cells[0], 'en': cells[2]},
                      'category': CATEGORIES[category[0] - 1], 'category_name': category[1]})
    ids = [food['id'] for food in foods]
    duplicates = sorted({i for i in ids if ids.count(i) > 1})
    if duplicates:
        raise SystemExit(f'duplicate ids: {duplicates}')
    OUT.write_text(json.dumps({'foods': foods}, ensure_ascii=False, indent=1) + '\n', encoding='utf-8')
    print(f'{len(foods)} foods -> {OUT.relative_to(ROOT)}')


if __name__ == '__main__':
    main()
