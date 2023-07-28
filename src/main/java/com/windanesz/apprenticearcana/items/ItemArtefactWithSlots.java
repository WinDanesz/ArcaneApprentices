package com.windanesz.apprenticearcana.items;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.client.gui.AAGuiHandler;
import com.windanesz.apprenticearcana.inventory.IItemWithSlots;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;

public class ItemArtefactWithSlots extends ItemArtefact implements IItemWithSlots {

	private final int slotCount;

	private final int rows;
	private final int columns;
	private final boolean hasGUI;

	public ItemArtefactWithSlots(EnumRarity rarity, Type type, int rows, int columns, boolean hasGUI) {
		super(rarity, type);
		this.rows = rows;
		this.columns = columns;
		this.slotCount = rows * columns;
		this.hasGUI = hasGUI;
	}

	public int getRowCount() {
		return rows;
	}

	public int getColumnCount() {
		return columns;
	}

	@Override
	public int getSlotCount() {
		return this.slotCount;
	}

	@Override
	public boolean hasGUI() {
		return hasGUI;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemStack = player.getHeldItem(hand);

		player.openGui(ApprenticeArcana.MODID, AAGuiHandler.ARTEFACT_BAG_GUI, world, hand.ordinal(), 0, 0);
		//
		//		if (!world.isRemote) {
		//			InventoryInItemStack inventory = new InventoryInItemStack("test", true, this);
		//			inventory.readInventoryFromNBT(itemStack.getTag());
		//
		//			// Handle interaction with the bag's inventory here (e.g., GUI, adding/removing items)
		//
		//			inventory.writeInventoryToNBT(itemStack.getTag());
		//			player.sendMessage(new StringTextComponent("Bag inventory updated!"), player.getUniqueID());
		//		}
		//
		return new ActionResult<ItemStack>(EnumActionResult.PASS, itemStack);
	}

	public static boolean isSlotEmpty(ItemStack stack, int slot) {
		if (stack.getItem() instanceof ItemArtefactWithSlots) {
			int maxCount = ((ItemArtefactWithSlots) stack.getItem()).getSlotCount();

			if (slot <= maxCount) {
				if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Items")) {
					NBTTagList items = stack.getTagCompound().getTagList("Items", 10);

					NBTTagCompound nbttagcompound = items.getCompoundTagAt(slot);
					return (new ItemStack(nbttagcompound)).isEmpty();
				}
			}
		}
		return true;
	}

	public static ItemStack getItemForSlot(ItemStack stack, int slot) {
		if (stack.getItem() instanceof ItemArtefactWithSlots) {
			int maxCount = ((ItemArtefactWithSlots) stack.getItem()).getSlotCount();

			if (slot <= maxCount) {
				if (stack.hasTagCompound() && stack.getTagCompound().hasKey("Items")) {
					NBTTagList items = stack.getTagCompound().getTagList("Items", 10);

					NBTTagCompound nbttagcompound = items.getCompoundTagAt(slot);
					return new ItemStack(nbttagcompound);
				}
			}
		}
		return ItemStack.EMPTY;
	}

	public static void setItemForSlot(ItemStack bag, ItemStack itemStack, int slot) {
		if (bag.getItem() instanceof ItemArtefactWithSlots) {
			int maxCount = ((ItemArtefactWithSlots) bag.getItem()).getSlotCount();

			if (slot <= maxCount) {
				NBTTagCompound nbt = bag.getTagCompound();
				if (nbt == null) {
					nbt = new NBTTagCompound();
				}

				NBTTagList items = new NBTTagList();
				if (nbt.hasKey("Items")) {
					items = bag.getTagCompound().getTagList("Items", 10);
				}
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) slot);
				itemStack.writeToNBT(nbttagcompound);
				items.appendTag(nbttagcompound);

				nbt.setTag("Items", items);
				bag.setTagCompound(nbt);
			}
		}
	}
}
