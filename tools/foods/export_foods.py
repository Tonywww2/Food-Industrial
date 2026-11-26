#!/usr/bin/env python3
"""Exports the finished product table of docs/additives/12 to src/main/resources/foodindustrial/foods.json (I21).

Every table row with an ID becomes one product: its Chinese name, the English name column and the food
category (the "## NN" heading, GB 2760 appendix E). Rows whose ID cell is not a plain ID (sucrose, salt,
baking powder: registered as chemicals) are skipped.

tools/foods/forms.json adds each product's container, the stack size of loose fresh foods (I49) and, for liquid products,
the bulk fluid with the product's ID (I47). The food intermediates (tools/foods/intermediates.json, category "food", foods/08 §1
rules 3-4) are part of the material table: tools/materials/export_materials.py writes them into materials.json (I47, 2026-11-25,
user); run this script first, it writes the product ids that one checks against.

Usage: python3 tools/foods/export_foods.py   (from the repository root)
"""
import json
import pathlib
import re

ROOT = pathlib.Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'docs' / 'additives' / '12-finished-products.md'
OUT = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'foods.json'
FORMS = ROOT / 'tools' / 'foods' / 'forms.json'
CONTAINERS = {'glass_bottle', 'glass_jar', 'paper_bag', 'plastic_bag', 'paper_cup', 'tin_can', 'plastic_casing', 'aluminum_can', 'pet_bottle', 'tea_bag'}
COLOUR = re.compile(r'^#[0-9A-F]{6}$')

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
    forms(foods)
    OUT.write_text(json.dumps({'foods': foods}, ensure_ascii=False, indent=1) + '\n', encoding='utf-8')
    print(f'{len(foods)} foods -> {OUT.relative_to(ROOT)}')


def forms(foods):
    """Container, poured fluid and bulk fluid of the products in forms.json."""
    by_id = {food['id']: food for food in foods}
    table = json.loads(FORMS.read_text(encoding='utf-8'))['forms']
    for pid, form in table.items():
        if pid not in by_id:
            raise SystemExit(f'forms.json: {pid} is not a product')
        food = by_id[pid]
        if 'container' in form:
            if form['container'] not in CONTAINERS:
                raise SystemExit(f'forms.json: {pid}: unknown container {form["container"]}')
            food['container'] = form['container']
        if 'fluid' in form:
            fluid = form['fluid']
            if not COLOUR.match(fluid['color']) or fluid['texture'] not in ('liquid', 'viscous'):
                raise SystemExit(f'forms.json: {pid}: bad fluid {fluid}')
            food['fluid'] = {'color': fluid['color'], 'texture': fluid['texture']}
        if 'pours' in form:
            food['pours'] = form['pours']
        if 'stack' in form:
            if form['stack'] not in (16, 64) or 'container' in form:
                raise SystemExit(f'forms.json: {pid}: stack 16 is for loose foods without a container')
            food['stack'] = form['stack']
    print(f'{len(table)} product forms ({sum(1 for f in table.values() if "fluid" in f)} bulk fluids)')



if __name__ == '__main__':
    main()
