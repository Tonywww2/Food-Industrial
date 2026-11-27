"""The steel machines of the second tier (CONVENTIONS.md §11, layouts after the reference sheets): the crusher, the
solid-liquid separator, the stirred reactor, the evaporating crystallizer, the dryer, the absorption tower, the rotary
kiln, the mixer, the forming line, the homogenizing sterilizer, the baking line, the cooker-fryer, the aging cellar,
the cutting station, the confectionery line, the freezing unit and the filling-retort line.

Brushed steel plates as the approved extraction tank's (seams, rivets, a bevel turning over two pixels). Openings
are shared with the shapes as in kilns.py; windows are set back a unit with what lies behind drawn on the glass (as
the approved culture bench's), slots and grilles are cut a unit deep, things standing out (gauges, knobs, hinges,
handles, rollers, teeth) are the shapes' cubes. Lights, screens and glowing openings change on front_active.
"""
import numpy as np

from crafts import deep, ring_round
from kilns import octagon, rect
from pixels import (BRASS, CHOCO, CYAN, GLASS, GLASS_LIT, ICE, IRON_DARK, ORANGE, SNOW, STEEL, STEEL_DARK, Grain,
                    Layer, stud)

CORNERS = ((2, 2), (13, 2), (2, 13), (13, 13))


def slots(x0, x1, rows):
    """A grille's slots: one row each, x0..x1 wide."""
    m = np.zeros((16, 16), bool)
    for y in rows:
        m[y, x0:x1] = True
    return m


def columns(cols, y0, y1):
    m = np.zeros((16, 16), bool)
    for x in cols:
        m[y0:y1, x] = True
    return m


def parts_of(mask):
    """A mask's slots as separate masks, one per row run (opening_cuts takes one run of pixels to a row)."""
    out = []
    for y in range(16):
        xs = np.nonzero(mask[y])[0]
        if len(xs):
            row = np.zeros((16, 16), bool)
            row[y, xs] = True
            out.append(row)
    return out


LOW_VENTS = slots(3, 7, (10, 12, 14)) | slots(9, 13, (10, 12, 14))

OPENINGS = {
    'crusher': {'front': [(rect(2, 2, 14, 12), 5)], 'top': [(rect(3, 6, 13, 9), 4)]},
    'solid_liquid_separator': {'front': [(rect(2, 3, 13, 10), 1)],
                               'west': [(m, 1) for m in parts_of(slots(4, 12, (4, 6, 8, 10, 12)))],
                               'east': [(m, 1) for m in parts_of(slots(4, 12, (4, 6, 8, 10, 12)))]},
    'stirred_reactor': {'front': [(rect(4, 2, 9, 13), 1)], 'top': [(octagon(3, 3, 13, 13, 3), 3, 'collar')]},
    'evaporating_crystallizer': {'front': [(rect(5, 2, 9, 13), 1)], 'top': [(octagon(3, 3, 13, 13, 3), 2, 'collar')]},
    'dryer': {'front': [(rect(5, 5, 10, 9), 1)], 'top': [(m, 1) for m in parts_of(slots(3, 13, (4, 6, 8, 10, 12)))],
              'west': [(rect(5, 3, 11, 8), 1)] + [(m, 1) for m in parts_of(LOW_VENTS)],
              'east': [(rect(5, 3, 11, 8), 1)] + [(m, 1) for m in parts_of(LOW_VENTS)],
              'south': [(m, 1) for m in parts_of(LOW_VENTS)]},
    'absorption_tower': {'front': [(rect(3, 3, 13, 12), 1)]},
    'rotary_kiln': {'front': [(octagon(4, 4, 11, 11, 2), 1)],
                    'top': [(m, 1) for m in parts_of(slots(5, 11, (6, 8, 10)))]},
    'mixer': {'front': [(rect(3, 3, 13, 13), 1)], 'top': [(octagon(4, 4, 12, 12, 2), 2)]},
    'forming_line': {'front': [(rect(3, 2, 13, 11), 4)]},
    'baking_line': {'front': [(rect(3, 2, 13, 7), 1), (rect(3, 8, 13, 12), 2)]},
    'cooker_fryer': {'front': [(rect(2, 2, 10, 9), 1), (rect(11, 2, 14, 8), 1)],
                     'top': [(m, 1) for m in parts_of(slots(2, 14, (3, 5, 7, 9, 11, 13)))]},
    'cutting_station': {'front': [(m, 2) for m in [columns((c,), 2, 7) for c in (4, 6, 8, 10, 12)]] + [(rect(4, 9, 11, 14), 3)],
                        'top': [(m, 1) for m in parts_of(slots(3, 13, (4, 6, 8, 10)))]},
    'confectionery_line': {'front': [(rect(2, 3, 10, 12), 3)], 'top': [(rect(3, 3, 13, 13), 1)]},
    'freezing_unit': {'front': [(rect(2, 2, 14, 13), 1)]},
    'filling_retort_line': {'front': [(rect(2, 2, 14, 14), 2)]},
}


