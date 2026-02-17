package net.runelite.client.plugins.agentcontroller;

import com.google.gson.JsonObject;
import net.runelite.api.Client;
import net.runelite.api.NPC;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.api.gameval.NpcID;
import net.runelite.api.gameval.SpotanimID;
import net.runelite.client.game.NPCManager;

class ObservationBuilder
{
	private static final int CYCLES_PER_TICK = 30;

	private final Client client;
	private final NPCManager npcManager;

	private NPC vorkath;
	private String meleeAttack;

	ObservationBuilder(Client client, NPCManager npcManager)
	{
		this.client = client;
		this.npcManager = npcManager;
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
		int vorkathHp = -1;
		int vorkathHpMax = -1;
		if (vorkath != null)
		{
			Integer maxHp = npcManager.getHealth(vorkath.getId());
			if (maxHp != null)
			{
				vorkathHpMax = maxHp;
				int ratio = vorkath.getHealthRatio();
				int scale = vorkath.getHealthScale();
				if (ratio > 0 && scale > 0)
				{
					vorkathHp = (int) ((maxHp * (long) ratio / scale) + 0.5f);
				}
				else if (ratio == 0)
				{
					vorkathHp = 0;
				}
			}
		}
		obs.addProperty("vorkath_hp", vorkathHp);
		obs.addProperty("vorkath_hp_max", vorkathHpMax);
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
