"""The masonry machines' faces (CONVENTIONS.md §11, layouts after the reference sheets): the stone mill, the boiling
stove, the earth kiln, the drying room, the brick oven and the candy table.

The openings are shared with their shapes (tools/models/machines.py): each is a mask in the face's texture
coordinates; the shape carves it out of the block, and the face draws what lies at the back of it (the flames of a
firebox, the pot in the stove's top) where the mask is set.
"""
import numpy as np

from masonry import arch, blocks, courses, embers, fire
from pixels import (BRICK, FLOUR, KILN, KILN_MORTAR, MORTAR, POT, SMOOTH, SOOT, STONE, WOOD, WOOD_DARK, Layer, edge,
                    round_mask, staves)


def rect(x0, y0, x1, y1):
    m = np.zeros((16, 16), bool)
    m[y0:y1, x0:x1] = True
    return m


def octagon(x0, y0, x1, y1, cut=1):
    m = rect(x0, y0, x1, y1)
    for k in range(cut):
        for y in (y0 + k, y1 - 1 - k):
            m[y, x0:x0 + cut - k] = m[y, x1 - cut + k:x1] = False
    return m


# the openings (texture coordinates of the face named), with their depths
OPENINGS = {
    'stone_mill': {'front': [(rect(5, 11, 9, 15), 3)], 'top': [(rect(7, 7, 9, 9), 2)]},
    'boiling_stove': {'front': [(arch(4, 12, 6, 12), 4), (rect(4, 13, 12, 15), 2)],
                      'top': [(octagon(4, 4, 12, 12, 2), 3, 'pot')]},
    'earth_kiln': {'front': [(arch(4, 12, 6, 12), 4), (rect(4, 13, 12, 15), 2)], 'top': [(rect(6, 6, 10, 10), 3)]},
    'drying_room': {'front': [(arch(4, 12, 9, 15), 3)], 'top': [(rect(4, 4, 13, 13), 2)]},
    'brick_oven': {'front': [(arch(4, 12, 6, 13, 2), 4)]},
    'candy_table': {'front': [(arch(4, 12, 5, 10), 3), (rect(4, 12, 12, 14), 2)]},
}
GRATE_SLATS = (4, 6, 8, 10, 12)        # the drying room's top: slats across, one pixel wide, built from cubes


def hole(mid, face, i=0):
    return OPENINGS[mid][face][i][0]


def sooted(L, mask, rows=4, strength=1.5):
    """Soot above an opening: the masonry over it darker, most at the opening's edge."""
    ys, xs = np.nonzero(mask)
    top = ys.min()
    for k in range(1, rows + 1):
        y = top - k
        if y < 0:
            break
        for x in range(xs.min() - 1 + (k > 2), xs.max() + 2 - (k > 2)):
            if 0 <= x < 16:
                L.px(x, y, add=-strength * (rows + 1 - k) / rows)


def surround(L, mask, ramp, level):
    """A frame of dressed stone or darker brick one pixel round an opening, lit at its top left."""
    near = np.pad(mask, 1)
    ring = ~mask & (near[:-2, 1:-1] | near[2:, 1:-1] | near[1:-1, :-2] | near[1:-1, 2:] | near[:-2, :-2] | near[:-2, 2:])
    yy, xx = np.mgrid[0:16, 0:16]
    L.mask(ring, ramp=ramp, level=level)
    ys, xs = np.nonzero(mask)
    L.mask(ring & (yy < ys.min()), add=0.5)
    L.mask(ring & (xx > xs.max()), add=-0.5)


def brick_wall(seed, ramp=BRICK, mortar=MORTAR, first=0):
    L = Layer(ramp, ramp.flat)
    courses(L, 0, 0, 16, 16, ramp, mortar, seed=seed, first=first)
    return L


# --- stone mill -----------------------------------------------------------------------------------------------------

def mill_side(seed=1):
    """The upper millstone over a base of dressed stones in two courses, a dark seam between them."""
    L = Layer(STONE, STONE.flat + 0.5)
    L.add(0, 0, 16, 1, 0.5)
    L.add(0, 5, 16, 6, -0.5)
    L.set(0, 6, 16, 7, level=STONE.flat - 1.0)                       # the stone's rounded underside
    L.set(0, 7, 16, 8, level=STONE.flat - 2.0)                       # the seam the millstone turns on
    for x, y in ((2, 2), (6, 3), (11, 2), (13, 4), (4, 4), (9, 1)):  # chisel marks
        L.px(x, y, add=-0.5)
    blocks(L, 0, 8, 16, 12, STONE, (7,), seed)
    blocks(L, 0, 12, 16, 16, STONE, (3, 11), seed + 1)
    return L


