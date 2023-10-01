package com.windanesz.arcaneapprentices.registry;

import com.windanesz.arcaneapprentices.advancement.CustomAdvancementTrigger;
import net.minecraft.advancements.CriteriaTriggers;

/**
 * Class responsible for defining, storing and registering all advancement triggers.
 */
public final class AAAdvancementTriggers {

	private AAAdvancementTriggers() {}

	public static final CustomAdvancementTrigger no_requirements_met = new CustomAdvancementTrigger("no_requirements_met");
	public static final CustomAdvancementTrigger take_apprentice = new CustomAdvancementTrigger("take_apprentice");
	public static final CustomAdvancementTrigger apprentice_go_on_journey = new CustomAdvancementTrigger("apprentice_go_on_journey");
	public static final CustomAdvancementTrigger apprentice_returns_from_journey = new CustomAdvancementTrigger("apprentice_returns_from_journey");
	public static final CustomAdvancementTrigger apprentice_learn_spell = new CustomAdvancementTrigger("apprentice_learn_spell");
	public static final CustomAdvancementTrigger apprentice_learn_max_spell = new CustomAdvancementTrigger("apprentice_learn_max_spell");
	public static final CustomAdvancementTrigger apprentice_artefact_use = new CustomAdvancementTrigger("apprentice_artefact_use");
	public static final CustomAdvancementTrigger apprentice_levels_up = new CustomAdvancementTrigger("apprentice_levels_up");
	public static final CustomAdvancementTrigger apprentice_max_level = new CustomAdvancementTrigger("apprentice_max_level");

	public static void register(){
		CriteriaTriggers.register(no_requirements_met);
		CriteriaTriggers.register(take_apprentice);
		CriteriaTriggers.register(apprentice_go_on_journey);
		CriteriaTriggers.register(apprentice_returns_from_journey);
		CriteriaTriggers.register(apprentice_learn_spell);
		CriteriaTriggers.register(apprentice_learn_max_spell);
		CriteriaTriggers.register(apprentice_artefact_use);
		CriteriaTriggers.register(apprentice_levels_up);
		CriteriaTriggers.register(apprentice_max_level);
	}
}
