#!/usr/bin/env python3
"""Generate levels JSON, story rewards JSON, and docs/LEVELS.md for Echo Station."""
from __future__ import annotations

import json
import math
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
SCRIPTS = Path(__file__).resolve().parent
if str(SCRIPTS) not in sys.path:
    sys.path.insert(0, str(SCRIPTS))
from story_beats import BEATS, EPILOGUES
LEVELS_DIR = ROOT / "app/src/main/assets/levels"
STORY_DIR = ROOT / "app/src/main/assets/story"
DOCS = ROOT / "docs"


def puzzle_for(level: int) -> str:
    # Act 1: wave tutorial, early cable, then mix — no 18-wave slog.
    if level <= 6:
        return "WAVE"
    if level <= 8:
        return "CABLE"
    if level <= 12:
        return "WAVE"
    if level <= 16:
        return "CABLE"
    if level <= 18:
        return "WAVE"
    if level <= 24:
        return "CABLE"
    if level <= 26:
        return "WAVE"
    if level <= 30:
        return "CABLE"
    if level <= 36:
        return "CASSETTE"
    if level <= 38:
        return "CABLE"
    if level <= 40:
        return "CASSETTE"
    if level <= 43:
        return "FREQUENCY"
    if level in (60, 68, 72):
        return "MULTI"
    if level <= 74:
        return ["WAVE", "CABLE", "CASSETTE", "FREQUENCY"][(level - 44) % 4]
    return "MULTI"


