#!/usr/bin/env python3
"""Generates the item textures of every material and finished product, and the fluid textures (I33), from the
greyscale templates in tools/textures/templates/ (drawn by draw_templates.py).

Item textures: the template of the item's kind (appearance.py for chemicals, groups.py for the rest) tinted with its
colour, with the template's fixed layer (glass, caps, labels: not tinted) and, for engineered strains, the badge on top.
Fluid textures: the greyscale still and flowing frames of the three fluid kinds, copied to textures/block/ and tinted
at run time with the fluid colour (materials.json `texture` names the kind).

A texture is (re)written only when it is missing, still an old placeholder (tools/materials and tools/foods drew
them before I33), or unchanged since this script last wrote it (generated.json keeps the hashes); a texture drawn
by hand is kept and listed. --force rewrites all. --report <file.html> also writes a page that shows every item and
fluid with its kind, colour and the rule behind them.

Usage: python3 tools/textures/generate.py [--force] [--report out.html]
"""
import base64
import hashlib
import html
import io
import json
import pathlib
import sys

from PIL import Image

sys.dont_write_bytecode = True
HERE = pathlib.Path(__file__).resolve().parent
ROOT = HERE.parents[1]
sys.path.insert(0, str(HERE))
import groups  # noqa: E402
import legacy_placeholders as old  # noqa: E402

TEMPLATES = HERE / 'templates'
MANIFEST = HERE / 'generated.json'
RESOURCES = ROOT / 'src' / 'main' / 'resources'
ITEMS = RESOURCES / 'assets' / 'foodindustrial' / 'textures' / 'item'
BLOCKS = RESOURCES / 'assets' / 'foodindustrial' / 'textures' / 'block'
MATERIALS = RESOURCES / 'foodindustrial' / 'materials.json'
FOODS = RESOURCES / 'foodindustrial' / 'foods.json'
SKIP_FOODS = {'dried_apple', 'dried_berries'}  # drawn by hand for I12
# fluid kind -> texture names under textures/block (the liquid keeps the original names)
FLUID_TEXTURES = {'liquid': ('fluid_still', 'fluid_flowing'), 'viscous': ('fluid_viscous_still', 'fluid_viscous_flowing'),
                  'gas': ('fluid_gas_still', 'fluid_gas_flowing')}
BASE = 166 / 255  # template level 6: the item's own colour
SHADOW = 0.25     # darkest shade, as a share of the colour
LIGHT = 0.6       # brightest highlight, as a share of the way to white


def rgb(hex_colour):
    return tuple(int(hex_colour[i:i + 2], 16) for i in (1, 3, 5))


def tint(template, colour):
    """The template with its grey levels mapped to shades of `colour`: level 6 is the colour, darker levels shade
    it, lighter levels lighten it towards white."""
    grey = template.convert('LA')
    out = Image.new('RGBA', grey.size, (0, 0, 0, 0))
    for y in range(grey.height):
        for x in range(grey.width):
            value, alpha = grey.getpixel((x, y))
            if alpha == 0:
                continue
            t = value / 255
            if t <= BASE:
                k = SHADOW + (1 - SHADOW) * t / BASE
                pixel = tuple(round(c * k) for c in colour)
            else:
                h = (t - BASE) / (1 - BASE) * LIGHT
                pixel = tuple(round(c + (255 - c) * h) for c in colour)
            out.putpixel((x, y), pixel + (alpha,))
    return out


_LAYERS = {}


def layers(name):
    """(tinted template, fixed layer or None) of an item kind, or of a badge."""
    if name not in _LAYERS:
        fixed = TEMPLATES / f'item_{name}_fixed.png'
        _LAYERS[name] = (Image.open(TEMPLATES / f'item_{name}.png'),
                         Image.open(fixed).convert('RGBA') if fixed.exists() else None)
    return _LAYERS[name]


def item_image(look):
    template, fixed = layers(look['kind'])
    image = tint(template, rgb(look['color']))
    if fixed is not None:
        image.alpha_composite(fixed)
    if look.get('badge'):
        image.alpha_composite(Image.open(TEMPLATES / f'badge_{look["badge"]}.png').convert('RGBA'))
    return image


def png(image):
    buffer = io.BytesIO()
    image.save(buffer, format='PNG')
    return buffer.getvalue()


def sha(data):
    return hashlib.sha256(data).hexdigest()


