#!/usr/bin/env python3
"""Draws the greyscale templates of the item and fluid textures (I33) as PNG files in tools/textures/templates/.
The first batch (chemicals) is written below; the second batch (cultures, enzymes, flavours, products, raw
materials, catalysts) is in art.py, with a fixed layer (item_<kind>_fixed.png) for glass, lids and labels.

Each template is 16x16 pixel art written below as text: '.' is transparent, '1'-'9' are grey levels from the
outline (1) through the base tone (6) to the highlight (9). generate.py tints them with each item's colour. The
PNG files are the real source: redraw them by hand to change a type; this script only writes the files that do not
exist yet (--force rewrites them).

Usage: python3 tools/textures/draw_templates.py [--force]
"""
import pathlib
import sys

from PIL import Image

sys.dont_write_bytecode = True
sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import art  # noqa: E402  (second batch: layered templates, I33)

OUT = pathlib.Path(__file__).resolve().parent / 'templates'
LEVELS = {str(i): v for i, v in zip(range(1, 10), (38, 70, 100, 128, 150, 166, 190, 215, 245))}

TEMPLATES = {
    # a heap of powder, light from the upper left
    'powder': """
................
................
................
................
................
......1111......
....11899711....
...1898877761...
..189878776661..
.18987877676661.
.17877767666561.
1787776766656551
1677667666565541
1566656655554431
.11111111111111.
................""",
    # a cluster of three prisms, the left facets lit
    'crystal': """
................
......11........
.....1961.......
....199661......
....199661..11..
....199661.1961.
....199661199661
.11.199661199661
1961199661199661
1961199661199661
1961199661199651
1961199661199551
1961199651199551
1951199551199551
1111111111111111
................""",
    # two irregular rock-like chunks
    'lump': """
................
................
................
....11111.......
...1887761......
..1887766611....
.18877666661....
.18776666551111.
1877666555418761
1766665554418661
1666655544416551
1665555444416541
.155544443316441
.1544433331.1431
..11111111...111
................""",
    # a glassy chunk (rosin, shellac, ester gums): bright edges and an inner glint
    'resin': """
................
................
................
......11111.....
....1199876611..
...19987766661..
..199877666661..
..1987766666651.
.19877666666551.
.18776667666551.
.17766676665541.
.17666666655441.
.16666665554441.
..166655554441..
...1111111111...
................""",
    # a metal bar: bright top, darker front and side
    'metal': """
................
................
................
................
.....11111111...
....1999999871..
...199999998761.
..1999999987661.
..1888888876661.
..1777777766651.
..1666666666541.
..1666666665441.
..1555555554441.
..1444444443331.
...11111111111..
................""",
    # a jelly cube, the top face lit
    'gel': """
................
................
................
................
...1111111111...
..199999988761..
.18999998877661.
.17888887776661.
.17777777666651.
.17777776666551.
.17777766665551.
.16777666655541.
.16666666555441.
.1666665555441..
..111111111111..
................""",
}

# Small sprites stamped several times (flakes, pellets).
CHIP = (".11111.", "1899871", "1666651", ".11111.")
BEAD = (".111.", "19961", "18661", "16551", ".111.")
STAMPED = {
    # waxy flakes and chips
    'flakes': [(CHIP, 8, 2), (CHIP, 1, 5), (CHIP, 7, 8), (CHIP, 0, 11)],
    # round beads or pellets (caustic soda, polymer granules)
    'pellets': [(BEAD, 8, 2), (BEAD, 2, 4), (BEAD, 9, 7), (BEAD, 4, 9), (BEAD, 10, 11)],
}

FLUIDS = {
    # still and flowing frames of the fluid kinds, tinted at run time (machines/03 §4)
    'liquid': lambda x, y, flowing: 205 + 40 * (((x + (y if flowing else 0) * 2) % 8) / 8),
    'viscous': lambda x, y, flowing: 175 + 60 * ((((x // 2) + (y if flowing else 0)) % 6) / 6) * (1 if (x + y) % 5 else 0.6),
    'gas': lambda x, y, flowing: 225 + 28 * ((((x * 3 + y * (5 if flowing else 2)) % 11) / 11)),
}


def render(art):
    rows = art.strip('\n').split('\n')
    if len(rows) != 16 or any(len(row) != 16 for row in rows):
        raise ValueError('a template is 16 rows of 16 characters')
    image = Image.new('LA', (16, 16), (0, 0))
    for y, row in enumerate(rows):
        for x, char in enumerate(row):
            if char in LEVELS:
                image.putpixel((x, y), (LEVELS[char], 255))
    return image


def stamped(sprites):
    image = Image.new('LA', (16, 16), (0, 0))
    for sprite, left, top in sprites:
        for y, row in enumerate(sprite):
            for x, char in enumerate(row):
                if char in LEVELS and 0 <= left + x < 16 and 0 <= top + y < 16:
                    image.putpixel((left + x, top + y), (LEVELS[char], 255))
    return image


def fluid(kind, flowing):
    image = Image.new('LA', (16, 16))
    for y in range(16):
        for x in range(16):
            value = int(min(255, FLUIDS[kind](x, y, flowing)))
            alpha = 200 if kind == 'gas' else 255
            image.putpixel((x, y), (value, alpha))
    return image


def layered(rows):
    """The tinted layer (digits, grey) and the fixed layer (letters, own colours) of an art.py grid."""
    tinted = Image.new('LA', (16, 16), (0, 0))
    fixed = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        for x, char in enumerate(row):
            if char in LEVELS:
                tinted.putpixel((x, y), (LEVELS[char], 255))
            elif char in art.FIXED:
                fixed.putpixel((x, y), art.FIXED[char] + (255,))
            elif char != '.':
                raise ValueError(f'unknown template character {char!r}')
    return tinted, (fixed if fixed.getbbox() else None)


def main():
    force = '--force' in sys.argv
    OUT.mkdir(parents=True, exist_ok=True)
    written = 0
    items = {f'item_{name}': render(art) for name, art in TEMPLATES.items()}
    items.update({f'item_{name}': stamped(sprites) for name, sprites in STAMPED.items()})
    fluids = {f'fluid_{kind}_{"flowing" if flowing else "still"}': fluid(kind, flowing) for kind in FLUIDS for flowing in (False, True)}
    for kind, make in art.TEMPLATES.items():
        tinted, fixed = layered(art.text(make()))
        items[f'item_{kind}'] = tinted
        if fixed is not None:
            items[f'item_{kind}_fixed'] = fixed
    for kind, rows in art.BADGES.items():
        _, fixed = layered(art.text(rows))
        items[f'badge_{kind}'] = fixed
    for name, image in {**items, **fluids}.items():
        path = OUT / f'{name}.png'
        if path.exists() and not force:
            continue
        image.save(path)
        written += 1
    print(f'{written} templates written to {OUT}')


if __name__ == '__main__':
    main()
