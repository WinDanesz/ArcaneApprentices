package com.windanesz.apprenticearcana.entity.ai;

import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIWatchClosest;

public class WizardAIWatchClosest extends EntityAIWatchClosest {
	public WizardAIWatchClosest(EntityLiving entityIn, Class<? extends Entity> watchTargetClass, float maxDistance) {
		super(entityIn, watchTargetClass, maxDistance);
	}

	public WizardAIWatchClosest(EntityLiving entityIn, Class<? extends Entity> watchTargetClass, float maxDistance, float chanceIn) {
		super(entityIn, watchTargetClass, maxDistance, chanceIn);
	}

	@Override
	public boolean shouldExecute() {
		return super.shouldExecute() && ((EntityWizardInitiate) entity).getTask() != EntityWizardInitiate.Task.TRY_TO_SLEEP;
	}
}
