from __future__ import annotations

from abc import ABC, abstractmethod

from agent.protocol import Action, Observation


class BaseAgent(ABC):
    """Override `on_tick` to implement agent logic."""

    @abstractmethod
    def on_tick(self, obs: Observation) -> Action:
        """Called each game tick with the latest observation. Return an action."""
        ...
