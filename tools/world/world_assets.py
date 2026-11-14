#!/usr/bin/env python3
"""Block states, block and item models, and placeholder textures for the I07 world blocks.

The files are the same on every node (vanilla's oak block states and models are identical on 1.20.1,
1.21.1 and 26.1.2, checked 2026-11-04), so they go to src/main/resources/assets/foodindustrial.
Models of cut-out blocks carry "render_type" (read by Forge and NeoForge before 26.1, ignored by 26.1,
which picks the layer from the texture). 26.1 item definitions (assets/<ns>/items) are written by data
generation (ModWorldData).

The rubber wood models copy vanilla's oak files, read from a Minecraft resources jar:

    python3 tools/world/world_assets.py <minecraft client or client-extra jar of 1.21.1> [--force]

Textures are only written when missing unless --force is given. Needs Pillow.
"""
import json
import pathlib
import random
import sys
import zipfile

from PIL import Image

ROOT = pathlib.Path(__file__).resolve().parents[2]
ASSETS = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial'
NS = 'foodindustrial'
CUTOUT = 'minecraft:cutout'

# id -> (texture base colour, speck colour)
ORES = {
    'gypsum_ore': ((125, 125, 125), (236, 230, 214)),
    'alunite_ore': ((125, 125, 125), (222, 178, 160)),
    'magnesite_ore': ((125, 125, 125), (240, 240, 232)),
    'apatite_ore': ((125, 125, 125), (70, 140, 170)),
    'sulfur_ore': ((125, 125, 125), (224, 208, 60)),
    'selenium_ore': ((125, 125, 125), (170, 60, 50)),  # I31
    'chromite_ore': ((125, 125, 125), (40, 40, 44)),  # I31
}
FULL_BLOCKS = {  # plain cube blocks: base colour, speck colour
    'rock_salt': ((214, 190, 190), (245, 236, 236)),
    'trona_ore': ((219, 207, 160), (246, 244, 236)),
    'nitratine': ((219, 207, 160), (238, 238, 246)),
    'nitre_soil': ((134, 96, 67), (232, 232, 224)),
}
CROPS = {  # produce id -> leaf colour, produce colour
    'stevia': ((70, 150, 60), (110, 180, 90)),
    'turmeric_rhizome': ((70, 140, 50), (214, 150, 40)),  # I12, 2026-11-10
    'chili_pepper': ((60, 140, 50), (200, 40, 30)),  # I12, 2026-11-10
    'mint': ((60, 170, 90), (120, 210, 140)),
    'rosemary': ((70, 110, 80), (140, 160, 200)),
    'indigo_plant': ((60, 130, 70), (190, 80, 120)),
    'paddy': ((110, 160, 60), (220, 200, 120)),  # 2026-11-12: was 'rice' (11 问题 43)
}
SEA_PLANTS = {'gelidium': (170, 40, 60), 'eucheuma': (150, 110, 60)}


def write_json(path, data):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(json.dumps(data, indent=2) + '\n', encoding='utf-8')


def blockstate(name, variants):
    write_json(ASSETS / 'blockstates' / f'{name}.json', {'variants': variants})


def model(name, parent, textures, render_type=None):
    data = {'parent': parent, 'textures': textures}
    if render_type:
        data['render_type'] = render_type
    write_json(ASSETS / 'models' / 'block' / f'{name}.json', data)


def item_model(name, parent=None, layer0=None):
    data = {'parent': parent} if parent else {'parent': 'minecraft:item/generated', 'textures': {'layer0': layer0}}
    write_json(ASSETS / 'models' / 'item' / f'{name}.json', data)


def simple_blocks():
    for name in list(ORES) + list(FULL_BLOCKS):
        blockstate(name, {'': {'model': f'{NS}:block/{name}'}})
        model(name, 'minecraft:block/cube_all', {'all': f'{NS}:block/{name}'})
        item_model(name, parent=f'{NS}:block/{name}')


