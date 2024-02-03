package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;

import java.util.List;

public class WizardAILookAround extends EntityAIBase {

	protected EntityLiving wizard;
	protected float maxDistance;
	protected float chance;
	private int lookTime;
	private boolean settingLook = false;
	BlockPos lookPos = null ;
	protected Class<? extends Entity> watchedClass;

	public WizardAILookAround(EntityWizardInitiate wizard, float maxDistance, float chance) {
		this.wizard = wizard;
		this.maxDistance = maxDistance;
		this.chance = 0.02F;
		this.setMutexBits(2);
	}

	public WizardAILookAround(EntityLiving wizard, Class<? extends Entity> watchTargetClass, float maxDistance, float chanceIn) {
		this.wizard = wizard;
		this.watchedClass = watchTargetClass;
		this.maxDistance = maxDistance;
		this.chance = chanceIn;
		this.setMutexBits(2);
	}

	public boolean shouldExecute() {
		return ((EntityWizardInitiate) wizard).getTask() != EntityWizardInitiate.Task.TRY_TO_SLEEP && !(this.wizard.getRNG().nextFloat() >= this.chance);
	}

	public boolean shouldContinueExecuting() {
			return this.lookTime > 0;
	}

	public void startExecuting() {
		this.lookTime = 30 + this.wizard.getRNG().nextInt(70);
		settingLook = true;
	}

	public void resetTask() {
		settingLook = false;
		lookPos = null;
	}

	public void updateTask() {
		if (settingLook) {
			List<BlockPos> blockPosList = BlockUtils.getBlockSphere(wizard.getPosition(),12);
			int i = 1;
			if (!blockPosList.isEmpty()) {
				lookPos = Utils.getRandomItem(blockPosList);
			}
			settingLook = false;

			if (this.wizard.getDistance(lookPos.getX(), lookPos.getY(), lookPos.getZ()) < 4 && !this.wizard.world.isAirBlock(lookPos)) {
				((EntityWizardInitiate) this.wizard).sayWithoutSpam(("What an interesting " + this.wizard.world.getBlockState(lookPos).getBlock().getLocalizedName()));
			}
		}

		if (lookPos != null) {
			this.wizard.getLookHelper().setLookPosition(
					lookPos.getX() + 0.5f,
					lookPos.getY() + 0.5f,
					lookPos.getZ() + 0.5f,
					(float) this.wizard.getHorizontalFaceSpeed()* 0.5f,
					(float) this.wizard.getVerticalFaceSpeed() * 0.5f);
		}

		--this.lookTime;
	}
}
