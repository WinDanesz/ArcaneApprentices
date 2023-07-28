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

	private static LootTable GATHERING_SHORT_DURATION_GENERIC;

	private LootRegistry() {}

	public static void preInit() {

		// adventures
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gathering_short_duration_generic"));
	}

}
