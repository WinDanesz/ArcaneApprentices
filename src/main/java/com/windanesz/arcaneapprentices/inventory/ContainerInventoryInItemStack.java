package com.windanesz.arcaneapprentices.inventory;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.items.ItemArtefactWithSlots;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ContainerInventoryInItemStack extends Container {
	private final IInventory itemInventory;

	public static final String BACKGROUND = ArcaneApprentices.MODID + ":gui/empty_main_hand_slot";

	public ContainerInventoryInItemStack(IInventory playerInventory, IInventory itemInventory, EntityPlayer player) {
		this.itemInventory = itemInventory;
		itemInventory.openInventory(player);

		if (itemInventory instanceof InventoryInItemStack) {

			int rowCount = ((InventoryInItemStack) itemInventory).getRowCount();
			int columnCount = ((InventoryInItemStack) itemInventory).getColumnCount();
			int index = 0;

			int offsetX = itemInventory.getSizeInventory() == 9 ? 0 : -54;

			// inventory
			// rows
			for (int k = 0; k < rowCount; ++k) {
				// columns
				for (int l = 0; l < columnCount; ++l) {
					this.addSlotToContainer(new Slot(itemInventory, index, 62 + l * 18 + offsetX, 18 + k * 18) {
						@Override
						public boolean isItemValid(ItemStack stack) {
							if (stack.getItem() instanceof ItemArtefactWithSlots) return false;
							return super.isItemValid(stack);
						}
					});
					index++;
				}
			}

			// player's inventory
			for (int i1 = 0; i1 < 3; ++i1) {
				for (int k1 = 0; k1 < 9; ++k1) {
					this.addSlotToContainer(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 88 + i1 * 18));
				}
			}

			// player's inventory hotbar
			for (int j1 = 0; j1 < 9; ++j1) {
				this.addSlotToContainer(new Slot(playerInventory, j1, 8 + j1 * 18, 146));
			}
		}
	}

	//	public static int[] calculateGridSize(int n) {
	//		int rows = 1;
	//		int columns = 1;
	//
	//		if (n == 1) {
	//			return new int[]{rows, columns};
	//		} else if (n == 2) {
	//			columns = 2;
	//		} else if (n == 3) {
	//			columns = 4;
	//		} else if (n == 4) {
	//			rows = 2;
	//			columns = 2;
	//		} else if (n == 5) {
	//			rows = 1;
	//			columns = 5;
	//		} else if (n == 6) {
	//			rows = 2;
	//			columns = 3;
	//		} else if (n == 7) {
	//			rows = 1;
	//			columns = 7;
	//		} else if (n == 8) {
	//			rows = 2;
	//			columns = 8;
	//		} else if (n == 9) {
	//			rows = 3;
	//			columns = 3;
	//		}
	//		return new int[]{rows, columns};
	//	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.itemInventory.isUsableByPlayer(playerIn);
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			if (index < this.itemInventory.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.itemInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
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
			} else if (this.itemInventory.getSizeInventory() <= 2 || !this.mergeItemStack(itemstack1, 2, this.itemInventory.getSizeInventory(), false)) {
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
		this.itemInventory.closeInventory(playerIn);
	}

}
