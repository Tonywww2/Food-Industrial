#!/usr/bin/env python3
"""Item textures of the flavourings (I48, foods/11 §4): for each of the 11 flavour types a vial (the liquid dosage
forms), a jar of paste and a sachet of powder, tinted with the type's colour. Same style as food_tool_art.py.

Writes textures/item/<type>_flavoring.png, <type>_flavoring_paste.png and <type>_flavoring_powder.png.
Usage: python3 tools/textures/flavoring_art.py
"""
import pathlib
import sys

sys.dont_write_bytecode = True
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
from food_tool_art import GLASS, ITEMS, PAPER, STEEL, WOOD, Canvas, ellipse, rect, rounded  # noqa: E402

# foods/11 §2, colours as in food.flavor.FlavorType
TYPES = {'fruity': 'F28C28', 'dairy': 'F3E6BE', 'sweet': 'C98B3A', 'nutty_roasted': '8B5A2B', 'savory': 'A0522D',
         'spicy': 'C0392B', 'green': '6AA84F', 'alcoholic': '8E3B5E', 'floral': 'E78AC3', 'cooling': '7FD6C2', 'smoky': '6B6B6B'}


def scale(colour, factor):
    channels = [int(colour[i:i + 2], 16) for i in (0, 2, 4)]
    if factor <= 1:
        channels = [round(c * factor) for c in channels]
    else:
        channels = [round(c + (255 - c) * (factor - 1)) for c in channels]
    return ''.join(f'{max(0, min(255, c)):02x}' for c in channels)


def ramp(colour):
    return scale(colour, 0.35), scale(colour, 0.7), colour, scale(colour, 1.3), scale(colour, 1.6)


def vial(c, tint):
    c.shape(rect(5, 4, 10, 15), GLASS)
    c.shape(rect(6, 8, 9, 14), ramp(tint), outline=False)
    c.shape(rect(6, 1, 9, 4), WOOD)


def paste(c, tint):
    c.shape(rounded(2, 6, 13, 15, 2), GLASS)
    c.shape(rect(3, 9, 12, 14), ramp(tint), outline=False)
    c.shape(rect(2, 3, 13, 6), STEEL)


def powder(c, tint):
    sachet = rounded(3, 2, 12, 15, 1)
    c.shape(sachet, PAPER)
    for x in range(3, 13):
        c.put(x, 3 if x % 2 else 2, '9e8e70')
    c.shape(ellipse(8, 10, 3.6, 3.2), ramp(tint))


def main():
    for type_id, tint in TYPES.items():
        for suffix, draw in (('', vial), ('_paste', paste), ('_powder', powder)):
            canvas = Canvas()
            draw(canvas, tint)
            canvas.image().save(ITEMS / f'{type_id}_flavoring{suffix}.png')
    print(f'{len(TYPES) * 3} flavouring textures -> {ITEMS.relative_to(ITEMS.parents[5])}')


if __name__ == '__main__':
    main()
