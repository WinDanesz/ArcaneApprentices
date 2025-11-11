package com.windanesz.arcaneapprentices.worldgen;

import com.google.common.collect.ImmutableMap;
import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.Settings;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.tileentity.TileEntityBookshelf;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.GeometryUtils;
import electroblob.wizardry.worldgen.MossifierTemplateProcessor;
import electroblob.wizardry.worldgen.MultiTemplateProcessor;
import electroblob.wizardry.worldgen.WoodTypeTemplateProcessor;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.structure.template.ITemplateProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import org.apache.commons.lang3.ArrayUtils;

import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import static net.minecraft.block.BlockStoneSlab.VARIANT;

@Mod.EventBusSubscriber
public class WorldGenSchool extends WorldGenSurfaceStructure {

	private static final String WIZARD_DATA_BLOCK_TAG = "ebwizardry:wizard";
	private static final String APPRENTICE_DATA_BLOCK_TAG = "arcaneapprentices:wizard_initiate";

	private final Map<BiomeDictionary.Type, IBlockState> specialWallBlocks;
	private final Map<BiomeDictionary.Type, IBlockState> specialStairBlocks;
	private final Map<BiomeDictionary.Type, IBlockState> specialSlabBlocks;

	public static final String[] schoolFiles = {
			ArcaneApprentices.MODID + ":school_1",
			ArcaneApprentices.MODID + ":school_2",
			ArcaneApprentices.MODID + ":school_3",
			ArcaneApprentices.MODID + ":school_4",
			ArcaneApprentices.MODID + ":school_5"
	};

	private Set<BlockPos> structureBlocks;

	public WorldGenSchool() {
		// These are initialised here because it's a convenient point after the blocks are registered
		specialWallBlocks = ImmutableMap.of(
				BiomeDictionary.Type.MESA, Blocks.RED_SANDSTONE.getDefaultState(),
				BiomeDictionary.Type.MOUNTAIN, Blocks.STONEBRICK.getDefaultState(),
				BiomeDictionary.Type.NETHER, Blocks.NETHER_BRICK.getDefaultState(),
				BiomeDictionary.Type.SANDY, Blocks.SANDSTONE.getDefaultState()
		);

		specialStairBlocks = ImmutableMap.of(
				BiomeDictionary.Type.MESA, Blocks.SANDSTONE_STAIRS.getDefaultState(),
				BiomeDictionary.Type.MOUNTAIN, Blocks.STONE_BRICK_STAIRS.getDefaultState(),
				BiomeDictionary.Type.NETHER, Blocks.NETHER_BRICK_STAIRS.getDefaultState(),
				BiomeDictionary.Type.SANDY, Blocks.SANDSTONE_STAIRS.getDefaultState()
		);

		specialSlabBlocks = ImmutableMap.of(
				BiomeDictionary.Type.MESA, Blocks.STONE_SLAB2.getDefaultState(),
				BiomeDictionary.Type.MOUNTAIN, Blocks.STONE_SLAB.getDefaultState(),
				BiomeDictionary.Type.NETHER, Blocks.STONE_SLAB.getDefaultState().withProperty(VARIANT, BlockStoneSlab.EnumType.NETHERBRICK),
				BiomeDictionary.Type.SANDY, Blocks.STONE_SLAB.getDefaultState().withProperty(VARIANT, BlockStoneSlab.EnumType.SAND)
		);
	}

	@Override
	public String getStructureName() {
		return "wizard_school";
	}

	@Override
	public long getRandomSeedModifier() {
		return 73829104L; // Random seed for school generation
	}

	@Override
	public boolean canGenerate(Random random, World world, int chunkX, int chunkZ) {
		return ArrayUtils.contains(Settings.worldgenSettings.schoolDimensions, world.provider.getDimension())
				&& Settings.worldgenSettings.schoolRarity > 0 && random.nextInt(Settings.worldgenSettings.schoolRarity) == 0;
	}

	@Override
	public ResourceLocation getStructureFile(Random random) {
		String structurePath = schoolFiles[random.nextInt(schoolFiles.length)];
		return new ResourceLocation(structurePath);
	}

