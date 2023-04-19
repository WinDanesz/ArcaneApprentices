package com.windanesz.apprenticearcana.registry;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
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

		registry.register(EntityRegistry.createEntry(EntityWizardInitiate.class, "wizard_initiate", ApprenticeArcana.MODID, EntityRegistry.TrackingType.LIVING).build());

	}
}
