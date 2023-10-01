package com.windanesz.arcaneapprentices.spell.override;

import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import electroblob.wizardry.event.ResurrectionEvent;
import electroblob.wizardry.packet.PacketResurrection;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.Resurrection;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.EnumHand;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.Comparator;

public class ResurrectionOverride extends Resurrection {

	public ResurrectionOverride(int originalNetworkID) {
		super();
		Utils.overrideDefaultSpell(this, originalNetworkID);
	}

	@Override
	public boolean cast(World world, EntityLiving caster, EnumHand hand, int ticksInUse, EntityLivingBase target, SpellModifiers modifiers) {
		if (!(caster instanceof EntityWizardInitiate)) return false;

		int waitTime = (int)(getProperty(WAIT_TIME).floatValue() / modifiers.get(SpellModifiers.POTENCY));
		double radius = getProperty(EFFECT_RADIUS).doubleValue() * modifiers.get(WizardryItems.range_upgrade);

		EntityPlayerMP nearestDeadAlly = caster.getServer().getPlayerList().getPlayers().stream()
				.filter(p -> !p.isEntityAlive() && p.deathTime > waitTime && (AllyDesignationSystem.isAllied(p, caster) || p.getUniqueID().equals(((EntityWizardInitiate) caster).getOwnerId()))
						&& p.getDistanceSq(caster) < radius * radius)
				.min(Comparator.comparingDouble(caster::getDistanceSq))
				.orElse(null);


		if(nearestDeadAlly != null){

			// hacky but I'm passing nearestDeadAlly as the caster...
			if(MinecraftForge.EVENT_BUS.post(new ResurrectionEvent(nearestDeadAlly, nearestDeadAlly))) return false;

			// When the player entity dies, it is removed from world#loadedEntityList. However, it is NOT removed
			// from playerEntityList (and probably a few other places) until respawn is clicked, and since that
			// never happens here we need to clean up those references or the player will have duplicate entries
			// in some entity lists - and weirdness will ensue!
			world.removeEntity(nearestDeadAlly); // Clean up the old entity references
			resurrect(nearestDeadAlly); // Reset isDead, must be before spawning the player again
			world.spawnEntity(nearestDeadAlly); // Re-add the player to all the relevant entity lists

			// Notify clients to reset the appropriate fields, spawn particles and play sounds
			IMessage msg = new PacketResurrection.Message(nearestDeadAlly.getEntityId());
			WizardryPacketHandler.net.sendToDimension(msg, caster.dimension);

			caster.getServer().getPlayerList().sendMessage(new TextComponentTranslation(
					"spell." + this.getRegistryName() + ".resurrect_ally", nearestDeadAlly.getDisplayName(), caster.getDisplayName()));

			return true;
		}

		return super.cast(world, caster, hand, ticksInUse, target, modifiers);
	}

	@Override
	public boolean canBeCastBy(EntityLiving npc, boolean override) {
		return true;
	}
}
