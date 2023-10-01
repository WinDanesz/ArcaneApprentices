package com.windanesz.arcaneapprentices.inventory;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import net.minecraft.inventory.Container;

public abstract class ContainerWizardBase extends Container {

	public abstract EntityWizardInitiate getWizard();
}
