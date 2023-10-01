package com.windanesz.apprenticearcana;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.ForgeRegistries;

import java.util.Arrays;
import java.util.List;

@Config(modid = ApprenticeArcana.MODID, name = "apprenticearcana") // No fancy configs here so we can use the annotation, hurrah!
public class Settings {

	public List<String> WIZARD_NAME_LIST = Arrays.asList((generalSettings.WIZARD_NAMES));

	public static boolean isArtefactEnabledForNPC(Item item) {
		//noinspection DataFlowIssue
		return Arrays.stream(generalSettings.ARTEFACTS_USABLE_BY_NPCS).anyMatch(s -> s.equals(item.getRegistryName().toString()));
	}

	@Config.Name("General Settings")
	@Config.LangKey("settings.apprenticearcana:general_settings")
	public static GeneralSettings generalSettings = new GeneralSettings();

	@Config.Name("Journey Settings")
	@Config.LangKey("settings.apprenticearcana:journey_settings")
	public static JourneySettings journeySettings = new JourneySettings();

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

	public static class JourneySettings {

		@Config.Name("Journey Food Requirement Modifier")
		public float JOURNEY_FOOD_REQUIREMENT_MODIFIER = 1f;

		@Config.Name("Wizard Journey XP Gain Modifier")
		@Config.Comment("Levels Required By Each Journey Type in a format of adventuretype:minimum_level")
		public float WIZARD_JOURNEY_XP_GAIN_MODIFIER = 1f;

		@Config.Name("Journeys Require Food")
		public boolean JOURNEY_REQUIRE_FOOD = true;

		@Config.Name("Levels Required By Each Journey Type")
		@Config.Comment("Levels Required By Each Journey Type in a format of journeytype:minimum_level")
		public String[] LEVELS_REQUIRED_BY_EACH_JOURNEY_TYPE = {
				"gather:5",
				"slay_mobs:10",
				"adventure:15",
				"custom_journey:5"
		};

		@Config.Name("Custom Journey")
		@Config.Comment("Enables a 4th, custom journey type. This could be used for pack makers or whatever other purpose you want. For the Level requirement, "
				+ "use the LEVELS_REQUIRED_BY_EACH_JOURNEY_TYPE settings' custom_journey:x type")
		public boolean CUSTOM_JOURNEY_ENABLED = false;

		@Config.Name("Custom Journey Name")
		@Config.Comment("Name of the custom journey type. Here you can simply provide a string, or a language key as well. If the string contains a colon (:), "
				+ "it will be treated as a language key and it will be localised. ")
		public String CUSTOM_JOURNEY_NAME = "Rename Me";

		@Config.Name("Custom Journey Loot Table")
		@Config.Comment("Path of the Custom Journey's loot table. E.g. 'ebwizardry:chests/shrine'")
		public String CUSTOM_JOURNEY_LOOT_TABLE = "";
	}

	public static class GeneralSettings {

