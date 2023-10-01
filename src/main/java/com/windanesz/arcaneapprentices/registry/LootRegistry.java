package com.windanesz.arcaneapprentices.registry;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.data.JourneyType;
import com.windanesz.arcaneapprentices.loot.BiomeLootCondition;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.conditions.LootConditionManager;
import net.minecraftforge.fml.common.Mod;

/**
 * @author WinDanesz
 */
@Mod.EventBusSubscriber
public class LootRegistry {

	public static final ResourceLocation STRUCTURES = new ResourceLocation(ArcaneApprentices.MODID, "subsets/structures");
	private LootRegistry() {}

	public static ResourceLocation getLootTableFor(JourneyType type) {
		return new ResourceLocation(ArcaneApprentices.MODID, "adventure/" + type.toString().toLowerCase());
	}

	public static void preInit() {

		LootConditionManager.registerCondition(new BiomeLootCondition.Serializer());

		LootTableList.register(STRUCTURES);
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_short_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_short_duration_axe"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_short_duration_pickaxe"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_medium_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_medium_duration_axe"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_medium_duration_pickaxe"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_long_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_long_duration_axe"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_long_duration_pickaxe"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/slay_mobs_short_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/slay_mobs_medium_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/slay_mobs_long_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/adventure_short_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/adventure_medium_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/adventure_long_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_short_duration_shears"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_medium_duration_shears"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/gather_long_duration_shears"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/nether_adventure_short_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/nether_adventure_medium_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/nether_adventure_long_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/ocean_adventure_short_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/ocean_adventure_medium_duration"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "adventure/ocean_adventure_long_duration"));

		// subsets
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/oak"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/spruce"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/birch"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/jungle"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/acacia"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/dark_oak"));

		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/animal_drops"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/mob_drops"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/random_items_multiple"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/rare_items"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/plants"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/nether_blocks"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/nether_mobs"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/ocean_mobs"));
		LootTableList.register(new ResourceLocation(ArcaneApprentices.MODID, "subsets/ocean_monument"));

	}

}