def crops():
    for crop in CROPS:
        stages = {age: min(3, age // 2) if age < 7 else 3 for age in range(8)}
        stages[6] = 2
        blockstate(f'{crop}_crop', {f'age={age}': {'model': f'{NS}:block/{crop}_stage{stage}'} for age, stage in stages.items()})
        for stage in range(4):
            model(f'{crop}_stage{stage}', 'minecraft:block/crop', {'crop': f'{NS}:block/{crop}_stage{stage}'}, CUTOUT)
        blockstate(f'wild_{crop}', {'': {'model': f'{NS}:block/wild_{crop}'}})
        model(f'wild_{crop}', 'minecraft:block/cross', {'cross': f'{NS}:block/{crop}_stage3'}, CUTOUT)
        item_model(crop, layer0=f'{NS}:item/{crop}')


def bush_and_vine():
    blockstate('gardenia_bush', {f'age={age}': {'model': f'{NS}:block/gardenia_bush_stage{age}'} for age in range(4)})
    for age in range(4):
        model(f'gardenia_bush_stage{age}', 'minecraft:block/cross', {'cross': f'{NS}:block/gardenia_bush_stage{age}'}, CUTOUT)
    item_model('gardenia_fruit', layer0=f'{NS}:item/gardenia_fruit')
    rotation = {'south': 0, 'west': 90, 'north': 180, 'east': 270}
    variants = {}
    for age in range(3):
        for facing, y in sorted(rotation.items()):
            variant = {'model': f'{NS}:block/vanilla_stage{age}'}
            if y:
                variant['y'] = y
            variants[f'age={age},facing={facing}'] = variant
    blockstate('vanilla', variants)
    for age in range(3):
        texture = f'{NS}:block/vanilla_stage{age}'
        model(f'vanilla_stage{age}', f'minecraft:block/cocoa_stage{age}', {'cocoa': texture, 'particle': texture}, CUTOUT)
    item_model('vanilla_pod', layer0=f'{NS}:item/vanilla_pod')
    for plant in SEA_PLANTS:
        blockstate(plant, {'': {'model': f'{NS}:block/{plant}'}})
        model(plant, 'minecraft:block/template_seagrass', {'texture': f'{NS}:block/{plant}'}, CUTOUT)
        item_model(plant, layer0=f'{NS}:block/{plant}')


def rename(value):
    if isinstance(value, dict):
        return {k: rename(v) for k, v in value.items()}
    if isinstance(value, list):
        return [rename(v) for v in value]
    if isinstance(value, str):
        for old, new in (('minecraft:block/stripped_oak_', f'{NS}:block/stripped_rubber_'),
                         ('minecraft:block/oak_', f'{NS}:block/rubber_'),
                         ('minecraft:item/oak_', f'{NS}:item/rubber_'),
                         ('block/stripped_oak_', f'{NS}:block/stripped_rubber_'),
                         ('block/oak_', f'{NS}:block/rubber_'),
                         ('item/oak_', f'{NS}:item/rubber_')):
            if value.startswith(old):
                return new + value[len(old):]
    return value


WOOD = ['oak_log', 'oak_wood', 'stripped_oak_log', 'stripped_oak_wood', 'oak_planks', 'oak_stairs', 'oak_slab',
        'oak_fence', 'oak_fence_gate', 'oak_door', 'oak_trapdoor', 'oak_pressure_plate', 'oak_button',
        'oak_leaves', 'oak_sapling']
CUTOUT_WOOD = ('door', 'trapdoor', 'sapling')


def wood(jar_path):
    jar = zipfile.ZipFile(jar_path)
    read = lambda path: json.loads(jar.read(f'assets/minecraft/{path}.json'))
    for block in WOOD:
        ours = block.replace('oak', 'rubber')
        state = rename(read(f'blockstates/{block}'))
        write_json(ASSETS / 'blockstates' / f'{ours}.json', state)
        models = set()

        def collect(node):
            if isinstance(node, dict):
                if 'model' in node and isinstance(node['model'], str):
                    models.add(node['model'])
                for v in node.values():
                    collect(v)
            elif isinstance(node, list):
                for v in node:
                    collect(v)
        collect(state)
        for name in sorted(models):
            vanilla = name.replace(f'{NS}:block/stripped_rubber_', 'block/stripped_oak_').replace(f'{NS}:block/rubber_', 'block/oak_')
            data = rename(read(f'models/{vanilla}'))
            if any(word in ours for word in CUTOUT_WOOD):
                data['render_type'] = CUTOUT
            if ours == 'rubber_leaves':
                data['render_type'] = 'minecraft:cutout_mipped'
            write_json(ASSETS / 'models' / 'block' / f'{name.split("/")[-1]}.json', data)
        item = rename(read(f'models/item/{block}'))
        write_json(ASSETS / 'models' / 'item' / f'{ours}.json', item)
        parent = item.get('parent', '')
        if parent.startswith(f'{NS}:block/') and parent not in models:  # inventory models (fence, button)
            vanilla = parent.replace(f'{NS}:block/rubber_', 'block/oak_')
            write_json(ASSETS / 'models' / 'block' / f'{parent.split("/")[-1]}.json', rename(read(f'models/{vanilla}')))


# ---------------------------------------------------------------- textures

def save(image, relative):
    path = ASSETS / 'textures' / f'{relative}.png'
    if path.exists() and '--force' not in sys.argv:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


def noise(base, speck, seed, density=0.18, size=2):
    rnd = random.Random(seed)
    img = Image.new('RGBA', (16, 16))
    for x in range(16):
        for y in range(16):
            d = rnd.randint(-12, 12)
            img.putpixel((x, y), tuple(max(0, min(255, c + d)) for c in base) + (255,))
    for _ in range(int(256 * density / (size * size))):
        x, y = rnd.randrange(16 - size + 1), rnd.randrange(16 - size + 1)
        for dx in range(size):
            for dy in range(size):
                img.putpixel((x + dx, y + dy), speck + (255,))
    return img


def plant(colour, height, seed, flower=None):
    rnd = random.Random(seed)
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for stem in range(4):
        x = 2 + stem * 4 + rnd.randint(-1, 1)
        top = 16 - height + rnd.randint(0, 2)
        for y in range(max(0, top), 16):
            img.putpixel((x, y), colour + (255,))
            if y % 3 == 0 and 0 < x < 15:
                img.putpixel((x + rnd.choice((-1, 1)), y), colour + (255,))
        if flower and top < 14:
            img.putpixel((x, max(0, top)), flower + (255,))
            if x < 15:
                img.putpixel((x + 1, max(0, top)), flower + (255,))
    return img


def blob(colour, seed):
    rnd = random.Random(seed)
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for _ in range(40):
        x, y = rnd.randint(4, 11), rnd.randint(3, 12)
        img.putpixel((x, y), tuple(max(0, min(255, c + rnd.randint(-20, 20))) for c in colour) + (255,))
    return img


def textures():
    for name, (base, speck) in {**ORES, **FULL_BLOCKS}.items():
        save(noise(base, speck, name), f'block/{name}')
    for crop, (leaf, produce) in CROPS.items():
        for stage in range(4):
            save(plant(leaf, 4 + stage * 3, f'{crop}{stage}', produce if stage == 3 else None), f'block/{crop}_stage{stage}')
        save(blob(produce, crop), f'item/{crop}')
    for stage in range(4):
        save(plant((50, 110, 50), 5 + stage * 3, f'gardenia{stage}', (240, 160, 40) if stage >= 2 else None), f'block/gardenia_bush_stage{stage}')
    save(blob((240, 150, 40), 'gardenia_fruit'), 'item/gardenia_fruit')
    for stage in range(3):
        green = (90 + stage * 30, 140 - stage * 20, 60)
        save(noise(green, (60, 40, 20), f'vanilla{stage}', 0.05, 1), f'block/vanilla_stage{stage}')
    save(blob((60, 40, 25), 'vanilla_pod'), 'item/vanilla_pod')
    for plant_id, colour in SEA_PLANTS.items():
        save(plant(colour, 13, plant_id), f'block/{plant_id}')
    bark, wood_colour, stripped = (92, 74, 52), (196, 160, 110), (176, 140, 92)
    save(noise(bark, (70, 56, 40), 'rubber_log', 0.2, 1), 'block/rubber_log')
    save(noise(wood_colour, (150, 120, 80), 'rubber_log_top', 0.1, 1), 'block/rubber_log_top')
    save(noise(stripped, (150, 118, 76), 'stripped_rubber_log', 0.1, 1), 'block/stripped_rubber_log')
    save(noise(wood_colour, (150, 120, 80), 'stripped_rubber_log_top', 0.1, 1), 'block/stripped_rubber_log_top')
    save(noise(wood_colour, (160, 128, 86), 'rubber_planks', 0.08, 1), 'block/rubber_planks')
    leaves = noise((60, 120, 50), (40, 90, 35), 'rubber_leaves', 0.2, 1)
    rnd = random.Random('leaf_holes')
    for _ in range(30):
        leaves.putpixel((rnd.randrange(16), rnd.randrange(16)), (0, 0, 0, 0))
    save(leaves, 'block/rubber_leaves')
    save(plant((70, 110, 40), 12, 'rubber_sapling'), 'block/rubber_sapling')
    for part in ('top', 'bottom'):
        save(noise(wood_colour, (150, 120, 80), f'rubber_door_{part}', 0.08, 1), f'block/rubber_door_{part}')
    trapdoor = noise(wood_colour, (150, 120, 80), 'rubber_trapdoor', 0.08, 1)
    for x in range(4, 12):
        for y in range(4, 12):
            if (x + y) % 4 == 0:
                trapdoor.putpixel((x, y), (0, 0, 0, 0))
    save(trapdoor, 'block/rubber_trapdoor')
    save(noise(wood_colour, (150, 120, 80), 'rubber_door_item', 0.08, 1), 'item/rubber_door')


def main():
    if len(sys.argv) < 2 or sys.argv[1].startswith('--'):
        sys.exit(__doc__)
    simple_blocks()
    crops()
    bush_and_vine()
    wood(sys.argv[1])
    textures()
    print('assets written to', ASSETS.relative_to(ROOT))


if __name__ == '__main__':
    main()
