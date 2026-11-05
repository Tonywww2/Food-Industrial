#!/usr/bin/env python3
"""Generates the placeholder textures of the tanks and the gas cylinder (I11, machines/03 §5).

  textures/block/wooden_tank_side.png, _inner.png, _bottom.png   planks with two iron bands
  textures/block/metal_tank_side.png                            iron frame, see-through (alpha 0) window
  textures/block/metal_tank_top.png                             iron plate
  textures/item/gas_cylinder.png

Existing files are overwritten only with --force. Requires Pillow.
Usage: python3 tools/logistics/logistics_textures.py [--force]
"""
import pathlib
import sys

from PIL import Image, ImageDraw

ROOT = pathlib.Path(__file__).resolve().parents[2]
TEXTURES = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures'
FORCE = '--force' in sys.argv
CLEAR = (0, 0, 0, 0)


def save(image, relative):
    path = TEXTURES / relative
    if path.exists() and not FORCE:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)
    print('wrote', path.relative_to(ROOT))


def planks(dark, light, bands):
    image = Image.new('RGBA', (16, 16), light)
    draw = ImageDraw.Draw(image)
    for x in range(0, 16, 4):
        draw.line([x, 0, x, 15], fill=dark)
    for x, y in [(1, 3), (6, 10), (9, 5), (13, 12), (2, 13)]:
        draw.point((x, y), fill=dark)
    if bands:
        iron = (150, 150, 155, 255)
        rivet = (90, 90, 95, 255)
        for y in (3, 12):
            draw.rectangle([0, y, 15, y + 1], fill=iron)
            for x in (2, 7, 12):
                draw.point((x, y), fill=rivet)
    return image


def metal_side():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    frame = (170, 172, 178, 255)
    dark = (95, 97, 104, 255)
    draw.rectangle([0, 0, 15, 15], outline=dark)
    draw.rectangle([1, 1, 14, 14], outline=frame)
    for x, y in [(1, 1), (14, 1), (1, 14), (14, 14)]:
        draw.point((x, y), fill=dark)
    draw.line([2, 2, 4, 2], fill=(230, 240, 250, 255))
    draw.line([2, 2, 2, 4], fill=(230, 240, 250, 255))
    return image


def metal_top():
    image = Image.new('RGBA', (16, 16), (160, 162, 168, 255))
    draw = ImageDraw.Draw(image)
    dark = (95, 97, 104, 255)
    draw.rectangle([0, 0, 15, 15], outline=dark)
    draw.rectangle([4, 4, 11, 11], outline=(120, 122, 128, 255))
    for x, y in [(2, 2), (13, 2), (2, 13), (13, 13)]:
        draw.point((x, y), fill=dark)
    return image


def cylinder():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    body = (90, 140, 200, 255)
    edge = (40, 70, 120, 255)
    draw.rounded_rectangle([4, 4, 11, 15], radius=2, fill=body, outline=edge)
    draw.line([6, 6, 6, 13], fill=(160, 200, 240, 255))
    draw.rectangle([6, 1, 9, 3], fill=(200, 110, 60, 255), outline=(120, 60, 30, 255))
    draw.line([9, 2, 12, 2], fill=(120, 60, 30, 255))
    return image


def main():
    save(planks((120, 85, 50, 255), (165, 120, 72, 255), True), 'block/wooden_tank_side.png')
    save(planks((110, 78, 45, 255), (140, 100, 60, 255), False), 'block/wooden_tank_inner.png')
    save(planks((120, 85, 50, 255), (155, 112, 68, 255), False), 'block/wooden_tank_bottom.png')
    save(metal_side(), 'block/metal_tank_side.png')
    save(metal_top(), 'block/metal_tank_top.png')
    save(cylinder(), 'item/gas_cylinder.png')


if __name__ == '__main__':
    main()