def hole(mid, face, i=0):
    return OPENINGS[mid][face][i][0]


# --- plates ---------------------------------------------------------------------------------------------------------

def plate(seed, vseams=(), hseams=(), rivets='corners', ramp=STEEL):
    """A brushed steel plate as the extraction tank's: lighter at the top, two faint brushed lines, its edge a single
    pixel lit at the top left, riveted at the corners (or all along its edges); seams with a lit and a shaded side."""
    L = Layer(ramp)
    L.vgrad(0, 0, 16, 16, 0.25, -0.25)
    g = Grain(seed)
    for _ in range(2):
        y, x = 4 + g.next(8), 2 + g.next(4)
        L.add(x, y, x + 6 + g.next(5), y + 1, 0.5)
    for x in vseams:
        L.add(x, 2, x + 1, 14, -1.25)
        L.add(x + 1, 2, x + 2, 14, 0.75)
    for y in hseams:
        L.add(2, y, 14, y + 1, -1.25)
        L.add(2, y + 1, 14, y + 2, 0.75)
    L.bevel(0, 0, 16, 16, 0.75, -0.75, inner=0)     # a one-pixel edge, as the sheets draw it (user 2026-11-26)
    if rivets == 'corners':
        for x, y in CORNERS:
            stud(L, x, y, ramp)
    elif rivets == 'edges':
        for i in range(2, 15, 3):
            for x, y in ((i, 1), (1, i), (i, 14), (14, i)):
                stud(L, x, y, ramp)
    return L


def frame(L, mask, ramp=STEEL_DARK, level=0.0):
    """A frame one pixel round an opening, lit at its top left."""
    ring_round(L, mask, ramp, level)


def glass(L, mask, lit, behind=None):
    """Glass, lit up while the machine works: two reflections across it at the top left; behind(L) draws what is
    seen through it first."""
    ramp = GLASS_LIT if lit else GLASS
    L.mask(mask, ramp=ramp, level=0.0)
    ys, xs = np.nonzero(mask)
    L.mask(mask & (np.mgrid[0:16, 0:16][0] == ys.min()), add=-1.0)
    if behind:
        behind(L)
    for k, d in ((0, 1.0), (2, 0.5)):
        for i in range(4):
            x, y = xs.min() + 1 + k + i, ys.min() + 3 - i
            if 0 <= y < 16 and mask[y, x]:
                L.px(x, y, ramp=ramp, level=d + 0.5)


def dark(L, mask, level=-1.5, ramp=IRON_DARK):
    """The inside of a chamber or a slot: dark, darkest under its top and left edges."""
    deep(L, mask, ramp, level)


def light(L, x, y, ramp, lit, size=1):
    """An indicator lamp: bright with a pale centre while lit, dull when not."""
    L.set(x, y, x + size, y + size, ramp=ramp, level=1.0 if lit else -1.5)
    if lit:
        L.px(x, y, level=2.0)


def screen(L, x0, y0, x1, y1, lit, seed, dots=10):
    """A display: dark glass in a dark bezel; while lit, lines of readings in cyan, a pale one at the top; off, the
    glass only catches a glint at its top left."""
    L.set(x0, y0, x1, y1, ramp=IRON_DARK, level=-1.0)
    L.set(x0 + 1, y0 + 1, x1 - 1, y1 - 1, level=-2.0)
    if not lit:
        L.px(x0 + 1, y0 + 1, level=-0.5)
        return
    g = Grain(seed)
    for k, y in enumerate(range(y0 + 1, y1 - 1, 2)):
        x = x0 + 1
        while x < x1 - 1 and dots > 0:
            n = 1 + g.next(3)
            for i in range(min(n, x1 - 1 - x)):
                L.px(x + i, y, ramp=CYAN, level=1.5 if k == 0 else 0.5)
            x += n + 1 + g.next(2)
            dots -= 1


