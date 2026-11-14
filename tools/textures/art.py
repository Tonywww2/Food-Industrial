"""Pixel art of the second batch of templates (I33): cultures, enzymes, flavour pastes, finished products, raw
materials and catalysts.

A template is a 16x16 grid of characters: '.' empty, '1'-'9' grey levels of the tinted layer (outline 1, base 6,
highlight 9), letters fixed colours of the fixed layer (glass, lids, labels, sticks; see FIXED). Containers and
details are written as grids; round or long shapes are drawn with the helpers below (lit from the upper left).
draw_templates.py renders each grid to item_<kind>.png (tinted layer) and item_<kind>_fixed.png (fixed layer).
"""
import math

FIXED = {
    'k': (40, 40, 48), 'g': (200, 226, 236), 'h': (244, 250, 255), 'G': (150, 182, 198), 'w': (242, 240, 234),
    'W': (206, 202, 194), 'v': (150, 146, 140), 'a': (236, 220, 160), 'A': (206, 186, 120), 's': (206, 210, 216),
    'S': (136, 140, 148), 'r': (196, 48, 44), 'R': (128, 24, 24), 'b': (200, 154, 98), 'B': (140, 98, 58),
    'y': (244, 196, 48), 'Y': (204, 146, 24), 'c': (64, 204, 224), 'C': (20, 120, 150), 'n': (244, 236, 214),
    'N': (196, 184, 156), 'e': (238, 236, 226), 'f': (246, 232, 222), 'L': (88, 150, 60), 'l': (56, 104, 40),
    't': (110, 74, 40), 'q': (214, 190, 122), 'Q': (178, 154, 92),  # q, Q: agar
}


def blank():
    return [['.'] * 16 for _ in range(16)]


def grid(*rows):
    if len(rows) != 16 or any(len(row) != 16 for row in rows):
        raise ValueError('16 rows of 16: ' + repr([len(row) for row in rows]))
    return [list(row) for row in rows]


def text(canvas):
    return [''.join(row) for row in canvas]


def put(canvas, x, y, char):
    if 0 <= x < 16 and 0 <= y < 16 and char != '.':
        canvas[y][x] = char


def stamp(canvas, sprite, left, top):
    for y, row in enumerate(sprite):
        for x, char in enumerate(row):
            put(canvas, left + x, top + y, char)


def lit(nx, ny):
    """Grey level 2-9 of a point of a dome (nx, ny in -1..1), lit from the upper left."""
    d = nx * nx + ny * ny
    nz = math.sqrt(max(0.0, 1 - d))
    light = (-0.55 * nx - 0.65 * ny + 0.52 * nz) / 1.0
    return max(2, min(9, round(5.6 + 3.6 * light)))


def ellipse(canvas, cx, cy, rx, ry, outline=True, flat=None, levels=None):
    """A lit ellipse; `flat` a single level instead of lighting; `levels` maps 2-9 to other characters."""
    inside = set()
    for y in range(16):
        for x in range(16):
            nx, ny = (x + 0.5 - cx) / rx, (y + 0.5 - cy) / ry
            if nx * nx + ny * ny <= 1:
                inside.add((x, y))
    for x, y in inside:
        nx, ny = (x + 0.5 - cx) / rx, (y + 0.5 - cy) / ry
        level = flat if flat is not None else lit(nx, ny)
        edge = outline and any((x + dx, y + dy) not in inside for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)))
        char = '1' if edge else str(level)
        if levels and char in levels:
            char = levels[char]
        put(canvas, x, y, char)
    return inside


def capsule(canvas, x0, y0, x1, y1, radius, pattern=None):
    """A lit rod from (x0, y0) to (x1, y1); `pattern(t)` may darken stripes along it (t 0-1)."""
    inside = {}
    dx, dy = x1 - x0, y1 - y0
    length2 = dx * dx + dy * dy
    for y in range(16):
        for x in range(16):
            px, py = x + 0.5 - x0, y + 0.5 - y0
            t = max(0.0, min(1.0, (px * dx + py * dy) / length2))
            ex, ey = px - t * dx, py - t * dy
            dist = math.hypot(ex, ey)
            if dist <= radius:
                inside[(x, y)] = (t, ex / radius, ey / radius)
    for (x, y), (t, nx, ny) in inside.items():
        edge = any((x + ax, y + ay) not in inside for ax, ay in ((1, 0), (-1, 0), (0, 1), (0, -1)))
        level = lit(nx, ny)
        if pattern:
            level = max(2, min(9, level + pattern(t)))
        put(canvas, x, y, '1' if edge else str(level))
    return inside


