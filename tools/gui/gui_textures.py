#!/usr/bin/env python3
"""Generates the placeholder GUI textures of the machine screens (I10, machines/04).

Thermal-like flat colour blocks drawn by code (2026-11-05); replace the PNGs by hand later. Existing
files are overwritten only with --force.

  textures/gui/window.png         256 x 256 window frame, cut into four corners by the screen
  textures/gui/panel_<colour>.png 256 x 256 side panel frames (blue info, green upgrades,
                                  orange input/output, red redstone)
  textures/gui/elements.png       slots, tank and energy frames, arrow, flame, lock, icons, buttons
  textures/item/device_wrench.png, textures/item/redprint.png

The element coordinates are mirrored in gui/client/GuiTextures.java.

Requires Pillow. Usage: python3 tools/gui/gui_textures.py [--force]
"""
import pathlib
import sys

from PIL import Image, ImageDraw

ROOT = pathlib.Path(__file__).resolve().parents[2]
TEXTURES = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures'
FORCE = '--force' in sys.argv

BLACK = (0, 0, 0, 255)
WHITE = (255, 255, 255, 255)
GRAY = (198, 198, 198, 255)
SHADOW = (85, 85, 85, 255)
SLOT_DARK = (55, 55, 55, 255)
SLOT_FILL = (139, 139, 139, 255)
CLEAR = (0, 0, 0, 0)


def save(image, relative):
    path = TEXTURES / relative
    if path.exists() and not FORCE:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)
    print('wrote', path.relative_to(ROOT))


def shade(color, factor):
    return tuple(max(0, min(255, int(c * factor))) for c in color[:3]) + (255,)


def bevel_frame(size, fill, border=BLACK, light=WHITE, dark=SHADOW, round_corners=True):
    """Vanilla-like window: black outline with cut corners, light top-left and dark bottom-right."""
    image = Image.new('RGBA', (size, size), CLEAR)
    draw = ImageDraw.Draw(image)
    last = size - 1
    draw.rectangle([1, 1, last - 1, last - 1], fill=fill)
    draw.line([2, 0, last - 2, 0], fill=border)
    draw.line([2, last, last - 2, last], fill=border)
    draw.line([0, 2, 0, last - 2], fill=border)
    draw.line([last, 2, last, last - 2], fill=border)
    if round_corners:
        for x, y in [(1, 1), (last - 1, 1), (1, last - 1), (last - 1, last - 1)]:
            draw.point((x, y), fill=border)
    draw.line([2, 1, last - 3, 1], fill=light)
    draw.line([1, 2, 1, last - 3], fill=light)
    draw.line([2, 2, 3, 2], fill=light)
    draw.line([2, 2, 2, 3], fill=light)
    draw.line([3, last - 1, last - 2, last - 1], fill=dark)
    draw.line([last - 1, 3, last - 1, last - 2], fill=dark)
    draw.line([last - 3, last - 2, last - 2, last - 2], fill=dark)
    draw.line([last - 2, last - 3, last - 2, last - 2], fill=dark)
    return image


def slot(draw, x, y, fill):
    draw.rectangle([x, y, x + 17, y + 17], fill=fill)
    draw.line([x, y, x + 16, y], fill=SLOT_DARK)
    draw.line([x, y, x, y + 16], fill=SLOT_DARK)
    draw.line([x + 1, y + 17, x + 17, y + 17], fill=WHITE)
    draw.line([x + 17, y + 1, x + 17, y + 17], fill=WHITE)


def inset(draw, x, y, w, h, fill):
    """Recessed frame like a slot: dark top-left, white bottom-right."""
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=fill)
    draw.line([x, y, x + w - 2, y], fill=SLOT_DARK)
    draw.line([x, y, x, y + h - 2], fill=SLOT_DARK)
    draw.line([x + 1, y + h - 1, x + w - 1, y + h - 1], fill=WHITE)
    draw.line([x + w - 1, y + 1, x + w - 1, y + h - 1], fill=WHITE)


def arrow(draw, x, y, color):
    """24 x 17 progress arrow pointing right."""
    draw.rectangle([x, y + 6, x + 15, y + 10], fill=color)
    for i in range(9):
        draw.line([x + 15 + i, y + i, x + 15 + i, y + 16 - i], fill=color)