	@Override
	protected BlockPos attemptPosition(Template template, PlacementSettings settings, Random random, World world,
									   int chunkX, int chunkZ, String structureFile) {
		// Get the position from parent class
		BlockPos pos = super.attemptPosition(template, settings, random, world, chunkX, chunkZ, structureFile);

		if (pos == null) return null;

		if (structureFile.contains("school_5")) {
			pos = pos.down(8);
		}

		// Get structure dimensions
		BlockPos size = template.transformedSize(settings.getRotation());

		// Check all positions within the structure's bounding box
		for (int x = 0; x < size.getX(); x++) {
			for (int z = 0; z < size.getZ(); z++) {
				for (int y = 0; y < size.getY(); y++) {
					BlockPos checkPos = pos.add(x, y, z);
					IBlockState state = world.getBlockState(checkPos);

					// Reject if we would replace stone bricks or cobblestone
					if (state.getBlock() == Blocks.STONEBRICK || state.getBlock() == Blocks.COBBLESTONE) {
						return null;
					}
				}
			}
		}

		return pos;
	}

	@Override
	public void spawnStructure(Random random, World world, BlockPos origin, Template template, PlacementSettings settings, ResourceLocation structureFile) {

		// Mark school on Antique Atlas if integration is enabled
		com.windanesz.arcaneapprentices.integration.antiqueatlas.AAAntiqueAtlasIntegration.markSchool(world, origin.getX(), origin.getZ());

		final EnumDyeColor colour = EnumDyeColor.values()[random.nextInt(EnumDyeColor.values().length)];
		final Biome biome = world.getBiome(origin);
		IBlockState biomeCover = biome.topBlock;
		final float mossiness = getBiomeMossiness(biome);

		final IBlockState wallMaterial = specialWallBlocks.keySet().stream().filter(t -> BiomeDictionary.hasType(biome, t))
				.findFirst().map(specialWallBlocks::get).orElse(Blocks.COBBLESTONE.getDefaultState());

		final IBlockState stairMaterial = specialStairBlocks.keySet().stream().filter(t -> BiomeDictionary.hasType(biome, t))
				.findFirst().map(specialStairBlocks::get).orElse(Blocks.STONE_STAIRS.getDefaultState());

		final IBlockState slabMaterial = specialSlabBlocks.keySet().stream().filter(t -> BiomeDictionary.hasType(biome, t))
				.findFirst().map(specialSlabBlocks::get).orElse(Blocks.STONE_SLAB.getDefaultState());

		final BlockPlanks.EnumType woodType = BlockUtils.getBiomeWoodVariant(biome);

		structureBlocks = new HashSet<>();

		ITemplateProcessor processor = new MultiTemplateProcessor(true,
				// wool colour
				(w, p, i) -> i.blockState.getBlock() == Blocks.WOOL ? new Template.BlockInfo(
						i.pos, Blocks.WOOL.getStateFromMeta(colour.getMetadata()), i.tileentityData) : i,
				// carpet colour
				(w, p, i) -> i.blockState.getBlock() == Blocks.CARPET ? new Template.BlockInfo(
						i.pos, Blocks.CARPET.getStateFromMeta(colour.getMetadata()), i.tileentityData) : i,
				// banner colour
				(w, p, i) -> i.blockState.getBlock() == Blocks.WALL_BANNER ? new Template.BlockInfo(
						i.pos, Blocks.CARPET.getStateFromMeta(colour.getMetadata()), i.tileentityData) : i,
				// Wall material
				(w, p, i) -> i.blockState.getBlock() == Blocks.COBBLESTONE ? new Template.BlockInfo(i.pos,
						wallMaterial, i.tileentityData) : i,
				// Stair material
				(w, p, i) -> i.blockState.getBlock() == Blocks.STONE_STAIRS ? new Template.BlockInfo(i.pos,
						stairMaterial
								.withProperty(BlockStairs.FACING, i.blockState.getValue(BlockStairs.FACING))
								.withProperty(BlockStairs.HALF, i.blockState.getValue(BlockStairs.HALF))
								.withProperty(BlockStairs.SHAPE, i.blockState.getValue(BlockStairs.SHAPE))
						, i.tileentityData) : i,
				// Slab material
				(w, p, i) -> i.blockState.getBlock() == Blocks.STONE_SLAB ? new Template.BlockInfo(i.pos,
						slabMaterial, i.tileentityData) : i,
				// change ground type to biome's cover block
				(w, p, i) -> i.blockState.getBlock() == Blocks.DIRT || i.blockState.getBlock() == Blocks.GRASS ? new Template.BlockInfo(i.pos,
						biomeCover, i.tileentityData) : i,
				// Bookshelf marker
				(w, p, i) -> {
					TileEntityBookshelf.markAsNatural(i.tileentityData);
					return i;
				},
				// Wood type - this processor handles planks, stairs, fences, etc.
				new WoodTypeTemplateProcessor(woodType),
				// Mossifier
				new MossifierTemplateProcessor(mossiness, 0.04f, origin.getY() + 1),
				// Block recording (the process() method doesn't get called for structure voids)
				(w, p, i) -> {
					if (i.blockState.getBlock() != Blocks.AIR) {
						structureBlocks.add(p);
					}
					return i;
				}
		);

		template.addBlocksToWorld(world, origin, processor, settings, 2 | 16);

		// Entity spawning - do this BEFORE edge blending to avoid data block positions being replaced
		Map<BlockPos, String> dataBlocks = template.getDataBlocks(origin, settings);
		Set<BlockPos> entitySpawnPositions = new HashSet<>();
		for (Map.Entry<BlockPos, String> entry : dataBlocks.entrySet()) {
			if (entry.getValue().startsWith("entity:")) {
				String entityType = entry.getValue().substring("entity:".length());
				Vec3d vec = GeometryUtils.getCentre(entry.getKey());
				entitySpawnPositions.add(entry.getKey()); // Track positions to skip in edge blending

				if (WIZARD_DATA_BLOCK_TAG.equals(entityType)) {
					spawnWizard(world, vec);
				} else if (APPRENTICE_DATA_BLOCK_TAG.equals(entityType)) {
					spawnWizardInitiate(world, vec);
				}
			}
		}

		if (settings.getBoundingBox() != null) {
			// Define edge blend factor
			float edgeBlendFactor = 0.3f;
			boolean isSnowyBiome = BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY) ||
					BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD);
			boolean isHighElevation = origin.getY() >= 90; // Consider Y >= 90 as high elevation
			boolean shouldHaveSnow = isSnowyBiome || isHighElevation;

