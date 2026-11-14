#!/usr/bin/env python3
"""Generates placeholder textures for machines, multiblock ports and members (I09, I14, I15).

Machines get block/<id>_front, _front_active, _side and _top; ports, casings and the air intake get
block/<id>; the condenser gets block/condenser_side and _top; machine modules, upgrades and the machine
frame get item/<id>. Existing files are overwritten only with --force, so hand-made textures survive.

Requires Pillow. Usage: python3 tools/machines/placeholder_textures.py [--force]
"""
import pathlib
import sys

from PIL import Image, ImageDraw

ROOT = pathlib.Path(__file__).resolve().parents[2]
BLOCKS = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures' / 'block'
ITEMS = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial' / 'textures' / 'item'
FORCE = '--force' in sys.argv

MACHINES = {
    'crusher': (128, 128, 136),
    # T1 workshop (I14)
    'stone_mill': (140, 140, 140),
    'screw_press': (150, 110, 70),
    'boiling_stove': (150, 80, 60),
    'steeping_vat': (130, 95, 60),
    'fermentation_vat': (120, 85, 60),
    'koji_room': (170, 140, 90),
    'earth_kiln': (125, 65, 45),
    'pot_still': (190, 110, 70),
    'crystallizing_tank': (160, 95, 75),
    'drying_room': (145, 95, 75),
    # T2 mechanical (I15)
    'solid_liquid_separator': (120, 130, 150),
    'stirred_reactor': (150, 150, 160),
    'evaporating_crystallizer': (140, 160, 175),
    'dryer': (170, 150, 120),
    'extraction_tank': (176, 120, 90),
    'absorption_tower': (130, 140, 135),
    'rotary_kiln': (150, 100, 80),
    'mixer': (135, 145, 165),
    # T3 chemical (I16)
    'distillation_column': (150, 160, 170),
    'high_pressure_reactor': (120, 125, 140),
    'electrolyzer': (90, 110, 150),
    'adsorption_column': (70, 70, 74),
    'electric_arc_furnace': (110, 80, 70),
    'gas_phase_reactor': (160, 90, 60),
    'spray_dryer': (200, 200, 205),
    # T4 bio and fine (I17)
    'sterile_fermenter': (215, 220, 225),
    'culture_bench': (225, 230, 235),
    'membrane_separator': (180, 200, 215),
}
PORTS = {'item_port': (176, 140, 80), 'fluid_port': (80, 120, 176), 'energy_port': (176, 60, 60)}
CASINGS = {'koji_room_wall': (170, 140, 90), 'kiln_brick': (125, 65, 45), 'drying_room_wall': (145, 95, 75),
           'tower_wall': (130, 140, 135), 'kiln_shell': (150, 100, 80),
           # I16, I17
           'chemical_tower_wall': (150, 160, 170), 'refractory_bricks': (170, 120, 90), 'sterile_vessel_wall': (215, 220, 225)}
# T2 items (I15): upgrades are boards with a coloured chip, modules a part with an accent colour
UPGRADES = {'speed_upgrade': (230, 190, 60), 'efficiency_upgrade': (200, 120, 80), 'parallel_upgrade': (150, 110, 70),
            'standardization_module': (180, 60, 60)}
MODULES = {'filter_plates': (230, 230, 220), 'centrifuge_drum': (190, 190, 200), 'corrosion_lining': (160, 170, 150),
           'vacuum_pump': (150, 200, 220), 'cryo_jacket': (120, 200, 240), 'uv_lamp': (170, 90, 230),
           'multi_effect': (210, 120, 70), 'chill_roll': (200, 230, 250), 'freeze_dryer': (140, 220, 250),
           'supercritical_unit': (220, 220, 240), 'lead_chamber': (110, 110, 120), 'emulsifying_head': (240, 240, 210),
           'internal_mixer': (90, 90, 100),
           # I16, I17
           'molecular_distillation': (200, 160, 240)}


def save(image, name, folder=BLOCKS):
    path = folder / f'{name}.png'
    if path.exists() and not FORCE:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)
    print('wrote', path.relative_to(ROOT))


def frame(color):
    image = Image.new('RGBA', (16, 16), color + (255,))
    draw = ImageDraw.Draw(image)
    dark = tuple(int(c * 0.6) for c in color) + (255,)
    light = tuple(min(255, int(c * 1.2)) for c in color) + (255,)
    draw.rectangle([0, 0, 15, 15], outline=dark)
    draw.line([1, 1, 14, 1], fill=light)
    draw.line([1, 1, 1, 14], fill=light)
    return image, draw, dark


for machine, color in MACHINES.items():
    side, draw, dark = frame(color)
    draw.rectangle([4, 4, 11, 11], outline=dark)
    save(side, f'{machine}_side')
    top, draw, dark = frame(color)
    for x in range(3, 13, 3):
        draw.line([x, 3, x, 12], fill=dark)
    save(top, f'{machine}_top')
    for active in (False, True):
        front, draw, dark = frame(color)
        draw.rectangle([3, 3, 12, 12], fill=(40, 40, 44, 255), outline=dark)
        glow = (255, 160, 40, 255) if active else (70, 70, 76, 255)
        draw.rectangle([5, 9, 10, 11], fill=glow)
        for x in (5, 8, 11):
            draw.line([x, 4, x - 1, 7], fill=(150, 150, 160, 255))
        save(front, f'{machine}_front_active' if active else f'{machine}_front')

