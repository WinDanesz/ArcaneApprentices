package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAITarget;

public class WizardAIOwnerHurtByTarget extends EntityAITarget {
	EntityWizardInitiate wizard;
	EntityLivingBase attacker;
	private int timestamp;

	public WizardAIOwnerHurtByTarget(EntityWizardInitiate wizard) {
		super(wizard, false);
		this.wizard = wizard;
		this.setMutexBits(1);
	}

	public boolean shouldExecute() {
		if (this.wizard.getOwner() == null) {
			return false;
		} else {
			EntityLivingBase entitylivingbase = (EntityLivingBase) this.wizard.getOwner();

			if (entitylivingbase == null) {
				return false;
			} else {
				this.attacker = entitylivingbase.getRevengeTarget();
				int i = entitylivingbase.getRevengeTimer();
				return i != this.timestamp && this.isSuitableTarget(this.attacker, false);
			}
		}
	}

	public void startExecuting() {
		this.taskOwner.setAttackTarget(this.attacker);
		if (this.wizard.getOwner() != null) {

			EntityLivingBase entitylivingbase = (EntityLivingBase) this.wizard.getOwner();

			if (entitylivingbase != null) {
				this.timestamp = entitylivingbase.getRevengeTimer();
			}
		}

		super.startExecuting();
	}
}