			// Iterate through each position in the bounding box
			for (BlockPos currPos : BlockPos.getAllInBox(
					settings.getBoundingBox().minX, settings.getBoundingBox().minY - 8, settings.getBoundingBox().minZ,
					settings.getBoundingBox().maxX, settings.getBoundingBox().minY, settings.getBoundingBox().maxZ)) {

				// Skip entity spawn positions
				if (entitySpawnPositions.contains(currPos)) continue;

				// Place top blocks
				if (currPos.getY() == settings.getBoundingBox().minY && world.canSnowAt(currPos, true) && world.isAirBlock(currPos)) {
					world.setBlockState(currPos, Blocks.SNOW_LAYER.getDefaultState(), 2);
				} else {
					// Edge blending
					if (currPos.getY() != settings.getBoundingBox().minY || world.rand.nextFloat() < edgeBlendFactor) {
						// Check if the position is air or not solid
						if (world.getBlockState(currPos).getBlock() instanceof BlockTallGrass || world.isAirBlock(currPos) || world.getBlockState(currPos).getBlock() instanceof BlockBush || world.getBlockState(currPos).getBlock() instanceof BlockLog) {
							// Determine block type based on Y position
							IBlockState blockState = (currPos.getY() == settings.getBoundingBox().minY) ? biome.topBlock : biome.fillerBlock;

							// Set the block state
							world.setBlockState(currPos, blockState);
						}
					}
				}
			}