def polygon(canvas, points, char_of):
    """Fills a polygon; char_of(x, y) gives the character of each pixel."""
    xs = [p[0] for p in points]
    ys = [p[1] for p in points]
    for y in range(int(min(ys)), int(max(ys)) + 1):
        for x in range(int(min(xs)), int(max(xs)) + 1):
            if inside_polygon(x + 0.5, y + 0.5, points):
                put(canvas, x, y, char_of(x, y))


def inside_polygon(px, py, points):
    result = False
    j = len(points) - 1
    for i in range(len(points)):
        xi, yi = points[i]
        xj, yj = points[j]
        if (yi > py) != (yj > py) and px < (xj - xi) * (py - yi) / (yj - yi) + xi:
            result = not result
        j = i
    return result


def outline(canvas, chars='23456789'):
    """Turns tinted pixels next to empty ones into the outline level."""
    edges = []
    for y in range(16):
        for x in range(16):
            if canvas[y][x] in chars:
                for dx, dy in ((1, 0), (-1, 0), (0, 1), (0, -1)):
                    nx, ny = x + dx, y + dy
                    if not (0 <= nx < 16 and 0 <= ny < 16) or canvas[ny][nx] == '.':
                        edges.append((x, y))
                        break
    for x, y in edges:
        canvas[y][x] = '1'


# ---- cultures: a petri dish seen from above (fixed), colonies tinted ----

DISH = grid(
    '................',
    '.....kkkkkk.....',
    '...kkhhGGGGkk...',
    '..khqqqqqqqqGk..',
    '.khqqqqqqqqqqGk.',
    '.khqqqqqqqqqqGk.',
    'khqqqqqqqqqqqqGk',
    'khqqqqqqqqqqqqGk',
    'kGqqqqqqqqqqqqGk',
    'kGqqqqqqqqqqqqGk',
    '.kGqqqqqqqqqqGk.',
    '.kGqqqqqqqqqqGk.',
    '..kGQQqqqqQQGk..',
    '...kkGGGGGGkk...',
    '.....kkkkkk.....',
    '................')


def dish():
    return [row[:] for row in DISH]


def culture_bacteria():
    canvas = dish()
    for x, y in ((4, 4), (8, 3), (10, 6), (5, 8), (8, 9), (11, 10), (3, 10), (7, 6), (9, 12), (6, 11)):
        stamp(canvas, ('86', '64'), x, y)
    for x, y in ((12, 8), (4, 6), (10, 4), (7, 12)):
        put(canvas, x, y, '7')
    return canvas


def culture_yeast():
    canvas = dish()
    for x, y in ((3, 4), (8, 3), (5, 8), (10, 8), (7, 11)):
        stamp(canvas, ('.75.', '7986', '5864', '.64.'), x, y)
    return canvas


def culture_mold():
    canvas = dish()
    blob = ('..787..', '.78558.', '7853587', '8532358', '7853587', '.78558.', '..787..')
    stamp(canvas, blob, 2, 3)
    stamp(canvas, blob, 7, 6)
    stamp(canvas, ('.787.', '75357', '83238', '75357', '.787.'), 3, 9)
    return canvas


BADGE = grid(
    '...........kkkkk',
    '...........kcwck',
    '...........kwcwk',
    '...........kcwck',
    '...........kkkkk',
    *['................'] * 11)

# ---- enzymes ----

ENZYME_LIQUID = grid(
    '................',
    '......kkkk......',
    '.....kssssk.....',
    '.....kSSSSk.....',
    '.....khggGk.....',
    '....khggggGk....',
    '...khggggggGk...',
    '...kh877776Gk...',
    '...kh766665Gk...',
    '...kh766665Gk...',
    '...kh666655Gk...',
    '...kh666555Gk...',
    '...kh655554Gk...',
    '...kh554443Gk...',
    '....kkkkkkkk....',
    '................')

