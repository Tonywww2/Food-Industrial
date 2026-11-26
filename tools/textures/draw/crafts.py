"""The workshop's and the first food line's crafted machines (CONVENTIONS.md §11, layouts after the reference sheets):
clay, plaster, crates and barrels, copper. The fermentation vat, the koji room, the crystallizing tank, the noodle
press, the mixing churn, the meat grinder, the ice-salt freezer, the filling sealer, the pot still and the seawater
pump.

OPENINGS are shared with the shapes (tools/models/machines.py) as in kilns.py; the parts standing out of the faces
(knobs, discs, the churn's paddle, nozzles, collars) are the shapes' cubes, drawn by part_faces.py.
"""
import numpy as np

from kilns import octagon, rect
from pixels import (BAND, BRASS, BRINE, COPPER, ICE, IRON_DARK, PLASTER, SALT, SNOW, STEEL, STONE, TERRA, WOOD,
                    WOOD_DARK, Grain, Layer, edge, round_mask, staves, strap, upper_left)
from masonry import blocks
from woodwork import banded, barrel_side, stave_ends

ROUND12 = (4, 8, 10, 10, 12, 12, 12, 12, 10, 10, 8, 4)


def round12(x0=2, y0=2):
    return round_mask(x0, y0, ROUND12)


OPENINGS = {
    'crystallizing_tank': {'top': [(rect(3, 3, 13, 13), 4)]},
    'noodle_press': {'front': [(rect(3, 3, 13, 11), 5)], 'top': [(rect(7, 7, 9, 9), 2)]},
    'mixing_churn': {'top': [(round12(), 3)]},
    'meat_grinder': {'top': [(rect(3, 3, 13, 13), 6)]},
    'ice_salt_freezer': {'top': [(round12(), 4)]},
    'filling_sealer': {'front': [(rect(3, 3, 13, 14), 5)], 'top': [(octagon(6, 6, 10, 10), 2)]},
    'pot_still': {'top': [(octagon(5, 5, 11, 11, 2), 3)]},
    'seawater_pump': {'top': [(octagon(5, 5, 11, 11, 2), 3)], 'west': [(rect(6, 6, 10, 10), 3)]},
    'koji_room': {'front': [(rect(5, 3, 11, 14), 1)]},
}


def ring_round(L, mask, ramp, level, width=1):
    """A raised rim round a hole: lit at the top left, turned away at the bottom right."""
    near = mask.copy()
    for _ in range(width):
        p = np.pad(near, 1)
        near = near | p[:-2, 1:-1] | p[2:, 1:-1] | p[1:-1, :-2] | p[1:-1, 2:]
    rim = near & ~mask
    yy, xx = np.mgrid[0:16, 0:16]
    ys, xs = np.nonzero(mask)
    cx, cy = (xs.min() + xs.max()) / 2, (ys.min() + ys.max()) / 2
    L.mask(rim, ramp=ramp, level=level)
    L.mask(rim & ((xx - cx) + (yy - cy) < -1), add=0.5)
    L.mask(rim & ((xx - cx) + (yy - cy) > 1), add=-0.5)


def deep(L, mask, ramp, level=-1.0):
    """The bottom of a hole as the top sees it: dark, darkest under its top and left lips."""
    L.mask(mask, ramp=ramp, level=level)
    ys, xs = np.nonzero(mask)
    yy, xx = np.mgrid[0:16, 0:16]
    L.mask(mask & ((yy == ys.min()) | (xx == xs.min())), add=-0.5)


# --- fermentation vat -----------------------------------------------------------------------------------------------

def vat_side(seed=101):
    """Red clay, smooth, two dark iron bands round it; the shoulder at the top lit (the lid sits on it), the foot
    darker. The body stands 15 high, so its sides show rows 1 to 15."""
    L = Layer(TERRA, TERRA.flat)
    L.vgrad(0, 0, 16, 16, 0.5, -0.5)
    g = Grain(seed)
    for _ in range(10):
        x, y = g.next(16), 1 + g.next(15)
        L.px(x, y, add=-0.5 if g.next(2) else 0.5)
    L.add(0, 1, 16, 2, 0.5)
    for i, y in enumerate((3, 11)):
        strap(L, 0, y, 16, y + 2, seed + i, BAND)
        L.add(0, y + 2, 16, y + 3, -1.0)
    L.add(0, 15, 16, 16, -0.5)
    return L


