package com.windanesz.arcaneapprentices.village;

import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.MapGenStructureIO;
import net.minecraft.world.gen.structure.StructureBoundingBox;
import net.minecraft.world.gen.structure.StructureComponent;
import net.minecraft.world.gen.structure.StructureVillagePieces;
import net.minecraft.world.gen.structure.StructureVillagePieces.PieceWeight;
import net.minecraft.world.gen.structure.StructureVillagePieces.Start;
import net.minecraft.world.gen.structure.StructureVillagePieces.Village;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.IVillageCreationHandler;

import java.util.List;
import java.util.Random;

public class StructureWizardHouse extends StructureVillagePieces.House1 implements IVillageCreationHandler {

	public static void init() {
		MapGenStructureIO.registerStructureComponent(StructureWizardHouse.class, "WizardHouse");
		VillagerRegistry.instance().registerVillageCreationHandler(new StructureWizardHouse());
	}

	public StructureWizardHouse() {

	}

	@Override
	protected void spawnVillagers(World world, StructureBoundingBox structurebb, int x, int y, int z, int count) {
		for (int i = 0; i < count; ++i) {
			int j = this.getXWithOffset(x + i, z);
			int k = this.getYWithOffset(y);
			int l = this.getZWithOffset(x + i, z);

			if (!structurebb.isVecInside(new BlockPos(j, k, l))) {
				break;
			}

			EntityWizardInitiate wizard = new EntityWizardInitiate(world);
			wizard.onInitialSpawn(world.getDifficultyForLocation(new BlockPos((double) j + 0.5D, k, (double) l + 0.5D)), (IEntityLivingData) null);
			wizard.setLocationAndAngles((double) j + 0.5D, k, (double) l + 0.5D, 0.0F, 0.0F);
			world.spawnEntity(wizard);
		}
	}

	public StructureWizardHouse(StructureVillagePieces.Start start, int type, Random rand, StructureBoundingBox p_i45571_4_, EnumFacing facing) {
		super(start, type, rand, p_i45571_4_, facing);
	}

	@Override
	protected IBlockState getBiomeSpecificBlockState(IBlockState blockstateIn) {
		return super.getBiomeSpecificBlockState(blockstateIn);
	}

	@Override
	public PieceWeight getVillagePieceWeight(Random random, int i) {
		return new StructureVillagePieces.PieceWeight(StructureWizardHouse.class, 100, 1);
	}

	@Override
	public Class<?> getComponentClass() {
		return StructureWizardHouse.class;
	}

	@Override
	public Village buildComponent(PieceWeight villagePiece, Start startPiece, List<StructureComponent> pieces,
			Random random, int p1, int p2, int p3, EnumFacing facing, int p5) {
		StructureBoundingBox structureboundingbox = StructureBoundingBox.getComponentToAddBoundingBox(p1, p2, p3, 0, 0, 0, 5, 6, 5, facing);
		return canVillageGoDeeper(structureboundingbox) && StructureComponent.findIntersecting(pieces, structureboundingbox) == null
				? new StructureWizardHouse(startPiece, p5, random, structureboundingbox, facing)
				: null;

	}
}