		@Config.Name("List of Possible Apprentice Names")
		@Config.Comment("List of Possible Apprentice Names. Randomly chosen at first spawn of an NPC.")
		public String[] WIZARD_NAMES = {
				"Aidan",
				"Alaric",
				"Alarion",
				"Aldous",
				"Alistair",
				"Alphonse",
				"Amadeus",
				"Ambrose",
				"Anselm",
				"Apollo",
				"Archibald",
				"Argus",
				"Armand",
				"Asher",
				"Ashwood",
				"Astor",
				"Augustus",
				"Azriel",
				"Baldwin",
				"Balthazar",
				"Bardo",
				"Barnabas",
				"Bartholomew",
				"Basil",
				"Beaumont",
				"Beauregard",
				"Belvedere",
				"Benedict",
				"Bertrand",
				"Blaise",
				"Boulderheart",
				"Bramble",
				"Bramwell",
				"Brookstone",
				"Byron",
				"Calder",
				"Callahan",
				"Caradoc",
				"Casimir",
				"Caspian",
				"Cato",
				"Cedric",
				"Cillian",
				"Claudius",
				"Claymore",
				"Corristo",
				"Corwin",
				"Cronos",
				"Cyprian",
				"Cyril",
				"Dan",
				"Darius",
				"Demetrius",
				"Desmond",
				"Dimitri",
				"Donovan",
				"Dorian",
				"Draven",
				"Drystan",
				"Dustan",
				"Eamon",
				"Edgar",
				"Edmund",
				"Edwin",
				"Eldritch",
				"Electro",
				"Elric",
				"Ember",
				"Emeric",
				"Emilio",
				"Emrys",
				"Enzo",
				"Ephraim",
				"Esmond",
				"Everard",
				"Ezekiel",
				"Fabian",
				"Faelan",
				"Ferdinand",
				"Finley",
				"Finnian",
				"Flint",
				"Florian",
				"Frost",
				"Fulgor",
				"Gabriel",
				"Gaian",
				"Gaius",
				"Galadriel",
				"Galen",
				"Gareth",
				"Garrick",
				"Gaspard",
				"Geoffrey",
				"Gideon",
				"Gilbert",
				"Giles",
				"Glacieron",
				"Godfrey",
				"Gregory",
				"Griffin",
				"Hadrian",
				"Hamish",
				"Harlan",
				"Hawthorn",
				"Hawthorne",
				"Hector",
				"Helios",
				"Hesperus",
				"Horace",
				"Horatio",
				"Icarius",
				"Ignatius",
				"Ikaros",
				"Indigo",
				"Isambard",
				"Isidore",
				"Jareth",
				"Jarvis",
				"Jasper",
				"Jericho",
				"Jovian",
				"Julius",
				"Kaelan",
				"Kai",
				"Kendrick",
				"Kieran",
				"Killian",
				"Lancelot",
				"Lazarus",
				"Leander",
				"Leopold",
				"Linus",
				"Lucian",
				"Lucius",
				"Luther",
				"Magnus",
				"Malachi",
				"Marcellus",
				"Marius",
				"Maurice",
				"Maximilian",
				"Meadowbrook",
				"Merlin",
				"Montgomery",
				"Morgan",
				"Mortimer",
				"Myxir",
				"Nathaniel",
				"Neville",
				"Nevin",
				"Nicodemus",
				"Oberon",
				"Octavius",
				"Octavos",
				"Odysseus",
				"Olaf",
				"Onyx",
				"Orin",
				"Orlando",
				"Orpheus",
				"Oswald",
				"Percival",
				"Philemon",
				"Phineas",
				"Prospero",
				"Ptolemy",
				"Quentin",
				"Quinlan",
				"Radcliffe",
				"Ragnar",
				"Rai",
				"Raiden",
				"Raphael",
				"Reginald",
				"Reuben",
				"Roderick",
				"Roland",
				"Roscoe",
				"Rowan",
				"Rufus",
				"Rupert",
				"Saturas",
				"Sebastian",
				"Silas",
				"Silvius",
				"Simeon",
				"Sinclair",
				"Solomon",
				"Solon",
				"Somerset",
				"Soren",
				"Sorin",
				"Sterling",
				"Stonebrook",
				"Sylvester",
				"Talus",
				"Tavian",
				"Terrin",
				"Thaddeus",
				"Theodoric",
				"Theon",
				"Theron",
				"Thistle",
				"Thistlewood",
				"Tiberius",
				"Titus",
				"Tobias",
				"Tristan",
				"Tristram",
				"Ulysses",
				"Uri",
				"Uriah",
				"Vajra",
				"Valentine",
				"Valerian",
				"Valerius",
				"Verdant",
				"Vesper",
				"Vincent",
				"Vincenzo",
				"Virgil",
				"Vulcan",
				"Waldemar",
				"Walden",
				"Waldo",
				"Warrick",
				"Wilbur",
				"Willard",
				"Winthrop",
				"Wolfgang",
				"Xander",
				"Xavier",
				"Yew",
				"Ymir",
				"Yorick",
				"Zachariah",
				"Zeno",
				"Zephyr",
				"Zephyrus",
				"Zigmund",
				"Zoltan"
		};

