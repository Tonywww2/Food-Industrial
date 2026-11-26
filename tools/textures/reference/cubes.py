"""Find the isometric cubes of a reference sheet and unwarp their three visible faces.

A cube's silhouette is a hexagon in its bounding box (x0, y0, w, h) with the side vertices `a` below the top:
T (x0+w/2, y0), UL (x0, y0+a), UR (x0+w, y0+a), C (x0+w/2, y0+2a), LL (x0, y0+h-a), LR (x0+w, y0+h-a),
B (x0+w/2, y0+h). Faces as textures (u right, v down), for a camera at the front-left (view A):
left face (UL, C, B, LL), right face (C, UR, LR, B), top (C, UL, T, UR) with the front edge on the top row.
"""
import numpy as np
from PIL import Image
from scipy import ndimage, signal

N = 16  # texels across a block face or an item: Minecraft's 16x standard (user 2026-11-25), as the sheets draw them
HIRES = 256  # faces are unwarped at this size first, 16 pixels to a texel, then sampled down


def load(path):
    return np.array(Image.open(path).convert('RGB')).astype(np.float64)


def background(im):
    edge = np.concatenate([im[:6].reshape(-1, 3), im[-6:].reshape(-1, 3), im[:, :6].reshape(-1, 3), im[:, -6:].reshape(-1, 3)])
    return np.median(edge, axis=0)


def foreground(im, thr=24):
    d = np.abs(im - background(im)).sum(axis=2)
    return ndimage.binary_fill_holes(d > thr)


def hexagon_mask(w, h, a):
    yy, xx = np.mgrid[0:h, 0:w]
    cx = (w - 1) / 2
    dx = np.abs(xx - cx) / (w / 2)  # 0 at the centre column, 1 at the sides
    return (yy >= a * dx) & (yy <= h - 1 - a * dx)


def clean_sizes(im, thr=45):
    """(w, h, a) of components that look like a whole cube: area close to w*(h - a) with a = w/4."""
    d = np.abs(im - background(im)).sum(axis=2)
    mask = ndimage.binary_fill_holes(d > thr)
    lab, n = ndimage.label(mask)
    found = []
    for i, sl in enumerate(ndimage.find_objects(lab), 1):
        h = sl[0].stop - sl[0].start
        w = sl[1].stop - sl[1].start
        if w < 40 or h < 40:
            continue
        region = lab[sl] == i
        area = region.sum()
        expect = w * (h - w / 4)
        if abs(area - expect) / expect < 0.04 and 0.8 < h / w < 1.6:
            col = region[:, 1:4].any(axis=1)
            found.append((w, h, int(np.argmax(col))))
    return found


