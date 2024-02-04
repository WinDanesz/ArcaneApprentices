package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.ai.EntityAIAttackMelee;
import net.minecraft.entity.player.EntityPlayer;

public class WizardAIAttackMelee extends EntityAIAttackMelee {
	public WizardAIAttackMelee(EntityCreature creature, double speedIn, boolean useLongMemory) {
		super(creature, speedIn, useLongMemory);
	}

	@Override
	public boolean shouldContinueExecuting() {
		if (this.attacker instanceof EntityWizardInitiate) {
			Entity owner = ((EntityWizardInitiate) this.attacker).getOwner();
			if (owner instanceof EntityPlayer) {
				if (owner.getDistance(this.attacker) > 10) {
					this.attacker.getNavigator().clearPath();
					this.attacker.getNavigator().tryMoveToEntityLiving(owner, 1);
					this.attacker.getLookHelper().setLookPositionWithEntity(owner, 30.0F, 30.0F);
					this.attacker.setAttackTarget(null);
					((EntityWizardInitiate) this.attacker).resetChatCooldown();
					return false;
				}
			}
			return super.shouldContinueExecuting();
		}
		return super.shouldContinueExecuting();
	}
}
