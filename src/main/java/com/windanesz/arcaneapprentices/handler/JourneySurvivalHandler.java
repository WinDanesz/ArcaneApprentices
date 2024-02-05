package com.windanesz.arcaneapprentices.handler;

import com.windanesz.arcaneapprentices.Settings;
import com.windanesz.arcaneapprentices.data.JourneyType;
import com.windanesz.arcaneapprentices.data.Talent;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.registry.AAItems;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.util.math.MathHelper;

public class JourneySurvivalHandler {
	private int armour;
	private int armourToughness;
	private int level;
	private int numKnownSpells;
	private double currentHealthPercent;
	private double maxHealth;
	private JourneyType journey;
	private EntityWizardInitiate wizard;

	private static final float DEFAULT_SURVIVAL_CHANCE = 0.5f;

	public JourneySurvivalHandler(EntityWizardInitiate wizard, JourneyType journey) {
		this.armour = wizard.getTotalArmorValue();
		IAttributeInstance iattributeinstance = wizard.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS);
		this.armourToughness = MathHelper.floor(iattributeinstance.getAttributeValue());
		this.level = wizard.getLevel();
		this.numKnownSpells = wizard.getSpells().size();
		this.currentHealthPercent = wizard.getHealth() / wizard.getMaxHealth();
		this.maxHealth = wizard.getMaxHealth();
		this.journey = journey;
		this.wizard = wizard;
	}

	public float calculateSurvivalChance() {
		if (!wizard.isChild() && wizard.getTalent() == Talent.SURVIVOR && Talent.SURVIVOR.isEnabled()) {
			return 1.0f;
		}

		float survivalChance = 0.3f;

		survivalChance -= 1 - journey.getSurvivalModifier();

		// Modify survivalChance based on different factors
		survivalChance += (armour / 30.0) * 0.3;
		survivalChance += ((float) armourToughness / 20) * 0.05;
		survivalChance += ((float) level / Settings.generalSettings.MAX_WIZARD_LEVEL) * 0.3;
		survivalChance += ((float) numKnownSpells / Settings.generalSettings.MAX_WIZARD_SPELL_SLOTS) * 0.08;
		survivalChance += ((currentHealthPercent) * 0.8) * 0.15;
		survivalChance += ((maxHealth) * 0.3) * 0.01;
		if (wizard.isArtefactActive(AAItems.amulet_survival_chance)) {
			survivalChance *= 1.15f;
		}

		// Cap survivalChance between 0 and 1
		survivalChance = (float) Math.max(0.0, Math.min(1.0, survivalChance));

		// Generate a random number to compare with survivalChance
	//	Random random = new Random();
	//	double randomValue = random.nextDouble();

		// Return true if the random value is less than survivalChance, indicating survival
		return survivalChance;
	}

	public static float calculateSurvivalChance(EntityWizardInitiate wizardInitiate, JourneyType journey) {
		JourneySurvivalHandler j = new JourneySurvivalHandler(wizardInitiate, journey);
		return j.calculateSurvivalChance();
	}
}
