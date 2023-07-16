package com.windanesz.apprenticearcana.inventory;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import net.minecraft.inventory.Container;

public abstract class ContainerWizardBase extends Container {

	public abstract EntityWizardInitiate getWizard();
}
