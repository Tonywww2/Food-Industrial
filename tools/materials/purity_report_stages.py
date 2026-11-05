#!/usr/bin/env python3
"""Appends the stages of additives/03, 13 and 14 to the purity report written by the data run.

The data run writes versions/<node>/build/reports/foodindustrial/purity_report.md (I08, machines/01
§1.1): the highest purity each material reaches with T0 … T0–T4 devices. The designers compare it
with the stages in the additive lists by hand (2026-11-04: only a table, no automatic judgement), so
this script adds those rows unchanged: number, name and the 最早 / 最终 / 阶段 columns.

Usage: python3 tools/materials/purity_report_stages.py [node]   (default 1.20.1-forge, from the repository root)
"""
import pathlib
import re
import sys

ROOT = pathlib.Path(__file__).resolve().parents[2]
DOCS = ROOT / 'docs' / 'additives'
MARK = '\n## 文档中的阶段\n'
SOURCES = ['03-first-batch.md', '13-batch-2.md', '14-batch-3.md']
STAGE_COLUMNS = ['最早', '最终', '阶段']


def cells(line):
    return [cell.strip() for cell in line.strip().strip('|').split('|')]


def rows(path):
    """(number, name, stages) of each table row whose header has a name column and a stage column."""
    header = None
    for line in path.read_text(encoding='utf-8').splitlines():
        if not line.startswith('|'):
            header = None
            continue
        row = cells(line)
        if header is None:
            header = row
            continue
        if set(row[0]) <= set('-: '):
            continue
        name_col = next((i for i, h in enumerate(header) if h in ('名称', '添加剂', '酶', '组分')), None)
        stage_cols = [i for i, h in enumerate(header) if h in STAGE_COLUMNS]
        if name_col is None or not stage_cols or not re.fullmatch(r'\d+', row[0]):
            continue
        stages = ' / '.join(f'{header[i]} {row[i]}' for i in stage_cols if i < len(row))
        yield row[0], row[name_col], stages


def main():
    node = sys.argv[1] if len(sys.argv) > 1 else '1.20.1-forge'
    report = ROOT / 'versions' / node / 'build' / 'reports' / 'foodindustrial' / 'purity_report.md'
    if not report.exists():
        sys.exit(f'{report} not found; run :{node}:runData first')
    text = report.read_text(encoding='utf-8').split(MARK)[0].rstrip('\n') + '\n'
    lines = [MARK.strip('\n'), '', '按 additives/03、13、14 原样列出，供与上表人工对照。', '',
             '| 文档 | # | 名称 | 阶段 |', '| --- | --- | --- | --- |']
    for source in SOURCES:
        for number, name, stages in rows(DOCS / source):
            lines.append(f'| {source[:2]} | {number} | {name} | {stages} |')
    report.write_text(text + '\n' + '\n'.join(lines) + '\n', encoding='utf-8')
    print(f'{report}: {len(lines) - 6} rows added')


if __name__ == '__main__':
    main()
