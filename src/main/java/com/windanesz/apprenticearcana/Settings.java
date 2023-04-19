package com.windanesz.apprenticearcana;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Arrays;
import java.util.List;

import static electroblob.wizardry.Settings.toResourceLocations;

@Config(modid = ApprenticeArcana.MODID, name = "apprenticearcana") // No fancy configs here so we can use the annotation, hurrah!
public class Settings {

	public List<String> WIZARD_NAME_LIST = Arrays.asList((generalSettings.WIZARD_NAMES));

	@Config.Name("General Settings")
	@Config.LangKey("settings.apprenticearcana:general_settings")
	public static GeneralSettings generalSettings = new GeneralSettings();

	@SuppressWarnings("unused")
	@Mod.EventBusSubscriber(modid = ApprenticeArcana.MODID)
	private static class EventHandler {
		/**
		 * Inject the new values and save to the config file when the config has been changed from the GUI.
		 *
		 * @param event The event
		 */
		@SubscribeEvent
		public static void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
			if (event.getModID().equals(ApprenticeArcana.MODID)) {
				ConfigManager.sync(ApprenticeArcana.MODID, Config.Type.INSTANCE);
			}
		}
	}

	public static class GeneralSettings {
		@Config.Name("List of Possible Wizard Names")
		@Config.Comment("List of Possible Wizard Names")
		public String[] WIZARD_NAMES = {
				"Alaric", "Aldous", "Alistair", "Ambrose", "Archibald", "Balthazar", "Barnabas", "Basil", "Beauregard", "Cedric", "Cyril", "Dorian", "Emeric",
				"Emrys", "Fabian", "Finnian", "Galen", "Garrick", "Gideon", "Giles", "Griffin", "Hadrian", "Horatio", "Ignatius", "Isidore", "Jasper", "Jericho",
				"Lancelot", "Leopold", "Lucian", "Magnus", "Marcellus", "Marius", "Maurice", "Merlin", "Montgomery", "Morgan", "Neville", "Octavius", "Orlando",
				"Oswald", "Percival", "Philemon", "Quentin", "Reginald", "Reuben", "Roland", "Rufus", "Sebastian", "Simeon", "Solomon", "Soren", "Sylvester",
				"Thaddeus", "Theodoric", "Tiberius", "Tobias", "Tristan", "Ulysses", "Valentine", "Valerian", "Vincent", "Walden", "Waldo", "Wilbur", "Willard",
				"Winthrop", "Wolfgang", "Xander", "Xavier", "Zachariah", "Zeno", "Zephyr", "Zigmund", "Zoltan", "Zorro"
		};

	}
}