ENZYME_POWDER = grid(
    '................',
    '................',
    '...kkkkkkkkkk...',
    '...kssssssssk...',
    '...kSSSSSSSSk...',
    '..khggggggggGk..',
    '..khggggggggGk..',
    '..khggg87gggGk..',
    '..khgg877666Gk..',
    '..khg87766666k..',
    '..kh877666655k..',
    '..kh766655554k..',
    '..kh655554444k..',
    '..kh554444333k..',
    '..kkkkkkkkkkkk..',
    '................')


def grain():
    canvas = blank()
    for cx, cy in ((4, 5.5), (9, 4), (13, 6.5), (6, 11), (11, 11.5)):
        ellipse(canvas, cx, cy, 1.9, 2.8)
        put(canvas, int(cx), int(cy - 3.2), 'L')
    return canvas


# ---- flavours: a soft paste (concrete, butter) in a shallow dish ----

def paste():
    canvas = blank()
    ellipse(canvas, 8, 12, 7.5, 3.2, outline=False, levels={str(i): 'W' for i in range(2, 10)})
    ellipse(canvas, 8, 11.5, 6.5, 2.4, outline=False, levels={str(i): 'w' for i in range(2, 10)})
    ellipse(canvas, 8, 8.5, 5.0, 4.0)
    for x, y in ((6, 6), (7, 6), (9, 8), (10, 8)):
        put(canvas, x, y, '9')
    return canvas


# ---- finished products ----

BOTTLE = grid(
    '......kkkk......',
    '......kSSk......',
    '......kssk......',
    '......khGk......',
    '......kh7k......',
    '.....kh777k.....',
    '....kh77776k....',
    '....kh77766k....',
    '....kh77666k....',
    '....knnnnnNk....',
    '....knnnnnNk....',
    '....kh66655k....',
    '....kh66555k....',
    '....kh55544k....',
    '....kkkkkkkk....',
    '................')

SODA_CAN = grid(
    '................',
    '....kkkkkkkk....',
    '...kssSssssSk...',
    '...kSSSSSSSSk...',
    '...k98777766k...',
    '...k98777666k...',
    '...k97776666k...',
    '...k9nnnnnnNk...',
    '...k9nnnnnnNk...',
    '...k87766655k...',
    '...k87666555k...',
    '...k87665554k...',
    '...k76655544k...',
    '...kSSSSSSSSk...',
    '....kkkkkkkk....',
    '................')

TIN_CAN = grid(
    '................',
    '................',
    '................',
    '...kkkkkkkkkk...',
    '..ksssssssssSk..',
    '..kSSSSSSSSSSk..',
    '..k8777777766k..',
    '..k8776666665k..',
    '..k7766666655k..',
    '..k7666666555k..',
    '..k6665555554k..',
    '..kSSSSSSSSSSk..',
    '..ksssssssssSk..',
    '...kkkkkkkkkk...',
    '................',
    '................')

JAR = grid(
    '................',
    '....kkkkkkkk....',
    '...krrrrrrrrk...',
    '...kRRRRRRRRk...',
    '..kgggggggggGk..',
    '..kh88877766Gk..',
    '..kh87777666Gk..',
    '..kh77766665Gk..',
    '..kh77666655Gk..',
    '..kh66666555Gk..',
    '..kh66655554Gk..',
    '..kh65555544Gk..',
    '..kh55544443Gk..',
    '..kGGGGGGGGGGk..',
    '...kkkkkkkkkk...',
    '................')

JAR_CHUNKS = grid(
    '................',
    '....kkkkkkkk....',
    '...krrrrrrrrk...',
    '...kRRRRRRRRk...',
    '..kgggggggggGk..',
    '..kh77777777Gk..',
    '..kh75577887Gk..',
    '..kh75567886Gk..',
    '..kh77777766Gk..',
    '..kh88575577Gk..',
    '..kh87565577Gk..',
    '..kh77777557Gk..',
    '..kh66655556Gk..',
    '..kGGGGGGGGGGk..',
    '...kkkkkkkkkk...',
    '................')