def find_cubes(im, w, h, a, thr=24, min_score=0.9):
    fg = foreground(im, thr).astype(np.float64)
    inside = hexagon_mask(w, h, a)
    template = np.where(inside, 1.0, -1.0)
    score = signal.fftconvolve(fg, template[::-1, ::-1], mode='valid') / inside.sum()
    found = []
    taken = np.zeros_like(score, dtype=bool)
    for idx in np.argsort(score, axis=None)[::-1]:
        y, x = np.unravel_index(idx, score.shape)
        if score[y, x] < min_score:
            break
        if taken[y, x]:
            continue
        found.append((int(x), int(y), float(score[y, x])))
        taken[max(0, y - h // 2):y + h // 2, max(0, x - w // 2):x + w // 2] = True
    found.sort(key=lambda c: (round(c[1] / (h / 2)), c[0]))
    return found


def vertices(x0, y0, w, h, a):
    return dict(T=(x0 + w / 2, y0), UL=(x0, y0 + a), UR=(x0 + w, y0 + a), C=(x0 + w / 2, y0 + 2 * a),
                LL=(x0, y0 + h - a), LR=(x0 + w, y0 + h - a), B=(x0 + w / 2, y0 + h))


def unwarp(im, quad, size):
    """The quad (image points of texture corners (0,0), (1,0), (1,1), (0,1)) sampled to size × size."""
    p00, p10, p11, p01 = [np.array(p, dtype=np.float64) for p in quad]
    t = (np.arange(size) + 0.5) / size
    u, v = np.meshgrid(t, t)
    pts = (p00 * ((1 - u) * (1 - v))[..., None] + p10 * (u * (1 - v))[..., None]
           + p11 * (u * v)[..., None] + p01 * ((1 - u) * v)[..., None])
    xs, ys = pts[..., 0] - 0.5, pts[..., 1] - 0.5
    out = np.zeros((size, size, im.shape[2]))
    for c in range(im.shape[2]):
        out[..., c] = ndimage.map_coordinates(im[..., c], [ys, xs], order=1, mode='nearest')
    return out


def faces(im, x0, y0, w, h, a, size=256):
    v = vertices(x0, y0, w, h, a)
    left = [v['UL'], v['C'], v['B'], v['LL']]
    right = [v['C'], v['UR'], v['LR'], v['B']]
    top = [v['C'], v['UL'], v['T'], v['UR']]
    return unwarp(im, left, size), unwarp(im, right, size), unwarp(im, top, size)


def texel_grid(face, n=N):
    """The centres (xs, ys, in pixels) of the n × n texels of a face or sprite sampled at HIRES. The sheets draw
    pixel art, so the colour steps lie on a grid; the cut can sit a few pixels off it (a cube outline drawn a little
    larger, a sprite placed by its slab), and cells laid on the cut itself would straddle two texels. The grid's
    spacing and offset are the ones whose lines hold much more colour change than the cell middles; a face with too
    few steps to tell, or drawn finer than 16 to the block (the crops), keeps the cut's own grid (aligned False)."""
    a = np.asarray(face, dtype=np.float64)[..., :3]
    size = a.shape[0]
    centres, aligned = [], []
    for axis in (1, 0):
        steps = np.abs(np.diff(a, axis=axis)).sum(axis=2)
        profile = steps.sum(axis=1 - axis)  # change between pixel j and j + 1, summed across the face
        best, grid = 0.0, (size / n, 0.0)
        if profile.sum() > 0:
            for spacing in np.arange(size / n * 0.88, size / n * 1.12, 0.05):
                for offset in np.arange(0, spacing, 0.25):
                    lines = np.arange(offset, size - 1, spacing)
                    lines = lines[lines >= 1]
                    mids = lines + spacing / 2
                    mids = mids[mids < size - 1]
                    if len(lines) < n // 2 or len(mids) < n // 2:
                        continue
                    on = np.interp(lines - 1, np.arange(len(profile)), profile).mean()
                    off = np.interp(mids - 1, np.arange(len(profile)), profile).mean()
                    contrast = (on - off) / (profile.mean() + 1e-9)
                    if contrast > best:
                        best, grid = contrast, (spacing, offset)
        aligned.append(best >= 1.0)
        if best < 1.0:
            grid = (size / n, 0.0)
        spacing, offset = grid
        k0 = int(round((size / 2 - offset) / spacing - n / 2))
        centres.append(np.clip(offset + spacing * (np.arange(k0, k0 + n) + 0.5), 0, size - 1))
    return centres[0], centres[1], spacing, all(aligned)


def sample_grid(face, n=N, mask=None):
    """face (HIRES × HIRES × 3) to n × n × 3 and an n × n coverage (the share of the mask's pixels in each texel).
    On the sheet's own grid a texel is the median of a small window round its centre; where the art is finer than
    the grid, a texel takes the colour that covers most of its cell (the face reduced to 24 colours first), so thin
    bright details (a cob, a fruit) keep their colour instead of blurring into the leaves."""
    a = np.asarray(face, dtype=np.float64)[..., :3]
    m = np.ones(a.shape[:2], bool) if mask is None else mask
    xs, ys, spacing, aligned = texel_grid(np.where(m[..., None], a, 0), n)
    w = max(1, int(spacing / 4)) if aligned else max(1, int(spacing * 3 / 8))
    if not aligned:
        img = Image.fromarray(np.clip(a, 0, 255).round().astype(np.uint8))
        q = img.quantize(24, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE)
        index, palette = np.asarray(q), np.array(q.getpalette()[:72]).reshape(-1, 3)
    out = np.zeros((n, n, 3))
    cover = np.zeros((n, n))
    for j, cy in enumerate(ys):
        for i, cx in enumerate(xs):
            y, x = int(cy), int(cx)
            win = (slice(max(0, y - w), y + w + 1), slice(max(0, x - w), x + w + 1))
            inside = m[win]
            cover[j, i] = inside.mean()
            if not inside.any():
                continue
            if aligned:
                out[j, i] = np.median(a[win][inside], axis=0)
            else:
                out[j, i] = palette[np.bincount(index[win][inside]).argmax()]
    return out, cover


def pixelate(face, n, colours=0):
    """face (size × size × 3) to n × n, one colour per texel of the sheet's own pixel grid (sample_grid), then an
    optional palette of `colours` colours (median cut)."""
    out, _ = sample_grid(face, n)
    img = Image.fromarray(np.clip(out, 0, 255).round().astype(np.uint8))
    if colours:
        img = img.quantize(colours, method=Image.Quantize.MEDIANCUT, dither=Image.Dither.NONE).convert('RGB')
    return img


def bands(profile, min_len, level):
    """Runs of indices where profile > level, at least min_len long: [(start, stop)]."""
    on = profile > level
    runs, start = [], None
    for i, v in enumerate(on):
        if v and start is None:
            start = i
        if not v and start is not None:
            if i - start >= min_len:
                runs.append((start, i))
            start = None
    if start is not None and len(on) - start >= min_len:
        runs.append((start, len(on)))
    return runs


def grid_cubes(im, thr=16, x_from=0, min_w=60):
    """Cubes laid out in columns: (x0, y0, w, h, a) for each, found from the foreground profiles. Columns are
    the x runs of foreground; in a column, each cube is a run of rows as wide as the column (its vertical edges),
    extended up and down by the rise of its top and bottom faces."""
    fg = foreground(im, thr)
    fg[:, :x_from] = False
    fg = ndimage.binary_opening(fg, iterations=2)
    cols = bands(fg.sum(axis=0), min_w, im.shape[0] * 0.02)
    found = []
    for c0, c1 in cols:
        sub = fg[:, c0:c1]
        width = c1 - c0
        prof = sub.sum(axis=1)
        for b0, b1 in bands(prof, 20, 0.85 * width):
            # rise above: rows above b0 while the profile grows
            y = b0
            while y > 0 and prof[y - 1] > 1 and prof[y - 1] <= prof[y] + 2:
                y -= 1
            y0 = y
            y = b1
            while y < len(prof) - 1 and prof[y] > 1 and prof[y] <= prof[y - 1] + 2:
                y += 1
            y1 = y
            rows = sub[b0:b1]
            xs = np.nonzero(rows.any(axis=0))[0]
            x0, x1 = c0 + xs.min(), c0 + xs.max() + 1
            a = ((b0 - y0) + (y1 - b1)) / 2
            found.append((int(x0), int(y0), int(x1 - x0), int(y1 - y0), float(a)))
    if found:
        mw = np.median([f[2] for f in found])
        found = [f for f in found if abs(f[2] - mw) < 0.12 * mw and 0.95 < f[3] / f[2] < 1.45]
    found = [(x0, y0, w, h, w / 4) for x0, y0, w, h, a in found]
    found.sort(key=lambda f: (f[1] // 60, f[0]))
    return found
