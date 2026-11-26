"""Uniform blocks (one texture on every face, or side and top) from the approved reference sheets (03, 04)."""
import pathlib
import sys

import numpy as np
from PIL import Image

sys.path.insert(0, str(pathlib.Path(__file__).resolve().parent))
import cubes  # noqa: E402
import machines  # noqa: E402

REF = pathlib.Path('.')  # set by run.py: the unpacked pack
N = cubes.N
# sheet -> rows: id (one texture from the side), (side id, top id), or None to skip
SHEETS = {
    '03_tanks_handmade_multiblock/multiblock_casings_1_koji_drying_kiln_tower_shell.png':
        ['koji_room_wall', 'drying_room_wall', 'kiln_brick', 'tower_wall', 'kiln_shell'],
    '03_tanks_handmade_multiblock/multiblock_casings_2_chemtower_refractory_sterile_food_tube.png':
        ['chemical_tower_wall', 'refractory_bricks', 'sterile_vessel_wall', 'food_machine_casing',
         ('furnace_tube_side', 'furnace_tube_top')],
    '03_tanks_handmade_multiblock/multiblock_ports_intake_item_fluid_energy_condenser.png':
        ['air_intake', 'item_port', 'fluid_port', 'energy_port', ('condenser_side', 'condenser_top')],
    '04_ores_and_deposits/ores_1_gypsum_alunite_magnesite_apatite_sulfur_selenium_jappa.png':
        ['gypsum_ore', 'alunite_ore', 'magnesite_ore', 'apatite_ore', 'sulfur_ore', 'selenium_ore'],
    '04_ores_and_deposits/ores_2_chromite_ilmenite_pyrolusite_rocksalt_trona_saltpeter.png':
        ['chromite_ore', 'ilmenite', 'pyrolusite', 'rock_salt', 'trona_ore', 'nitratine'],
    '04_ores_and_deposits/soils_and_deposits_nitre_attapulgite_bentonite_perlite_talc.png':
        ['nitre_soil', 'attapulgite_deposit', 'bentonite_deposit', 'perlite_deposit', 'talc_deposit'],
    '03_tanks_handmade_multiblock/tanks_wooden_and_metal_flush.png':
        [('wooden_tank_side', None), ('metal_tank_side', 'metal_tank_top')],
}


def faces_of(im, row):
    """(side, top) as unshaded 256-pixel faces: the left face of view A (or of view C, or the right face of B)."""
    a_view, b_view, c_view = row
    if c_view:  # the label text of a sheet can reach into view A
        v, left, right = machines.view_faces(im, c_view)
        side = left
        top = cubes.unwarp(im, machines.top_quad(v, False), cubes.HIRES)
    elif a_view:
        v, left, right = machines.view_faces(im, a_view)
        side = left
        top = cubes.unwarp(im, machines.top_quad(v, False), cubes.HIRES)
    else:
        v, left, right = machines.view_faces(im, b_view)
        side = right
        top = cubes.unwarp(im, machines.top_quad(v, True), cubes.HIRES)
    return side, top


def extract(out_dir):
    out_dir = pathlib.Path(out_dir)
    out_dir.mkdir(parents=True, exist_ok=True)
    done = []
    for sheet, rows in SHEETS.items():
        im = cubes.load(REF / sheet)
        table = machines.grid(im)
        if len(table) != len(rows):
            print('rows', sheet, len(table), len(rows))
        for row, ids in zip(table, rows):
            if ids is None:
                continue
            side, top = faces_of(im, row)
            if isinstance(ids, tuple):
                side_id, top_id = ids
                cubes.pixelate(side, N, 32).save(out_dir / f'{side_id}.png')
                done.append(side_id)
                if top_id:
                    cubes.pixelate(top, N, 32).save(out_dir / f'{top_id}.png')
                    done.append(top_id)
            else:
                cubes.pixelate(side, N, 32).save(out_dir / f'{ids}.png')
                done.append(ids)
    return done


if __name__ == '__main__':
    print(extract(sys.argv[1]))


