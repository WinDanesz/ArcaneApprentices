package com.windanesz.apprenticearcana;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Arrays;

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

}
