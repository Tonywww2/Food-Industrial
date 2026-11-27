"""Woodwork for the machines' faces, after the vanilla barrel (user 2026-11-26, CONVENTIONS.md §11): boards drawn
flat with a lit line and a dark seam (pixels.staves), iron straps two pixels wide with the boards under them in
shadow (pixels.strap). The screw press and the steeping vat, and the crates and barrels of the other wooden machines.
"""
import numpy as np

from pixels import BAND, WOOD, WOOD_DARK, Layer, edge, round_mask, staves, strap, upper_left


# --- wood -----------------------------------------------------------------------------------------------------------
# After the vanilla barrel (user 2026-11-26): its browns, flat boards with a lit line and a dark seam, near-black
# iron straps two pixels wide with the boards under them in shadow, a light rim where the boards end. Drawn anew
# for these machines (their own layouts, seams, hoops and lid), not copied from the barrel.

def banded(seed, boards, ramp=BAND, width=2):
    """Boards inside an iron band round the face (the press's sides and top, two pixels and nailed at the corners;
    the crates', one pixel as their sheets draw it); boards(layer) lays the boards inside, from width to 16 - width;
    ramp: the band's metal."""
    L = Layer(WOOD, WOOD.flat)
    boards(L)
    if width == 1:
        L.add(1, 1, 15, 2, -1.0)
        L.add(1, 2, 2, 15, -0.5)
        for box, lv in (((0, 0, 16, 1), 0.5), ((0, 1, 1, 16), 0.0), ((1, 15, 16, 16), -1.0), ((15, 1, 16, 15), -1.0)):
            L.set(*box, ramp=ramp, level=lv)
        return L
    L.add(2, 2, 14, 3, -1.0)                        # the band's shadow on the boards, under its top and left
    L.add(2, 3, 3, 14, -0.5)
    strap(L, 0, 0, 16, 2, seed, ramp)
    strap(L, 0, 14, 16, 16, seed + 1, ramp)
    strap(L, 0, 2, 2, 14, seed + 2, ramp)
    strap(L, 14, 2, 16, 14, seed + 3, ramp)
    for x, y in ((1, 1), (14, 1), (1, 14), (14, 14)):
        L.px(x, y, level=2.0)                       # nail heads
    return L


def press_front():
    """A wooden frame three pixels wide round the chamber (its screw, plate and bed are the model's, its walls the
    inner texture): bars across, posts up, meeting in mitres; each a board lit along its upper or left edge, its
    seam along the other."""
    L = Layer(WOOD_DARK, WOOD_DARK.flat)
    L.vgrad(3, 3, 13, 13, -1.5, 0.5)
    staves(L, 0, 0, 3, 16, (3,), True, 13, ramp=WOOD)
    staves(L, 13, 0, 16, 16, (3,), True, 14, ramp=WOOD)
    top, bottom = Layer(WOOD), Layer(WOOD)
    staves(top, 0, 0, 16, 3, (3,), False, 11)
    staves(bottom, 0, 13, 16, 16, (3,), False, 12)
    for y in range(3):                              # the bars run out to the mitres
        for x in range(16):
            if y <= x <= 15 - y:
                L.px(x, y, WOOD, top.lv[y, x])
            if y <= x <= 15 - y:
                L.px(x, 15 - y, WOOD, bottom.lv[15 - y, x])
    for k in range(1, 3):                           # the mitre joints
        for x, y in ((k, k), (15 - k, k), (k, 15 - k), (15 - k, 15 - k)):
            L.px(x, y, level=WOOD.flat - 2.0)
    L.add(3, 3, 13, 4, -0.5)                        # the frame's shadow at the chamber's top and left
    L.add(3, 4, 4, 13, -0.5)
    return L


def press_side():
    return banded(21, lambda L: staves(L, 2, 2, 14, 14, (4, 4, 4), True, 21))