def vat_top():
    """The clay shoulder round the lid; the lid (the shape's raised cube shows it): boards across under a batten,
    nailed."""
    L = Layer(TERRA, TERRA.flat + 0.5)
    L.add(0, 0, 16, 1, 0.5)
    L.add(0, 1, 1, 16, 0.5)
    L.add(0, 15, 16, 16, -0.5)
    L.add(15, 0, 16, 15, -0.5)
    staves(L, 2, 2, 14, 14, (3, 3, 3, 3), False, 103, ramp=WOOD)
    L.set(7, 2, 9, 14, ramp=WOOD, level=WOOD.flat - 0.5)
    L.set(7, 2, 8, 14, level=WOOD.flat)
    for y in (3, 6, 9, 12):
        L.px(7, y, ramp=BAND, level=1.5)
    L.mask(edge(rect(2, 2, 14, 14)) & upper_left(edge(rect(2, 2, 14, 14))), add=0.5)
    L.mask(edge(rect(2, 2, 14, 14)) & ~upper_left(edge(rect(2, 2, 14, 14))), add=-0.5)
    return L


# --- koji room ------------------------------------------------------------------------------------------------------

def frame_panel(L, ramp, frame_w=2):
    """A wooden frame round a panel: posts and rails two pixels, each a board lit on its first line; the panel in
    the frame's shadow under the top rail and right of the left post."""
    staves(L, 0, 0, 16, frame_w, (frame_w,), False, 111, ramp=WOOD)
    staves(L, 0, 16 - frame_w, 16, 16, (frame_w,), False, 112, ramp=WOOD)
    staves(L, 0, frame_w, frame_w, 16 - frame_w, (frame_w,), True, 113, ramp=WOOD)
    staves(L, 16 - frame_w, frame_w, 16, 16 - frame_w, (frame_w,), True, 114, ramp=WOOD)
    L.add(frame_w, frame_w, 16 - frame_w, frame_w + 1, -1.0)
    L.add(frame_w, frame_w + 1, frame_w + 1, 16 - frame_w, -0.5)


def plaster(seed):
    L = Layer(PLASTER, PLASTER.flat)
    g = Grain(seed)
    for _ in range(14):
        x, y = g.next(16), g.next(16)
        L.px(x, y, add=-0.5 if g.next(3) else 0.5)
        if g.next(2):
            L.px(x + 1, y, add=-0.5)
    return L


def koji_side(seed=121):
    """Plaster panels in a wooden frame."""
    L = plaster(seed)
    frame_panel(L, PLASTER)
    return L


def koji_front():
    """The door, set back a pixel (the shape carves it): boards, a slatted vent at the top, two battens; the knob
    and the thermometer by the door are the shape's cubes."""
    L = koji_side(125)
    d = OPENINGS['koji_room']['front'][0][0]
    staves(L, 5, 3, 11, 14, (2, 2, 2), True, 126, ramp=WOOD, specks=1)
    for x in (6, 8, 10):                                   # the vent's slots between slats
        L.set(x, 4, x + 1, 7, ramp=WOOD_DARK, level=-1.0)
    for y in (8, 12):                                      # battens across the boards, a shadow under the upper
        L.set(5, y, 11, y + 1, ramp=WOOD, level=WOOD.flat + 0.5)
    L.set(5, 9, 11, 10, ramp=WOOD, level=WOOD.flat - 1.0)
    L.mask(edge(d) & ~d, level=PLASTER.flat - 1.0)
    return L


# --- crystallizing tank ---------------------------------------------------------------------------------------------

def crust(L, y, seed, depth=3):
    """Salt crusted along a row and dripping down in places."""
    g = Grain(seed)
    for x in range(16):
        n = 1 + g.next(depth)
        for k in range(n):
            L.px(x, y + k, ramp=SALT, level=SALT.flat + 0.5 - 0.5 * k)