def vent_shade(L, mask):
    """Slots cut a unit deep (the shape cuts them): dark, the slat under each lit along its top."""
    L.mask(mask, ramp=IRON_DARK, level=-1.5)
    below = np.roll(mask, 1, axis=0) & ~mask
    L.mask(below, add=0.5)


def frosted(L, seed):
    """Frost along a plate's edges: a rim of rime, creeping in a pixel here and there, a rare glint of ice."""
    g = Grain(seed)
    for i in range(16):
        for x, y in ((i, 0), (0, i), (i, 15), (15, i)):
            L.px(x, y, ramp=SNOW, level=SNOW.flat - 0.5 * (x == 15 or y == 15))
        for x, y in ((i, 1), (1, i), (i, 14), (14, i)):
            k = g.next(6)
            if k < 3:
                L.px(x, y, ramp=SNOW, level=SNOW.flat - 1.0)
            elif k == 3:
                L.px(x, y, ramp=ICE, level=2.0)


# --- the machines' faces ------------------------------------------------------------------------------------------

def crusher_front():
    """The crushing chamber, dark, in a steel frame (its teeth are the shape's)."""
    L = plate(301)
    m = hole('crusher', 'front')
    frame(L, m)
    dark(L, m, -1.5)
    return L


def crusher_top():
    L = plate(305)
    m = hole('crusher', 'top')
    frame(L, m)
    dark(L, m, -2.0)
    return L


def separator_front(lit):
    """A wide window onto the tank: liquid in its lower half, a filter screen across it."""
    L = plate(311)
    m = hole('solid_liquid_separator', 'front')
    frame(L, m)

    def behind(L):
        L.mask(m & (np.mgrid[0:16, 0:16][0] >= 7), add=-1.0)
        L.set(3, 7, 12, 8, level=0.5 if lit else -0.5)
    glass(L, m, lit, behind)
    return L


def vented(seed, m):
    L = plate(seed)
    vent_shade(L, m)
    ring_round(L, slots(4, 12, range(4, 13)), STEEL_DARK, 0.0)
    return L


def separator_side():
    return vented(315, slots(4, 12, (4, 6, 8, 10, 12)))


def reactor_front(lit):
    """A tall window onto the stirred tank (its indicators are the shape's cubes)."""
    L = plate(321)
    m = hole('stirred_reactor', 'front')
    frame(L, m)
    glass(L, m, lit)
    return L


def collar_top(seed, mid, ramp, inside=IRON_DARK):
    """A collar round the vessel's mouth in the middle of the plate: lit at the top left; the vessel dark below."""
    L = plate(seed)
    m = hole(mid, 'top')
    ring_round(L, m, ramp, 0.5, width=2)
    dark(L, m, -1.5, inside)
    return L


def collar_inner(ramp):
    """A vessel's mouth: the collar's metal at the top, dark steel down."""
    def paint():
        L = Layer(STEEL_DARK, -1.0)
        L.vgrad(0, 0, 16, 16, 0.0, -1.0)
        L.set(0, 0, 16, 1, ramp=ramp, level=0.5)
        L.set(0, 1, 16, 2, ramp=ramp, level=-0.5)
        return L
    return paint


def crystallizer_front(lit):
    """A tall glass tube with the brine in it, glowing cyan while the machine works."""
    L = plate(331)
    m = hole('evaporating_crystallizer', 'front')
    frame(L, m)
    glass(L, m, lit)
    if lit:
        L.mask(m, ramp=CYAN, level=0.5)
        L.set(6, 3, 7, 12, level=1.5)
    return L


def crystallizer_top():
    """A glass dome's ring round the vessel's mouth, crystals forming on the steel below."""
    L = collar_top(335, 'evaporating_crystallizer', GLASS)
    for x, y in ((6, 7), (9, 8), (7, 10), (10, 6)):
        L.px(x, y, ramp=SNOW, level=0.5)
    return L


