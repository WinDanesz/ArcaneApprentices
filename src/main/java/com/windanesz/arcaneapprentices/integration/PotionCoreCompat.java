package com.windanesz.arcaneapprentices.integration;

import com.tmtravlr.potioncore.ConfigLoader;

public class PotionCoreCompat {

	public static void setPotionCoreHealthSetting() {
		ConfigLoader.fixChangingDimensions = false;
	}
}
