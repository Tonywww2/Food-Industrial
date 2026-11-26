#!/usr/bin/env python3
"""Block states, block and item models, and placeholder textures of the I44 plants (tools/world/sources.py).

Like tools/world/world_assets.py the files are the same on every node and go to src/main/resources/assets; cut-out
models carry "render_type" (Forge and NeoForge before 26.1). 26.1 item definitions are written by data generation
(world.source.SourceData). Textures are procedural placeholders in the species' colours, written only when missing
(--force rewrites them); the new raw items use the material templates of tools/textures (I33).

The animals' textures (civet on the ocelot's body, beaver on the fox's) follow the UV layout of the vanilla texture
they replace: only the vanilla file's opaque pixels are read (which parts of the sheet are used), never its colours.
Give a 26.1 client jar (it also has the baby textures) to write them:

Usage: python3 tools/world/source_assets.py [--force] [--jar <minecraft 26.1 client jar>]   (after tools/world/sources.py)
"""
import json
import pathlib
import random
import sys

from PIL import Image

sys.dont_write_bytecode = True
ROOT = pathlib.Path(__file__).resolve().parents[2]
sys.path.insert(0, str(ROOT / 'tools' / 'textures'))
import generate  # noqa: E402  (material item templates, I33)

ASSETS = ROOT / 'src' / 'main' / 'resources' / 'assets' / 'foodindustrial'
DATA = ROOT / 'src' / 'main' / 'resources' / 'foodindustrial' / 'world_sources.json'
NS = 'foodindustrial'
CUTOUT = 'minecraft:cutout'
MIPPED = 'minecraft:cutout_mipped'
FORCE = '--force' in sys.argv
STEM = (96, 72, 44)


def rgb(colour):
    return tuple(int(colour[i:i + 2], 16) for i in (1, 3, 5))


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


def save(image, relative):
    path = ASSETS / 'textures' / f'{relative}.png'
    if path.exists() and not FORCE:
        return
    path.parent.mkdir(parents=True, exist_ok=True)
    image.save(path)


# ------------------------------------------------------------------ drawing


def shade(colour, rnd, spread=18):
    return tuple(max(0, min(255, c + rnd.randint(-spread, spread))) for c in colour) + (255,)


def put(img, x, y, colour):
    if 0 <= x < 16 and 0 <= y < 16:
        img.putpixel((x, y), colour)