def dryer_front(lit):
    """A door all over the front: its edge a seam, a small window in it (hinges and handle are the shape's)."""
    L = plate(341)
    door = rect(1, 1, 15, 15)
    ring = door & ~rect(2, 2, 14, 14)
    L.mask(ring, add=-1.0)
    m = hole('dryer', 'front')
    frame(L, m)
    glass(L, m, lit)
    return L


def dryer_side(window=True, seed=345):
    """A small window into the drying chamber and two groups of vents low down (the back has the vents only)."""
    L = plate(seed)
    if window:
        win = rect(5, 3, 11, 8)
        frame(L, win)
        glass(L, win, False)
    vent_shade(L, LOW_VENTS)
    return L


def dryer_top():
    L = plate(349)
    vent_shade(L, slots(3, 13, (4, 6, 8, 10, 12)))
    ring_round(L, rect(3, 4, 13, 13), STEEL_DARK, 0.0)
    return L


def tower_front(lit):
    """A control panel set in a unit: a display, a row of keys, a cyan button."""
    L = plate(351, rivets='edges')
    m = hole('absorption_tower', 'front')
    frame(L, m)
    L.mask(m, ramp=STEEL_DARK, level=-0.5)
    screen(L, 4, 4, 12, 8, lit, 352, dots=0)
    if lit:
        L.set(5, 5, 9, 6, ramp=CYAN, level=0.0)
        L.set(5, 6, 8, 7, ramp=CYAN, level=-0.5)
    for x in (4, 6):
        L.set(x, 9, x + 1, 11, ramp=IRON_DARK, level=-0.5)
    light(L, 10, 9, CYAN, lit, size=2)
    return L


def kiln_band(L):
    """The drum's band round the middle (it stands out on the sides: the shape's cubes) and the dark iron corner
    brackets."""
    for x0, y0 in ((0, 0), (13, 0), (0, 13), (13, 13)):
        L.set(x0, y0, x0 + 3, y0 + 3, ramp=IRON_DARK, level=0.0)
        L.px(x0, y0, level=1.0)
        L.px(x0 + 2, y0 + 2, level=-1.0)


def kiln_front(lit):
    """The fire's window in the middle of the front, glowing orange while the kiln turns (its rim is the shape's)."""
    L = plate(361, rivets=None)
    kiln_band(L)
    m = hole('rotary_kiln', 'front')
    L.mask(rect(4, 4, 11, 11) & ~m, ramp=IRON_DARK, level=-1.0)   # the rim's inner corners
    if lit:
        L.mask(m, ramp=ORANGE, level=0.5)
        L.set(6, 6, 9, 9, level=1.5)
        L.px(6, 6, level=2.0)
    else:
        L.mask(m, ramp=IRON_DARK, level=-1.0)
        L.set(6, 7, 9, 9, ramp=ORANGE, level=-2.0)
    return L


def kiln_side():
    L = plate(363, rivets=None)
    kiln_band(L)
    L.set(0, 7, 16, 9, ramp=IRON_DARK, level=-1.5)          # the band's shadow behind it
    return L


def kiln_top():
    L = plate(365, rivets=None)
    kiln_band(L)
    ring_round(L, rect(5, 5, 11, 11), IRON_DARK, 0.0)
    vent_shade(L, slots(5, 11, (6, 8, 10)))
    return L


