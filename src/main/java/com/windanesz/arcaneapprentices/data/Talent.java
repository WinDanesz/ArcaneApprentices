package com.windanesz.arcaneapprentices.data;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.Settings;
import electroblob.wizardry.Wizardry;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public enum Talent {

	NONE(true, "none"),
	RESEARCHER(true, "researcher"),
	CONDUIT(true, "conduit"),
	EMPOWERING_RESONANCE(true, "empowering_resonance"),
	SPELL_TINKERER(true, "spell_tinkerer"),
	PHASER(true, "phaser"),
	SURVIVOR(true, "survivor"),
	APPAREL_EXPERT(true, "apparel_expert"),
	SWIFT_VOYAGE(true, "swift_voyage"),
	HEALER(true, "healer"),
	ALCHEMY_ADEPT(true, "alchemy_adept"),
	ARTIFICE_MASTER(true, "artifice_master"),
	ANIMAL_WHISPERER(true, "animal_whisperer"),

	// TODO:
	REMNANT_TAMER(false, "remnant_tamer"),
	TREASURE_HUNTER(false, "treasure_hunter"),
	;

	private final boolean implemented;
	private final String unlocalisedName;

	Talent(boolean implemented, String unlocalisedName) {
		this.implemented = implemented;
		this.unlocalisedName = unlocalisedName;
	}

	public static Talent fromName(String name) {
		Talent[] var1 = values();
		int var2 = var1.length;

		for (int var3 = 0; var3 < var2; ++var3) {
			Talent talent = var1[var3];
			if (talent.unlocalisedName.equals(name)) {
				return talent;
			}
		}

		throw new IllegalArgumentException("No such talent with unlocalised name: " + name);
	}

	@Nullable
	public static Talent fromName(String name, @Nullable Talent fallback) {
		Talent[] var2 = values();
		int var3 = var2.length;

		for (int var4 = 0; var4 < var3; ++var4) {
			Talent talent = var2[var4];
			if (talent.unlocalisedName.equals(name)) {
				return talent;
			}
		}
		return fallback;
	}

	public boolean isEnabled() {
		return this.implemented && TalentSettings.isEnabled(this);
	}

	public String getName() {
		return this.unlocalisedName;
	}

	public static Talent getRandom() {
		List<Talent> talents = new ArrayList<>();
		for (Talent talent : Talent.values()) {
			if (talent != Talent.NONE && talent.isEnabled()) {
				talents.add(talent);
			}
		}
		return talents.get(ArcaneApprentices.rand.nextInt(talents.size()));
	}

	public String getDisplayName() {
		return Wizardry.proxy.translate("talent." + this.getName(), new Object[0]);
	}

	public String getDescription() {
		return Wizardry.proxy.translate("talent." + this.getName() + ".desc", new Object[0]);
	}

	public static class TalentSettings {
		public static HashMap<Talent, Boolean> TALENT_SETTINGS = new HashMap<Talent, Boolean>();

		public static void init() {
			String[] talentList = Settings.generalSettings.APPRENTICE_TALENTS;

			for (String talent : talentList) {
				String[] parts = talent.split(":");
				String talentName = parts[0];
				boolean enabled = Boolean.parseBoolean(parts[1]);
				if (Talent.fromName(talentName) != Talent.NONE) {
					TALENT_SETTINGS.put(Talent.fromName(talentName), enabled);
				}
			}
		}

		private static boolean isEnabled(Talent talent) {
			return TALENT_SETTINGS.getOrDefault(talent, true);
		}
	}

}
