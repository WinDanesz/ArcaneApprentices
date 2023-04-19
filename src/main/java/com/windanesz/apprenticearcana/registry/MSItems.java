package com.windanesz.apprenticearcana.registry;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@ObjectHolder(ApprenticeArcana.MODID)
@Mod.EventBusSubscriber
public final class MSItems {


	private MSItems() {} // No instances!

//	public static final Item ring_bat = placeholder();

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Item> event) {

		IForgeRegistry<Item> registry = event.getRegistry();
//		ItemRegistry.registerItemArtefact(registry, "ring_bat", ApprenticeArcana.MODID, new ItemArtefact(EnumRarity.UNCOMMON, ItemArtefact.Type.RING));
	}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	public static <T> T placeholder() { return null; }


}