"""The faces of the parts the machine models build from cubes (tools/models/machines.py): the press's screw, nut,
plate, bed and trough, the vat's tap, the tank's gauges and handwheel (user 2026-11-26: the parts get drawn textures
and models of their own, as the machines' faces do). Drawn in light levels like the faces (pixels.Layer), one pixel
per unit, at whatever size the cube's face is; machines.Part(painter, ...) puts a painter on a cube face and calls
painter(w, h, face, ...), which returns a Layer (or, for the gauge's dial, a Canvas with its own transparency).
"""
import numpy as np

from pixels import (BRASS, CHOCO, COPPER, CYAN, DOUGH, FLOUR, IRON_DARK, ORANGE, STEEL, STEEL_DARK, WHITE, WOOD,
                    WOOD_DARK, Canvas, Layer, staves)


def metal(w, h, face, ramp, level=0.0):
    """A small metal cube's face: the top row and left column catch the light, the bottom row and right column turn
    away from it, the top left corner glints; a face one pixel wide still runs from light at the top to dark at the
    bottom."""
    L = Layer(ramp, level, w, h)
    if h >= 2:
        L.add(0, 0, w, 1, 1.0)
    if w >= 2:
        L.add(0, 1 if h >= 2 else 0, 1, h, 0.5)
    if h >= 3:
        L.add(1 if w >= 2 else 0, h - 1, w, h, -1.0)
    if w >= 3:
        L.add(w - 1, 1, w, h - 1, -0.5)
    if w >= 2 and h >= 2:
        L.px(0, 0, add=0.5)
    return L


def screw(w, h, face, ramp=IRON_DARK):
    """The press's screw, as the sheet draws it: a column striped by its thread, the crest a pixel high and lit,
    the root below it nearly black, the left side lighter. The thread steps one pixel at each corner of the square
    shaft, so it winds round; the ends are plain."""
    if face in ('up', 'down'):
        return metal(w, h, face, ramp)
    L = Layer(ramp, 0.0, w, h)
    phase = 1 if face in ('east', 'west') else 0
    for y in range(h):
        crest = (y + phase) % 2 == 0
        for x in range(w):
            side = 0.5 if x == 0 else (-0.5 if x == w - 1 and w > 1 else 0.0)
            L.px(x, y, level=(0.5 if crest else -1.5) + side)
    return L


