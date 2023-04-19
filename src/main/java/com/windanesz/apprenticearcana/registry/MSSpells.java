package com.windanesz.apprenticearcana.registry;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import electroblob.wizardry.spell.Spell;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@ObjectHolder(ApprenticeArcana.MODID)
@EventBusSubscriber
public final class MSSpells {

	public static final Spell bat_form = placeholder();

	private MSSpells() {} // no instances

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Spell> event) {

		IForgeRegistry<Spell> registry = event.getRegistry();

//		registry.register(new TrueResurrection());

	}
}
