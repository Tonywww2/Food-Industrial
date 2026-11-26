#!/usr/bin/env python3
"""Draws the metabolism icons (I35, I46): one 16 x 16 icon per substance for the body panel and Apothic Attributes'
panel, and the 8 x 8 icon of the panel's button, into assets/foodindustrial/textures/gui/metabolism/.

Each icon is an ASCII grid; '.' is transparent and every other letter a colour of PALETTE.
Usage: python3 tools/textures/metabolism_icons.py
"""
import pathlib

from PIL import Image

OUT = pathlib.Path(__file__).resolve().parents[2] / 'src/main/resources/assets/foodindustrial/textures/gui/metabolism'

PALETTE = {
    'k': (40, 32, 28), 'w': (245, 245, 240), 'g': (200, 200, 196), 'G': (150, 150, 146),
    'b': (110, 66, 36), 'B': (70, 40, 20), 'c': (160, 104, 60),           # coffee
    'a': (236, 176, 60), 'A': (200, 130, 30), 'f': (250, 250, 250), 'y': (255, 236, 150),  # beer
    's': (252, 252, 252), 'S': (215, 215, 222), 'T': (175, 175, 186),      # sugar cubes
    'p': (96, 84, 104), 'P': (60, 50, 70), 'q': (140, 128, 150),            # impurity
    'o': (245, 140, 30), 'O': (205, 100, 20), 'l': (255, 200, 120), 'r': (255, 240, 200),  # orange
    'h': (222, 196, 140), 'H': (180, 150, 96), 'e': (120, 160, 60),         # grain
    'u': (255, 210, 40), 'U': (240, 160, 20), 'v': (255, 240, 140),         # sun
    'm': (200, 60, 60), 'M': (140, 30, 30),                                 # button
    'n': (122, 62, 40), 'N': (82, 38, 26), 'x': (168, 98, 70),              # nitrite: chocolate-brown blood
    'i': (250, 246, 222), 'I': (222, 214, 176),                             # nitrite: pale yellow crystals
    'd': (240, 128, 32), 'D': (190, 88, 20), 'j': (255, 186, 96), 'E': (80, 120, 40),  # vitamin A: carrot (I46)
    'R': (150, 24, 30), 'z': (200, 40, 44), 'Z': (228, 96, 96),             # iron: red blood cell
    'C': (170, 176, 184), 'F': (118, 124, 134), 'J': (236, 230, 218),       # zinc: oyster
}