def replaceable(path, placeholders, manifest, force):
    if force or not path.exists():
        return True
    if manifest.get(path.name) == sha(path.read_bytes()):
        return True
    return any(old.same(path, image) for image in placeholders())


def put(path, data, manifest):
    """Writes `data` when it differs; returns whether it wrote."""
    changed = not path.exists() or path.read_bytes() != data
    if changed:
        path.write_bytes(data)
    manifest[path.name] = sha(data)
    return changed


def main():
    force = '--force' in sys.argv
    report = sys.argv[sys.argv.index('--report') + 1] if '--report' in sys.argv else None
    manifest = json.loads(MANIFEST.read_text(encoding='utf-8')) if MANIFEST.exists() else {}
    materials = json.loads(MATERIALS.read_text(encoding='utf-8'))['materials']
    foods = [f for f in json.loads(FOODS.read_text(encoding='utf-8'))['foods'] if f['id'] not in SKIP_FOODS]
    written, kept, rows = 0, [], []
    items = [(m, groups.material(m), lambda m=m: old.material(m)) for m in materials]
    items += [(f, groups.product(f), lambda f=f: old.food(f)) for f in foods]
    for entry, look, placeholders in items:
        image = None
        if entry.get('state', 'solid') == 'solid':
            image = item_image(look)
            path = ITEMS / f"{entry['id']}.png"
            if replaceable(path, placeholders, manifest, force):
                written += put(path, png(image), manifest)
            else:
                kept.append(entry['id'])
        rows.append((entry, look, image))
    for kind, names in FLUID_TEXTURES.items():
        for frame, name in zip(('still', 'flowing'), names):
            path = BLOCKS / f'{name}.png'
            data = png(Image.open(TEMPLATES / f'fluid_{kind}_{frame}.png').convert('RGBA'))
            if replaceable(path, lambda f=frame: [old.fluid_texture(f == 'flowing')], manifest, force):
                written += put(path, data, manifest)
            else:
                kept.append(name)
    MANIFEST.write_text(json.dumps(dict(sorted(manifest.items())), indent=1) + '\n', encoding='utf-8')
    print(f'{written} textures written; kept (drawn by hand): {kept or "none"}')
    if report:
        write_report(pathlib.Path(report), rows)


# ---- review page ----

KIND_NAMES = {
    'powder': '粉末', 'crystal': '晶体', 'lump': '块状', 'resin': '树脂块', 'flakes': '片状', 'pellets': '颗粒', 'metal': '金属',
    'gel': '凝胶', 'paste': '浸膏', 'liquid': '液体', 'viscous': '黏稠液体', 'gas': '气体',
    'culture_bacteria': '细菌', 'culture_yeast': '酵母', 'culture_mold': '霉菌', 'enzyme_liquid': '液体酶制剂',
    'enzyme_powder': '酶粉', 'grain': '谷粒', 'beads': '树脂珠', 'mesh': '金属网',
    'leaf': '叶', 'flower': '花', 'seed': '籽与豆', 'root': '根与根茎', 'bark': '树皮与木', 'fruit': '果实', 'mushroom': '菌菇',
    'tissue': '动物组织', 'pieces': '小块',
    'bottle': '瓶装', 'soda_can': '易拉罐', 'tin_can': '罐头', 'jar': '酱罐', 'jar_chunks': '腌菜罐', 'cup': '杯装', 'bag': '袋装',
    'block': '块状（规整）', 'bar': '条块', 'noodles': '面条', 'bun': '包点', 'bread': '面包', 'cake': '蛋糕', 'biscuit': '饼干',
    'fried_stick': '条状炸物', 'candy': '糖果', 'stick_treat': '签上', 'sausage': '肠', 'meat': '肉块', 'poultry': '禽',
    'fish': '鱼', 'egg': '蛋'}
GROUP_NAMES = {'chemical': '化学品', 'flavor': '香料', 'culture': '菌种', 'enzyme': '酶', 'catalyst': '催化剂', 'consumable': '耗材',
               'raw': '原料', 'product': '成品'}


def data_url(image, scale=4):
    big = image.resize((16 * scale, 16 * scale), Image.NEAREST)
    return 'data:image/png;base64,' + base64.b64encode(png(big)).decode()


