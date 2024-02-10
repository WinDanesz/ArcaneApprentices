package com.windanesz.arcaneapprentices.entity.ai;

import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.data.Speech;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.handler.XpProgression;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.event.SpellCastEvent.Source;
import electroblob.wizardry.item.ItemScroll;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.packet.PacketNPCCastSpell;
import electroblob.wizardry.packet.WizardryPacketHandler;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.network.NetworkRegistry.TargetPoint;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WizardAIAttackSpellWithCost extends EntityAIBase {

	/**
	 * The entity the AI instance has been applied to. Thanks to type parameters, methods from both EntityLiving and
	 * ISummonedCreature may be invoked on this field.
	 */
	private final EntityWizardInitiate npc;
	private EntityLivingBase target;
	/**
	 * Decremented each tick while greater than 0. When a spell is cast, this is set to that spell's cooldown plus the
	 * base cooldown.
	 */
	private int cooldown;
	/**
	 * The number of ticks between the entity finding a new target and when it first starts attacking, and also the
	 * amount that is added to the spell's cooldown between casting spells.
	 */
	private final int baseCooldown;
	/**
	 * Decremented each tick while greater than 0. When a continuous spell is first cast, this is set to the value of
	 * {@link WizardAIAttackSpellWithCost#continuousSpellDuration}.
	 */
	// I think that in this case this is only necessary on the server side. If any inconsistent behaviour
	// occurs, look into syncing this as well.
	private int continuousSpellTimer;
	/**
	 * If true, buff spells will be casted on the owner (if present), instead of this entity.
	 */
	private boolean proxyBuffs;
	/**
	 * The number of ticks that continuous spells will be cast for before cooling down.
	 */
	private final int continuousSpellDuration;
	/**
	 * The speed that the entity should move when attacking. Only used when passed into the navigator.
	 */
	private final double speed;
	private int seeTime;
	private final float maxAttackDistance;
	private int attackTime = -1;
	private boolean strafingClockwise;
	private boolean strafingBackwards;
	private int strafingTime = -1;
	private int timeSinceDisengage = 0;

	private Spell currentSpell = Spells.none;
	/**
	 * Creates a new spell attack AI with the given parameters.
	 *
	 * @param wizard                The entity that that uses this AI.
	 * @param speed                   The speed that the entity should move when attacking. Only used when passed into the navigator.
	 * @param maxDistance             The maximum distance the entity should be from its target.
	 * @param baseCooldown            The number of ticks between the entity finding a new target and when it first starts
	 *                                attacking, and also the amount that is added to the cooldown of the spell that has just been cast.
	 * @param continuousSpellDuration The number of ticks that continuous spells will be cast for before cooling down.
	 */
	public WizardAIAttackSpellWithCost(EntityWizardInitiate wizard, double speed, float maxDistance, int baseCooldown, int continuousSpellDuration, boolean proxyBuffs) {
		this.cooldown = -1;
		this.npc = wizard;
		this.baseCooldown = baseCooldown;
		this.continuousSpellDuration = continuousSpellDuration;
		this.speed = speed;
		this.maxAttackDistance = maxDistance * maxDistance;
		this.proxyBuffs = proxyBuffs;
		this.setMutexBits(3);
	}

	@Override
	public boolean shouldExecute() {
		if (!(this.npc.getHeldItemMainhand().getItem() instanceof ItemWand)) {
			return false;
		}

		EntityLivingBase entitylivingbase = this.npc.getAttackTarget();

		if (entitylivingbase == null) {
			return false;
		} else {
			this.target = entitylivingbase;
			return true;
		}
	}

	@Override
	public boolean shouldContinueExecuting() {
		return this.shouldExecute() || !this.npc.getNavigator().noPath();
	}

	@Override
	public void resetTask() {
		this.target = null;
		this.seeTime = 0;
		this.cooldown = -1;
		this.setContinuousSpellAndNotify(Spells.none, new SpellModifiers());
		this.continuousSpellTimer = 0;
	}

	private void setContinuousSpellAndNotify(Spell spell, SpellModifiers modifiers) {
		npc.setContinuousSpell(spell);
		WizardryPacketHandler.net.sendToAllAround(
				new PacketNPCCastSpell.Message(npc.getEntityId(), target == null ? -1 : target.getEntityId(),
						EnumHand.MAIN_HAND, spell, modifiers),
				// Particles are usually only visible from 16 blocks away, so 128 is more than far enough.
				// TODO: Why is this one a 128 block radius, whilst the other one is all in dimension?
				new TargetPoint(npc.dimension, npc.posX, npc.posY, npc.posZ, 128));
	}

	@Override
	public void updateTask() {

		// Only executed server side.

		double distanceSq = this.npc.getDistanceSq(this.target.posX, this.target.posY,
				this.target.posZ);
		boolean targetIsVisible = this.npc.getEntitySenses().canSee(this.target);

		if (targetIsVisible) {
			++this.seeTime;
		} else {
			this.seeTime = 0;
		}

		// Allied target

		// Hostile target
		if (npc.getAttackTarget() != null && npc.world.rand.nextInt(200) == 0) {
			npc.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_COMBAT.getRandom(), npc.getAttackTarget().getDisplayName()));
		}

		if (distanceSq <= (double) this.maxAttackDistance && this.seeTime >= 20) {
			this.npc.getNavigator().clearPath();
		} else {
			this.npc.getNavigator().tryMoveToEntityLiving(this.target, this.speed);
		}

		this.npc.getLookHelper().setLookPositionWithEntity(this.target, 30.0F, 30.0F);

		if (this.continuousSpellTimer > 0) {

			if (this.target == null || this.target.isDead) {
				resetTask();
			}

			this.continuousSpellTimer--;

			// If the target goes out of range or out of sight...
			if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible
					// ...or the spell is cancelled via events...
					|| MinecraftForge.EVENT_BUS
					.post(new SpellCastEvent.Tick(Source.NPC, npc.getContinuousSpell(), npc,
							npc.getModifiers(), this.continuousSpellDuration - this.continuousSpellTimer))
					// ...or the spell no longer succeeds...
					|| !npc.getContinuousSpell().cast(npc.world, npc, EnumHand.MAIN_HAND,
					this.continuousSpellDuration - this.continuousSpellTimer, target, npc.getModifiers())
					// ...or the time has elapsed...
					|| this.continuousSpellTimer == 0) {

				// ...reset the continuous spell timer and start the cooldown.
				this.continuousSpellTimer = 0;
				this.cooldown = npc.getContinuousSpell().getCooldown() + this.baseCooldown;
				setContinuousSpellAndNotify(Spells.none, new SpellModifiers());
				return;

			} else if (this.continuousSpellDuration - this.continuousSpellTimer == 1) {
				// On the first tick, if the spell did succeed, fire SpellCastEvent.Post.
				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(Source.NPC, npc.getContinuousSpell(),
						npc, npc.getModifiers()));
			}

		} else if (--this.cooldown == 0) {

			if (distanceSq > (double) this.maxAttackDistance || !targetIsVisible) {
				return;
			}

			double dx = target.posX - npc.posX;
			double dz = target.posZ - npc.posZ;

			List<Spell> spells = new ArrayList<Spell>(npc.getSpells());
			spells.removeIf(spell -> spell == Spells.none);
			spells.removeIf(npc::isSpellDisabled);

			if (spells.size() > 0) {

				if (!npc.world.isRemote) {

					// New way of choosing a spell; keeps trying until one works or all have been tried

					Spell spell;

					while (!spells.isEmpty()) {
						if (this.npc.getHeldItemMainhand().getItem() instanceof ItemWand) {

							Tier wandTier = ((ItemWand) this.npc.getHeldItemMainhand().getItem()).tier;

							if (npc.getCurrentTierCap() < wandTier.ordinal()) {
								npc.sayWithoutSpam(new TextComponentTranslation(Speech.WAND_TIER_TOO_HIGH.getRandom()));
								spell = null;
								break;
							}

							spell = Utils.getRandomItem(spells);

							// prioritize not dying from low health...
							if (this.npc.getHealth() < this.npc.getMaxHealth() * 0.4f) {
								List<Spell> healSpells = Arrays.asList(Spells.heal, Spells.greater_heal, Spells.healing_aura, Spells.group_heal);
								List<Spell> matches = getMatches(spells, healSpells);
								if (!matches.isEmpty()) {
									spell = Utils.getRandomItem(matches);
								}
								// prioritize not dying from fire...
							} else if (this.npc.isBurning()) {
								Spell extinguish = Spell.registry.getValue(new ResourceLocation("ancientspellcraft:extinguish"));			
								List<Spell> fireCounters = new ArrayList<>(Arrays.asList(Spells.fire_resistance, Spells.blink));
								if (extinguish != null) {
									fireCounters.add(extinguish);
								}
								List<Spell> matches = getMatches(spells, fireCounters);
								if (!matches.isEmpty()) {
									spell = Utils.getRandomItem(matches);
								}
							}

							ItemStack wandStack = this.npc.getHeldItemMainhand();

							int requiredMana = spell.getCost();
							int currentMana = ((ItemWand) wandStack.getItem()).getMana(wandStack);
							if (currentMana < requiredMana) {
								// not enough mana to cast this
								this.npc.sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_LOW_MANA.getRandom()));
								spells.remove(spell);
								spell = null;
							}
						} else {
							return;
						}

						// add artefact to occassionally execute buffs as the owner
						//noinspection ConstantConditions
						//						if ((spell instanceof SpellBuff || spell.getType() == SpellType.BUFF || spell.getType() == SpellType.DEFENCE)
						//								&& (attacker instanceof ISummonedCreature && ((ISummonedCreature) attacker).getCaster() != null
						//								&& attacker.getDistance(((ISummonedCreature) attacker).getCaster()) < 20)) {
						//
						//							if (((ISummonedCreature) attacker).getCaster() instanceof EntityPlayer) {
						//								EntityPlayer player = (EntityPlayer) ((ISummonedCreature) attacker).getCaster();
						//								if (SpellcastUtils.tryCastSpellAsPlayer(player, spell, EnumHand.MAIN_HAND, Source.WAND, new SpellModifiers(), 60)) {
						//									this.cooldown = this.baseCooldown + spell.getCooldown() * 2;
						//								}
						//							} else if (((ISummonedCreature) attacker).getCaster() instanceof EntityLiving) {
						//								EntityLiving living = (EntityLiving) ((ISummonedCreature) attacker).getCaster();
						//								if (SpellcastUtils.tryCastSpellAsMob(living, spell, null)) {
						//									this.cooldown = this.baseCooldown + spell.getCooldown() * 2;
						//								}
						//							}
						//						}

						SpellModifiers modifiers = npc.getModifiers();

						if (spell != null && npc.setCurrentSpell(spell) && attemptCastSpell(spell, modifiers)) {
							// The spell worked, so we're done!
							npc.rotationYaw = (float) (Math.atan2(dz, dx) * 180.0D / Math.PI) - 90.0F;

							// -------------- NPC XP Progression --------------
							// get some xp progress for the spell cast
							// TODO: artefact that improves xp gain on spell cast
							npc.addExperience(XpProgression.getXpGainPerSpellCast());

							ItemStack stack = this.npc.getHeldItemMainhand();
							ItemWand wand = ((ItemWand) stack.getItem());
							wand.consumeMana(this.npc.getHeldItemMainhand(), spell.getCost(), this.npc);

							// -------------- Wand Progression --------------
							if (wand.tier.level < Tier.MASTER.level) {

								// We don't care about cost modifiers here, otherwise players would be penalised for wearing robes!
								int progression = (int) (spell.getCost() * modifiers.get(SpellModifiers.PROGRESSION));
								WandHelper.addProgression(stack, progression);

								if (!Wizardry.settings.legacyWandLevelling) { // Don't display the message if legacy wand levelling is enabled
									// If the wand just gained enough progression to be upgraded...
									Tier nextTier = wand.tier.next();
									int excess = WandHelper.getProgression(stack) - nextTier.getProgression();
									if (excess >= 0 && excess < progression) {
										// ...display a message above the player's hotbar
										npc.playSound(WizardrySounds.ITEM_WAND_LEVELUP, 1.25f, 1);
									}
								}

							}

							return;
						} else {
							spells.remove(spell);
						}
					}
				}
			}

		} else if (this.cooldown < 0) {
			// This should only be reached when the entity first starts attacking. Stops it attacking instantly.
			this.cooldown = this.baseCooldown;
		}

		EntityLivingBase target = this.npc.getAttackTarget();

		boolean regrouping = false;
		if (this.npc.getTask() == EntityWizardInitiate.Task.FOLLOW) {
			Entity owner = this.npc.getOwner();
			if (owner instanceof EntityPlayer) {
				if (owner.getDistance(this.npc) > 8) {
					regrouping = true;
				}
			}
		}

		if (timeSinceDisengage == 0) {
			if (target != null) {
				double d0 = this.npc.getDistanceSq(target.posX, target.getEntityBoundingBox().minY, target.posZ);
				boolean flag = this.npc.getEntitySenses().canSee(target);
				boolean flag1 = this.seeTime > 0;

				if (flag != flag1) {
					this.seeTime = 0;
				}

				if (flag) {
					++this.seeTime;
				} else {
					--this.seeTime;
				}

				if (d0 <= ((double) this.maxAttackDistance * 0.1f) && this.seeTime >= 20) {
					this.npc.getNavigator().clearPath();
					++this.strafingTime;
				} else {
					if (this.npc.getDistanceSq(target) > 32) {
						this.npc.getNavigator().tryMoveToEntityLiving(target, this.speed);
						this.strafingTime = -1;
					}
				}

				if (this.strafingTime >= 20) {
					if ((double) this.npc.getRNG().nextFloat() < 0.3D) {
						this.strafingClockwise = !this.strafingClockwise;
					}

					if ((double) this.npc.getRNG().nextFloat() < 0.3D) {
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

					if (!regrouping) {
						this.npc.getMoveHelper().strafe(this.strafingBackwards ? -0.45F : 0.45F, this.strafingClockwise ? 0.3F : -0.3F);
						this.npc.faceEntity(target, 30.0F, 30.0F);
					} else {
						this.npc.getNavigator().clearPath();
						this.npc.getNavigator().tryMoveToEntityLiving(this.npc.getOwner(), speed);
						this.npc.getLookHelper().setLookPositionWithEntity(this.npc.getOwner(), 30.0F, 30.0F);
					}
				} else {
					this.npc.getLookHelper().setLookPositionWithEntity(target, 30.0F, 30.0F);
				}
			}
		} else {
			timeSinceDisengage--;
		}
	}

	/**
	 * Attempts to cast the given spell (including event firing) and returns true if it succeeded.
	 */
	public boolean attemptCastSpell(Spell spell, SpellModifiers modifiers) {

		// If anything stops the spell working at this point, nothing else happens.
		if (MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Pre(Source.NPC, spell, npc, modifiers))) {
			return false;
		}

		// This is only called when spell casting starts so ticksInUse is always zero
		if (spell.cast(npc.world, npc, EnumHand.MAIN_HAND, 0, target, modifiers)) {

			if (spell.isContinuous) {
				// -1 because the spell has been cast once already!
				this.continuousSpellTimer = this.continuousSpellDuration - 1;
				setContinuousSpellAndNotify(spell, modifiers);

			} else {

				MinecraftForge.EVENT_BUS.post(new SpellCastEvent.Post(Source.NPC, spell, npc, modifiers));

				// For now, the cooldown is just added to the constant base cooldown. I think this
				// is a reasonable way of doing things; it's certainly better than before.
				this.cooldown = this.baseCooldown + spell.getCooldown();

				if (spell.requiresPacket()) {
					// Sends a packet to all players in dimension to tell them to spawn particles.
					IMessage msg = new PacketNPCCastSpell.Message(npc.getEntityId(), target.getEntityId(),
							EnumHand.MAIN_HAND, spell, modifiers);
					WizardryPacketHandler.net.sendToDimension(msg, npc.world.provider.getDimension());
				}
				if (!npc.world.isRemote) {
					if (npc.getHeldItemMainhand().getItem() instanceof ItemScroll) {
						npc.getHeldItemMainhand().shrink(1);
					}
				}
			}

			return true;
		}

		return false;
	}

	// Helper method to get the matches between the two lists
	private static List<Spell> getMatches(List<Spell> sourceList, List<Spell> compareList) {
		// Iterate over the source list and check for matches
		List<Spell> matches = new ArrayList<>();
		for (Spell sourceObject : sourceList) {
			if (compareList.contains(sourceObject)) {
				matches.add(sourceObject);
			}
		}
		return matches;
	}
}




