from __future__ import annotations

import json
from dataclasses import asdict
from pathlib import Path

from agent.base import BaseAgent
from agent.protocol import Action, Observation

DATA_DIR = Path("/home/jhe/vorkath_data/real")


class DemoAgent(BaseAgent):
    """Logs Vorkath observations, records fight data to disk."""

    def __init__(self) -> None:
        self._was_in_fight = False
        self._fight_num = _next_fight_num()
        self._ticks: list[dict] = []
        self._start_tick = 0

    def on_tick(self, obs: Observation) -> Action:
        print(
            f"tick={obs.tick} in_fight={obs.in_fight} "
            f"vorkath_hp={obs.vorkath_hp}/{obs.vorkath_hp_max} "
            f"attack={obs.attack} attack_ticks={obs.attack_ticks} "
            f"hp={obs.hp}/{obs.hp_max} prayer={obs.prayer}/{obs.prayer_max}"
        )

        if obs.in_fight and not self._was_in_fight:
            # Fight just started
            self._ticks = []
            self._start_tick = obs.tick
            print(f"[*] Fight {self._fight_num} started")

        if obs.in_fight:
            entry = asdict(obs)
            entry["tick"] = obs.tick - self._start_tick + 1
            self._ticks.append(entry)

        if not obs.in_fight and self._was_in_fight:
            # Fight just ended
            self._save()

        self._was_in_fight = obs.in_fight
        return Action.none()

    def _save(self) -> None:
        DATA_DIR.mkdir(parents=True, exist_ok=True)
        path = DATA_DIR / f"vorkath_{self._fight_num}.txt"
        path.write_text(json.dumps(self._ticks, indent=2))
        print(f"[*] Fight {self._fight_num} saved ({len(self._ticks)} ticks) -> {path}")
        self._fight_num += 1
        self._ticks = []


def _next_fight_num() -> int:
    """Find the next available fight number based on existing files."""
    if not DATA_DIR.exists():
        return 1
    existing = [
        int(p.stem.split("_")[1])
        for p in DATA_DIR.glob("vorkath_*.txt")
        if p.stem.split("_")[1].isdigit()
    ]
    return max(existing, default=0) + 1
