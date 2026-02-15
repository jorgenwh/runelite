from __future__ import annotations

from dataclasses import dataclass, asdict


@dataclass(frozen=True, slots=True)
class Observation:
    tick: int
    hp: int
    hp_max: int
    prayer: int
    prayer_max: int
    x: int
    y: int
    plane: int

    @classmethod
    def from_dict(cls, d: dict) -> Observation:
        return cls(**{k: d[k] for k in cls.__slots__})


@dataclass(frozen=True, slots=True)
class Action:
    type: str
    prayer: str | None = None

    def to_dict(self) -> dict:
        return {k: v for k, v in asdict(self).items() if v is not None}

    @classmethod
    def none(cls) -> Action:
        return _ACTION_NONE


_ACTION_NONE = Action(type="none")
