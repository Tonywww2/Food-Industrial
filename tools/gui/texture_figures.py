#!/usr/bin/env python3
"""
Figures of docs/texture-requirements.md for the atlas textures of the screens: the texture enlarged
over a checkerboard, every region outlined and numbered as in the document's tables.

    python tools/gui/texture_figures.py

Writes docs/texture-requirements/elements.png and immersive_panel.png. Run it after the regions of an
atlas change (tools/gui/gui_textures.py) and keep the tables in the document in step.
"""
import os

from PIL import Image, ImageDraw, ImageFont

ROOT = os.path.normpath(os.path.join(os.path.dirname(os.path.abspath(__file__)), '..', '..'))
TEXTURES = os.path.join(ROOT, 'src', 'main', 'resources', 'assets', 'foodindustrial', 'textures', 'gui')
FIGURES = os.path.join(ROOT, 'docs', 'texture-requirements')
RED = (220, 30, 60, 255)
WHITE = (255, 255, 255, 255)

# number, x, y, width, height: the rows of the tables in docs/texture-requirements.md
ELEMENTS = [
    (1, 0, 0, 18, 18), (2, 18, 0, 18, 18), (3, 36, 0, 18, 18), (4, 54, 0, 18, 18),
    (5, 80, 0, 24, 17), (6, 104, 0, 24, 17), (7, 128, 0, 14, 14), (8, 142, 0, 14, 14),
    (9, 160, 0, 7, 9), (10, 168, 0, 8, 8), (11, 176, 0, 24, 8),
    (12, 0, 32, 18, 64), (13, 36, 32, 14, 64), (14, 50, 32, 12, 64),
    (15, 0, 100, 16, 16), (16, 16, 100, 16, 16), (17, 64, 100, 16, 16), (18, 80, 100, 16, 16),
    (19, 96, 100, 16, 16), (24, 112, 100, 16, 16),
    (20, 0, 132, 72, 8),
    (21, 0, 160, 120, 20), (22, 120, 160, 120, 20), (23, 0, 180, 120, 20),
]
IMMERSIVE_PANEL = [
    (1, 0, 0, 16, 16), (2, 16, 0, 18, 18), (3, 0, 20, 24, 17), (4, 24, 20, 24, 17),
    (5, 40, 0, 4, 4), (6, 48, 0, 6, 6), (7, 56, 0, 4, 4),
]


def font(size):
    for path in ('/usr/share/fonts/truetype/dejavu/DejaVuSans-Bold.ttf', 'C:/Windows/Fonts/arialbd.ttf'):
        if os.path.exists(path):
            return ImageFont.truetype(path, size)
    return ImageFont.load_default()


def figure(texture, regions, scale, name, labels_outside=False, checker=((255, 255, 255, 255), (228, 228, 228, 255))):
    source = Image.open(os.path.join(TEXTURES, texture)).convert('RGBA')
    width, height = source.size
    margin = 22 if labels_outside else 0
    image = Image.new('RGBA', (width * scale + 2 + margin, height * scale + 2 + margin), WHITE)
    draw = ImageDraw.Draw(image)
    draw.rectangle([margin, margin, margin + width * scale - 1, margin + height * scale - 1], fill=checker[0])
    cell = 8 * scale
    for y in range(0, height * scale, cell):
        for x in range(0, width * scale, cell):
            if (x // cell + y // cell) % 2 == 0:
                draw.rectangle([margin + x, margin + y, margin + x + cell - 1, margin + y + cell - 1], fill=checker[1])
    big = source.resize((width * scale, height * scale), Image.NEAREST)
    image.alpha_composite(big, (margin, margin))
    letters = font(11)
    for number, x, y, w, h in regions:
        x0 = margin + x * scale
        y0 = margin + y * scale
        draw.rectangle([x0, y0, x0 + w * scale, y0 + h * scale], outline=RED)
        text = str(number)
        box = draw.textbbox((0, 0), text, font=letters)
        label_w = box[2] - box[0] + 7
        label_h = 15
        # a label sits in the region's upper left corner, or above the region when the region is small
        lx, ly = x0, y0
        if labels_outside and h * scale < 40:
            ly = y0 - label_h
        draw.rectangle([lx, ly, lx + label_w, ly + label_h], fill=RED)
        draw.text((lx + 4 - box[0], ly + 2 - box[1]), text, font=letters, fill=WHITE)
    os.makedirs(FIGURES, exist_ok=True)
    image.convert('RGB').save(os.path.join(FIGURES, name))


def main():
    figure('elements.png', ELEMENTS, 3, 'elements.png')
    # a darker board: the white square, the steam puff and the bubble are white
    figure('immersive_panel.png', IMMERSIVE_PANEL, 8, 'immersive_panel.png', labels_outside=True,
           checker=((150, 158, 170, 255), (126, 134, 146, 255)))


if __name__ == '__main__':
    main()
