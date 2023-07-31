package com.windanesz.apprenticearcana.data;

import akka.actor.Identify;
import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import net.minecraft.util.text.TextComponentTranslation;

public enum Speech {

	WIZARD_IDLING(33),
	WIZARD_STARVING(4),
	WIZARD_TAKE_DAMAGE_BY_SWORD(2),
	WIZARD_TAKE_DAMAGE_FROM_PLAYER(3),
	OWNER_HAS_LOW_HEALTH(5),
	OWNER_HAS_LOW_FOOD_LEVEL(3),
	OWNER_GIVE_HEALING_POTION(2),
	WIZARD_PLACE_TORCH(2),
	WIZARD_RIDDLE(3),
	OWNER_GIVE_FOOD(2),
	WIZARD_TAKE_DAMAGE(3),
	WIZARD_MUMBLING_WHILE_STUDYING_SPELL(3, new int[] {10, 13, 12}),
	WIZARD_FOLLOWING_PLAYER(9),
	WIZARD_TELL_STORY_WHILE_FOLLOWING_PLAYER(9),
	WIZARD_PLAYER_CLICK_HOLD_POSITION_BUTTON(2),
	WIZARD_NO_LECTERN_NEARBY(3),
	WIZARD_NOTHING_TO_LEARN(2),
	WIZARD_SPELL_TIER_TOO_HIGH(4),
	WIZARD_HOLDS_KNOWN_SPELL(1),
	WIZARD_SLAY_ENEMY(6),
	WIZARD_LOW_MANA(3),
	WIZARD_HOLDS_UNCASTABLE_SPELL(1),
	WIZARD_NEARBY_DISPENSER_SPELL_CAST(0),
	WIZARD_NEARBY_HOSTILE_SPELL_CAST(2),
	WIZARD_NO_MORE_FREE_SLOTS(1),
	WIZARD_NO_ARCANE_TOME(1),
	WIZARD_TIER_TOO_HIGH(1),
	WIZARD_OWNER_SPELL_CAST_COMPLIMENT_HIGH_TIER(2),
	WIZARD_OWNER_SPELL_CAST_COMPLIMENT_LOW_TIER(3),
	WIZARD_COMBAT(8),
	WIZARD_PLAYER_CLICK_STUDY_BUTTON(1),
	WIZARD_SET_HOME(1),
	WIZARD_RETURNED_FROM_JOURNEY(4),
	WIZARD_GOING_ON_JOURNEY(3),
	WIZARD_GO_HOME(3),
	WIZARD_FINISHED_SPELL_IDENTIFYING(1),
	WIZARD_TASKED_TO_IDENTIFY_SPELL(2),
	LEVEL_UP(3),
	WAND_TIER_TOO_HIGH(4),
	GREET(5),
	PLAYER_GIVES_HANDBOOK(14),
	PLAYER_GIVES_HANDBOOK_WITHOUT_REQUIREMENTS(14),
	WIZARD_FINISHED_SPELL_LEARNING(11);

	final int count;
	final int[] lineCounts;

	Speech(int count) {
		this.count = count;
		this.lineCounts = null; // No line counts specified
	}

	Speech(int count, int[] lineCounts) {
		this.count = count;
		this.lineCounts = lineCounts;
	}

	public int getCount() {
		return count;
	}

	public String getString() {
		return "message.apprenticearcana:" + this.toString().toLowerCase();
	}

	public String getRandom() {
		return getString() + "_" + ApprenticeArcana.rand.nextInt(count);
	}

	public void say(EntityWizardInitiate wizardInitiate) {
		wizardInitiate.sayImmediately(new TextComponentTranslation(this.getRandom()));
	}

	public void sayWithoutSpam(EntityWizardInitiate wizardInitiate) {
		wizardInitiate.sayWithoutSpam(new TextComponentTranslation(this.getRandom()));
	}

	public int getLineCount(int variationIndex) {
		if (lineCounts != null && variationIndex >= 0 && variationIndex < lineCounts.length) {
			return lineCounts[variationIndex];
		}
		throw new IllegalArgumentException("Invalid variation index or line counts not available: " + variationIndex);
	}

}
