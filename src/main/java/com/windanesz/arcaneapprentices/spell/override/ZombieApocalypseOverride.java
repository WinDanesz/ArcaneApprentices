package com.windanesz.arcaneapprentices.spell.override;

import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import electroblob.wizardry.entity.construct.EntityZombieSpawner;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.ZombieApocalypse;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.EnumFacing;

import javax.annotation.Nullable;

public class ZombieApocalypseOverride extends ZombieApocalypse {

	public ZombieApocalypseOverride(int originalNetworkID) {
		super();
		Utils.overrideDefaultSpell(this, originalNetworkID);
	}

	@Override
	protected void addConstructExtras(EntityZombieSpawner construct, EnumFacing side, @Nullable EntityLivingBase caster, SpellModifiers modifiers){
		construct.spawnHusks = caster instanceof EntityWizardInitiate && ((EntityWizardInitiate) caster).isArtefactActive(WizardryItems.charm_minion_variants)
				|| caster instanceof EntityPlayer && ItemArtefact.isArtefactActive((EntityPlayer) caster, WizardryItems.charm_minion_variants);
	}
}