		@Config.RequiresMcRestart
		@Config.Name("List of Artefacts NPC Apprentices Can Use")
		@Config.Comment("List of Artefacts NPC Apprentices Can Use. This list controls what artefacts can be used by NPCs."
				+ "This list is more for disabling artefacts that have native support or dedicated support. New entries have a high chance of not working, but you can try.")
		public String[] ARTEFACTS_USABLE_BY_NPCS = {
				"ebwizardry:amulet_arcane_defence",
				"ebwizardry:amulet_banishing",
				"ebwizardry:amulet_channeling",
				"ebwizardry:amulet_fire_cloaking",
				"ebwizardry:amulet_fire_protection",
				"ebwizardry:amulet_frost_warding",
				"ebwizardry:amulet_ice_immunity",
				"ebwizardry:amulet_ice_protection",
				"ebwizardry:amulet_lich",
				"ebwizardry:amulet_potential",
				"ebwizardry:amulet_recovery",
				"ebwizardry:amulet_transience",
				"ebwizardry:amulet_warding",
				"ebwizardry:amulet_wisdom",
				"ebwizardry:amulet_wither_immunity",
				"ebwizardry:charm_experience_tome",
				"ebwizardry:charm_minion_health",
				"ebwizardry:charm_minion_variants",
				"ebwizardry:greater_telekinesis",
				"ebwizardry:ring_arcane_frost",
				"ebwizardry:ring_battlemage",
				"ebwizardry:ring_blockwrangler",
				"ebwizardry:ring_combustion",
				"ebwizardry:ring_condensing",
				"ebwizardry:ring_conjurer",
				"ebwizardry:ring_disintegration",
				"ebwizardry:ring_earth_biome",
				"ebwizardry:ring_earth_melee",
				"ebwizardry:ring_extraction",
				"ebwizardry:ring_fire_biome",
				"ebwizardry:ring_fire_melee",
				"ebwizardry:ring_full_moon",
				"ebwizardry:ring_ice_biome",
				"ebwizardry:ring_ice_melee",
				"ebwizardry:ring_leeching",
				"ebwizardry:ring_lightning_melee",
				"ebwizardry:ring_necromancy_melee",
				"ebwizardry:ring_paladin",
				"ebwizardry:ring_poison",
				"ebwizardry:ring_shattering",
				"ebwizardry:ring_soulbinding",
				"ebwizardry:ring_storm",
				"ebwizardry:charm_withering_atlas",
				"ebwizardry:charm_golden_lure",
				"ebwizardry:ring_siphoning",
				"ebwizardry:amulet_survival_chance",
		};

		@Config.Name("Minimum NPC Level for Identifying Spells")
		public int MINIMUM_NPC_LEVEL_FOR_IDENTIFYING_SPELLS = 10;

		@Config.Name("Identify Spells in Inventory")
		public boolean IDENTIFY_SPELLS_IN_INVENTORY = true;

		@Config.Name("Wizards Can Identify Spells")
		public boolean WIZARDS_CAN_IDENTIFY_SPELLS = true;

		@Config.Name("Max Tier for Identifying Spells")
		@Config.Comment("0 = novice, 1 = up to apprentice, 2 = up to advanced, 3 = up to master")
		@Config.RangeInt(min = 0, max = 3)
		public int MAX_TIER_FOR_IDENTIFYING_SPELLS = 2;

		@Config.Name("Identifying Spell Duration Multiplier Compared to Learning")
		@Config.Comment("Checked once every second. Set to 0 to disable this event.")
		public float IDENTIFYING_SPELL_DURATION_MULTIPLIER_COMPARED_TO_LEARNING = 2.5f;

		@Config.Name("Max Apprentice Level")
		public int MAX_WIZARD_LEVEL = 30;

		@Config.Name("Max Apprentice Spell Slots")
		@Config.RangeInt(min = 1, max = 8)
		public int MAX_WIZARD_SPELL_SLOTS = 8;

		@Config.Name("Max Apprentice Spell Tier")
		@Config.Comment("0 = novice, 1 = up to apprentice, 2 = up to advanced, 3 = up to master")
		@Config.RangeInt(min = 0, max = 3)
		public int MAX_WIZARD_SPELL_TIER = 2;

		@Config.Name("XP Gain Per Kill")
		@Config.RangeInt(min = 1)
		public int XP_GAIN_PER_KILL = 20;

		@Config.Name("Maximum Apprentice Count")
		@Config.Comment("The number of apprentice wizard NPCs a player can have at a time. Set to -1 to have no limit")
		@Config.RangeInt(min = -1)
		public int MAXIMUM_APPRENTICE_COUNT = 2;

		@Config.Name("Maximum Party Size")
		@Config.Comment("The number of apprentice wizard NPCs a player can have at a time following them. Can be used in conjunction with the MAXIMUM_APPRENTICE_COUNT setting,"
				+ "to allow having more apprentices at a time in total, but still keep the game more balanced. Set to -1 to allow unlimited parties.")
		@Config.RangeInt(min = -1)
		public int MAXIMUM_PARTY_SIZE = 2;

		@Config.Name("Apprentices Can Be Revived")
		@Config.Comment("If true, died apprentices can be resurrected by using a Totem of Undying on an Imbuement Altar.")
		public boolean APPRENTICES_CAN_BE_RESURRECTED = true;

