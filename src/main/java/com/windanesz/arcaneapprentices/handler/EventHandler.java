package com.windanesz.arcaneapprentices.handler;

import com.google.common.collect.Streams;
import com.windanesz.arcaneapprentices.Settings;
import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.data.PlayerData;
import com.windanesz.arcaneapprentices.data.Speech;
import com.windanesz.arcaneapprentices.data.StoredEntity;
import com.windanesz.arcaneapprentices.data.Talent;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.block.BlockReceptacle;
import electroblob.wizardry.constants.Constants;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.entity.construct.EntityFireRing;
import electroblob.wizardry.entity.construct.EntityIceBarrier;
import electroblob.wizardry.entity.living.EntityEvilWizard;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.entity.projectile.EntityDart;
import electroblob.wizardry.entity.projectile.EntityForceOrb;
import electroblob.wizardry.entity.projectile.EntityIceShard;
import electroblob.wizardry.event.SpellCastEvent;
import electroblob.wizardry.integration.DamageSafetyChecker;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemArtefact;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Banish;
import electroblob.wizardry.spell.CurseOfSoulbinding;
import electroblob.wizardry.spell.Disintegration;
import electroblob.wizardry.spell.GreaterHeal;
import electroblob.wizardry.spell.Heal;
import electroblob.wizardry.spell.HealAlly;
import electroblob.wizardry.spell.ImbueWeapon;
import electroblob.wizardry.spell.LifeDrain;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.spell.SpellConjuration;
import electroblob.wizardry.spell.SpellMinion;
import electroblob.wizardry.tileentity.TileEntityReceptacle;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.IElementalDamage;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.MagicDamage;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.PotionEvent;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.Event;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.UUID;
import java.util.stream.Collectors;

@Mod.EventBusSubscriber
public final class EventHandler {

	private EventHandler() {}

