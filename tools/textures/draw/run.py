#!/usr/bin/env python3
"""Draws the scripted block textures (machine_faces.DRAWN) into assets/foodindustrial/textures/block.

  python3 tools/textures/draw/run.py [--sheet out.png]   --sheet also writes a contact sheet, 8× enlarged

The reference pipeline (tools/textures/reference/run.py) leaves these files alone.
"""
import pathlib
import sys

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
sys.dont_write_bytecode = True
import machine_faces  # noqa: E402

BLOCK = HERE.parents[2] / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures' / 'block'


def main():
    drawn = machine_faces.files()
    for name, painter in drawn.items():
        painter().save(BLOCK / name)
    print(len(drawn), 'textures drawn')
    if '--sheet' in sys.argv:
        from PIL import Image
        out = pathlib.Path(sys.argv[sys.argv.index('--sheet') + 1])
        names = sorted(drawn)
        k, gap = 8, 6
        sheet = Image.new('RGBA', (6 * (16 * k + gap), ((len(names) + 5) // 6) * (16 * k + gap)), (50, 50, 50, 255))
        for i, name in enumerate(names):
            t = Image.open(BLOCK / name).resize((16 * k, 16 * k), Image.NEAREST)
            sheet.alpha_composite(t, ((i % 6) * (16 * k + gap), (i // 6) * (16 * k + gap)))
        sheet.save(out)
        print(out, names)


if __name__ == '__main__':
    main()
