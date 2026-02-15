package net.runelite.client.plugins.agentcontroller;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.MenuAction;

@Slf4j
class ActionHandler
{
	private final Client client;

	ActionHandler(Client client)
	{
		this.client = client;
	}

	void handle(String actionJson)
	{
		try
		{
			JsonObject action = new JsonParser().parse(actionJson).getAsJsonObject();
			String type = action.has("type") ? action.get("type").getAsString() : null;

			if (type == null || type.equals("none"))
			{
				return;
			}

			switch (type)
			{
				case "toggle_prayer":
					togglePrayer(action);
					break;
				default:
					log.warn("Unknown action type: {}", type);
					break;
			}
		}
		catch (Exception e)
		{
			log.warn("Failed to parse action: {}", actionJson, e);
		}
	}

	private void togglePrayer(JsonObject action)
	{
		String prayerName = action.get("prayer").getAsString();
		PrayerMap.Entry info = PrayerMap.PRAYERS.get(prayerName);
		if (info == null)
		{
			log.warn("Unknown prayer: {}", prayerName);
			return;
		}

		boolean isActive = client.getVarbitValue(info.prayer.getVarbit()) > 0;
		String option = isActive ? "Deactivate" : "Activate";
		log.info("{} {}", option, prayerName);

		client.menuAction(-1, info.widgetId, MenuAction.CC_OP, 1, -1, option, prayerName);
	}
}
