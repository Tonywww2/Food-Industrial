#!/usr/bin/env python3
"""Generates placeholder textures for the materials in src/main/resources/foodindustrial/materials.json.

Item textures (16x16) are drawn by category. Buckets have no texture of their own: their models are the
loader's dynamic bucket, filled with the fluid's texture and tint (data generation). The shared fluid textures
block/fluid_still and block/fluid_flowing are greyscale and tinted per fluid at runtime. Existing
files are overwritten only with --force, so hand-made textures survive regeneration.

Requires Pillow. Usage: python3 tools/materials/placeholder_textures.py [--force]
"""
import hashlib
import json
import pathlib
import sys

from PIL import Image, ImageDraw

ROOT = pathlib.Path(__file__).resolve().parents[2]
RESOURCES = ROOT / 'src' / 'main' / 'resources'
ITEMS = RESOURCES / 'assets' / 'foodindustrial' / 'textures' / 'item'
BLOCKS = RESOURCES / 'assets' / 'foodindustrial' / 'textures' / 'block'
FORCE = '--force' in sys.argv


def hashed_color(key, lightness=0.75):
    digest = hashlib.md5(key.encode()).digest()
    base = [digest[0], digest[1], digest[2]]
    return tuple(int(lightness * 255 + (1 - lightness) * c) for c in base)


def darker(color, factor=0.6):
    return tuple(int(c * factor) for c in color)


def save(image, path):
    if path.exists() and not FORCE:
        return False
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)
    return True


def powder(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.polygon([(2, 13), (8, 4), (14, 13)], fill=color + (255,), outline=darker(color) + (255,))
    draw.line([(2, 13), (14, 13)], fill=darker(color, 0.5) + (255,))
    return image


def pellets(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    for x, y in ((3, 8), (8, 4), (9, 10)):
        draw.ellipse([x, y, x + 4, y + 4], fill=color + (255,), outline=darker(color) + (255,))
    return image


def dish(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.ellipse([1, 3, 14, 13], fill=(220, 230, 235, 255), outline=(120, 130, 140, 255))
    for x, y in ((4, 6), (8, 8), (10, 5), (6, 9)):
        draw.ellipse([x, y, x + 2, y + 2], fill=darker(color, 0.8) + (255,))
    return image


def vial(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.rectangle([6, 1, 9, 3], fill=(150, 110, 80, 255))
    draw.rectangle([5, 4, 10, 14], fill=(225, 235, 240, 255), outline=(120, 130, 140, 255))
    draw.rectangle([6, 8, 9, 13], fill=color + (255,))
    return image


def lump(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.ellipse([2, 4, 14, 13], fill=color + (255,), outline=darker(color) + (255,))
    return image


def fluid_texture(flowing):
    image = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            wave = ((x + (y if flowing else 0) * 2) % 8) / 8
            value = int(205 + 40 * wave)
            image.putpixel((x, y), (value, value, value, 255))
    return image


SHAPES = {'chemical': powder, 'catalyst': pellets, 'culture': dish, 'enzyme': vial, 'raw': lump}


def main():
    materials = json.loads((RESOURCES / 'foodindustrial' / 'materials.json').read_text(encoding='utf-8'))['materials']
    written = 0
    for m in materials:
        if m['state'] == 'solid':
            color = hashed_color(m['id'])
            written += save(SHAPES.get(m['category'], powder)(color), ITEMS / f"{m['id']}.png")
    written += save(fluid_texture(False), BLOCKS / 'fluid_still.png')
    written += save(fluid_texture(True), BLOCKS / 'fluid_flowing.png')
    print(f'{written} textures written')


if __name__ == '__main__':
    main()
