package com.windanesz.arcaneapprentices.handler;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.Settings;

public final class XpProgression {
	private static final int MAX_LEVEL = 30;
	private static final double BASE_XP = 100;
	private static final double XP_MULTIPLIER = 1.2;
	private static final double[] XP_TABLE = new double[MAX_LEVEL];

	public static int getMaxLevel() {
		return Settings.generalSettings.MAX_WIZARD_LEVEL;
	}

	public static int getXpGainPerKill() {
		return Settings.generalSettings.XP_GAIN_PER_KILL;
	}

	public static int getXpGainPerSpellCast() {
		return Settings.generalSettings.XP_GAIN_PER_SPELL_CAST;
	}

	static {
		// Initialize the XP_TABLE with calculated XP values for each level
		for (int i = 0; i < MAX_LEVEL; i++) {
			XP_TABLE[i] = calculateXpRequired(i + 1);
		}
	}

	private XpProgression() {}

	public static double calculateXpRequired(int level) {
		if (level < 1 || level > MAX_LEVEL) {
			ArcaneApprentices.logger.warn("Invalid level: " + level);
			return 0; // or any other default value
		}
		// Calculate the XP required for a specific level using the base XP and multiplier
		return BASE_XP * Math.pow(XP_MULTIPLIER, level - 1);
	}

	public static int calculateNextLevel(double currentXp) {
		// Find the next level based on the current XP
		for (int level = 1; level <= MAX_LEVEL; level++) {
			if (currentXp < XP_TABLE[level - 1]) {
				return level;
			}
		}
		return MAX_LEVEL;
	}

	public static double getXpForLevel(int targetLevel) {
		double totalXp = 0;

		for (int i = 0; i < targetLevel; i++) {
			if (i >= XP_TABLE.length) {
				// Handle cases where targetLevel exceeds the highest level in XP_TABLE
				return Double.POSITIVE_INFINITY;
			}
			totalXp += XP_TABLE[i];
		}

		return totalXp;
	}

	public static double calculateTotalXpRequired(int level) {
		if (level < 1 || level > MAX_LEVEL) {
			return 0; // or any other default value
		}
		double totalXp = 0;
		// Calculate the total XP required to reach a specific level by summing up the XP for each level
		for (int i = 1; i <= level; i++) {
			totalXp += calculateXpRequired(i);
		}
		return totalXp;
	}

	public static int getLevelForXp(double currentXp) {
		int level = 0;
		double totalXp = 0;

		for (double xp : XP_TABLE) {
			totalXp += xp;
			if (currentXp <= totalXp) {
				return level;
			}
			level++;
		}

		return level;
	}

	public static double calculateXpRequiredForNextLevel(double currentXp) {
//		int currentLevel = getLevelForXp(currentXp);
//		if (currentLevel < MAX_LEVEL) {
//			// Calculate the XP required to reach the next level based on the current XP
//			double xpRequiredForNextLevel = XP_TABLE[currentLevel];
//			return xpRequiredForNextLevel - currentXp;
//		}
		return 0; // Max level reached, no XP required for next level
	}
}
