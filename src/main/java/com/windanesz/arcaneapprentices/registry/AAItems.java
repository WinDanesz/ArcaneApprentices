package com.windanesz.arcaneapprentices.registry;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.items.ItemArtefactWithSlots;
import com.windanesz.arcaneapprentices.items.ItemCharmItinerary;
import com.windanesz.wizardryutils.registry.ItemRegistry;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.item.EnumRarity;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@ObjectHolder(ArcaneApprentices.MODID)
@Mod.EventBusSubscriber
public final class AAItems {


	private AAItems() {} // No instances!

	public static final Item belt_strength = placeholder();
	public static final Item belt_explorer = placeholder();
	public static final Item charm_bag_9 = placeholder();
	public static final Item charm_bag_27 = placeholder();
	public static final Item charm_eternal_grimoire = placeholder();
	public static final Item charm_spell_compass = placeholder();
	public static final Item charm_talent_detector = placeholder();
	public static final Item charm_treasure_map = placeholder();
	public static final Item charm_withering_atlas = placeholder();
	public static final Item charm_golden_lure = placeholder();
	public static final Item charm_itinerary = placeholder();
	public static final Item ring_serendipity = placeholder();
	public static final Item amulet_survival_chance = placeholder();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {

		IForgeRegistry<Item> registry = event.getRegistry();
		ItemRegistry.registerItemArtefact(registry, "belt_strength", ArcaneApprentices.MODID, new ItemArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));
		ItemRegistry.registerItemArtefact(registry, "belt_explorer", ArcaneApprentices.MODID, new ItemArtefact(EnumRarity.RARE, ItemArtefact.Type.BELT));
		ItemRegistry.registerItemArtefact(registry, "charm_bag_9", ArcaneApprentices.MODID, new ItemArtefactWithSlots(EnumRarity.UNCOMMON, ItemArtefact.Type.CHARM, 3,3, true));
		ItemRegistry.registerItemArtefact(registry, "charm_bag_27", ArcaneApprentices.MODID, new ItemArtefactWithSlots(EnumRarity.EPIC, ItemArtefact.Type.CHARM, 3, 9, true));
		ItemRegistry.registerItemArtefact(registry, "charm_eternal_grimoire", ArcaneApprentices.MODID, new ItemArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		ItemRegistry.registerItemArtefact(registry, "charm_spell_compass", ArcaneApprentices.MODID, new ItemArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		ItemRegistry.registerItemArtefact(registry, "charm_itinerary", ArcaneApprentices.MODID, new ItemCharmItinerary(EnumRarity.RARE, ItemArtefact.Type.CHARM));
//		ItemRegistry.registerItemArtefact(registry, "charm_talent_detector", ApprenticeArcana.MODID, new ItemArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
//		ItemRegistry.registerItemArtefact(registry, "charm_treasure_map", ApprenticeArcana.MODID, new ItemArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		ItemRegistry.registerItemArtefact(registry, "charm_withering_atlas", ArcaneApprentices.MODID, new ItemArtefact(EnumRarity.EPIC, ItemArtefact.Type.CHARM));
		ItemRegistry.registerItemArtefact(registry, "charm_golden_lure", ArcaneApprentices.MODID, new ItemArtefact(EnumRarity.RARE, ItemArtefact.Type.CHARM));
		ItemRegistry.registerItemArtefact(registry, "ring_serendipity", ArcaneApprentices.MODID, new ItemArtefact(EnumRarity.RARE, ItemArtefact.Type.RING));
		ItemRegistry.registerItemArtefact(registry, "amulet_survival_chance", ArcaneApprentices.MODID, new ItemArtefact(EnumRarity.RARE, ItemArtefact.Type.AMULET));
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	public static <T> T placeholder() { return null; }


}