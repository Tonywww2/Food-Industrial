"""Machine textures from the approved reference sheets (02_machines): front, front_active, side, top at 16 × 16."""
import colorsys
import pathlib
import sys

import numpy as np
from scipy import ndimage
from PIL import Image

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import cubes  # noqa: E402

REF = pathlib.Path('.')  # set by run.py: <pack>/02_machines
SHEETS = {
    'T1_workshop_1_mill_press_stove_steeping.png': ['stone_mill', 'screw_press', 'boiling_stove', 'steeping_vat'],
    'T1_workshop_2_fermentation_koji_kiln_still.png': ['fermentation_vat', 'koji_room', 'earth_kiln', 'pot_still'],
    'T1_workshop_3_crystallizing_drying_pump.png': ['crystallizing_tank', 'drying_room', 'seawater_pump'],
    'T1_food_line_1_noodle_churn_oven_grinder.png': ['noodle_press', 'mixing_churn', 'brick_oven', 'meat_grinder'],
    'T1_food_line_2_candy_freezer_sealer.png': ['candy_table', 'ice_salt_freezer', None, 'filling_sealer'],
    'T2_mechanical_1_crusher_separator_reactor_evap_dryer.png': ['crusher', 'solid_liquid_separator', 'stirred_reactor',
                                                                 'evaporating_crystallizer', 'dryer'],
    'T2_mechanical_2_extraction_absorption_kiln_mixer.png': ['extraction_tank', 'absorption_tower', 'rotary_kiln', 'mixer'],
    'T2_food_line_1_forming_sterilizer_baking_cooker_aging.png': ['forming_line', 'homogenizing_sterilizer', 'baking_line',
                                                                  'cooker_fryer', 'aging_cellar'],
    'T2_food_line_2_cutting_confectionery_freezing_retort.png': ['cutting_station', 'confectionery_line', 'freezing_unit',
                                                                 'filling_retort_line'],
    'T3_chemical_1_distillation_reactor_electrolyzer_adsorption.png': ['distillation_column', 'high_pressure_reactor',
                                                                       'electrolyzer', 'adsorption_column'],
    'T3_T4_chemical_2_and_biotech_arc_gas_spray_fermenter_bench_membrane.png': ['electric_arc_furnace', 'gas_phase_reactor',
                                                                               'spray_dryer', 'sterile_fermenter',
                                                                               'culture_bench', 'membrane_separator'],
}
LEFT, RIGHT = 0.83, 0.59  # baked shading of the left and right faces against the top (measured on the sheets)
N = cubes.N


def grid(im):
    """The cubes of a sheet as rows of [view A, view B, view C] (None where one is missing)."""
    found = cubes.grid_cubes(im, thr=12)
    xs = sorted(f[0] + f[2] / 2 for f in found)
    cols = []
    for x in xs:
        if not cols or x - cols[-1][-1] > 60:
            cols.append([x])
        else:
            cols[-1].append(x)
    centres = [np.mean(c) for c in cols][-3:]
    ys = sorted(f[1] + f[3] / 2 for f in found)
    rows = []
    for y in ys:
        if not rows or y - rows[-1][-1] > 80:
            rows.append([y])
        else:
            rows[-1].append(y)
    rcentres = [np.mean(r) for r in rows]
    table = [[None, None, None] for _ in rcentres]
    for f in found:
        cx, cy = f[0] + f[2] / 2, f[1] + f[3] / 2
        c = int(np.argmin([abs(cx - x) for x in centres]))
        r = int(np.argmin([abs(cy - y) for y in rcentres]))
        if abs(cx - centres[c]) < 60:
            table[r][c] = f
    return table


def unshade(face, factor):
    return np.clip(face / factor, 0, 255)


def hsv(img):
    a = np.asarray(img, dtype=np.float64) / 255
    out = np.zeros_like(a)
    for y in range(a.shape[0]):
        for x in range(a.shape[1]):
            out[y, x] = colorsys.rgb_to_hsv(*a[y, x])
    return out


def glow_masks(img):
    h = hsv(img)
    hue, s, v = h[..., 0] * 360, h[..., 1], h[..., 2]
    warm = (hue < 62) | (hue > 340)
    fire = warm & (((s > 0.5) & (v > 0.72)) | ((s > 0.35) & (v > 0.97)))
    # the paler core of a flame next to it
    for _ in range(2):
        fire |= ndimage.binary_dilation(fire) & warm & (v > 0.8) & (s > 0.3)
    cyan = (hue > 160) & (hue < 210) & (s > 0.3) & (v > 0.78)
    return fire, cyan


