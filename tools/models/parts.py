"""Small parts of the machine shapes (screws, taps, handwheels, gauges), built from cubes: each part face gets its
own rectangle in the machine's parts texture (<id>_parts.png, 32 × 32, one pixel per unit), drawn by
tools/textures/draw/part_faces.py.
"""
import numpy as np
from PIL import Image

PX = 1  # texture pixels per model unit, on every face: Minecraft's 16x standard (style guide: one density)
SIZE = 32  # the parts texture; a rectangle of n pixels spans n * 16 / SIZE UV units


class Atlas:
    """Packs part-face tiles into the parts texture, row by row; identical tiles share one rectangle."""

    def __init__(self):
        self.img = np.zeros((SIZE, SIZE, 4), dtype=np.uint8)
        self.x = 0
        self.y = 0
        self.row = 0
        self.seen = {}

    def add(self, tile):
        key = (tile.shape, tile.tobytes())
        if key in self.seen:
            return self.seen[key]
        h, w = tile.shape[:2]
        if self.x + w > SIZE:
            self.x, self.y, self.row = 0, self.y + self.row, 0
        if self.y + h > SIZE:
            raise ValueError('parts texture full')
        self.img[self.y:self.y + h, self.x:self.x + w] = tile
        k = 16 / SIZE
        rect = [self.x * k, self.y * k, (self.x + w) * k, (self.y + h) * k]
        self.seen[key] = rect
        self.x += w
        self.row = max(self.row, h)
        return rect

    def save(self, path):
        Image.fromarray(self.img, 'RGBA').save(path)
