package com.windanesz.arcaneapprentices.spell.override;

import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import electroblob.wizardry.entity.living.EntitySkeletonMinion;
import electroblob.wizardry.entity.living.EntityStrayMinion;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.spell.SummonSkeletonLegion;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class SummonSkeletonLegionOverride extends SummonSkeletonLegion {

	public SummonSkeletonLegionOverride(int originalNetworkID) {
		super();
		Utils.overrideDefaultSpell(this, originalNetworkID);
	}

	@Override
	protected EntitySkeletonMinion createMinion(World world, EntityLivingBase caster, SpellModifiers modifiers) {
		if (caster instanceof EntityWizardInitiate) {
			EntitySkeletonMinion minion;
			if (((EntityWizardInitiate) caster).isArtefactActive(WizardryItems.charm_minion_variants)) {
				minion = new EntityStrayMinion(world);
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
