"""I55 crops (ripe stage), I55 tree blocks and the rubber wood set from the approved reference sheets."""
import pathlib
import sys

import numpy as np
from PIL import Image
from scipy import ndimage

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import cubes  # noqa: E402
import machines  # noqa: E402
import plants  # noqa: E402

REF = pathlib.Path('.')  # set by run.py: <pack>/05_plants_and_trees
N = cubes.N


def leaves(face, holes=0.10, fruit=None):
    """Leaves texture: the darkest share of pixels become holes; `fruit` (a mask function) removed gives the plain
    leaves too."""
    img = cubes.pixelate(face, N, 32)
    a = np.asarray(img.convert('RGBA')).copy()
    lum = a[..., :3] @ np.array([0.299, 0.587, 0.114])
    a[..., 3] = np.where(lum <= np.quantile(lum, holes), 0, 255)
    plain = None
    if fruit is not None:
        h, s, v = plants.hsv_arrays(a[..., :3].astype(float))
        m = fruit(h, s, v) & (a[..., 3] > 0)
        m = ndimage.binary_dilation(m) & (a[..., 3] > 0) & ~((h > 70) & (h < 170) & (s > 0.3))
        plain = a.copy()
        green = (a[..., 3] > 0) & ~m
        for _ in range(4):
            if not m.any():
                break
            grown = np.zeros_like(m)
            for y, x in zip(*np.nonzero(m)):
                ys, xs = slice(max(0, y - 1), y + 2), slice(max(0, x - 1), x + 2)
                nb = green[ys, xs]
                if nb.any():
                    plain[y, x, :3] = np.median(plain[ys, xs][nb][:, :3], axis=0)
                    grown[y, x] = True
            green |= grown
            m &= ~grown
    return Image.fromarray(a, 'RGBA'), (Image.fromarray(plain, 'RGBA') if plain is not None else None)


def ripe_crops(out_dir):
    im = cubes.load(REF / 'I55_crops_bushes_trees_16x16.png')
    labels, _ = ndimage.label(cubes.foreground(im, 12))
    ws, a, e = 202.0, 50.5, 4.0
    xs = [352, 578, 802, 1027]
    rows = {243: ['corn', 'tomato', 'cabbage', 'pineapple'], 458.5: ['onion', 'scallion', 'cucumber', None]}
    done = []
    for by, ids in rows.items():
        for bx, cid in zip(xs, ids):
            if cid is None:
                continue
            rgba = plants.cut(im, bx, by, ws, a, e, labels=labels)
            plants.quantize(rgba).save(out_dir / f'{cid}_crop_stage3.png')
            done.append(f'{cid}_crop_stage3')
    return done


def tree_blocks(out_dir):
    im = cubes.load(REF / 'I55_crops_bushes_trees_16x16.png')
    done = []
    log = (253, 673, 198, 216, 49.5)
    v, left, right = machines.view_faces(im, log)
    cubes.pixelate(left, N, 32).save(out_dir / 'cassia_log.png')
    cubes.pixelate(cubes.unwarp(im, machines.top_quad(v, False), cubes.HIRES), N, 32).save(out_dir / 'cassia_log_top.png')
    done += ['cassia_log', 'cassia_log_top']
    orange = lambda h, s, v: (h > 15) & (h < 45) & (s > 0.55) & (v > 0.55)
    yellow = lambda h, s, v: (h > 25) & (h < 60) & (s > 0.5) & (v > 0.55)
    tan = lambda h, s, v: (h > 20) & (h < 45) & (s > 0.3) & (v > 0.7)
    for tid, box, fruit in (('orange', (480, 676, 195, 214, 48.75), orange), ('papaya', (705, 676, 195, 213, 48.75), yellow),
                            ('walnut', (931, 675, 195, 214, 48.75), tan)):
        v, left, right = machines.view_faces(im, box)
        fruiting, plain = leaves(left, fruit=fruit)
        fruiting.save(out_dir / f'{tid}_leaves_fruiting.png')
        plain.save(out_dir / f'{tid}_leaves.png')
        done += [f'{tid}_leaves_fruiting', f'{tid}_leaves']
    return done


def rubber(out_dir):
    im = cubes.load(REF / 'rubber_wood_1_log_stripped_planks_leaves.png')
    table = machines.grid(im)
    done = []
    names = [('rubber_log', 'rubber_log_top'), ('stripped_rubber_log', 'stripped_rubber_log_top'), ('rubber_planks', None),
             ('rubber_leaves', None)]
    for row, (side_id, top_id) in zip(table, names):
        cube = row[2] or row[0]  # view C: the tapping cuts of view A stay off the plain log
        v, left, right = machines.view_faces(im, cube)
        if side_id == 'rubber_leaves':
            leaves(left)[0].save(out_dir / 'rubber_leaves.png')
        else:
            cubes.pixelate(left, N, 32).save(out_dir / f'{side_id}.png')
        done.append(side_id)
        if top_id:
            cubes.pixelate(cubes.unwarp(im, machines.top_quad(v, False), cubes.HIRES), N, 32).save(out_dir / f'{top_id}.png')
            done.append(top_id)
    # stripped wood blocks use the stripped log side
    return done


