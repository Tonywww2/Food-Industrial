"""Shapes of the machines the reference pack draws in three dimensions (user 2026-11-25), north = front.

The aim is to stay as close to the reference sheets as possible (user 2026-11-25): the block keeps the sheet's cube
outline and its textures (drawn by script, tools/textures/draw), and what the sheet draws as an opening (a press
chamber, a window) is set back into the block. What the sheet draws standing out of a face (taps, handwheels,
gauges, spouts, the press screw) is built from a few cubes (user 2026-11-25: such parts look poor as texture), placed
where the sheet draws them and painted in the same materials as the textures; the drawn faces leave them out.
Minecraft's 16x standard (user 2026-11-25): one texture pixel to a unit, elements on whole units, the smallest one
unit across.

Face specs: a texture variable ('front', 'side', 'top', or the machine's own 'inner', 'flank') maps with Minecraft's
UV convention (shape.auto_uv); a Part (a painter of tools/textures/draw/part_faces.py) draws the face into the
machine's parts texture.
Positions: a point the sheet's front face shows at (u, v) units is x = 16 - u, y = 16 - v on the block.
"""
import pathlib
import sys

import numpy as np

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
sys.path.insert(0, str(HERE.parent / 'textures' / 'draw'))
import crafts  # noqa: E402
import kilns  # noqa: E402
import labworks  # noqa: E402
import machine_faces  # noqa: E402
import steelworks  # noqa: E402
import part_faces as pf  # noqa: E402
import parts  # noqa: E402
import shape  # noqa: E402

ROOT = HERE.parents[1]
BLOCK = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures' / 'block'


class Part:
    """A part face drawn by a painter of part_faces at the face's size, with the painter's own arguments."""

    def __init__(self, painter, **kw):
        self.painter, self.kw = painter, kw

    def tile(self, w, h, face):
        return pf.tile(self.painter(w, h, face, **self.kw))


SCREW, PLATEN, BED, BOARDS, WET = (Part(f) for f in (pf.screw, pf.platen, pf.bed, pf.boards, pf.wet))
DOUGH, FLOURED, ROLLER, THERMO, STEEL, COPPER = (Part(f) for f in (pf.dough, pf.floured, pf.roller, pf.thermometer,
                                                               pf.steel, pf.copper))
IRON = Part(pf.wheel)
LAMPS, CHOCOLATE, ENAMEL = Part(pf.lamps), Part(pf.chocolate), Part(pf.enamel)
INDICATOR_ORANGE, INDICATOR_RED = Part(pf.indicator), Part(pf.indicator, ramp=pf.COPPER)
MEMBRANE, MINIGAUGE = Part(pf.membrane), Part(pf.minigauge)
BRASS, OPENING = Part(pf.brass), Part(pf.opening)
BEZEL, NEEDLE, HUB = Part(pf.bezel), Part(pf.needle), Part(pf.hub)


def face_size(face, frm, to):
    dx, dy, dz = (b - a for a, b in zip(frm, to))
    return {'north': (dx, dy), 'south': (dx, dy), 'east': (dz, dy), 'west': (dz, dy), 'up': (dx, dz), 'down': (dx, dz)}[face]


class Builder:
    def __init__(self, mid):
        self.mid = mid
        self.atlas = parts.Atlas()
        self.elements = []
        self.uses = set()

    def add(self, name, frm, to, faces, rotation=None, origin=None):
        expanded = dict(faces)
        for f in shape.FACES:
            if f in expanded:
                continue
            if f in ('north', 'east', 'south', 'west') and 'sides' in expanded:
                expanded[f] = expanded['sides']
            elif '*' in expanded:
                expanded[f] = expanded['*']
        resolved = {}
        for f in shape.FACES:
            spec = expanded.get(f)
            if spec is None:
                continue
            if isinstance(spec, Part):
                w, h = face_size(f, frm, to)
                tile = spec.tile(int(round(w * parts.PX)), int(round(h * parts.PX)), f)
                resolved[f] = ('parts', self.atlas.add(tile))
                self.uses.add('parts')
            else:
                resolved[f] = spec
                self.uses.add(spec[0] if isinstance(spec, tuple) else spec)
        self.elements.append(shape.element(name, frm, to, resolved, rotation, origin))


# --- the body, carved -----------------------------------------------------------------------------------------------

NORMALS = {'north': (2, -1), 'south': (2, 1), 'west': (0, -1), 'east': (0, 1), 'down': (1, -1), 'up': (1, 1)}
BLOCK_FACES = {'north': 'front', 'south': 'side', 'west': 'side', 'east': 'side', 'up': 'top', 'down': 'side'}


