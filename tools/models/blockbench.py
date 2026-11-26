#!/usr/bin/env python3
"""The node script that builds a shape in the running Blockbench (tools/models/bb_template.mjs with the spec inserted).

  python3 tools/models/blockbench.py <machine id> [--keep]   writes build/models/<id>.mjs

The script is run on the user's computer (desktop-commander node:local); it saves <id>.json (the exported model),
<id>.bbmodel and <id>_preview<n>.png to the device's FoodIndustrial/build/blockbench/.
"""
import base64
import hashlib
import json
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
ROOT = HERE.parents[1]
sys.path.insert(0, str(HERE))
import machines  # noqa: E402

DEVICE_ROOT = r'C:\Users\Tony\Documents\EX\ForgeDev\FoodIndustrial'
DEVICE_OUT = DEVICE_ROOT + r'\build\blockbench'
CAMERAS = [{'position': [-30, 28, -30]}, {'position': [30, 28, -30]}, {'position': [30, 28, 30]}]  # front-left, front-right, back-right


def data_url(path):
    return 'data:image/png;base64,' + base64.b64encode(path.read_bytes()).decode()


def device_path(path):
    return DEVICE_ROOT + '\\' + str(path.relative_to(ROOT)).replace('/', '\\')


def script(mid, close=True, synced=False):
    shape = machines.SHAPES[mid]
    textures = machines.texture_files(mid)
    spec = {
        'id': mid,
        'out': DEVICE_OUT,
        'close': close,
        # the textures travel inline from this copy (the device's may not be synced yet), or, once synced, the
        # script reads the device's files (a long inline string can be garbled in the paste; 'check' stops that);
        # Blockbench gets data URLs either way, so it needs no file permission
        'textures': {name: (device_path(path) if synced else data_url(path)) for name, path in textures.items()},
        'elements': shape(),
        'cameras': CAMERAS,
    }
    # the script is pasted by hand into the device's node: a check value per texture stops it on a bad copy
    spec['check'] = {name: hashlib.sha256(src.encode()).hexdigest()[:10] for name, src in spec['textures'].items()
                     if src.startswith('data:')}
    template = (HERE / 'bb_template.mjs').read_text(encoding='utf-8')
    return template.replace('__SPEC__', json.dumps(spec, separators=(',', ':')))


if __name__ == '__main__':
    mid = sys.argv[1]
    out = ROOT / 'build' / 'models'
    out.mkdir(parents=True, exist_ok=True)
    (out / f'{mid}.mjs').write_text(script(mid, close='--keep' not in sys.argv), encoding='utf-8')
    print(out / f'{mid}.mjs')
