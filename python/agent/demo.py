from agent.base import BaseAgent
from agent.protocol import Action, Observation


class DemoAgent(BaseAgent):
    """Toggles Protect from Melee every 100 ticks."""

    def on_tick(self, obs: Observation) -> Action:
        if obs.tick % 100 == 0:
            return Action(type="toggle_prayer", prayer="PROTECT_FROM_MELEE")
        return Action.none()