def carve(b, cuts, faces=None, name='body', inner='inner'):
    """The block's body: the full cube less the recesses, as boxes found by a greedy pass.

    cuts: [(frm, to, face[, walls])], boxes cut out of the cube, each open towards one block face (a firebox, a
    window, a hole in the top; an arch or an octagon is several boxes). A box face on the block's boundary shows that
    face's texture (faces, default front, side, top); one looking into a recess shows, when it faces the way the
    recess opens (its back wall), the opening face's texture where it lies, so the front texture draws what lies at
    the back of a firebox or behind a window and front_active swaps it; the recess's other walls show its walls
    texture (default inner)."""
    faces = dict(BLOCK_FACES, **(faces or {}))
    solid = np.ones((16, 16, 16), bool)
    for frm, to, *_ in cuts:
        solid[frm[0]:to[0], frm[1]:to[1], frm[2]:to[2]] = False
    used = ~solid
    boxes = []
    for z in range(16):
        for y in range(16):
            for x in range(16):
                if used[x, y, z]:
                    continue
                x1 = x
                while x1 < 16 and not used[x1, y, z]:
                    x1 += 1
                y1 = y + 1
                while y1 < 16 and not used[x:x1, y1, z].any():
                    y1 += 1
                z1 = z + 1
                while z1 < 16 and not used[x:x1, y:y1, z1].any():
                    z1 += 1
                used[x:x1, y:y1, z:z1] = True
                boxes.append(((x, y, z), (x1, y1, z1)))

    def opening_of(point):
        for frm, to, face, *walls in cuts:
            if all(a <= p < c for a, p, c in zip(frm, point, to)):
                return face, (walls[0] if walls else inner)
        return None, inner

    for i, (frm, to) in enumerate(boxes):
        spec = {}
        for face, (axis, sign) in NORMALS.items():
            plane = to[axis] if sign > 0 else frm[axis] - 1
            if plane in (16, -1):
                spec[face] = faces[face]
                continue
            sl = [slice(frm[k], to[k]) for k in range(3)]
            sl[axis] = slice(plane, plane + 1)
            beyond = solid[tuple(sl)]
            if beyond.all():
                continue
            idx = np.argwhere(~beyond)[0]
            point = [sl[k].start + idx[k] for k in range(3)]
            opens, walls = opening_of(point)
            spec[face] = faces[face] if opens == face else walls
        b.add(f'{name}{i}', frm, to, spec)


def mask_rects(mask):
    """A mask's rows merged into rectangles (x0, y0, x1, y1), texture coordinates; one run of pixels to a row."""
    rects = []
    for y in range(16):
        xs = np.nonzero(mask[y])[0]
        if not len(xs):
            continue
        span = (int(xs.min()), int(xs.max()) + 1)
        if rects and rects[-1][3] == y and (rects[-1][0], rects[-1][2]) == span:
            rects[-1][3] = y + 1
        else:
            rects.append([span[0], y, span[1], y + 1])
    return rects


def opening_cuts(openings):
    """carve's cuts for the openings a face painter draws: {face: [(mask, depth[, walls])]}, the masks in the
    face's texture coordinates (front: north; west, east, south: the sides and back; top)."""
    cuts = []
    for face, items in openings.items():
        for mask, depth, *walls in items:
            for u0, v0, u1, v1 in mask_rects(mask):
                frm, to, opens = {
                    'front': ((16 - u1, 16 - v1, 0), (16 - u0, 16 - v0, depth), 'north'),
                    'top': ((u0, 16 - depth, v0), (u1, 16, v1), 'up'),
                    'west': ((0, 16 - v1, u0), (depth, 16 - v0, u1), 'west'),
                    'east': ((16 - depth, 16 - v1, 16 - u1), (16, 16 - v0, 16 - u0), 'east'),
                    'south': ((u0, 16 - v1, 16 - depth), (u1, 16 - v0, 16), 'south'),
                }[face]
                cuts.append((frm, to, opens, *walls))
    return cuts


# --- parts built from cubes -----------------------------------------------------------------------------------------

