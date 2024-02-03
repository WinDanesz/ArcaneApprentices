package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.data.Speech;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.RandomPositionGenerator;
import net.minecraft.util.math.Vec3d;

import javax.annotation.Nullable;

public class WizardAIWander extends EntityAIWander {
	public WizardAIWander(EntityCreature creatureIn, double speedIn) {
		super(creatureIn, speedIn);
	}

	public WizardAIWander(EntityCreature creatureIn, double speedIn, int chance) {
		super(creatureIn, speedIn, chance);
	}

	@Override
	public boolean shouldExecute() {
		return ((EntityWizardInitiate) entity).getTask() != EntityWizardInitiate.Task.TRY_TO_SLEEP;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return entity.getAttackTarget() == null;
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		//if (entity.world.rand.nextInt(20) == 0) {
		if (entity.getAttackTarget() == null) {

			Speech.WIZARD_IDLING.sayWithoutSpam((EntityWizardInitiate) entity);
		}
		//}
		if (entity.getDistanceSq(((EntityWizardInitiate) entity).currentStayPos) > 12) {
			this.entity.getNavigator().tryMoveToXYZ(((EntityWizardInitiate) entity).currentStayPos.getX(), ((EntityWizardInitiate) entity).currentStayPos.getY(),
					((EntityWizardInitiate) entity).currentStayPos.getZ(), speed);
		}
	}

	@Nullable
	protected Vec3d getPosition()
	{
		return RandomPositionGenerator.findRandomTarget(this.entity, this.entity.world.rand.nextInt(10), 7);
	}


}
