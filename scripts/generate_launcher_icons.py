#!/usr/bin/env python3
"""Generate solid-color mipmap placeholders + docs 512 icon for Echo Station."""
from __future__ import annotations

from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError:
    raise SystemExit("Install Pillow: python3 -m pip install Pillow")

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"
DOCS = ROOT / "docs"


def wave_icon(size: int) -> Image.Image:
    img = Image.new("RGBA", (size, size), (18, 22, 28, 255))
    d = ImageDraw.Draw(img)
    pad = size // 8
    # amber ghost
    pts_a = []
    pts_b = []
    for i in range(32):
        x = pad + (size - 2 * pad) * i / 31
        y1 = size / 2 + (size / 5) * __import__("math").sin(i / 31 * 6.28 + 0.5)
        y2 = size / 2 + (size / 6) * __import__("math").sin(i / 31 * 6.28)
        pts_a.append((x, y1))
        pts_b.append((x, y2))
    d.line(pts_a, fill=(232, 165, 75, 200), width=max(2, size // 48))
    d.line(pts_b, fill=(110, 193, 228, 255), width=max(2, size // 40))
    r = size // 14
    d.ellipse((size * 0.68, size * 0.28 - r, size * 0.68 + 2 * r, size * 0.28 + r), fill=(232, 165, 75, 255))
    return img


def main() -> None:
    sizes = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    for folder, size in sizes.items():
        out = RES / folder
        out.mkdir(parents=True, exist_ok=True)
        icon = wave_icon(size)
        icon.save(out / "ic_launcher.webp", "WEBP")
        icon.save(out / "ic_launcher_round.webp", "WEBP")
    DOCS.mkdir(exist_ok=True)
    wave_icon(512).save(DOCS / "ic_launcher_512.png")
    print("icons ok")


if __name__ == "__main__":
    main()