def tank_side(seed=131):
    """Grey stones in a wooden frame, salt crusted along the top of the stones."""
    L = Layer(STONE, STONE.flat)
    blocks(L, 2, 2, 14, 8, STONE, (7,), seed)
    blocks(L, 2, 8, 14, 14, STONE, (4, 10), seed + 1)
    crust(L, 2, seed)
    frame_panel(L, STONE)
    return L


def tank_top():
    """A wooden rim, a crust of salt on the basin's edge, the brine in the basin with salt crystals forming."""
    L = Layer(SALT, SALT.flat)
    staves(L, 0, 0, 16, 1, (1,), False, 135, ramp=WOOD)
    staves(L, 0, 15, 16, 16, (1,), False, 136, ramp=WOOD)
    staves(L, 0, 1, 1, 15, (1,), True, 137, ramp=WOOD)
    staves(L, 15, 1, 16, 15, (1,), True, 138, ramp=WOOD)
    g = Grain(139)
    for _ in range(12):
        L.px(1 + g.next(14), 1 + g.next(14), add=0.5 if g.next(2) else -0.5)
    m = OPENINGS['crystallizing_tank']['top'][0][0]
    L.mask(m, ramp=BRINE, level=0.0)
    L.set(3, 3, 13, 4, level=-1.0)
    L.set(3, 4, 4, 13, level=-0.5)
    for x, y in ((6, 7), (9, 6), (11, 10), (5, 11), (8, 9), (10, 12)):
        L.px(x, y, ramp=SALT, level=SALT.flat + 1.0)
        L.px(x + 1, y, ramp=SALT, level=SALT.flat)
    L.set(3, 12, 13, 13, ramp=SALT, level=SALT.flat)
    return L


def tank_inner():
    """The basin's walls: stone, salt crusted at the top and along the old water lines."""
    L = Layer(STONE, STONE.flat - 1.0)
    blocks(L, 0, 0, 16, 8, STONE, (7,), 141, level=STONE.flat - 1.0)
    blocks(L, 0, 8, 16, 16, STONE, (3, 11), 142, level=STONE.flat - 1.0)
    crust(L, 0, 143)
    for x in range(0, 16, 3):
        L.px(x, 7, ramp=SALT, level=SALT.flat - 0.5)
    return L


# --- crates: noodle press, meat grinder, filling sealer -------------------------------------------------------------

def crate(seed, vertical=True, ramp=BAND):
    return banded(seed, lambda L: staves(L, 2, 2, 14, 14, (4, 4, 4), vertical, seed), ramp)


def window_frame(L, m, ramp, level=0.5):
    """An iron frame one pixel round an opening, lit at its top left."""
    ring_round(L, m, ramp, level)


def noodle_front():
    """The window onto the sheets of dough (the shape's cubes), in a dark iron frame; the chamber behind dark."""
    L = crate(151)
    m = OPENINGS['noodle_press']['front'][0][0]
    window_frame(L, m, BAND)
    L.mask(m, ramp=WOOD_DARK, level=-0.5)
    L.set(3, 3, 13, 4, level=-1.5)
    return L


def noodle_top():
    """Boards across, an iron hatch in the middle with the hopper's mouth."""
    L = crate(155, vertical=False)
    L.set(5, 5, 11, 11, ramp=BAND, level=0.5)
    L.bevel(5, 5, 11, 11, 1.0, -1.0, inner=0)
    deep(L, OPENINGS['noodle_press']['top'][0][0], WOOD_DARK)
    return L


def dark_inner():
    """The inside of a crate or a hopper: dark boards."""
    L = Layer(WOOD_DARK, WOOD_DARK.flat)
    staves(L, 0, 0, 16, 16, (4, 4, 4, 4), True, 161, ramp=WOOD_DARK, level=WOOD_DARK.flat)
    L.vgrad(0, 0, 16, 16, -0.5, 0.25)
    return L


def grinder_top():
    """The hopper's mouth in a steel-banded top."""
    L = crate(171, vertical=False, ramp=STEEL)
    m = OPENINGS['meat_grinder']['top'][0][0]
    deep(L, m, WOOD_DARK, -1.5)
    L.set(6, 6, 10, 10, ramp=STEEL, level=-1.0)               # the auger's end, far down
    L.set(7, 7, 9, 9, level=-0.5)
    return L


