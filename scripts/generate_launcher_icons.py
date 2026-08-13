#!/usr/bin/env python3
"""Иконки лаунчера и витрины 512×512 — тот же рисунок, что adaptive-icon (XML)."""
from __future__ import annotations

import sys
from pathlib import Path

try:
    from PIL import Image, ImageDraw
except ImportError as e:
    print("Установите Pillow: python3 -m venv .venv-icon && .venv-icon/bin/pip install Pillow", file=sys.stderr)
    raise SystemExit(1) from e

ROOT = Path(__file__).resolve().parents[1]
RES = ROOT / "app/src/main/res"
# Viewport adaptive-icon / vector: 108×108, фон #12161C.
BG = (0x12, 0x16, 0x1C, 255)
WAVE_TEAL = (0x3A, 0x7C, 0xA5, 255)
WAVE_GLOW = (0x6E, 0xC1, 0xE4, 255)
WAVE_AMBER = (0xE8, 0xA5, 0x4B, 255)


def _quad(p0: tuple[float, float], p1: tuple[float, float], p2: tuple[float, float], n: int = 64) -> list[tuple[float, float]]:
    pts: list[tuple[float, float]] = []
    for i in range(n + 1):
        t = i / n
        u = 1.0 - t
        x = u * u * p0[0] + 2 * u * t * p1[0] + t * t * p2[0]
        y = u * u * p0[1] + 2 * u * t * p1[1] + t * t * p2[1]
        pts.append((x, y))
    return pts


def draw_icon(size: int) -> Image.Image:
    """Рисует ic_launcher_foreground.xml на ic_launcher_background.xml."""
    supersample = 8
    canvas = 108 * supersample
    img = Image.new("RGBA", (canvas, canvas), BG)
    d = ImageDraw.Draw(img)

    def stroke(parts: list[tuple[tuple[float, float], tuple[float, float], tuple[float, float]]], color, width: float) -> None:
        pts: list[tuple[float, float]] = []
        for i, (p0, c, p1) in enumerate(parts):
            sampled = _quad(p0, c, p1)
            if i:
                sampled = sampled[1:]
            pts.extend(sampled)
        scaled = [(x * supersample, y * supersample) for x, y in pts]
        d.line(scaled, fill=color, width=max(1, round(width * supersample)))

    # path 1: M30,54 Q42,30 54,54 Q66,78 78,54  stroke #3A7CA5 w=4
    stroke([((30, 54), (42, 30), (54, 54)), ((54, 54), (66, 78), (78, 54))], WAVE_TEAL, 4)
    # path 2: M28,54 Q40,28 54,54 Q68,80 80,54  stroke #6EC1E4 w=4
    stroke([((28, 54), (40, 28), (54, 54)), ((54, 54), (68, 80), (80, 54))], WAVE_GLOW, 4)
    # path 3: M28,54 Q40,40 54,54 Q68,68 80,54  stroke #E8A54B w=3
    stroke([((28, 54), (40, 40), (54, 54)), ((54, 54), (68, 68), (80, 54))], WAVE_AMBER, 3)
    # path 4: круг (78, 34) r=6 fill #E8A54B
    cx = 78 * supersample
    cy = 34 * supersample
    r = 6 * supersample
    d.ellipse((cx - r, cy - r, cx + r, cy + r), fill=WAVE_AMBER)

    return img.resize((size, size), Image.Resampling.LANCZOS)


def main() -> None:
    mip = {
        "mipmap-mdpi": 48,
        "mipmap-hdpi": 72,
        "mipmap-xhdpi": 96,
        "mipmap-xxhdpi": 144,
        "mipmap-xxxhdpi": 192,
    }
    store = draw_icon(512)
    (ROOT / "docs").mkdir(exist_ok=True)
    rustore_docs = ROOT / "docs" / "rustore"
    rustore_docs.mkdir(parents=True, exist_ok=True)
    store.save(ROOT / "docs" / "ic_launcher_512.png", "PNG")
    store.save(rustore_docs / "ic_launcher_store_512.png", "PNG")

    for folder, dim in mip.items():
        out_dir = RES / folder
        out_dir.mkdir(parents=True, exist_ok=True)
        im = draw_icon(dim)
        im.save(out_dir / "ic_launcher.webp", "WEBP", quality=92, method=6)
        im.save(out_dir / "ic_launcher_round.webp", "WEBP", quality=92, method=6)

    print("OK: mipmaps + docs/ic_launcher_512.png + docs/rustore/ic_launcher_store_512.png")


if __name__ == "__main__":
    main()
