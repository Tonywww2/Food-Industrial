"""Item icons from the approved reference sheets (07_items): each icon cut from its cell, fitted to 16 × 16."""
import pathlib
import sys

import numpy as np
from PIL import Image
from scipy import ndimage

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import cubes  # noqa: E402
import plants  # noqa: E402

REF = pathlib.Path('.')  # set by run.py: the unpacked pack
N = cubes.N
SHEETS = {
    '07_items/I55_ingredients_16x16_1_grains_veg_nuts.png': (
        [(20, 232), (300, 512), (600, 778)],
        [['corn', 'oats', 'onion', 'scallion', 'cabbage'],
         ['napa_cabbage', 'spinach', 'tea_leaf', 'lemongrass', 'soybean'],
         ['sesame', 'amomum', 'peanut', 'walnut', 'caraway_seed']]),
    '07_items/I55_ingredients_16x16_2_fruits_seafood_duck.png': (
        [(20, 240), (305, 525), (585, 800)],
        [['litsea_berry', 'tomato', 'cucumber', 'pineapple', 'orange'],
         ['lemon', 'papaya', 'grape', 'coconut', 'cassia_bark'],
         ['straw', 'oyster', 'shrimp', 'raw_duck', 'duck_egg']]),
}


def icon(im, box, thr=24, fill=N - 1):
    """The icon inside box (x0, y0, x1, y1) as N × N RGBA, its larger side `fill` pixels."""
    x0, y0, x1, y1 = box
    patch = im[y0:y1, x0:x1]
    bg = cubes.background(im)
    mask = np.abs(patch - bg).sum(axis=2) > thr
    mask = ndimage.binary_opening(mask, iterations=1)
    lab, n = ndimage.label(mask)
    if n == 0:
        return None
    sizes = ndimage.sum(mask, lab, range(1, n + 1))
    keep = np.isin(lab, [i + 1 for i, s in enumerate(sizes) if s >= max(40, sizes.max() * 0.02)])
    ys, xs = np.nonzero(keep)
    cy, cx = (ys.min() + ys.max()) / 2, (xs.min() + xs.max()) / 2
    side = max(ys.max() - ys.min(), xs.max() - xs.min()) * N / fill
    size = cubes.HIRES
    g = (np.arange(size) + 0.5) / size * side - side / 2
    gx, gy = np.meshgrid(cx + g, cy + g)
    rgb = np.zeros((size, size, 3))
    for c in range(3):
        rgb[..., c] = ndimage.map_coordinates(patch[..., c], [gy - 0.5, gx - 0.5], order=1, mode='constant', cval=bg[c])
    m = ndimage.map_coordinates(keep.astype(float), [gy - 0.5, gx - 0.5], order=1, mode='constant', cval=0) > 0.5
    return plants.pixel_rgba(rgb, m)


def extract(out_dir):
    out_dir = pathlib.Path(out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    done = []
    for sheet, (bands, rows) in SHEETS.items():
        im = cubes.load(REF / sheet)
        width = im.shape[1] / 5
        for (y0, y1), ids in zip(bands, rows):
            for c, iid in enumerate(ids):
                rgba = icon(im, (int(c * width), y0, int((c + 1) * width), y1))
                plants.quantize(rgba, 32).save(out_dir / f'{iid}.png')
                done.append(iid)
    return done


if __name__ == '__main__':
    print(extract(sys.argv[1]))
