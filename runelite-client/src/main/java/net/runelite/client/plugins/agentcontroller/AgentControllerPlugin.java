package net.runelite.client.plugins.agentcontroller;

import com.google.gson.Gson;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.gameval.AnimationID;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.game.NPCManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import okhttp3.OkHttpClient;

@Slf4j
@PluginDescriptor(
	name = "Agent Controller",
	description = "WebSocket bridge for external agent control",
	enabledByDefault = false
)
public class AgentControllerPlugin extends Plugin
{
	@Inject
	private Client client;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private Gson gson;

	@Inject
	private AgentControllerConfig config;

	@Inject
	private NPCManager npcManager;

	private AgentWebSocket ws;
	private ActionHandler actionHandler;
	private ObservationBuilder observationBuilder;

	@Provides
	AgentControllerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AgentControllerConfig.class);
	}

	@Override
	protected void startUp()
	{
		ws = new AgentWebSocket(okHttpClient);
		actionHandler = new ActionHandler(client);
		observationBuilder = new ObservationBuilder(client, npcManager);
		ws.connect();
	}

	@Override
	protected void shutDown()
	{
		ws.disconnect();
		observationBuilder.reset();
	}

	@Subscribe
	public void onAnimationChanged(AnimationChanged event)
	{
		if (observationBuilder.getVorkath() != null
			&& event.getActor() == observationBuilder.getVorkath()
			&& observationBuilder.getVorkath().getAnimation() == AnimationID.DS2_VORKATH_ATTACK_MELEE)
		{
			observationBuilder.onMeleeAttack();
		}
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (!ws.isConnected())
		{
			ws.connect();
			return;
		}

		if (!config.active())
		{
			return;
		}

		String action = ws.poll();
		if (action != null)
		{
			actionHandler.handle(action);
		}

		ws.send(gson.toJson(observationBuilder.build()));
	}
}