def sealer_front():
    """The filling chamber, dark, in a dark iron frame (its nozzle and tray are the shape's cubes)."""
    L = crate(181)
    m = OPENINGS['filling_sealer']['front'][0][0]
    window_frame(L, m, BAND)
    L.mask(m, ramp=WOOD_DARK, level=-1.0)
    return L


def sealer_top():
    """A dark iron ring round the inlet in the middle of the top."""
    L = crate(185, vertical=False)
    m = OPENINGS['filling_sealer']['top'][0][0]
    ring_round(L, m, BAND, 0.5, width=2)
    deep(L, m, WOOD_DARK, -1.5)
    return L


# --- barrels: mixing churn, ice-salt freezer ------------------------------------------------------------------------

def churn_top():
    """The staves' ends round the churn's mouth, a dark iron ring round it; inside, the cream far down (the paddle
    is the shape's)."""
    L = stave_ends()
    m = round12()
    ring = edge(m)
    L.mask(m, ramp=WOOD_DARK, level=-1.0)
    L.mask(ring, ramp=BAND, level=0.0)
    L.mask(ring & upper_left(ring), add=1.0)
    L.mask(ring & ~upper_left(ring), add=-1.0)
    L.mask(m & ~ring, ramp=WOOD_DARK, level=-0.5)
    return L


def churn_inner():
    L = dark_inner()
    L.set(0, 0, 16, 1, ramp=BAND, level=0.0)
    return L


def frost(L, seed, rows=3):
    """Snow along the top, dripping down the staves; frost on them here and there."""
    g = Grain(seed)
    for x in range(16):
        n = rows + g.next(3) - (1 if x % 4 == 3 else 0)
        for y in range(n):
            L.px(x, y, ramp=SNOW, level=SNOW.flat + 0.5 - 0.5 * (y == n - 1))
    for _ in range(7):                                   # rime on the staves, thinning out downwards
        x, y = g.next(16), rows + 1 + g.next(6)
        if L.ramp[y, x] is WOOD:
            L.px(x, y, ramp=SNOW, level=SNOW.flat - 1.0)


def freezer_side(seed=191):
    L = barrel_side(seed, hoops=(4, 11))
    frost(L, seed)
    return L


def freezer_top():
    """Snow and ice round the steel bowl set into the barrel's top; the bowl's steel rim lit."""
    L = Layer(SNOW, SNOW.flat)
    g = Grain(195)
    for _ in range(26):
        L.px(g.next(16), g.next(16), ramp=ICE, level=g.next(2) * 0.5)
    m = round12()
    ring_round(L, m, STEEL, 0.5)
    L.mask(m, ramp=STEEL, level=-0.5)
    yy, xx = np.mgrid[0:16, 0:16]
    L.mask(m & (abs((xx - yy)) <= 1) & (xx > 5) & (xx < 11), add=1.0)   # the bowl's shine across its bottom
    L.set(4, 2, 12, 3, ramp=STEEL, level=-1.5)
    return L


def steel_inner():
    """A steel bowl's or vessel's wall: lit at the rim, darker down."""
    L = Layer(STEEL, 0.0)
    L.vgrad(0, 0, 16, 16, 0.5, -1.0)
    return L


# --- copper: pot still, seawater pump -------------------------------------------------------------------------------

def copper_panel(seed, trim=BRASS, seam=True, trim_w=2):
    """Copper sheet in a trim round its edges, riveted along the trim and down a seam in the middle; a soft sheen
    across it."""
    L = Layer(COPPER, COPPER.flat)
    for k in range(-16, 16, 9):                                   # the sheen
        for i in range(16):
            for w in range(2):
                L.px(i + k + w, 15 - i, add=0.5)
    L.vgrad(0, 0, 16, 16, 0.25, -0.25)
    if seam:
        L.set(8, 0, 9, 16, level=COPPER.flat - 1.5)
        L.set(9, 0, 10, 16, level=COPPER.flat + 0.5)
        for y in (3, 7, 11):
            L.px(7, y, level=COPPER.flat - 1.5)
            L.px(10, y, level=COPPER.flat - 1.5)
    t = trim_w
    for box in ((0, 0, 16, t), (0, 16 - t, 16, 16), (0, t, t, 16 - t), (16 - t, t, 16, 16 - t)):
        L.set(*box, ramp=trim, level=0.0)
    L.bevel(0, 0, 16, 16, 1.0, -1.0, inner=0.5 if t > 1 else 0)
    for i in range(2, 15, 3):
        for x, y in ((i, 0 if t == 1 else 1), (0 if t == 1 else 1, i), (i, 15 if t == 1 else 14), (15 if t == 1 else 14, i)):
            L.px(x, y, level=-1.5 if trim is BRASS else 1.5)
    L.add(t, t, 16 - t, t + 1, -0.5)
    return L