		@Config.Name("Apprentices Respawn At Player Spawnpoint")
		@Config.Comment("If true, died apprentices will just respawn at the player's spawnpoint the next time the player sleeps. Mutually exclusive with the APPRENTICES_CAN_BE_RESURRECTED setting.")
		public boolean APPRENTICES_RESPAWN_AT_PLAYER_SPAWNPOINT = true;

		@Config.Name("Apprentices Spawn In Vanilla Villages")
		@Config.Comment("If true, NPC apprentices will naturally spawn in vanilla villages. If set to false, you must provide your own method of spawning them.")
		public boolean APPRENTICES_SPAWN_IN_VANILLA_VILLAGES = true;

		@Config.Name("Show Partied Up Apprentice Hitpoints")
		@Config.Comment("If true, NPCs currently following the player will have their health bar displayed on the screen.")
		public boolean SHOW_PARTIED_UP_APPRENTICE_HITPOINTS = true;

		@Config.Name("Party NPC Hitpoint X Position")
		@Config.Comment("Sets the X position of the NPC health display of the NPCs apprentices currently following the player.")
		@Config.RangeInt(min = 0)
		public int PARTY_NPC_HITPOINT_X_POSITION = 0;

		@Config.Name("Party NPC Hitpoint Y Position")
		@Config.Comment("Sets the X position of the NPC health display of the NPCs apprentices currently following the player.")
		@Config.RangeInt(min = 0)
		public int PARTY_NPC_HITPOINT_Y_POSITION = 0;

		@Config.Name("Apprentice Minimum HP")
		@Config.RangeInt(min = 1)
		public int WIZARD_MINIMUM_HP = 8;

		@Config.Name("Apprentice HP Gain Per Level")
		@Config.RangeInt(min = 0)
		public int WIZARD_HP_GAIN_PER_LEVEL = 1;

		@Config.Name("Apprentice MAX HEALTH CAP")
		@Config.RangeInt(min = 1)
		public int WIZARD_MAX_HEALTH_CAP = 200;

		@Config.Name("XP Gain Per Spell Cast")
		public int XP_GAIN_PER_SPELL_CAST = 5;

		@Config.Name("Kill Message Chance")
		public float KILL_MESSAGE_CHANCE = 0.05f;

		@Config.Name("Spell Remark Message Chance")
		public float SPELL_REMARK_MESSAGE_CHANCE = 0.05f;

		@Config.Name("Items Found By Apprentices During Travelling Together")
		@Config.Comment("List of items found by wizard apprentices that they give to the player on rare occasions during adventuring TOGETHER." +
				"The format should be like this: modid:itemname:metadata:count_min:count_max:nbt:nbt_stuff_goes_here. The nbt tag can be omitted. "
				+ "Example: ebwizardry:magic_crystal:0:2:5 - this didn't have an nbt tag with a random count between 2-5"
				+ "Example: bwizardry:magic_crystal:4:2:5:nbt:{ench:[{id:6,lvl:1}]} - this would be 2-5 magic crystals with an aqua affinity enchantment and metadata as 4")
		public String[] APPRENTICE_ITEM_LIST_TRAVELLING_TOGETHER = {
				"ebwizardry:magic_crystal:1:2:5",
				"ebwizardry:crystal_flower:0:2:2:{ench:[{id:6,lvl:1}]}"
		};

		@Config.Name("Event Chance - Items Found By Apprentices")
		@Config.Comment("Checked once every second. Set to 0 to disable this event.")
		public float EVENT_FREQUENCY_ITEMS_FOUND_BY_APPRENTICES = 1;

		@Config.Name("Event Chance - Give Food To Starving Owner")
		@Config.Comment("Checked once every second. Set to 0 to disable this event.")
		public float EVENT_FREQUENCY_GIVE_FOOD_TO_STARVING_OWNER = 1;

		@Config.Name("Event Chance - Give Healing Potion To Dying Owner")
		@Config.Comment("Checked once every second. Set to 0 to disable this event.")
		public float EVENT_FREQUENCY_GIVE_HEALING_POTION_TO_DYING_OWNER = 1;

		@Config.Name("Max Adventure Duration In Ticks (short journeys)")
		@Config.Comment("Determines the maximum duration an NPC can spend in an adventure before it returns.")
		public int MAX_ADVENTURE_DURATION_IN_TICKS_SHORT = 18000;

		@Config.Name("Min Adventure Duration In Ticks (short journeys)")
		@Config.Comment("Determines the minimum duration an NPC must spend in an adventure before it returns.")
		public int MIN_ADVENTURE_DURATION_IN_TICKS_SHORT = 6000;