FLAME = [
    '......#.......',
    '......##......',
    '..#...##......',
    '..#..###...#..',
    '.##..####..#..',
    '.##.#####.##..',
    '.##########...',
    '.###########..',
    '############..',
    '#############.',
    '#############.',
    '.###########..',
    '..#########...',
    '...#######....',
]


def flame(draw, x, y, color):
    """14 x 14 flame."""
    for dy, row in enumerate(FLAME):
        for dx, c in enumerate(row):
            if c == '#':
                draw.point((x + dx, y + dy), fill=color)


def lock(draw, x, y):
    """7 x 9 padlock."""
    gold = (240, 200, 60, 255)
    dark = (120, 90, 20, 255)
    draw.rectangle([x + 1, y, x + 5, y + 4], outline=dark)
    draw.rectangle([x, y + 4, x + 6, y + 8], fill=gold, outline=dark)
    draw.point((x + 3, y + 6), fill=dark)


def gas(draw, x, y):
    """8 x 8 cloud for gas tanks."""
    white = (235, 240, 245, 255)
    edge = (110, 120, 130, 255)
    draw.ellipse([x, y + 2, x + 4, y + 6], fill=white, outline=edge)
    draw.ellipse([x + 3, y, x + 7, y + 6], fill=white, outline=edge)
    draw.rectangle([x + 2, y + 4, x + 5, y + 6], fill=white)
    draw.line([x + 1, y + 7, x + 6, y + 7], fill=edge)


def vent_icons(draw, x, y):
    """8 x 8 venting switch icons: off, overflow, all."""
    gray = (120, 120, 120, 255)
    draw.line([x + 1, y + 4, x + 6, y + 4], fill=gray)
    for i, color in enumerate([(235, 235, 240, 255), (255, 120, 90, 255)]):
        ox = x + 8 * (i + 1)
        draw.ellipse([ox, y + 3, ox + 4, y + 7], fill=color)
        draw.ellipse([ox + 3, y + 2, ox + 7, y + 7], fill=color)
        draw.line([ox + 3, y, ox + 3, y + 2], fill=color)
        if i == 1:
            draw.line([ox + 1, y, ox + 1, y + 2], fill=color)
            draw.line([ox + 5, y, ox + 5, y + 1], fill=color)


def icon(draw, index, row, painter):
    painter(draw, index * 16, row)


def icon_info(draw, x, y):
    draw.ellipse([x + 1, y + 1, x + 14, y + 14], fill=(60, 120, 200, 255), outline=(20, 50, 100, 255))
    draw.rectangle([x + 7, y + 3, x + 8, y + 4], fill=WHITE)
    draw.rectangle([x + 7, y + 6, x + 8, y + 12], fill=WHITE)


def icon_upgrades(draw, x, y):
    green = (70, 170, 80, 255)
    for i in range(6):
        draw.line([x + 7 - i, y + 2 + i, x + 8 + i, y + 2 + i], fill=green)
    draw.rectangle([x + 5, y + 8, x + 10, y + 13], fill=green)


def icon_io(draw, x, y):
    blue = (63, 118, 228, 255)
    orange = (224, 123, 39, 255)
    draw.rectangle([x + 4, y + 4, x + 11, y + 11], fill=GRAY, outline=SLOT_DARK)
    draw.line([x, y + 7, x + 3, y + 7], fill=blue)
    draw.line([x, y + 8, x + 3, y + 8], fill=blue)
    draw.line([x + 12, y + 7, x + 15, y + 7], fill=orange)
    draw.line([x + 12, y + 8, x + 15, y + 8], fill=orange)


def icon_redstone(draw, x, y):
    red = (200, 20, 20, 255)
    dark = (110, 0, 0, 255)
    for px, py in [(4, 5), (7, 3), (10, 6), (5, 9), (9, 10), (7, 7), (11, 12), (3, 12)]:
        draw.rectangle([x + px, y + py, x + px + 2, y + py + 2], fill=red, outline=dark)


def icon_structure(draw, x, y):
    stone = (150, 150, 155, 255)
    for bx, by in [(1, 8), (8, 8), (4, 1)]:
        draw.rectangle([x + bx, y + by, x + bx + 6, y + by + 6], fill=stone, outline=SLOT_DARK)


