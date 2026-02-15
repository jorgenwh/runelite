package net.runelite.client.plugins.agentcontroller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.inject.Provides;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.api.Player;
import net.runelite.api.Skill;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
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

	private AgentWebSocket ws;
	private ActionHandler actionHandler;

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
		ws.connect();
	}

	@Override
	protected void shutDown()
	{
		ws.disconnect();
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

		sendObservation();
	}

	private void sendObservation()
	{
		Player local = client.getLocalPlayer();
		if (local == null)
		{
			return;
		}

		WorldPoint pos = local.getWorldLocation();

		JsonObject obs = new JsonObject();
		obs.addProperty("tick", client.getTickCount());
		obs.addProperty("hp", client.getBoostedSkillLevel(Skill.HITPOINTS));
		obs.addProperty("hp_max", client.getRealSkillLevel(Skill.HITPOINTS));
		obs.addProperty("prayer", client.getBoostedSkillLevel(Skill.PRAYER));
		obs.addProperty("prayer_max", client.getRealSkillLevel(Skill.PRAYER));
		obs.addProperty("x", pos.getX());
		obs.addProperty("y", pos.getY());
		obs.addProperty("plane", pos.getPlane());

		ws.send(gson.toJson(obs));
	}
}
