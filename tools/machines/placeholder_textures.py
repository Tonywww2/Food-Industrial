#!/usr/bin/env python3
"""Generates placeholder block textures for machines and multiblock ports (I09).

Machines get block/<id>_front, _front_active, _side and _top; ports get block/<id>. Existing files are
overwritten only with --force, so hand-made textures survive.

Requires Pillow. Usage: python3 tools/machines/placeholder_textures.py [--force]
"""
import pathlib
import sys

from PIL import Image, ImageDraw

ROOT = pathlib.Path(__file__).resolve().parents[2]
BLOCKS = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures' / 'block'
FORCE = '--force' in sys.argv

MACHINES = {'crusher': (128, 128, 136)}
PORTS = {'item_port': (176, 140, 80), 'fluid_port': (80, 120, 176), 'energy_port': (176, 60, 60)}


def save(image, name):
    path = BLOCKS / f'{name}.png'
    if path.exists() and not FORCE:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)
    print('wrote', path.relative_to(ROOT))


def frame(color):
    image = Image.new('RGBA', (16, 16), color + (255,))
    draw = ImageDraw.Draw(image)
    dark = tuple(int(c * 0.6) for c in color) + (255,)
    light = tuple(min(255, int(c * 1.2)) for c in color) + (255,)
    draw.rectangle([0, 0, 15, 15], outline=dark)
    draw.line([1, 1, 14, 1], fill=light)
    draw.line([1, 1, 1, 14], fill=light)
    return image, draw, dark


for machine, color in MACHINES.items():
    side, draw, dark = frame(color)
    draw.rectangle([4, 4, 11, 11], outline=dark)
    save(side, f'{machine}_side')
    top, draw, dark = frame(color)
    for x in range(3, 13, 3):
        draw.line([x, 3, x, 12], fill=dark)
    save(top, f'{machine}_top')
    for active in (False, True):
        front, draw, dark = frame(color)
        draw.rectangle([3, 3, 12, 12], fill=(40, 40, 44, 255), outline=dark)
        glow = (255, 160, 40, 255) if active else (70, 70, 76, 255)
        draw.rectangle([5, 9, 10, 11], fill=glow)
        for x in (5, 8, 11):
            draw.line([x, 4, x - 1, 7], fill=(150, 150, 160, 255))
        save(front, f'{machine}_front_active' if active else f'{machine}_front')

for port, color in PORTS.items():
    image, draw, dark = frame(color)
    draw.rectangle([4, 4, 11, 11], fill=(30, 30, 34, 255), outline=dark)
    draw.rectangle([6, 6, 9, 9], fill=tuple(min(255, int(c * 1.3)) for c in color) + (255,))
    save(image, port)