def crossed(b, name, cx, cy, n, tile, rim, z0=-1, z1=0):
    """A round plate n units across standing out of the front from z0 to z1, centred at (cx, cy) (half units when n
    is odd, so its edges lie on whole units): crossed cubes make its octagon, one for each width its rows take
    (widest first), their fronts showing one picture (tile, n × n) at the same place, so they agree where they
    overlap; rim paints their edges."""
    rect = b.atlas.add(tile)
    b.uses.add('parts')
    k = (rect[2] - rect[0]) / n
    u0, v0 = 16 - (cx + n / 2), 16 - (cy + n / 2)
    on = pf.octagon_mask(n)
    widths = [int(r.sum()) for r in on]
    for i, w in enumerate(sorted(set(widths), reverse=True)):
        rows = [r for r, x in enumerate(widths) if x >= w]
        hw, hh = w / 2, (rows[-1] - rows[0] + 1) / 2
        frm, to = (cx - hw, cy - hh, z0), (cx + hw, cy + hh, z1)
        u1, v1, u2, v2 = shape.auto_uv('north', frm, to)
        uv = [rect[0] + (u1 - u0) * k, rect[1] + (v1 - v0) * k, rect[0] + (u2 - u0) * k, rect[1] + (v2 - v0) * k]
        b.add(f'{name}_{"abc"[i]}', frm, to, {'north': ('parts', uv), 'east': rim, 'west': rim, 'up': rim, 'down': rim})


