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
		return super.shouldExecute() && ((EntityWizardInitiate) entity).getTask() != EntityWizardInitiate.Task.TRY_TO_SLEEP;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return entity.getAttackTarget() == null || !this.entity.getNavigator().noPath();
	}

	@Override
	public void startExecuting() {
		super.startExecuting();
		if (entity.getAttackTarget() == null && entity.world.rand.nextInt(20) == 0) {

			Speech.WIZARD_IDLING.sayWithoutSpam((EntityWizardInitiate) entity);
		}
		if (((EntityWizardInitiate) entity).getTask() == EntityWizardInitiate.Task.STAY && entity.getDistanceSq(((EntityWizardInitiate) entity).currentStayPos) > 12) {
			this.entity.getNavigator().tryMoveToXYZ(((EntityWizardInitiate) entity).currentStayPos.getX(), ((EntityWizardInitiate) entity).currentStayPos.getY(),
					((EntityWizardInitiate) entity).currentStayPos.getZ(), speed);
		}
	}

	@Nullable
	protected Vec3d getPosition() {
		return RandomPositionGenerator.findRandomTarget(this.entity, this.entity.world.rand.nextInt(5), 5);
	}

}
