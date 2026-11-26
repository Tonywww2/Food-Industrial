#!/usr/bin/env python3
"""I44 report: materials with no source (additives/08 §C 来源规则, I44 报表).

A material has a source when one of these gives it: the I44 generation list (crops, wild herbs, trees, bushes,
insects, animals, organs, loot, the nest), the I07 world blocks (ores, crusts, crops, plants), a recipe output in the
generated data of a node, or its recipe tag listing anything besides the mod's own item (vanilla items, other mods'
tags). Metals and metalloids that come from other mods only (问题 4、14) are listed apart; everything else must be empty.

Usage: python3 tools/world/no_source_report.py [node]   (after data generation; node defaults to 1.20.1-forge)
Writes build/reports/foodindustrial/no_source.md and exits 1 when the main list is not empty.
"""
import json
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[2]
RES = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial'
NODE = sys.argv[1] if len(sys.argv) > 1 else '1.20.1-forge'
GENERATED = ROOT / 'versions' / NODE / 'src' / 'generated' / 'resources' / 'data'
OUT = ROOT / 'build' / 'reports' / 'foodindustrial' / 'no_source.md'
NS = 'foodindustrial'
# 问题 4、14: metals and metalloids from other mods only
METALS = {'nickel', 'tin', 'silver', 'lead', 'crude_selenium', 'chromium', 'cobalt', 'platinum', 'rhodium', 'palladium', 'vanadium'}


def outputs(json_value, found):
    """Every item or fluid id a recipe JSON names as a result (results, outputs, result)."""
    if isinstance(json_value, dict):
        for key, value in json_value.items():
            if key in ('result', 'results', 'outputs', 'output'):
                collect(value, found)
            else:
                outputs(value, found)
    elif isinstance(json_value, list):
        for value in json_value:
            outputs(value, found)


def collect(value, found):
    if isinstance(value, str):
        found.add(value)
    elif isinstance(value, dict):
        for key in ('item', 'id', 'fluid', 'semi_finished'):
            if isinstance(value.get(key), str):
                found.add(value[key])
        for inner in value.values():
            if isinstance(inner, (dict, list)):
                collect(inner, found)
    elif isinstance(value, list):
        for inner in value:
            collect(inner, found)


# a 获取 that describes processing (a by-product, a recipe still to write, a deferred tapping) rather than a world source
PROCESSING = re.compile(r'副产|配方|暂不做|发酵|萎凋|蒸煮|烘干|干燥|焙炒|杀青|剥壳|去肉|去青皮|酿造')


def raw_routes():
    """08 §C rows: ID -> 获取."""
    text = (ROOT / 'docs' / 'additives' / '08-raw-materials.md').read_text(encoding='utf-8').split('\n')
    routes = {}
    for line in text:
        cells = [c.strip() for c in line.strip('|').split('|')]
        if line.startswith('| ') and len(cells) >= 8 and re.fullmatch(r'[a-z0-9_]+', cells[6]):
            routes.setdefault(cells[6], cells[3])
    return routes


def main():
    materials = json.loads((RES / 'materials.json').read_text(encoding='utf-8'))['materials']
    sources = json.loads((RES / 'world_sources.json').read_text(encoding='utf-8'))
    imports = json.loads((RES / 'tag_imports.json').read_text(encoding='utf-8'))['tags']
    produced = set()
    for path in GENERATED.rglob('*.json'):
        if '/recipe' in str(path).replace('\\', '/'):
            try:
                outputs(json.loads(path.read_text(encoding='utf-8')), produced)
            except json.JSONDecodeError:
                pass
    world = set()
    for crop in sources['crops']:
        world.update(d[0] for d in crop['drops'])
    for wild in sources['wild']:
        world.update(d[0] for d in wild['drops'])
    for tree in sources['trees']:
        for part in ('flower', 'fruit', 'leaf', 'strip', 'wood'):
            world.update(d[0] for d in tree[part])
        if tree['tap']:
            world.add(tree['tap'][0])
    world.update(d[0] for part in sources['gardenia'].values() for d in part)
    world.update(d['item'] for d in sources['vanilla_drops'])
    world.update(d['result'] for d in sources['smoked'])
    world.update(d['item'] for d in sources['loot'])
    world.update(d['item'] for d in sources['scrape'])
    world.update(part for parts in sources['organs'].values() for part, count in parts)
    world.update(a['drop'][0] for a in sources['animals'])
    world.add('swallowroot')
    world.update(sources['deposits'].values())
    world.update(('spirulina', 'dunaliella'))  # M30 plates (content.Cultures)
    world.update(sources['items'])
    external = set()
    for entry in imports:
        listed = entry.get('forge', []) + entry.get('c', [])
        if any(not value.startswith(NS + ':') for value in listed):
            external.add(entry['tag'])
    for tag, linked in sources['linked'].items():
        external.add('materials/' + tag)
    routes = raw_routes()
    missing, metals, processing, others = [], [], [], []
    for material in materials:
        mid = material['id']
        own = NS + ':' + mid
        sourced = (mid in world or own in produced or own + '_bucket' in produced or material.get('tag') in external)
        if sourced:
            continue
        if mid in METALS or any(mid.startswith(m + '_') for m in METALS):
            metals.append(material)
        elif material.get('source') != '08 §C' or material['category'] != 'raw':
            others.append(material)
        elif PROCESSING.search(routes.get(mid, '')):
            processing.append((material, routes[mid]))
        else:
            missing.append(material)
    lines = ['# 无来源物料报表（I44）', '', f'节点：{NODE}；物料 {len(materials)} 种，配方产物 {len(produced)} 个。', '']
    lines += ['## 无来源（应为空）', '']
    lines += [f'- `{m["id"]}` {m["name"]["zh"]}（{m["category"]}）' for m in missing] or ['- 无']
    lines += ['', '## 金属与类金属（依赖其他模组，问题 4、14）', '']
    lines += [f'- `{m["id"]}` {m["name"]["zh"]}' for m in metals] or ['- 无']
    lines += ['', '## 原料的加工路线还没有配方（随各批计划补，不属 I44 的六类来源）', '']
    lines += [f'- `{m["id"]}` {m["name"]["zh"]}：{route}' for m, route in processing] or ['- 无']
    lines += ['', '## 其他类别没有配方产出（化学品、酶、催化剂，不属 I44）', '']
    lines += [f'- `{m["id"]}` {m["name"]["zh"]}（{m["category"]}）' for m in others] or ['- 无']
    OUT.parent.mkdir(parents=True, exist_ok=True)
    OUT.write_text('\n'.join(lines) + '\n', encoding='utf-8')
    print(f'{len(missing)} without a source, {len(metals)} metals -> {OUT.relative_to(ROOT)}')
    sys.exit(1 if missing else 0)


if __name__ == '__main__':
    main()
