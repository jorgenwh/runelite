package net.runelite.client.plugins.agentcontroller;

import java.util.HashMap;
import java.util.Map;
import net.runelite.api.Prayer;
import net.runelite.api.gameval.InterfaceID;

final class PrayerMap
{
	static final Map<String, Entry> PRAYERS = new HashMap<>();

	static
	{
		PRAYERS.put("THICK_SKIN", new Entry(Prayer.THICK_SKIN, InterfaceID.Prayerbook.PRAYER1));
		PRAYERS.put("BURST_OF_STRENGTH", new Entry(Prayer.BURST_OF_STRENGTH, InterfaceID.Prayerbook.PRAYER2));
		PRAYERS.put("CLARITY_OF_THOUGHT", new Entry(Prayer.CLARITY_OF_THOUGHT, InterfaceID.Prayerbook.PRAYER3));
		PRAYERS.put("SHARP_EYE", new Entry(Prayer.SHARP_EYE, InterfaceID.Prayerbook.PRAYER4));
		PRAYERS.put("MYSTIC_WILL", new Entry(Prayer.MYSTIC_WILL, InterfaceID.Prayerbook.PRAYER5));
		PRAYERS.put("ROCK_SKIN", new Entry(Prayer.ROCK_SKIN, InterfaceID.Prayerbook.PRAYER6));
		PRAYERS.put("SUPERHUMAN_STRENGTH", new Entry(Prayer.SUPERHUMAN_STRENGTH, InterfaceID.Prayerbook.PRAYER7));
		PRAYERS.put("IMPROVED_REFLEXES", new Entry(Prayer.IMPROVED_REFLEXES, InterfaceID.Prayerbook.PRAYER8));
		PRAYERS.put("RAPID_RESTORE", new Entry(Prayer.RAPID_RESTORE, InterfaceID.Prayerbook.PRAYER9));
		PRAYERS.put("RAPID_HEAL", new Entry(Prayer.RAPID_HEAL, InterfaceID.Prayerbook.PRAYER10));
		PRAYERS.put("PROTECT_ITEM", new Entry(Prayer.PROTECT_ITEM, InterfaceID.Prayerbook.PRAYER11));
		PRAYERS.put("HAWK_EYE", new Entry(Prayer.HAWK_EYE, InterfaceID.Prayerbook.PRAYER12));
		PRAYERS.put("MYSTIC_LORE", new Entry(Prayer.MYSTIC_LORE, InterfaceID.Prayerbook.PRAYER13));
		PRAYERS.put("STEEL_SKIN", new Entry(Prayer.STEEL_SKIN, InterfaceID.Prayerbook.PRAYER14));
		PRAYERS.put("ULTIMATE_STRENGTH", new Entry(Prayer.ULTIMATE_STRENGTH, InterfaceID.Prayerbook.PRAYER15));
		PRAYERS.put("INCREDIBLE_REFLEXES", new Entry(Prayer.INCREDIBLE_REFLEXES, InterfaceID.Prayerbook.PRAYER16));
		PRAYERS.put("PROTECT_FROM_MAGIC", new Entry(Prayer.PROTECT_FROM_MAGIC, InterfaceID.Prayerbook.PRAYER17));
		PRAYERS.put("PROTECT_FROM_MISSILES", new Entry(Prayer.PROTECT_FROM_MISSILES, InterfaceID.Prayerbook.PRAYER18));
		PRAYERS.put("PROTECT_FROM_MELEE", new Entry(Prayer.PROTECT_FROM_MELEE, InterfaceID.Prayerbook.PRAYER19));
		PRAYERS.put("EAGLE_EYE", new Entry(Prayer.EAGLE_EYE, InterfaceID.Prayerbook.PRAYER20));
		PRAYERS.put("MYSTIC_MIGHT", new Entry(Prayer.MYSTIC_MIGHT, InterfaceID.Prayerbook.PRAYER21));
		PRAYERS.put("RETRIBUTION", new Entry(Prayer.RETRIBUTION, InterfaceID.Prayerbook.PRAYER22));
		PRAYERS.put("REDEMPTION", new Entry(Prayer.REDEMPTION, InterfaceID.Prayerbook.PRAYER23));
		PRAYERS.put("SMITE", new Entry(Prayer.SMITE, InterfaceID.Prayerbook.PRAYER24));
		PRAYERS.put("CHIVALRY", new Entry(Prayer.CHIVALRY, InterfaceID.Prayerbook.PRAYER25));
		PRAYERS.put("PIETY", new Entry(Prayer.PIETY, InterfaceID.Prayerbook.PRAYER26));
		PRAYERS.put("RIGOUR", new Entry(Prayer.RIGOUR, InterfaceID.Prayerbook.PRAYER27));
		PRAYERS.put("AUGURY", new Entry(Prayer.AUGURY, InterfaceID.Prayerbook.PRAYER28));
		PRAYERS.put("PRESERVE", new Entry(Prayer.PRESERVE, InterfaceID.Prayerbook.PRAYER29));
	}

	static class Entry
	{
		final Prayer prayer;
		final int widgetId;

		Entry(Prayer prayer, int widgetId)
		{
			this.prayer = prayer;
			this.widgetId = widgetId;
		}
	}

	private PrayerMap()
	{
	}
}