		@Config.Name("Max Adventure Duration In Ticks (medium journeys)")
		@Config.Comment("Determines the maximum duration an NPC can spend in an adventure before it returns.")
		public int MAX_ADVENTURE_DURATION_IN_TICKS_MEDIUM = 18000;

		@Config.Name("Min Adventure Duration In Ticks (medium journeys)")
		@Config.Comment("Determines the minimum duration an NPC must spend in an adventure before it returns.")
		public int MIN_ADVENTURE_DURATION_IN_TICKS_MEDIUM = 54000;

		@Config.Name("Max Adventure Duration In Ticks (long journeys)")
		@Config.Comment("Determines the maximum duration an NPC can spend in an adventure before it returns.")
		public int MAX_ADVENTURE_DURATION_IN_TICKS_LONG = 81000;

		@Config.Name("Min Adventure Duration In Ticks (long journeys)")
		@Config.Comment("Determines the minimum duration an NPC must spend in an adventure before it returns.")
		public int MIN_ADVENTURE_DURATION_IN_TICKS_LONG = 54000;

		@Config.Name("NPC Spell Study Time Modifier")
		@Config.Comment("The higher the number, the longer it takes to learn a spell (as a math exponent)"
				+ "For reference, here is a table for each tier's learning time in MINUTES for a given whole number of this setting, format is:"
				+ "<time modifier> : <novice> | <apprentice> | <advanced> | <master>"
				+ "4: <1 | <1 | 1 | 2"
				+ "5: 3 | 4 | 7 | 10"
				+ "6: 13 | 23 | 39 | 63"
				+ "7: 65 | 127 | 233 | 408"
				+ "8: 325 | 698 | 1400 | 2655"
		)
		@Config.RangeDouble(min = 4, max = 8)
		public double NPC_SPELL_STUDY_TIME_MODIFIER = 5d;

		//		@Config.Name("Items Found By Apprentices During Adventure")
		//		@Config.Comment("List of items found by wizard apprentices that they give to the player on rare occasions when they are sent on an adventure" +
		//				"The format should be like this: modid:itemname:metadata:count_min:count_max:nbt:nbt_stuff_goes_here. The nbt tag can be omitted. "
		//				+ "Example: ebwizardry:magic_crystal:0:2:5 - this didn't have an nbt tag with a random count between 2-5"
		//				+ "Example: bwizardry:magic_crystal:4:2:5:nbt:{ench:[{id:6,lvl:1}]} - this would be 2-5 magic crystals with an aqua affinity enchantment and metadata as 4")
		//		public String[] APPRENTICE_ITEM_LIST = {
		//				"ebwizardry:astral_diamond:0:1:1"
		//		};
	}

	/**
	 * Parses an item from a string format and generates an ItemStack from it
	 *
	 * @param string the item's string in this format: modid:itemname:metadata:count_min:count_max:nbt:nbttag. nbt is optional
	 * @return ItemStack parsed from the string, or ItemStack.EMPTY if the string wasn't valid
	 */
	public static ItemStack getItemFromString(String string, World world) {
		String[] parts = string.split(":");

		if (parts.length >= 3) {
			String modId = parts[0];
			String itemName = parts[1];
			int metadata = Integer.parseInt(parts[2]);

			ResourceLocation itemLocation = new ResourceLocation(modId, itemName);
			Item item = ForgeRegistries.ITEMS.getValue(itemLocation);
			if (item != null) {

				ItemStack itemStack = new ItemStack(item, 1, metadata);

				// count_min
				if (parts.length >= 4) {
					int countMin = Integer.parseInt(parts[3]);
					itemStack.setCount(countMin);

					// count_max
					if (parts.length >= 5) {
						int count = countMin;
						int countMax = Integer.parseInt(parts[4]);
						if (countMax > countMin) {
							count = countMin + ApprenticeArcana.rand.nextInt(countMax - countMin);
						}
						itemStack.setCount(count);

						// nbt
						if (string.contains(":nbt:") && parts.length >= 6) {
							// the +5 is to offset by the 5 charaters of the :nbt: piece
							String nbtString = string.substring(string.indexOf(":nbt:") + 5);
							try {
								NBTTagCompound nbt = JsonToNBT.getTagFromJson(nbtString);
								itemStack.setTagCompound(nbt);
							}
							catch (Exception e) {
								e.printStackTrace();
								return ItemStack.EMPTY;
							}
						}
					}
				}

				return itemStack;
			}
		}

		return ItemStack.EMPTY;
	}
}
