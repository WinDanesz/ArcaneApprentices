package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBow;
import net.minecraft.util.EnumHand;

public class WizardAIAttackRangedBow<T extends EntityWizardInitiate> extends EntityAIBase {
	private final T wizard;
	private final double moveSpeedAmp;
	private int attackCooldown;
	private final float maxAttackDistance;
	private int attackTime = -1;
	private int seeTime;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;
	private int ticksForRegroup = 0;

	public WizardAIAttackRangedBow(T wizard, double moveSpeedAmpIn, int attackCooldownIn, float maxAttackDistanceIn) {
		this.wizard = wizard;
		this.moveSpeedAmp = moveSpeedAmpIn;
		this.attackCooldown = attackCooldownIn;
		this.maxAttackDistance = maxAttackDistanceIn * maxAttackDistanceIn;
		this.setMutexBits(3);
	}

	public void setAttackCooldown(int p_189428_1_) {
		this.attackCooldown = p_189428_1_;
	}

	public boolean shouldExecute() {
		return this.wizard.getAttackTarget() == null ? false : this.isBowInMainhand();
	}

	protected boolean isBowInMainhand() {
		return !this.wizard.getHeldItemMainhand().isEmpty() && this.wizard.getHeldItemMainhand().getItem() instanceof ItemBow;
	}

	public boolean shouldContinueExecuting() {

		Entity owner = this.wizard.getOwner();
		if (owner instanceof EntityPlayer) {
			if (owner.getDistance(this.wizard) > 10) {
				this.wizard.getNavigator().clearPath();
				this.wizard.getNavigator().tryMoveToEntityLiving(this.wizard.getOwner(), 0.6);
				this.wizard.getLookHelper().setLookPositionWithEntity(this.wizard.getOwner(), 30.0F, 30.0F);
				return false;
			}
		}
		return (this.shouldExecute() || !this.wizard.getNavigator().noPath()) && this.isBowInMainhand();
	}

	public void startExecuting() {
		super.startExecuting();
		((IRangedAttackMob) this.wizard).setSwingingArms(true);
	}

	public void resetTask() {
		super.resetTask();
		((IRangedAttackMob) this.wizard).setSwingingArms(false);
		this.seeTime = 0;
		this.attackTime = -1;
		this.wizard.resetActiveHand();
	}

	public void updateTask() {
		if (!this.wizard.hasArrow()) {
			return;
		}


		EntityLivingBase target = this.wizard.getAttackTarget();
		boolean regrouping = false;
		if (this.wizard.getTask() == EntityWizardInitiate.Task.FOLLOW) {

		}


		if (target != null && !regrouping) {
			double d0 = this.wizard.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
			boolean flag = this.wizard.getEntitySenses().canSee(target);
			boolean flag1 = this.seeTime > 0;

			if (flag != flag1) {
				this.seeTime = 0;
			}

			if (flag) {
				++this.seeTime;
			} else {
				--this.seeTime;
			}

			if (d0 <= (double) this.maxAttackDistance && this.seeTime >= 20) {
				this.wizard.getNavigator().clearPath();
				++this.strafingTime;
			} else {
				this.wizard.getNavigator().tryMoveToEntityLiving(target, this.moveSpeedAmp);
				this.strafingTime = -1;
			}

			if (this.strafingTime >= 20) {
				if ((double) this.wizard.getRNG().nextFloat() < 0.3D) {
					this.strafingClockwise = !this.strafingClockwise;
				}

				if ((double) this.wizard.getRNG().nextFloat() < 0.3D) {
					this.strafingBackwards = !this.strafingBackwards;
				}

				this.strafingTime = 0;
			}

			if (this.strafingTime > -1) {
				if (d0 > (double) (this.maxAttackDistance * 0.75F)) {
					this.strafingBackwards = false;
				} else if (d0 < (double) (this.maxAttackDistance * 0.25F)) {
					this.strafingBackwards = true;
				}

				this.wizard.getMoveHelper().strafe(this.strafingBackwards ? -0.5F : 0.5F, this.strafingClockwise ? 0.5F : -0.5F);
				this.wizard.faceEntity(target, 30.0F, 30.0F);
			} else {
				this.wizard.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
			}

			if (this.wizard.isHandActive()) {
				if (!flag && this.seeTime < -60) {
					this.wizard.resetActiveHand();
				} else if (flag) {
					int i = this.wizard.getItemInUseMaxCount();

					if (i >= 20) {
						this.wizard.resetActiveHand();
						((IRangedAttackMob) this.wizard).attackEntityWithRangedAttack(target, ItemBow.getArrowVelocity(i));
						this.attackTime = this.attackCooldown;
					}
				}
			} else if (--this.attackTime <= 0 && this.seeTime >= -60) {
				this.wizard.setActiveHand(EnumHand.MAIN_HAND);
			}
		}
	}
}
