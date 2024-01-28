package com.windanesz.arcaneapprentices.spell;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.data.PlayerData;
import com.windanesz.arcaneapprentices.data.StoredEntity;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.wizardryutils.tools.WizardryUtilsTools;
import electroblob.wizardry.block.BlockTransportationStone;
import electroblob.wizardry.item.SpellActions;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class RecallApprentices extends Spell {
	public static final String SUMMON_RADIUS = "summon_radius";

	public RecallApprentices() {
		super(ArcaneApprentices.MODID, "recall_apprentices", SpellActions.SUMMON, false);
		addProperties(SUMMON_RADIUS);
	}

	@Override

	public boolean cast(World world, EntityPlayer caster, EnumHand hand, int ticksInUse, SpellModifiers modifiers) {
		boolean flag = false;
		if (!BlockTransportationStone.testForCircle(world, caster.getPosition())) {
			WizardryUtilsTools.sendMessage(caster, "spell.arcaneapprentices:recall_apprentices.no_circle", false);
			return false;
		}

		if (!world.isRemote) {
			flag = recallApprenticesFromJourneys(caster, world);

			List<UUID> nearby = PlayerData.getApprentices(caster);
			if (!nearby.isEmpty()) {
				int range = getProperty(SUMMON_RADIUS).intValue();

				for (UUID nearbyApprentice : nearby) {
					Entity mob = ((WorldServer) world).getEntityFromUuid(nearbyApprentice);
					if (mob instanceof EntityWizardInitiate) {
						// Try and find a nearby floor space
						BlockPos pos = BlockUtils.findNearbyFloorSpace(caster, range, range * 2);

						if (pos != null) {
							mob.setPosition(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
							if (world.spawnEntity(mob)) {
								flag = true;
							}
						}
					}
				}

			}
		} else {
			ParticleBuilder.create(ParticleBuilder.Type.DUST).vel(0, 0.1, 0).fade(1f, 1f, 1f).spin(0.8f, 0.03f).time(40).entity(caster).scale(1.2f).spawn(world);
		}

		return flag;
	}

	private boolean recallApprenticesFromJourneys(EntityPlayer caster, World world) {
		boolean flag = false;

		List<StoredEntity> apprentices = PlayerData.getAdventuringApprentices(caster);
		if (apprentices.isEmpty()) {
			WizardryUtilsTools.sendMessage(caster, "spell.arcaneapprentices:recall_apprentices.no_apprentices_on_journey", false);
			return false;
		}

		List<UUID> respawnedEntities = new ArrayList<>();
		for (StoredEntity storedEntity : apprentices) {
			Entity mob = EntityList.createEntityFromNBT(storedEntity.getNbtTagCompound(), world);
			if (mob instanceof EntityWizardInitiate) {
				int range = getProperty(SUMMON_RADIUS).intValue();

				// Try and find a nearby floor space
				BlockPos pos = BlockUtils.findNearbyFloorSpace(caster, range, range * 2);

				if (pos != null) {
					mob.setPosition(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
					if (world.spawnEntity(mob)) {
						flag = true;
						respawnedEntities.add(mob.getUniqueID());
					}
				}
			}
		}
		for (UUID uuid : respawnedEntities) {
			PlayerData.removeAdventuringApprentice(caster, uuid);
		}

		return flag;
	}

	@Override
	public boolean canBeCastBy(TileEntityDispenser dispenser) {
		return false;
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return false;
	}
}
