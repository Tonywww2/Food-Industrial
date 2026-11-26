#!/usr/bin/env python3
"""Generates the placeholder GUI textures of the machine screens (I10, machines/04).

Thermal-like flat colour blocks drawn by code (2026-11-05); replace the PNGs by hand later. Existing
files are overwritten only with --force.

  textures/gui/window.png         256 x 256 window frame, cut into four corners by the screen
  textures/gui/panel_<colour>.png 256 x 256 side panel frames (blue info, green upgrades,
                                  orange input/output, red redstone)
  textures/gui/elements.png       slots, tank and energy frames, arrow, flame, lock, icons, buttons
  textures/gui/immersive_panel.png  64 x 64 pieces of the panels of the immersive display (I37)

The element coordinates are mirrored in gui/client/Widgets.java, those of the panel texture in
machine/display/client/ImmersiveDisplay.java. The icons of the device wrench and the redprint are drawn
by tools/textures/item_art.py since 2026-11-18.

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


def icon_display(draw, x, y):
    """Immersive display (I37): a small panel floating over a machine block."""
    frame = (30, 44, 56, 255)
    glow = (66, 224, 240, 255)
    draw.rectangle([x + 1, y + 1, x + 14, y + 7], fill=(18, 26, 34, 255), outline=frame)
    draw.rectangle([x + 3, y + 3, x + 5, y + 5], fill=(210, 220, 228, 255))
    draw.line([x + 7, y + 4, x + 9, y + 4], fill=glow)
    draw.rectangle([x + 10, y + 3, x + 12, y + 5], fill=glow)
    draw.line([x + 7, y + 8, x + 8, y + 9], fill=glow)
    draw.rectangle([x + 4, y + 10, x + 11, y + 14], fill=(150, 150, 155, 255), outline=SLOT_DARK)


# 8 x 8 icons of the small buttons in the redstone and input/output panels (2026-11-05)

def small_ignore(draw, x, y):
    draw.ellipse([x, y, x + 7, y + 7], outline=SHADOW)
    draw.rectangle([x + 2, y + 3, x + 5, y + 4], fill=SHADOW)


def small_torch(draw, x, y, head):
    draw.rectangle([x + 3, y + 3, x + 4, y + 7], fill=(130, 90, 50, 255))
    draw.rectangle([x + 2, y, x + 5, y + 2], fill=head)


def small_low(draw, x, y):
    small_torch(draw, x, y, (90, 20, 20, 255))


def small_high(draw, x, y):
    small_torch(draw, x, y, (255, 40, 40, 255))
    draw.point((x, y + 1), fill=(255, 120, 120, 255))
    draw.point((x + 7, y + 1), fill=(255, 120, 120, 255))


def small_disabled(draw, x, y):
    red = (200, 30, 30, 255)
    for i in range(7):
        draw.rectangle([x + i, y + i, x + i + 1, y + i], fill=red)
        draw.rectangle([x + 6 - i, y + i, x + 7 - i, y + i], fill=red)


def small_box(draw, x, y):
    draw.rectangle([x, y + 2, x + 3, y + 5], fill=(160, 120, 70, 255), outline=(90, 60, 30, 255))


def small_drop(draw, x, y):
    water = (60, 110, 220, 255)
    draw.rectangle([x + 1, y + 1, x + 2, y + 2], fill=water)
    draw.rectangle([x, y + 3, x + 3, y + 5], fill=water)
    draw.point((x, y + 3), fill=CLEAR)
    draw.point((x + 3, y + 3), fill=CLEAR)


def small_arrow(draw, x, y, color):
    """4 pixels wide arrow pointing right."""
    draw.rectangle([x, y + 3, x + 1, y + 4], fill=color)
    draw.polygon([(x + 2, y + 1), (x + 3, y + 3), (x + 3, y + 4), (x + 2, y + 6)], fill=color)


def small_in_items(draw, x, y):
    small_arrow(draw, x, y, (63, 118, 228, 255))
    small_box(draw, x + 4, y)


def small_in_fluids(draw, x, y):
    small_arrow(draw, x, y, (63, 118, 228, 255))
    small_drop(draw, x + 4, y)


def small_out_items(draw, x, y):
    small_box(draw, x, y)
    small_arrow(draw, x + 4, y, (224, 123, 39, 255))


def small_out_fluids(draw, x, y):
    small_drop(draw, x, y)
    small_arrow(draw, x + 4, y, (224, 123, 39, 255))


def small_back(draw, x, y):
    draw.polygon([(x, y + 3), (x + 3, y), (x + 3, y + 7), (x, y + 4)], fill=SLOT_DARK)
    draw.rectangle([x + 3, y + 3, x + 7, y + 4], fill=SLOT_DARK)


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
    # (columns 2 and 3 were the input/output and redstone panel icons; those tabs show items since 2026-11-05)
    for index, painter in {0: icon_info, 1: icon_upgrades, 4: icon_structure, 5: icon_mode, 6: icon_highlight,
                           7: icon_display}.items():
        icon(draw, index, 100, painter)
    # row 132: 8 x 8 icons of the small panel buttons
    for index, painter in enumerate([small_ignore, small_low, small_high, small_disabled, small_in_items,
                                     small_in_fluids, small_out_items, small_out_fluids, small_back]):
        painter(draw, index * 8, 132)
    # row 160: buttons 120 x 20 (normal, hover), row 180: selected
    button(draw, 0, 160, 120, 20, (150, 150, 150, 255), (220, 220, 220, 255), (90, 90, 90, 255))
    button(draw, 120, 160, 120, 20, (170, 180, 210, 255), (230, 235, 250, 255), (100, 110, 140, 255))
    button(draw, 0, 180, 120, 20, (70, 150, 80, 255), (40, 95, 50, 255), (140, 210, 150, 255))
    return image


def immersive_panel():
    """
    Texture of the panels of the immersive display (machines/04 §9.2), 64 x 64: the backing frame at 0,0
    (16 x 16, 3-pixel corners, stretched), a slot at 16,0 (18 x 18), the progress arrow empty at 0,20 and full
    at 24,20 (24 x 17), a white square at 40,0 (4 x 4), a steam puff at 48,0 (6 x 6), a bubble at 56,0 (4 x 4).
    """
    image = Image.new('RGBA', (64, 64), CLEAR)
    draw = ImageDraw.Draw(image)
    # backing: dark translucent glass, a thin steel edge, cyan corner marks
    draw.rectangle([0, 0, 15, 15], fill=(16, 22, 30, 196))
    draw.rectangle([0, 0, 15, 15], outline=(86, 104, 122, 235))
    for x, y in [(0, 0), (15, 0), (0, 15), (15, 15)]:
        draw.point((x, y), fill=CLEAR)
    for x, y in [(1, 1), (14, 1), (1, 14), (14, 14)]:
        draw.point((x, y), fill=(66, 224, 240, 235))
    # slot: sunken, dark, lit from the top left
    draw.rectangle([16, 0, 33, 17], fill=(8, 12, 18, 150))
    draw.line([16, 0, 33, 0], fill=(40, 52, 64, 255))
    draw.line([16, 0, 16, 17], fill=(40, 52, 64, 255))
    draw.line([17, 17, 33, 17], fill=(120, 138, 154, 255))
    draw.line([33, 1, 33, 17], fill=(120, 138, 154, 255))
    # arrows: the outline of the window's arrow, empty and lit
    for x, colour, edge in ((0, (58, 70, 84, 230), (30, 38, 48, 255)), (24, (66, 224, 240, 255), (16, 84, 104, 255))):
        y = 20
        draw.rectangle([x + 1, y + 6, x + 13, y + 10], fill=colour, outline=edge)
        for i in range(8):
            draw.line([x + 13 + i, y + 1 + i, x + 13 + i, y + 15 - i], fill=colour)
        for i in range(9):
            draw.point((x + 13 + i, y + i), fill=edge)
            draw.point((x + 13 + i, y + 16 - i), fill=edge)
        draw.line([x + 13, y + 1, x + 13, y + 5], fill=edge)
        draw.line([x + 13, y + 11, x + 13, y + 15], fill=edge)
        draw.line([x + 14, y + 6, x + 14, y + 10], fill=colour)
    # white, steam puff, bubble
    draw.rectangle([40, 0, 43, 3], fill=WHITE)
    for dx, dy, alpha in [(1, 0, 150), (2, 0, 190), (3, 0, 190), (4, 0, 150), (0, 1, 150), (5, 1, 150), (0, 4, 150), (5, 4, 150),
                          (1, 5, 150), (2, 5, 190), (3, 5, 190), (4, 5, 150)]:
        draw.point((48 + dx, dy), fill=(255, 255, 255, alpha))
    draw.rectangle([49, 1, 52, 4], fill=(255, 255, 255, 235))
    draw.rectangle([48, 2, 53, 3], fill=(255, 255, 255, 235))
    draw.rectangle([50, 0, 51, 5], fill=(255, 255, 255, 235))
    draw.ellipse([56, 0, 59, 3], outline=(214, 250, 255, 255))
    draw.point((57, 1), fill=(255, 255, 255, 255))
    return image


def main():
    save(bevel_frame(256, GRAY), 'gui/window.png')
    for name, color in {'blue': (59, 110, 170), 'green': (62, 140, 75), 'orange': (205, 120, 40), 'red': (170, 50, 50)}.items():
        save(bevel_frame(256, shade(color, 1.0), light=shade(color, 1.35), dark=shade(color, 0.6)), f'gui/panel_{name}.png')
    save(elements(), 'gui/elements.png')
    save(immersive_panel(), 'gui/immersive_panel.png')


if __name__ == '__main__':
    main()