def mixer_front(lit):
    """A big window onto the mixing tank, its paddle seen through the glass."""
    L = plate(371)
    m = hole('mixer', 'front')
    frame(L, m)

    def behind(L):
        for i in range(8):                                   # the paddle's arm, across
            L.px(4 + i, 11 - i // 2, ramp=STEEL_DARK, level=-0.5)
        L.set(7, 5, 9, 12, ramp=STEEL_DARK, level=-1.0)      # its shaft
    glass(L, m, lit, behind)
    return L


def mixer_top():
    """A dark steel ring round the tank's lid, set in a little."""
    L = collar_top(375, 'mixer', STEEL_DARK, STEEL_DARK)
    m = hole('mixer', 'top')
    L.mask(m, level=0.0)
    L.set(6, 6, 10, 7, level=-1.0)
    return L


def forming_front(lit):
    """The forming rollers' chamber (rollers and sheet are the shape's); lamps beside it."""
    L = plate(381)
    m = hole('forming_line', 'front')
    frame(L, m)
    dark(L, m, -1.5)
    light(L, 1, 6, ORANGE, lit)
    light(L, 1, 9, CYAN, lit)
    light(L, 14, 9, CYAN, lit)
    return L


def sterilizer_front(lit):
    """A seam across under the two gauges (the shape's), a lamp."""
    L = plate(391, hseams=(8,))
    light(L, 12, 9, CYAN, lit)
    return L


def baking_front(lit):
    """The oven's dark window over the glowing heating slot."""
    L = plate(401)
    win, heat = hole('baking_line', 'front', 0), hole('baking_line', 'front', 1)
    frame(L, win)
    frame(L, heat)
    L.mask(win, ramp=CHOCO, level=-1.5)
    L.set(4, 3, 7, 4, ramp=GLASS, level=-1.0)
    if lit:
        L.mask(heat, ramp=ORANGE, level=0.5)
        L.set(3, 8, 13, 9, level=1.5)
    else:
        L.mask(heat, ramp=ORANGE, level=-2.0)
        L.set(3, 8, 13, 9, ramp=IRON_DARK, level=-1.0)
    return L


def fryer_front(lit):
    """A display and a glass panel of readings, keys under the display (the knob is the shape's)."""
    L = plate(411)
    a, b = hole('cooker_fryer', 'front', 0), hole('cooker_fryer', 'front', 1)
    frame(L, a)
    frame(L, b)
    screen(L, 2, 2, 10, 9, lit, 412, dots=12)
    L.mask(b, ramp=GLASS_LIT if lit else GLASS, level=-0.5)
    for x, y in ((12, 3), (11, 5), (12, 6)):
        L.px(x, y, ramp=CYAN, level=1.0 if lit else -1.5)
    L.set(2, 10, 10, 13, ramp=STEEL_DARK, level=-0.5)
    for x in (3, 5, 7):
        L.set(x, 11, x + 1, 12, ramp=IRON_DARK, level=-1.0)
    light(L, 8, 11, CYAN, lit)
    return L


def vent_top(seed, rows, x0=2, x1=14):
    L = plate(seed)
    vent_shade(L, slots(x0, x1, rows))
    ring_round(L, rect(x0, rows[0], x1, rows[-1] + 1), STEEL_DARK, 0.0)
    return L


def cellar_front():
    """The cold room's door: its edge a seam (hinges, handle and gauge are the shape's)."""
    L = plate(421)
    door = rect(2, 1, 13, 15)
    L.mask(door & ~rect(3, 2, 12, 14), add=-1.0)
    return L


def cutting_front():
    """The blades behind their slots, the chute below where the cut pieces come out."""
    L = plate(431)
    ring_round(L, rect(3, 2, 13, 7), STEEL_DARK, -0.5)
    L.set(3, 2, 13, 7, ramp=STEEL, level=1.0)
    for c in (4, 6, 8, 10, 12):
        L.set(c, 2, c + 1, 7, ramp=IRON_DARK, level=-1.5)
    m = hole('cutting_station', 'front', 5)
    frame(L, m)
    dark(L, m, -1.0)
    L.set(4, 12, 11, 14, ramp=STEEL_DARK, level=-0.5)
    return L


def confection_front(lit):
    """The window onto the chocolate (the shape's heap), a dial and a keypad beside it."""
    L = plate(441)
    m = hole('confectionery_line', 'front')
    frame(L, m)
    L.mask(m, ramp=IRON_DARK, level=-1.5)
    L.set(11, 9, 15, 13, ramp=STEEL_DARK, level=-0.5)
    for x, y in ((12, 10), (13, 10), (12, 11), (13, 11)):
        L.px(x, y, ramp=STEEL, level=0.5 if (x + y) % 2 else -0.5)
    return L


def confection_top():
    """A dark lid set into the top, a slot for a handle in it."""
    L = plate(445)
    m = hole('confectionery_line', 'top')
    frame(L, m)
    L.mask(m, ramp=IRON_DARK, level=-0.5)
    L.set(7, 5, 10, 6, level=-2.0)
    return L


def freezer_front(lit):
    """A grille of dark slats across the panel, a display at its top right, frost round the plate."""
    L = plate(451)
    frosted(L, 452)
    m = hole('freezing_unit', 'front')
    frame(L, m)
    L.mask(m, ramp=IRON_DARK, level=-1.0)
    for y in range(3, 13, 2):
        L.set(3, y, 13, y + 1, ramp=STEEL_DARK, level=-0.5)
    L.set(8, 3, 13, 6, ramp=CYAN if lit else GLASS, level=0.5 if lit else -1.0)
    return L


def freezer_side(seed=455):
    L = plate(seed)
    frosted(L, seed + 1)
    return L


def retort_front():
    """A dark panel set in two units, its pipes and gauge the shape's."""
    L = plate(461)
    m = hole('filling_retort_line', 'front')
    frame(L, m)
    L.mask(m, ramp=IRON_DARK, level=-1.0)
    return L


def dark_inner():
    L = Layer(IRON_DARK, -1.0)
    L.vgrad(0, 0, 16, 16, -0.5, 0.5)
    return L


def both(painter):
    return {'front': lambda: painter(False), 'front_active': lambda: painter(True)}


def same(painter):
    return {'front': painter, 'front_active': painter}


DRAWN = {
    'crusher': {**same(crusher_front), 'side': lambda: plate(303, (8,), (8,)), 'top': crusher_top, 'inner': dark_inner},
    'solid_liquid_separator': {**both(separator_front), 'side': separator_side, 'back': lambda: plate(317),
                               'top': lambda: plate(319, hseams=(2,)), 'inner': dark_inner},
    'stirred_reactor': {**both(reactor_front), 'side': lambda: plate(323), 'top': lambda: collar_top(325, 'stirred_reactor', BRASS),
                        'inner': dark_inner, 'collar': collar_inner(BRASS)},
    'evaporating_crystallizer': {**both(crystallizer_front), 'side': lambda: plate(333), 'top': crystallizer_top,
                                 'inner': dark_inner, 'collar': collar_inner(GLASS)},
    'dryer': {**both(dryer_front), 'side': dryer_side, 'back': lambda: dryer_side(False, 347), 'top': dryer_top,
              'inner': dark_inner},
    'absorption_tower': {**both(tower_front), 'side': lambda: plate(353, rivets='edges'),
                         'top': lambda: plate(355, hseams=(9,), rivets='edges'), 'inner': dark_inner},
    'rotary_kiln': {**both(kiln_front), 'side': kiln_side, 'top': kiln_top, 'inner': dark_inner},
    'mixer': {**both(mixer_front), 'side': lambda: plate(373), 'top': mixer_top, 'inner': dark_inner},
    'forming_line': {**both(forming_front), 'side': lambda: plate(383, (8,)), 'top': lambda: plate(385, hseams=(3,)),
                     'inner': dark_inner},
    'homogenizing_sterilizer': {**both(sterilizer_front), 'side': lambda: plate(393, (8,)),
                                'top': lambda: plate(395, hseams=(3,))},
    'baking_line': {**both(baking_front), 'side': lambda: plate(403, hseams=(8,)), 'top': lambda: plate(405),
                    'inner': dark_inner},
    'cooker_fryer': {**both(fryer_front), 'side': lambda: plate(413, (8,)),
                     'top': lambda: vent_top(415, (3, 5, 7, 9, 11, 13)), 'inner': dark_inner},
    'aging_cellar': {**same(cellar_front), 'side': lambda: plate(423, (8,)), 'top': lambda: plate(425)},
    'cutting_station': {**same(cutting_front), 'side': lambda: plate(433), 'top': lambda: vent_top(435, (4, 6, 8, 10), 3, 13),
                        'inner': dark_inner},
    'confectionery_line': {**both(confection_front), 'side': lambda: plate(443, (8,)), 'top': confection_top,
                           'inner': dark_inner},
    'freezing_unit': {**both(freezer_front), 'side': freezer_side, 'top': lambda: freezer_side(457),
                      'inner': dark_inner},
    'filling_retort_line': {**same(retort_front), 'side': lambda: plate(463, (8,)), 'top': lambda: plate(465),
                            'inner': dark_inner},
}