def mill_front():
    """The flour spout's opening, low on the front, in a wooden frame; flour heaped at the back of it."""
    L = mill_side(5)
    m = hole('stone_mill', 'front')
    L.set(4, 10, 10, 11, ramp=WOOD, level=WOOD.flat + 0.5)          # lintel, posts
    L.set(4, 11, 5, 16, ramp=WOOD, level=WOOD.flat)
    L.set(9, 11, 10, 16, ramp=WOOD, level=WOOD.flat - 0.5)
    L.set(4, 15, 10, 16, ramp=WOOD, level=WOOD.flat - 0.5)
    L.mask(m, ramp=SOOT, level=0.0)
    L.set(5, 11, 9, 12, ramp=SOOT, level=-1.0)
    L.set(5, 13, 9, 15, ramp=FLOUR, level=FLOUR.flat)                # the heap
    L.set(6, 13, 8, 14, level=FLOUR.flat + 1.0)
    L.px(5, 13, ramp=SOOT, level=0.0)
    L.px(8, 13, ramp=SOOT, level=0.0)
    return L


def mill_top():
    """The millstone's face: grooves in rings round the eye, where the grain goes in."""
    L = Layer(STONE, STONE.flat + 0.5)
    L.add(0, 0, 16, 1, 0.5)
    L.add(0, 1, 1, 16, 0.5)
    L.add(0, 15, 16, 16, -1.0)
    L.add(15, 0, 16, 15, -1.0)
    for widths, y0 in (((6, 10, 12, 12, 14, 14, 14, 14, 14, 14, 12, 12, 10, 6), 1), ((4, 6, 8, 8, 8, 8, 6, 4), 4)):
        ring = edge(round_mask(1 if y0 == 1 else 4, y0, widths))
        L.mask(ring, level=STONE.flat - 1.0)
    L.set(7, 7, 9, 9, level=STONE.flat - 2.0)                        # the eye
    L.px(6, 6, add=0.5)
    return L


def mill_inner():
    L = Layer(STONE, STONE.flat - 1.5)
    L.vgrad(0, 0, 16, 16, -0.5, 0.5)
    return L


# --- fireboxes ------------------------------------------------------------------------------------------------------

def firebox_front(mid, lit, seed, ramp=BRICK, mortar=MORTAR, first=0, frame=(BRICK, -1.5)):
    """Bricks; the firebox low on the front with flames at its back while the machine works, an iron grate bar
    under it and the ash slot below that; the bricks round the opening burnt dark, soot over it."""
    L = brick_wall(seed, ramp, mortar, first)
    boxes = OPENINGS[mid]['front']
    surround(L, boxes[0][0], *frame)
    if len(boxes) > 1:
        surround(L, boxes[1][0], *frame)
    sooted(L, boxes[0][0])
    fire(L, boxes[0][0], lit, seed)
    if len(boxes) > 1:
        ys, xs = np.nonzero(boxes[1][0])
        L.set(xs.min(), ys.min() - 1, xs.max() + 1, ys.min(), ramp=POT, level=0.5)   # the grate bar
        L.px(xs.min(), ys.min() - 1, add=0.5)
        embers(L, boxes[1][0], lit, seed)
    return L


def soot_inner(ramp=BRICK, mortar=MORTAR):
    """The walls inside an opening: masonry under soot, darkest at the top."""
    L = Layer(ramp, ramp.flat)
    courses(L, 0, 0, 16, 16, ramp, mortar, seed=9)
    L.add(0, 0, 16, 16, -1.5)
    L.vgrad(0, 0, 16, 16, -0.5, 0.25)
    return L


def pot_inner():
    """The iron pot set into the stove's top: its wall, lit along the rim."""
    L = Layer(POT, POT.flat)
    L.vgrad(0, 0, 16, 16, 0.5, -1.0)
    return L


def stove_top():
    """Bricks round the pot set into the top: a stone rim round it, the pot's dark iron inside with the water
    shining at its middle."""
    L = brick_wall(17)
    m = hole('boiling_stove', 'top')
    surround(L, m, SMOOTH, -1.0)
    L.mask(m, ramp=POT, level=-0.5)
    L.set(6, 6, 10, 10, level=0.0)
    L.set(7, 7, 9, 9, level=0.5)
    L.px(6, 6, level=1.0)
    return L


def kiln_top():
    """Dark bricks round the flue in the middle of the top."""
    L = Layer(KILN, KILN.flat)
    courses(L, 0, 0, 16, 16, KILN, KILN_MORTAR, seed=27)
    m = hole('earth_kiln', 'top')
    surround(L, m, KILN, -1.0)
    L.mask(m, ramp=SOOT, level=-1.0)
    return L


