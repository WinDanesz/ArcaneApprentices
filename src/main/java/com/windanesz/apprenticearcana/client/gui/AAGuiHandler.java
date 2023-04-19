package com.windanesz.apprenticearcana.client.gui;

import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import com.windanesz.apprenticearcana.inventory.ContainerWizardInitiateInventory;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class AAGuiHandler implements IGuiHandler {

	/**
	 * Incrementable index for the gui ID
	 */
	private static int nextGuiId = 0;

	public static final int WIZARD_GUI = nextGuiId++;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == WIZARD_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				EntityWizardInitiate wizard = (EntityWizardInitiate) entity;
				return new ContainerWizardInitiateInventory(player.inventory, wizard.inventory, wizard, player);
			}
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {

		if (id == WIZARD_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				EntityWizardInitiate wizard = (EntityWizardInitiate) entity;
				return new GuiScreenWizardInitiateInventory(player.inventory, wizard.inventory, wizard);
			}
		}
		return null;
	}
}

