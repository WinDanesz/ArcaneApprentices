package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.data.Speech;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
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

/**
 * AI class based on {@link net.minecraft.entity.ai.EntityAIBase}, which makes summons follow their owners
 */
public class WizardAIFollowOwner extends EntityAIBase {
	private final EntityWizardInitiate wizard;
	private EntityLivingBase owner;
	World world;
	private final double followSpeed;
	private final PathNavigate petPathfinder;
	private int timeToRecalcPath;
	float maxDist;
	float minDist;
	private float oldWaterCost;

	public WizardAIFollowOwner(EntityWizardInitiate summonedCreature, double followSpeedIn, float minDistIn, float maxDistIn) {
		this.wizard = summonedCreature;
		this.world = summonedCreature.world;
		this.followSpeed = followSpeedIn;
		this.petPathfinder = summonedCreature.getNavigator();
		this.minDist = minDistIn;
		this.maxDist = maxDistIn;
		this.setMutexBits(3);
	}

	public boolean shouldExecute() {
		if (owner instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) owner;

			if (wizard.world.rand.nextInt(300) == 0 ) {
				Vec3d vec3d = owner.getLook(1.0F).normalize();
				Vec3d vec3d1 = new Vec3d(this.wizard.posX - player.posX, this.wizard.getEntityBoundingBox().minY + (double) this.wizard.getEyeHeight() - (player.posY + (double) player.getEyeHeight()), this.wizard.posZ - player.posZ);
				double d0 = vec3d1.length();
				vec3d1 = vec3d1.normalize();
				double d1 = vec3d.dotProduct(vec3d1);
				boolean ownerLooksInEyes = d1 > 1.0D - 0.025D / d0 && player.canEntityBeSeen(this.wizard);
				if (ownerLooksInEyes) {
					wizard.sayWithoutSpam(new TextComponentTranslation(Speech.GREET.getRandom(), wizard.getOwner().getDisplayName()));
				}
			}


			if (wizard.world.rand.nextInt(3000) == 0 && player.getHealth() <= (player.getMaxHealth() * 0.4)) { // 400
				if (wizard.rareEventReady() && wizard.world.rand.nextFloat() <= EntityWizardInitiate.RARE_EVENT_CHANCE) {
					ItemStack item = PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), PotionTypes.HEALING);
					Utils.giveStackToPlayer(player, item);
					this.wizard.sayImmediately(new TextComponentTranslation(Speech.OWNER_GIVE_HEALING_POTION.getRandom(), item.getDisplayName()));
					this.wizard.resetRareEventCooldown();
				} else {
					Speech.OWNER_HAS_LOW_HEALTH.say(this.wizard);
				}
			} else if (wizard.world.rand.nextInt(2000) == 0 && player.getFoodStats().getFoodLevel() < 10) { // 400
				if (wizard.rareEventReady() && wizard.world.rand.nextFloat() <= EntityWizardInitiate.RARE_EVENT_CHANCE) {
					ItemStack itemStack = new ItemStack(Utils.getRandomItem(Arrays.asList(Items.BREAD, Items.COOKED_CHICKEN, Items.COOKED_FISH, Items.MUSHROOM_STEW)));
					this.wizard.sayImmediately(new TextComponentTranslation(Speech.OWNER_GIVE_FOOD.getRandom(), itemStack.getDisplayName()));
					Utils.giveStackToPlayer(player, itemStack);
					this.wizard.resetRareEventCooldown();
				} else {
					Speech.OWNER_HAS_LOW_FOOD_LEVEL.say(this.wizard);
				}
			}

			if (wizard.rareEventReady() && (wizard.world.rand.nextInt(1200) == 0)) {
				BlockPos pos = this.wizard.getPos();
				if (!world.canSeeSky(pos) && world.getLightBrightness(pos) < 0.4f && world.isAirBlock(pos)) {
					if (BlockUtils.canPlaceBlock(owner, world, pos)) {
						world.setBlockState(pos, Blocks.TORCH.getDefaultState());
						this.wizard.sayImmediately(new TextComponentTranslation(Speech.WIZARD_PLACE_TORCH.getRandom()));
						this.wizard.resetRareEventCooldown();
					}
				}
			}

			//			if ((wizard.world.rand.nextInt(4000) == 0) && !wizard.hasScheduledMessage()) {
			//				for (int i = 0; i <= 9; i++) {
			//					this.wizard.scheduleMessage(new MessageEntry(Speech.WIZARD_TELL_STORY_WHILE_FOLLOWING_PLAYER.getString() + "_0_" + i,
			//							Math.max(100, wizard.world.rand.nextInt(150))));
			//				}
			//				this.wizard.resetRareEventCooldown();
			//			}
		}

		this.owner = this.wizard.getOwner() instanceof EntityLivingBase ? (EntityLivingBase) this.wizard.getOwner() : null;
		if (this.wizard.getTask() == EntityWizardInitiate.Task.FOLLOW) {
			if (this.owner == null) {
				return false;
			} else if (this.owner instanceof EntityPlayer && ((EntityPlayer) this.owner).isSpectator()) {
				return false;
			} else if (this.wizard.getDistanceSq(this.owner) < (double) (this.minDist * this.minDist)) {
				return false;
			}
		} else {
			return false;
		}

		return true;
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
	public void updateTask() {
		this.wizard.getLookHelper().setLookPositionWithEntity(this.owner, 10.0F, (float) this.wizard.getVerticalFaceSpeed());

		if (--this.timeToRecalcPath <= 0) {
			this.timeToRecalcPath = 10;

			if (!this.petPathfinder.tryMoveToEntityLiving(this.owner, this.followSpeed)) {
				if (!this.wizard.getLeashed() && !this.wizard.isRiding()) {
					if (this.wizard.getDistanceSq(this.owner) >= 144.0D) {
						int i = MathHelper.floor(this.owner.posX) - 2;
						int j = MathHelper.floor(this.owner.posZ) - 2;
						int k = MathHelper.floor(this.owner.getEntityBoundingBox().minY);

						for (int l = 0; l <= 4; ++l) {
							for (int i1 = 0; i1 <= 4; ++i1) {
								if ((l < 1 || i1 < 1 || l > 3 || i1 > 3) && this.isTeleportFriendlyBlock(i, j, k, l, i1)) {
									this.wizard.setLocationAndAngles((double) ((float) (i + l) + 0.5F), (double) k, (double) ((float) (j + i1) + 0.5F), this.wizard.rotationYaw, this.wizard.rotationPitch);
									this.petPathfinder.clearPath();
									return;
								}
							}
						}
					}
				}
			}
		}

		if (wizard.world.rand.nextInt(600) == 0) {
			this.wizard.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_FOLLOWING_PLAYER.getRandom(), this.owner.getDisplayName()));
		}
	}

	protected boolean isTeleportFriendlyBlock(int x, int z, int y, int xOffset, int zOffset) {
		BlockPos blockpos = new BlockPos(x + xOffset, y - 1, z + zOffset);
		IBlockState iblockstate = this.world.getBlockState(blockpos);
		return iblockstate.getBlockFaceShape(this.world, blockpos, EnumFacing.DOWN) == BlockFaceShape.SOLID && iblockstate.canEntitySpawn(this.wizard) && this.world.isAirBlock(blockpos.up()) && this.world.isAirBlock(blockpos.up(2));
	}
}