package com.windanesz.apprenticearcana.registry;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.fml.common.Mod;

/**
 * @author WinDanesz
 */
@Mod.EventBusSubscriber
public class LootRegistry {

	private static LootTable JOURNEY_LOW_TIER;

	private LootRegistry() {}

	public static void preInit() {

		// adventures
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/journey_low_tier"));
	}

}
