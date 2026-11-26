#!/usr/bin/env python3
"""Block and item textures from the approved reference pack (Food_Industrial_Approved_Textures-v3, user 2026-11-25;
docs/texture-requirements.md, "参考图").

The pack's sheets are pictures, not textures: isometric cubes seen from three sides (machines, casings, ores),
plants standing on a soil slab, items on a grey ground. The scripts here find each cube and unwarp its faces
(cubes.py), take the shading the picture baked in off the side faces (left 0.83, right 0.59 of the top, measured on
the sheets), cut the plants and items out of their ground (plants.py, items.py) and sample everything to 16 × 16
(cubes.N; Minecraft's 16x standard, user 2026-11-25: the sheets draw 16 texels to a face, and a block holds 16
pixels to a side), one colour per texel, fully opaque or fully transparent.

  machines.py  46 machines: front, front_active, side, top (the inactive front puts the fire out or dims the lights
               the sheet shows; machines with windows light them up when active); the machines whose textures
               are drawn by script (tools/textures/draw) are left alone
  blocks.py    casings, ports, furnace tube, condenser, ores and deposits, the two tanks, nine hand devices
  plants.py    the I07 crops and the gardenia bush, stages 0–3
  extras.py    I55 crops (ripe stage from the sheet, younger stages scaled from it), the I55 tree blocks, rubber wood
  items.py     the I55 raw materials (30 items)

Left out on purpose (user 2026-11-25): the I55 bushes and the oyster reef (the sheet draws them as other shapes than
the game's models; to be fixed later). Not extractable from the pack yet: the rubber door (its face does not come out
square), vanilla pods (cocoa layout), sea plants (drawn as crossed planes), entities (drawn as models).

Usage: python3 tools/textures/reference/run.py <unpacked pack directory>
Rewrites the textures it makes; it never adds a texture the mod does not have.
"""
import pathlib
import shutil
import sys
import tempfile

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
sys.dont_write_bytecode = True

import blocks  # noqa: E402
import extras  # noqa: E402
import items  # noqa: E402
import machines  # noqa: E402
import plants  # noqa: E402

# block textures drawn by script (tools/textures/draw; user 2026-11-25: block textures are drawn, not sampled down
# from the sheets) are left alone here
sys.path.insert(0, str(HERE.parent / 'draw'))
import machine_faces  # noqa: E402
DRAWN = set(machine_faces.files())

TEXTURES = HERE.parents[2] / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures'
SKIP = {'rubber_door_top.png', 'rubber_door_bottom.png'}
I55_RIPE = ['corn', 'tomato', 'cabbage', 'pineapple', 'onion', 'scallion', 'cucumber']


def main():
    pack = pathlib.Path(sys.argv[1])
    machines.REF = pack / '02_machines'
    blocks.REF = pack
    plants.REF = pack / '05_plants_and_trees'
    extras.REF = pack / '05_plants_and_trees'
    items.REF = pack
    with tempfile.TemporaryDirectory() as tmp:
        tmp = pathlib.Path(tmp)
        out = {kind: tmp / kind for kind in ('block', 'item')}
        for d in out.values():
            d.mkdir()
        machines.extract(out['block'])
        blocks.extract(out['block'])
        blocks.handmade(out['block'])
        blocks.finish(out['block'])
        plants.crops(out['block'])
        extras.ripe_crops(out['block'])
        extras.young_stages(out['block'], I55_RIPE)
        extras.tree_blocks(out['block'])
        extras.rubber(out['block'])
        extras.rubber_2(out['block'])
        items.extract(out['item'])
        written, unknown = 0, []
        for kind, d in out.items():
            for f in sorted(d.glob('*.png')):
                if f.name in SKIP or (kind == 'block' and f.name in DRAWN):
                    continue
                target = TEXTURES / kind / f.name
                if not target.exists():
                    unknown.append(f'{kind}/{f.name}')
                    continue
                shutil.copyfile(f, target)
                written += 1
    print(f'{written} textures written' + (f'; not in the mod, skipped: {unknown}' if unknown else ''))


if __name__ == '__main__':
    main()