def gauge(b, name, cx, cy, angle, n=5, z=-1):
    """A round gauge n units across, its case one unit deep from z (the front of whatever it stands on, -1 on the
    block's front), centred at (cx, cy): the case (crossed cubes, the dial on their fronts), a rotated 1 × 2 cube for
    the needle in front of it (longer on a bigger gauge)."""
    crossed(b, name, cx, cy, n, pf.dial(n).a, BEZEL, z, z + 1)
    if n % 2:
        frm, to = (cx - 0.5, cy - 0.5, z - 1), (cx + 0.5, cy + (1.5 if n <= 5 else 2.5), z)
    else:                                        # an even dial's centre is a corner: the needle starts from it
        frm, to = (cx, cy, z - 1), (cx + 1, cy + n // 2 - 1, z)
    b.add(f'{name}_needle', frm, to,
          {'north': NEEDLE, 'east': NEEDLE, 'west': NEEDLE, 'up': NEEDLE, 'down': NEEDLE},
          rotation=[0, 0, angle], origin=[cx, cy, z - 0.5])


def handwheel(b, name, x0, y0):
    """A handwheel 7 units across (lower left corner at x0, y0), one unit thick, standing on a stem one unit out of
    the front: a ring of eight cubes drawn as a pixel circle, two spokes, a hub in front. The rim's pieces at the
    wheel's upper left (as the front shows it: larger x, larger y) are painted lighter, those at its lower right
    darker, so the ring reads round."""
    rim = (((2, 0), (5, 1)), ((2, 6), (5, 7)), ((0, 2), (1, 5)), ((6, 2), (7, 5)),
           ((1, 1), (2, 2)), ((5, 1), (6, 2)), ((1, 5), (2, 6)), ((5, 5), (6, 6)))
    for i, ((x1, y1), (x2, y2)) in enumerate(rim):
        lean = (x1 + x2) / 2 - 3.5 + (y1 + y2) / 2 - 3.5
        iron = Part(pf.wheel, level=0.5 if lean > 1 else (-0.5 if lean < -1 else 0.0))
        b.add(f'{name}_rim{i}', (x0 + x1, y0 + y1, -2), (x0 + x2, y0 + y2, -1),
              {'north': iron, 'east': iron, 'west': iron, 'up': iron, 'down': iron})
    iron = Part(pf.wheel)
    faces = {'north': iron, 'east': iron, 'west': iron, 'up': iron, 'down': iron}
    b.add(f'{name}_spoke_h', (x0 + 1, y0 + 3, -2), (x0 + 6, y0 + 4, -1), faces)
    b.add(f'{name}_spoke_v', (x0 + 3, y0 + 1, -2), (x0 + 4, y0 + 6, -1), faces)
    b.add(f'{name}_hub', (x0 + 3, y0 + 3, -3), (x0 + 4, y0 + 4, -2), {'north': HUB, 'east': HUB, 'west': HUB, 'up': HUB, 'down': HUB})
    b.add(f'{name}_stem', (x0 + 3, y0 + 3, -1), (x0 + 4, y0 + 4, 0), {'east': iron, 'west': iron, 'up': iron, 'down': iron})


# --- shapes ---------------------------------------------------------------------------------------------------------

def screw_press(b):
    """The sheet's open front frame (3 units) round a dark chamber 6 deep. Inside, as the sheet draws it: the screw,
    striped by its thread, comes down from the top beam through the iron plate to the wooden bed, whose top has a
    juice groove; a wooden trough stands out of the frame's foot."""
    inner = 'inner'
    b.add('frame_east', (13, 0, 0), (16, 16, 6), {'north': 'front', 'east': 'side', 'west': inner, 'up': 'top', 'down': 'side'})
    b.add('frame_west', (0, 0, 0), (3, 16, 6), {'north': 'front', 'west': 'side', 'east': inner, 'up': 'top', 'down': 'side'})
    b.add('frame_top', (3, 13, 0), (13, 16, 6), {'north': 'front', 'up': 'top', 'down': inner})
    b.add('frame_bottom', (3, 0, 0), (13, 3, 6), {'north': 'front', 'up': inner, 'down': 'side'})
    b.add('body', (0, 0, 6), (16, 16, 16), {'north': inner, 'east': 'side', 'west': 'side', 'south': 'side', 'up': 'top', 'down': 'side'})
    b.add('bed', (3, 3, 1), (13, 5, 6), {'north': BED, 'up': BED})
    b.add('screw', (7, 5, 2), (9, 13, 4), {'sides': SCREW})
    b.add('plate', (4, 7, 1), (12, 9, 5), {'sides': PLATEN, 'up': PLATEN, 'down': PLATEN})
    b.add('trough', (6, 1, -2), (10, 2, 0), {'north': BOARDS, 'east': BOARDS, 'west': BOARDS, 'up': WET, 'down': BOARDS})
    b.add('trough_east', (9, 2, -2), (10, 3, 0), {'north': BOARDS, 'east': BOARDS, 'west': BOARDS, 'up': BOARDS})
    b.add('trough_west', (6, 2, -2), (7, 3, 0), {'north': BOARDS, 'east': BOARDS, 'west': BOARDS, 'up': BOARDS})


def steeping_vat(b):
    """The sheet's barrel cube as it is, with a brass tap low on its front, over the lower hoop: a pipe out of the
    barrel, its spout turning down at the end, the key standing on it."""
    b.add('vat', (0, 0, 0), (16, 16, 16), {'north': 'front', 'sides': 'side', 'up': 'top', 'down': 'side'})
    b.add('tap_pipe', (7, 4, -3), (9, 6, 0), {'north': BRASS, 'east': BRASS, 'west': BRASS, 'up': BRASS, 'down': BRASS})
    b.add('tap_spout', (7, 2, -3), (9, 4, -2), {'north': BRASS, 'east': BRASS, 'west': BRASS, 'south': BRASS, 'down': OPENING})
    b.add('tap_key', (6, 6, -2), (10, 8, -1), {'north': BRASS, 'east': BRASS, 'west': BRASS, 'south': BRASS, 'up': BRASS,
                                                'down': BRASS})


# the masonry machines (tools/textures/draw/kilns.py draws their faces and holds their openings)

def stone_mill(b):
    """The sheet's millstone on its stone base: the flour spout's opening low on the front with a wooden lip
    standing out under it, the eye in the top."""
    carve(b, opening_cuts(kilns.OPENINGS['stone_mill']))
    b.add('lip', (6, 0, -2), (12, 1, 0), {'north': BOARDS, 'east': BOARDS, 'west': BOARDS, 'up': BOARDS, 'down': BOARDS})


def masonry(mid):
    """A masonry machine as the sheet draws it: a block with its firebox (and the openings of its top) carved in."""
    def shape(b):
        carve(b, opening_cuts(kilns.OPENINGS[mid]))
    return shape


def drying_room(b):
    """Bricks; the firebox low on the front; on the top a wooden grate whose slats (cubes) lie across the flue."""
    carve(b, opening_cuts(kilns.OPENINGS['drying_room']))
    for i, z in enumerate(kilns.GRATE_SLATS):
        b.add(f'slat{i}', (4, 15, z), (13, 16, z + 1), {'north': BOARDS, 'south': BOARDS, 'up': BOARDS})


# the crafted machines (tools/textures/draw/crafts.py draws their faces and holds their openings)

def cut(mid):
    return opening_cuts(crafts.OPENINGS[mid])


def sides(paint, *also):
    return {f: paint for f in ('north', 'east', 'south', 'west', 'up', 'down') if f not in also}


def fermentation_vat(b):
    """The sheet's clay vat with its wooden lid sitting on the shoulder, a unit proud of it."""
    b.add('vat', (0, 0, 0), (16, 15, 16), {'north': 'front', 'sides': 'side', 'up': 'top', 'down': 'side'})
    b.add('lid', (2, 15, 2), (14, 16, 14), {'up': 'top', 'sides': BOARDS})


def koji_room(b):
    """The plastered cabinet: its door set back a unit, the brass knob on it, the thermometer beside it."""
    carve(b, cut('koji_room'))
    b.add('knob', (6, 6, 0), (7, 7, 1), {'north': BRASS, 'east': BRASS, 'west': BRASS, 'up': BRASS, 'down': BRASS})
    b.add('thermometer', (3, 7, -1), (4, 12, 0), {'north': THERMO, 'east': THERMO, 'west': THERMO, 'up': THERMO,
                                                   'down': THERMO})


def noodle_press(b):
    """The crate with its window onto the chamber: sheets of dough stacked on its floor, the rolling pin over them;
    the hopper's mouth in the top."""
    carve(b, cut('noodle_press'))
    b.add('sheet0', (4, 5, 1), (12, 6, 5), {'north': FLOURED, 'up': FLOURED})
    b.add('sheet1', (4, 6, 1), (12, 7, 5), {'north': DOUGH, 'up': DOUGH})
    b.add('sheet2', (5, 7, 1), (11, 8, 4), {'north': DOUGH, 'east': DOUGH, 'west': DOUGH, 'up': DOUGH})
    b.add('roller', (3, 10, 2), (13, 11, 4), {'north': ROLLER, 'up': ROLLER, 'down': ROLLER})


def mixing_churn(b):
    """The barrel churn, open at the top: its iron paddle, two bars crossed on a shaft, just under the mouth."""
    carve(b, cut('mixing_churn'))
    b.add('paddle_x', (3, 14, 7), (13, 15, 9), {'north': IRON, 'south': IRON, 'up': IRON, 'down': IRON})
    b.add('paddle_z', (7, 14, 3), (9, 15, 13), {'east': IRON, 'west': IRON, 'up': IRON, 'down': IRON})
    b.add('shaft', (7, 15, 7), (9, 16, 9), {'sides': IRON, 'up': IRON})


def meat_grinder(b):
    """The crate with its deep hopper in the top and the grinder's pierced plate standing out of the front."""
    carve(b, cut('meat_grinder'))
    crossed(b, 'plate', 9.5, 8.5, 7, pf.perforated(7).a, STEEL)


def filling_sealer(b):
    """The crate's filling chamber: the steel filling head on its pipe from the ceiling, the tray on the floor."""
    carve(b, cut('filling_sealer'))
    b.add('pipe', (7, 10, 2), (9, 13, 4), {'sides': STEEL})
    b.add('head', (6, 9, 1), (10, 10, 5), sides(STEEL))
    b.add('tray', (4, 2, 1), (12, 3, 5), {'north': STEEL, 'east': STEEL, 'west': STEEL, 'up': STEEL})


def pot_still(b):
    """The copper still: its brass hatch standing out of the front, the neck's mouth in the top."""
    carve(b, cut('pot_still'))
    crossed(b, 'hatch', 8, 8, 8, pf.hatch(8).a, BRASS)


def seawater_pump(b):
    """The copper pump: its pressure gauge on the front, the intake's collar standing out of the west side round the
    pipe's mouth, the outlet in the top."""
    carve(b, cut('seawater_pump'), faces={'west': 'flank'})
    gauge(b, 'gauge', 9.5, 7.5, 45, n=7)
    for name, frm, to in (('collar_up', (-2, 10, 5), (0, 11, 11)), ('collar_down', (-2, 5, 5), (0, 6, 11)),
                          ('collar_north', (-2, 6, 5), (0, 10, 6)), ('collar_south', (-2, 6, 10), (0, 10, 11))):
        b.add(name, frm, to, {'west': BEZEL, 'north': BEZEL, 'south': BEZEL, 'up': BEZEL, 'down': BEZEL})


# the steel machines of the second tier (tools/textures/draw/steelworks.py draws their faces, holds their openings)

def steel_cut(mid):
    return opening_cuts(steelworks.OPENINGS[mid])


def body(b, faces=None):
    """A plain block, its faces drawn (front, side, top, side below)."""
    f = dict(BLOCK_FACES, **(faces or {}))
    b.add('body', (0, 0, 0), (16, 16, 16), {k: f[k] for k in ('north', 'east', 'south', 'west', 'up', 'down')})


def crusher(b):
    """The steel crusher: its chamber open at the front with a row of crushing teeth on its floor, the feed slot in
    the top."""
    carve(b, steel_cut('crusher'))
    for i, (x0, h) in enumerate(((3, 2), (5, 3), (7, 2), (9, 3), (11, 2))):
        b.add(f'tooth{i}', (x0, 4, 1), (x0 + 2, 4 + h, 4), sides(BEZEL, 'south', 'down'))


def stirred_reactor(b):
    """The reactor's tall window, two instrument boxes standing out beside it; in the vessel's mouth in the top, the
    stirrer's shaft and blades."""
    carve(b, steel_cut('stirred_reactor'))
    b.add('indicator_a', (3, 11, -1), (6, 14, 0), sides(INDICATOR_ORANGE, 'south'))
    b.add('indicator_b', (3, 7, -1), (6, 10, 0), sides(INDICATOR_RED, 'south'))
    b.add('stirrer', (7, 13, 7), (9, 16, 9), {'sides': IRON, 'up': IRON})
    b.add('blade_x', (4, 13, 7), (12, 14, 9), {'north': IRON, 'south': IRON, 'up': IRON})
    b.add('blade_z', (7, 13, 4), (9, 14, 12), {'east': IRON, 'west': IRON, 'up': IRON})


def evaporating_crystallizer(b):
    """The glass tube's window, a strip of lamps standing out beside it; the glass ring round the vessel's mouth."""
    carve(b, steel_cut('evaporating_crystallizer'))
    b.add('lamps', (4, 8, -1), (5, 13, 0), sides(LAMPS, 'south'))


def dryer(b):
    """The door over the front: its two hinges and its handle stand out; windows in the sides, vents cut low in the
    sides and back, the vent in the top."""
    carve(b, steel_cut('dryer'), faces={'south': 'back'})
    for i, y in enumerate((10, 3)):
        b.add(f'hinge{i}', (15, y, -1), (16, y + 3, 0), sides(BEZEL, 'south'))
    b.add('handle', (2, 6, -1), (3, 10, 0), sides(BEZEL, 'south'))


def rotary_kiln(b):
    """The kiln's drum: a band standing out round its sides, an iron rim round the fire's window in the front."""
    carve(b, steel_cut('rotary_kiln'))
    for name, frm, to, out in (('band_west', (-1, 7, 0), (0, 9, 16), 'west'), ('band_east', (16, 7, 0), (17, 9, 16), 'east'),
                               ('band_south', (0, 7, 16), (16, 9, 17), 'south')):
        b.add(name, frm, to, {out: IRON, 'up': IRON, 'down': IRON, 'north': IRON if out != 'south' else None})
    for name, frm, to in (('rim_up', (5, 12, -1), (12, 13, 0)), ('rim_down', (5, 4, -1), (12, 5, 0)),
                          ('rim_east', (4, 4, -1), (5, 13, 0)), ('rim_west', (12, 4, -1), (13, 13, 0))):
        b.add(name, frm, to, sides(IRON, 'south'))


def forming_line(b):
    """The forming chamber: a wooden roller over a steel one, the dough sheet between them coming out."""
    carve(b, steel_cut('forming_line'))
    b.add('roller_top', (4, 9, 1), (12, 11, 3), {'north': ROLLER, 'up': ROLLER, 'down': ROLLER})
    b.add('sheet', (4, 8, 0), (12, 9, 4), {'north': DOUGH, 'up': DOUGH})
    b.add('roller_bottom', (4, 6, 1), (12, 8, 3), {'north': STEEL, 'up': STEEL, 'down': STEEL})


def homogenizing_sterilizer(b):
    """A steel block with its two gauges standing out of the front."""
    body(b)
    gauge(b, 'gauge_a', 10.5, 11.5, 22.5)
    gauge(b, 'gauge_b', 4.5, 11.5, -22.5)


def cooker_fryer(b):
    """The display and glass panel set in, the control knob standing out below them, the vent cut into the top."""
    carve(b, steel_cut('cooker_fryer'))
    b.add('knob', (2, 4, -1), (4, 6, 0), sides(BEZEL, 'south'))


def aging_cellar(b):
    """The cold room's door: two hinges, a pull handle on its standoffs, a thermometer's gauge above."""
    body(b)
    for i, y in enumerate((11, 3)):
        b.add(f'hinge{i}', (14, y, -1), (15, y + 2, 0), sides(BEZEL, 'south'))
    b.add('handle_top', (5, 9, -1), (6, 10, 0), sides(BEZEL, 'south', 'north'))
    b.add('handle_foot', (5, 6, -1), (6, 7, 0), sides(BEZEL, 'south', 'north'))
    b.add('handle', (5, 6, -2), (6, 10, -1), sides(BEZEL, 'south'))
    gauge(b, 'gauge', 2.5, 12.5, 45)


def confectionery_line(b):
    """The chocolate heaped up behind the window, the dial's knob standing out beside it."""
    carve(b, steel_cut('confectionery_line'))
    b.add('heap0', (7, 4, 1), (13, 6, 3), {'north': CHOCOLATE, 'up': CHOCOLATE, 'east': CHOCOLATE, 'west': CHOCOLATE})
    b.add('heap1', (8, 6, 1), (12, 8, 3), {'north': CHOCOLATE, 'up': CHOCOLATE, 'east': CHOCOLATE, 'west': CHOCOLATE})
    b.add('heap2', (9, 8, 2), (11, 9, 3), {'north': CHOCOLATE, 'up': CHOCOLATE, 'east': CHOCOLATE, 'west': CHOCOLATE})
    crossed(b, 'knob', 4, 11, 4, pf.knob(4).a, BEZEL)


def filling_retort_line(b):
    """The filling panel set in two units: the white filling pipe with its nozzle, a second pipe, a gauge."""
    carve(b, steel_cut('filling_retort_line'))
    b.add('pipe', (10, 6, 1), (12, 14, 2), {'north': ENAMEL, 'east': ENAMEL, 'west': ENAMEL})
    b.add('nozzle', (9, 5, 0), (13, 6, 2), sides(ENAMEL, 'south'))
    b.add('pipe_b', (7, 4, 1), (8, 9, 2), {'north': ENAMEL, 'east': ENAMEL, 'west': ENAMEL, 'up': ENAMEL})
    gauge(b, 'gauge', 4.5, 11.5, -22.5, z=1)


# the third and fourth tiers (tools/textures/draw/labworks.py draws their faces and holds their openings)

def lab_cut(mid):
    return opening_cuts(labworks.OPENINGS[mid])


def distillation_column(b):
    """The column's panel with its display; the brass knob stands out under it."""
    body(b)
    crossed(b, 'knob', 5, 6, 4, pf.knob(4, pf.BRASS).a, BRASS)


def high_pressure_reactor(b):
    """The big pressure gauge standing out of the front."""
    body(b)
    gauge(b, 'gauge', 9, 10, 45, n=8)


def gas_phase_reactor(b):
    """The round window onto the flame, two small gauges standing out beside it, the reactor's mouth in the top."""
    carve(b, lab_cut('gas_phase_reactor'))
    b.add('gauge_a', (3, 11, -1), (6, 14, 0), sides(MINIGAUGE, 'south'))
    b.add('gauge_b', (3, 7, -1), (6, 10, 0), sides(MINIGAUGE, 'south'))


def membrane_separator(b):
    """The rack of three membrane tubes in their chamber, the gauge standing out above the lamps."""
    carve(b, lab_cut('membrane_separator'))
    for i, y in enumerate((10, 7, 4)):
        b.add(f'tube{i}', (7, y, 1), (14, y + 2, 3), {'north': MEMBRANE, 'up': MEMBRANE, 'down': MEMBRANE})
    gauge(b, 'gauge', 3.5, 11.5, 22.5)


def extraction_tank(b):
    """The sheet's steel cube; its two gauges and the handwheel stand out of the front."""
    b.add('tank', (0, 0, 0), (16, 16, 16), {'north': 'front', 'sides': 'side', 'up': 'top', 'down': 'side'})
    gauge(b, 'gauge_a', 11.5, 11.5, 22.5)
    gauge(b, 'gauge_b', 4.5, 11.5, -45)
    handwheel(b, 'wheel', 7, 1)


def culture_bench(b):
    """The sheet's white cabinet; the windows on the front and the west side sit one unit behind their frames (the
    sheet draws them nearly flush), the lab scene behind the glass, the cyan light strips above them as drawn."""
    inner = 'inner'
    b.add('core', (1, 0, 1), (16, 16, 16), {'north': 'front', 'west': 'flank', 'east': 'side', 'south': 'side', 'up': 'top', 'down': 'side'})
    b.add('front_foot', (0, 0, 0), (16, 2, 1), {'north': 'front', 'east': 'side', 'west': 'flank', 'up': inner, 'down': 'side'})
    b.add('front_head', (0, 12, 0), (16, 16, 1), {'north': 'front', 'east': 'side', 'west': 'flank', 'up': 'top', 'down': inner})
    b.add('front_post', (14, 2, 0), (16, 12, 1), {'north': 'front', 'east': 'side', 'west': inner})
    b.add('corner_post', (0, 2, 0), (2, 12, 1), {'north': 'front', 'west': 'flank', 'east': inner})
    b.add('west_corner', (0, 2, 1), (1, 12, 2), {'west': 'flank', 'south': inner})
    b.add('west_foot', (0, 0, 1), (1, 2, 16), {'west': 'flank', 'south': 'side', 'up': inner, 'down': 'side'})
    b.add('west_head', (0, 12, 1), (1, 16, 16), {'west': 'flank', 'south': 'side', 'up': 'top', 'down': inner})
    b.add('west_post', (0, 2, 14), (1, 12, 16), {'west': 'flank', 'south': 'side', 'north': inner})


# machine -> (shape, sets the block back)
SHAPES_BUILD = {
    'screw_press': (screw_press, True),
    'steeping_vat': (steeping_vat, False),
    'extraction_tank': (extraction_tank, False),
    'culture_bench': (culture_bench, True),
    'stone_mill': (stone_mill, True),
    'boiling_stove': (masonry('boiling_stove'), True),
    'earth_kiln': (masonry('earth_kiln'), True),
    'drying_room': (drying_room, True),
    'brick_oven': (masonry('brick_oven'), True),
    'candy_table': (masonry('candy_table'), True),
    'fermentation_vat': (fermentation_vat, False),
    'koji_room': (koji_room, True),
    'crystallizing_tank': (lambda b: carve(b, cut('crystallizing_tank')), True),
    'noodle_press': (noodle_press, True),
    'mixing_churn': (mixing_churn, True),
    'meat_grinder': (meat_grinder, True),
    'ice_salt_freezer': (lambda b: carve(b, cut('ice_salt_freezer')), True),
    'filling_sealer': (filling_sealer, True),
    'pot_still': (pot_still, True),
    'seawater_pump': (seawater_pump, True),
    'crusher': (crusher, True),
    'solid_liquid_separator': (lambda b: carve(b, steel_cut('solid_liquid_separator'), faces={'south': 'back'}), True),
    'stirred_reactor': (stirred_reactor, True),
    'evaporating_crystallizer': (evaporating_crystallizer, True),
    'dryer': (dryer, True),
    'absorption_tower': (lambda b: carve(b, steel_cut('absorption_tower')), True),
    'rotary_kiln': (rotary_kiln, True),
    'mixer': (lambda b: carve(b, steel_cut('mixer')), True),
    'forming_line': (forming_line, True),
    'homogenizing_sterilizer': (homogenizing_sterilizer, False),
    'baking_line': (lambda b: carve(b, steel_cut('baking_line')), True),
    'cooker_fryer': (cooker_fryer, True),
    'aging_cellar': (aging_cellar, False),
    'cutting_station': (lambda b: carve(b, steel_cut('cutting_station')), True),
    'confectionery_line': (confectionery_line, True),
    'freezing_unit': (lambda b: carve(b, steel_cut('freezing_unit')), True),
    'filling_retort_line': (filling_retort_line, True),
    'distillation_column': (distillation_column, False),
    'high_pressure_reactor': (high_pressure_reactor, False),
    'electrolyzer': (lambda b: carve(b, lab_cut('electrolyzer')), True),
    'adsorption_column': (lambda b: carve(b, lab_cut('adsorption_column')), True),
    'electric_arc_furnace': (lambda b: carve(b, lab_cut('electric_arc_furnace')), True),
    'gas_phase_reactor': (gas_phase_reactor, True),
    'spray_dryer': (lambda b: carve(b, lab_cut('spray_dryer')), True),
    'sterile_fermenter': (lambda b: carve(b, lab_cut('sterile_fermenter'), faces={'west': 'flank', 'south': 'back'}),
                          True),
    'membrane_separator': (membrane_separator, True),
}


def build(mid):
    fn, recessed = SHAPES_BUILD[mid]
    b = Builder(mid)
    fn(b)
    return b


def texture_files(mid, write=True):
    """{variable: png path} the shape uses; writes its parts texture. The faces are drawn by
    tools/textures/draw/run.py (inner and flank among them)."""
    b = build(mid)
    files = {}
    for var in sorted(b.uses):
        files[var] = BLOCK / f'{mid}_{var}.png'
        if var == 'parts':
            if write:
                b.atlas.save(files[var])
        elif var not in machine_faces.DRAWN[mid]:
            raise ValueError(f'{mid}: no drawn texture for {var}')
    return files


SHAPES = {mid: (lambda m=mid: build(m).elements) for mid in SHAPES_BUILD}
RECESSED = {mid for mid, (_, r) in SHAPES_BUILD.items() if r}
