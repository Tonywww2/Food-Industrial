"""Crop and plant sprites from the approved reference sheets: the plant standing on a soil slab, cut out above the slab
centre and scaled so that the slab's width is the block's width (16 × 16, alpha 0 or 255)."""
import colorsys
import pathlib
import sys

import numpy as np
from PIL import Image
from scipy import ndimage

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import cubes  # noqa: E402

REF = pathlib.Path('.')  # set by run.py: <pack>/05_plants_and_trees
N = cubes.N


def cells(im, thr=12, min_area=1500):
    """Components (plant with its slab) as rows of columns, ordered."""
    fg = ndimage.binary_opening(cubes.foreground(im, thr), iterations=1)
    lab, n = ndimage.label(fg)
    comps = []
    for i, sl in enumerate(ndimage.find_objects(lab), 1):
        region = lab[sl] == i
        if region.sum() < min_area:
            continue
        comps.append((sl, region))
    return comps


def sat_val(px):
    a = px / 255.0
    mx, mn = a.max(axis=-1), a.min(axis=-1)
    s = np.where(mx > 0, (mx - mn) / np.maximum(mx, 1e-6), 0)
    return s, mx


def cut(im, bx, by, ws, a, e, thr=20, soil=None, labels=None, above=None):
    """The sprite above the slab whose bottom vertex is (bx, by): N × N RGBA."""
    base = by - a - e
    x0, x1 = bx - ws / 2, bx + ws / 2
    y0, y1 = base - ws, base
    size = cubes.HIRES
    xs = np.linspace(x0, x1, size, endpoint=False) + (x1 - x0) / size / 2
    ys = np.linspace(y0, y1, size, endpoint=False) + (y1 - y0) / size / 2
    gx, gy = np.meshgrid(xs, ys)
    patch = np.zeros((size, size, 3))
    for c in range(3):
        patch[..., c] = ndimage.map_coordinates(im[..., c], [gy - 0.5, gx - 0.5], order=1, mode='constant', cval=38)
    bg = cubes.background(im)
    d = np.abs(patch - bg).sum(axis=2)
    plant = d > thr
    # the back half of the slab's top rhombus lies in the cut: keep only plant colours there
    if labels is not None:
        own = labels[int(by) - 3, int(bx)]
        lab = ndimage.map_coordinates(labels, [gy - 0.5, gx - 0.5], order=0, mode='constant', cval=0)
        plant &= lab == own
    dxn = np.abs(gx - bx) / (ws / 2)
    in_slab = gy > base - a * (1 - dxn) - ws * 0.03
    if above is not None:  # the slab of the row above can reach into the cut
        in_slab |= gy <= above + 2
    hue, s, v = hsv_arrays(patch)
    green = (hue > 55) & (hue < 205) & (s > 0.3) & (v > 0.25)
    bright = v > 0.66
    red = ((hue < 18) | (hue > 330)) & (s > 0.5) & (v > 0.42)
    pink = (hue > 280) & (s > 0.2) & (v > 0.5)
    keep = green | bright | red | pink
    if soil is not None:
        keep &= ~soil(patch)
    plant &= ~in_slab | keep
    return pixel_rgba(patch, plant)


def hsv_arrays(patch):
    a = np.clip(patch / 255.0, 0, 1)
    r, g, b = a[..., 0], a[..., 1], a[..., 2]
    mx, mn = a.max(axis=-1), a.min(axis=-1)
    d = mx - mn
    hue = np.zeros_like(mx)
    nz = d > 1e-6
    rm = nz & (mx == r)
    gm = nz & (mx == g) & ~rm
    bm = nz & ~rm & ~gm
    hue[rm] = ((g - b)[rm] / d[rm]) % 6
    hue[gm] = (b - r)[gm] / d[gm] + 2
    hue[bm] = (r - g)[bm] / d[bm] + 4
    hue *= 60
    s = np.where(mx > 0, d / np.maximum(mx, 1e-6), 0)
    return hue, s, mx


def pixel_rgba(patch, mask):
    """patch (HIRES × HIRES × 3) and its sprite mask to N × N RGBA (cubes.sample_grid): a texel is opaque when the
    sprite covers half of it."""
    rgb, cover = cubes.sample_grid(patch, N, mask)
    out = np.zeros((N, N, 4), dtype=np.uint8)
    on = cover >= 0.5
    out[on, :3] = np.clip(rgb[on], 0, 255).round()
    out[on, 3] = 255
    return out


def quantize(rgba, colours=24):
    img = Image.fromarray(rgba, 'RGBA')
    alpha = rgba[..., 3]
    q = img.convert('RGB').quantize(colours, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE).convert('RGBA')
    q = np.asarray(q).copy()
    q[..., 3] = alpha
    return Image.fromarray(q, 'RGBA')


def clusters(values, gap):
    values = sorted(values)
    groups = []
    for v in values:
        if not groups or v - groups[-1][-1] > gap:
            groups.append([v])
        else:
            groups[-1].append(v)
    return [float(np.median(g)) for g in groups]


def slab_grid(im):
    """(columns' x, rows' y of the slab bottom vertices, slab width, a, e)."""
    comps = cells(im)
    bottoms, widths, edges = [], [], []
    for sl, region in comps:
        ys, xs = np.nonzero(region)
        w = sl[1].stop - sl[1].start
        if w < 120:
            continue  # label text
        ymax = ys.max()
        bottoms.append((xs[ys == ymax].mean() + sl[1].start, ymax + sl[0].start))
        widths.append(w)
        col = ys[xs <= xs.min() + 1]
        edges.append(col.max() - col.min())
    ws = float(np.median(widths))
    e = float(np.percentile(edges, 15))
    xs = clusters([b[0] for b in bottoms], 80)
    ys = clusters([b[1] for b in bottoms], 80)
    return xs, ys, ws, ws / 4, e


CROP_SHEETS = {
    'crops_16x16_1_chili_turmeric_stevia_mint.png': ['chili_pepper', 'turmeric_rhizome', 'stevia', 'mint'],
    'crops_16x16_2_rosemary_indigo_paddy_gardenia.png': ['rosemary', 'indigo_plant', 'paddy', 'gardenia_bush'],
}


def crops(out_dir):
    out_dir = pathlib.Path(out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    done = []
    for sheet, ids in CROP_SHEETS.items():
        im = cubes.load(REF / sheet)
        xs, ys, ws, a, e = slab_grid(im)
        labels, _ = ndimage.label(cubes.foreground(im, 12))
        print(sheet, [round(x) for x in xs], [round(y) for y in ys], round(ws), round(a), round(e))
        for r, mid in enumerate(ids):
            for c in range(4):
                rgba = cut(im, xs[-4 + c], ys[r], ws, a, e, labels=labels, above=ys[r - 1] if r else None)
                quantize(rgba).save(out_dir / f'{mid}_stage{c}.png')
                done.append(f'{mid}_stage{c}')
    return done
