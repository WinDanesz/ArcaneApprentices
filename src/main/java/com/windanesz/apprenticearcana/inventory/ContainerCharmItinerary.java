package com.windanesz.apprenticearcana.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;

public class ContainerCharmItinerary extends Container {

	public ContainerCharmItinerary() {
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return true;
	}

}
