package com.windanesz.apprenticearcana.entity.ai;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.Settings;
import com.windanesz.apprenticearcana.data.Speech;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import electroblob.wizardry.block.BlockLectern;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;

import javax.annotation.Nullable;

public class EntityAIStudy extends EntityAIBase {

	private final EntityWizardInitiate wizard;

	private final float maxLecternFindDistance;
	private final float maxLecternUseDistance;

	public EntityAIStudy(EntityWizardInitiate wizard, int maxLecternFindDistance, int maxLecternUseDistance) {
		this.wizard = wizard;
		this.maxLecternFindDistance = maxLecternFindDistance;
		this.maxLecternUseDistance = maxLecternUseDistance;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if (this.wizard.getTask() == EntityWizardInitiate.Task.STUDY) {

			if (!this.wizard.canLearnNewSpell()) {
				if (this.wizard.ticksExisted % 200 == 0) {
					this.wizard.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_NO_MORE_FREE_SLOTS.getRandom()));
				}
				return false;
			}

			if (this.wizard.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemSpellBook) {
				Spell spell = Spell.byMetadata(this.wizard.getHeldItem(EnumHand.OFF_HAND).getItemDamage());
				if (!this.wizard.isSpellKnown(spell)) {
					if (spell.canBeCastBy(this.wizard, false)) {

						if (spell.getTier().ordinal() <= this.wizard.getCurrentTierCap()) {
							BlockPos lectern = findNearbyLectern(this.wizard.world, this.wizard.getPosition());
							if (lectern != null) {
								this.wizard.setLectern(lectern);
								return true;
							} else if (this.wizard.ticksExisted % 200 == 0) {
								this.wizard.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_NO_LECTERN_NEARBY.getRandom()));
							}
						} else if (this.wizard.ticksExisted % 200 == 0) {
							this.wizard.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_TIER_TOO_HIGH.getRandom()));
						}
					} else if (this.wizard.ticksExisted % 200 == 0) {
						this.wizard.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_HOLDS_UNCASTABLE_SPELL.getRandom()));
					}
				} else if (this.wizard.ticksExisted % 200 == 0) {
					this.wizard.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_HOLDS_KNOWN_SPELL.getRandom(), spell.getDisplayName()));
				}
			} else if (this.wizard.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemSpellBook) {
				ItemStack offhand = this.wizard.getHeldItem(EnumHand.OFF_HAND).copy();
				ItemStack mainhand = this.wizard.getHeldItem(EnumHand.MAIN_HAND).copy();
				this.wizard.setHeldItem(EnumHand.MAIN_HAND, offhand);
				this.wizard.setHeldItem(EnumHand.OFF_HAND, mainhand);
			} else if (this.wizard.ticksExisted % 200 == 0) {
				this.wizard.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_NOTHING_TO_LEARN.getRandom()));
			}
		}
		return false;
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.shouldExecute() || !this.wizard.getNavigator().noPath();
	}

	@Override
	public void resetTask() {
		this.wizard.setLectern(null);
	}

	@Override
	public void updateTask() {
		// Only executed server side.
		if (wizard.getLectern() != null) {
			if (wizard.getDistanceSq(wizard.getLectern().getX() + 0.5, wizard.getLectern().getY(), wizard.getLectern().getZ() + 0.5) > 1.9) {
				// move to be in front of the lectern
				float offsetAmount = 0.7f;
				Vec3i directionVec = wizard.world.getBlockState(wizard.getLectern()).getValue(BlockLectern.FACING).getDirectionVec();
				Vec3d offsetVec = new Vec3d(directionVec.getX() * offsetAmount, directionVec.getY(), directionVec.getZ() * offsetAmount);
				Vec3d moveTo = new Vec3d(wizard.getLectern().getX() + 0.5, wizard.getLectern().getY(), wizard.getLectern().getZ() + 0.5).add(offsetVec);
				this.wizard.getNavigator().tryMoveToXYZ(moveTo.x, moveTo.y, moveTo.z, 0.35);
			} else {
				if (this.wizard.getLectern() != null) {
					this.wizard.getLookHelper().setLookPosition(this.wizard.getLectern().getX() + 0.5f, this.wizard.getLectern().getY() + (this.wizard.isChild() ? 1.5f : 0.5f),
							this.wizard.getLectern().getZ() + 0.5f, (float) this.wizard.getHorizontalFaceSpeed(), (float) this.wizard.getVerticalFaceSpeed());
					if (this.wizard.ticksExisted % 30 == 0 && this.wizard.getHeldItem(EnumHand.OFF_HAND).getItem() instanceof ItemSpellBook) {
						Spell spell = Spell.byMetadata(this.wizard.getHeldItem(EnumHand.OFF_HAND).getItemDamage());
						this.wizard.addStudyProgress(this.wizard.getStudyProgressForSpell(spell, 30));
						if (this.wizard.isStudyComplete()) {
							if (spell.getTier().ordinal() > 0) {
								if (!this.wizard.consumeArcaneTome(spell.getTier())) {
									if (this.wizard.ticksExisted % 200 == 0) {
										this.wizard.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_NO_ARCANE_TOME.getRandom(),
												spell.getTier().getDisplayName(), spell.getDisplayName()));
									}
									return;
								}
							}

							this.wizard.learnSpell(spell);
							if (this.wizard.getOwner() != null) {
								this.wizard.sayImmediately(new TextComponentTranslation(Speech.WIZARD_FINISHED_SPELL_LEARNING.getRandom(),
										spell.getDisplayName()));
							}
						} else {
							if (ApprenticeArcana.rand.nextInt(20) == 0) {
								Speech.WIZARD_MUMBLING_WHILE_STUDYING_SPELL.sayWithoutSpam(this.wizard);
							}
						}
					}
				}
			}
		}
	}

	/**
	 * Attempts to cast the given spell (including event firing) and returns true if it succeeded.
	 */
	private boolean attemptCastSpell(Spell spell, SpellModifiers modifiers) {

		//		// If anything stops the spell working at this point, nothing else happens.
		//		if(MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(Source.NPC, spell, attacker, modifiers))){
		//			return false;
		//		}
		//
		//		// This is only called when spell casting starts so ticksInUse is always zero
		//		if(spell.cast(attacker.world, attacker, EnumHand.MAIN_HAND, 0, target, modifiers)){
		//
		//			if(spell.isContinuous){
		//				// -1 because the spell has been cast once already!
		//				this.continuousSpellTimer = this.continuousSpellDuration - 1;
		//				setContinuousSpellAndNotify(spell, modifiers);
		//
		//			}else{
		//
		//				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(Source.NPC, spell, attacker, modifiers));
		//
		//				// For now, the cooldown is just added to the constant base cooldown. I think this
		//				// is a reasonable way of doing things; it's certainly better than before.
		//				this.cooldown = this.baseCooldown + spell.getCooldown();
		//
		//				if(spell.requiresPacket()){
		//					// Sends a packet to all players in dimension to tell them to spawn particles.
		//					IMessage msg = new PacketNPCCastSpell.Message(attacker.getEntityId(), target.getEntityId(),
		//							EnumHand.MAIN_HAND, spell, modifiers);
		//					WizardryPacketHandler.net.sendToDimension(msg, attacker.world.provider.getDimension());
		//				}
		//			}
		//
		//			return true;
		//		}

		return false;
	}

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

