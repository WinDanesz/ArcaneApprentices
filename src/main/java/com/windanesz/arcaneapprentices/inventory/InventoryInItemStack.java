package com.windanesz.arcaneapprentices.inventory;

import com.windanesz.arcaneapprentices.items.ItemArtefactWithSlots;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.InventoryBasic;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class InventoryInItemStack extends InventoryBasic {

	private final ItemStack stack;

	public InventoryInItemStack(String title, boolean customName, IItemWithSlots itemWithSlots, ItemStack stack) {
		super(title, customName, itemWithSlots.getSlotCount());
		this.stack = stack;
	}

	public int getRowCount() {
		return ((ItemArtefactWithSlots) stack.getItem()).getRowCount();
	}

	public int getColumnCount() {
		return ((ItemArtefactWithSlots) stack.getItem()).getColumnCount();
	}

	@Override
	public void openInventory(EntityPlayer player) {
		super.openInventory(player);
		NBTTagCompound nbt = this.stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}

		if (nbt.hasKey("Items")) {

			NBTTagList items = nbt.getTagList("Items", 10);

			for (int i = 0; i < items.tagCount(); ++i) {
				NBTTagCompound nbttagcompound = items.getCompoundTagAt(i);
				int j = nbttagcompound.getByte("Slot") & 255;

				if (j < this.getSizeInventory()) {
					this.setInventorySlotContents(j, new ItemStack(nbttagcompound));
				}
			}
		}
	}

	@Override
	public void closeInventory(EntityPlayer player) {

		NBTTagCompound nbt = this.stack.getTagCompound();
		if (nbt == null) {
			nbt = new NBTTagCompound();
		}

		NBTTagList items = new NBTTagList();

		for (int i = 0; i < this.getSizeInventory(); ++i) {
			ItemStack itemstack = this.getStackInSlot(i);

			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				items.appendTag(nbttagcompound);
			}
		}

		nbt.setTag("Items", items);
		stack.setTagCompound(nbt);
	}

	@Override
	public boolean isItemValidForSlot(int index, ItemStack stack) {
		if (stack.getItem() instanceof ItemArtefactWithSlots) {
			return false;
		}
		return super.isItemValidForSlot(index, stack);
	}
}