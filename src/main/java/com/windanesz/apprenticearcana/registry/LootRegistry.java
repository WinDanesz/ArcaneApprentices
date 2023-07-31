package com.windanesz.apprenticearcana.registry;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.data.JourneyType;
import com.windanesz.apprenticearcana.loot.BiomeLootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.fml.common.Mod;

/**
 * @author WinDanesz
 */
@Mod.EventBusSubscriber
public class LootRegistry {

	public static final ResourceLocation STRUCTURES = new ResourceLocation(ApprenticeArcana.MODID, "subsets/structures");
	private LootRegistry() {}

	public static ResourceLocation getLootTableFor(JourneyType type) {
		return new ResourceLocation(ApprenticeArcana.MODID, "adventure/" + type.toString().toLowerCase());
	}

	public static void preInit() {

		LootConditionManager.registerCondition(new BiomeLootCondition.Serializer());

		LootTableList.register(STRUCTURES);
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_short_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_short_duration_axe"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_short_duration_pickaxe"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_medium_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_medium_duration_axe"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_medium_duration_pickaxe"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_long_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_long_duration_axe"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_long_duration_pickaxe"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/slay_mobs_short_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/slay_mobs_medium_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/slay_mobs_long_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/adventure_short_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/adventure_medium_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/adventure_long_duration"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_short_duration_shears"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_medium_duration_shears"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "adventure/gather_long_duration_shears"));

		// subsets
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/oak"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/spruce"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/birch"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/jungle"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/acacia"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/dark_oak"));

		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/animal_drops"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/mob_drops"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/random_items_multiple"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/rare_items"));
		LootTableList.register(new ResourceLocation(ApprenticeArcana.MODID, "subsets/plants"));

	}

}
