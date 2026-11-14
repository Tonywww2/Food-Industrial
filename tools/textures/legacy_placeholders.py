"""The placeholder textures drawn before I33 (by tools/materials/placeholder_textures.py and
tools/foods/placeholder_textures.py, now removed), kept so that generate.py can tell an untouched placeholder,
which it replaces, from a texture drawn by hand, which it keeps."""
import hashlib

from PIL import Image, ImageDraw


def hashed_color(key, lightness=0.75):
    digest = hashlib.md5(key.encode()).digest()
    base = [digest[0], digest[1], digest[2]]
    return tuple(int(lightness * 255 + (1 - lightness) * c) for c in base)


def darker(color, factor=0.6):
    return tuple(int(c * factor) for c in color)


def same(path, image):
    with Image.open(path) as existing:
        return existing.convert('RGBA').tobytes() == image.convert('RGBA').tobytes()


def powder(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.polygon([(2, 13), (8, 4), (14, 13)], fill=color + (255,), outline=darker(color) + (255,))
    draw.line([(2, 13), (14, 13)], fill=darker(color, 0.5) + (255,))
    return image


def pellets(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    for x, y in ((3, 8), (8, 4), (9, 10)):
        draw.ellipse([x, y, x + 4, y + 4], fill=color + (255,), outline=darker(color) + (255,))
    return image


def dish(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.ellipse([1, 3, 14, 13], fill=(220, 230, 235, 255), outline=(120, 130, 140, 255))
    for x, y in ((4, 6), (8, 8), (10, 5), (6, 9)):
        draw.ellipse([x, y, x + 2, y + 2], fill=darker(color, 0.8) + (255,))
    return image


def vial(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.rectangle([6, 1, 9, 3], fill=(150, 110, 80, 255))
    draw.rectangle([5, 4, 10, 14], fill=(225, 235, 240, 255), outline=(120, 130, 140, 255))
    draw.rectangle([6, 8, 9, 13], fill=color + (255,))
    return image


def lump(color):
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    draw = ImageDraw.Draw(image)
    draw.ellipse([2, 4, 14, 13], fill=color + (255,), outline=darker(color) + (255,))
    return image


def fluid_texture(flowing):
    image = Image.new('RGBA', (16, 16))
    for y in range(16):
        for x in range(16):
            wave = ((x + (y if flowing else 0) * 2) % 8) / 8
            value = int(205 + 40 * wave)
            image.putpixel((x, y), (value, value, value, 255))
    return image


MATERIAL_SHAPES = {'chemical': powder, 'catalyst': pellets, 'culture': dish, 'enzyme': vial, 'raw': lump}


def rgb(hex_colour):
    return tuple(int(hex_colour[i:i + 2], 16) for i in (1, 3, 5))


def material(m):
    """The images the placeholder generator may have drawn for material `m` (any shape, hashed or own colour)."""
    colours = [hashed_color(m['id'])] + ([rgb(m['color'])] if m.get('color') else [])
    return [shape(colour) for shape in MATERIAL_SHAPES.values() for colour in colours]


# ---- finished products (I21) ----

def color(key, lightness=0.45):
    digest = hashlib.md5(key.encode()).digest()
    return tuple(int(lightness * 255 + (1 - lightness) * c) for c in digest[:3])


def dark(c, f=0.55):
    return tuple(int(v * f) for v in c)


def light(c, f=1.25):
    return tuple(min(255, int(v * f)) for v in c)


def a(c):
    return c + (255,)


def cup(d, c):  # dairy: a jar with a lid
    d.rectangle([4, 5, 11, 14], fill=a((236, 232, 220)), outline=a((150, 146, 140)))
    d.rectangle([5, 8, 10, 13], fill=a(c))
    d.rectangle([3, 3, 12, 5], fill=a(dark(c)), outline=a(dark(c, 0.35)))


def oil(d, c):  # fats: a squat bottle
    d.rectangle([6, 2, 9, 4], fill=a((120, 90, 60)))
    d.polygon([(5, 5), (10, 5), (12, 8), (12, 14), (3, 14), (3, 8)], fill=a(c), outline=a(dark(c)))
    d.line([(5, 9), (5, 12)], fill=a(light(c, 1.4)))


def frozen(d, c):  # ice pop on a stick
    d.rectangle([7, 11, 8, 15], fill=a((196, 160, 110)))
    d.rounded_rectangle([4, 1, 11, 11], radius=3, fill=a(c), outline=a(dark(c)))
    d.line([(6, 3), (6, 8)], fill=a(light(c, 1.4)))


def jar(d, c):  # fruit and vegetable products: a glass jar
    d.rectangle([4, 2, 11, 3], fill=a((170, 170, 170)))
    d.rectangle([3, 4, 12, 14], fill=a((210, 230, 235)), outline=a((120, 140, 150)))
    d.rectangle([4, 7, 11, 13], fill=a(c))


def candy(d, c):  # confectionery: a wrapped sweet
    d.polygon([(1, 5), (4, 7), (4, 9), (1, 11)], fill=a(light(c)))
    d.polygon([(15, 5), (12, 7), (12, 9), (15, 11)], fill=a(light(c)))
    d.ellipse([4, 5, 12, 11], fill=a(c), outline=a(dark(c)))


def bowl(d, c):  # cereals: a bowl
    d.pieslice([2, 2, 14, 14], 0, 180, fill=a((240, 240, 236)), outline=a((140, 140, 140)))
    d.ellipse([3, 6, 13, 10], fill=a(c), outline=a(dark(c)))


def loaf(d, c):  # bakery: a loaf
    d.rounded_rectangle([2, 5, 14, 13], radius=4, fill=a(c), outline=a(dark(c)))
    for x in (5, 8, 11):
        d.line([(x, 7), (x + 1, 9)], fill=a(light(c, 1.3)))


def meat(d, c):  # meat: a slab with fat
    d.polygon([(2, 6), (10, 3), (14, 7), (12, 13), (3, 12)], fill=a(c), outline=a(dark(c)))
    d.line([(4, 8), (11, 6)], fill=a((240, 230, 220)))


def fish(d, c):  # aquatic: a fish
    d.ellipse([2, 5, 11, 11], fill=a(c), outline=a(dark(c)))
    d.polygon([(10, 8), (14, 5), (14, 11)], fill=a(dark(c, 0.8)))
    d.point((4, 7), fill=a((20, 20, 20)))


def egg(d, c):  # eggs
    d.ellipse([4, 2, 12, 14], fill=a(c), outline=a(dark(c)))
    d.ellipse([6, 4, 8, 7], fill=a(light(c, 1.3)))


def pile(d, c):  # sweeteners: a heap
    d.polygon([(2, 13), (8, 5), (14, 13)], fill=a(c), outline=a(dark(c)))
    d.line([(2, 13), (14, 13)], fill=a(dark(c, 0.4)))


def sauce(d, c):  # condiments: a sauce bottle
    d.rectangle([7, 1, 8, 4], fill=a((60, 60, 60)))
    d.polygon([(6, 4), (9, 4), (11, 7), (11, 14), (4, 14), (4, 7)], fill=a(c), outline=a(dark(c)))
    d.rectangle([5, 9, 10, 11], fill=a((236, 232, 220)))


def tin(d, c):  # special dietary: a tin
    d.rectangle([4, 3, 11, 14], fill=a(c), outline=a(dark(c)))
    d.rectangle([4, 3, 11, 4], fill=a((200, 200, 200)))
    d.rectangle([5, 7, 10, 10], fill=a((240, 240, 240)))


def can(d, c):  # beverages: a can with a straw
    d.line([(10, 1), (9, 5)], fill=a((230, 60, 60)))
    d.rectangle([4, 4, 11, 14], fill=a(c), outline=a(dark(c)))
    d.rectangle([5, 6, 6, 12], fill=a(light(c, 1.4)))


def wine(d, c):  # alcoholic drinks: a wine bottle
    d.rectangle([7, 1, 8, 5], fill=a(dark(c)))
    d.rounded_rectangle([5, 5, 10, 14], radius=2, fill=a(c), outline=a(dark(c)))
    d.rectangle([6, 9, 9, 11], fill=a((236, 228, 200)))


def box(d, c):  # other: a snack bag
    d.polygon([(4, 2), (12, 2), (13, 14), (3, 14)], fill=a(c), outline=a(dark(c)))
    d.line([(4, 4), (12, 4)], fill=a(dark(c)))
    d.ellipse([6, 7, 10, 11], fill=a(light(c, 1.3)))


FOOD_SHAPES = {'dairy': cup, 'fats_oils': oil, 'frozen_desserts': frozen, 'fruits_vegetables': jar,
               'cocoa_confectionery': candy, 'cereals': bowl, 'bakery': loaf, 'meat': meat, 'aquatic': fish,
               'eggs': egg, 'sweeteners': pile, 'condiments': sauce, 'special_dietary': tin, 'beverages': can,
               'alcoholic': wine, 'other': box}



def food(f):
    """The placeholder the generator drew for finished product `f`."""
    image = Image.new('RGBA', (16, 16), (0, 0, 0, 0))
    FOOD_SHAPES[f['category']](ImageDraw.Draw(image), color(f['id']))
    return [image]
