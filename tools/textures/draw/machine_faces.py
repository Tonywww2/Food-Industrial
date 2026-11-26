"""The machines' block textures, drawn (pixels.py). Layouts follow the reference sheets; what a machine's model
builds from cubes (taps, gauges, handwheels, the press's screw and trough) is not drawn on its faces.

Faces are painted in light levels (pixels.Layer): a face is lit from the top left and turns darker towards the
bottom right, raised edges turn over two pixels, frames and hoops cast a short shadow, recesses are darkest under
their upper and left lips.

DRAWN: {machine id: {texture suffix: painter}}; a painter returns a Canvas. Faces a shape model uses besides
front, front_active, side and top: inner (the walls of a recess), flank (the other face the sheet shows).
"""
import crafts
import kilns
import labworks
import steelworks
from pixels import CYAN, GEL, GLASS, GLASS_LIT, STEEL, STEEL_DARK, WHITE, Grain, Layer, disc, edge, stud, upper_left
from woodwork import barrel_side, barrel_top, press_front, press_inner, press_side, press_top

CORNERS = ((2, 2), (13, 2), (2, 13), (13, 13))


# --- steel ----------------------------------------------------------------------------------------------------------

def steel_panel(seams=(), seed=71):
    """A steel plate, lighter at the top; a bevel that turns over two pixels, rivets at the corners, two faint
    brushed lines along it; seams with a lit and a shaded side, riveted."""
    L = Layer(STEEL)
    L.vgrad(0, 0, 16, 16, 0.35, -0.35)
    g = Grain(seed)
    for i in range(2):
        y, x = 4 + g.next(8), 2 + g.next(4)
        L.add(x, y, x + 6 + g.next(5), y + 1, 0.5)
    for x in seams:
        L.add(x, 2, x + 1, 14, -1.25)
        L.add(x + 1, 2, x + 2, 14, 0.75)
        for y in (4, 11):
            stud(L, x - 1, y, STEEL)
    L.bevel(0, 0, 16, 16, 1.25, -1.0, inner=0.5)
    for x, y in CORNERS:
        stud(L, x, y, STEEL)
    return L


def tank_top():
    """A raised round hatch in the middle of the plate, its lid set in, a handle across it."""
    L = steel_panel(seed=73)
    hatch = disc(8, 8, 5.2)
    L.mask(hatch, ramp=STEEL_DARK, level=0.75)
    ring = edge(hatch)
    L.mask(ring & upper_left(ring), level=1.75)
    L.mask(ring & ~upper_left(ring), level=-1.25)
    L.add(13, 5, 14, 12, -0.5)                      # the hatch's shadow on the plate
    L.add(5, 13, 12, 14, -0.5)
    lid = disc(8, 8, 3.4)
    L.mask(lid, level=0.0)
    rim = edge(lid)
    L.mask(rim & upper_left(rim), level=-1.25)
    L.mask(rim & ~upper_left(rim), level=0.75)
    L.set(6, 7, 10, 9, ramp=STEEL, level=0.0)       # the handle
    L.add(6, 7, 10, 8, 1.0)
    L.add(6, 8, 10, 9, -0.75)
    return L


# --- white cabinet --------------------------------------------------------------------------------------------------

def white_panel():
    """Enamelled white, lighter at the top, a bevel turning over two pixels, a screw in each corner."""
    L = Layer(WHITE)
    L.vgrad(0, 0, 16, 16, 0.5, -0.25)
    L.bevel(0, 0, 16, 16, 1.5, -1.25, inner=0.5)
    for x, y in CORNERS:
        L.px(x, y, level=-1.25)
        L.px(x + 1, y + 1, add=-0.5)
        L.px(x - 1, y - 1, add=0.5)
    return L


def cabinet_top():
    """The cabinet's top: a vent of five slats in an inset frame (as the sheet's other views draw it)."""
    L = white_panel()
    L.add(3, 3, 13, 4, -1.0)                        # the inset, in shadow under its upper and left edges
    L.add(3, 4, 4, 13, -0.75)
    L.add(4, 12, 13, 13, 0.5)
    L.add(12, 4, 13, 12, 0.25)
    for y in (5, 7, 9, 11):
        L.add(4, y, 12, y + 1, -1.25)               # the gap between two slats
        L.add(4, y - 1, 12, y, 0.75)                # the slat's lit edge above it
    return L