for port, color in PORTS.items():
    image, draw, dark = frame(color)
    draw.rectangle([4, 4, 11, 11], fill=(30, 30, 34, 255), outline=dark)
    draw.rectangle([6, 6, 9, 9], fill=tuple(min(255, int(c * 1.3)) for c in color) + (255,))
    save(image, port)

for casing, color in CASINGS.items():
    image, draw, dark = frame(color)
    for y in (5, 10):
        draw.line([1, y, 14, y], fill=dark)
    draw.line([8, 1, 8, 4], fill=dark)
    draw.line([4, 6, 4, 9], fill=dark)
    draw.line([11, 6, 11, 9], fill=dark)
    draw.line([8, 11, 8, 14], fill=dark)
    save(image, casing)

copper = (196, 118, 80)
side, draw, dark = frame(copper)
for y in (4, 8, 12):
    draw.line([2, y, 13, y], fill=dark)
save(side, 'condenser_side')
top, draw, dark = frame(copper)
draw.ellipse([4, 4, 11, 11], fill=(60, 60, 64, 255), outline=dark)
save(top, 'condenser_top')

module = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
draw = ImageDraw.Draw(module)
draw.rectangle([4, 1, 11, 14], fill=(200, 230, 240, 160), outline=(120, 150, 160, 255))
draw.rectangle([5, 2, 10, 6], fill=(230, 190, 60, 220))
draw.rectangle([5, 7, 10, 13], fill=(140, 190, 230, 200))
draw.rectangle([6, 0, 9, 1], fill=copper + (255,))
draw.rectangle([6, 14, 9, 15], fill=copper + (255,))
save(module, 'essential_oil_separator', ITEMS)

iron = (176, 176, 184)
intake, draw, dark = frame(iron)
for x in (3, 6, 9, 12):
    draw.line([x, 2, x, 13], fill=(40, 40, 44, 255))
save(intake, 'air_intake')

machine_frame = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
draw = ImageDraw.Draw(machine_frame)
draw.rectangle([2, 2, 13, 13], outline=iron + (255,))
draw.rectangle([3, 3, 12, 12], outline=(110, 110, 118, 255))
draw.line([3, 3, 12, 12], fill=(110, 110, 118, 255))
draw.rectangle([7, 7, 8, 8], fill=(200, 40, 40, 255))
save(machine_frame, 'machine_frame', ITEMS)

for upgrade, color in UPGRADES.items():
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.rectangle([2, 3, 13, 12], fill=(40, 110, 60, 255), outline=(20, 60, 30, 255))
    draw.rectangle([5, 5, 10, 10], fill=color + (255,), outline=(30, 30, 30, 255))
    for x in (3, 6, 9, 12):
        draw.point([x, 13], fill=(220, 190, 80, 255))
    save(image, upgrade, ITEMS)

for module_id, color in MODULES.items():
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.rectangle([3, 2, 12, 13], fill=iron + (255,), outline=(90, 90, 96, 255))
    draw.rectangle([5, 4, 10, 11], fill=color + (255,), outline=(60, 60, 66, 255))
    draw.line([1, 7, 2, 7], fill=(90, 90, 96, 255))
    draw.line([13, 7, 14, 7], fill=(90, 90, 96, 255))
    save(image, module_id, ITEMS)

# I16, I17: frames, electrode, membrane, furnace tube
for frame_id, accent in (('chemical_frame', (230, 190, 60)), ('clean_frame', (120, 220, 230))):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.rectangle([2, 2, 13, 13], outline=iron + (255,))
    draw.rectangle([3, 3, 12, 12], outline=(110, 110, 118, 255))
    draw.line([3, 3, 12, 12], fill=(110, 110, 118, 255))
    draw.line([12, 3, 3, 12], fill=(110, 110, 118, 255))
    draw.rectangle([6, 6, 9, 9], fill=accent + (255,))
    save(image, frame_id, ITEMS)

electrode = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
draw = ImageDraw.Draw(electrode)
draw.rectangle([6, 1, 9, 14], fill=(60, 60, 66, 255), outline=(30, 30, 34, 255))
draw.line([7, 2, 7, 13], fill=(100, 100, 110, 255))
save(electrode, 'graphite_electrode', ITEMS)

membrane = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
draw = ImageDraw.Draw(membrane)
draw.rectangle([3, 2, 12, 13], fill=(230, 235, 240, 255), outline=(120, 130, 140, 255))
for y in (4, 7, 10):
    draw.line([4, y, 11, y], fill=(170, 190, 210, 255))
save(membrane, 'membrane_module', ITEMS)

tube, draw, dark = frame((170, 120, 90))
draw.rectangle([4, 0, 11, 15], fill=(150, 150, 156, 255), outline=(90, 90, 96, 255))
save(tube, 'furnace_tube_side')
top, draw, dark = frame((170, 120, 90))
draw.ellipse([3, 3, 12, 12], fill=(40, 40, 44, 255), outline=(150, 150, 156, 255))
save(top, 'furnace_tube_top')