CUP = grid(
    '................',
    '................',
    '....kkkkkkkk....',
    '..kk98887777kk..',
    '..k8877776666k..',
    '..kwwwwwwwwwWk..',
    '...kwwwwwwwWk...',
    '...kw766665Wk...',
    '...kw766655Wk...',
    '...kwwwwwwwWk...',
    '....kwwwwwWk....',
    '....kwwwwwWk....',
    '....kWWWWWWk....',
    '.....kkkkkk.....',
    '................',
    '................')

BAG = grid(
    '................',
    '....kkkkkkkk....',
    '....kvWvWvWk....',
    '...k98777766k...',
    '..k9887777666k..',
    '..k8777776665k..',
    '..k87wwwwww65k..',
    '..k77wnnnnw55k..',
    '..k77wnnnnw55k..',
    '..k76wwwwww55k..',
    '..k7666666554k..',
    '..k6666665544k..',
    '..k6655555444k..',
    '...k55554444k...',
    '....kkkkkkkk....',
    '................')

BLOCK = grid(
    '................',
    '................',
    '................',
    '................',
    '.....11111111...',
    '....1999999871..',
    '...199999998761.',
    '..1999999987661.',
    '..1888888876661.',
    '..1777777766651.',
    '..1666666666541.',
    '..1666666665441.',
    '..1555555554441.',
    '..1444444443331.',
    '...11111111111..',
    '................')

BAR = grid(
    '................',
    '................',
    '................',
    '................',
    '.....11111111...',
    '....1999999871..',
    '...199999998761.',
    '..1999999987661.',
    '..1888388376661.',
    '..1777377366651.',
    '..1333333333541.',
    '..1666366365441.',
    '..1555355354441.',
    '..1444344343331.',
    '...11111111111..',
    '................')

NOODLES = grid(
    '................',
    '................',
    '...1111111111...',
    '..186868686861..',
    '..168686868681..',
    '..186868686861..',
    '..168686868681..',
    '..1nnnnnnnnnN1..',
    '..1NNNNNNNNNN1..',
    '..186868686861..',
    '..168686868681..',
    '..186868686861..',
    '..168686868681..',
    '..176767676761..',
    '...1111111111...',
    '................')


def bun():
    canvas = blank()
    ellipse(canvas, 8, 9, 6.6, 5.2)
    for x, y in ((8, 4), (7, 5), (9, 5), (6, 6), (10, 6), (8, 6), (5, 7), (11, 7)):
        put(canvas, x, y, '9' if (x + y) % 2 else '5')
    return canvas


def bread():
    canvas = blank()
    capsule(canvas, 3.5, 9, 12.5, 9, 4.2)
    for x0 in (4, 7, 10):
        put(canvas, x0, 7, '9')
        put(canvas, x0 + 1, 8, '9')
        put(canvas, x0 + 1, 7, '5')
    return canvas


CAKE = grid(
    '................',
    '................',
    '................',
    '................',
    '..........111...',
    '.......1119991..',
    '....1119999991..',
    '..119999999991..',
    '.1888888888881..',
    '.1666666666661..',
    '.1666666666661..',
    '.1999999999991..',
    '.1666666666661..',
    '.1555555555551..',
    '..11111111111...',
    '................')


def biscuit():
    canvas = blank()
    ellipse(canvas, 8, 10, 7, 3.6, levels={str(i): '5' for i in range(2, 10)})
    ellipse(canvas, 8, 8, 7, 3.6)
    for x, y in ((5, 7), (8, 7), (11, 7), (6, 9), (10, 9), (8, 9)):
        put(canvas, x, y, '4')
    return canvas


def fried_stick():
    canvas = blank()
    twist = (lambda t: -2 if int(t * 9) % 2 else 1)
    capsule(canvas, 2, 11, 11, 2, 1.7, twist)
    capsule(canvas, 6, 14.5, 14.5, 6, 1.7, twist)
    return canvas


CANDY = grid(
    '................',
    '................',
    '................',
    '................',
    '................',
    'kWk.11111111.kWk',
    'kwwk19988881kwwk',
    'kwwW18888871Wwwk',
    'kwwW18877761Wwwk',
    'kwwk17766661kwwk',
    'kWk.11111111.kWk',
    '................',
    '................',
    '................',
    '................',
    '................')