# a microscope seen from the side on the bench: eyepiece, tube, objective, stage, arm and foot (light level)
SCOPE = {(6, 5): -1.0, (6, 6): -2.0, (6, 7): -2.0, (7, 7): -1.5, (7, 8): -1.5, (9, 6): -1.5, (9, 7): -2.0, (9, 8): -2.0,
         (5, 9): -1.0, (6, 9): -1.5, (7, 9): -1.5, (8, 9): -1.5, (9, 9): -2.0, (9, 10): -2.0, (6, 11): -1.5,
         (7, 11): -2.0, (8, 11): -2.0, (9, 11): -2.0, (10, 11): -2.0}
DISHES = (4, 9)


def cabinet_window(scene, lit):
    """The cabinet's face: a light strip under its top edge (glowing on the enamel below it while the machine
    works), a window in a recessed grey bezel, a lab scene behind the glass and reflections on it (the model sets
    the window back by one pixel; the frame round it is the face's own)."""
    L = white_panel()
    L.set(1, 1, 15, 3, ramp=CYAN, level=1.5 if lit else 0.0)
    L.add(1, 2, 15, 3, -0.75)
    for x in (1, 14):
        L.px(x, 1, add=-0.5)
        L.px(x, 2, add=-0.5)
    if lit:
        L.set(2, 3, 14, 4, ramp=CYAN, level=2.0)
    L.set(2, 4, 14, 14, ramp=WHITE, level=-1.0)     # the bezel, in shadow under its top and left
    L.add(2, 4, 14, 5, -0.75)
    L.add(2, 5, 3, 14, -0.5)
    L.add(3, 13, 14, 14, 0.75)
    L.add(13, 5, 14, 13, 0.5)
    glass = GLASS_LIT if lit else GLASS
    L.set(3, 5, 13, 13, ramp=glass, level=0.0)
    L.vgrad(3, 5, 13, 13, 0.75, -0.5)
    L.add(3, 5, 13, 6, -1.0)                        # the bezel's shadow on the glass
    L.add(3, 6, 4, 13, -0.75)
    if scene == 'microscope':
        for (x, y), level in SCOPE.items():
            L.px(x, y, level=level)
    else:  # petri dishes, cultures growing in them
        for x0 in DISHES:
            L.set(x0 + 1, 10, x0 + 3, 11, level=1.75)
            L.px(x0, 11, level=1.25)
            L.px(x0 + 3, 11, level=1.25)
            L.set(x0 + 1, 11, x0 + 3, 12, ramp=GEL, level=0.0)
            L.px(x0 + 1, 11, add=1.0)
            L.px(x0 + 2, 11, add=-1.0)
            L.set(x0, 12, x0 + 4, 13, level=-0.75)
    L.set(3, 12, 13, 13, level=-1.0)                # the bench top
    for x, y, level in ((12, 6, 2.0), (11, 7, 2.0), (10, 8, 1.5), (12, 8, 1.5), (11, 9, 1.0)):  # reflections
        L.px(x, y, ramp=glass, level=level)
    return L


def cabinet_inner():
    """The window's reveal: enamel in shadow, deepest at the top."""
    L = Layer(WHITE, -0.75)
    L.vgrad(0, 0, 16, 16, -0.5, 0.25)
    return L


def canvas(painter):
    return lambda: painter().render()


DRAWN = {
    'screw_press': {'front': press_front, 'front_active': press_front, 'side': press_side, 'top': press_top,
                    'inner': press_inner},
    'steeping_vat': {'front': lambda: barrel_side(52), 'front_active': lambda: barrel_side(52), 'side': barrel_side,
                     'top': barrel_top},
    'extraction_tank': {'front': steel_panel, 'front_active': steel_panel, 'side': lambda: steel_panel((5, 10)),
                        'top': tank_top},
    'culture_bench': {'front': lambda: cabinet_window('microscope', False), 'front_active': lambda: cabinet_window('microscope', True),
                      'flank': lambda: cabinet_window('dishes', False), 'side': white_panel, 'top': cabinet_top,
                      'inner': cabinet_inner},
}
DRAWN.update(kilns.DRAWN)
DRAWN.update(crafts.DRAWN)
DRAWN.update(steelworks.DRAWN)
DRAWN.update(labworks.DRAWN)

def files():
    """{file name: painter} of every drawn texture; a painter returns a Canvas."""
    return {f'{mid}_{name}.png': canvas(painter) for mid, faces in DRAWN.items() for name, painter in faces.items()}
