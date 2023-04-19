package com.windanesz.apprenticearcana.registry;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import net.minecraft.potion.Potion;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@GameRegistry.ObjectHolder(ApprenticeArcana.MODID)
@Mod.EventBusSubscriber
public class MSPotions {

	private MSPotions() {}

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	/**
	 * Sets both the registry and unlocalised names of the given potion, then registers it with the given registry. Use
	 * this instead of {@link Potion#setRegistryName(String)} and {@link Potion#setPotionName(String)} during
	 * construction, for convenience and consistency.
	 *
	 * @param registry The registry to register the given potion to.
	 * @param name     The name of the potion, without the mod ID or the .name stuff. The registry name will be
	 *                 {@code apprenticearcana:[name]}. The unlocalised name will be {@code potion.apprenticearcana:[name].name}.
	 * @param potion   The potion to register.
	 */
	public static void registerPotion(IForgeRegistry<Potion> registry, String name, Potion potion) {
		potion.setRegistryName(ApprenticeArcana.MODID, name);
		// For some reason, Potion#getName() doesn't prepend "potion." itself, so it has to be done here.
		potion.setPotionName("potion." + potion.getRegistryName().toString());
		registry.register(potion);
	}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Potion> event) {

		IForgeRegistry<Potion> registry = event.getRegistry();
	}

}