def icon_mode(draw, x, y):
    metal = (170, 170, 180, 255)
    draw.ellipse([x + 2, y + 2, x + 13, y + 13], fill=metal, outline=SLOT_DARK)
    for px, py in [(7, 0), (7, 13), (0, 7), (13, 7)]:
        draw.rectangle([x + px, y + py, x + px + 1, y + py + 2 if py in (0, 13) else y + py + 1], fill=metal)
    draw.ellipse([x + 5, y + 5, x + 10, y + 10], fill=SLOT_DARK)


def icon_highlight(draw, x, y):
    red = (230, 60, 60, 255)
    draw.rectangle([x + 2, y + 2, x + 13, y + 13], outline=red)
    draw.rectangle([x + 3, y + 3, x + 12, y + 12], outline=red)
    draw.rectangle([x + 6, y + 6, x + 9, y + 9], fill=red)


def icon_back(draw, x, y):
    for i in range(5):
        draw.line([x + 3 + i, y + 7 - i, x + 3 + i, y + 8 + i], fill=SLOT_DARK)
    draw.rectangle([x + 7, y + 6, x + 13, y + 9], fill=SLOT_DARK)


def torch(draw, x, y, head):
    draw.rectangle([x + 7, y + 6, x + 8, y + 14], fill=(130, 90, 50, 255))
    draw.rectangle([x + 6, y + 2, x + 9, y + 5], fill=head)


def icon_ignore(draw, x, y):
    draw.ellipse([x + 2, y + 2, x + 13, y + 13], outline=SHADOW)
    draw.line([x + 4, y + 7, x + 11, y + 7], fill=SHADOW)
    draw.line([x + 4, y + 8, x + 11, y + 8], fill=SHADOW)


def icon_low(draw, x, y):
    torch(draw, x, y, (90, 20, 20, 255))


def icon_high(draw, x, y):
    torch(draw, x, y, (255, 40, 40, 255))
    draw.point((x + 5, y + 1), fill=(255, 120, 120, 255))
    draw.point((x + 10, y + 1), fill=(255, 120, 120, 255))


def icon_disabled(draw, x, y):
    red = (200, 30, 30, 255)
    for i in range(10):
        draw.rectangle([x + 3 + i, y + 3 + i, x + 4 + i, y + 4 + i], fill=red)
        draw.rectangle([x + 12 - i, y + 3 + i, x + 13 - i, y + 4 + i], fill=red)


def box(draw, x, y):
    draw.rectangle([x + 8, y + 5, x + 14, y + 11], fill=(160, 120, 70, 255), outline=(90, 60, 30, 255))


def drop(draw, x, y):
    water = (60, 110, 220, 255)
    draw.polygon([(x + 11, y + 3), (x + 14, y + 9), (x + 8, y + 9)], fill=water)
    draw.ellipse([x + 8, y + 7, x + 14, y + 13], fill=water)


def arrow_in(draw, x, y):
    blue = (63, 118, 228, 255)
    draw.rectangle([x + 1, y + 7, x + 5, y + 8], fill=blue)
    draw.polygon([(x + 5, y + 4), (x + 8, y + 7), (x + 8, y + 8), (x + 5, y + 11)], fill=blue)


def arrow_out(draw, x, y):
    orange = (224, 123, 39, 255)
    draw.rectangle([x + 1, y + 7, x + 4, y + 8], fill=orange)
    draw.polygon([(x + 4, y + 4), (x + 7, y + 7), (x + 7, y + 8), (x + 4, y + 11)], fill=orange)


def icon_in_items(draw, x, y):
    box(draw, x, y)
    arrow_in(draw, x, y)


def icon_in_fluids(draw, x, y):
    drop(draw, x, y)
    arrow_in(draw, x, y)


def icon_out_items(draw, x, y):
    box(draw, x - 7, y)
    arrow_out(draw, x + 8, y)


def icon_out_fluids(draw, x, y):
    drop(draw, x - 7, y)
    arrow_out(draw, x + 8, y)


def button(draw, x, y, w, h, fill, light, dark):
    draw.rectangle([x, y, x + w - 1, y + h - 1], fill=fill, outline=BLACK)
    draw.line([x + 1, y + 1, x + w - 2, y + 1], fill=light)
    draw.line([x + 1, y + 1, x + 1, y + h - 2], fill=light)
    draw.line([x + 1, y + h - 2, x + w - 2, y + h - 2], fill=dark)
    draw.line([x + w - 2, y + 1, x + w - 2, y + h - 2], fill=dark)


