#!/usr/bin/env python3
"""Generates the placeholder textures of the T0 tools and devices (I12, additives/07 §操作与实现规格).

  textures/block/clay_pot.png, iron_pot.png, crock.png, leach_barrel.png, salt_pan.png,
                 drying_rack.png, koji_tray.png, fumigation_box.png     one texture on every face
  textures/item/mortar.png, sieve.png, cloth_bag.png, flint_knife.png, fluid_bottle.png,
                dried_apple.png, dried_berries.png

Existing files are overwritten only with --force. Requires Pillow.
Usage: python3 tools/manual/manual_textures.py [--force]
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


def rgba(hex_colour, alpha=255):
    value = hex_colour.lstrip('#')
    return tuple(int(value[i:i + 2], 16) for i in (0, 2, 4)) + (alpha,)


def shade(colour, factor):
    return tuple(max(0, min(255, round(c * factor))) for c in colour[:3]) + (colour[3],)


def speckled(base, specks):
    image = Image.new('RGBA', (16, 16), base)
    draw = ImageDraw.Draw(image)
    for (x, y), factor in specks:
        draw.point((x, y), fill=shade(base, factor))
    return image, draw


SPECKS = [((2, 3), 0.85), ((7, 1), 1.12), ((11, 5), 0.88), ((4, 9), 1.1), ((13, 11), 0.86), ((9, 13), 1.1),
          ((1, 14), 0.9), ((14, 2), 1.08), ((6, 6), 0.9), ((10, 9), 1.1)]


def pottery(hex_colour, bands):
    base = rgba(hex_colour)
    image, draw = speckled(base, SPECKS)
    for y in bands:
        draw.line([0, y, 15, y], fill=shade(base, 0.75))
        draw.line([0, y + 1, 15, y + 1], fill=shade(base, 1.15))
    draw.rectangle([0, 0, 15, 15], outline=shade(base, 0.7))
    return image


def planks(hex_colour, vertical=True, bands=()):
    base = rgba(hex_colour)
    image, draw = speckled(base, SPECKS)
    for i in range(0, 16, 4):
        draw.line([i, 0, i, 15] if vertical else [0, i, 15, i], fill=shade(base, 0.72))
    iron = (120, 120, 126, 255)
    for y in bands:
        draw.rectangle([0, y, 15, y + 1], fill=iron)
        for x in (2, 7, 12):
            draw.point((x, y), fill=(80, 80, 86, 255))
    return image


def bricks(hex_colour):
    base = rgba(hex_colour)
    mortar = rgba('#B8AFA0')
    image = Image.new('RGBA', (16, 16), base)
    draw = ImageDraw.Draw(image)
    for y in (3, 7, 11, 15):
        draw.line([0, y, 15, y], fill=mortar)
    for row, y in enumerate((0, 4, 8, 12)):
        for x in ((7, 15) if row % 2 == 0 else (3, 11)):
            draw.line([x, y, x, y + 2], fill=mortar)
    for (x, y), factor in SPECKS:
        if image.getpixel((x, y)) == base:
            draw.point((x, y), fill=shade(base, factor))
    return image


def weave(hex_colour):
    base = rgba(hex_colour)
    image = Image.new('RGBA', (16, 16), base)
    draw = ImageDraw.Draw(image)
    for y in range(16):
        for x in range(16):
            if ((x // 2) + (y // 4)) % 2 == 0 and y % 4 == 0:
                draw.point((x, y), fill=shade(base, 0.7))
            elif ((x // 4) + (y // 2)) % 2 == 1 and x % 4 == 0:
                draw.point((x, y), fill=shade(base, 0.8))
    return image


def iron_pot():
    base = rgba('#3C3E44')
    image, draw = speckled(base, SPECKS)
    draw.line([0, 2, 15, 2], fill=shade(base, 1.6))
    draw.rectangle([0, 0, 15, 15], outline=shade(base, 0.6))
    return image


def fumigation_box():
    image = planks('#8E6A3E', vertical=False)
    draw = ImageDraw.Draw(image)
    draw.rectangle([0, 12, 15, 15], fill=(70, 70, 76, 255))
    draw.rectangle([5, 13, 10, 14], fill=(220, 200, 60, 255))
    return image


def mortar():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    stone = (150, 150, 150, 255)
    draw.polygon([(2, 8), (13, 8), (11, 14), (4, 14)], fill=stone, outline=(95, 95, 95, 255))
    draw.line([3, 9, 12, 9], fill=(70, 70, 70, 255))
    draw.line([9, 2, 12, 8], fill=(150, 110, 60, 255), width=2)
    return image


def sieve():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    draw.ellipse([1, 3, 14, 13], outline=(140, 100, 55, 255), width=2)
    for x in range(4, 13, 2):
        draw.line([x, 5, x, 11], fill=(220, 215, 200, 255))
    for y in range(6, 12, 2):
        draw.line([3, y, 12, y], fill=(220, 215, 200, 255))
    return image


def cloth_bag():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    cloth = (225, 215, 190, 255)
    draw.polygon([(5, 4), (10, 4), (13, 14), (2, 14)], fill=cloth, outline=(150, 140, 115, 255))
    draw.line([5, 4, 10, 4], fill=(120, 90, 60, 255), width=2)
    draw.point((7, 9), fill=(190, 180, 155, 255))
    return image


def flint_knife():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    draw.line([2, 13, 6, 9], fill=(130, 95, 55, 255), width=2)
    draw.polygon([(6, 9), (13, 2), (14, 3), (8, 10)], fill=(70, 70, 80, 255), outline=(40, 40, 46, 255))
    draw.point((12, 3), fill=(150, 150, 160, 255))
    return image


def fluid_bottle():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    glass = (200, 225, 235, 180)
    draw.rectangle([6, 2, 9, 5], outline=glass)
    draw.rectangle([6, 1, 9, 1], fill=(150, 110, 70, 255))
    draw.ellipse([3, 5, 12, 14], fill=(200, 225, 235, 90), outline=glass)
    draw.line([5, 8, 5, 11], fill=(255, 255, 255, 200))
    return image


def dried_fruit(hex_colour, berries):
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    base = rgba(hex_colour)
    if berries:
        for x, y in [(3, 7), (8, 4), (9, 10), (4, 11)]:
            draw.ellipse([x, y, x + 4, y + 4], fill=base, outline=shade(base, 0.6))
    else:
        for x, y in [(2, 4), (7, 8)]:
            draw.chord([x, y, x + 7, y + 6], 180, 360, fill=base, outline=shade(base, 0.65))
            draw.line([x + 2, y + 3, x + 5, y + 3], fill=shade(base, 1.25))
    return image


def main():
    save(pottery('#A0583A', (4, 11)), 'block/clay_pot.png')
    save(iron_pot(), 'block/iron_pot.png')
    save(pottery('#6E4A2E', (3, 12)), 'block/crock.png')
    save(planks('#7A5A36', bands=(3, 12)), 'block/leach_barrel.png')
    save(bricks('#9A4E3A'), 'block/salt_pan.png')
    save(planks('#A88452'), 'block/drying_rack.png')
    save(weave('#B8B060'), 'block/koji_tray.png')
    save(fumigation_box(), 'block/fumigation_box.png')
    save(mortar(), 'item/mortar.png')
    save(sieve(), 'item/sieve.png')
    save(cloth_bag(), 'item/cloth_bag.png')
    save(flint_knife(), 'item/flint_knife.png')
    save(fluid_bottle(), 'item/fluid_bottle.png')
    save(dried_fruit('#C8903C', False), 'item/dried_apple.png')
    save(dried_fruit('#7A2030', True), 'item/dried_berries.png')


if __name__ == '__main__':
    main()
