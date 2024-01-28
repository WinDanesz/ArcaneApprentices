package com.windanesz.arcaneapprentices.registry;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

import java.util.ArrayList;
import java.util.List;

@GameRegistry.ObjectHolder(ArcaneApprentices.MODID)
@Mod.EventBusSubscriber(modid = ArcaneApprentices.MODID)
public class AASounds {
	private AASounds() {}

	private static final List<SoundEvent> sounds = new ArrayList<>();

	public static SoundEvent createSound(String name) {
		return createSound(ArcaneApprentices.MODID, name);
	}

	/**
	 * Creates a sound with the given name, to be read from {@code assets/[modID]/sounds.json}.
	 */
	public static SoundEvent createSound(String modID, String name) {
		// All the setRegistryName methods delegate to this one, it doesn't matter which you use.
		return new SoundEvent(new ResourceLocation(modID, name)).setRegistryName(name);
	}


	// For some reason, sound events seem to work even when they aren't registered, without even so much as a warning.

	@SubscribeEvent
	public static void register(RegistryEvent.Register<SoundEvent> event){
		event.getRegistry().registerAll(sounds.toArray(new SoundEvent[0]));
	}
}