package com.windanesz.arcaneapprentices.inventory;

import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.wizardryutils.item.ItemNewArtefact;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;

public class WizardInventory extends InventoryBasic {

	public WizardInventory(String title, boolean customName, int slotCount) {
		super(title, customName, slotCount);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (index >= 2 && index <= 5) {
			return stack.getItem() instanceof ItemArmor;
		} else if (index == EntityWizardInitiate.ARTEFACT_SLOT) {
			return (stack.getItem() instanceof ItemArtefact || stack.getItem() instanceof ItemNewArtefact);
		}

		return super.isItemValidForSlot(index, stack);
	}
}
