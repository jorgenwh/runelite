from agent.base import BaseAgent
from agent.protocol import Action, Observation


class DemoAgent(BaseAgent):
    """Logs Vorkath observations and responds with no action."""

    def on_tick(self, obs: Observation) -> Action:
        print(
            f"tick={obs.tick} in_fight={obs.in_fight} "
            f"vorkath_hp={obs.vorkath_hp}/{obs.vorkath_hp_scale} "
            f"attack={obs.attack} attack_ticks={obs.attack_ticks} "
            f"hp={obs.hp}/{obs.hp_max} prayer={obs.prayer}/{obs.prayer_max}"
        )
        return Action.none()
