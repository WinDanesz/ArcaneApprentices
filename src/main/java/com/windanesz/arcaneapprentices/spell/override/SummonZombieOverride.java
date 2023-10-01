package com.windanesz.arcaneapprentices.spell.override;

import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import electroblob.wizardry.entity.living.EntityHuskMinion;
import electroblob.wizardry.entity.living.EntityZombieMinion;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SummonZombie;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SummonZombieOverride extends SummonZombie {

	public SummonZombieOverride(int originalNetworkID) {
		super();
		Utils.overrideDefaultSpell(this, originalNetworkID);
	}

	@Override
	protected EntityZombieMinion createMinion(World world, EntityLivingBase caster, SpellModifiers modifiers) {
		if (caster instanceof EntityWizardInitiate) {
			EntityZombieMinion minion;
			if (((EntityWizardInitiate) caster).isArtefactActive(WizardryItems.charm_minion_variants)) {
				minion = new EntityHuskMinion(world);
			} else {
				minion = super.createMinion(world, caster, modifiers);
			}

			if (((EntityWizardInitiate) caster).isArtefactActive(WizardryItems.charm_undead_helmets)) {
				minion.setItemStackToSlot(EntityEquipmentSlot.HEAD, new ItemStack(Items.LEATHER_HELMET));
			}

			return minion;
		}
		return super.createMinion(world, caster, modifiers);
	}
}