def objects(im, thr=12, min_area=3000):
    """Bounding boxes (x0, y0, w, h) of the drawn objects, as rows of [view A, view B, view C]."""
    from scipy import ndimage
    fg = cubes.foreground(im, thr)
    fg = ndimage.binary_opening(fg, iterations=2)
    lab, n = ndimage.label(fg)
    boxes = []
    for i, sl in enumerate(ndimage.find_objects(lab), 1):
        area = (lab[sl] == i).sum()
        w, h = sl[1].stop - sl[1].start, sl[0].stop - sl[0].start
        if area >= min_area and w > 80:
            boxes.append((sl[1].start, sl[0].start, w, h))
    # columns: the three largest x clusters (labels are narrower and on the left)
    boxes.sort(key=lambda b: b[0] + b[2] / 2)
    cols = []
    for b in boxes:
        cx = b[0] + b[2] / 2
        if not cols or cx - cols[-1][-1][0] - cols[-1][-1][2] / 2 > 80:
            cols.append([b])
        else:
            cols[-1].append(b)
    cols = cols[-3:]
    rows = []
    for col in cols:
        col.sort(key=lambda b: b[1])
    for r in range(max(len(c) for c in cols)):
        rows.append([c[r] if r < len(c) else None for c in cols])
    return rows


def top_of(im, box, size=cubes.HIRES):
    x0, y0, w, h = box
    a = w / 4
    v = cubes.vertices(x0, y0, w, w / 2 + 10, a)
    return cubes.unwarp(im, machines.top_quad(v, False), size)


def left_of(im, box, size=cubes.HIRES):
    x0, y0, w, h = box
    v = cubes.vertices(x0, y0, w, h, w / 4)
    return machines.unshade(cubes.unwarp(im, [v['UL'], v['C'], v['B'], v['LL']], size), machines.LEFT)


HANDMADE = {
    '03_tanks_handmade_multiblock/handmade_equipment_1_pots_crock_leach_saltpan.png':
        [('clay_pot', 'side'), ('iron_pot', 'side'), ('crock', 'side'), ('leach_barrel', 'side'), ('salt_pan', 'top')],
    '03_tanks_handmade_multiblock/handmade_equipment_2_rack_tray_fumigation_pump.png':
        [('drying_rack', 'top'), ('koji_tray', 'top'), ('fumigation_box', 'front'), ('hand_seawater_pump', 'side')],
}


def handmade(out_dir):
    out_dir = pathlib.Path(out_dir)
    done = []
    for sheet, rows in HANDMADE.items():
        im = cubes.load(REF / sheet)
        table = objects(im)
        if len(table) != len(rows):
            print('rows', sheet, len(table), len(rows), table)
        for row, (mid, part) in zip(table, rows):
            a_view, b_view, c_view = row
            if part == 'top':
                face = top_of(im, c_view or a_view)
            elif part == 'front':
                face = left_of(im, a_view)
            else:
                face = left_of(im, c_view or a_view)
            cubes.pixelate(face, N, 32).save(out_dir / f'{mid}.png')
            done.append(mid)
    return done


def finish(out_dir):
    """Tank textures the sheets do not show directly, and symmetric fixes."""
    out_dir = pathlib.Path(out_dir)
    side = np.asarray(Image.open(out_dir / 'metal_tank_side.png').convert('RGBA')).copy()
    ring = N // 8  # the frame round the metal tank's window
    frame = side.copy()
    frame[:, N - ring:] = side[:, :ring][:, ::-1]
    frame[N - ring:, :] = frame[:ring, :][::-1, :]
    frame[ring:N - ring, ring:N - ring, 3] = 0
    Image.fromarray(frame).save(out_dir / 'metal_tank_side.png')
    wood = np.asarray(Image.open(out_dir / 'wooden_tank_side.png').convert('RGB')).astype(float)
    Image.fromarray(np.clip(wood * 0.78, 0, 255).astype(np.uint8)).save(out_dir / 'wooden_tank_inner.png')
    Image.fromarray(np.clip(np.rot90(wood) * 0.68, 0, 255).astype(np.uint8)).save(out_dir / 'wooden_tank_bottom.png')
    for name in ('salt_pan',):
        a = np.asarray(Image.open(out_dir / f'{name}.png').convert('RGB')).copy()
        h = N // 2
        q = a[:h, :h]
        a[:h, h:] = q[:, ::-1]
        a[h:, :] = a[:h, :][::-1]
        Image.fromarray(a).save(out_dir / f'{name}.png')
