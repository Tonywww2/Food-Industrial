#!/usr/bin/env python3
"""Puts the models Blockbench built into the mod.

  python3 tools/models/install.py <folder with the exports> <machine id>...

For each machine: checks <id>.json (Blockbench's Java block export) against the shape in machines.py, writes it as
assets/foodindustrial/models/block/<id>_shape.json (shape.parent_model) and keeps <id>.bbmodel, the Blockbench
project, in tools/models/bbmodel/ for editing by hand. The machine's block models take the shape as parent when its
definition is marked shaped() (MachineData).
"""
import json
import pathlib
import re
import shutil
import sys

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import machines  # noqa: E402
import shape  # noqa: E402

MODELS = machines.ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'models' / 'block'


def check(mid, exported):
    """The export has the shape's elements with the same boxes, rotations and face UVs."""
    want = machines.SHAPES[mid]()
    got = exported['elements']
    if len(want) != len(got):
        raise ValueError(f'{mid}: {len(got)} elements exported, {len(want)} in the shape')
    names = exported['textures']
    for w, g in zip(want, got):
        if w['name'] != g['name'] or w['from'] != g['from'] or w['to'] != g['to']:
            raise ValueError(f"{mid}: {w['name']} exported as {g['name']} {g['from']} {g['to']}")
        angle = (w.get('rotation') or [0, 0, 0])[2]
        if angle != g['rotation']['angle'] or (angle and g['rotation']['axis'] != 'z'):
            raise ValueError(f"{mid}: {w['name']} rotation {g['rotation']}")
        if set(w['faces']) != set(g['faces']):
            raise ValueError(f"{mid}: {w['name']} faces {sorted(g['faces'])}")
        for f, face in w['faces'].items():
            gf = g['faces'][f]
            if names[gf['texture'][1:]] != face['texture'] or any(abs(a - b) > 1e-6 for a, b in zip(face['uv'], gf['uv'])):
                raise ValueError(f"{mid}: {w['name']} {f} exported as {gf}")


if __name__ == '__main__':
    src = pathlib.Path(sys.argv[1])
    for mid in sys.argv[2:]:
        exported = json.loads((src / f'{mid}.json').read_text(encoding='utf-8'))
        check(mid, exported)
        own = set(machines.texture_files(mid, write=False)) - {'front', 'side', 'top'}
        model = shape.parent_model(exported, mid, own)
        text = json.dumps(model, indent=2)
        text = re.sub(r'\[\s+([^\[\]{}]*?)\s+\]', lambda m: '[' + ', '.join(x.strip() for x in m.group(1).split(',')) + ']', text)
        (MODELS / f'{mid}_shape.json').write_text(text + '\n', encoding='utf-8')
        if (src / f'{mid}.bbmodel').exists():
            (HERE / 'bbmodel').mkdir(exist_ok=True)
            shutil.copyfile(src / f'{mid}.bbmodel', HERE / 'bbmodel' / f'{mid}.bbmodel')
        print(mid, len(model['elements']), 'elements', sorted(model['textures']))
