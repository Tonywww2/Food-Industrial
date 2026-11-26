#!/usr/bin/env python3
"""Item textures of the molds, tools and containers of the finished-food line (I40, machines/05 §3, §4).

Same style as item_art.py (user, 2026-11-17): Create-like, dark outlines, light from the top left, every pixel
opaque or transparent. The icons are built from shaded shapes instead of hand-typed grids: each shape is a mask
with a material ramp (outline, dark, mid, light, highlight); edges facing up or left take the light tones, edges
facing down or right the dark ones, and the outline goes round the whole shape.

Writes textures/item/<id>.png. Requires Pillow.
Usage: python3 tools/textures/food_tool_art.py [--out <dir>] [--sheet <png>]
"""
import pathlib
import sys

from PIL import Image

ROOT = pathlib.Path(__file__).resolve().parents[2]
ITEMS = ROOT / 'src/main/resources/assets/foodindustrial/textures/item'

# ramps: outline, dark, mid, light, highlight
STEEL = ('25262b', '54565b', '969aa1', 'babec5', 'f6f8fa')
DARK_STEEL = ('17181c', '37383d', '54565b', '74777d', '969aa1')
WOOD = ('2e1b0e', '4f311c', '784d2f', '9e6b43', 'c49262')
OAK = ('3b2a14', '6b4f2a', '9c7a44', 'b89458', 'd4b072')
PAPER = ('5a4c3a', '9e8e70', 'cfc4a9', 'efe8d4', 'fbf7ec')
KRAFT = ('4f3418', '8a6034', 'b48a52', 'cfa86e', 'e6c890')
GLASS = ('3e5a6a', '8fb4c4', 'b9d6e0', 'd8ecf2', 'ffffff')
GREEN_GLASS = ('163a26', '2c6a40', '4c9a5c', '84c68a', 'c8f0c8')
PLASTIC = ('3e5a6a', 'a8c8d4', 'd4e8ee', 'eef8fb', 'ffffff')
RED_PLASTIC = ('4a1010', '8f1f1f', 'c43a2e', 'e46a52', 'f8a890')
TIN = ('3a3c40', '7c8088', 'b4b8c0', 'd6dae0', 'f6f8fa')
ALUMINUM = ('2c3440', '6c7c90', 'a8b8c8', 'd0dce8', 'f4f8fc')
SILICONE = ('4a1a2e', '8a2f52', 'c0507a', 'e07ca0', 'f8b8d0')
BRASS = ('5c3d0e', '9b6c1e', 'd49a38', 'f0c665', 'fce6a0')
CLOTH = ('5a5040', 'a89c84', 'd8d0bc', 'eee8d8', 'fbf8f0')
STARCH = ('6a6250', 'c8c0a8', 'e6e0cc', 'f4f0e2', 'ffffff')
DARK_HOLE = '17181c'


class Canvas:
    def __init__(self):
        self.px = [[None] * 16 for _ in range(16)]

    def put(self, x, y, colour):
        if 0 <= x < 16 and 0 <= y < 16:
            self.px[y][x] = colour

    def shape(self, mask, ramp, outline=True, light=1):
        """Paints a shaded shape: mask is a set of (x, y)."""
        o, d, m, l, h = ramp
        for (x, y) in mask:
            up = (x, y - 1) not in mask
            left = (x - 1, y) not in mask
            down = (x, y + 1) not in mask
            right = (x + 1, y) not in mask
            if outline and (up or left or down or right):
                colour = o
            else:
                inner_up = (x, y - 1 - light) not in mask or (x, y - 1) not in mask
                inner_left = (x - 1 - light, y) not in mask or (x - 1, y) not in mask
                inner_down = (x, y + 2) not in mask
                inner_right = (x + 2, y) not in mask
                if outline:
                    inner_up = (x, y - 2) not in mask
                    inner_left = (x - 2, y) not in mask
                if inner_up and inner_left:
                    colour = h
                elif inner_up or inner_left:
                    colour = l
                elif inner_down or inner_right:
                    colour = d
                else:
                    colour = m
            self.put(x, y, colour)

    def fill(self, mask, colour):
        for (x, y) in mask:
            self.put(x, y, colour)

    def image(self):
        image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
        for y in range(16):
            for x in range(16):
                c = self.px[y][x]
                if c is not None:
                    image.putpixel((x, y), (int(c[0:2], 16), int(c[2:4], 16), int(c[4:6], 16), 255))
        return image


