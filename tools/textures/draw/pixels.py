"""Drawing block textures pixel by pixel (user 2026-11-25: block textures are drawn by script, not obtained by
sampling the reference sheets down; Minecraft's 16x standard: 16 pixels to a block side).

A Canvas is a 16 × 16 RGBA picture, x to the right, y down. A Ramp is a material's shades, lit from the top left
(Blockbench style guide: midtone, shadow, highlight; an outline below the shadow, a highlight above the light; no
noise). A Layer is painted in light levels rather than colours: each pixel has its material and a level, and
gradients, bevels, the shadow a frame casts and grain all add to the level; rendering rounds each level to the
nearest of the material's nine shades (user 2026-11-26: clearer transitions, more natural detail). Base colours were measured on the approved reference sheets (Food_Industrial_Approved_Textures-v3,
2026-11-25) and are kept here, so drawing needs no sheet.
"""
import numpy as np
from PIL import Image

N = 16


def rgb(h):
    h = h.lstrip('#')
    return tuple(int(h[i:i + 2], 16) for i in (0, 2, 4))


class Ramp:
    """outline < shadow < base < light < highlight: light levels -2, -1, 0, 1, 2; the half levels between them are
    mixed, so a material has nine shades. A ramp given nine colours uses them for the nine levels as they are (a
    hand-picked palette, as the vanilla textures keep theirs)."""

    def __init__(self, *colours, flat=0.0):
        cs = [rgb(c) for c in colours]
        if len(cs) == 9:
            self.nine = np.array(cs, dtype=float)
            self.outline, self.shadow, self.base, self.light, self.highlight = cs[0], cs[2], cs[4], cs[6], cs[8]
        else:
            self.outline, self.shadow, self.base, self.light, self.highlight = cs
            five = np.array(cs, dtype=float)
            self.nine = np.array([five[i // 2] if i % 2 == 0 else (five[i // 2] + five[i // 2 + 1]) / 2 for i in range(9)])
        self.flat = flat

    def at(self, level):
        """The colour at a light level (clamped to -2..2, rounded to a half step)."""
        i = int(min(2.0, max(-2.0, round(level * 2) / 2)) * 2) + 4
        return tuple(int(round(v)) for v in self.nine[i])

    def parts(self):
        """The (shadow, mid, light, accent) palette the model parts are painted with (tools/models/parts.py): the
        level a flat face of the material is drawn at (flat) and a step either side of it; a hand-picked palette
        steps half a level, as its faces do."""
        step = 0.5 if self.flat else 1.0
        return (self.at(self.flat - step), self.at(self.flat), self.at(self.flat + step), self.outline)


# materials, measured on the sheets (median colours of each material's areas)
# the wooden machines follow the vanilla barrel (user 2026-11-26): its browns, its near-black hoop iron and the dark
# browns inside the open barrel; the lightest two wood shades and the darkest iron one are added in the same hues
WOOD = Ramp('#553a1f', '#5e4323', '#664d28', '#6b5332', '#785836', '#805e36', '#8b673c', '#997140', '#a67c48', flat=1.0)
WOOD_DARK = Ramp('#1f1406', '#271a08', '#2f1f09', '#33230b', '#38260c', '#3f2b0f', '#463112', '#523a18', '#5e4420',
                 flat=0.5)
BAND = Ramp('#1c1814', '#221d18', '#28221c', '#2c2723', '#302c29', '#3a3532', '#453f3b', '#55504b', '#6a645e', flat=0.5)
BRASS = Ramp('#5e4a22', '#9a7d3c', '#c0a154', '#d8bb6c', '#ecd58e')         # the vat's tap
STEEL = Ramp('#8b8b8b', '#b5b5b5', '#cfcfcf', '#e2e2e2', '#f2f2f2')         # the extraction tank
STEEL_DARK = Ramp('#5f5f5f', '#7f7f7f', '#959595', '#a9a9a9', '#bdbdbd')    # hatches, seams in shadow
IRON_DARK = Ramp('#25231f', '#363330', '#4b4741', '#625e57', '#908f8b')     # the handwheel, the press's screw and plate
WHITE = Ramp('#a9b0b3', '#d3d8da', '#eaeded', '#f6f7f7', '#ffffff')         # the culture bench
GLASS = Ramp('#7f9196', '#96a8ad', '#adc4cb', '#c8d8dc', '#e8f3f5')         # its windows
GLASS_LIT = Ramp('#5f9aa3', '#8cc6cf', '#b2e4ec', '#ccf0f5', '#ecfcfd')     # ... lit while it works
CYAN = Ramp('#2f7f86', '#4fa9b0', '#6fd6dd', '#9cf9fc', '#d6feff')          # its light strips (lit: light, highlight)
GEL = Ramp('#3f6a2c', '#5f8f42', '#86b85e', '#a6d47c', '#c9eaa3')           # cultures in its dishes

# masonry and the other machines' materials (user 2026-11-26: every machine as the approved pilots, CONVENTIONS.md
# §11): a vanilla block's palette where the sheet's material has one (stone bricks, smooth stone, bricks, copper,
# snow, ice, cut sandstone, the campfire's flame), else measured on the sheet; each completed to nine shades
STONE = Ramp('#4f4e4f', '#5a595a', '#636363', '#6a6d6a', '#787678', '#7f7f7f', '#8b898b', '#9c999c', '#aaa8aa', flat=0.5)
SMOOTH = Ramp('#6a6a6a', '#747474', '#7f7f7f', '#8f8f8f', '#9d9d9d', '#a3a3a3', '#a8a8a8', '#b0b0b0', '#bcbcbc', flat=1.0)
BRICK = Ramp('#5f3328', '#733f31', '#7c4536', '#8f503f', '#9b5643', '#a85b47', '#b1624d', '#c66851', '#d2775f', flat=0.0)
MORTAR = Ramp('#5e4a45', '#6f5852', '#7d625b', '#8b6e67', '#967a72', '#a2867d', '#a9948d', '#b5a39d', '#c2b3ad', flat=0.5)
KILN = Ramp('#221914', '#2c211b', '#362922', '#3f2e27', '#45322b', '#4b3931', '#553f36', '#5f4840', '#6b5249', flat=0.5)
KILN_MORTAR = Ramp('#3a2c27', '#44342e', '#4e3c35', '#5a453d', '#644c44', '#6c544b', '#755c53', '#7f665c', '#8a7066',
                   flat=0.0)
SOOT = Ramp('#0e0b0a', '#151110', '#1c1614', '#231b18', '#2b211d', '#342823', '#3e302a', '#4a3933', '#57443d', flat=0.0)
FIRE = Ramp('#7a2a00', '#9a3300', '#b13f00', '#c96c03', '#dfa21b', '#e9bd39', '#efcd56', '#f9ebab', '#fefefb', flat=0.0)
EMBER = Ramp('#2a0f08', '#3a140a', '#4d1a0c', '#62200d', '#7a2a0f', '#923612', '#a84516', '#bf5a1c', '#d47424', flat=0.0)
POT = Ramp('#1f1f23', '#26262a', '#2d2d32', '#343438', '#3f3e42', '#494848', '#4f4f4f', '#595858', '#676161', flat=0.0)
FLOUR = Ramp('#a99c86', '#b9ad97', '#c8bda8', '#d4cab6', '#ddd5c3', '#e6dfcf', '#eee9dc', '#f5f2e9', '#fcfbf6', flat=0.5)


class Canvas:
    """A picture, N × N unless a part face asks for another size (w × h)."""

    def __init__(self, colour=(0, 0, 0), w=N, h=N):
        self.a = np.zeros((h, w, 4), dtype=np.uint8)
        self.a[..., :3] = colour
        self.a[..., 3] = 255

    def copy(self):
        c = Canvas(w=self.a.shape[1], h=self.a.shape[0])
        c.a = self.a.copy()
        return c

    def px(self, x, y, colour):
        if 0 <= x < self.a.shape[1] and 0 <= y < self.a.shape[0]:
            self.a[y, x, :3] = colour

    def rect(self, x0, y0, x1, y1, colour):
        """Fills x0 <= x < x1, y0 <= y < y1."""
        self.a[y0:y1, x0:x1, :3] = colour

    def mask(self, m, colour):
        self.a[m, :3] = colour

    def shade(self, x0, y0, x1, y1, factor):
        """Darkens (factor < 1) or lightens a rectangle in place."""
        self.a[y0:y1, x0:x1, :3] = np.clip(self.a[y0:y1, x0:x1, :3] * factor, 0, 255).astype(np.uint8)

    def bevel(self, x0, y0, x1, y1, light, shadow):
        """A rectangle's top row and left column light, its bottom row and right column in shadow."""
        self.rect(x0, y0, x1, y0 + 1, light)
        self.rect(x0, y0, x0 + 1, y1, light)
        self.rect(x0, y1 - 1, x1, y1, shadow)
        self.rect(x1 - 1, y0, x1, y1, shadow)

    def save(self, path):
        Image.fromarray(self.a, 'RGBA').save(path)


class Grain:
    """Fixed pseudo-random positions for wood grain, so every run draws the same picture."""

    def __init__(self, seed):
        self.state = seed * 2654435761 % 2 ** 32 or 1

    def next(self, n):
        self.state = (1103515245 * self.state + 12345) % 2 ** 31
        return (self.state >> 8) % n


def disc(cx, cy, r):
    """Pixels whose centres lie within r of (cx, cy) (pixel centres at x + 0.5)."""
    yy, xx = np.mgrid[0:N, 0:N] + 0.5
    return (xx - cx) ** 2 + (yy - cy) ** 2 <= r * r


def round_mask(x0, y0, widths):
    """A drawn pixel circle: row i (from y0) is widths[i] pixels wide, centred on a box of max(widths) from x0, so
    the steps are even, as a hand-drawn one's are (a disc() of a fractional radius can step unevenly)."""
    m = np.zeros((N, N), bool)
    full = max(widths)
    for i, w in enumerate(widths):
        a = x0 + (full - w) // 2
        m[y0 + i, a:a + w] = True
    return m


def edge(m):
    """The pixels of a mask that touch a pixel outside it."""
    p = np.pad(m, 1)
    inner = p[:-2, 1:-1] & p[2:, 1:-1] & p[1:-1, :-2] & p[1:-1, 2:]
    return m & ~inner


def upper_left(m):
    yy, xx = np.mgrid[0:N, 0:N]
    ys, xs = np.nonzero(m)
    cx, cy = xs.mean(), ys.mean()
    return m & ((xx - cx) + (yy - cy) < 0)


class Layer:
    """A face painted in light levels (see the module notes): ramp per pixel, level per pixel."""

    def __init__(self, ramp, level=0.0, w=N, h=N):
        self.w, self.h = w, h
        self.ramp = np.empty((h, w), dtype=object)
        self.ramp[:, :] = ramp
        self.lv = np.full((h, w), float(level))

    def set(self, x0, y0, x1, y1, ramp=None, level=None):
        if ramp is not None:
            self.ramp[y0:y1, x0:x1] = ramp
        if level is not None:
            self.lv[y0:y1, x0:x1] = level

    def add(self, x0, y0, x1, y1, d):
        self.lv[y0:y1, x0:x1] += d

    def px(self, x, y, ramp=None, level=None, add=None):
        if 0 <= x < self.w and 0 <= y < self.h:
            if ramp is not None:
                self.ramp[y, x] = ramp
            if level is not None:
                self.lv[y, x] = level
            if add is not None:
                self.lv[y, x] += add

    def mask(self, m, ramp=None, level=None, add=None):
        if ramp is not None:
            self.ramp[m] = ramp
        if level is not None:
            self.lv[m] = level
        if add is not None:
            self.lv[m] += add

    def vgrad(self, x0, y0, x1, y1, top, bottom):
        """Adds a level running from top to bottom down the rectangle."""
        n = y1 - y0
        for i in range(n):
            self.lv[y0 + i, x0:x1] += top + (bottom - top) * (i / max(1, n - 1))

    def hgrad(self, x0, y0, x1, y1, left, right):
        n = x1 - x0
        for i in range(n):
            self.lv[y0:y1, x0 + i] += left + (right - left) * (i / max(1, n - 1))

    def bevel(self, x0, y0, x1, y1, lit=1.0, shaded=-1.0, inner=0.4):
        """A raised edge round a rectangle: the top and left rows lit, the bottom and right in shadow, and a second,
        softer ring inside, so the edge turns over two pixels."""
        self.lv[y0, x0:x1] += lit
        self.lv[y0 + 1:y1, x0] += lit
        self.lv[y1 - 1, x0 + 1:x1] += shaded
        self.lv[y0 + 1:y1 - 1, x1 - 1] += shaded
        if inner:
            self.lv[y0 + 1, x0 + 1:x1 - 1] += inner
            self.lv[y0 + 2:y1 - 1, x0 + 1] += inner
            self.lv[y1 - 2, x0 + 2:x1 - 1] -= inner
            self.lv[y0 + 2:y1 - 2, x1 - 2] -= inner

    def render(self):
        c = Canvas(w=self.w, h=self.h)
        for y in range(self.h):
            for x in range(self.w):
                c.a[y, x, :3] = self.ramp[y, x].at(self.lv[y, x])
        return c


def staves(layer, x0, y0, x1, y1, sizes, vertical, seed, ramp=None, level=1.0, specks=2, seam=(3.0, 2.0)):
    """Boards as the vanilla barrel draws its staves (user 2026-11-26: the wooden machines follow the vanilla barrel):
    flat, one shade either side of the wood's level at most; a lit line down each board's first pixel, a dark seam
    down its last that flickers between the two darkest shades; the board's second pixel carries a few short specks
    of grain half a shade down, and now and then a darker one by the seam. seam: how far the seam's two shades lie
    below the wood's level (boards laid tight, as a lid's, take a shallower one)."""
    g = Grain(seed)
    pos = x0 if vertical else y0
    lo, hi = (y0, y1) if vertical else (x0, x1)
    for size in sizes:
        a, b = pos, pos + size
        box = (a, y0, b, y1) if vertical else (x0, a, x1, b)
        layer.set(*box, ramp=ramp, level=level)

        def at(across, along, lv):
            if vertical:
                layer.px(across, along, level=lv)
            else:
                layer.px(along, across, level=lv)

        for t in range(lo, hi):
            at(a, t, level + (0.5 if g.next(6) else 0.0))              # the lit line
            at(b - 1, t, level - (seam[0] if g.next(5) < 3 else seam[1]))   # the seam
        if size >= 3:
            for _ in range(specks + g.next(2)):
                t, n = lo + g.next(hi - lo), 1 + g.next(3)
                for k in range(t, min(hi, t + n)):
                    at(a + 1, k, level - 0.5)
            for _ in range(g.next(3)):
                at(b - 2, lo + g.next(hi - lo), level - 1.0)
        pos = b


def strap(layer, x0, y0, x1, y1, seed, ramp=None):
    """An iron strap two pixels wide (a barrel's hoop, the bands round the press), as the vanilla barrel draws its
    hoops: the half towards the light iron, here and there a shade darker, the other half nearly black. A strap
    wider than high runs across (its upper row lit), else down (its left column lit)."""
    g = Grain(seed)
    across = (x1 - x0) >= (y1 - y0)
    if across:
        for x in range(x0, x1):
            layer.px(x, y0, ramp=ramp, level=1.0 if g.next(3) else 0.0)
            layer.px(x, y0 + 1, ramp=ramp, level=-1.0 if g.next(5) else 0.0)
    else:
        for y in range(y0, y1):
            layer.px(x0, y, ramp=ramp, level=0.5 if g.next(3) else 0.0)
            layer.px(x0 + 1, y, ramp=ramp, level=-1.0 if g.next(5) else 0.0)


def stud(layer, x, y, ramp=None, size=1, level=0.5):
    """A rivet or nail head: lit on its upper left, its shadow falling to the lower right."""
    layer.set(x, y, x + size, y + size, ramp=ramp, level=level)
    layer.px(x, y, add=1.0)
    layer.px(x + size, y + size, add=-1.25)
    if size > 1:
        layer.px(x + size - 1, y + size - 1, add=-1.0)
TERRA = Ramp('#4a2416', '#5a2c1b', '#6a3c2a', '#763e29', '#7e422b', '#884831', '#93543a', '#a0603f', '#ae6d4a', flat=0.5)
PLASTER = Ramp('#8e7a68', '#a08a76', '#b19881', '#bea48e', '#c5ac96', '#cbb39c', '#d6bfa9', '#e0ccb8', '#ebdcca',
               flat=0.5)
SALT = Ramp('#9a9a96', '#b0aeaa', '#c4c2bd', '#d2d0cb', '#dddbd6', '#e8e7e3', '#f0efec', '#f7f7f5', '#ffffff', flat=0.5)
SNOW = Ramp('#a9c4cc', '#bcd3da', '#cde0e6', '#dcebef', '#e6f2f4', '#f0fdfd', '#f7fefe', '#fbffff', '#ffffff', flat=0.5)
ICE = Ramp('#6a95e8', '#7ca5f4', '#86aefd', '#8cb3fe', '#92b9fe', '#a1c3ff', '#bcd4ff', '#c8dcff', '#dce9ff', flat=0.0)
COPPER = Ramp('#7e3f2a', '#904931', '#9a5038', '#a75a40', '#b26247', '#c26b4c', '#c87456', '#d67b5b', '#e3826c', flat=0.5)
DOUGH = Ramp('#a8925f', '#bba46d', '#cbb57d', '#d8c48f', '#e2d1a0', '#eadcb1', '#f1e6c3', '#f7efd5', '#fcf8e8', flat=0.5)
BRINE = Ramp('#5d6366', '#6a7174', '#787f82', '#868d90', '#949b9e', '#a2a9ab', '#b1b7b9', '#c1c6c7', '#d2d6d6', flat=0.0)
CHOCO = Ramp('#2e170c', '#3b1e10', '#4a2614', '#582f19', '#663a1f', '#744426', '#82502e', '#915d37', '#a06b42', flat=0.0)
ORANGE = Ramp('#5a1e08', '#7a2a0a', '#9c3a0c', '#c0520f', '#e06a14', '#f08420', '#f8a030', '#fcc060', '#fee0a0', flat=0.0)
SLATE = Ramp('#2f3a42', '#3a4650', '#45525c', '#4f5d67', '#58666f', '#5d6f7a', '#66777f', '#738389', '#839196', flat=0.5)
SAND = Ramp('#8f7550', '#a2865d', '#b2976b', '#c2a678', '#cbae7f', '#d6b885', '#dfc18e', '#e7c99a', '#efd4a8', flat=0.5)
ARC = Ramp('#3f7f9a', '#5aa3bf', '#7cc6dc', '#a5e0ee', '#c8eef6', '#def6fb', '#effcfe', '#f8feff', '#ffffff', flat=0.5)
