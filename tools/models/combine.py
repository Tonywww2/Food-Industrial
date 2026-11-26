#!/usr/bin/env python3
"""One node script that builds several shapes in a row (one MCP session).

  python3 tools/models/combine.py <id>... [--synced]   the specs inside the script
  python3 tools/models/combine.py <id>... --files      the specs written to tools/models/specs/<id>.json, which the
                                                       script reads from the device's synced copy (a short script
                                                       to paste however many machines it builds)

--synced and --files read the textures from the device's synced copy, so sync before running the script.
"""
import json
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import blockbench  # noqa: E402

files = '--files' in sys.argv
synced = files or '--synced' in sys.argv
ids = [a for a in sys.argv[1:] if not a.startswith('--')]
template = (HERE / 'bb_template.mjs').read_text(encoding='utf-8')
head, rest = template.split('const SPEC = __SPEC__;\n')
pre, post = rest.split('const log = [];', 1)
specs = []
for mid in ids:
    s = blockbench.script(mid, synced=synced)
    specs.append(s.split('const SPEC = ', 1)[1].split(';\n', 1)[0])
head = '\n'.join(l for l in head.splitlines() if not l.startswith('//'))
if files:
    folder = HERE / 'specs'
    folder.mkdir(exist_ok=True)
    for mid, spec in zip(ids, specs):
        (folder / f'{mid}.json').write_text(json.dumps(json.loads(spec), indent=1) + '\n', encoding='utf-8')
    device = blockbench.device_path(folder)
    load = (f'const SPECS = {json.dumps(ids)}.map((id) => JSON.parse(fs.readFileSync({json.dumps(device)} + "\\\\" + id'
            ' + ".json", "utf8")));\n')
else:
    load = '\nconst SPECS = [' + ','.join(specs) + '];\n'
out = head + '\n' + load + pre + 'for (const SPEC of SPECS) {\nconst log = [];' + post + '}\n'
path = blockbench.ROOT / 'build' / 'models' / ('_'.join(ids)[:80] + '.mjs')
path.parent.mkdir(parents=True, exist_ok=True)
path.write_text(out, encoding='utf-8')
print(path, len(out))
