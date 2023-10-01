package com.windanesz.arcaneapprentices.client.gui;

import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.inventory.ContainerCharmItinerary;
import com.windanesz.arcaneapprentices.inventory.ContainerInventoryInItemStack;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardInfo;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardInitiateInventory;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardInititateAdventure;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardInititateDismissal;
import com.windanesz.arcaneapprentices.inventory.IItemWithSlots;
import com.windanesz.arcaneapprentices.inventory.InventoryInItemStack;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class AAGuiHandler implements IGuiHandler {

	/**
	 * Incrementable index for the gui ID
	 */
	private static int nextGuiId = 0;

	public static final int WIZARD_INVENTORY_GUI = nextGuiId++;
	public static final int WIZARD_STATS_GUI = nextGuiId++;
	public static final int WIZARD_DISMISS_CONFIRM_GUI = nextGuiId++;
	public static final int WIZARD_ADVENTURING_GUI = nextGuiId++;
	public static final int ARTEFACT_BAG_GUI = nextGuiId++;
	public static final int ARTEFACT_ITINERARY_GUI = nextGuiId++;

	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == WIZARD_INVENTORY_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				EntityWizardInitiate wizard = (EntityWizardInitiate) entity;
				return new ContainerWizardInitiateInventory(player.inventory, wizard.inventory, wizard, player);
			}
		} else if (id == WIZARD_STATS_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				EntityWizardInitiate wizard = (EntityWizardInitiate) entity;
				return new ContainerWizardInfo(wizard, player);
			}
		} else if (id == WIZARD_DISMISS_CONFIRM_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				EntityWizardInitiate wizard = (EntityWizardInitiate) entity;
				return new ContainerWizardInititateDismissal(wizard, player);
			}
		} else if (id == WIZARD_ADVENTURING_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				EntityWizardInitiate wizard = (EntityWizardInitiate) entity;
				return new ContainerWizardInititateAdventure(wizard, player);
			}
		} else if (id == ARTEFACT_BAG_GUI) {
			ItemStack stack = player.getHeldItem(EnumHand.values()[x]);
			if (stack.getItem() instanceof IItemWithSlots) {
				InventoryInItemStack inventory = new InventoryInItemStack("test tile", true, (IItemWithSlots) stack.getItem(), stack);
				return new ContainerInventoryInItemStack(player.inventory, inventory, player);
			}
		} else if (id == ARTEFACT_ITINERARY_GUI) {
			return new ContainerCharmItinerary();
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == WIZARD_INVENTORY_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				EntityWizardInitiate wizard = (EntityWizardInitiate) entity;
				return new GuiScreenWizardInitiateInventory(player.inventory, wizard.inventory, wizard);
			}
 		} else  if (id == WIZARD_STATS_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				return new GuiScreenWizardInitiateStats((EntityWizardInitiate) entity);
			}
		} else if (id == WIZARD_DISMISS_CONFIRM_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				return new GuiScreenWizardInitiateDismissal((EntityWizardInitiate) entity);
			}
		} else if (id == WIZARD_ADVENTURING_GUI) {
			Entity entity = world.getEntityByID(x);
			if (entity instanceof EntityWizardInitiate) {
				return new GuiScreenWizardInitiateAdventure((EntityWizardInitiate) entity);
			}
		} else if (id == ARTEFACT_BAG_GUI) {
			ItemStack stack = player.getHeldItem(EnumHand.values()[x]);
			if (stack.getItem() instanceof IItemWithSlots) {
				InventoryInItemStack inventory = new InventoryInItemStack("Artefact", true, (IItemWithSlots) stack.getItem(), stack);
				return new GuiScreenInventoryInItem(inventory, player);
			}
		} else if (id == ARTEFACT_ITINERARY_GUI) {
			return new GuiScreenCharmItinerary();
		}
		return null;
	}
}