def rect(x0, y0, x1, y1):
    return {(x, y) for x in range(x0, x1 + 1) for y in range(y0, y1 + 1)}


def ellipse(cx, cy, rx, ry):
    return {(x, y) for x in range(16) for y in range(16)
            if ((x + 0.5 - cx) / rx) ** 2 + ((y + 0.5 - cy) / ry) ** 2 <= 1.0}


def rounded(x0, y0, x1, y1, r=1):
    mask = rect(x0, y0, x1, y1)
    for (cx, cy) in ((x0, y0), (x1, y0), (x0, y1), (x1, y1)):
        for i in range(r):
            for j in range(r - i):
                dx = cx + (i if cx == x0 else -i)
                dy = cy + (j if cy == y0 else -j)
                mask.discard((dx, dy))
    return mask


ICONS = {}


def icon(name):
    def register(function):
        ICONS[name] = function
        return function
    return register


# ---- molds and tools ----------------------------------------------------------------------------------------

@icon('granulating_screen')
def granulating_screen(c):
    ring = ellipse(8, 8, 7.6, 7.6) - ellipse(8, 8, 5.6, 5.6)
    mesh = ellipse(8, 8, 5.8, 5.8)
    for (x, y) in mesh:
        c.put(x, y, '54565b' if (x + y) % 2 else '969aa1')
    for (x, y) in mesh:
        if x % 3 == 0 or y % 3 == 0:
            c.put(x, y, '37383d')
    c.shape(ring, WOOD)


@icon('tablet_die')
def tablet_die(c):
    c.shape(rect(1, 11, 14, 15), STEEL)
    c.fill(rect(5, 12, 10, 12), DARK_HOLE)
    c.shape(rect(6, 0, 9, 8), STEEL)
    c.shape(rect(4, 7, 11, 9), DARK_STEEL)


@icon('filling_mold')
def filling_mold(c):
    half = {(x, y) for (x, y) in ellipse(8, 11, 7.5, 7.5) if y <= 11}
    c.shape(half, PLASTIC)
    inner = {(x, y) for (x, y) in ellipse(8, 11, 5, 5) if y <= 10}
    c.shape(inner, ('8fb4c4', 'b9d6e0', 'cfe6ee', 'dbe8ee', 'eef8fb'), outline=False)
    for x in range(1, 15, 2):
        c.put(x, 11, '3e5a6a')
    c.shape(rect(0, 12, 3, 14), PLASTIC)
    c.shape(rect(12, 12, 15, 14), PLASTIC)


@icon('stretching_mold')
def stretching_mold(c):
    c.shape(rect(1, 2, 3, 13), WOOD)
    c.shape(rect(12, 2, 14, 13), WOOD)
    for i, y in enumerate(range(4, 12, 2)):
        for x in range(4, 12):
            c.put(x, y, 'efe8d4' if (x + i) % 3 else 'cfc4a9')
    c.shape(rect(0, 0, 15, 2), STEEL)


@icon('extrusion_die')
def extrusion_die(c):
    c.shape(ellipse(8, 7, 7, 6.5), STEEL)
    for (x, y) in ((5, 5), (8, 4), (11, 5), (4, 8), (7, 7), (10, 7), (6, 10), (9, 10), (12, 9)):
        c.put(x, y, DARK_HOLE)
    for x in (5, 8, 11):
        for y in range(13, 16):
            c.put(x, y, 'efe8d4')


@icon('cake_mold')
def cake_mold(c):
    c.shape(ellipse(8, 11.5, 7.5, 3.5), STEEL)
    c.shape(rect(1, 5, 14, 11), STEEL)
    c.shape(ellipse(8, 5.5, 7.5, 3), STEEL)
    c.shape(ellipse(8, 5.5, 6, 2), DARK_STEEL, outline=False)


