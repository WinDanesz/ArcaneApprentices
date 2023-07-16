package com.windanesz.apprenticearcana.inventory;

import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerWizardInititateAdventure extends ContainerWizardBase {
	public final EntityWizardInitiate wizard;

	public ContainerWizardInititateAdventure(final EntityWizardInitiate wizard, EntityPlayer player) {
		this.wizard = wizard;
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.wizard.isEntityAlive() && this.wizard.getDistance(playerIn) < 8.0F;
	}

	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
	}

	@Override
	public EntityWizardInitiate getWizard() {
		return wizard;
	}
}
