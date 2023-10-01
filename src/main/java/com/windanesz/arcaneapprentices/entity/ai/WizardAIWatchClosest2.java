package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest2;

public class WizardAIWatchClosest2 extends EntityAIWatchClosest2 {
	public WizardAIWatchClosest2(EntityLiving entitylivingIn, Class<? extends Entity> watchTargetClass, float maxDistance, float chanceIn) {
		super(entitylivingIn, watchTargetClass, maxDistance, chanceIn);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && ((EntityWizardInitiate) entity).getTask() != EntityWizardInitiate.Task.TRY_TO_SLEEP;
	}
}