STICK_TREAT = grid(
    '................',
    '.....111111.....',
    '....18999871....',
    '....19888761....',
    '....18887761....',
    '....18877661....',
    '....18776661....',
    '....17766651....',
    '....17666551....',
    '....16665551....',
    '.....111111.....',
    '.......bB.......',
    '.......bB.......',
    '.......bB.......',
    '.......kk.......',
    '................')


def sausage():
    canvas = blank()
    capsule(canvas, 3, 6, 13, 3.5, 2.2)
    capsule(canvas, 3, 12, 13, 9.5, 2.2)
    for x, y in ((1, 6), (14, 3), (1, 12), (14, 9)):
        put(canvas, x, y, 't')
    return canvas


MEAT = grid(
    '................',
    '................',
    '................',
    '......11111.....',
    '....1199876611..',
    '...19987766661..',
    '..1ff8776ff661..',
    '..1987ffff66651.',
    '.19877666666551.',
    '.18776667666551.',
    '.1ff666ff6ff541.',
    '.1766fff6665441.',
    '.16666665554441.',
    '..166655554441..',
    '...1111111111...',
    '................')


def poultry():
    canvas = blank()
    ellipse(canvas, 6.5, 9, 6.0, 4.8)
    capsule(canvas, 10, 11.5, 13, 13.5, 1.9)
    capsule(canvas, 10, 6.5, 13, 4.5, 1.9)
    for x, y in ((14, 14), (15, 15), (14, 3), (15, 2)):
        put(canvas, x, y, 'w')
    return canvas


def fish():
    canvas = blank()
    ellipse(canvas, 7, 8, 6, 3.6)
    polygon(canvas, [(11.5, 8), (15.5, 4), (15.5, 12)], lambda x, y: '5')
    outline(canvas)
    put(canvas, 4, 7, 'k')
    for x in range(6, 11, 2):
        put(canvas, x, 9, '4')
    return canvas


def egg():
    canvas = blank()
    ellipse(canvas, 5.5, 7.5, 3.9, 5.0)
    ellipse(canvas, 11.2, 10.5, 4.0, 4.3, levels={str(i): 'e' for i in range(2, 10)})
    ellipse(canvas, 11.2, 10.5, 2.0, 2.2, outline=False, levels={str(i): 'y' for i in range(2, 10)})
    return canvas


def pieces():
    canvas = blank()
    for cx, cy, rx, ry in ((5, 6, 2.6, 2.0), (10.5, 5, 2.4, 2.2), (8, 9.5, 2.8, 2.1), (3.5, 11.5, 2.4, 2.0), (12, 11, 2.6, 2.2)):
        ellipse(canvas, cx, cy, rx, ry)
    return canvas


# ---- raw materials ----

