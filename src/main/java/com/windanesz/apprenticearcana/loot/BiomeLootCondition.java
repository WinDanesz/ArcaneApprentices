package com.windanesz.apprenticearcana.loot;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.windanesz.apprenticearcana.ApprenticeArcana;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.conditions.LootCondition;

import java.util.Random;

public class BiomeLootCondition implements LootCondition {
	private final JsonArray requiredBiomes;

	public static final String BIOME_NAME_LIST_TAG = "required_biomes";

	public BiomeLootCondition(JsonArray requiredBiomes) {
		if (requiredBiomes == null) {
			throw new IllegalArgumentException("Required biomes array cannot be null.");
		}
		this.requiredBiomes = requiredBiomes;
	}

	public boolean testCondition(Random rand, LootContext context) {
		EntityPlayer player = (EntityPlayer) context.getKillerPlayer();
		if (player != null) {
			ResourceLocation playerBiome = player.world.getBiome(new BlockPos(player.posX, player.posY, player.posZ)).getRegistryName();
			for (int i = 0; i < requiredBiomes.size(); i++) {
				ResourceLocation requiredBiome = new ResourceLocation(JsonUtils.getString(requiredBiomes.get(i), ""));
				if (playerBiome.equals(requiredBiome)) {
					return true;
				}
			}
		}
		return false;
	}

	public static class Serializer extends LootCondition.Serializer<BiomeLootCondition> {

		public Serializer() {
			super(new ResourceLocation(ApprenticeArcana.MODID, "biome_condition"), BiomeLootCondition.class);
		}

		public void serialize(JsonObject json, BiomeLootCondition value, JsonSerializationContext context) {
			json.add(BIOME_NAME_LIST_TAG, value.requiredBiomes);
		}

		public BiomeLootCondition deserialize(JsonObject object, JsonDeserializationContext context) {
			JsonArray requiredBiomes = JsonUtils.getJsonArray(object, BIOME_NAME_LIST_TAG);
			return new BiomeLootCondition(requiredBiomes);
		}
	}
}
