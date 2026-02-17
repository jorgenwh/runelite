from __future__ import annotations

from dataclasses import dataclass, asdict
from typing import Optional


@dataclass(frozen=True, slots=True)
class Observation:
    tick: int
    in_fight: bool
    vorkath_hp: int
    vorkath_hp_max: int
    attack: Optional[str]
    attack_ticks: int
    hp: int
    hp_max: int
    prayer: int
    prayer_max: int

    @classmethod
    def from_dict(cls, d: dict) -> Observation:
        return cls(**{k: d.get(k) for k in cls.__slots__})


@dataclass(frozen=True, slots=True)
class Action:
    type: str
    prayer: Optional[str] = None

    def to_dict(self) -> dict:
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def none(cls) -> Action:
        return _ACTION_NONE


_ACTION_NONE = Action(type="none")