	/**
	 * Not really an event, but putting this here regardless at is a direct "fork" of {@link ItemArtefact#onPlayerTickEvent(TickEvent.PlayerTickEvent)}
	 */
	public static void tickArtefacts(EntityWizardInitiate npc) {

		World world = npc.world;

		for (ItemStack artefactStack : npc.getActiveArtefacts()) {

			Item artefact = artefactStack.getItem();

			if (Settings.isArtefactEnabledForNPC(artefact)) {

				if (artefact == WizardryItems.ring_condensing) {

					if (npc.ticksExisted % 150 == 0) {

						for (ItemStack stack : npc.getHeldItems()) {
							// Needs to be both of these interfaces because this ring only recharges wands
							// (or more accurately, chargeable spellcasting items)
							if (stack.getItem() instanceof ISpellCastingItem && stack.getItem() instanceof IManaStoringItem) {
								((IManaStoringItem) stack.getItem()).rechargeMana(stack, 1);
							}
						}
					}

				} else if (artefact == WizardryItems.amulet_arcane_defence) {

					if (npc.ticksExisted % 300 == 0) {
						for (ItemStack stack : npc.getArmorInventoryList()) {
							// IManaStoringItem is sufficient, since anything in the armour slots is probably armour
							if (stack.getItem() instanceof IManaStoringItem) {((IManaStoringItem) stack.getItem()).rechargeMana(stack, 1);}
						}
					}

				} else if (artefact == WizardryItems.amulet_recovery) {

					if (npc.shouldHeal() && npc.getHealth() < npc.getMaxHealth() / 2
							&& npc.ticksExisted % 50 == 0) {

						int totalArmourMana = Streams.stream(npc.getArmorInventoryList())
								.filter(s -> s.getItem() instanceof IManaStoringItem)
								.mapToInt(s -> ((IManaStoringItem) s.getItem()).getMana(s))
								.sum();

						if (totalArmourMana >= 2) {
							npc.heal(1);
							// 2 mana per half-heart, randomly distributed
							List<ItemStack> chargedArmour = Streams.stream(npc.getArmorInventoryList())
									.filter(s -> s.getItem() instanceof IManaStoringItem)
									.filter(s -> !((IManaStoringItem) s.getItem()).isManaEmpty(s))
									.collect(Collectors.toList());

							if (chargedArmour.size() == 1) {
								((IManaStoringItem) chargedArmour.get(0).getItem()).consumeMana(chargedArmour.get(0), 2, npc);
							} else {
								Collections.shuffle(chargedArmour);
								((IManaStoringItem) chargedArmour.get(0).getItem()).consumeMana(chargedArmour.get(0), 1, npc);
								((IManaStoringItem) chargedArmour.get(1).getItem()).consumeMana(chargedArmour.get(1), 1, npc);
							}
						}
					}

				} else if (artefact == WizardryItems.amulet_frost_warding) {

					if (!world.isRemote && npc.ticksExisted % 40 == 0) {

						List<EntityIceBarrier> barriers = world.getEntitiesWithinAABB(EntityIceBarrier.class, npc.getEntityBoundingBox().grow(1.5));

						// Check whether any barriers near the player are facing away from them, meaning the player is behind them
						if (!barriers.isEmpty() && barriers.stream().anyMatch(b -> b.getLookVec().dotProduct(b.getPositionVector().subtract(npc.getPositionVector())) > 0)) {
							npc.addPotionEffect(new PotionEffect(WizardryPotions.ward, 50, 1));
						}

					}

				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOW)
	public static void onSpellCastPreEvent(SpellCastEvent.Pre event) {

		if (event.getCaster() instanceof EntityWizardInitiate) {

			EntityWizardInitiate npc = (EntityWizardInitiate) event.getCaster();
			SpellModifiers modifiers = event.getModifiers();

			if (npc.getTalent() == Talent.SPELL_TINKERER && npc.hasTalentUnlocked() && AllyDesignationSystem.isAllied(event.getCaster(), npc)) {
				event.getModifiers().set(WizardryItems.duration_upgrade, event.getModifiers().get(WizardryItems.duration_upgrade) * 1.5f, false);
				event.getModifiers().set(WizardryItems.blast_upgrade, event.getModifiers().get(WizardryItems.duration_upgrade) * 1.5f, true);
				event.getModifiers().set(WizardryItems.range_upgrade, event.getModifiers().get(WizardryItems.duration_upgrade) * 1.5f, true);
			}

			for (ItemStack artefactStack : npc.getActiveArtefacts()) {
				Item artefact = artefactStack.getItem();

				if (Settings.isArtefactEnabledForNPC(artefact)) {

					float potency = modifiers.get(SpellModifiers.POTENCY);
					float cooldown = modifiers.get(WizardryItems.cooldown_upgrade);
					Biome biome = npc.world.getBiome(npc.getPosition());

					if (artefact == WizardryItems.ring_battlemage) {

						if (npc.getHeldItemOffhand().getItem() instanceof ISpellCastingItem
								&& ImbueWeapon.isSword(npc.getHeldItemMainhand())) {
							modifiers.set(SpellModifiers.POTENCY, 1.1f * potency, false);
						}

					} else if (artefact == WizardryItems.ring_fire_biome) {

						if (event.getSpell().getElement() == Element.FIRE
								&& BiomeDictionary.hasType(biome, BiomeDictionary.Type.HOT)
								&& BiomeDictionary.hasType(biome, BiomeDictionary.Type.DRY)) {
							modifiers.set(SpellModifiers.POTENCY, 1.3f * potency, false);
						}

					} else if (artefact == WizardryItems.ring_ice_biome) {

						if (event.getSpell().getElement() == Element.ICE
								&& BiomeDictionary.hasType(biome, BiomeDictionary.Type.SNOWY)) {
							modifiers.set(SpellModifiers.POTENCY, 1.3f * potency, false);
						}

					} else if (artefact == WizardryItems.ring_earth_biome) {

						if (event.getSpell().getElement() == Element.EARTH
								// If it was any forest that would be far too many, so taigas and jungles are excluded
								&& BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST)
								&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.CONIFEROUS)
								&& !BiomeDictionary.hasType(biome, BiomeDictionary.Type.JUNGLE)) {
							modifiers.set(SpellModifiers.POTENCY, 1.3f * potency, false);
						}

					} else if (artefact == WizardryItems.ring_storm) {

						if (event.getSpell().getElement() == Element.LIGHTNING && npc.world.isThundering()) {
							modifiers.set(WizardryItems.cooldown_upgrade, cooldown * 0.3f, false);
						}

					} else if (artefact == WizardryItems.ring_full_moon) {

						if (event.getSpell().getElement() == Element.EARTH && !npc.world.isDaytime()
								&& npc.world.provider.getMoonPhase(npc.world.getWorldTime()) == 0) {
							modifiers.set(WizardryItems.cooldown_upgrade, cooldown * 0.3f, false);
						}

					} else if (artefact == WizardryItems.ring_blockwrangler) {

						if (event.getSpell() == Spells.greater_telekinesis) {
							modifiers.set(SpellModifiers.POTENCY, modifiers.get(SpellModifiers.POTENCY) * 2, false);
						}

					} else if (artefact == WizardryItems.ring_conjurer) {

						if (event.getSpell() instanceof SpellConjuration) {
							modifiers.set(WizardryItems.duration_upgrade, modifiers.get(WizardryItems.duration_upgrade) * 2, false);
						}

					} else if (artefact == WizardryItems.charm_minion_health) {
						// We COULD check the spell is a SpellMinion here, but there's really no point
						modifiers.set(SpellMinion.HEALTH_MODIFIER, 1.25f * modifiers.get(SpellMinion.HEALTH_MODIFIER), true);

					} else if (artefact == WizardryItems.charm_experience_tome) {
						modifiers.set(SpellModifiers.PROGRESSION, modifiers.get(SpellModifiers.PROGRESSION) * 1.5f, false);
					}
				}
			}
		}
		if (event.getCaster() != null) {
			for (EntityWizardInitiate initiate : EntityUtils.getEntitiesWithinRadius(16, event.getCaster().posX, event.getCaster().posY, event.getCaster().posZ, event.getWorld(), EntityWizardInitiate.class)) {
				if (initiate.getTalent() == Talent.SPELL_TINKERER && initiate.hasTalentUnlocked() && AllyDesignationSystem.isAllied(event.getCaster(), initiate)) {
					if (initiate.getHeldItemMainhand().getItem() instanceof ItemWand) {
						Element elm = ((ItemWand) initiate.getHeldItemMainhand().getItem()).element;
						if (AllyDesignationSystem.isAllied(initiate, event.getCaster()) && event.getCaster().getHeldItemMainhand().getItem()
								instanceof ItemWand && ((ItemWand) event.getCaster().getHeldItemMainhand().getItem()).element == elm) {
							event.getModifiers().set(WizardryItems.duration_upgrade, event.getModifiers().get(WizardryItems.duration_upgrade) * 1.25f, false);
							event.getModifiers().set(WizardryItems.blast_upgrade, event.getModifiers().get(WizardryItems.blast_upgrade) * 1.25f, true);
							event.getModifiers().set(WizardryItems.range_upgrade, event.getModifiers().get(WizardryItems.range_upgrade) * 1.25f, true);
							if (event.getWorld().isRemote) {
								ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(BlockReceptacle.PARTICLE_COLOURS.get(elm)[0]).time(20)
										.pos(initiate.posX, initiate.posY + initiate.height / 2, initiate.posZ)
										.target(event.getCaster()).spawn(event.getWorld());
								ParticleBuilder.create(ParticleBuilder.Type.BEAM).clr(BlockReceptacle.PARTICLE_COLOURS.get(elm)[0]).time(10)
										.scale(2)
										.pos(initiate.posX, initiate.posY + initiate.height / 2, initiate.posZ)
										.target(event.getCaster()).spawn(event.getWorld());
							}
						}
					}
				}
			}
		}

	}

	@SubscribeEvent
	public static void onSpellCastPostEvent(SpellCastEvent.Post event) {
		if (event.getWorld().rand.nextFloat() < Settings.generalSettings.SPELL_REMARK_MESSAGE_CHANCE) {
			List<EntityWizardInitiate> wizards = new ArrayList<>();
			if (event.getCaster() != null) {
				wizards = Utils.getEntitiesWithinRadius(13, event.getCaster().posX, event.getCaster().posY, event.getCaster().posZ, event.getWorld(), EntityWizardInitiate.class);
			} else {
				wizards = Utils.getEntitiesWithinRadius(13, event.getX(), event.getY(), event.getZ(), event.getWorld(), EntityWizardInitiate.class);
			}
			if (!wizards.isEmpty()) {
				EntityWizardInitiate wizard = wizards.get(0);
				if (wizards.size() > 1) {
					wizard = wizards.get(event.getWorld().rand.nextInt(wizards.size() - 1));
				}

				if (event.getCaster() == wizard.getOwner()) {
					wizard.addExperience(Settings.generalSettings.XP_GAIN_ON_WATCHING_PLAYER_USING_MAGIC * (event.getSpell().getTier().ordinal() + 1));
					if (event.getSpell().getTier().ordinal() < 3) {
						wizard.sayImmediately(new TextComponentTranslation(Speech.WIZARD_OWNER_SPELL_CAST_COMPLIMENT_LOW_TIER.getRandom(), event.getSpell().getDisplayName()));
					} else {
						wizard.sayImmediately(new TextComponentTranslation(Speech.WIZARD_OWNER_SPELL_CAST_COMPLIMENT_HIGH_TIER.getRandom(), event.getSpell().getDisplayName()));
					}
				} else if (event.getSource() == SpellCastEvent.Source.DISPENSER) {
					wizard.sayImmediately(new TextComponentTranslation(Speech.WIZARD_NEARBY_DISPENSER_SPELL_CAST.getRandom(), event.getSpell().getDisplayName()));
				} else if (event.getCaster() instanceof EntityEvilWizard) {
					wizard.sayImmediately(new TextComponentTranslation(Speech.WIZARD_NEARBY_HOSTILE_SPELL_CAST.getRandom(), event.getSpell().getDisplayName()));
				}
			}
		}

		if (event.getCaster() instanceof EntityWizardInitiate) {

			EntityWizardInitiate caster = (EntityWizardInitiate) event.getCaster();

			if (Settings.isArtefactEnabledForNPC(WizardryItems.ring_paladin) && caster.isArtefactActive(WizardryItems.ring_paladin)) {

				if (event.getSpell() instanceof Heal || event.getSpell() instanceof HealAlly || event.getSpell() instanceof GreaterHeal) {
					// Spell properties allow all three of the above spells to be dealt with the same way - neat!
					float healthGained = event.getSpell().getProperty(Spell.HEALTH).floatValue() * event.getModifiers().get(SpellModifiers.POTENCY);

					List<EntityLivingBase> nearby = EntityUtils.getLivingWithinRadius(4, caster.posX, caster.posY, caster.posZ, event.getWorld());

					for (EntityLivingBase entity : nearby) {
						if (AllyDesignationSystem.isAllied(caster, entity) && entity.getHealth() > 0 && entity.getHealth() < entity.getMaxHealth()) {
							entity.heal(healthGained * 0.2f); // 1/5 of the amount healed by the spell itself
							if (event.getWorld().isRemote) {ParticleBuilder.spawnHealParticles(event.getWorld(), entity);}
						}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingHurtEvent(LivingHurtEvent event) {

		if (event.getEntity() instanceof EntityWizardInitiate) {

			EntityWizardInitiate npc = (EntityWizardInitiate) event.getEntity();

			for (ItemStack artefactStack : npc.getActiveArtefacts()) {

				Item artefact = artefactStack.getItem();

				if (Settings.isArtefactEnabledForNPC(artefact)) {

					if (artefact == WizardryItems.amulet_warding) {

						if (!event.getSource().isUnblockable() && event.getSource().isMagicDamage()) {
							event.setAmount(event.getAmount() * 0.9f);
						}

					} else if (artefact == WizardryItems.amulet_fire_protection) {

						if (event.getSource().isFireDamage()) {event.setAmount(event.getAmount() * 0.7f);}

					} else if (artefact == WizardryItems.amulet_ice_protection) {

						if (event.getSource() instanceof IElementalDamage
								&& ((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.FROST) {
							event.setAmount(event.getAmount() * 0.7f);
						}

					} else if (artefact == WizardryItems.amulet_channeling) {

						if (npc.world.rand.nextFloat() < 0.3f && event.getSource() instanceof IElementalDamage
								&& ((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.SHOCK) {
							event.setCanceled(true);
							return;
						}

					} else if (artefact == WizardryItems.amulet_fire_cloaking) {

						if (!event.getSource().isUnblockable()) {

							List<EntityFireRing> fireRings = npc.world.getEntitiesWithinAABB(EntityFireRing.class, npc.getEntityBoundingBox());

							for (EntityFireRing fireRing : fireRings) {
								if (fireRing.getCaster() instanceof EntityWizardInitiate && (fireRing.getCaster() == npc
										|| AllyDesignationSystem.isAllied(npc, fireRing.getCaster()))) {
									event.setAmount(event.getAmount() * 0.25f);
								}
							}
						}

					} else if (artefact == WizardryItems.amulet_potential) {

						if (npc.world.rand.nextFloat() < 0.2f && EntityUtils.isMeleeDamage(event.getSource())
								&& event.getSource().getTrueSource() instanceof EntityLivingBase) {

							EntityLivingBase target = (EntityLivingBase) event.getSource().getTrueSource();

							if (npc.world.isRemote) {

								ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).entity(event.getEntity())
										.pos(0, event.getEntity().height / 2, 0).target(target).spawn(npc.world);

								ParticleBuilder.spawnShockParticles(npc.world, target.posX,
										target.posY + target.height / 2, target.posZ);
							}

							DamageSafetyChecker.attackEntitySafely(target, MagicDamage.causeDirectMagicDamage(npc,
									MagicDamage.DamageType.SHOCK, true), Spells.static_aura.getProperty(Spell.DAMAGE).floatValue(), event.getSource().getDamageType());
							target.playSound(WizardrySounds.SPELL_STATIC_AURA_RETALIATE, 1.0F, npc.world.rand.nextFloat() * 0.4F + 1.5F);

						}

					} else if (artefact == WizardryItems.amulet_lich) {

						if (!event.getSource().isUnblockable() && npc.world.rand.nextFloat() < 0.15f) {

							List<EntityLiving> nearbyMobs = EntityUtils.getEntitiesWithinRadius(5, npc.posX, npc.posY, npc.posZ, npc.world, EntityLiving.class);
							nearbyMobs.removeIf(e -> !(e instanceof ISummonedCreature && ((ISummonedCreature) e).getCaster() == npc));

							if (!nearbyMobs.isEmpty()) {
								Collections.shuffle(nearbyMobs);
								// Even though we're passing the same damage source through, we still need the safety check
								DamageSafetyChecker.attackEntitySafely(nearbyMobs.get(0), event.getSource(), event.getAmount(), event.getSource().getDamageType());
								event.setCanceled(true);
								return; // Standard practice: stop as soon as the event is canceled
							}
						}

					} else if (artefact == WizardryItems.amulet_banishing) {

						if (npc.world.rand.nextFloat() < 0.2f && EntityUtils.isMeleeDamage(event.getSource())
								&& event.getSource().getTrueSource() instanceof EntityLivingBase) {

							EntityLivingBase target = (EntityLivingBase) event.getSource().getTrueSource();
							((Banish) Spells.banish).teleport(target, target.world, 8 + target.world.rand.nextDouble() * 8);
						}

					} else if (artefact == WizardryItems.amulet_transience) {

						if (npc.getHealth() <= 6 && npc.world.rand.nextFloat() < 0.25f) {
							npc.addPotionEffect(new PotionEffect(WizardryPotions.transience, 300));
							npc.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, 300, 0, false, false));
						}
					}
				}
			}
		}

		if (event.getSource().getTrueSource() instanceof EntityWizardInitiate) {

			EntityWizardInitiate npc = (EntityWizardInitiate) event.getSource().getTrueSource();
			ItemStack mainhandItem = npc.getHeldItemMainhand();
			World world = npc.world;

			for (ItemStack artefactStack : npc.getActiveArtefacts()) {

				Item artefact = artefactStack.getItem();
				if (Settings.isArtefactEnabledForNPC(artefact)) {

					if (artefact == WizardryItems.ring_fire_melee) {
						// Used ItemWand intentionally because we need the element
						// Other mods can always make their own events if they want their own spellcasting items to do this
						if (EntityUtils.isMeleeDamage(event.getSource()) && mainhandItem.getItem() instanceof ItemWand
								&& ((ItemWand) mainhandItem.getItem()).element == Element.FIRE) {
							event.getEntity().setFire(5);
						}

					} else if (artefact == WizardryItems.ring_ice_melee) {

						if (EntityUtils.isMeleeDamage(event.getSource()) && mainhandItem.getItem() instanceof ItemWand
								&& ((ItemWand) mainhandItem.getItem()).element == Element.ICE) {
							event.getEntityLiving().addPotionEffect(new PotionEffect(WizardryPotions.frost, 200, 0));
						}

					} else if (artefact == WizardryItems.ring_lightning_melee) {

						if (EntityUtils.isMeleeDamage(event.getSource()) && mainhandItem.getItem() instanceof ItemWand
								&& ((ItemWand) mainhandItem.getItem()).element == Element.LIGHTNING) {

							EntityUtils.getLivingWithinRadius(3, npc.posX, npc.posY, npc.posZ, world).stream()
									.filter(EntityUtils::isLiving)
									.filter(e -> e != npc)
									.min(Comparator.comparingDouble(npc::getDistanceSq))
									.ifPresent(target -> {

										if (world.isRemote) {

											ParticleBuilder.create(ParticleBuilder.Type.LIGHTNING).entity(event.getEntity())
													.pos(0, event.getEntity().height / 2, 0).target(target).spawn(world);

											ParticleBuilder.spawnShockParticles(world, target.posX,
													target.posY + target.height / 2, target.posZ);
										}

										DamageSafetyChecker.attackEntitySafely(target, MagicDamage.causeDirectMagicDamage(npc,
												MagicDamage.DamageType.SHOCK, true), Spells.static_aura.getProperty(Spell.DAMAGE).floatValue(), event.getSource().getDamageType());
										target.playSound(WizardrySounds.SPELL_STATIC_AURA_RETALIATE, 1.0F, world.rand.nextFloat() * 0.4F + 1.5F);
									});
						}

					} else if (artefact == WizardryItems.ring_necromancy_melee) {

						if (EntityUtils.isMeleeDamage(event.getSource()) && mainhandItem.getItem() instanceof ItemWand
								&& ((ItemWand) mainhandItem.getItem()).element == Element.NECROMANCY) {
							event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.WITHER, 200, 0));
						}

					} else if (artefact == WizardryItems.ring_earth_melee) {

						if (EntityUtils.isMeleeDamage(event.getSource()) && mainhandItem.getItem() instanceof ItemWand
								&& ((ItemWand) mainhandItem.getItem()).element == Element.EARTH) {
							event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 0));
						}

					} else if (artefact == WizardryItems.ring_shattering) {

						if (!npc.world.isRemote && npc.world.rand.nextFloat() < 0.15f
								&& event.getEntityLiving().getHealth() < 12f // Otherwise it's a bit overpowered!
								&& event.getEntityLiving().isPotionActive(WizardryPotions.frost)
								&& EntityUtils.isMeleeDamage(event.getSource())) {

							event.setAmount(12f);

							for (int i = 0; i < 8; i++) {
								double dx = event.getEntity().world.rand.nextDouble() - 0.5;
								double dy = event.getEntity().world.rand.nextDouble() - 0.5;
								double dz = event.getEntity().world.rand.nextDouble() - 0.5;
								EntityIceShard iceshard = new EntityIceShard(event.getEntity().world);
								iceshard.setPosition(event.getEntity().posX + dx + Math.signum(dx) * event.getEntity().width,
										event.getEntity().posY + event.getEntity().height / 2 + dy,
										event.getEntity().posZ + dz + Math.signum(dz) * event.getEntity().width);
								iceshard.motionX = dx * 1.5;
								iceshard.motionY = dy * 1.5;
								iceshard.motionZ = dz * 1.5;
								iceshard.setCaster(npc);
								event.getEntity().world.spawnEntity(iceshard);
							}
						}

					} else if (artefact == WizardryItems.ring_soulbinding) {

						// Best guess at necromancy spell damage: either it's wither damage...
						if ((event.getSource() instanceof IElementalDamage
								&& npc.getOwner() instanceof EntityPlayer
								&& (((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.WITHER))
								// or it's direct, non-melee damage and the player is holding a wand with a necromancy spell selected
								|| (event.getSource().getImmediateSource() == npc && !EntityUtils.isMeleeDamage(event.getSource())
								&& Streams.stream(npc.getHeldEquipment()).anyMatch(s -> s.getItem() instanceof ISpellCastingItem
								&& ((ISpellCastingItem) s.getItem()).getCurrentSpell(s).getElement() == Element.NECROMANCY))) {

							event.getEntityLiving().addPotionEffect(new PotionEffect(WizardryPotions.curse_of_soulbinding, 400));

							CurseOfSoulbinding.getSoulboundCreatures(WizardData.get((EntityPlayer) npc.getOwner())).add(event.getEntity().getUniqueID());
						}

					} else if (artefact == WizardryItems.ring_leeching) {

						// Best guess at necromancy spell damage: either it's wither damage...
						if (npc.world.rand.nextFloat() < 0.3f && ((event.getSource() instanceof IElementalDamage
								&& (((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.WITHER))
								// ...or it's direct, non-melee damage and the player is holding a wand with a necromancy spell selected
								|| (event.getSource().getImmediateSource() == npc && !EntityUtils.isMeleeDamage(event.getSource())
								&& Streams.stream(npc.getHeldEquipment()).anyMatch(s -> s.getItem() instanceof ISpellCastingItem
								&& ((ISpellCastingItem) s.getItem()).getCurrentSpell(s).getElement() == Element.NECROMANCY
								&& ((ISpellCastingItem) s.getItem()).getCurrentSpell(s) != Spells.life_drain)))) {

							if (npc.shouldHeal()) {
								npc.heal(event.getAmount() * Spells.life_drain.getProperty(LifeDrain.HEAL_FACTOR).floatValue());
							}
						}

					} else if (artefact == WizardryItems.ring_poison) {

						// Best guess at earth spell damage: either it's poison damage...
						if ((event.getSource() instanceof IElementalDamage
								&& (((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.POISON))
								// ...or it was from a dart...
								|| event.getSource().getImmediateSource() instanceof EntityDart
								// ...or it's direct, non-melee damage and the player is holding a wand with an earth spell selected
								|| (event.getSource().getImmediateSource() == npc && !EntityUtils.isMeleeDamage(event.getSource())
								&& Streams.stream(npc.getHeldEquipment()).anyMatch(s -> s.getItem() instanceof ISpellCastingItem
								&& ((ISpellCastingItem) s.getItem()).getCurrentSpell(s).getElement() == Element.EARTH))) {

							event.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.POISON, 200, 0));
						}

					} else if (artefact == WizardryItems.ring_extraction) {

						// Best guess at sorcery spell damage: either it's force damage...
						if ((event.getSource() instanceof IElementalDamage
								&& (((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.FORCE))
								// ...or it was from a force orb...
								|| event.getSource().getImmediateSource() instanceof EntityForceOrb
								// ...or it's direct, non-melee damage and the player is holding a wand with a sorcery spell selected
								|| (event.getSource().getImmediateSource() == npc && !EntityUtils.isMeleeDamage(event.getSource())
								&& Streams.stream(npc.getHeldEquipment()).anyMatch(s -> s.getItem() instanceof ISpellCastingItem
								&& ((ISpellCastingItem) s.getItem()).getCurrentSpell(s).getElement() == Element.SORCERY))) {

							npc.getHeldItems().stream()
									.filter(s -> s.getItem() instanceof ISpellCastingItem && s.getItem() instanceof IManaStoringItem
											&& !((IManaStoringItem) s.getItem()).isManaFull(s))
									.findFirst()
									.ifPresent(s -> ((IManaStoringItem) s.getItem()).rechargeMana(s, 4 + world.rand.nextInt(3)));
						}

					}

				}
			}
		}
	}

	@SubscribeEvent
	public static void onLivingDeathEvent(LivingDeathEvent event) {

		if (event.getEntity() instanceof EntityLivingBase && event.getSource().getTrueSource() instanceof EntityWizardInitiate) {
			EntityWizardInitiate wizard = (EntityWizardInitiate) event.getSource().getTrueSource();

			wizard.addExperience(XpProgression.getXpGainPerKill());
			if (wizard.world.rand.nextFloat() < Settings.generalSettings.KILL_MESSAGE_CHANCE) {
				wizard.sayImmediately(new TextComponentTranslation(Speech.WIZARD_SLAY_ENEMY.getRandom(), event.getEntity().getDisplayName()));
			}
			wizard.resetChatCooldown();
		}

		if (event.getSource().getTrueSource() instanceof EntityWizardInitiate) {

			EntityWizardInitiate npc = (EntityWizardInitiate) event.getSource().getTrueSource();

			for (ItemStack artefactStack : npc.getActiveArtefacts()) {
				Item artefact = artefactStack.getItem();

				if (artefact == WizardryItems.ring_combustion) {

					if (event.getSource() instanceof IElementalDamage && ((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.FIRE) {
						event.getEntity().world.createExplosion(event.getEntity(), event.getEntity().posX, event.getEntity().posY,
								event.getEntity().posZ, 1.5f, false);
					}

				} else if (artefact == WizardryItems.ring_disintegration) {

					if (event.getSource() instanceof IElementalDamage && ((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.FIRE) {
						Disintegration.spawnEmbers(event.getEntity().world, npc, event.getEntity(),
								Spells.disintegration.getProperty(Disintegration.EMBER_COUNT).intValue());
					}

				} else if (artefact == WizardryItems.ring_arcane_frost) {

					if (!npc.world.isRemote && event.getSource() instanceof IElementalDamage
							&& ((IElementalDamage) event.getSource()).getType() == MagicDamage.DamageType.FROST) {

						for (int i = 0; i < 8; i++) {
							double dx = event.getEntity().world.rand.nextDouble() - 0.5;
							double dy = event.getEntity().world.rand.nextDouble() - 0.5;
							double dz = event.getEntity().world.rand.nextDouble() - 0.5;
							EntityIceShard iceshard = new EntityIceShard(event.getEntity().world);
							iceshard.setPosition(event.getEntity().posX + dx + Math.signum(dx) * event.getEntity().width,
									event.getEntity().posY + event.getEntity().height / 2 + dy,
									event.getEntity().posZ + dz + Math.signum(dz) * event.getEntity().width);
							iceshard.motionX = dx * 1.5;
							iceshard.motionY = dy * 1.5;
							iceshard.motionZ = dz * 1.5;
							iceshard.setCaster(npc);
							event.getEntity().world.spawnEntity(iceshard);
						}
					}
				}
			}
		}
	}

	@SubscribeEvent(priority = EventPriority.LOWEST) // No siphoning if the event is cancelled, that could be exploited...
	public static void onLivingDeathEventLowest(LivingDeathEvent event) {

		if (event.getSource().getTrueSource() instanceof EntityWizardInitiate) {

			EntityWizardInitiate wizard = (EntityWizardInitiate) event.getSource().getTrueSource();

			for (ItemStack stack : wizard.getHeldItems()) {

				if (stack.getItem() instanceof IManaStoringItem && !((IManaStoringItem) stack.getItem()).isManaFull(stack)
						&& WandHelper.getUpgradeLevel(stack, WizardryItems.siphon_upgrade) > 0) {

					int mana = Constants.SIPHON_MANA_PER_LEVEL
							* WandHelper.getUpgradeLevel(stack, WizardryItems.siphon_upgrade)
							+ wizard.world.rand.nextInt(Constants.SIPHON_MANA_PER_LEVEL);

					if (wizard.isArtefactActive(WizardryItems.ring_siphoning)) {mana *= 1.3f;}

					((IManaStoringItem) stack.getItem()).rechargeMana(stack, mana);

					break; // Only recharge one item per kill
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPotionApplicableEvent(PotionEvent.PotionApplicableEvent event) {

		if (event.getEntity() instanceof EntityWizardInitiate) {

			EntityWizardInitiate npc = (EntityWizardInitiate) event.getEntity();
			for (ItemStack artefactStack : npc.getActiveArtefacts()) {
				Item artefact = artefactStack.getItem();

				if (Settings.isArtefactEnabledForNPC(artefact)) {

					if (artefact == WizardryItems.amulet_ice_immunity) {

						if (event.getPotionEffect().getPotion() == WizardryPotions.frost) {event.setResult(Event.Result.DENY);}

					} else if (artefact == WizardryItems.amulet_wither_immunity) {

						if (event.getPotionEffect().getPotion() == MobEffects.WITHER) {event.setResult(Event.Result.DENY);}
					}
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerSleepInBedEvent(PlayerSleepInBedEvent event) {

		if (Settings.generalSettings.APPRENTICES_RESPAWN_AT_PLAYER_SPAWNPOINT) {
			List<StoredEntity> list = PlayerData.getDeadApprentices(event.getEntityPlayer());
			if (!list.isEmpty()) {

				List<UUID> respawnedEntities = new ArrayList();

				for (StoredEntity entity : list) {
					World world = event.getEntityPlayer().world;
					BlockPos pos = BlockUtils.findNearbyFloorSpace(event.getEntityPlayer(), 5, 5);
					Arrays.asList("CurrentStayPos", "Motion", "Leashed", "ActiveEffects", "FallDistance", "HurtTime", "Fire", "FoodLevel", "FoodSaturation", "Health")
							.forEach(tag -> entity.getNbtTagCompound().removeTag(tag));

					if (pos != null) {
						Entity mob = EntityList.createEntityFromNBT(entity.getNbtTagCompound(), world);
						if (mob instanceof EntityLivingBase) {
							//	((EntityLivingBase) mob).heal(((EntityLivingBase) mob).getMaxHealth());
							//	((EntityLivingBase) mob).setHealth(((EntityLivingBase) mob).getMaxHealth());
							mob.setPosition(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
							((EntityWizardInitiate) mob).setFoodLevel(20f);
							((EntityWizardInitiate) mob).setSaturation(10f);
							((EntityWizardInitiate) mob).resetChatCooldown();
							((EntityWizardInitiate) mob).resetRareEventCooldown();

							if (world.spawnEntity(mob)) {
								respawnedEntities.add(mob.getUniqueID());
								Utils.sendMessage(event.getEntityPlayer(), "message.arcaneapprentices:wizard_respawned", false, ((EntityWizardInitiate) mob).getDisplayNameWithoutOwner());
							}

						}
					}
				}

				for (UUID uuid : respawnedEntities) {
					PlayerData.removeDeadApprentice(event.getEntityPlayer(), uuid);
				}
			}
		}
	}

	@SubscribeEvent
	public static void onPlayerTickEvent(TickEvent.PlayerTickEvent event) {
		if (event.phase != TickEvent.Phase.END) {
			return; // Only process at the end of the tick phase
		}

		if (!event.player.world.isRemote && event.player.ticksExisted % 61 == 0) {

			List<StoredEntity> list = PlayerData.getPendingHomeApprentices(event.player);
			if (!list.isEmpty()) {

				List<UUID> respawnedEntities = new ArrayList();

				for (StoredEntity entity : list) {
					World world = event.player.world;

					if (entity.getNbtTagCompound().hasKey("HomePos")) {
						Location homePos = Location.fromNBT(entity.getNbtTagCompound().getCompoundTag("HomePos"));
						if (event.player.dimension == homePos.dimension && event.player.world.isBlockLoaded(homePos.pos) && event.player.getDistance(homePos.pos.getX(), homePos.pos.getY(), homePos.pos.getZ()) < 12) {

							BlockPos pos = BlockUtils.findNearbyFloorSpace(event.player.world, homePos.pos, 3, 3);
							Arrays.asList("CurrentStayPos", "Motion", "Leashed", "ActiveEffects", "FallDistance", "HurtTime", "Fire")
									.forEach(tag -> entity.getNbtTagCompound().removeTag(tag));

							if (pos != null) {
								Entity mob = EntityList.createEntityFromNBT(entity.getNbtTagCompound(), world);
								if (mob instanceof EntityLivingBase) {
									mob.setPosition(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
									if (world.spawnEntity(mob)) {
										respawnedEntities.add(mob.getUniqueID());
									}
								}
							}
						}
					}
				}

				for (UUID uuid : respawnedEntities) {
					PlayerData.removePendingHomeApprentice(event.player, uuid);
				}
			}
		}

		int tickRate = 55;
		if (!event.player.world.isRemote && event.player.ticksExisted % tickRate == 0) {
			List<StoredEntity> list = PlayerData.getAdventuringApprentices(event.player);
			if (!list.isEmpty()) {

				List<UUID> respawnedEntities = new ArrayList<>();
				List<UUID> updatedEntities = new ArrayList<>();

				for (StoredEntity entity : list) {
					World world = event.player.world;

					if (entity.getNbtTagCompound().hasKey("HomePos") && entity.getNbtTagCompound().hasKey("AdventureRemainingDuration")) {

						int remainingCountdown = entity.getNbtTagCompound().getInteger("AdventureRemainingDuration");
						if (remainingCountdown > tickRate) {
							updatedEntities.add(entity.getNbtTagCompound().getUniqueId("UUID"));
						} else {

							Location homePos = Location.fromNBT(entity.getNbtTagCompound().getCompoundTag("HomePos"));
							if (event.player.dimension == homePos.dimension && event.player.world.isBlockLoaded(homePos.pos) && event.player.getDistance(homePos.pos.getX(), homePos.pos.getY(), homePos.pos.getZ()) < 12) {

								BlockPos pos = BlockUtils.findNearbyFloorSpace(event.player.world, homePos.pos, 3, 3);
								Arrays.asList("CurrentStayPos", "Motion", "Leashed", "ActiveEffects", "FallDistance", "HurtTime", "Fire")
										.forEach(tag -> entity.getNbtTagCompound().removeTag(tag));

								if (pos != null) {
									Entity mob = EntityList.createEntityFromNBT(entity.getNbtTagCompound(), world);
									if (mob instanceof EntityLivingBase) {
										mob.setPosition(pos.getX() + 0.5f, pos.getY(), pos.getZ() + 0.5f);
										if (world.spawnEntity(mob)) {
											respawnedEntities.add(mob.getUniqueID());
											if (mob instanceof EntityWizardInitiate) {
												EntityWizardInitiate wizard = (EntityWizardInitiate) mob;
												wizard.returnFromAdventuring();

											}
										}
										respawnedEntities.add(mob.getUniqueID());
									}
								}
							}
						}
					}
				}

				for (UUID uuid : respawnedEntities) {
					PlayerData.removeAdventuringApprentice(event.player, uuid);
				}

				// Loop through the updatedEntities list, which contains UUIDs of apprentices that need updating.
				for (UUID uuid : updatedEntities) {
					// Get the list of adventuring apprentices for the current player.
					List<StoredEntity> list2 = PlayerData.getAdventuringApprentices(event.player);

					boolean entityUpdated = false;

					for (int i = 0; i < list2.size(); i++) {
						StoredEntity entity = list2.get(i);

						// Check if the UUID of the current entity matches the one we're updating.
						if (Objects.equals(entity.getNbtTagCompound().getUniqueId("UUID"), uuid)) {
							// Only update the entity once to avoid multiple updates for the same UUID.
							if (!entityUpdated) {
								// Get the remaining adventure duration for the entity.
								int remainingCountdown = entity.getNbtTagCompound().getInteger("AdventureRemainingDuration");

								// Decrease the remaining duration by the tickRate.
								remainingCountdown -= tickRate;

								// Update the entity's remaining adventure duration.
								entity.getNbtTagCompound().setInteger("AdventureRemainingDuration", remainingCountdown);

								// Remove the apprentice from the adventuring list.
								PlayerData.removeAdventuringApprentice(event.player, uuid);

								// Store the updated apprentice back into the adventuring list.
								PlayerData.storeAdventuringApprentice(event.player, entity);

								entityUpdated = true;
							}
						}
					}
				}

			}
		}
	}

	private static void handlePendingHomeApprentices(EntityPlayer player) {
	}

	private static void handleAdventuringApprentices(EntityPlayer player, int tickRate) {
	}
}
