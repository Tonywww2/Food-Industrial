"""Contact sheet: rows of textures (name, [files]) scaled ×3 with captions."""
import sys, pathlib
from PIL import Image, ImageDraw


def sheet(rows, out, scale=3, cols_per_row=None):
    cell = 32 * scale + 6
    width = max(len(files) for _, files in rows) * cell + 170
    im = Image.new('RGB', (width, len(rows) * cell + 4), (45, 45, 45))
    d = ImageDraw.Draw(im)
    for r, (name, files) in enumerate(rows):
        d.text((4, r * cell + cell // 2 - 6), name[:26], fill=(230, 230, 230))
        for c, f in enumerate(files):
            if f is None or not pathlib.Path(f).exists():
                continue
            t = Image.open(f).convert('RGBA')
            t = t.resize((t.width * scale * 32 // t.width, t.height * scale * 32 // t.width), Image.NEAREST)
            bg = Image.new('RGBA', t.size, (45, 45, 45, 255))
            bg.alpha_composite(t)
            im.paste(bg.convert('RGB'), (170 + c * cell, r * cell + 3))
    im.save(out)