def elements():
    image = Image.new('RGBA', (256, 256), CLEAR)
    draw = ImageDraw.Draw(image)
    # row 0: slots (normal, catalyst, main output, upgrade), arrow, flame, lock, gas
    slot(draw, 0, 0, SLOT_FILL)
    slot(draw, 18, 0, (150, 132, 172, 255))
    slot(draw, 36, 0, (170, 146, 112, 255))
    slot(draw, 54, 0, (120, 160, 124, 255))
    arrow(draw, 80, 0, (139, 139, 139, 255))
    arrow(draw, 104, 0, WHITE)
    flame(draw, 128, 0, (139, 139, 139, 255))
    flame(draw, 142, 0, (255, 150, 30, 255))
    lock(draw, 160, 0)
    gas(draw, 168, 0)
    vent_icons(draw, 176, 0)
    # row 32: tank frame, tank gauge, energy frame, energy fill
    inset(draw, 0, 32, 18, 64, (43, 43, 43, 255))
    for i in range(1, 8):
        y = 32 + i * 8
        length = 6 if i % 2 == 0 else 3
        draw.line([18, y, 18 + length - 1, y], fill=(255, 255, 255, 110))
    inset(draw, 36, 32, 14, 64, (43, 43, 43, 255))
    for i in range(64):
        t = i / 63
        color = (int(255 - 70 * t), int(40 + 20 * (1 - t)), int(30 * (1 - t)), 255)
        draw.line([50, 32 + i, 61, 32 + i], fill=color)
    # row 100: panel icons
    for index, painter in enumerate([icon_info, icon_upgrades, icon_io, icon_redstone, icon_structure, icon_mode, icon_highlight, icon_back]):
        icon(draw, index, 100, painter)
    # row 116: redstone modes and automation switches
    for index, painter in enumerate([icon_ignore, icon_low, icon_high, icon_disabled, icon_in_items, icon_in_fluids, icon_out_items, icon_out_fluids]):
        icon(draw, index, 116, painter)
    # row 160: buttons 120 x 20 (normal, hover), row 180: selected
    button(draw, 0, 160, 120, 20, (150, 150, 150, 255), (220, 220, 220, 255), (90, 90, 90, 255))
    button(draw, 120, 160, 120, 20, (170, 180, 210, 255), (230, 235, 250, 255), (100, 110, 140, 255))
    button(draw, 0, 180, 120, 20, (70, 150, 80, 255), (40, 95, 50, 255), (140, 210, 150, 255))
    return image


def wrench():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    iron = (200, 200, 205, 255)
    dark = (90, 90, 100, 255)
    copper = (200, 110, 60, 255)
    for i in range(9):
        draw.rectangle([3 + i, 12 - i, 4 + i, 13 - i], fill=iron)
    draw.line([3, 14, 13, 4], fill=dark)
    draw.rectangle([10, 1, 14, 5], fill=iron, outline=dark)
    draw.rectangle([12, 0, 13, 3], fill=CLEAR)
    draw.rectangle([1, 11, 4, 14], fill=copper, outline=(120, 60, 30, 255))
    return image


def redprint():
    image = Image.new('RGBA', (16, 16), CLEAR)
    draw = ImageDraw.Draw(image)
    paper = (200, 40, 40, 255)
    draw.rectangle([2, 1, 13, 14], fill=paper, outline=(110, 10, 10, 255))
    for y in range(3, 13, 3):
        draw.line([4, y, 11, y], fill=(250, 200, 200, 255))
    draw.rectangle([4, 9, 7, 12], outline=(250, 220, 220, 255))
    return image


def main():
    save(bevel_frame(256, GRAY), 'gui/window.png')
    for name, color in {'blue': (59, 110, 170), 'green': (62, 140, 75), 'orange': (205, 120, 40), 'red': (170, 50, 50)}.items():
        save(bevel_frame(256, shade(color, 1.0), light=shade(color, 1.35), dark=shade(color, 0.6)), f'gui/panel_{name}.png')
    save(elements(), 'gui/elements.png')
    save(wrench(), 'item/device_wrench.png')
    save(redprint(), 'item/redprint.png')


if __name__ == '__main__':
    main()
