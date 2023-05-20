package com.windanesz.apprenticearcana.inventory;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import electroblob.wizardry.item.ItemWand;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IContainerListener;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ContainerWizardInitiateInventory extends Container {
	private final IInventory wizardInventory;
	private final EntityWizardInitiate wizard;
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD,
			EntityEquipmentSlot.CHEST,
			EntityEquipmentSlot.LEGS,
			EntityEquipmentSlot.FEET};

	public ContainerWizardInitiateInventory(IInventory playerInventory, IInventory inventory, final EntityWizardInitiate wizard, EntityPlayer player) {
		this.wizardInventory = inventory;
		this.wizard = wizard;
		int i = 3;
		inventory.openInventory(player);
		int j = -18;

		//main hand
		this.addSlotToContainer(new Slot(inventory, 0, 62, 64) {
//			public boolean isItemValid(ItemStack stack) {
//				return !this.getHasStack();
//			}

			//
			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return true;
			}
		});


		//		//offhand
		//		this.addSlotToContainer(new Slot(inventory, 0, 46, 64) {
		//			//
		//			@SideOnly(Side.CLIENT)
		//			public boolean isEnabled() {
		//				return true;
		//			}
		//		});

		// offhand bauble slot
		this.addSlotToContainer(new Slot(inventory, 1, 44, 64) {
//			public boolean isItemValid(ItemStack stack) {
//				return !this.getHasStack();
//			}

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return true;
			}

			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return ApprenticeArcana.MODID + ":textures/items/empty_shield_slot";
			}
		});

		// artefact bauble slot
		this.addSlotToContainer(new Slot(inventory, 22, 26, 64) {
			public boolean isItemValid(ItemStack stack) {
				return !this.getHasStack();
			}

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return true;
			}

			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return ApprenticeArcana.MODID + ":textures/items/empty_shield_slot";
			}
		});

		for (int k = 0; k < 4; ++k) {
			final EntityEquipmentSlot entityequipmentslot = VALID_EQUIPMENT_SLOTS[k];
			this.addSlotToContainer(new Slot(inventory, k + 2, 8, 10 + k * 18) {
				public int getSlotStackLimit() {
					return 1;
				}

				public boolean isItemValid(ItemStack stack) {
					return stack.getItem().isValidArmor(stack, entityequipmentslot, player);
				}

				@Nullable
				@SideOnly(Side.CLIENT)
				public String getSlotTexture() {
					return ItemArmor.EMPTY_SLOT_NAMES[entityequipmentslot.getIndex()];
				}
			});
		}

		// main inventory
		if (wizard instanceof EntityWizardInitiate) {
			for (int k = 0; k < 3; ++k) {
				for (int l = 0; l < ((EntityWizardInitiate) wizard).getInventoryColumns(); ++l) {



					this.addSlotToContainer(new Slot(inventory, 7 + l + k * ((EntityWizardInitiate) wizard).getInventoryColumns(), 80 + l * 18, 28 + k * 18));
				}
			}
		}

		// player inventory
		for (int i1 = 0; i1 < 3; ++i1) {
			for (int k1 = 0; k1 < 9; ++k1) {
				this.addSlotToContainer(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 132 + i1 * 18 + -18));
			}
		}

		// player inventory hotbar
		for (int j1 = 0; j1 < 9; ++j1) {
			this.addSlotToContainer(new Slot(playerInventory, j1, 8 + j1 * 18, 172));
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.wizardInventory.isUsableByPlayer(playerIn) && this.wizard.isEntityAlive() && this.wizard.getDistance(playerIn) < 8.0F;
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.wizardInventory.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.wizardInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack()) {
				if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.getSlot(0).isItemValid(itemstack1)) {
				if (!this.mergeItemStack(itemstack1, 0, 1, false)) {
					return ItemStack.EMPTY;
				}
			} else if (this.wizardInventory.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.wizardInventory.getSizeInventory(), false)) {
				return ItemStack.EMPTY;
			}

			if (itemstack1.isEmpty()) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

		}

		return itemstack;
	}

	public void onContainerClosed(EntityPlayer playerIn) {
		super.onContainerClosed(playerIn);
		this.wizardInventory.closeInventory(playerIn);
	}
}
