package com.windanesz.apprenticearcana;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Arrays;
import java.util.List;

public final class Utils {

	private Utils() {} // no instances

	/**
	 * Shorthand method to do instance check and sideonly checks for player messages
	 */
	public static void sendMessage(Entity player, String translationKey, boolean actionBar, Object... args) {
		if (player instanceof EntityPlayer && !player.world.isRemote) {
			((EntityPlayer) player).sendStatusMessage(new TextComponentTranslation(translationKey, args), actionBar);
		}
	}

	public static String generateWizardName(World world) {
		return (Arrays.asList(Settings.generalSettings.WIZARD_NAMES)).get(world.rand.nextInt(Arrays.asList(Settings.generalSettings.WIZARD_NAMES).size()));
	}

	/**
	 * Static method to give an itemstack to a player. Handles side checks and null checks, prioritizes the hands.
	 *
	 * @param player the player who receives the item
	 * @param stack  the stack to give
	 * @return false if failed to give, true if successfully gave the item
	 */
	public static boolean giveStackToPlayer(EntityPlayer player, ItemStack stack) {
		if (player != null && stack != null && !stack.isEmpty()) {

			if (!player.world.isRemote) {

				if (player.getHeldItemMainhand().isEmpty()) {
					// main hand
					player.setHeldItem(EnumHand.MAIN_HAND, stack);
				} else if (player.getHeldItemOffhand().isEmpty()) {
					// offhand
					player.setHeldItem(EnumHand.OFF_HAND, stack);
				} else {
					// any slot
					if (!player.inventory.addItemStackToInventory(stack)) {
						// or just drop the item...
						player.dropItem(stack, false);
					}
				}

				return true;
			}
		}

		return false;
	}

	public static <T> T getRandomItem(List<T> list) {
		if (list == null || list.isEmpty()) {
			throw new IllegalArgumentException("List cannot be null or empty");
		}

		int randomIndex = ApprenticeArcana.rand.nextInt(list.size());
		return list.get(randomIndex);
	}

	public static <T extends Entity> List<T> getEntitiesWithinRadius(double radius, double x, double y, double z, World world, Class<T> entityType){
		AxisAlignedBB aabb = new AxisAlignedBB(x - radius, y - radius, z - radius, x + radius, y + radius, z + radius);
		List<T> entityList = world.getEntitiesWithinAABB(entityType, aabb);
		for(int i = 0; i < entityList.size(); i++){
			if(entityList.get(i).getDistance(x, y, z) > radius){
				entityList.remove(i);
				break;
			}
		}
		return entityList;
	}

}
