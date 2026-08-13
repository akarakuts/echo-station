#!/usr/bin/env python3
"""Скриншоты витрины Echo Station: нативное 9:16, без обрезки заголовков."""
from __future__ import annotations

import os
import re
import shutil
import subprocess
import sys
import time
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
OUT = ROOT / "docs" / "store-screenshots"
SHOT_W, SHOT_H = 1080, 1920
PKG = "ru.akarakuts.echostation"
ACT = f"{PKG}/.MainActivity"
UID = Path("/tmp/echo_uid.xml")
ADB = os.environ.get("ADB", "adb")


def adb(*args: str, check: bool = True) -> subprocess.CompletedProcess:
    return subprocess.run([ADB, *args], check=check, capture_output=True)


def dump_root() -> ET.Element:
    adb("shell", "uiautomator", "dump", "/sdcard/uid.xml")
    adb("pull", "/sdcard/uid.xml", str(UID))
    return ET.parse(UID).getroot()


def nodes() -> list[ET.Element]:
    return list(dump_root().iter("node"))


def bounds(n: ET.Element) -> tuple[int, int, int, int] | None:
    m = re.match(r"\[(\d+),(\d+)\]\[(\d+),(\d+)\]", n.attrib.get("bounds", ""))
    if not m:
        return None
    return tuple(int(x) for x in m.groups())  # type: ignore[return-value]


def tap_xy(x: int, y: int) -> None:
    adb("shell", "input", "tap", str(x), str(y))


def tap_center(n: ET.Element) -> bool:
    b = bounds(n)
    if not b:
        return False
    tap_xy((b[0] + b[2]) // 2, (b[1] + b[3]) // 2)
    return True


def find_node(pred) -> ET.Element | None:
    for n in nodes():
        if pred(n):
            return n
    return None


def wait_pred(pred, tries: int = 25, delay: float = 0.35) -> ET.Element | None:
    for _ in range(tries):
        n = find_node(pred)
        if n is not None:
            return n
        time.sleep(delay)
    return None


def text_is(*vals: str):
    return lambda n: n.attrib.get("text", "") in vals


def desc_is(*vals: str):
    return lambda n: n.attrib.get("content-desc", "") in vals


def cap(path: Path) -> None:
    adb("shell", "input", "keyevent", "224", check=False)
    time.sleep(0.25)
    data = subprocess.check_output([ADB, "exec-out", "screencap", "-p"])
    path.write_bytes(data)
    print("cap", path.name, len(data))


def ffmpeg_vf(src: Path, dst: Path, vf: str) -> None:
    subprocess.check_call(
        [
            "ffmpeg", "-y", "-nostdin", "-hide_banner", "-loglevel", "error",
            "-i", str(src), "-vf", vf, "-frames:v", "1", str(dst),
        ]
    )


def set_store_display() -> None:
    # Скрыть status/nav bar и поставить кадр 9:16 — заголовки не режутся кропом.
    adb("shell", "settings", "put", "global", "policy_control", "immersive.full=*")
    adb("shell", "wm", "size", f"{SHOT_W}x{SHOT_H}")
    time.sleep(1.4)


def restore_display() -> None:
    adb("shell", "settings", "delete", "global", "policy_control", check=False)
    adb("shell", "wm", "size", "reset", check=False)


def tap_pred(pred, what: str) -> None:
    n = wait_pred(pred)
    if n is None or not tap_center(n):
        raise SystemExit(f"не найден элемент: {what}")
    time.sleep(0.9)


def publish_store(src: Path, dst: Path) -> None:
    """Убрать status/nav bar, сохранить заголовки, добить кадр до 1080×1920."""
    from PIL import Image

    im = Image.open(src).convert("RGB")
    pw, ph = im.size
    if pw != SHOT_W or ph != SHOT_H:
        tmp = dst.with_suffix(".fit.png")
        ffmpeg_vf(
            src,
            tmp,
            f"scale={SHOT_W}:{SHOT_H}:force_original_aspect_ratio=decrease,"
            f"pad={SHOT_W}:{SHOT_H}:(ow-iw)/2:(oh-ih)/2:color=0x12161C",
        )
        im = Image.open(tmp).convert("RGB")
        tmp.unlink(missing_ok=True)
    top, bot = 48, 40
    body = im.crop((0, top, SHOT_W, SHOT_H - bot))
    out = Image.new("RGB", (SHOT_W, SHOT_H), (0x12, 0x16, 0x1C))
    out.paste(body, (0, 80))
    out.save(dst, "PNG")


def main() -> None:
    OUT.mkdir(parents=True, exist_ok=True)
    store = OUT / f"store_{SHOT_W}x{SHOT_H}"
    store.mkdir(parents=True, exist_ok=True)

    adb("shell", "input", "keyevent", "224", check=False)
    time.sleep(0.3)
    try:
        set_store_display()
        adb("shell", "pm", "clear", PKG, check=False)
        time.sleep(0.5)
        adb("shell", "am", "force-stop", PKG, check=False)
        time.sleep(0.3)
        adb("shell", "am", "start", "-n", ACT)
        home = lambda n: n.attrib.get("content-desc") == "night shift" or n.attrib.get("text") in (
            "Ночная смена",
            "Night shift",
        )
        wait_pred(home)
        time.sleep(0.8)
        cap(OUT / "01_ru_home.png")

        tap_pred(home, "night shift")
        wait_pred(text_is("Карта ночи", "Night map"))
        time.sleep(0.5)
        cap(OUT / "02_ru_hub.png")

        tap_pred(
            lambda n: n.attrib.get("text", "").startswith("Продолжить")
            or n.attrib.get("text", "").startswith("Continue"),
            "continue",
        )
        time.sleep(0.8)
        got = find_node(text_is("Понятно", "Got it"))
        if got is not None:
            tap_center(got)
            time.sleep(0.7)
        cap(OUT / "03_ru_puzzle_wave.png")

        adb("shell", "input", "keyevent", "4")
        time.sleep(0.6)
        conf = find_node(text_is("Подтвердить", "Confirm"))
        if conf is not None:
            tap_center(conf)
            time.sleep(0.5)
        adb("shell", "input", "keyevent", "4")
        time.sleep(0.8)
        wait_pred(home)
        tap_pred(desc_is("settings"), "settings")
        time.sleep(0.5)
        cap(OUT / "04_ru_settings.png")

        adb("shell", "input", "keyevent", "4")
        time.sleep(0.7)
        tap_pred(desc_is("archive"), "archive")
        time.sleep(0.5)
        cap(OUT / "05_ru_archive.png")
    finally:
        restore_display()

    for f in sorted(OUT.glob("0*.png")):
        publish_store(f, store / f.name)
        print("store", store / f.name)

    dest = os.environ.get("STORE_COPY_TO", "").strip()
    if not dest:
        p = ROOT / "store-copy.dir"
        if p.is_file():
            dest = p.read_text().splitlines()[0].strip()
    if dest:
        Path(dest).mkdir(parents=True, exist_ok=True)
        for f in sorted(store.glob("0*.png")):
            shutil.copy2(f, Path(dest) / f.name)
        print("copy", dest)

    print("OK", store)


if __name__ == "__main__":
    try:
        main()
    except subprocess.CalledProcessError as e:
        sys.stderr.write((e.stderr or e.stdout or b"").decode() + "\n")
        raise
