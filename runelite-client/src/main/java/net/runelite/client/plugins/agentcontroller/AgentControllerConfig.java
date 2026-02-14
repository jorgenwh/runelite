package net.runelite.client.plugins.agentcontroller;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup(AgentControllerConfig.GROUP)
public interface AgentControllerConfig extends Config
{
	String GROUP = "agentcontroller";

	@ConfigItem(
		keyName = "active",
		name = "Active",
		description = "Send observations and receive actions each tick"
	)
	default boolean active()
	{
		return false;
	}
}