def difficulty(level: int) -> int:
    if level <= 5:
        return 1
    if level <= 20:
        return 1 + (level - 1) // 5
    if level <= 50:
        return min(5, 2 + (level - 21) // 8)
    if level <= 74:
        return min(5, 3 + (level - 51) // 8)
    return 5


def act_for(level: int) -> int:
    if level <= 20:
        return 1
    if level <= 50:
        return 2
    return 3


def wave_params(level: int, diff: int, envelope: str = "sine") -> dict:
    hide_amp = level <= 2
    use_freq = level >= 16 and diff >= 3
    drift = 0.0 if diff < 3 or level <= 5 else round(0.010 + diff * 0.003, 3)
    glitch_every = 0
    glitch_frames = 0
    if level >= 31 and diff >= 3:
        glitch_every = 90 + (level % 20)
        glitch_frames = 18
    return {
        "targetPhase": round((level * 0.37) % (2 * math.pi), 4),
        "targetAmplitude": round(0.45 + (diff * 0.08), 3),
        "noiseSeed": level * 17 + 3,
        "tolerance": round(max(0.07, 0.20 - diff * 0.022), 3),
        "holdFrames": 10 + diff * 2,
        "targetFrequency": round(3.1 + (level % 5) * 0.38, 3),
        "useFrequency": use_freq,
        "drift": drift,
        "hideAmplitude": hide_amp,
        "glitchEvery": glitch_every,
        "glitchFrames": glitch_frames,
        "envelope": envelope,
    }


def cable_params(level: int, diff: int) -> dict:
    if level <= 8:
        pairs, mode, allow_cross = 2, "match", True
    elif level <= 16:
        pairs, mode, allow_cross = 3, "match", True
    elif level <= 24:
        pairs = 3 if diff <= 2 else 4
        mode, allow_cross = "untangle", False
    else:
        pairs = min(3 + diff // 2, 6)
        mode, allow_cross = "untangle", False
    highlight = 6 if pairs >= 4 and level in (19, 44, 76) else -1
    return {
        "pairCount": pairs,
        "allowCrossing": allow_cross,
        "seed": level * 31,
        "mode": mode,
        "lockCorrect": mode == "match",
        "hideDigits": mode == "match" and pairs >= 3,
        "highlightPairId": highlight if highlight < pairs else -1,
    }


def cassette_params(level: int, diff: int) -> dict:
    if level <= 33:
        cols, rows, adjacent, lock = 3, 2, False, False
    else:
        cols = 3 if diff <= 3 else 4
        rows = 2 if diff <= 3 else 3
        adjacent = diff >= 4
        lock = diff >= 3
    return {
        "cols": cols,
        "rows": rows,
        "seed": level * 13,
        "adjacentOnly": adjacent,
        "lockCorrect": lock,
    }


def frequency_params(level: int, diff: int) -> dict:
    if level <= 43:
        markers, extra = 2, 1
    else:
        markers = min(2 + diff // 2, 5)
        extra = 1 if diff >= 3 else 0
        if diff >= 5:
            extra = 2
    slot_count = markers + extra
    slots = [(i + 0.5) / slot_count for i in range(slot_count)]
    pool = list(range(slot_count))
    pick = []
    for i in range(markers):
        j = (level * 19 + i * 7) % len(pool)
        pick.append(pool.pop(j))
    return {
        "markerCount": markers,
        "slotCount": slot_count,
        "tolerance": 0.08 if diff >= 4 else 0.12,
        "seed": level * 19,
        "targets": [round(slots[i], 3) for i in pick],
        "assigned": True,
        "legend": level <= 43 or level == 50,
        "zones": True,
    }


def multi_params(level: int) -> dict:
    env = "double" if level >= 74 else "sine"
    if level == 60:
        steps = [
            {"type": "WAVE", "params": wave_params(level, 3)},
            {"type": "FREQUENCY", "params": frequency_params(level, 3)},
        ]
    elif level == 68:
        steps = [
            {"type": "CABLE", "params": cable_params(level, 3)},
            {"type": "CASSETTE", "params": cassette_params(level, 3)},
        ]
    elif level == 72:
        steps = [
            {"type": "WAVE", "params": wave_params(level, 4)},
            {"type": "CABLE", "params": cable_params(level, 3)},
            {"type": "FREQUENCY", "params": frequency_params(level, 3)},
        ]
    elif level < 80:
        steps = [
            {"type": "WAVE", "params": wave_params(level, 4, env)},
            {"type": "CABLE", "params": cable_params(level, 3)},
            {"type": "CASSETTE", "params": cassette_params(level, 3)},
        ]
    else:
        steps = [
            {"type": "WAVE", "params": wave_params(level, 5, "double")},
            {"type": "CABLE", "params": cable_params(level, 4)},
            {"type": "CASSETTE", "params": cassette_params(level, 4)},
            {"type": "FREQUENCY", "params": frequency_params(level, 4)},
        ]
    return {"steps": steps}


def params_for(ptype: str, level: int, diff: int) -> dict:
    if ptype == "WAVE":
        return wave_params(level, diff)
    if ptype == "CABLE":
        return cable_params(level, diff)
    if ptype == "CASSETTE":
        return cassette_params(level, diff)
    if ptype == "FREQUENCY":
        return frequency_params(level, diff)
    return multi_params(level)


def reward_for(level: int) -> dict:
    kind, title_ru, title_en, body_ru, body_en, archive, image = BEATS[level - 1]
    return {
        "id": f"r{level:03d}",
        "kind": kind,
        "titleRu": title_ru,
        "titleEn": title_en,
        "bodyRu": body_ru,
        "bodyEn": body_en,
        "imageAsset": image,
        "archiveKey": archive,
    }


def main() -> None:
    LEVELS_DIR.mkdir(parents=True, exist_ok=True)
    STORY_DIR.mkdir(parents=True, exist_ok=True)

    levels = []
    rewards = []
    md_lines = [
        "# Levels catalogue",
        "",
        "| Id | Act | Type | Diff | Reward | Notes |",
        "|----|-----|------|------|--------|-------|",
    ]

    tutorial_notes = {
        1: "Wave tutorial (phase only)",
        7: "Cable match tutorial",
        17: "Wave + carrier knob",
        21: "Cable untangle",
        31: "Cassette tutorial",
        41: "Frequency tutorial",
        60: "Early multi",
        75: "Multi finale start",
        80: "Letter + epilogue gate",
    }

    for level in range(1, 81):
        ptype = puzzle_for(level)
        diff = difficulty(level)
        act = act_for(level)
        rid = f"r{level:03d}"
        levels.append(
            {
                "id": level,
                "act": act,
                "puzzleType": ptype,
                "difficulty": diff,
                "params": params_for(ptype, level, diff),
                "storyRewardId": rid,
                "unlockPrevId": None if level == 1 else level - 1,
            }
        )
        rewards.append(reward_for(level))
        note = tutorial_notes.get(level, "")
        md_lines.append(f"| {level} | {act} | {ptype} | {diff} | {rid} | {note} |")

    ru_bodies = [r["bodyRu"] for r in rewards]
    if len(set(ru_bodies)) != 80:
        raise SystemExit("story beats must be unique (duplicate bodyRu)")

    rewards.extend(EPILOGUES)

    (LEVELS_DIR / "levels.json").write_text(
        json.dumps({"levels": levels}, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    (STORY_DIR / "rewards.json").write_text(
        json.dumps({"rewards": rewards}, ensure_ascii=False, indent=2) + "\n",
        encoding="utf-8",
    )
    (DOCS / "LEVELS.md").write_text("\n".join(md_lines) + "\n", encoding="utf-8")
    print(f"Wrote {len(levels)} levels, {len(rewards)} rewards")


if __name__ == "__main__":
    main()
