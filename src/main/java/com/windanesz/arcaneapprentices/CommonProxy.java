package com.windanesz.arcaneapprentices;

import com.windanesz.arcaneapprentices.integration.PotionCoreCompat;
import net.minecraftforge.fml.common.Loader;

public class CommonProxy {

	/**
	 * Called from init() in the main mod class to initialise the particle factories.
	 */
	public void registerParticles() {}

	/**
	 * Called from preInit() in the main mod class to initialise the renderers.
	 */
	public void registerRenderers() {}

	public void init() {

	}

	public void postInit() {
		if (Loader.isModLoaded("potioncore") && Settings.generalSettings.POTIONCORE_COMPAT_FIX) {
			PotionCoreCompat.setPotionCoreHealthSetting();
		}
	}

	public void registerExtraHandbookContent() {}

	/** Called from the Antique Atlas integration to register marker icons. Does nothing on the server side. */
	public void registerAtlasMarkers() {}
}