ICONS = {
    'caffeine': [
        '................',
        '.....g...g......',
        '......g...g.....',
        '.....g...g......',
        '................',
        '..kkkkkkkkkk....',
        '..kBBBBBBBBk....',
        '..kwbcbbbbwkkk..',
        '..kwwwwwwwwk.k..',
        '..kwwwwwwwwk.k..',
        '..kwwwwwwwwkkk..',
        '..kwwwwwwwwk....',
        '...kwwwwwwk.....',
        '.kkkkkkkkkkkk...',
        '..kggggggggk....',
        '...kkkkkkkk.....',
    ],
    'alcohol': [
        '................',
        '...kkkkkkkk.....',
        '..kffffffffk....',
        '..kfyfffffykkk..',
        '..kAaaaaaaaAk.k.',
        '..kAayaaaaaAk.k.',
        '..kAaaaaaaaAk.k.',
        '..kAaaayaaaAk.k.',
        '..kAaaaaaaaAk.k.',
        '..kAaayaaaaAk.k.',
        '..kAaaaaaaaAkkk.',
        '..kAaaaaaaaAk...',
        '..kAAAAAAAAAk...',
        '..kgggggggggk...',
        '...kkkkkkkkk....',
        '................',
    ],
    'fast_sugar': [
        '................',
        '................',
        '......kkkkkk....',
        '.....ksssssk....',
        '....kssssssk....',
        '....kkkkkkSk....',
        '....ksssssSk....',
        '..kkkkkkksSk....',
        '.ksssssskSSk....',
        'kssssssk.kk.....',
        'kkkkkkkTk.......',
        'kssssssTk.......',
        'kssssssTk.......',
        'kSSSSSSTk.......',
        'kkkkkkkk........',
        '................',
    ],
    'nitrite': [
        '................',
        '.....kk.........',
        '....kxnk........',
        '....kxnk........',
        '...kxnnnk.......',
        '...knnnnk.......',
        '..kxnnnnNk......',
        '..knnnnnNk......',
        '..knnnnNNk......',
        '...kNNNNk..kk...',
        '....kkkk..kiik..',
        '.........kiIiik.',
        '......kk.kIiiIk.',
        '.....kiikkiiIiik',
        '....kiIiIkIiiIik',
        '....kkkkkkkkkkkk',
    ],
    'impurity': [
        '................',
        '.......kk.......',
        '......kqqk......',
        '......kqpk......',
        '.....kqppPk.....',
        '.....kqppPk.....',
        '....kqpppPPk....',
        '....kqpppPPk....',
        '...kqppppPPPk...',
        '...kqppppPPPk...',
        '...kpppppPPPk...',
        '...kppppPPPPk...',
        '....kPPPPPPk....',
        '.....kkkkkk.....',
        '................',
        '................',
    ],
    'vitamin_c': [
        '................',
        '................',
        '.....kkkkkk.....',
        '...kkOOOOOOkk...',
        '..kOorrlrrooOk..',
        '..kOrorlrorrOk..',
        '.kOrrorlrorrrOk.',
        '.kOlllllllllOOk.',
        '.kOrrorlrorrrOk.',
        '.kOrorrlrrorrOk.',
        '..kOrrrlrrrorOk.',
        '..kOOorlroOOk...',
        '...kkOOOOOOkk...',
        '.....kkkkkk.....',
        '................',
        '................',
    ],
    'vitamin_b1': [
        '................',
        '.......e........',
        '......ee........',
        '.....kkk........',
        '....khhHk..kkk..',
        '....khhHk.khhHk.',
        '....khhHk.khhHk.',
        '.....kkk..khhHk.',
        '..kkk......kkk..',
        '.khhHk..........',
        '.khhHk..kkk.....',
        '.khhHk.khhHk....',
        '..kkk..khhHk....',
        '.......khhHk....',
        '........kkk.....',
        '................',
    ],
    'vitamin_d': [
        '.......U........',
        '..U....U....U...',
        '...U...U...U....',
        '....U.....U.....',
        '......kkkk......',
        '.....kuuuuk.....',
        '....kuvvuuUk....',
        'UUU.kuvuuuUk.UUU',
        '....kuuuuuUk....',
        '....kuuuuUUk....',
        '.....kUUUUk.....',
        '....U.kkkk.U....',
        '...U...U...U....',
        '..U....U....U...',
        '.......U........',
        '................',
    ],
    # I46 (2026-11-19): carrot, bone (calcium), red blood cell (iron), oyster (zinc)
    'vitamin_a': [
        '.............e..',
        '..........e.eE..',
        '...........eEe.e',
        '.........kkkEeE.',
        '........kjjdkEe.',
        '.......kjddddk..',
        '......kjdddDDk..',
        '......kjddDDk...',
        '.....kjdddDk....',
        '.....kjdDDk.....',
        '....kjddDk......',
        '....kjdDk.......',
        '...kjdDk........',
        '...kdDk.........',
        '..kDk...........',
        '...k............',
    ],
    'calcium': [
        '..........kk....',
        '.........kwwk...',
        '........kwwwwk..',
        '........kwwwwgk.',
        '.........kwwgggk',
        '........kwwggggk',
        '.......kwwgkggk.',
        '......kwwgk.kk..',
        '..kk.kwwgk......',
        '.kwwkwwgk.......',
        'kwwwwwgk........',
        'kwwwwggk........',
        '.kwwgggk........',
        '..kggggk........',
        '...kggk.........',
        '....kk..........',
    ],
    'iron': [
        '................',
        '................',
        '......kkkk......',
        '....kkRRRRkk....',
        '...kRzRRRRRRk...',
        '..kRzzzzzzzRRk..',
        '.kRzzzzzzzzzRRk.',
        '.kRzzzZZZZzzzRk.',
        'kRRzzzZZZZzzzRRk',
        '.kRzzzZZZZzzzRk.',
        '.kRRzzzzzzzzRRk.',
        '..kRRzzzzzzRRk..',
        '...kRRRRRRRRk...',
        '....kkRRRRkk....',
        '......kkkk......',
        '................',
    ],
    'zinc': [
        '................',
        '................',
        '.......kk.......',
        '....kkkCCkkk....',
        '..kkCCCCCCCCkk..',
        '.kCCCCCFCCCCCCk.',
        '.kCCFCCCCCFCCCk.',
        'kCCCCCCCCCCCCCCk',
        'kCCCJJJJJJJJCCCk',
        'kFFJJJJJJffJJFFk',
        'kFFJJJJJJffJJFFk',
        '.kFFJJJJJffJFFk.',
        '.kFFFFJJJJFFFFk.',
        '..kkFFFFFFFFkk..',
        '....kkkFFkkk....',
        '.......kk.......',
    ],
}

BUTTON = [
    '..kkkk..',
    '..kmmk..',
    '..kmmk..',
    'kkkkkkkk',
    'kmmmmmmk',
    'kkmmmmkk',
    '.kmkkmk.',
    '.kmk.km.',
]


def draw(rows):
    size = len(rows)
    image = Image.new('RGBA', (size, size), (0, 0, 0, 0))
    for y, row in enumerate(rows):
        assert len(row) == size, (row, size)
        for x, ch in enumerate(row):
            if ch != '.':
                image.putpixel((x, y), PALETTE[ch] + (255,))
    return image


def main():
    OUT.mkdir(parents=True, exist_ok=True)
    for name, rows in ICONS.items():
        draw(rows).save(OUT / f'{name}.png')
    draw(BUTTON).save(OUT / 'button.png')
    print(f'{len(ICONS) + 1} icons -> {OUT}')


if __name__ == '__main__':
    main()