def leaf():
    canvas = blank()
    polygon(canvas, [(2, 14), (3, 8), (7, 3), (13, 1.5), (13, 7), (9, 12), (4, 14.5)],
            lambda x, y: str(max(3, min(9, 8 - (x + y) // 5))))
    outline(canvas)
    for i in range(3, 12):
        put(canvas, i, 14 - i, '4')
    put(canvas, 1, 15, '1')
    return canvas


def flower():
    canvas = blank()
    for angle in range(0, 360, 72):
        a = math.radians(angle - 90)
        ellipse(canvas, 8 + 3.6 * math.cos(a), 8 + 3.6 * math.sin(a), 2.8, 2.8)
    ellipse(canvas, 8, 8, 2.1, 2.1, levels={str(i): 'y' for i in range(2, 10)} | {'1': 'Y'})
    return canvas


def seed():
    canvas = blank()
    spots = ((4, 5), (8, 4), (12, 5), (6, 8), (10, 8), (3, 11), (7, 12), (11, 11), (13, 13), (5, 14))
    for cx, cy in spots:
        ellipse(canvas, cx + 0.5, cy + 0.5, 1.9, 1.5)
    return canvas


def root():
    canvas = blank()
    capsule(canvas, 3, 3, 12, 12, 2.4)
    capsule(canvas, 8, 8, 13, 6, 1.2)
    capsule(canvas, 6, 6, 3, 10, 1.1)
    capsule(canvas, 11, 11, 14, 14, 1.0)
    return canvas


def bark():
    canvas = blank()
    for x0, y0, x1, y1 in ((2, 4, 13, 2), (1, 9, 12, 7), (4, 14, 14, 11)):
        capsule(canvas, x0, y0, x1, y1, 1.8, lambda t: -2 if int(t * 7) % 2 else 0)
    return canvas


def fruit():
    canvas = blank()
    ellipse(canvas, 8, 9.5, 5.6, 5.2)
    for x, y, c in ((8, 3, 'B'), (8, 4, 'B'), (9, 3, 'L'), (10, 3, 'L'), (10, 2, 'l')):
        put(canvas, x, y, c)
    return canvas


def mushroom():
    canvas = blank()
    for y in range(8, 15):
        for x in range(6, 10):
            put(canvas, x, y, 'w' if x < 9 else 'W')
    for y in range(8, 15):
        put(canvas, 5, y, 'k')
        put(canvas, 10, y, 'k')
    for x in range(6, 10):
        put(canvas, x, 15, 'k')
    inside = ellipse(canvas, 8, 8, 7, 5.5)
    for x, y in list(inside):
        if y >= 8:
            canvas[y][x] = '.' if not (6 <= x <= 9) else canvas[y][x]
    for x in range(1, 15):
        if canvas[8][x] in '23456789':
            canvas[8][x] = '1'
    for x in range(6, 10):
        canvas[9][x] = 'W'
    return canvas


def tissue():
    canvas = blank()
    ellipse(canvas, 8, 9, 6.8, 4.8)
    for x, y in ((5, 7), (6, 7), (9, 6), (10, 6), (7, 10), (11, 9)):
        put(canvas, x, y, '9')
    return canvas


# ---- catalysts ----

def beads():
    canvas = blank()
    for cx, cy in ((5, 5), (10, 4.5), (8, 9), (3.5, 10.5), (12.5, 10), (7, 13.2), (11, 13.5)):
        ellipse(canvas, cx, cy, 2.4, 2.4)
        put(canvas, int(cx - 1), int(cy - 1), '9')
    return canvas


def mesh():
    canvas = blank()
    for y in range(2, 14):
        for x in range(2, 14):
            on = x % 3 == 2 or y % 3 == 2
            if on:
                canvas[y][x] = '8' if (x + y) % 2 else '6'
    for i in range(2, 14):
        put(canvas, i, 1, '1')
        put(canvas, i, 14, '1')
        put(canvas, 1, i, '1')
        put(canvas, 14, i, '1')
    return canvas


TEMPLATES = {
    # cultures, enzymes
    'culture_bacteria': culture_bacteria, 'culture_yeast': culture_yeast, 'culture_mold': culture_mold,
    'enzyme_liquid': lambda: ENZYME_LIQUID, 'enzyme_powder': lambda: ENZYME_POWDER, 'grain': grain,
    # flavours
    'paste': paste,
    # finished products
    'bottle': lambda: BOTTLE, 'soda_can': lambda: SODA_CAN, 'tin_can': lambda: TIN_CAN, 'jar': lambda: JAR,
    'jar_chunks': lambda: JAR_CHUNKS, 'cup': lambda: CUP, 'bag': lambda: BAG, 'block': lambda: BLOCK, 'bar': lambda: BAR,
    'noodles': lambda: NOODLES, 'bun': bun, 'bread': bread, 'cake': lambda: CAKE, 'biscuit': biscuit,
    'fried_stick': fried_stick, 'candy': lambda: CANDY, 'stick_treat': lambda: STICK_TREAT, 'sausage': sausage,
    'meat': lambda: MEAT, 'poultry': poultry, 'fish': fish, 'egg': egg, 'pieces': pieces,
    # raw materials
    'leaf': leaf, 'flower': flower, 'seed': seed, 'root': root, 'bark': bark, 'fruit': fruit, 'mushroom': mushroom,
    'tissue': tissue,
    # catalysts
    'beads': beads, 'mesh': mesh,
}
BADGES = {'engineered': BADGE}