def nut(w, h, face, ramp=IRON_DARK):
    """The nut under the press's top beam the screw turns in: a block of iron, its bolt heads on the front."""
    L = metal(w, h, face, ramp, 0.5)
    if face in ('north', 'south') and w >= 4:
        for x in (1, w - 2):
            L.px(x, h // 2, level=2.0)
    return L


def platen(w, h, face, ramp=IRON_DARK):
    """The press's plate: lighter iron, a bolt head near each end of its front; on its top the screw's shadow,
    to the lower right of where the screw comes through."""
    L = metal(w, h, face, ramp, 0.5)
    if face in ('north', 'south') and w >= 6 and h >= 2:
        for x in (1, w - 2):
            L.px(x, h - 1, level=1.5)
    if face == 'up':
        cx = w // 2                                    # the screw stands on the middle two pixels
        L.add(cx + 1, 1, cx + 2, h - 1, -1.0)
        L.add(cx - 1, h - 2, cx + 2, h - 1, -1.0)
    return L


def bed(w, h, face):
    """The press's bed: boards, the juice groove round the edge of its top dark and wet."""
    L = Layer(WOOD, WOOD.flat, w, h)
    if face == 'up':
        staves(L, 0, 0, w, h, (h,), False, 71)
        if w >= 6 and h >= 4:
            ring = np.zeros((h, w), bool)
            ring[1, 1:w - 1] = ring[h - 2, 1:w - 1] = True
            ring[1:h - 1, 1] = ring[1:h - 1, w - 2] = True
            L.mask(ring, ramp=WOOD_DARK, level=WOOD_DARK.flat)
            L.mask(ring & (np.arange(h)[:, None] == 1), add=-1.0)   # deepest under its far lip
        return L
    if w >= h:
        staves(L, 0, 0, w, h, (h,), False, 72)
    else:
        staves(L, 0, 0, w, h, (w,), True, 73)
    return L


def boards(w, h, face, seed=81):
    """A wooden part's face: one board along its length, lit along its upper or left edge, the seam along the
    other."""
    L = Layer(WOOD, WOOD.flat, w, h)
    if w >= h:
        staves(L, 0, 0, w, h, (h,), False, seed, specks=1)
    else:
        staves(L, 0, 0, w, h, (w,), True, seed, specks=1)
    return L


def wet(w, h, face):
    """The trough's floor where the juice runs: wood in the dark browns, its far edge lit."""
    L = Layer(WOOD_DARK, WOOD_DARK.flat + 0.5, w, h)
    L.add(0, h - 1, w, h, 0.5)
    return L


def brass(w, h, face):
    """The vat's tap: polished brass."""
    return metal(w, h, face, BRASS)


def opening(w, h, face):
    """The mouth of the tap's spout."""
    return Layer(BRASS, -2.0, w, h)


def octagon_mask(n):
    """An n × n face with its corners cut, as two crossed cubes show it: n // 2 - 1 pixels off each corner, in steps
    (5: one pixel, 7: a step of two, 8: of two)."""
    yy, xx = np.mgrid[0:n, 0:n]
    cut = 1 if n <= 6 else 2
    d = np.minimum(np.minimum(xx, n - 1 - xx), 99) + np.minimum(yy, n - 1 - yy)
    return d >= cut


def crossed(n, painter):
    """A picture for two crossed cubes n across: painter(L, inside, ring) paints the Layer; returns a Canvas,
    transparent outside the octagon."""
    on = octagon_mask(n)
    p = np.pad(on, 1)
    inside = on & p[:-2, 1:-1] & p[2:, 1:-1] & p[1:-1, :-2] & p[1:-1, 2:]
    L = Layer(WHITE, 0.0, n, n)
    painter(L, inside, on & ~inside)
    c = L.render()
    c.a[..., 3] = np.where(on, 255, 0)
    return c


def lit_ring(L, ring, ramp, level, n):
    yy, xx = np.mgrid[0:n, 0:n]
    L.mask(ring, ramp=ramp, level=level)
    L.mask(ring & (xx + yy < n - 1), add=1.0)
    L.mask(ring & (xx + yy > n - 1), add=-1.0)


def dial(n=5):
    """A gauge's face, n × n with the corners cut (an octagon, as its two crossed cubes are): a white dial in a
    steel bezel; the bezel lit at the top left, turned away at the bottom right, its shadow on the dial under its
    top edge; a bigger dial has its scale's marks. Returns a Canvas, transparent outside the octagon."""
    def paint(L, inside, ring):
        L.mask(inside, ramp=WHITE, level=1.0)
        L.set(1, 1, n - 1, 2, level=0.0)
        if n >= 7:
            for x, y in ((1, n // 2), (n // 2, 1), (n - 2, n // 2), (2, 2), (n - 3, 2)):
                L.px(x, y, ramp=STEEL_DARK, level=-1.0)
        lit_ring(L, ring, STEEL_DARK, 0.5, n)
    return crossed(n, paint)


def perforated(n=7):
    """The meat grinder's plate: polished steel pierced by rows of holes, its rim lit at the top left."""
    def paint(L, inside, ring):
        L.mask(inside, ramp=STEEL, level=0.5)
        for y in range(1, n - 1):
            for x in range(1, n - 1):
                if inside[y, x] and (x + y) % 2 == 0 and 0 < x < n - 1:
                    L.px(x, y, ramp=IRON_DARK, level=-1.0)
        lit_ring(L, ring, STEEL, 0.5, n)
    return crossed(n, paint)


def hatch(n=8):
    """The pot still's hatch: a brass rim round a copper plate, bolted, a brass boss in the middle."""
    def paint(L, inside, ring):
        L.mask(inside, ramp=COPPER, level=0.5)
        lit_ring(L, ring, BRASS, 0.0, n)
        c = n // 2
        L.set(c - 1, c - 1, c + 1, c + 1, ramp=BRASS, level=0.5)
        L.px(c - 1, c - 1, level=1.5)
        for x, y in ((1, c), (c, 1), (n - 2, c - 1), (c - 1, n - 2)):
            L.px(x, y, ramp=BRASS, level=-1.0)
    return crossed(n, paint)


def dough(w, h, face):
    """A sheet of dough: soft, its upper edge a little lighter."""
    L = Layer(DOUGH, DOUGH.flat, w, h)
    if h >= 2:
        L.add(0, 0, w, 1, 0.5)
    return L


def floured(w, h, face):
    """A sheet of dough dusted with flour."""
    L = Layer(FLOUR, FLOUR.flat, w, h)
    if h >= 2:
        L.add(0, h - 1, w, h, -0.5)
    return L


def roller(w, h, face):
    """The rolling pin: wood, turned, lit along its upper side."""
    L = Layer(WOOD, WOOD.flat, w, h)
    if face in ('north', 'south', 'up', 'down') and h >= 1:
        L.add(0, 0, w, 1, 0.5)
    return L


def thermometer(w, h, face):
    """The koji room's thermometer: a glass tube on a white card, red spirit in its lower part and the bulb."""
    L = Layer(WHITE, 0.5, w, h)
    if face == 'north':
        L.set(0, h - 2, w, h, ramp=COPPER, level=-1.5)
        L.set(0, h // 2, w, h - 2, ramp=COPPER, level=-0.5)
        L.px(0, 0, level=1.5)
    return L


def steel(w, h, face):
    return metal(w, h, face, STEEL)


def copper(w, h, face):
    return metal(w, h, face, COPPER, 0.5)


def indicator(w, h, face, ramp=ORANGE):
    """A small instrument box: dark steel, its lamp in the middle of its front."""
    L = metal(w, h, face, STEEL_DARK, 0.0)
    if face == 'north' and w >= 3 and h >= 3:
        L.set(1, 1, w - 1, h - 1, ramp=IRON_DARK, level=-1.0)
        L.px(w // 2, h // 2, ramp=ramp, level=1.0)
    return L


def lamps(w, h, face):
    """A strip of lamps one pixel wide: cyan ones above, a red one at the foot."""
    L = metal(w, h, face, STEEL_DARK, 0.0)
    if face == 'north':
        for y in range(1, h - 1, 2):
            L.px(0, y, ramp=CYAN, level=1.0)
        L.px(0, h - 1, ramp=COPPER, level=-1.0)
    return L


def chocolate(w, h, face):
    """Chocolate heaped up, glossy along its top."""
    L = Layer(CHOCO, CHOCO.flat, w, h)
    if h >= 2:
        L.add(0, 0, w, 1, 1.0)
    L.add(0, 0, 1, h, 0.5)
    return L


def enamel(w, h, face):
    """White enamelled pipe."""
    return metal(w, h, face, WHITE, 0.0)


def knob(n=4, ramp=STEEL_DARK):
    """A control knob, round: lit at the top left, its pointer a light line to the top."""
    def paint(L, inside, ring):
        L.mask(inside, ramp=ramp, level=-0.5)
        lit_ring(L, ring, ramp, 0.0, n)
        L.set(n // 2 - 1, 0, n // 2, n // 2, ramp=STEEL if ramp is STEEL_DARK else ramp, level=1.0 if ramp is STEEL_DARK else 1.5)
    return crossed(n, paint)


def membrane(w, h, face):
    """A membrane module: a cyan glass tube, lit along its top, the membrane's darker core seen through it."""
    L = Layer(CYAN, 0.0, w, h)
    if h >= 2:
        L.add(0, 0, w, 1, 1.0)
        L.add(0, h - 1, w, h, -1.0)
    if face in ('east', 'west'):
        L = metal(w, h, face, STEEL_DARK, 0.0)
    return L


def minigauge(w, h, face):
    """A small square gauge: a white face in a dark steel case, its needle a dark pixel."""
    L = metal(w, h, face, STEEL_DARK, 0.0)
    if face == 'north' and w >= 3 and h >= 3:
        L.set(1, 1, w - 1, h - 1, ramp=WHITE, level=1.0)
        L.px(w // 2, h // 2, ramp=IRON_DARK, level=-2.0)
    return L


def bezel(w, h, face):
    return metal(w, h, face, STEEL_DARK, 0.5)


def needle(w, h, face):
    return metal(w, h, face, IRON_DARK, -1.0)


def wheel(w, h, face, level=0.0):
    """A piece of the handwheel's rim, its spokes or hub: dark iron; level lifts the pieces at the wheel's upper
    left and lowers those at its lower right, so the ring reads round."""
    return metal(w, h, face, IRON_DARK, level)


def hub(w, h, face):
    return metal(w, h, face, STEEL_DARK, 0.5)


def tile(painted):
    """The RGBA array of a painter's result."""
    return (painted if isinstance(painted, Canvas) else painted.render()).a
