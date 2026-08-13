#!/usr/bin/env python3
"""Generate short GPLv3 station SFX as 16-bit mono WAV for res/raw."""
from __future__ import annotations

import math
import random
import struct
import wave
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "app/src/main/res/raw"
RATE = 16000


def write_wav(name: str, samples: list[float]) -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    path = OUT / f"{name}.wav"
    clipped = [max(-1.0, min(1.0, s)) for s in samples]
    with wave.open(str(path), "w") as w:
        w.setnchannels(1)
        w.setsampwidth(2)
        w.setframerate(RATE)
        frames = b"".join(struct.pack("<h", int(s * 32000)) for s in clipped)
        w.writeframes(frames)
    print("wrote", path, "samples", len(samples))


def sine(freq: float, t: float) -> float:
    return math.sin(2 * math.pi * freq * t)


def env(i: int, n: int, attack: float = 0.02, release: float = 0.2) -> float:
    a = int(n * attack)
    r = int(n * release)
    if i < a:
        return i / max(1, a)
    if i > n - r:
        return max(0.0, (n - i) / max(1, r))
    return 1.0


def hum_loop() -> list[float]:
    n = int(RATE * 6)
    rng = random.Random(7)
    out = []
    for i in range(n):
        t = i / RATE
        s = 0.22 * sine(50, t) + 0.08 * sine(100, t) + 0.04 * sine(49.2, t)
        s += 0.015 * sine(240, t) * (0.5 + 0.5 * sine(0.07, t))
        if rng.random() < 0.00012:
            s += (rng.random() * 2 - 1) * 0.35
        out.append(s * 0.55)
    # fade edges for seamless-ish loop
    fade = int(RATE * 0.04)
    for i in range(fade):
        out[i] *= i / fade
        out[-1 - i] *= i / fade
    return out


def tick() -> list[float]:
    n = int(RATE * 0.03)
    rng = random.Random(1)
    return [rng.uniform(-1, 1) * 0.4 * env(i, n, 0.05, 0.7) for i in range(n)]


def lock() -> list[float]:
    n = int(RATE * 0.1)
    out = []
    for i in range(n):
        t = i / RATE
        f = 420 + 380 * (i / n)
        out.append(0.45 * sine(f, t) * env(i, n, 0.08, 0.35))
    return out


def reject() -> list[float]:
    n = int(RATE * 0.09)
    out = []
    for i in range(n):
        t = i / RATE
        f = 280 - 90 * (i / n)
        out.append(0.4 * (sine(f, t) + 0.5 * sine(f * 1.37, t)) * env(i, n, 0.05, 0.4))
    return out


def solve() -> list[float]:
    n = int(RATE * 0.42)
    notes = [330, 415, 523]
    out = [0.0] * n
    for k, f in enumerate(notes):
        start = int(k * RATE * 0.09)
        leng = int(RATE * 0.22)
        for i in range(leng):
            if start + i >= n:
                break
            t = i / RATE
            out[start + i] += 0.28 * sine(f, t) * env(i, leng, 0.06, 0.45)
    return out


def reel() -> list[float]:
    n = int(RATE * 0.055)
    rng = random.Random(3)
    out = []
    for i in range(n):
        t = i / RATE
        s = 0.3 * sine(90 + i * 8, t) + 0.2 * rng.uniform(-1, 1)
        out.append(s * env(i, n, 0.04, 0.5))
    return out


def relay() -> list[float]:
    n = int(RATE * 0.07)
    out = []
    for i in range(n):
        t = i / RATE
        s = 0.5 * sine(180, t) * math.exp(-i / (RATE * 0.012))
        if i < 40:
            s += 0.4 * math.sin(i)
        out.append(s * env(i, n, 0.02, 0.5))
    return out


def main() -> None:
    write_wav("hum_loop", hum_loop())
    write_wav("tick", tick())
    write_wav("lock", lock())
    write_wav("reject", reject())
    write_wav("solve", solve())
    write_wav("reel", reel())
    write_wav("relay", relay())


if __name__ == "__main__":
    main()