@icon('loaf_pan')
def loaf_pan(c):
    pan = {(x, y) for x in range(0, 16) for y in range(5, 14) if x >= (y - 5) // 4 and x <= 15 - (y - 5) // 4}
    c.shape(pan, STEEL)
    c.fill(rect(2, 6, 13, 7), '37383d')
    c.fill(rect(1, 5, 14, 5), 'f6f8fa')


@icon('tart_pan')
def tart_pan(c):
    c.shape(ellipse(8, 9, 7.6, 5), STEEL)
    c.shape(ellipse(8, 8.5, 5.6, 3.4), DARK_STEEL, outline=False)
    for (x, y) in ellipse(8, 9, 7.6, 5) - ellipse(8, 9, 6.6, 4.2):
        if x % 2 == 0:
            c.put(x, y, '54565b')


@icon('baking_iron')
def baking_iron(c):
    c.shape(rect(0, 3, 9, 12), DARK_STEEL)
    for x in range(2, 9, 2):
        for y in range(5, 12, 2):
            c.put(x, y, '74777d')
    c.shape(rect(9, 6, 15, 9), WOOD)


@icon('mooncake_mold')
def mooncake_mold(c):
    c.shape(rect(6, 0, 9, 7), WOOD)
    c.shape(ellipse(8, 11.5, 6.5, 4.2), WOOD)
    c.fill(ellipse(8, 11.5, 4, 2.4), '4f311c')
    for (x, y) in ((8, 10), (6, 11), (10, 11), (8, 12)):
        c.put(x, y, 'c49262')


@icon('cheese_mold')
def cheese_mold(c):
    c.shape(rect(2, 3, 13, 14), WOOD)
    c.shape(ellipse(8, 3.5, 6, 1.6), WOOD)
    for x in range(4, 13, 3):
        for y in range(6, 14, 3):
            c.put(x, y, DARK_HOLE)


@icon('popsicle_mold')
def popsicle_mold(c):
    for i, x in enumerate((1, 6, 11)):
        c.shape(rounded(x, 4, x + 3, 13, 1), SILICONE)
        c.shape(rect(x + 1, 0, x + 2, 4), WOOD)


@icon('stuffing_nozzle')
def stuffing_nozzle(c):
    c.shape(rect(0, 5, 6, 10), STEEL)
    cone = {(x, y) for x in range(6, 12) for y in range(16) if abs(y - 7.5) <= 3 - (x - 6) * 0.35}
    c.shape(cone, STEEL)
    c.shape(rect(11, 6, 15, 9), STEEL)
    c.put(15, 7, DARK_HOLE)
    c.put(15, 8, DARK_HOLE)


@icon('ham_mold')
def ham_mold(c):
    c.shape(rounded(1, 4, 14, 14, 2), STEEL)
    c.shape(rect(3, 1, 12, 4), DARK_STEEL)
    c.shape(rect(7, 0, 8, 2), DARK_STEEL)
    c.fill(rect(4, 8, 11, 8), '74777d')


@icon('tofu_mold')
def tofu_mold(c):
    c.shape(rect(1, 4, 14, 13), WOOD)
    c.fill(rect(3, 6, 12, 11), 'eee8d8')
    for x in range(3, 13, 2):
        c.put(x, 11, 'd8d0bc')
    c.shape(rect(3, 2, 12, 4), WOOD)


@icon('chocolate_mold')
def chocolate_mold(c):
    c.shape(rect(0, 2, 15, 13), PLASTIC)
    for x0 in (2, 6, 10):
        for y0 in (4, 9):
            c.fill(rect(x0, y0, x0 + 3, y0 + 2), '6a4430')
            c.put(x0, y0, '8a5a3c')


@icon('hard_candy_mold')
def hard_candy_mold(c):
    c.shape(rect(0, 2, 15, 13), SILICONE)
    for x0 in (2, 7, 12):
        for y0 in (4, 9):
            c.fill(rect(x0, y0, x0 + 1, y0 + 1), '8a2f52')
            c.put(x0, y0, '4a1a2e')


@icon('lollipop_mold')
def lollipop_mold(c):
    c.shape(rect(0, 1, 15, 14), STEEL)
    for cx in (4, 11):
        c.fill(ellipse(cx + 0.5, 5.5, 2.6, 2.6), '37383d')
        c.fill(rect(cx, 8, cx, 13), '54565b')


@icon('starch_mogul')
def starch_mogul(c):
    c.shape(rect(0, 4, 15, 13), WOOD)
    c.fill(rect(2, 6, 13, 11), 'e6e0cc')
    for x0 in (3, 7, 11):
        c.fill(rect(x0, 7, x0 + 1, 8), 'c8c0a8')
        c.fill(rect(x0, 10, x0 + 1, 10), 'c8c0a8')


@icon('panning_drum')
def panning_drum(c):
    copper = ('4a2318', '853f2b', 'c46847', 'e89574', 'f8c0a0')
    c.shape(ellipse(8, 9, 7, 6.5), copper)
    c.shape(ellipse(8.5, 8, 3.6, 3.2), DARK_STEEL, outline=False)
    for (x, y) in ((7, 7), (9, 8), (8, 9), (10, 7)):
        c.put(x, y, 'c0507a' if (x + y) % 2 else 'f0c665')
    c.shape(rect(6, 14, 10, 15), DARK_STEEL)


@icon('blow_mold')
def blow_mold(c):
    c.shape(rect(0, 2, 6, 15), STEEL)
    c.shape(rect(9, 2, 15, 15), STEEL)
    bottle = rect(5, 3, 6, 4) | rect(3, 5, 6, 14)
    c.fill(bottle, '37383d')
    bottle2 = rect(9, 3, 10, 4) | rect(9, 5, 12, 14)
    c.fill(bottle2, '37383d')


@icon('oak_barrel')
def oak_barrel(c):
    body = {(x, y) for x in range(16) for y in range(1, 16)
            if abs(x + 0.5 - 8) <= 5.2 + 1.2 * (1 - ((y + 0.5 - 8.5) / 7.5) ** 2)}
    c.shape(body, OAK)
    for x in range(16):
        for y in (4, 12):
            if (x, y) in body:
                c.put(x, y, '37383d' if (x, y - 1) in body and (x, y + 1) in body else '17181c')
    for x in (5, 8, 11):
        for y in range(2, 15):
            if (x, y) in body and y not in (4, 12):
                c.put(x, y, '6b4f2a')


# ---- T2 line modules (I42) ----------------------------------------------------------------------------------

@icon('extrusion_section')
def extrusion_section(c):
    c.shape(rect(0, 5, 11, 10), STEEL)
    for x in range(1, 11, 2):
        c.put(x, 7, '37383d')
        c.put(x + 1, 8, '37383d')
    c.shape(rect(11, 4, 13, 11), DARK_STEEL)
    c.fill(rect(14, 6, 15, 9), 'f0c665')
    for x in (2, 5, 8):
        c.put(x, 4, 'e46a52')
        c.put(x, 11, 'e46a52')


@icon('uht_section')
def uht_section(c):
    c.shape(rect(1, 2, 14, 13), STEEL)
    for x in range(3, 13, 2):
        c.fill(rect(x, 4, x, 11), 'c46847')
    c.fill(rect(3, 4, 12, 4), 'e89574')
    c.shape(rect(6, 14, 9, 15), DARK_STEEL)


# ---- containers ---------------------------------------------------------------------------------------------

@icon('paper_bag')
def paper_bag(c):
    c.shape(rect(3, 4, 12, 15), KRAFT)
    for x in range(3, 13):
        c.put(x, 4 if x % 2 else 3, '4f3418')
    c.fill(rect(5, 8, 10, 11), 'e6c890')


@icon('glass_jar')
def glass_jar(c):
    c.shape(rounded(2, 5, 13, 15, 2), GLASS)
    c.shape(rect(3, 1, 12, 4), STEEL)
    c.fill(rect(4, 7, 4, 12), 'ffffff')


@icon('plastic_bag')
def plastic_bag(c):
    c.shape(rounded(2, 3, 13, 15, 1), PLASTIC)
    c.fill(rect(2, 3, 13, 4), 'c43a2e')
    c.fill(rect(4, 7, 11, 11), 'e6c890')


@icon('paper_cup')
def paper_cup(c):
    cup = {(x, y) for x in range(16) for y in range(2, 16) if abs(x + 0.5 - 8) <= 6.5 - (y - 2) * 0.22}
    c.shape(cup, PAPER)
    c.fill({(x, y) for (x, y) in cup if 8 <= y <= 10 and c.px[y][x] not in ('5a4c3a',)}, 'c43a2e')
    c.fill(rect(2, 2, 13, 2), 'fbf7ec')


@icon('tin_can')
def tin_can(c):
    c.shape(rect(2, 3, 13, 14), TIN)
    c.shape(ellipse(8, 3.5, 6, 1.8), TIN)
    c.fill(rect(3, 6, 12, 11), 'c43a2e')
    c.fill(rect(3, 8, 12, 9), 'f0c665')
    c.shape(ellipse(8, 14.2, 6, 1.6) - rect(0, 0, 15, 13), TIN)


@icon('plastic_casing')
def plastic_casing(c):
    tube = {(x, y) for x in range(16) for y in range(16) if abs((x - y)) <= 2 and 1 <= x <= 14}
    c.shape(tube, RED_PLASTIC)
    for (x, y) in ((1, 0), (14, 15)):
        c.put(x, y, 'babec5')


@icon('glass_bottle')
def glass_bottle(c):
    body = rect(4, 7, 11, 15) | rect(5, 6, 10, 6) | rect(6, 2, 9, 5)
    c.shape(body, GREEN_GLASS)
    c.shape(rect(6, 0, 9, 2), DARK_STEEL)
    c.fill(rect(5, 9, 5, 13), 'c8f0c8')


@icon('aluminum_can')
def aluminum_can(c):
    c.shape(rect(3, 2, 12, 15), ALUMINUM)
    c.fill(rect(4, 5, 11, 12), '2c5ab8')
    c.fill(rect(4, 7, 11, 8), 'a8d4fa')
    c.fill(rect(4, 2, 11, 2), 'f4f8fc')
    c.put(7, 1, '6c7c90')
    c.put(8, 1, '6c7c90')


@icon('pet_bottle')
def pet_bottle(c):
    body = rect(4, 6, 11, 15) | rect(5, 4, 10, 5) | rect(6, 2, 9, 3)
    c.shape(body, PLASTIC)
    c.shape(rect(6, 0, 9, 2), ('0e2a20', '1e4a38', '3a7a58', '78b088', 'b0e0c0'))
    c.fill(rect(4, 9, 11, 11), '2c6a30')


@icon('tea_bag')
def tea_bag(c):
    c.shape(rect(3, 6, 12, 15), CLOTH)
    c.fill(rect(5, 9, 10, 13), 'a89c84')
    for y in range(0, 6):
        c.put(7 + (y % 2), y, '5a5040')
    c.shape(rect(5, 0, 10, 2), RED_PLASTIC)


def main():
    out = ITEMS
    if '--out' in sys.argv:
        out = pathlib.Path(sys.argv[sys.argv.index('--out') + 1])
        out.mkdir(parents=True, exist_ok=True)
    images = {}
    for name, draw in ICONS.items():
        canvas = Canvas()
        draw(canvas)
        images[name] = canvas.image()
        images[name].save(out / f'{name}.png')
    print(f'{len(images)} item textures written to {out}')
    if '--sheet' in sys.argv:
        sheet_path = pathlib.Path(sys.argv[sys.argv.index('--sheet') + 1])
        columns = 8
        rows = (len(images) + columns - 1) // columns
        scale = 8
        sheet = Image.new('RGBA', (columns * 18 * scale, rows * 18 * scale), (70, 70, 80, 255))
        for index, (name, image) in enumerate(images.items()):
            x = (index % columns) * 18 * scale + scale
            y = (index // columns) * 18 * scale + scale
            sheet.alpha_composite(image.resize((16 * scale, 16 * scale), Image.NEAREST), (x, y))
        sheet.save(sheet_path)


if __name__ == '__main__':
    main()
