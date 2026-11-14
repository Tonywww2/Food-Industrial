#!/usr/bin/env python3
"""Generates the placeholder texture of the recipe pages shown in JEI (I22, 2026-11-11).

  textures/gui/recipe.png  96 x 48
    row 0  (y 0):  9 x 9 condition icons, 10 px apart: temperature, pressure, atmosphere, sterile,
                   method, heat_sensitivity, environment, mode
    row 16 (y 16): 18 x 18 frames: port position (blue), controller (gold), selected device (green)

The coordinates are mirrored in gui/client/recipe/RecipeTextures.java. Existing files are overwritten
only with --force.

Requires Pillow. Usage: python3 tools/gui/recipe_textures.py [--force]
"""
import pathlib
import sys

from PIL import Image, ImageDraw

ROOT = pathlib.Path(__file__).resolve().parents[2]
TEXTURES = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures'
FORCE = '--force' in sys.argv

CLEAR = (0, 0, 0, 0)
DARK = (40, 40, 40, 255)


def save(image, relative):
    path = TEXTURES / relative
    if path.exists() and not FORCE:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)
    print('wrote', path.relative_to(ROOT))


def badge(draw, x, y, fill):
    """Round 9 x 9 badge with a dark rim."""
    draw.ellipse([x, y, x + 8, y + 8], fill=fill, outline=DARK)


def temperature(draw, x, y):
    badge(draw, x, y, (232, 92, 64, 255))
    draw.line([x + 4, y + 2, x + 4, y + 5], fill=(255, 255, 255, 255))
    draw.point((x + 4, y + 6), fill=(255, 255, 255, 255))
    draw.point((x + 3, y + 6), fill=(255, 255, 255, 255))
    draw.point((x + 5, y + 6), fill=(255, 255, 255, 255))


def pressure(draw, x, y):
    badge(draw, x, y, (120, 140, 170, 255))
    draw.line([x + 4, y + 4, x + 6, y + 2], fill=(255, 255, 255, 255))
    draw.point((x + 4, y + 4), fill=DARK)


def atmosphere(draw, x, y):
    badge(draw, x, y, (150, 200, 235, 255))
    draw.line([x + 2, y + 4, x + 6, y + 4], fill=(255, 255, 255, 255))
    draw.line([x + 3, y + 3, x + 5, y + 3], fill=(255, 255, 255, 255))
    draw.line([x + 2, y + 6, x + 5, y + 6], fill=(255, 255, 255, 255))


def sterile(draw, x, y):
    badge(draw, x, y, (90, 180, 120, 255))
    draw.line([x + 4, y + 2, x + 4, y + 6], fill=(255, 255, 255, 255))
    draw.line([x + 2, y + 4, x + 6, y + 4], fill=(255, 255, 255, 255))


def method(draw, x, y):
    badge(draw, x, y, (160, 160, 160, 255))
    draw.rectangle([x + 3, y + 3, x + 5, y + 5], outline=DARK)
    for px, py in [(4, 1), (4, 7), (1, 4), (7, 4)]:
        draw.point((x + px, y + py), fill=DARK)


def heat_sensitivity(draw, x, y):
    badge(draw, x, y, (240, 170, 60, 255))
    draw.line([x + 4, y + 2, x + 4, y + 6], fill=(80, 140, 230, 255))
    draw.line([x + 2, y + 4, x + 6, y + 4], fill=(80, 140, 230, 255))


def environment(draw, x, y):
    badge(draw, x, y, (110, 170, 70, 255))
    draw.ellipse([x + 3, y + 2, x + 5, y + 4], fill=(255, 220, 80, 255))
    draw.line([x + 2, y + 6, x + 6, y + 6], fill=(70, 120, 40, 255))


def mode(draw, x, y):
    badge(draw, x, y, (130, 110, 190, 255))
    draw.line([x + 2, y + 6, x + 2, y + 3], fill=(255, 255, 255, 255))
    draw.line([x + 2, y + 3, x + 4, y + 5], fill=(255, 255, 255, 255))
    draw.line([x + 4, y + 5, x + 6, y + 3], fill=(255, 255, 255, 255))
    draw.line([x + 6, y + 3, x + 6, y + 6], fill=(255, 255, 255, 255))


def frame(draw, x, y, color):
    draw.rectangle([x, y, x + 17, y + 17], outline=color)
    draw.rectangle([x + 1, y + 1, x + 16, y + 16], outline=color[:3] + (110,))


def main():
    image = Image.new('RGBA', (96, 48), CLEAR)
    draw = ImageDraw.Draw(image)
    for index, painter in enumerate([temperature, pressure, atmosphere, sterile, method, heat_sensitivity, environment, mode]):
        painter(draw, index * 10, 0)
    frame(draw, 0, 16, (63, 118, 228, 255))
    frame(draw, 18, 16, (230, 180, 40, 255))
    frame(draw, 36, 16, (80, 200, 80, 255))
    save(image, 'gui/recipe.png')


if __name__ == '__main__':
    main()
