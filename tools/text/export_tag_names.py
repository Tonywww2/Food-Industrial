#!/usr/bin/env python3
"""Exports the tag name table of docs/tag-names.md §2 to src/main/resources/foodindustrial/tag_names.json.

Each table row (类型 物品 / 流体, the tag path under foodindustrial:, Chinese and English name) becomes one entry;
the data run (core.text.TagNames) names the remaining tags from their single entry and writes the language keys
tag.<item|fluid>.<namespace>.<path with dots> (2026-11-25, user: tag names for EMI).

Usage: python3 tools/text/export_tag_names.py   (from the repository root)
"""
import json
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[2]
SOURCE = ROOT / 'docs' / 'tag-names.md'
OUT = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'tag_names.json'
KINDS = {'物品': 'item', '流体': 'fluid'}
ROW = re.compile(r'^\| (物品|流体) \| `([a-z0-9_/]+)` \| ([^|]+?) \| ([^|]+?) \| [^|]*\|$')


def main():
    names = {'item': {}, 'fluid': {}}
    in_table = False
    for line in SOURCE.read_text(encoding='utf-8').splitlines():
        if line.startswith('## 2'):
            in_table = True
            continue
        if not in_table or not line.startswith('| ') or line.startswith('| 类型') or line.startswith('| ---'):
            continue
        match = ROW.match(line)
        if not match:
            sys.exit(f'unreadable row: {line}')
        kind, path, zh, en = KINDS[match.group(1)], match.group(2), match.group(3), match.group(4)
        if path in names[kind]:
            sys.exit(f'duplicate {kind} tag {path}')
        names[kind][path] = {'zh': zh, 'en': en}
    OUT.write_text(json.dumps(names, ensure_ascii=False, indent=1, sort_keys=True) + '\n', encoding='utf-8')
    print(f"{len(names['item'])} item and {len(names['fluid'])} fluid tag names -> {OUT.relative_to(ROOT)}")


if __name__ == '__main__':
    main()