def still_top():
    """Copper in its brass trim, a brass collar round the neck in the middle."""
    L = copper_panel(205, seam=False)
    m = OPENINGS['pot_still']['top'][0][0]
    ring_round(L, m, BRASS, 0.5, width=2)
    deep(L, m, COPPER, -2.0)
    return L


def still_inner():
    """The neck: brass at its mouth, dark copper down."""
    L = Layer(COPPER, -1.0)
    L.vgrad(0, 0, 16, 16, 0.0, -1.0)
    L.set(0, 0, 16, 2, ramp=BRASS, level=0.0)
    L.set(0, 1, 16, 2, level=-1.0)
    return L


def pump_side(seed=211):
    return copper_panel(seed, trim=IRON_DARK, trim_w=1)


def pump_top():
    L = copper_panel(215, trim=IRON_DARK, seam=False, trim_w=1)
    m = OPENINGS['seawater_pump']['top'][0][0]
    ring_round(L, m, COPPER, 1.0, width=2)
    deep(L, m, IRON_DARK, -1.5)
    return L


def pump_flank():
    """The intake side: the pipe's mouth (its collar is the shape's), dark inside."""
    L = pump_side(219)
    m = OPENINGS['seawater_pump']['west'][0][0]
    deep(L, m, IRON_DARK, -2.0)
    return L


def pipe_inner():
    L = Layer(IRON_DARK, -1.0)
    L.vgrad(0, 0, 16, 16, 0.0, -1.0)
    return L


DRAWN = {
    'fermentation_vat': {'front': lambda: vat_side(105), 'front_active': lambda: vat_side(105), 'side': vat_side,
                         'top': vat_top},
    'koji_room': {'front': koji_front, 'front_active': koji_front, 'side': koji_side,
                  'top': lambda: koji_side(129), 'inner': lambda: plaster(127)},
    'crystallizing_tank': {'front': lambda: tank_side(133), 'front_active': lambda: tank_side(133), 'side': tank_side,
                           'top': tank_top, 'inner': tank_inner},
    'noodle_press': {'front': noodle_front, 'front_active': noodle_front, 'side': lambda: crate(153),
                     'top': noodle_top, 'inner': dark_inner},
    'mixing_churn': {'front': lambda: barrel_side(165), 'front_active': lambda: barrel_side(165),
                     'side': lambda: barrel_side(163), 'top': churn_top, 'inner': churn_inner},
    'meat_grinder': {'front': lambda: crate(175, False, STEEL), 'front_active': lambda: crate(175, False, STEEL),
                     'side': lambda: crate(173, False, STEEL), 'top': grinder_top, 'inner': dark_inner},
    'ice_salt_freezer': {'front': lambda: freezer_side(193), 'front_active': lambda: freezer_side(193),
                         'side': freezer_side, 'top': freezer_top, 'inner': steel_inner},
    'filling_sealer': {'front': sealer_front, 'front_active': sealer_front, 'side': lambda: crate(183),
                       'top': sealer_top, 'inner': dark_inner},
    'pot_still': {'front': lambda: copper_panel(201), 'front_active': lambda: copper_panel(201),
                  'side': lambda: copper_panel(203), 'top': still_top, 'inner': still_inner},
    'seawater_pump': {'front': lambda: pump_side(213), 'front_active': lambda: pump_side(213), 'side': pump_side,
                      'flank': pump_flank, 'top': pump_top, 'inner': pipe_inner},
}