def herb(leaf, height, seed, dots=None, stems=4):
    """Upright stems with leaf pairs; `dots` (colour) on the tips when flowering or fruiting."""
    rnd = random.Random(seed)
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for stem in range(stems):
        x = 2 + stem * (12 // max(1, stems - 1)) + rnd.randint(-1, 1)
        x = max(1, min(14, x))
        top = max(1, 16 - height + rnd.randint(0, 2))
        for y in range(top, 16):
            put(img, x, y, shade(leaf, rnd, 10))
            if (y - top) % 3 == 1:
                put(img, x - 1, y, shade(leaf, rnd))
                put(img, x + 1, y - 1, shade(leaf, rnd))
        if dots and top < 15:
            colour = dots + (255,)
            for dx, dy in ((0, -1), (-1, 0), (1, 0), (0, 0)):
                put(img, x + dx, top + dy, colour)
    return img


def bush(leaf, stage, seed, dots=None):
    """A rounded bush that fills more of the tile as it grows; `dots` when in flower or fruit."""
    rnd = random.Random(seed)
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    radius = 3 + stage * 1.5
    cx, cy = 7.5, 15.5 - radius * 0.8
    for x in range(16):
        for y in range(16):
            d = ((x - cx) ** 2 + ((y - cy) * 1.2) ** 2) ** 0.5
            if d <= radius and rnd.random() > 0.12:
                put(img, x, y, shade(leaf, rnd))
    for y in range(13, 16):
        put(img, 7, y, STEM + (255,))
    if dots:
        for _ in range(4 + stage * 2):
            x, y = int(cx + rnd.uniform(-radius, radius) * 0.8), int(cy + rnd.uniform(-radius, radius) * 0.7)
            if img.getpixel((max(0, min(15, x)), max(0, min(15, y))))[3]:
                put(img, x, y, dots + (255,))
                put(img, x + 1, y, dots + (255,))
    return img


def sapling(leaf, seed):
    rnd = random.Random(seed)
    img = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    for y in range(8, 16):
        put(img, 7, y, STEM + (255,))
        put(img, 8, y, STEM + (255,))
    for x in range(16):
        for y in range(16):
            d = ((x - 7.5) ** 2 + (y - 6) ** 2) ** 0.5
            if d <= 4.5 and rnd.random() > 0.15:
                put(img, x, y, shade(leaf, rnd))
    return img


def leaves(leaf, seed, dots=None, size=1):
    rnd = random.Random(seed)
    img = Image.new('RGBA', (16, 16))
    for x in range(16):
        for y in range(16):
            img.putpixel((x, y), shade(leaf, rnd, 22))
    holes = random.Random(seed + 'holes')
    for _ in range(28):
        img.putpixel((holes.randrange(16), holes.randrange(16)), (0, 0, 0, 0))
    if dots:
        spots = random.Random(seed + 'dots')
        for _ in range(7):
            x, y = spots.randrange(15), spots.randrange(15)
            for dx in range(size):
                for dy in range(size):
                    put(img, x + dx, y + dy, dots + (255,))
    return img


def log_side(bark, seed):
    rnd = random.Random(seed)
    img = Image.new('RGBA', (16, 16))
    for x in range(16):
        streak = rnd.randint(-14, 14)
        for y in range(16):
            img.putpixel((x, y), tuple(max(0, min(255, c + streak + rnd.randint(-8, 8))) for c in bark) + (255,))
    return img


def log_top(bark, seed):
    rnd = random.Random(seed)
    wood = tuple(min(255, c + 70) for c in bark)
    img = Image.new('RGBA', (16, 16))
    for x in range(16):
        for y in range(16):
            d = max(abs(x - 7.5), abs(y - 7.5))
            colour = bark if d > 6.5 else tuple(max(0, c - (18 if int(d) % 2 else 0)) for c in wood)
            img.putpixel((x, y), tuple(max(0, min(255, c + rnd.randint(-6, 6))) for c in colour) + (255,))
    return img


# ------------------------------------------------------------------ blocks

CROP_STAGES = {0: 0, 1: 0, 2: 1, 3: 1, 4: 2, 5: 2, 6: 2, 7: 3}


def crops(data):
    for crop in data['crops']:
        cid, leaf, produce = crop['id'], rgb(crop['leaf']), rgb(crop['colour'])
        blockstate(f'{cid}_crop', {f'age={age}': {'model': f'{NS}:block/{cid}_crop_stage{stage}'} for age, stage in CROP_STAGES.items()})
        for stage in range(4):
            model(f'{cid}_crop_stage{stage}', 'minecraft:block/crop', {'crop': f'{NS}:block/{cid}_crop_stage{stage}'}, CUTOUT)
            save(herb(leaf, 4 + stage * 3, f'{cid}{stage}', produce if stage == 3 else None), f'block/{cid}_crop_stage{stage}')
        if crop['wild']:
            blockstate(f'wild_{cid}', {'': {'model': f'{NS}:block/wild_{cid}'}})
            model(f'wild_{cid}', 'minecraft:block/cross', {'cross': f'{NS}:block/{cid}_crop_stage3'}, CUTOUT)


def wild_herbs(data):
    for wild in data['wild']:
        name, leaf, produce = 'wild_' + wild['id'], rgb(wild['leaf']), rgb(wild['colour'])
        blockstate(name, {f'age={age}': {'model': f'{NS}:block/{name}_stage{age}'} for age in range(4)})
        for stage in range(4):
            model(f'{name}_stage{stage}', 'minecraft:block/cross', {'cross': f'{NS}:block/{name}_stage{stage}'}, CUTOUT)
            save(herb(leaf, 3 + stage * 3, f'{name}{stage}', produce if stage == 3 else None, stems=3), f'block/{name}_stage{stage}')
        item_model(name, layer0=f'{NS}:block/{name}_stage3')


def trees(data):
    for tree in data['trees']:
        tid, leaf = tree['id'], rgb(tree['leaves'])
        bloom, fruit = rgb(tree['bloom']), rgb(tree['colour'])
        flowers, fruits = bool(tree['flower']), bool(tree['fruit'])
        if tree['form'] == 'bush':
            name = f'{tid}_bush'
            blockstate(name, {f'age={age}': {'model': f'{NS}:block/{name}_stage{age}'} for age in range(4)})
            for stage in range(4):
                dots = None
                if stage == 2 and flowers and fruits:
                    dots = bloom
                elif stage == 3:
                    dots = fruit if fruits else bloom if flowers else None
                model(f'{name}_stage{stage}', 'minecraft:block/cross', {'cross': f'{NS}:block/{name}_stage{stage}'}, CUTOUT)
                save(bush(leaf, stage, f'{name}{stage}', dots), f'block/{name}_stage{stage}')
            item_model(name, layer0=f'{NS}:block/{name}_stage3')
            continue
        blockstate(f'{tid}_sapling', {'': {'model': f'{NS}:block/{tid}_sapling'}})
        model(f'{tid}_sapling', 'minecraft:block/cross', {'cross': f'{NS}:block/{tid}_sapling'}, CUTOUT)
        item_model(f'{tid}_sapling', layer0=f'{NS}:block/{tid}_sapling')
        save(sapling(leaf, f'{tid}_sapling'), f'block/{tid}_sapling')
        looks = {age: f'{tid}_leaves' for age in range(4)}
        if flowers and fruits:
            looks[2], looks[3] = f'{tid}_leaves_flowering', f'{tid}_leaves_fruiting'
        elif flowers:
            looks[3] = f'{tid}_leaves_flowering'
        elif fruits:
            looks[3] = f'{tid}_leaves_fruiting'
        blockstate(f'{tid}_leaves', {f'age={age}': {'model': f'{NS}:block/{look}'} for age, look in looks.items()})
        for look in sorted(set(looks.values())):
            model(look, 'minecraft:block/leaves', {'all': f'{NS}:block/{look}'}, MIPPED)
        save(leaves(leaf, f'{tid}_leaves'), f'block/{tid}_leaves')
        if flowers:
            save(leaves(leaf, f'{tid}_leaves', bloom), f'block/{tid}_leaves_flowering')
        if fruits:
            save(leaves(leaf, f'{tid}_leaves', fruit, 2), f'block/{tid}_leaves_fruiting')
        item_model(f'{tid}_leaves', parent=f'{NS}:block/{tid}_leaves')
        if tree['trunk'] == 'own':
            name, bark = f'{tid}_log', rgb(tree['bark'])
            blockstate(name, {'axis=x': {'model': f'{NS}:block/{name}_horizontal', 'x': 90, 'y': 90},
                              'axis=y': {'model': f'{NS}:block/{name}'},
                              'axis=z': {'model': f'{NS}:block/{name}_horizontal', 'x': 90}})
            textures = {'end': f'{NS}:block/{name}_top', 'side': f'{NS}:block/{name}'}
            model(name, 'minecraft:block/cube_column', textures)
            model(f'{name}_horizontal', 'minecraft:block/cube_column_horizontal', textures)
            item_model(name, parent=f'{NS}:block/{name}')
            save(log_side(bark, name), f'block/{name}')
            save(log_top(bark, name + '_top'), f'block/{name}_top')


def new_items(data):
    for iid, item in data['items'].items():
        item_model(iid, layer0=f'{NS}:item/{iid}')
        path = ASSETS / 'textures' / 'item' / f'{iid}.png'
        if path.exists() and not FORCE:
            continue
        path.parent.mkdir(parents=True, exist_ok=True)
        generate.item_image({'kind': item['texture'], 'color': item['colour']}).save(path)


def nest():
    """The swiftlet nest (燕巢) hanging on the wall it faces: a small box at the north side, turned by facing."""
    rotation = {'north': 0, 'east': 90, 'south': 180, 'west': 270}
    variants = {}
    for facing, y in rotation.items():
        variant = {'model': f'{NS}:block/swiftlet_nest'}
        if y:
            variant['y'] = y
        variants[f'facing={facing}'] = variant
    blockstate('swiftlet_nest', variants)
    texture = f'{NS}:block/swiftlet_nest'
    faces = {face: {'texture': '#nest'} for face in ('north', 'south', 'east', 'west', 'up', 'down')}
    data = {'parent': 'minecraft:block/block', 'textures': {'nest': texture, 'particle': texture},
            'elements': [{'from': [3, 5, 0], 'to': [13, 11, 6], 'faces': faces}]}
    write_json(ASSETS / 'models' / 'block' / 'swiftlet_nest.json', data)
    item_model('swiftlet_nest', parent=f'{NS}:block/swiftlet_nest')
    rnd = random.Random('swiftlet_nest')
    img = Image.new('RGBA', (16, 16))
    for x in range(16):
        for y in range(16):
            light = 18 if (x + 2 * y) % 5 == 0 else 0
            img.putpixel((x, y), tuple(min(255, c + light + rnd.randint(-10, 10)) for c in (222, 210, 182)) + (255,))
    save(img, 'block/swiftlet_nest')


BODIES = {'OCELOT': 'cat/ocelot', 'FOX': 'fox/fox', 'CHICKEN': 'chicken/chicken_temperate'}  # 26.1 paths; the 1.20.1 chicken.png has the same layout


def animals(data, jar_path):
    import io
    import zipfile
    jar = zipfile.ZipFile(jar_path)
    for animal in data['animals']:
        base, marks = rgb(animal['colour']), rgb(animal['marks'])
        for suffix in ('', '_baby'):
            name = f'assets/minecraft/textures/entity/{BODIES[animal["body"]]}{suffix}.png'
            try:
                mask = Image.open(io.BytesIO(jar.read(name))).convert('RGBA')
            except KeyError:
                continue
            rnd = random.Random(animal['id'] + suffix)
            img = Image.new('RGBA', mask.size, (0, 0, 0, 0))
            for x in range(mask.width):
                for y in range(mask.height):
                    if mask.getpixel((x, y))[3] == 0:
                        continue
                    colour = marks if rnd.random() < 0.12 else base
                    img.putpixel((x, y), shade(colour, rnd, 14))
            save(img, f'entity/{animal["id"]}{suffix}')


def main():
    data = json.loads(DATA.read_text(encoding='utf-8'))
    crops(data)
    wild_herbs(data)
    trees(data)
    new_items(data)
    nest()
    if '--jar' in sys.argv:
        animals(data, sys.argv[sys.argv.index('--jar') + 1])
    print('I44 plant assets written to', ASSETS.relative_to(ROOT))


if __name__ == '__main__':
    main()
