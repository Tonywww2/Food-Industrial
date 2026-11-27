"""The third and fourth tiers' machines (CONVENTIONS.md §11, layouts after the reference sheets): the distillation
column, the high-pressure reactor, the electrolyzer, the adsorption column, the electric arc furnace, the gas-phase
reactor, the spray dryer, the sterile fermenter and the membrane separator.

Chemical plant: slate-blue panels in a polished steel frame with heavier corner brackets (refractory bricks inside
the frame for the two furnaces); the biotech machines in white enamel as the approved culture bench's. Openings are
shared with the shapes as in kilns.py.
"""
import numpy as np

from crafts import ring_round
from kilns import arch, octagon, rect
from masonry import courses
from pixels import (ARC, BRASS, COPPER, CYAN, GLASS, GLASS_LIT, IRON_DARK, ORANGE, SAND, SLATE, STEEL, STEEL_DARK, WHITE,
                    Grain, Layer, stud)
from steelworks import dark, frame, glass, light, screen

OPENINGS = {
    'distillation_column': {},
    'high_pressure_reactor': {},
    'electrolyzer': {'top': [(rect(3, 6, 6, 9), 2), (rect(10, 6, 13, 9), 2)]},
    'adsorption_column': {'front': [(rect(3, 2, 13, 13), 1)], 'top': [(octagon(5, 5, 11, 11, 2), 3)]},
    'electric_arc_furnace': {'front': [(arch(5, 11, 3, 13, 2), 2)],
                             'top': [(rect(6, 5, 8, 7), 2), (rect(9, 9, 11, 11), 2)]},
    'gas_phase_reactor': {'front': [(octagon(3, 4, 9, 10, 2), 1)], 'top': [(octagon(4, 4, 12, 12, 2), 3)]},
    'spray_dryer': {'front': [(rect(5, 7, 11, 13), 1), (rect(6, 8, 10, 13), 2), (rect(7, 9, 9, 13), 3)],
                    'top': [(rect(7, 6, 9, 8), 3)]},
    'sterile_fermenter': {'front': [(rect(9, 2, 12, 11), 1)], 'west': [(rect(6, 3, 9, 12), 1)],
                          'east': [(rect(7, 3, 10, 12), 1)]},
    'membrane_separator': {'front': [(rect(2, 4, 9, 12), 3)]},
}


def hole(mid, face, i=0):
    return OPENINGS[mid][face][i][0]


def framed(seed, inside=None):
    """A slate panel (or what inside(L) draws) in a polished steel frame: a bright outer pixel, a greyer inner one,
    the panel's corners cut on the diagonal where the frame's brackets reach in (two steps, as the sheets draw them;
    user 2026-11-26: the earlier square brackets made the frame read too thick); the frame's shadow on the panel
    under its top and left; rivets in the panel's corners."""
    L = Layer(SLATE, SLATE.flat)
    L.vgrad(0, 0, 16, 16, 0.25, -0.25)
    if inside:
        inside(L)
    yy, xx = np.mgrid[0:16, 0:16]
    inner = (xx >= 2) & (xx < 14) & (yy >= 2) & (yy < 14)
    for cx, cy in ((2, 2), (13, 2), (2, 13), (13, 13)):
        inner &= (abs(xx - cx) + abs(yy - cy)) >= 2
    L.mask(~inner, ramp=STEEL_DARK, level=0.5)
    L.mask(~inner & ((xx == 0) | (yy == 0) | (xx == 15) | (yy == 15)), ramp=STEEL, level=0.5)
    L.bevel(0, 0, 16, 16, 0.75, -0.75, inner=0)
    L.mask(inner & np.roll(~inner, 1, axis=0), add=-0.5)           # the frame's shadow on the panel
    L.mask(inner & np.roll(~inner, 1, axis=1), add=-0.5)
    for x, y in ((4, 4), (11, 4), (4, 11), (11, 11)):
        if L.ramp[y, x] is SLATE:
            stud(L, x, y, SLATE)
    return L


