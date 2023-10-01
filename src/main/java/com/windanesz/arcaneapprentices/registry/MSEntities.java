package com.windanesz.arcaneapprentices.registry;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.wizardryutils.registry.EntityRegistry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.registries.IForgeRegistry;

@Mod.EventBusSubscriber
public class MSEntities {

	private MSEntities() {}

	@SubscribeEvent
	public static void register(RegistryEvent.Register<EntityEntry> event) {

		IForgeRegistry<EntityEntry> registry = event.getRegistry();

		registry.register(EntityRegistry.createEntry(EntityWizardInitiate.class, "wizard_initiate", ArcaneApprentices.MODID, EntityRegistry.TrackingType.LIVING).egg(0x19295e, 0x03c2fc).build());

	}
}
