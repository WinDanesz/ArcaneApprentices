package com.windanesz.apprenticearcana.entity.ai;

import com.windanesz.apprenticearcana.Utils;
import com.windanesz.apprenticearcana.data.PlayerData;
import com.windanesz.apprenticearcana.data.Speech;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import electroblob.wizardry.util.BlockUtils;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.pathfinding.PathNavigate;
import net.minecraft.pathfinding.PathNodeType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import java.util.Arrays;

public class WizardAIGoHome extends EntityAIBase {
	private final EntityWizardInitiate wizard;
	private EntityLivingBase owner;
	World world;
	private final double walkSpeed;
	private final PathNavigate petPathfinder;
	private int timeToRecalcPath;
	float maxDist;
	private float oldWaterCost;

	public WizardAIGoHome(EntityWizardInitiate summonedCreature, double followSpeedIn, float maxDistIn) {
		this.wizard = summonedCreature;
		this.world = summonedCreature.world;
		this.walkSpeed = followSpeedIn;
		this.petPathfinder = summonedCreature.getNavigator();
		this.maxDist = maxDistIn;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		this.owner = this.wizard.getOwner() instanceof EntityPlayer ? (EntityPlayer) this.wizard.getOwner() : null;
		if (this.wizard.getTask() == EntityWizardInitiate.Task.GO_HOME) {
		 	BlockPos homePos = wizard.getHome().pos;
			if (this.wizard.world.isBlockLoaded(homePos) && this.wizard.getDistanceSq(homePos) < Math.pow(20,2) && world.isBlockLoaded(homePos)) {
				// TODO: say something
				return true;
			} else if (this.owner instanceof EntityPlayer) {
				// just disappear
				PlayerData.storePendingHomeApprentice((EntityPlayer) this.owner, this.wizard);
				// TODO: say something
				world.removeEntity(this.wizard);
			}
		}
		return false;
	}

	/**
	 * Returns whether an in-progress EntityAIBase should continue executing
	 */
	public boolean shouldContinueExecuting() {
		return this.owner != null && !this.petPathfinder.noPath() && this.wizard.getDistanceSq(this.owner) > (double) (this.maxDist * this.maxDist);
	}

	/**
	 * Execute a one shot task or start executing a continuous task
	 */
	public void startExecuting() {
		this.timeToRecalcPath = 0;
		this.oldWaterCost = this.wizard.getPathPriority(PathNodeType.WATER);
		this.wizard.setPathPriority(PathNodeType.WATER, 0.0F);
		BlockPos homePos = wizard.getHome().pos;
		this.wizard.getNavigator().tryMoveToXYZ(homePos.getX() + 0.5f, homePos.getY() + 0.5f, homePos.getZ() + 0.5f, walkSpeed);
	}

	/**
	 * Reset the task's internal state. Called when this task is interrupted by another one
	 */
	public void resetTask() {
		this.petPathfinder.clearPath();
		this.wizard.setPathPriority(PathNodeType.WATER, this.oldWaterCost);
	}

	/**
	 * Keep ticking a continuous task that has already been started
	 */
	public void updateTask() {}
}