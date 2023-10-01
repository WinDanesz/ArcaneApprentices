package com.windanesz.arcaneapprentices.entity.ai;

import electroblob.wizardry.block.BlockLectern;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public abstract class WizardAILecternBase extends EntityAIBase {

	@Nullable
	public static BlockPos findNearbyLectern(World world, BlockPos centre) {

		int searchRadius = 12;

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
