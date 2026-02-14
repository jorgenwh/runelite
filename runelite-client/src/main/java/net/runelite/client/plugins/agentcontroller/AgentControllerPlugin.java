package net.runelite.client.plugins.agentcontroller;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
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
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.WebSocket;
import okhttp3.WebSocketListener;

@Slf4j
@PluginDescriptor(
	name = "Agent Controller",
	description = "WebSocket bridge for external agent control",
	enabledByDefault = false
)
public class AgentControllerPlugin extends Plugin
{
	private static final String WS_URL = "ws://localhost:8765";

	@Inject
	private Client client;

	@Inject
	private OkHttpClient okHttpClient;

	@Inject
	private Gson gson;

	@Inject
	private AgentControllerConfig config;

	private WebSocket webSocket;
	private volatile boolean connected;
	private volatile String pendingAction;

	@Provides
	AgentControllerConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(AgentControllerConfig.class);
	}

	@Override
	protected void startUp()
	{
		connect();
	}

	@Override
	protected void shutDown()
	{
		disconnect();
	}

	private void connect()
	{
		if (webSocket != null)
		{
			return;
		}

		Request request = new Request.Builder()
			.url(WS_URL)
			.build();

		webSocket = okHttpClient.newWebSocket(request, new WebSocketListener()
		{
			@Override
			public void onOpen(WebSocket ws, Response response)
			{
				log.info("Agent WebSocket connected to {}", WS_URL);
				connected = true;
			}

			@Override
			public void onMessage(WebSocket ws, String text)
			{
				pendingAction = text;
			}

			@Override
			public void onClosed(WebSocket ws, int code, String reason)
			{
				log.info("Agent WebSocket closed: {}/{}", code, reason);
				connected = false;
				webSocket = null;
			}

			@Override
			public void onFailure(WebSocket ws, Throwable t, Response response)
			{
				log.warn("Agent WebSocket error", t);
				connected = false;
				webSocket = null;
			}
		});
	}

	@Subscribe
	public void onGameTick(GameTick tick)
	{
		if (client.getGameState() != GameState.LOGGED_IN)
		{
			return;
		}

		if (!connected)
		{
			connect();
			return;
		}

		if (!config.active())
		{
			return;
		}

		String action = pendingAction;
		pendingAction = null;
		if (action != null)
		{
			handleAction(action);
		}

		sendObservation();
	}

	private void sendObservation()
	{
		Player local = client.getLocalPlayer();
		if (local == null || webSocket == null)
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

		String json = gson.toJson(obs);
		webSocket.send(json);
	}

	private void handleAction(String actionJson)
	{
		try
		{
			JsonObject action = new JsonParser().parse(actionJson).getAsJsonObject();
			String type = action.has("type") ? action.get("type").getAsString() : null;

			if (type == null || type.equals("none"))
			{
				return;
			}

			log.info("Received action: {}", type);
			// Action handling will be expanded here
		}
		catch (Exception e)
		{
			log.warn("Failed to parse action: {}", actionJson, e);
		}
	}

	private void disconnect()
	{
		if (webSocket != null)
		{
			webSocket.close(1000, "Plugin shutting down");
			webSocket = null;
		}
		connected = false;
		pendingAction = null;
	}
}
