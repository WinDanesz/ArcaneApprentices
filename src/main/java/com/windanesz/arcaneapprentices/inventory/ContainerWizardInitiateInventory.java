package com.windanesz.arcaneapprentices.inventory;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.data.Talent;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.handler.XpProgression;
import com.windanesz.arcaneapprentices.registry.AAItems;
import com.windanesz.wizardryutils.item.ItemNewArtefact;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;

public class ContainerWizardInitiateInventory extends ContainerWizardBase {
	private final IInventory wizardInventory;
	public final EntityWizardInitiate wizard;
	private static final EntityEquipmentSlot[] VALID_EQUIPMENT_SLOTS = new EntityEquipmentSlot[] {EntityEquipmentSlot.HEAD,
			EntityEquipmentSlot.CHEST,
			EntityEquipmentSlot.LEGS,
			EntityEquipmentSlot.FEET};

	public static final String EMPTY_MAIN_HAND_SLOT_BACKGROUND = ArcaneApprentices.MODID + ":gui/empty_main_hand_slot";
	public static final String EMPTY_ARTEFACT_SLOT_BACKGROUND = ArcaneApprentices.MODID + ":gui/empty_artefact_slot";
	public static final String LOCKED_SLOT = ArcaneApprentices.MODID + ":gui/locked_slot";

	public ContainerWizardInitiateInventory(IInventory playerInventory, IInventory inventory, final EntityWizardInitiate wizard, EntityPlayer player) {
		this.wizardInventory = inventory;
		this.wizard = wizard;
		int i = 3;
		inventory.openInventory(player);
		int j = -18;

		//main hand
		this.addSlotToContainer(new Slot(inventory, 0, 62, 64) {

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return true;
			}

			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return EMPTY_MAIN_HAND_SLOT_BACKGROUND;
			}
		});

		// offhand
		this.addSlotToContainer(new Slot(inventory, 1, 44, 64) {

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return true;
			}

			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return "minecraft:items/empty_armor_slot_shield";
			}
		});

		// wizard's equipment slots
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

		// wizard's main inventory
		// 3 rows
		for (int k = 0; k < 3; ++k) {
			// 5 columns
			for (int l = 0; l < wizard.getInventoryColumns(); ++l) {
				int index = 6 + l + k * wizard.getInventoryColumns();

				this.addSlotToContainer(new Slot(inventory, index, 80 + l * 18, 28 + k * 18) {

					@Override
					public boolean isItemValid(ItemStack stack) {
						if (isSlotUnlocked(this.slotNumber, wizard)) {
							return super.isItemValid(stack);
						}
						return false;
					}

					@Nullable
					@SideOnly(Side.CLIENT)
					public String getSlotTexture() {
						if ((slotNumber == 6 || slotNumber == 7) && wizard.hasTalentUnlocked() && wizard.getTalent() == Talent.ARTIFICE_MASTER) {
							return EMPTY_ARTEFACT_SLOT_BACKGROUND;
						}
						if (isSlotUnlocked(this.slotNumber, wizard)) {
							return super.getSlotTexture();
						}
						return LOCKED_SLOT;
					}
				});
			}
		}


		// artefact
		this.addSlotToContainer(new Slot(inventory, 21, 26, 64) {

			public boolean isItemValid(ItemStack stack) {
				return !this.getHasStack() && (stack.getItem() instanceof ItemArtefact || stack.getItem() instanceof ItemNewArtefact);
			}

			@SideOnly(Side.CLIENT)
			public boolean isEnabled() {
				return true;
			}

			@Nullable
			@SideOnly(Side.CLIENT)
			public String getSlotTexture() {
				return EMPTY_ARTEFACT_SLOT_BACKGROUND;
			}
		});


		// player's inventory
		for (int i1 = 0; i1 < 3; ++i1) {
			for (int k1 = 0; k1 < 9; ++k1) {
				this.addSlotToContainer(new Slot(playerInventory, k1 + i1 * 9 + 9, 8 + k1 * 18, 132 + i1 * 18 + -18));
			}
		}

		// player's inventory hotbar
		for (int j1 = 0; j1 < 9; ++j1) {
			this.addSlotToContainer(new Slot(playerInventory, j1, 8 + j1 * 18, 172));
		}
	}

	public boolean canInteractWith(EntityPlayer playerIn) {
		return this.wizardInventory.isUsableByPlayer(playerIn) && this.wizard.isEntityAlive() && this.wizard.getDistance(playerIn) < 8.0F;
	}

	public static boolean isSlotUnlocked(int slotNumber, EntityWizardInitiate wizard) {
		return wizard.isArtefactActive(AAItems.belt_strength) ||
				(((float) slotNumber - (float) 6) / (float) 15) <= (((float) wizard.getLevel() + 1) / XpProgression.getMaxLevel() + 0.1f);
	}

	public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
		// 0-21 are wizard inventory slots
		// 22+ are player inventory slots
		ItemStack itemstack = ItemStack.EMPTY;
		Slot slot = this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();
			inventorySlots.stream().forEach(i -> System.out.println("slotindex " + i.getSlotIndex() + " , slotnumber:: "  + i.slotNumber + i.getStack().getDisplayName()));


			EntityEquipmentSlot entityequipmentslot = EntityLiving.getSlotForItemStack(itemstack);

			if (entityequipmentslot == EntityEquipmentSlot.HEAD) {
				if (!inventorySlots.get(2).getHasStack() && this.getSlot(2).isItemValid(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 2, 3, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (entityequipmentslot == EntityEquipmentSlot.CHEST) {
				if (!inventorySlots.get(3).getHasStack() && this.getSlot(3).isItemValid(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 3, 4, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (entityequipmentslot == EntityEquipmentSlot.LEGS) {
				if (!inventorySlots.get(4).getHasStack() && this.getSlot(4).isItemValid(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 4, 5, false)) {
						return ItemStack.EMPTY;
					}
				}
			} else if (entityequipmentslot == EntityEquipmentSlot.FEET) {
				if (!inventorySlots.get(5).getHasStack() && this.getSlot(5).isItemValid(itemstack1)) {
					if (!this.mergeItemStack(itemstack1, 5, 6, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (index < this.wizardInventory.getSizeInventory()) {
				if (!this.mergeItemStack(itemstack1, this.wizardInventory.getSizeInventory(), this.inventorySlots.size(), true)) {
					return ItemStack.EMPTY;
				}
			} else

			if (itemstack1.getItem() instanceof ItemArtefact) {
				if (!inventorySlots.get(21).getHasStack()) {
					if (!this.mergeItemStack(itemstack1, 21, 22, false)) {
						return ItemStack.EMPTY;
					}
				}
			}

			if (!this.mergeItemStack(itemstack1, 0, this.wizardInventory.getSizeInventory(), false)) {
					return ItemStack.EMPTY;
			}
			System.out.println();
				//} else if (this.getSlot(1).isItemValid(itemstack1) && !this.getSlot(1).getHasStack()) {
				//	if (!this.mergeItemStack(itemstack1, 1, 2, false)) {
				//	return ItemStack.EMPTY;
				//	}

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

	@Override
	public EntityWizardInitiate getWizard() {
		return wizard;
	}
}