def bricks_inside(seed):
    def paint(L):
        courses(L, 0, 0, 16, 16, SAND, SAND, height=4, length=8, seed=seed, tone=(0.0, 0.0, -0.5, 0.5), specks=4)
        for y in range(16):
            for x in range(16):
                if L.ramp[y, x] is SAND and (y % 4 == 3 or ((x + (y // 4 % 2) * 4) % 8 == 7)):
                    L.px(x, y, level=SAND.flat - 1.5)
    return paint


def distillation_front(lit):
    """A display of the column's readings, keys under it (the brass knob is the shape's)."""
    L = framed(501)
    screen(L, 3, 2, 13, 8, lit, 502, dots=12)
    for x in (4, 6):
        L.set(x, 10, x + 1, 11, ramp=IRON_DARK, level=-1.0)
    light(L, 8, 10, CYAN, lit)
    return L


def distillation_top():
    L = framed(505)
    L.set(8, 2, 9, 14, level=SLATE.flat - 1.5)
    L.set(9, 2, 10, 14, level=SLATE.flat + 0.5)
    return L


def reactor_front(lit):
    """Two lamps under the big gauge (the gauge is the shape's)."""
    L = framed(511)
    light(L, 5, 12, CYAN, lit)
    light(L, 9, 12, CYAN, lit)
    return L


def reactor_top():
    """A heavy brass hatch, bolted round, a darker boss in the middle."""
    L = framed(515)
    m = octagon(3, 3, 13, 13, 3)
    L.mask(m, ramp=BRASS, level=0.0)
    ring_round(L, octagon(4, 4, 12, 12, 3), BRASS, 0.0)
    yy, xx = np.mgrid[0:16, 0:16]
    L.mask(m & ((xx - 8) + (yy - 8) < -5), add=0.5)
    L.mask(m & ((xx - 8) + (yy - 8) > 4), add=-0.5)
    for x, y in ((7, 4), (11, 7), (8, 11), (4, 8), (5, 5), (10, 5), (10, 10), (5, 10)):
        L.px(x, y, ramp=BRASS, level=-1.5)
    L.set(6, 6, 10, 10, ramp=BRASS, level=-1.0)
    L.set(7, 7, 9, 9, level=-0.5)
    return L


def electrolyzer_side():
    """A copper band round the cell between two dark seals, bolted."""
    L = framed(521)
    L.set(2, 5, 14, 6, ramp=IRON_DARK, level=-1.0)
    L.set(2, 10, 14, 11, ramp=IRON_DARK, level=-1.5)
    L.set(2, 6, 14, 10, ramp=COPPER, level=COPPER.flat)           # inside the frame, as the sheet draws it
    L.set(2, 6, 14, 7, level=COPPER.flat + 1.0)
    L.set(2, 9, 14, 10, level=COPPER.flat - 1.0)
    for x in (2, 13):
        for y in (7, 8):
            L.px(x, y, level=COPPER.flat - 2.0)
    return L


def electrolyzer_top():
    """The two electrodes' ports, dark."""
    L = framed(525)
    for i in range(2):
        m = hole('electrolyzer', 'top', i)
        ring_round(L, m, STEEL_DARK, 0.0)
        dark(L, m, -1.5)
    return L


def adsorption_front(lit):
    """A window onto the column's bed of black carbon granules."""
    L = framed(531)
    m = hole('adsorption_column', 'front')
    frame(L, m, STEEL, 0.5)
    g = Grain(532)

    def behind(L):
        ys, xs = np.nonzero(m)
        for y, x in zip(ys, xs):
            if y >= 4 and (x + y) > 7:
                L.px(x, y, ramp=IRON_DARK, level=-2.0 if g.next(3) else -0.5)
    glass(L, m, lit, behind)
    return L


def adsorption_top():
    L = framed(535)
    m = hole('adsorption_column', 'top')
    ring_round(L, m, STEEL, 0.5, width=2)
    dark(L, m, -2.0)
    return L


def arc_front(lit):
    """Refractory bricks in the frame; the arched window onto the arc, blinding white-cyan while it burns."""
    L = framed(541, bricks_inside(542))
    m = hole('electric_arc_furnace', 'front')
    ring_round(L, m, SLATE, -0.5)
    if lit:
        L.mask(m, ramp=ARC, level=1.0)
        L.set(7, 6, 9, 11, level=2.0)
        ys, xs = np.nonzero(m)
        L.mask(m & ((np.mgrid[0:16, 0:16][1] == xs.min()) | (np.mgrid[0:16, 0:16][1] == xs.max())), add=-1.0)
    else:
        L.mask(m, ramp=IRON_DARK, level=-1.5)
    return L


def arc_top():
    """A slate plate over the bricks with the two electrodes' ports."""
    L = framed(545, bricks_inside(546))
    L.set(4, 3, 13, 13, ramp=SLATE, level=SLATE.flat)
    ring_round(L, rect(4, 3, 13, 13), SLATE, 0.0)
    for i in range(2):
        m = hole('electric_arc_furnace', 'top', i)
        dark(L, m, -2.0)
    return L


def gas_front(lit):
    """Refractory bricks in the frame; the round window onto the burning gas, orange while it burns (the two small
    gauges are the shape's)."""
    L = framed(551, bricks_inside(552))
    m = hole('gas_phase_reactor', 'front')
    ring_round(L, m, SLATE, -0.5)
    if lit:
        L.mask(m, ramp=ORANGE, level=0.5)
        L.set(5, 6, 7, 8, level=2.0)
    else:
        L.mask(m, ramp=IRON_DARK, level=-1.5)
    return L


def gas_top():
    L = framed(555, bricks_inside(556))
    m = hole('gas_phase_reactor', 'top')
    ring_round(L, m, SLATE, 0.0, width=2)
    dark(L, m, -2.0)
    return L


def spray_front(lit):
    """A cyan display over the cone of the drying chamber's outlet (cut in steps)."""
    L = framed(561)
    screen(L, 4, 2, 12, 6, lit, 562, dots=6)
    if lit:
        L.set(5, 3, 11, 5, ramp=CYAN, level=0.0)
        L.set(5, 3, 9, 4, level=1.0)
    for i, lv in ((0, -0.5), (1, -1.0), (2, -1.5)):
        L.mask(hole('spray_dryer', 'front', i), ramp=STEEL_DARK, level=lv)
    ring_round(L, hole('spray_dryer', 'front', 0), SLATE, 0.0)
    return L


def spray_top():
    L = framed(565)
    m = hole('spray_dryer', 'top')
    ring_round(L, m, SLATE, 0.0, width=2)
    dark(L, m, -2.0)
    return L


def enamel(seed):
    """White enamel, riveted at the corners, its edge a single pixel lit at the top left (as the culture bench's)."""
    L = Layer(WHITE, 0.0)
    L.vgrad(0, 0, 16, 16, 0.35, -0.35)
    L.bevel(0, 0, 16, 16, 1.0, -1.0, inner=0)
    for x, y in ((2, 2), (13, 2), (2, 13), (13, 13)):
        stud(L, x, y, WHITE, level=-0.5)
    return L


def tube_strip(L, m, lit):
    """A glass strip with cyan readings in it."""
    frame(L, m, WHITE, -0.5)
    L.mask(m, ramp=GLASS_LIT if lit else GLASS, level=-0.5)
    ys, xs = np.nonzero(m)
    for y in range(ys.min() + 1, ys.max(), 2):
        L.px(xs.min() + 1, y, ramp=CYAN, level=1.0 if lit else -1.0)


def fermenter_front(lit):
    """A display of the culture's readings, keys under it, a glass strip of readings beside it."""
    L = enamel(571)
    screen(L, 1, 2, 8, 8, lit, 572, dots=8)
    L.set(1, 8, 8, 10, ramp=WHITE, level=-1.0)
    for x in (2, 4):
        L.px(x, 9, ramp=IRON_DARK, level=-1.0)
    light(L, 6, 9, CYAN, lit)
    tube_strip(L, hole('sterile_fermenter', 'front'), lit)
    return L


def fermenter_side(flank=False):
    L = enamel(575)
    tube_strip(L, rect(6, 3, 9, 12) if flank else rect(7, 3, 10, 12), False)
    return L


def membrane_front(lit):
    """The rack of membrane tubes (the shape's), lamps under the gauge."""
    L = enamel(581)
    m = hole('membrane_separator', 'front')
    frame(L, m, WHITE, -0.5)
    L.mask(m, ramp=STEEL_DARK, level=-1.0)
    light(L, 11, 9, CYAN, lit)
    light(L, 11, 11, CYAN, lit, size=2)
    return L


def slate_inner():
    L = Layer(SLATE, -1.0)
    L.vgrad(0, 0, 16, 16, -0.5, 0.5)
    return L


def both(painter):
    return {'front': lambda: painter(False), 'front_active': lambda: painter(True)}


DRAWN = {
    'distillation_column': {**both(distillation_front), 'side': lambda: framed(503), 'top': distillation_top},
    'high_pressure_reactor': {**both(reactor_front), 'side': lambda: framed(513), 'top': reactor_top},
    'electrolyzer': {'front': electrolyzer_side, 'front_active': electrolyzer_side, 'side': electrolyzer_side,
                     'top': electrolyzer_top, 'inner': slate_inner},
    'adsorption_column': {**both(adsorption_front), 'side': lambda: framed(533), 'top': adsorption_top,
                          'inner': slate_inner},
    'electric_arc_furnace': {**both(arc_front), 'side': lambda: framed(543, bricks_inside(544)), 'top': arc_top,
                             'inner': slate_inner},
    'gas_phase_reactor': {**both(gas_front), 'side': lambda: framed(553, bricks_inside(554)), 'top': gas_top,
                          'inner': slate_inner},
    'spray_dryer': {**both(spray_front), 'side': lambda: framed(563), 'top': spray_top, 'inner': slate_inner},
    'sterile_fermenter': {**both(fermenter_front), 'side': fermenter_side, 'flank': lambda: fermenter_side(True),
                          'back': lambda: enamel(577), 'top': lambda: enamel(579), 'inner': slate_inner},
    'membrane_separator': {**both(membrane_front), 'side': lambda: enamel(583), 'top': lambda: enamel(585),
                           'inner': slate_inner},
}