if __name__ == '__main__':
    out = pathlib.Path(sys.argv[1])
    out.mkdir(parents=True, exist_ok=True)
    print(ripe_crops(out) + tree_blocks(out) + rubber(out))


def rubber_2(out_dir):
    import items
    im = cubes.load(REF / 'rubber_wood_2_sapling_door_trapdoor_swiftlet_nest.png')
    bg = cubes.background(im)
    done = []
    # sapling: the sprite, bottom-anchored and filling the block
    rgba = items.icon(im, (262 - 10, 53 - 10, 262 + 181 + 10, 53 + 179 + 10), fill=31)
    plants.quantize(rgba).save(out_dir / 'rubber_sapling.png')
    done.append('rubber_sapling')
    # door: the wide face of view A, two blocks tall
    x0, y0, w, h = 302, 249, 101, 249
    fg = cubes.foreground(im, 12)
    def extent(x):
        col = np.nonzero(fg[y0:y0 + h, int(x)])[0]
        return y0 + col.min(), y0 + col.max() + 1
    xa = x0 + w * (3 / 16) / (1 + 3 / 16) + 1
    xb = x0 + w - 2
    (ta, ba), (tb, bb) = extent(xa), extent(xb)
    # the wide face rises to the right (seen like a right face): its corners
    face = machines.unshade(cubes.unwarp(im, [(xa, ta), (xb, tb), (xb, bb), (xa, ba)], 2 * cubes.HIRES), machines.RIGHT)
    face = face.reshape(2 * cubes.HIRES, 2 * cubes.HIRES, 3)[:, ::2] if False else np.asarray(Image.fromarray(np.clip(face, 0, 255).astype(np.uint8)).resize((cubes.HIRES, 2 * cubes.HIRES), Image.BILINEAR)).astype(float)
    top, bottom = face[:cubes.HIRES], face[cubes.HIRES:]
    for name, part in (('rubber_door_top', top), ('rubber_door_bottom', bottom)):
        rgba = np.asarray(cubes.pixelate(part, N, 32).convert('RGBA')).copy()
        dark = np.abs(rgba[..., :3].astype(float) - bg).sum(axis=2) < 40
        rgba[..., 3] = np.where(dark, 0, 255)
        Image.fromarray(rgba, 'RGBA').save(out_dir / f'{name}.png')
        done.append(name)
    # trapdoor: the top of view A, holes see through
    box = (245, 524, 215, 141)
    face = blocks_top(im, box)
    rgba = np.asarray(cubes.pixelate(face, N, 32).convert('RGBA')).copy()
    dark = np.abs(rgba[..., :3].astype(float) - bg).sum(axis=2) < 40
    rgba[..., 3] = np.where(dark, 0, 255)
    Image.fromarray(rgba, 'RGBA').save(out_dir / 'rubber_trapdoor.png')
    done.append('rubber_trapdoor')
    # swiftlet nest: a patch of the woven nest
    cx, cy, r = 352, 778, 34
    patch = im[cy - r:cy + r, cx - r:cx + r]
    big = np.asarray(Image.fromarray(np.clip(patch, 0, 255).astype(np.uint8)).resize((cubes.HIRES, cubes.HIRES), Image.BILINEAR)).astype(float)
    cubes.pixelate(big, N, 24).save(out_dir / 'swiftlet_nest.png')
    done.append('swiftlet_nest')
    return done


def blocks_top(im, box):
    x0, y0, w, h = box
    v = cubes.vertices(x0, y0, w, w / 2 + 10, w / 4)
    return cubes.unwarp(im, machines.top_quad(v, False), cubes.HIRES)


def young_stages(out_dir, ids):
    """Stages 0–2 of a crop whose ripe stage came from the sheet: the ripe sprite scaled down from the bottom centre,
    produce (not green) turned green on stages 0–1."""
    done = []
    for cid in ids:
        ripe = np.asarray(Image.open(out_dir / f'{cid}_crop_stage3.png').convert('RGBA')).astype(float)
        h, s, v = plants.hsv_arrays(ripe[..., :3])
        leafy = (h > 55) & (h < 190) & (s > 0.25)
        greens = ripe[(ripe[..., 3] > 0) & leafy][:, :3]
        green = np.median(greens, axis=0) if len(greens) else np.array([90, 140, 60])
        for stage, scale in ((0, 0.4), (1, 0.62), (2, 0.84)):
            src = ripe.copy()
            if stage < 2:
                produce = (src[..., 3] > 0) & ~leafy
                shade = (src[..., :3] @ np.array([0.299, 0.587, 0.114]))[produce] / 255
                src[produce, :3] = np.clip(green * (0.7 + 0.5 * shade[:, None]), 0, 255)
            img = Image.fromarray(src.astype(np.uint8), 'RGBA')
            size = max(4, round(N * scale))
            small = img.resize((size, size), Image.NEAREST)
            out = Image.new('RGBA', (N, N), (0, 0, 0, 0))
            out.alpha_composite(small, ((N - size) // 2, N - size))
            out.save(out_dir / f'{cid}_crop_stage{stage}.png')
            done.append(f'{cid}_crop_stage{stage}')
    return done
