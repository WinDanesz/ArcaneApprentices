package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import electroblob.wizardry.block.BlockLectern;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class WizardAIWatchClosestLectern extends EntityAIBase {
	protected EntityWizardInitiate wizard;
	protected int maxDistance;

	public WizardAIWatchClosestLectern(EntityWizardInitiate wizard, int maxDistance) {
		this.wizard = wizard;
		this.maxDistance = maxDistance;
		this.setMutexBits(2);
	}

	public boolean shouldExecute() {
		return this.wizard.isStudying();
	}

	public boolean shouldContinueExecuting() {
		if (shouldExecute()) {
			BlockPos pos = findNearbyLectern(this.wizard.world, this.wizard.getPosition(), maxDistance);
			if (pos != null) {
				this.wizard.setLectern(pos);
				return true;
			}
		}
		return false;
	}

	@Override
	public void resetTask() {
		this.wizard.setLectern(null);
	}

	public void updateTask() {
		if (this.wizard.getLectern() != null) {
			this.wizard.getLookHelper().setLookPosition(this.wizard.getLectern().getX(), this.wizard.getLectern().getY() + 1.5f,
					this.wizard.getLectern().getZ(), (float) this.wizard.getHorizontalFaceSpeed(), (float) this.wizard.getVerticalFaceSpeed());
		}
	}

	@Nullable
	public static BlockPos findNearbyLectern(World world, BlockPos centre, int searchRadius) {
		for (int x = -searchRadius; x <= searchRadius; x++) {
			for (int y = -searchRadius; y <= searchRadius; y++) {
				for (int z = -searchRadius; z <= searchRadius; z++) {

					BlockPos pos = centre.add(x, y, z);

					if (world.getBlockState(pos).getBlock() instanceof BlockLectern) {
						return pos;
					}

				}
			}
		}

		return null;
	}
}