def press_top():
    """Four boards turning round the iron plate in the middle, where the screw comes through."""
    def boards(L):
        staves(L, 2, 2, 11, 5, (3,), False, 31)        # top, right, bottom, left: each butts on the next
        staves(L, 11, 2, 14, 11, (3,), True, 32)
        staves(L, 5, 11, 14, 14, (3,), False, 33)
        staves(L, 2, 5, 5, 14, (3,), True, 34)
        L.add(11, 6, 12, 12, -1.0)                      # the plate's shadow on the boards
        L.add(6, 11, 11, 12, -1.0)
    L = banded(35, boards)
    strap(L, 5, 5, 11, 6, 36, BAND)                     # the plate: a strap round the hole
    L.set(5, 6, 11, 7, level=-1.0)
    L.set(5, 10, 11, 11, ramp=BAND, level=-1.0)
    L.set(5, 6, 6, 10, ramp=BAND, level=0.5)
    L.set(10, 6, 11, 10, ramp=BAND, level=-1.0)
    L.set(6, 6, 10, 10, ramp=WOOD_DARK, level=0.0)      # the hole, dark as the inside of an open barrel
    L.set(6, 6, 10, 7, level=-1.0)
    L.set(6, 7, 7, 10, level=-1.0)
    L.set(9, 9, 10, 10, level=1.0)
    L.set(5, 5, 6, 6, level=1.5)
    return L


def press_inner():
    """The chamber's walls: boards in the dark browns inside an open barrel, darker towards the top."""
    L = Layer(WOOD_DARK, WOOD_DARK.flat)
    staves(L, 0, 0, 16, 16, (4, 4, 4, 4), True, 41, ramp=WOOD_DARK, level=WOOD_DARK.flat)
    L.add(0, 0, 16, 2, -0.5)
    return L


def barrel_side(seed=51, hoops=(2, 12)):
    """Staves four pixels wide bound by two iron hoops, the staves under each hoop in its shadow; a light rim where
    the staves end at the top, their foot a shade darker."""
    L = Layer(WOOD, WOOD.flat)
    staves(L, 0, 0, 16, 16, (4, 4, 4, 4), True, seed)
    for x in range(16):                             # the rim
        L.px(x, 0, level=WOOD.flat - 0.5 if L.lv[0, x] < WOOD.flat - 1 else WOOD.flat + 0.5)
    for i, y in enumerate(hoops):
        strap(L, 0, y, 16, y + 2, seed + i, BAND)
        L.add(0, y + 2, 16, y + 3, -1.0)            # the hoop's shadow on the staves
    L.add(0, 15, 16, 16, -0.5)                      # the foot
    return L


def stave_ends():
    """A barrel's top round its lid or mouth: the staves' ends, light with a joint every four pixels, and the dark gap
    inside them, deepest under their top and left edges."""
    L = Layer(WOOD, WOOD.flat - 1.5)
    for i in range(16):
        lv = WOOD.flat + (0.0 if i % 4 == 3 else 0.5)
        for x, y in ((i, 0), (0, i), (i, 15), (15, i)):
            L.px(x, y, level=lv)
    for x, y in ((0, 0), (15, 0), (0, 15), (15, 15)):
        L.px(x, y, level=WOOD.flat - 0.5)
    L.add(1, 1, 15, 2, -0.5)
    L.add(1, 2, 2, 15, -0.5)
    return L


def barrel_top():
    """The staves' ends round the top, light with a joint every four pixels, and the dark gap inside them; the lid,
    a drawn pixel circle twelve across (user 2026-11-26: the earlier lid looked unnatural), of boards across laid
    tight, so their seams are only a shade or so down; it stands a little proud: its edge lit at the top left,
    turned away at the bottom right, a pixel of shadow beyond that."""
    L = stave_ends()
    lid = Layer(WOOD, WOOD.flat)
    staves(lid, 0, 2, 16, 14, (4, 4, 4), False, 61, specks=1, seam=(1.0, 1.0))
    inside = round_mask(2, 2, (8, 10, 12, 12, 12, 12, 12, 12, 12, 12, 10, 8))
    L.lv[inside] = lid.lv[inside]
    rim = edge(inside)
    L.mask(rim & upper_left(rim), level=WOOD.flat + 0.5)
    L.mask(rim & ~upper_left(rim), level=WOOD.flat - 0.5)
    near = np.pad(inside, 1)
    beyond = ~inside & (near[:-2, 1:-1] | near[2:, 1:-1] | near[1:-1, :-2] | near[1:-1, 2:])  # the gap by the lid
    yy, xx = np.mgrid[0:16, 0:16]
    L.mask(beyond & (xx + yy > 15), add=-0.5)
    return L
