package net.runelite.client.plugins.agentcontroller;

import com.google.gson.JsonObject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;

class ObservationBuilder
{
	private static final int CYCLES_PER_TICK = 30;

	private final Client client;

	private NPC vorkath;
	private String meleeAttack;

	ObservationBuilder(Client client)
	{
		this.client = client;
	}

	void onMeleeAttack()
	{
		meleeAttack = "melee";
	}

	NPC getVorkath()
	{
		return vorkath;
	}

	void reset()
	{
		vorkath = null;
		meleeAttack = null;
	}

	JsonObject build()
	{
		findVorkath();
		boolean inFight = vorkath != null && !vorkath.isDead();

		String attack = null;
		int attackTicks = -1;

		if (inFight)
		{
			for (Projectile p : client.getProjectiles())
			{
				String type = projectileToAttack(p.getId());
				if (type != null)
				{
					attack = type;
					attackTicks = p.getRemainingCycles() / CYCLES_PER_TICK;
					break;
				}
			}

			if (attack == null && meleeAttack != null)
			{
				attack = meleeAttack;
				attackTicks = 0;
			}
		}

		meleeAttack = null;

		JsonObject obs = new JsonObject();
		obs.addProperty("tick", client.getTickCount());
		obs.addProperty("in_fight", inFight);
		obs.addProperty("vorkath_hp", vorkath != null ? vorkath.getHealthRatio() : -1);
		obs.addProperty("vorkath_hp_scale", vorkath != null ? vorkath.getHealthScale() : -1);
		if (attack != null)
		{
			obs.addProperty("attack", attack);
		}
		else
		{
			obs.add("attack", null);
		}
		obs.addProperty("attack_ticks", attackTicks);
		obs.addProperty("hp", client.getBoostedSkillLevel(Skill.HITPOINTS));
		obs.addProperty("hp_max", client.getRealSkillLevel(Skill.HITPOINTS));
		obs.addProperty("prayer", client.getBoostedSkillLevel(Skill.PRAYER));
		obs.addProperty("prayer_max", client.getRealSkillLevel(Skill.PRAYER));

		return obs;
	}

	private void findVorkath()
	{
		vorkath = null;
		for (NPC npc : client.getNpcs())
		{
			int id = npc.getId();
			if (id == NpcID.VORKATH || id == NpcID.VORKATH_QUEST)
			{
				vorkath = npc;
				return;
			}
		}
	}

	private static String projectileToAttack(int id)
	{
		switch (id)
		{
			case SpotanimID.VORKATH_RANGED_TRAVEL:
				return "ranged";
			case SpotanimID.VORKATH_MAGIC_TRAVEL:
				return "magic";
			case SpotanimID.VORKATH_AREA_TRAVEL:
				return "dragonfire";
			case SpotanimID.VORKATH_ACID_TRAVEL:
				return "acid";
			case SpotanimID.VORKATH_SPAWN_TRAVEL:
				return "spawn";
			default:
				return null;
		}
	}
}
