"""Masonry and fire for the machines' faces (CONVENTIONS.md §11): bricks and stone laid in courses after the vanilla
bricks' and stone bricks' palettes, and the flames a firebox shows while its machine works.

Drawn in light levels (pixels.Layer). Bricks: each brick lit along its top row, a shade down along its last row and
column before the mortar; the mortar a lighter line under each course and after each brick, as in the vanilla bricks.
"""
import numpy as np

from pixels import EMBER, FIRE, SOOT, Grain


def courses(L, x0, y0, x1, y1, ramp, mortar, height=4, length=8, shift=4, seed=0, first=0, tone=(0.0, 0.0, -0.5, 0.5),
            specks=6):
    """Bricks in courses `height` pixels high (mortar on each course's last row) and `length` long (mortar on each
    brick's last column), every other course shifted by `shift`; `first` starts the courses part way through one
    (a course cut by a slab above it). Each brick one of `tone` from its neighbours; a few darker specks."""
    g = Grain(seed)
    tones = {}
    for y in range(y0, y1):
        k, r = divmod(y - y0 + first, height)
        off = (k % 2) * shift
        for x in range(x0, x1):
            j, c = divmod(x - x0 + off, length)
            if r == height - 1 or c == length - 1:
                lv = mortar.flat - (0.5 if r == height - 1 and c != length - 1 and c == 0 else 0.0)
                L.px(x, y, ramp=mortar, level=lv)
                continue
            if (k, j) not in tones:
                tones[(k, j)] = tone[g.next(len(tone))]
            lv = ramp.flat + tones[(k, j)]
            if r == 0:
                lv += 0.5
            elif r == height - 2:
                lv -= 0.5
            if c == length - 2 and r != 0:
                lv -= 0.5
            L.px(x, y, ramp=ramp, level=lv)
    for _ in range(specks):
        x, y = x0 + g.next(x1 - x0), y0 + g.next(y1 - y0)
        if L.ramp[y, x] is ramp:
            L.px(x, y, add=-0.5)


def blocks(L, x0, y0, x1, y1, ramp, joints, seed=0, level=None):
    """Dressed stones between x0 and x1 in one course (rows y0..y1-1), joints at the given columns: each stone lit
    along its top row and left column, turned away along its last row and column, the joint a dark line."""
    g = Grain(seed)
    level = ramp.flat if level is None else level
    edges = [x0] + [j + 1 for j in joints] + [x1]
    for a, b in zip(edges, edges[1:]):
        stop = b - 1 if b < x1 else b
        L.set(a, y0, stop, y1, ramp=ramp, level=level + (0.0, 0.0, -0.5, 0.5)[g.next(4)])
        L.add(a, y0, stop, y0 + 1, 0.5)
        L.add(a, y0 + 1, a + 1, y1 - 1, 0.5)
        L.add(a + 1, y1 - 2, stop, y1 - 1, -0.5)
        if stop - a > 2:
            L.add(stop - 1, y0 + 1, stop, y1 - 2, -0.5)
        for _ in range(2):
            L.px(a + 1 + g.next(max(1, stop - a - 2)), y0 + 1 + g.next(max(1, y1 - y0 - 3)), add=-0.5)
        if b < x1:
            L.set(b - 1, y0, b, y1, ramp=ramp, level=level - 2.0)
    L.set(x0, y1 - 1, x1, y1, ramp=ramp, level=level - 1.5)


def opening(shape, rows):
    """A mask from rows: {y: (x0, x1)}."""
    m = np.zeros((16, 16), bool)
    for y, (a, b) in rows.items():
        m[y, a:b] = True
    return m


def arch(x0, x1, y0, y1, step=1):
    """An opening x0..x1 wide, y0..y1 high, its top corners stepped in (an arch at 16x)."""
    rows = {y: (x0, x1) for y in range(y0, y1)}
    for k in range(step):
        rows[y0 + k] = (x0 + step - k, x1 - step + k)
    return opening(None, rows)


def fire(L, mask, lit, seed=0):
    """The back of a firebox where mask is set: lit, tongues of flame of uneven height rise from the bed, pale
    yellow at their roots, orange, red at the tips, white-hot only at the heart of the bed, soot above them; unlit,
    soot with a few dull embers on the bed."""
    g = Grain(seed)
    ys, xs = np.nonzero(mask)
    if not len(xs):
        return
    top = ys.min()
    cols = sorted(set(int(x) for x in xs))
    mid = (cols[0] + cols[-1]) / 2
    half = max(1.0, (cols[-1] - cols[0]) / 2)
    for x in cols:
        col = ys[xs == x]
        lo = col.max()
        span = lo - top + 1
        edge = abs(x - mid) / half
        tongue = max(1, int(round(span * (0.95 - 0.55 * edge * edge))) - g.next(3))
        for y in col:
            h = lo - y                               # pixels above the bed
            if not lit:
                L.px(x, y, ramp=SOOT, level=-0.5 + 0.5 * (h == 0) - 0.5 * (y == top))
            elif h >= tongue:
                L.px(x, y, ramp=SOOT, level=0.5 if h == tongue else -0.5)
            else:
                lv = 1.5 - 3.5 * h / tongue - 0.5 * (edge > 0.7)
                if edge < 0.3 and h == 0:
                    lv = 2.0
                L.px(x, y, ramp=FIRE, level=lv)
    if not lit:
        for _ in range(max(2, len(cols) // 3)):
            x = cols[g.next(len(cols))]
            L.px(x, ys[xs == x].max(), ramp=EMBER, level=-0.5 + g.next(2) * 0.5)


def embers(L, mask, lit, seed=0):
    """An ash slot under the grate: glowing embers while the fire burns, grey ash and soot when it is out."""
    g = Grain(seed)
    for y, x in zip(*np.nonzero(mask)):
        if lit:
            L.px(x, y, ramp=FIRE if g.next(3) else EMBER, level=(0.5 if g.next(3) else 1.5) - (y == np.nonzero(mask)[0].min()))
        else:
            L.px(x, y, ramp=SOOT, level=(0.5 if g.next(4) == 0 else -0.5))
