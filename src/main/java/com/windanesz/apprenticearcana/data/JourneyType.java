package com.windanesz.apprenticearcana.data;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.Settings;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import net.minecraft.util.ResourceLocation;

public enum JourneyType {

	NOT_ADVENTURING("NONE"),
	GATHER_SHORT_DURATION("SHORT"),
	GATHER_MEDIUM_DURATION("MEDIUM"),
	GATHER_LONG_DURATION("LONG"),
	SLAY_MOBS_SHORT_DURATION("SHORT"),
	SLAY_MOBS_MEDIUM_DURATION("MEDIUM"),
	SLAY_MOBS_LONG_DURATION("LONG"),
	ADVENTURE_SHORT_DURATION("SHORT"),
	ADVENTURE_MEDIUM_DURATION("MEDIUM"),
	ADVENTURE_LONG_DURATION("LONG"),
	;

	private final String duration;

	JourneyType(String duration) {
		this.duration = duration;
	}

	public String getDuration() {
		return duration;
	}

	public int getRandomXPValueForAdventure(EntityWizardInitiate wizardInitiate) {
		// TODO: artefact to increase xp gained in general should affect this
		return (int) (this.getDurationValue() * Settings.generalSettings.WIZARD_JOURNEY_XP_GAIN_MODIFIER * wizardInitiate.world.rand.nextFloat() + 0.3);
	}

	private int getDurationValue() {
		switch (duration) {
			case "NONE":
				return 0;
			case "SHORT":
				return 1;
			case "MEDIUM":
				return 2;
			case "HARD":
				return 3;
		}

		return 0;
	}

	public int getRandomAdventureDuration(EntityWizardInitiate wizardInitiate) {
		// TODO: artefact to reduce duration
		int min = this.getDurationValue() * Settings.generalSettings.MIN_ADVENTURE_DURATION_IN_TICKS;
		int max = this.getDurationValue() * Settings.generalSettings.MAX_ADVENTURE_DURATION_IN_TICKS;

		if (max > min) {
			return wizardInitiate.world.rand.nextInt(max - min + 1) + min;
		}
		return 0;
	}

	public ResourceLocation getLootTable() {
		return new ResourceLocation(ApprenticeArcana.MODID, "adventure/" + this.toString().toLowerCase());
	}
	
}