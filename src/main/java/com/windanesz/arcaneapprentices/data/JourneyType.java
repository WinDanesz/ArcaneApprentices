package com.windanesz.arcaneapprentices.data;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.Settings;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.registry.AAItems;
import net.minecraft.util.ResourceLocation;

public enum JourneyType {

	NOT_ADVENTURING("NONE", 1f, 0),
	GATHER_SHORT_DURATION("SHORT", 1f, 0),
	GATHER_MEDIUM_DURATION("MEDIUM", 0.9f, 1),
	GATHER_LONG_DURATION("LONG", 0.8f, 2),
	GATHER_SHORT_DURATION_AXE("SHORT", 1f, 0),
	GATHER_MEDIUM_DURATION_AXE("MEDIUM", 0.95f, 0),
	GATHER_LONG_DURATION_AXE("LONG", 0.85f, 0),
	GATHER_SHORT_DURATION_PICKAXE("SHORT", 0.9f, 0),
	GATHER_MEDIUM_DURATION_PICKAXE("MEDIUM", 0.8f, 0),
	GATHER_LONG_DURATION_PICKAXE("LONG", 0.7f, 0),
	GATHER_SHORT_DURATION_SHEARS("SHORT", 0.95f, 0),
	GATHER_MEDIUM_DURATION_SHEARS("MEDIUM", 0.90f, 0),
	GATHER_LONG_DURATION_SHEARS("LONG", 0.85f, 0),
	SLAY_MOBS_SHORT_DURATION("SHORT", 0.7f, 0),
	SLAY_MOBS_MEDIUM_DURATION("MEDIUM", 0.7f, 0),
	SLAY_MOBS_LONG_DURATION("LONG", 0.60f, 0),
	ADVENTURE_SHORT_DURATION("SHORT", 0.75f, 1),
	ADVENTURE_MEDIUM_DURATION("MEDIUM", 0.7f, 2),
	ADVENTURE_LONG_DURATION("LONG", 0.67f, 3),
	SPELL_HUNT_SHORT_DURATION("SHORT", 0.75f, 1),
	SPELL_HUNT_MEDIUM_DURATION("MEDIUM", 0.7f, 2),
	SPELL_HUNT_LONG_DURATION("LONG", 0.67f, 3),
	NETHER_ADVENTURE_SHORT_DURATION("SHORT", 0.75f, 1),
	NETHER_ADVENTURE_MEDIUM_DURATION("MEDIUM", 0.7f, 2),
	NETHER_ADVENTURE_LONG_DURATION("LONG", 0.67f, 3),
	OCEAN_ADVENTURE_SHORT_DURATION("SHORT", 0.8f, 1),
	OCEAN_ADVENTURE_MEDIUM_DURATION("MEDIUM", 0.75f, 2),
	OCEAN_ADVENTURE_LONG_DURATION("LONG", 0.7f, 3),
	;

	private final String duration;
	private final float survivalModifier;
	private final int bonusLootItemCount;

	JourneyType(String duration, float survivalModifier, int bonusLootItemCount) {
		this.duration = duration;
		this.survivalModifier = survivalModifier;
		this.bonusLootItemCount = bonusLootItemCount;
	}

	public String getDuration() {
		return duration;
	}

	public int getRandomXPValueForAdventure(EntityWizardInitiate wizardInitiate) {
		// TODO: artefact to increase xp gained in general should affect this
		return (int) (Settings.journeySettings.WIZARD_JOURNEY_XP_GAIN_MODIFIER * wizardInitiate.world.rand.nextFloat() + 0.3);
	}

	public int getRandomAdventureDuration(EntityWizardInitiate wizardInitiate) {
		float modifier = wizardInitiate.isArtefactActive(AAItems.belt_explorer) ? Settings.journeySettings.EXPLORERS_BELT_JOURNEY_TIME_MODIFIER : 1.0f;
		if (wizardInitiate.getTalent() == Talent.SWIFT_VOYAGE && wizardInitiate.hasTalentUnlocked()) {
			modifier *= 0.5f;
		}
		int min, max;

		switch (duration) {
			case "SHORT":
				min = Settings.generalSettings.MIN_ADVENTURE_DURATION_IN_TICKS_SHORT;
				max = Settings.generalSettings.MAX_ADVENTURE_DURATION_IN_TICKS_SHORT;
				break;
			case "MEDIUM":
				min = Settings.generalSettings.MIN_ADVENTURE_DURATION_IN_TICKS_MEDIUM;
				max = Settings.generalSettings.MAX_ADVENTURE_DURATION_IN_TICKS_MEDIUM;
				break;
			case "LONG":
				min = Settings.generalSettings.MIN_ADVENTURE_DURATION_IN_TICKS_LONG;
				max = Settings.generalSettings.MAX_ADVENTURE_DURATION_IN_TICKS_LONG;
				break;
			default:
				return 0;
		}

		if (max > min) {
			return (int) ((wizardInitiate.world.rand.nextInt(max - min + 1) + min) * modifier);
		}

		return min;
	}

	public int[] getMinMaxDuration(EntityWizardInitiate wizardInitiate) {
		float modifier = wizardInitiate.isArtefactActive(AAItems.belt_explorer) ? Settings.journeySettings.EXPLORERS_BELT_JOURNEY_TIME_MODIFIER : 1.0f;
		if (wizardInitiate.getTalent() == Talent.SWIFT_VOYAGE && wizardInitiate.hasTalentUnlocked()) {
			modifier *= 0.5f;
		}
		int min = 0, max = 0;

		switch (duration) {
			case "SHORT":
				min = Settings.generalSettings.MIN_ADVENTURE_DURATION_IN_TICKS_SHORT;
				max = Settings.generalSettings.MAX_ADVENTURE_DURATION_IN_TICKS_SHORT;
				break;
			case "MEDIUM":
				min = Settings.generalSettings.MIN_ADVENTURE_DURATION_IN_TICKS_MEDIUM;
				max = Settings.generalSettings.MAX_ADVENTURE_DURATION_IN_TICKS_MEDIUM;
				break;
			case "LONG":
				min = Settings.generalSettings.MIN_ADVENTURE_DURATION_IN_TICKS_LONG;
				max = Settings.generalSettings.MAX_ADVENTURE_DURATION_IN_TICKS_LONG;
				break;
		}



		return new int[]{min, max};
	}

	public ResourceLocation getLootTable() {
		return new ResourceLocation(ArcaneApprentices.MODID, "adventure/" + this.toString().toLowerCase());
	}

	public float getSurvivalModifier() {
		return survivalModifier;
	}

	public int getBonusLootItemCount() {
		return bonusLootItemCount;
	}
}