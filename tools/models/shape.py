"""Machine shape models (user 2026-11-25: the machines the reference pack draws in three dimensions are modelled in
Blockbench through its MCP server; models follow the Blockbench Minecraft style guide).

A shape is a list of elements in model units (a block is 16 × 16 × 16; north is the machine's front). Each face names a
texture variable (front, side, top, or one of the machine's own: inner, parts) and is mapped with Minecraft's own UV
convention unless a rectangle is given, so a face shows the part of the texture that lies where the face lies and the
pixel density stays one texture pixel per unit on every face (Minecraft's 16x standard: 16 × 16 textures, elements
on whole units).

  north: u = 16 - x, v = 16 - y      south: u = x, v = 16 - y
  west:  u = z,      v = 16 - y      east:  u = 16 - z, v = 16 - y
  up:    u = x,      v = z           down:  u = x, v = 16 - z
"""
import json

FACES = ('north', 'east', 'south', 'west', 'up', 'down')


def auto_uv(face, frm, to):
    x1, y1, z1 = frm
    x2, y2, z2 = to
    return {
        'north': [16 - x2, 16 - y2, 16 - x1, 16 - y1],
        'south': [x1, 16 - y2, x2, 16 - y1],
        'west': [z1, 16 - y2, z2, 16 - y1],
        'east': [16 - z2, 16 - y2, 16 - z1, 16 - y1],
        'up': [x1, z1, x2, z2],
        'down': [x1, 16 - z2, x2, 16 - z1],
    }[face]


def element(name, frm, to, faces, rotation=None, origin=None):
    """faces: {face: texture or (texture, uv) or None}; '*' sets every face not named, 'sides' the four sides."""
    out = {}
    default = faces.get('*')
    sides = faces.get('sides')
    for f in FACES:
        spec = faces.get(f, sides if f in ('north', 'east', 'south', 'west') and sides is not None else default)
        if spec is None:
            continue
        if isinstance(spec, tuple):
            tex, uv = spec
        else:
            tex, uv = spec, None
        out[f] = {'texture': tex, 'uv': uv or auto_uv(f, frm, to)}
    for a, b in zip(frm, to):
        if b - a < 1 or a != int(a) or b != int(b):
            raise ValueError(f'{name}: off the 16 grid (16x: whole units, one pixel each; style guide: no mixels)')
    e = {'name': name, 'from': list(frm), 'to': list(to), 'faces': out}
    turned = [a for a in (rotation or []) if a]
    if len(turned) > 1 or any(a not in (-45, -22.5, 22.5, 45) for a in turned):
        raise ValueError(f'{name}: Minecraft turns an element about one axis by -45, -22.5, 22.5 or 45 degrees only')
    if rotation:  # Blockbench's [x, y, z] degrees about origin; Minecraft allows one axis, a multiple of 22.5
        e['rotation'] = rotation
        e['origin'] = origin
    return e


def parent_model(exported, mid, own):
    """The exported Java block model as the machine's shape model (models/block/<id>_shape.json): front, side and
    top stay variables (the machine's block models fill them in, front_active for a running machine); the machine's
    own textures (own: parts, inner, flank) are fixed; faces on the block's boundary get their cullface; Blockbench's
    zero rotations and its file notes are dropped."""
    model = json.loads(exported) if isinstance(exported, str) else exported
    names = {}
    textures = {'particle': '#side'}
    for key, ref in model.get('textures', {}).items():
        if key == 'particle':
            continue
        base = ref.split('/')[-1].split(':')[-1]
        if base in own:
            textures[base] = f'foodindustrial:block/{mid}_{base}'
        elif base not in ('front', 'side', 'top'):
            raise ValueError(f'{mid}: unknown texture {ref}')
        names['#' + key] = '#' + base
    bounds = {'north': (2, 0), 'south': (2, 16), 'west': (0, 0), 'east': (0, 16), 'down': (1, 0), 'up': (1, 16)}
    elements = []
    for e in model['elements']:
        rotation = e.get('rotation')
        if rotation and not rotation.get('angle'):
            rotation = None
        out = {'name': e['name'], 'from': e['from'], 'to': e['to']}
        if rotation:
            out['rotation'] = rotation
        faces = {}
        for f, face in e['faces'].items():
            face = dict(face, texture=names[face['texture']])
            axis, edge = bounds[f]
            coord = e['from'][axis] if edge == 0 else e['to'][axis]
            if coord == edge and not rotation:
                face['cullface'] = f
            faces[f] = face
        out['faces'] = faces
        elements.append(out)
    return {'parent': 'minecraft:block/block', 'textures': textures, 'elements': elements}
