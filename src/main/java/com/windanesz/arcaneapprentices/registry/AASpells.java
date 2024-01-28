package com.windanesz.arcaneapprentices.registry;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.spell.RecallApprentices;
import com.windanesz.arcaneapprentices.spell.override.ResurrectionOverride;
import com.windanesz.arcaneapprentices.spell.override.SummonSkeletonLegionOverride;
import com.windanesz.arcaneapprentices.spell.override.SummonSkeletonOverride;
import com.windanesz.arcaneapprentices.spell.override.SummonZombieOverride;
import com.windanesz.arcaneapprentices.spell.override.ZombieApocalypseOverride;
import electroblob.wizardry.spell.Spell;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.IForgeRegistry;

import javax.annotation.Nonnull;

@ObjectHolder(ArcaneApprentices.MODID)
@EventBusSubscriber
public final class AASpells {

	private AASpells() {} // no instances

	public static final Spell recall_apprentices = placeholder();

	@Nonnull
	@SuppressWarnings("ConstantConditions")
	private static <T> T placeholder() { return null; }

	@SubscribeEvent
	public static void register(RegistryEvent.Register<Spell> event) {

		IForgeRegistry<Spell> registry = event.getRegistry();

		// For these overrides, we cannot directly call the static references e.g. Spells.summon_zombie, because by the time we call this, they are still null
		registry.register(new SummonZombieOverride(Spell.get("ebwizardry:summon_zombie").networkID()));
		registry.register(new SummonSkeletonOverride(Spell.get("ebwizardry:summon_skeleton").networkID()));
		registry.register(new SummonSkeletonLegionOverride(Spell.get("ebwizardry:summon_skeleton_legion").networkID()));
		registry.register(new ZombieApocalypseOverride(Spell.get("ebwizardry:zombie_apocalypse").networkID()));
		registry.register(new ResurrectionOverride(Spell.get("ebwizardry:resurrection").networkID()));

		registry.register(new RecallApprentices());

	}
}