def template_image(kind):
    if kind in FLUID_TEXTURES:
        return Image.open(TEMPLATES / f'fluid_{kind}_still.png').convert('RGBA')
    template, fixed = layers(kind)
    image = template.convert('RGBA')
    if fixed is not None:
        image.alpha_composite(fixed)
    return image


def write_report(path, rows):
    fluid_frames = {kind: Image.open(TEMPLATES / f'fluid_{kind}_still.png') for kind in FLUID_TEXTURES}
    tree = {}
    for entry, look, image in rows:
        if image is None:
            image = tint(fluid_frames[look['kind']], rgb(look['color']))
        group = entry.get('category') if 'state' in entry else 'product'
        tree.setdefault(group, {}).setdefault(look['kind'], []).append((entry, look, image))
    parts, nav = [], []
    for group in GROUP_NAMES:
        if group not in tree:
            continue
        count = sum(len(v) for v in tree[group].values())
        nav.append(f'<a href="#{group}">{GROUP_NAMES[group]} {count}</a>')
        parts.append(f'<h2 id="{group}">{GROUP_NAMES[group]} <small>{count}</small></h2>')
        for kind in sorted(tree[group], key=lambda k: list(KIND_NAMES).index(k)):
            cards = []
            for entry, look, image in tree[group][kind]:
                note = entry.get('formula') or ''
                cards.append(
                    f'<figure title="{html.escape(entry["id"])}"><img src="{data_url(image)}" alt="">'
                    f'<figcaption><b>{html.escape(entry["name"]["zh"])}</b><span>{html.escape(note)}</span>'
                    f'<span class="sw" style="--c:{look["color"]}"></span><small>{look["color"]} · {html.escape(look["color_source"])}</small>'
                    f'<small>{html.escape(look["kind_reason"])}</small></figcaption></figure>')
            items = tree[group][kind]
            parts.append(f'<section><h3>{KIND_NAMES[kind]} <small>{kind} · {len(items)}</small></h3>'
                         f'<img class="tpl" src="{data_url(template_image(kind), 6)}" alt=""><div class="grid">{"".join(cards)}</div></section>')
    page = f'''<!doctype html><html lang="zh"><head><meta charset="utf-8"><meta name="viewport" content="width=device-width,initial-scale=1">
<title>物料与成品贴图</title><style>
:root{{--bg:#f6f5f2;--fg:#222;--card:#fff;--muted:#777;--line:#ddd}}
@media (prefers-color-scheme:dark){{:root{{--bg:#1c1c1e;--fg:#eee;--card:#2a2a2d;--muted:#aaa;--line:#444}}}}
body{{margin:0;padding:16px;background:var(--bg);color:var(--fg);font:14px/1.4 system-ui,sans-serif}}
h1{{font-size:20px}} h2{{font-size:19px;margin:36px 0 4px;border-bottom:1px solid var(--line)}} h3{{font-size:16px;margin:22px 0 8px}}
h2 small,h3 small{{color:var(--muted);font-weight:normal}}
nav{{display:flex;flex-wrap:wrap;gap:6px 14px;position:sticky;top:0;background:var(--bg);padding:6px 0;z-index:1}}
nav a{{color:inherit}}
.tpl{{image-rendering:pixelated;background:#5a8a5a;border-radius:6px}}
.grid{{display:grid;grid-template-columns:repeat(auto-fill,minmax(150px,1fr));gap:8px}}
figure{{margin:0;padding:8px;background:var(--card);border:1px solid var(--line);border-radius:8px;display:flex;gap:8px}}
figure img{{width:48px;height:48px;image-rendering:pixelated;flex:none;background:#8b8b8b;border-radius:4px}}
figcaption{{display:flex;flex-direction:column;min-width:0}} figcaption span,small{{color:var(--muted);font-size:12px;overflow-wrap:anywhere}}
.sw{{display:inline-block;width:28px;height:8px;border-radius:2px;background:var(--c);border:1px solid var(--line)}}
</style></head><body><h1>物料与成品贴图（I33，按类别与种类分组）</h1>
<p>每种贴图由该种类的模板（左上角，绿底）按颜色染色生成；容器、玻璃、标签等固定部分不染色。颜色来源与种类依据写在每格下方。</p>
<nav>{"".join(nav)}</nav>{"".join(parts)}</body></html>'''
    path.write_text(page, encoding='utf-8')
    print(f'report -> {path}')


if __name__ == '__main__':
    main()