def drying_front(lit):
    """A louvred vent in a wooden frame high on the front, the firebox below it."""
    L = firebox_front('drying_room', lit, 31)
    L.set(5, 3, 11, 7, ramp=WOOD, level=WOOD.flat)
    L.set(5, 3, 11, 4, level=WOOD.flat + 0.5)
    L.set(5, 4, 6, 7, level=WOOD.flat + 0.5)
    L.set(6, 6, 11, 7, level=WOOD.flat - 0.5)
    for y in (4, 5):
        L.set(6, y, 10, y + 1, ramp=WOOD_DARK, level=-1.0 if y == 4 else -0.5)
    L.add(5, 7, 11, 8, -0.75)
    return L


def drying_top():
    """A wooden frame round the grate (its slats are the model's), the warm dark of the flue under them."""
    L = brick_wall(37)
    L.set(3, 3, 14, 14, ramp=WOOD, level=WOOD.flat)
    staves(L, 3, 3, 14, 4, (1,), False, 38, ramp=WOOD)
    L.set(3, 3, 14, 4, level=WOOD.flat + 0.5)
    L.set(3, 4, 4, 14, level=WOOD.flat + 0.5)
    L.set(4, 13, 14, 14, level=WOOD.flat - 0.5)
    L.set(13, 4, 14, 13, level=WOOD.flat - 0.5)
    L.mask(hole('drying_room', 'top'), ramp=SOOT, level=-0.5)
    L.set(4, 4, 13, 5, level=-1.5)
    return L


def oven_top():
    """Four dark stone slabs over the oven, sooted most towards the front where the smoke comes out."""
    L = Layer(STONE, STONE.flat - 2.0)
    for (x0, y0, x1, y1), k in zip(((0, 0, 8, 8), (8, 0, 16, 8), (0, 8, 8, 16), (8, 8, 16, 16)), range(4)):
        blocks(L, x0, y0, x1, y1, STONE, (), 51 + k, level=STONE.flat - 2.0)
        if x1 < 16:
            L.set(x1 - 1, y0, x1, y1, level=STONE.flat - 3.5)
    for x, y in ((3, 1), (4, 1), (5, 2), (4, 2), (10, 1), (11, 2), (12, 1), (6, 4), (2, 9), (13, 10), (9, 12)):
        L.px(x, y, ramp=SOOT, level=0.5)
    return L


def table_side(seed=61):
    """A smooth stone slab for a top, its edge two pixels: lit, then its underside; bricks below in its shadow."""
    L = brick_wall(seed, first=2)
    L.set(0, 0, 16, 1, ramp=SMOOTH, level=SMOOTH.flat + 0.5)
    L.set(0, 1, 16, 2, ramp=SMOOTH, level=SMOOTH.flat - 1.0)
    L.add(0, 2, 16, 3, -1.0)
    return L


def table_top():
    """The candy table's smooth stone slab, its edge lit at the top left."""
    L = Layer(SMOOTH, SMOOTH.flat)
    L.bevel(0, 0, 16, 16, 0.5, -0.5, inner=0)
    for x, y in ((4, 3), (10, 6), (6, 11), (12, 12), (2, 8)):
        L.px(x, y, add=-0.5)
    return L


def candy_front(lit):
    L = firebox_front('candy_table', lit, 63, first=2)
    L.set(0, 0, 16, 1, ramp=SMOOTH, level=SMOOTH.flat + 0.5)
    L.set(0, 1, 16, 2, ramp=SMOOTH, level=SMOOTH.flat - 1.0)
    L.add(0, 2, 16, 3, -1.0)
    return L


DRAWN = {
    'stone_mill': {'front': mill_front, 'front_active': mill_front, 'side': mill_side, 'top': mill_top,
                   'inner': mill_inner},
    'boiling_stove': {'front': lambda: firebox_front('boiling_stove', False, 11),
                      'front_active': lambda: firebox_front('boiling_stove', True, 11),
                      'side': lambda: brick_wall(12), 'top': stove_top, 'inner': soot_inner, 'pot': pot_inner},
    'earth_kiln': {'front': lambda: firebox_front('earth_kiln', False, 21, KILN, KILN_MORTAR, frame=(KILN, -1.0)),
                   'front_active': lambda: firebox_front('earth_kiln', True, 21, KILN, KILN_MORTAR, frame=(KILN, -1.0)),
                   'side': lambda: brick_wall(22, KILN, KILN_MORTAR), 'top': kiln_top,
                   'inner': lambda: soot_inner(KILN, KILN_MORTAR)},
    'drying_room': {'front': lambda: drying_front(False), 'front_active': lambda: drying_front(True),
                    'side': lambda: brick_wall(32), 'top': drying_top, 'inner': soot_inner},
    'brick_oven': {'front': lambda: firebox_front('brick_oven', False, 41),
                   'front_active': lambda: firebox_front('brick_oven', True, 41),
                   'side': lambda: brick_wall(42), 'top': oven_top, 'inner': soot_inner},
    'candy_table': {'front': lambda: candy_front(False), 'front_active': lambda: candy_front(True),
                    'side': table_side, 'top': table_top, 'inner': soot_inner},
}