			// Add snow layers on top of structure blocks in snowy biomes or at high elevation
			if (shouldHaveSnow) {
				for (BlockPos structurePos : structureBlocks) {
					BlockPos abovePos = structurePos.up();
					IBlockState structureBlockState = world.getBlockState(structurePos);
					// Check if position is within the structure bounds, has a solid full top surface, sky is visible, and can support snow
					if (settings.getBoundingBox().isVecInside(abovePos) &&
							world.isAirBlock(abovePos) &&
							structureBlockState.isTopSolid() &&
							structureBlockState.isFullCube() &&
							world.canSeeSky(abovePos) &&
							world.canSnowAt(abovePos, false)) {
						world.setBlockState(abovePos, Blocks.SNOW_LAYER.getDefaultState(), 2);
					}
				}
			}

			// Landfill gaps under the structure to prevent floating sections
			for (BlockPos structurePos : structureBlocks) {
				// Only process blocks at the bottom layer of the structure
				if (structurePos.getY() == settings.getBoundingBox().minY) {
					IBlockState structureBlockState = world.getBlockState(structurePos);
					// Check if the block is stone or the biome's natural cover/filler block
					if (structureBlockState.getBlock() == Blocks.STONE ||
							structureBlockState.getBlock() == Blocks.COBBLESTONE ||
							structureBlockState.getBlock() == wallMaterial.getBlock() ||
							structureBlockState == biomeCover ||
							structureBlockState == biome.fillerBlock) {

						// Fill downward until we hit a solid block
						BlockPos currentPos = structurePos.down();
						int maxFillDepth = 20; // Prevent infinite loops
						int depth = 0;

						while (depth < maxFillDepth) {
							IBlockState currentState = world.getBlockState(currentPos);
							// Stop if we hit a solid, non-replaceable block
							if (currentState.isFullCube() && !currentState.getBlock().isReplaceable(world, currentPos)) {
								break;
							}
							// Fill replaceable blocks (grass, flowers, snow) and non-solid blocks (air, water)
							world.setBlockState(currentPos, biome.fillerBlock, 2);
							currentPos = currentPos.down();
							depth++;
						}
					}
				}
			}
		}
	}

	private void spawnWizard(World world, Vec3d pos) {
		EntityWizard wizard = new EntityWizard(world);
		wizard.setLocationAndAngles(pos.x, pos.y, pos.z, 0, 0);
		wizard.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(pos)), null);
		world.spawnEntity(wizard);
	}

	private void spawnWizardInitiate(World world, Vec3d pos) {
		EntityWizardInitiate wizard = new EntityWizardInitiate(world);
		wizard.setLocationAndAngles(pos.x, pos.y, pos.z, 0, 0);
		wizard.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(pos)), null);
		wizard.setHome(new electroblob.wizardry.util.Location(new BlockPos(pos), world.provider.getDimension()));
		world.spawnEntity(wizard);
	}

	@SubscribeEvent
	public static void onPlayerTick(TickEvent.PlayerTickEvent event) {
		if (event.player instanceof EntityPlayerMP && event.player.ticksExisted % 20 == 0) {
			WizardryAdvancementTriggers.visit_structure.trigger((EntityPlayerMP) event.player);
		}
	}

	private static float getBiomeMossiness(Biome biome) {
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DENSE)) {
			return 0.7f;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE)) {
			return 0.7f;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
			return 0.5f;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.SWAMP)) {
			return 0.5f;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)) {
			return 0.3f;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.LUSH)) {
			return 0.3f;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)) {
			return 0;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.COLD)) {
			return 0;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.DEAD)) {
			return 0;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.WASTELAND)) {
			return 0;
		}
		if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.NETHER)) {
			return 0;
		}
		return 0.1f; // Everything else (plains, etc.) has a small amount of moss
	}

	@Override
	protected void postGenerate(Random random, World world, PlacementSettings settings) {
		// Call parent's tree removal but pass our structure blocks so they don't get removed
		if (!Wizardry.settings.fastWorldgen) {
			removeFloatingTrees(world, settings.getBoundingBox(), random, structureBlocks);
		}
	}
}
