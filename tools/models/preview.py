#!/usr/bin/env python3
"""Quick previews of the machine shapes without Blockbench: an orthographic z-buffer render of the elements with
their textures and Minecraft's face shading (up 1.0, north/south 0.8, east/west 0.6, down 0.5).

  python3 tools/models/preview.py out.png <machine id>...   three views per machine: front-left, front-right, back-right
"""
import pathlib
import sys

import numpy as np
from PIL import Image

HERE = pathlib.Path(__file__).resolve().parent
sys.path.insert(0, str(HERE))
import machines  # noqa: E402

SHADE = {'up': 1.0, 'down': 0.5, 'north': 0.8, 'south': 0.8, 'east': 0.6, 'west': 0.6}
VIEWS = [(-1, 0.82, -1), (1, 0.82, -1), (1, 0.82, 1)]


def corners(face, frm, to):
    """The face's corners at (s, t) = (0, 0), (1, 0), (0, 1): s along u, t along v of Minecraft's UV convention."""
    x1, y1, z1 = frm
    x2, y2, z2 = to
    return {
        'north': ((x2, y2, z1), (x1, y2, z1), (x2, y1, z1)),
        'south': ((x1, y2, z2), (x2, y2, z2), (x1, y1, z2)),
        'west': ((x1, y2, z1), (x1, y2, z2), (x1, y1, z1)),
        'east': ((x2, y2, z2), (x2, y2, z1), (x2, y1, z2)),
        'up': ((x1, y2, z1), (x2, y2, z1), (x1, y2, z2)),
        'down': ((x1, y1, z2), (x2, y1, z2), (x1, y1, z1)),
    }[face]


def rotate(points, rotation, origin):
    if not rotation:
        return np.array(points, float)
    p = np.array(points, float) - origin
    for axis, deg in enumerate(rotation):
        if not deg:
            continue
        a = np.radians(deg)
        c, s = np.cos(a), np.sin(a)
        i, j = [(1, 2), (2, 0), (0, 1)][axis]
        pi, pj = p[:, i].copy(), p[:, j].copy()
        p[:, i], p[:, j] = c * pi - s * pj, s * pi + c * pj
    return p + origin


def render(elements, textures, view, size=256):
    fwd = -np.array(view, float)
    fwd /= np.linalg.norm(fwd)
    right = np.cross(fwd, [0, 1, 0])
    right /= np.linalg.norm(right)
    up = np.cross(right, fwd)
    scale = size / 30
    centre = np.array([8, 8, 8])
    img = np.zeros((size, size, 4))
    depth = np.full((size, size), np.inf)

    def project(p):
        d = p - centre
        return np.stack([size / 2 + d @ right * scale, size / 2 - d @ up * scale, d @ fwd], axis=-1)

    for e in elements:
        for face, spec in e['faces'].items():
            tex = textures[spec['texture']]
            p = project(rotate(corners(face, e['from'], e['to']), e.get('rotation'), np.array(e.get('origin') or [8, 8, 8], float)))
            o, a, b = p[0], p[1] - p[0], p[2] - p[0]
            m = np.array([[a[0], b[0]], [a[1], b[1]]])
            if abs(np.linalg.det(m)) < 1e-6:
                continue
            inv = np.linalg.inv(m)
            q = np.array([o, o + a, o + b, o + a + b])
            x0, y0 = np.floor(q[:, :2].min(axis=0)).astype(int)
            x1, y1 = np.ceil(q[:, :2].max(axis=0)).astype(int)
            x0, y0, x1, y1 = max(0, x0), max(0, y0), min(size, x1), min(size, y1)
            if x0 >= x1 or y0 >= y1:
                continue
            yy, xx = np.mgrid[y0:y1, x0:x1] + 0.5
            st = np.einsum('ij,jyx->iyx', inv, np.stack([xx - o[0], yy - o[1]]))
            s, t = st
            inside = (s >= 0) & (s < 1) & (t >= 0) & (t < 1)
            z = o[2] + s * a[2] + t * b[2]
            u1, v1, u2, v2 = spec['uv']
            h, w = tex.shape[:2]
            tu = np.clip(((u1 + s * (u2 - u1)) * w / 16).astype(int), 0, w - 1)
            tv = np.clip(((v1 + t * (v2 - v1)) * h / 16).astype(int), 0, h - 1)
            colour = tex[tv, tu]
            ok = inside & (colour[..., 3] > 127) & (z < depth[y0:y1, x0:x1])
            shade = SHADE[face]
            region = img[y0:y1, x0:x1]
            region[ok, :3] = colour[ok, :3] * shade
            region[ok, 3] = 255
            depth[y0:y1, x0:x1][ok] = z[ok]
    return Image.fromarray(np.clip(img, 0, 255).astype(np.uint8), 'RGBA')


def views(mid, size=256):
    files = machines.texture_files(mid)
    textures = {name: np.asarray(Image.open(path).convert('RGBA')).astype(float) for name, path in files.items()}
    return [render(machines.SHAPES[mid](), textures, v, size) for v in VIEWS]


if __name__ == '__main__':
    out = pathlib.Path(sys.argv[1])
    rows = [views(mid) for mid in sys.argv[2:]]
    sheet = Image.new('RGBA', (3 * 266, 266 * len(rows)), (50, 50, 50, 255))
    for r, imgs in enumerate(rows):
        for c, im in enumerate(imgs):
            sheet.alpha_composite(im, (c * 266, r * 266))
    sheet.save(out)
    print(out)