def darken(img, mask, how):
    a = np.asarray(img, dtype=np.float64).copy()
    if how == 'fire':
        lum = a[mask] @ np.array([0.299, 0.587, 0.114])
        dark = np.stack([lum * 0.22 + 18, lum * 0.18 + 14, lum * 0.16 + 12], axis=1)
        a[mask] = dark
    else:
        a[mask] = a[mask] * 0.6 + np.array([8, 14, 16])
    return Image.fromarray(np.clip(a, 0, 255).round().astype(np.uint8))


def light_up(img):
    """No glow in the reference: windows (light blue-grey glass) light up cyan, dark openings glow warm."""
    a = np.asarray(img, dtype=np.float64).copy()
    h = hsv(img)
    hue, s, v = h[..., 0] * 360, h[..., 1], h[..., 2]
    glass = (hue > 170) & (hue < 230) & (s > 0.08) & (s < 0.45) & (v > 0.45)
    changed = False
    if glass.sum() >= 2:  # two texels of glass at least
        a[glass] = np.clip(a[glass] * 0.6 + np.array([40, 150, 170]) * 0.55, 0, 255)
        changed = True
    return Image.fromarray(np.clip(a, 0, 255).round().astype(np.uint8)), changed


FIRE = {'boiling_stove', 'earth_kiln', 'drying_room', 'brick_oven', 'candy_table', 'rotary_kiln', 'gas_phase_reactor',
        'baking_line'}
LIGHT = {'cooker_fryer', 'absorption_tower', 'distillation_column', 'sterile_fermenter', 'culture_bench', 'membrane_separator',
         'spray_dryer', 'freezing_unit', 'evaporating_crystallizer', 'electric_arc_furnace', 'electrolyzer'}
# where the comparison with view C picks the wrong face (a small feature, the rest alike)
FRONT_RIGHT = {'stone_mill': False}
WINDOWS = {'solid_liquid_separator', 'stirred_reactor', 'dryer', 'mixer', 'adsorption_column'}


def top_quad(v, front_right):
    """The top as a texture with the front edge on the top row and west on the left."""
    if front_right:
        return [v['UR'], v['C'], v['UL'], v['T']]
    return [v['C'], v['UL'], v['T'], v['UR']]


def view_faces(im, cube):
    """The cube's corners and its left and right faces, unshaded."""
    x0, y0, w, h, a = cube
    v = cubes.vertices(x0, y0, w, h, a)
    size = cubes.HIRES
    left = cubes.unwarp(im, [v['UL'], v['C'], v['B'], v['LL']], size)
    right = cubes.unwarp(im, [v['C'], v['UR'], v['LR'], v['B']], size)
    return v, unshade(left, LEFT), unshade(right, RIGHT)


def distance(a, b):
    return float(np.abs(np.asarray(cubes.pixelate(a, 16), dtype=float) - np.asarray(cubes.pixelate(b, 16), dtype=float)).mean())


def extract(out_dir):
    out_dir = pathlib.Path(out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    report = []
    for sheet, ids in SHEETS.items():
        im = cubes.load(REF / sheet)
        table = grid(im)
        if len(table) != len(ids):
            print('rows', sheet, len(table), len(ids))
        for row, mid in zip(table, ids):
            if mid is None:
                continue
            a_view, b_view, c_view = row
            view = a_view or b_view
            v, left, right = view_faces(im, view)
            _, cl, cr = view_faces(im, c_view) if c_view else (None, None, None)
            if c_view is not None:
                dl = distance(left, cl)
                dr = distance(right, cr)
                front_right = dr > dl
                front_right = FRONT_RIGHT.get(mid, front_right)
            else:
                front_right = view is b_view
            front = right if front_right else left
            side = cl if c_view is not None else (left if front_right else right)
            top = cubes.unwarp(im, top_quad(v, front_right), cubes.HIRES)
            imgs = {f: cubes.pixelate(x, N, 32) for f, x in (('front', front), ('side', side), ('top', top))}
            fire, cyan = glow_masks(imgs['front'])
            active = inactive = imgs['front']
            how = []
            if mid in FIRE and fire.sum():
                inactive = darken(inactive, fire, 'fire')
                how.append(f'fire {int(fire.sum())}')
            if mid in LIGHT and cyan.sum():
                inactive = darken(inactive, cyan, 'cyan')
                how.append(f'light {int(cyan.sum())}')
            if mid in WINDOWS:
                active, changed = light_up(inactive)
                how.append('windows' if changed else 'no window found')
            imgs['front'], imgs['front_active'] = inactive, active
            for f, img in imgs.items():
                img.save(out_dir / f'{mid}_{f}.png')
            report.append((mid, ('right ' if front_right else 'left ') + ', '.join(how)))
    return report


if __name__ == '__main__':
    for mid, how in extract(sys.argv[1]):
        print(mid, how)
