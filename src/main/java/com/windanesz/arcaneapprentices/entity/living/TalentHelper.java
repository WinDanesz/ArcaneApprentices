package com.windanesz.arcaneapprentices.entity.living;

import electroblob.wizardry.constants.Element;
import electroblob.wizardry.item.IManaStoringItem;
import electroblob.wizardry.item.ISpellCastingItem;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.misc.WildcardTradeList;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.block.material.Material;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextComponentTranslation;

import java.util.Collection;
import java.util.List;
import java.util.Random;

public class TalentHelper {

	static void tickTalent(EntityWizardInitiate entityWizardInitiate) {
		Random rand = entityWizardInitiate.world.rand;

		switch (entityWizardInitiate.getTalent()) {
			case HEALER:
				if (entityWizardInitiate.ticksExisted % 200 == 0) {
					for (EntityLivingBase nearbyMob : EntityUtils.getEntitiesWithinRadius(10, entityWizardInitiate.posX, entityWizardInitiate.posY, entityWizardInitiate.posZ, entityWizardInitiate.world, EntityLivingBase.class)) {
						if (AllyDesignationSystem.isAllied(entityWizardInitiate, nearbyMob) && nearbyMob.getMaxHealth() > nearbyMob.getHealth()) {
							if (entityWizardInitiate.world.isRemote) {
								Vec3d origin = nearbyMob.getPositionEyes(1);
								for (int i = 0; i < 30; i++) {
									double x = origin.x - 1 + entityWizardInitiate.world.rand.nextDouble() * 2;
									double y = origin.y - 0.25 + entityWizardInitiate.world.rand.nextDouble() * 0.5;
									double z = origin.z - 1 + entityWizardInitiate.world.rand.nextDouble() * 2;
									if (entityWizardInitiate.world.rand.nextBoolean()) {
										ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f).clr(1f, 1f, 0.9f).spawn(entityWizardInitiate.world);
									} else {
										ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f).clr(1.0F, 1.0F, 0.3F).spawn(entityWizardInitiate.world);
									}
								}
							}
							nearbyMob.heal(2f);
						}
					}
				}
				break;

			case COOK:
				// Every 60 seconds (1200 ticks), 50% chance to create food if there is an empty slot
				if (!entityWizardInitiate.world.isRemote && entityWizardInitiate.ticksExisted % 1200 == 0) {
					if (rand.nextFloat() < 0.5f) {
						String[] foodList = com.windanesz.arcaneapprentices.Settings.generalSettings.APPRENTICE_COOK_FOOD_LIST;
						if (foodList != null && foodList.length > 0) {
							String foodString = foodList[rand.nextInt(foodList.length)];
							net.minecraft.item.ItemStack food = com.windanesz.arcaneapprentices.Settings.getItemFromString(foodString, entityWizardInitiate.world);
							if (!food.isEmpty()) {
								// If the config entry doesn't randomize count, randomize 1-2
								if (food.getCount() <= 1) {
									food.setCount(1 + rand.nextInt(2));
								}
								for (int i = 6; i < entityWizardInitiate.inventory.getSizeInventory(); i++) {
									if (entityWizardInitiate.inventory.getStackInSlot(i).isEmpty()) {
										entityWizardInitiate.inventory.setInventorySlotContents(i, food.copy());
										if (entityWizardInitiate.getOwner() instanceof EntityPlayer) {
											String foodName = food.getDisplayName();
											entityWizardInitiate.sayWithoutSpam((EntityPlayer) entityWizardInitiate.getOwner(), new net.minecraft.util.text.TextComponentTranslation("message.arcaneapprentices:apprentice_cook_food", entityWizardInitiate.getDisplayName(), food.getCount(), foodName));
										}
										break;
									}
								}
							}
						}
					}
				}
				break;

			case ALCHEMY_ADEPT:
				if (!entityWizardInitiate.world.isRemote && entityWizardInitiate.rareEventReady() && rand.nextInt(600) == 0) {
					{
						PotionType potiontype = null;

						if (rand.nextFloat() < 0.15F || entityWizardInitiate.isInsideOfMaterial(Material.WATER)) {
							potiontype = PotionTypes.WATER_BREATHING;
						} else if (rand.nextFloat() < 0.15F || ((entityWizardInitiate.getOwner() != null && entityWizardInitiate.getOwner().isBurning()) || entityWizardInitiate.isBurning() || entityWizardInitiate.getLastDamageSource() != null && entityWizardInitiate.getLastDamageSource().isFireDamage())) {
							potiontype = PotionTypes.FIRE_RESISTANCE;
						} else if (rand.nextFloat() < 0.05F) {
							potiontype = PotionTypes.HEALING;
						} else if (rand.nextFloat() < 0.5F) {
							potiontype = PotionTypes.SWIFTNESS;
						} else if (rand.nextFloat() < 0.3F) {
							potiontype = PotionTypes.SWIFTNESS;
						} else if (rand.nextFloat() < 0.04F) {
							potiontype = PotionTypes.REGENERATION;
						} else if (rand.nextFloat() < 0.04F) {
							potiontype = PotionTypes.INVISIBILITY;
						} else if (rand.nextFloat() < 0.04F) {
							potiontype = PotionTypes.LEAPING;
						}

						if (potiontype != null) {
							List<Integer> emptySlots = entityWizardInitiate.getEmptySlotsRandomized();
							if (!emptySlots.isEmpty()) {
								entityWizardInitiate.inventory.setInventorySlotContents(emptySlots.get(0), PotionUtils.addPotionToItemStack(new ItemStack(Items.POTIONITEM), potiontype));
								entityWizardInitiate.resetRareEventCooldown(1.5f);
							}
						}
					}
				}
				break;

			case APPAREL_EXPERT:
				if (entityWizardInitiate.ticksExisted % 100 == 0) {
					for (ItemStack stack : entityWizardInitiate.getArmorInventoryList()) {
						// IManaStoringItem is sufficient, since anything in the armour slots is probably armour
						if (stack.getItem() instanceof IManaStoringItem) {
							((IManaStoringItem) stack.getItem()).rechargeMana(stack, 1);
						}
					}
				}
				break;

			case CONDUIT:
				if (entityWizardInitiate.ticksExisted % 260 == 0) {
					if (entityWizardInitiate.getHeldItemMainhand().getItem() instanceof IManaStoringItem) {
						((IManaStoringItem) entityWizardInitiate.getHeldItemMainhand().getItem()).rechargeMana(entityWizardInitiate.getHeldItemMainhand(), 4);
					}
					if (entityWizardInitiate.getOwner() instanceof EntityPlayer && entityWizardInitiate.getOwner().getDistance(entityWizardInitiate) < 12) {
						EntityPlayer player = (EntityPlayer) entityWizardInitiate.getOwner();
						if (player.getHeldItemMainhand().getItem() instanceof ISpellCastingItem && player.getHeldItemMainhand().getItem() instanceof IManaStoringItem && ((IManaStoringItem) player.getHeldItemMainhand().getItem()).getFullness(player.getHeldItemMainhand()) < 0.15f) {
							((IManaStoringItem) player.getHeldItemMainhand().getItem()).rechargeMana(player.getHeldItemMainhand(), 4);
						}
						if (player.getHeldItemOffhand().getItem() instanceof ISpellCastingItem && player.getHeldItemOffhand().getItem() instanceof IManaStoringItem && ((IManaStoringItem) player.getHeldItemOffhand().getItem()).getFullness(player.getHeldItemOffhand()) < 0.15f) {
							((IManaStoringItem) player.getHeldItemOffhand().getItem()).rechargeMana(player.getHeldItemOffhand(), 4);
						}
					}
				}
				break;
			case EMPOWERING_RESONANCE:
				if (entityWizardInitiate.ticksExisted % 100 == 0) {
					entityWizardInitiate.addPotionEffect(new PotionEffect(WizardryPotions.empowerment, 100));
					if (entityWizardInitiate.getHeldItemMainhand().getItem() instanceof ItemWand) {
						Element elm = ((ItemWand) entityWizardInitiate.getHeldItemMainhand().getItem()).element;
						for (EntityLivingBase nearbyMob : EntityUtils.getEntitiesWithinRadius(12, entityWizardInitiate.posX, entityWizardInitiate.posY, entityWizardInitiate.posZ, entityWizardInitiate.world, EntityLivingBase.class)) {
							if (AllyDesignationSystem.isAllied(entityWizardInitiate, nearbyMob) && nearbyMob.getHeldItemMainhand().getItem() instanceof ItemWand && ((ItemWand) nearbyMob.getHeldItemMainhand().getItem()).element == elm) {
								entityWizardInitiate.addPotionEffect(new PotionEffect(WizardryPotions.empowerment, 100));
							}
						}
					}
				}
				break;

			case BLESSED_AURA:                // Check if the ability is ready to be used (reduced by decrementRareEventCooldown)
				if (entityWizardInitiate.getTalentCooldown() == 0) {
					// Try to cure the apprentice
					boolean curedSelf = false;
					boolean removedCurse = false;

					// Check for harmful potion effects on the apprentice
					Collection<PotionEffect> effects = entityWizardInitiate.getActivePotionEffects();
					for (PotionEffect effect : effects) {
						Potion potion = effect.getPotion();
						// Check if it's a harmful effect or a curse
						boolean isCurse = potion.getRegistryName() != null && potion.getRegistryName().toString().contains("curse");
						boolean isNegative = !potion.isBeneficial(); // Use Minecraft's built-in method to detect negative effects

						if (isNegative || isCurse) {
							entityWizardInitiate.removePotionEffect(potion);
							curedSelf = true;
							break; // Only remove one effect at a time
						}
					}

					// Visual and sound effects if cured
					if (curedSelf) {
						entityWizardInitiate.setTalentCooldown(1200);
						if (entityWizardInitiate.world.isRemote) {
							Vec3d origin = entityWizardInitiate.getPositionEyes(1);
							for (int i = 0; i < 20; i++) {
								double x = origin.x - 0.5 + entityWizardInitiate.world.rand.nextDouble();
								double y = origin.y - 0.5 + entityWizardInitiate.world.rand.nextDouble();
								double z = origin.z - 0.5 + entityWizardInitiate.world.rand.nextDouble();
								ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).fade(0, 0, 0).clr(0.5f, 0.5f, 1.0f).spawn(entityWizardInitiate.world);
							}
						}
						return; // Don't try to cure the player in the same tick
					}

					// Try to cure the owner (player) if the apprentice didn't need healing
					if (entityWizardInitiate.getOwner() instanceof EntityPlayer && entityWizardInitiate.getDistance(entityWizardInitiate.getOwner()) < 10) {
						EntityPlayer player = (EntityPlayer) entityWizardInitiate.getOwner();
						boolean curedPlayer = false;
						boolean removedPlayerCurse = false;
						Collection<PotionEffect> playerEffects = player.getActivePotionEffects();
						for (PotionEffect effect : playerEffects) {
							Potion potion = effect.getPotion();
							// Check if it's a harmful effect or a curse
							boolean isCurse = potion.getRegistryName() != null && potion.getRegistryName().toString().contains("curse");
							boolean isNegative = !potion.isBeneficial(); // Use Minecraft's built-in method to detect negative effects

							if (isNegative || isCurse) {
								if (!entityWizardInitiate.world.isRemote) {
									player.removePotionEffect(potion);
								}
								curedPlayer = true;
								if (isCurse) {
									removedPlayerCurse = true;
									entityWizardInitiate.setTalentCooldown(6000);
								}
								break; // Only remove one effect at a time
							}
						}

						// Visual and sound effects if cured
						if (curedPlayer) {
							entityWizardInitiate.setTalentCooldown(2400);
							if (entityWizardInitiate.world.isRemote) {
								// Particles around player
								Vec3d origin = player.getPositionEyes(1);
								for (int i = 0; i < 30; i++) {
									double x = origin.x - 0.5 + entityWizardInitiate.world.rand.nextDouble();
									double y = origin.y - 0.5 + entityWizardInitiate.world.rand.nextDouble();
									double z = origin.z - 0.5 + entityWizardInitiate.world.rand.nextDouble();
									ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(0, 0.1, 0).fade(0, 0, 0).clr(0.5f, 0.5f, 1.0f).spawn(entityWizardInitiate.world);
								}

								// Particles from apprentice to player
								Vec3d apprenticePos = entityWizardInitiate.getPositionEyes(1);
								Vec3d playerPos = player.getPositionEyes(1);
								Vec3d direction = playerPos.subtract(apprenticePos).normalize();

								for (int i = 0; i < 10; i++) {
									double progress = i / 10.0;
									double x = apprenticePos.x + (playerPos.x - apprenticePos.x) * progress;
									double y = apprenticePos.y + (playerPos.y - apprenticePos.y) * progress;
									double z = apprenticePos.z + (playerPos.z - apprenticePos.z) * progress;
									ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z).vel(direction.x * 0.1, direction.y * 0.1 + 0.05, direction.z * 0.1).clr(0.7f, 0.7f, 1.0f).spawn(entityWizardInitiate.world);
								}
							}

							// Play healing sound

							// Say something about the healing
							if (removedPlayerCurse) {
								entityWizardInitiate.sayImmediately(player, new TextComponentTranslation("message.arcaneapprentices:wizard_removed_curse"));
							} else {
								entityWizardInitiate.sayImmediately(player, new TextComponentTranslation("message.arcaneapprentices:wizard_removed_negative_effect"));
							}
						}
					}
				}
				break;
			case BLACKSMITH:
				if (entityWizardInitiate.ticksExisted % 400 == 0) {
					ItemStack offhandItem = entityWizardInitiate.getHeldItemOffhand();

					// Check if there's an item in the offhand that can be repaired
					if (!offhandItem.isEmpty() && offhandItem.isItemDamaged() && offhandItem.getItem().isRepairable()) {
						// Repair by 1 durability point
						int damage = offhandItem.getItemDamage();
						if (damage > 0) {
							// Visual effect when repairing
							if (entityWizardInitiate.world.isRemote) {
								Vec3d pos = entityWizardInitiate.getPositionVector().add(0, 1.0, 0);
								ParticleBuilder.create(ParticleBuilder.Type.SPARK)
										.pos(pos)
										.vel(0, 0.1, 0)
										.clr(1.0F, 0.6F, 0.2F)
										.spawn(entityWizardInitiate.world);

								// Add hammer sound occasionally
								if (rand.nextFloat() < 0.3f) {
									entityWizardInitiate.world.playSound(null, entityWizardInitiate.posX, entityWizardInitiate.posY, entityWizardInitiate.posZ,
											SoundEvents.BLOCK_ANVIL_USE, SoundCategory.NEUTRAL, 0.1F,
											1.0F + rand.nextFloat() * 0.4F);
								}
							}

							// Repair the item by 1 point
							offhandItem.setItemDamage(damage - 1);

							// When fully repaired, make a special effect and message
							if (offhandItem.getItemDamage() == 0 && !entityWizardInitiate.world.isRemote) {
								if (entityWizardInitiate.getOwner() instanceof EntityPlayer) {
									entityWizardInitiate.sayWithoutSpam((EntityPlayer) entityWizardInitiate.getOwner(),
											new TextComponentTranslation("message.arcaneapprentices:apprentice_item_repaired",
													entityWizardInitiate.getDisplayName(), offhandItem.getDisplayName()));
								}

								// Play a sound when fully repaired
								entityWizardInitiate.world.playSound(null, entityWizardInitiate.posX, entityWizardInitiate.posY, entityWizardInitiate.posZ,
										SoundEvents.BLOCK_ANVIL_LAND, SoundCategory.NEUTRAL, 0.5F, 1.2F);
							}
						}
					}
				}
				break;
			case WARDEN_OF_FLAME:
				// Grant permanent fire immunity
				if (!entityWizardInitiate.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
					entityWizardInitiate.addPotionEffect(new PotionEffect(MobEffects.FIRE_RESISTANCE, 400, 0, false, false));
				}

				// Help extinguish the player
				if (!entityWizardInitiate.world.isRemote && entityWizardInitiate.ticksExisted % 25 == 0 && entityWizardInitiate.isTalentReady() && entityWizardInitiate.ticksExisted % 60 == 0 && entityWizardInitiate.getOwner() instanceof EntityPlayer) {
					EntityPlayer player = (EntityPlayer) entityWizardInitiate.getOwner();
					if (player.isBurning() && entityWizardInitiate.getDistance(player) < 10) {
						player.extinguish();
						entityWizardInitiate.setTalentCooldown(600);

						// Visual effects
						if (entityWizardInitiate.world.isRemote) {
							// Create steam/extinguishing particles between apprentice and player
							Vec3d apprenticePos = entityWizardInitiate.getPositionEyes(1);
							Vec3d playerPos = player.getPositionEyes(1);
							Vec3d direction = playerPos.subtract(apprenticePos).normalize();

							for (int i = 0; i < 20; i++) {
								double progress = i / 20.0;
								double x = apprenticePos.x + (playerPos.x - apprenticePos.x) * progress;
								double y = apprenticePos.y + (playerPos.y - apprenticePos.y) * progress;
								double z = apprenticePos.z + (playerPos.z - apprenticePos.z) * progress;

								// Steam particles
								ParticleBuilder.create(ParticleBuilder.Type.CLOUD)
										.pos(x, y, z)
										.vel(direction.x * 0.05 + (rand.nextDouble() - 0.5) * 0.1,
												direction.y * 0.05 + rand.nextDouble() * 0.1,
												direction.z * 0.05 + (rand.nextDouble() - 0.5) * 0.1)
										.clr(0.9F, 0.9F, 0.9F)
										.time(20 + rand.nextInt(10))
										.spawn(entityWizardInitiate.world);
							}

							// Particles around the player
							for (int i = 0; i < 30; i++) {
								double x = player.posX - 1 + rand.nextDouble() * 2;
								double y = player.posY + rand.nextDouble() * 2;
								double z = player.posZ - 1 + rand.nextDouble() * 2;

								entityWizardInitiate.world.spawnParticle(EnumParticleTypes.WATER_SPLASH,
										x, y, z,
										0, 0.1, 0);
							}
						}

						// Sound effect
						entityWizardInitiate.world.playSound(null, player.posX, player.posY, player.posZ,
								SoundEvents.ENTITY_GENERIC_EXTINGUISH_FIRE, SoundCategory.NEUTRAL,
								1.0F, 1.0F);

						// Say something
						entityWizardInitiate.sayWithoutSpam(player, new TextComponentTranslation(
								"message.arcaneapprentices:warden_flame_extinguish"));

					}
				}
				break;

			case WARDEN_OF_FROST:
				// Immune to frost effects
				if (!entityWizardInitiate.world.isRemote && entityWizardInitiate.ticksExisted % 25 == 0 && entityWizardInitiate.isPotionActive(WizardryPotions.frost)) {
					entityWizardInitiate.removePotionEffect(WizardryPotions.frost);
				}

				// Help remove frost from the player
				if (entityWizardInitiate.isTalentReady() && entityWizardInitiate.getOwner() instanceof EntityPlayer) {

					EntityPlayer player = (EntityPlayer) entityWizardInitiate.getOwner();
					boolean hasFrostEffect = player.isPotionActive(WizardryPotions.frost);

					// Look for any frost-related potion effects
					if (hasFrostEffect && entityWizardInitiate.getDistance(player) < 10) {
						if (!entityWizardInitiate.world.isRemote) {
							player.removePotionEffect(WizardryPotions.frost);
						}
						entityWizardInitiate.setTalentCooldown(600);

						// Add a brief speed boost as warmth effect
						if (!entityWizardInitiate.world.isRemote) {
							player.addPotionEffect(new PotionEffect(MobEffects.SPEED, 20, 0));
						}

						// Visual effects
						if (entityWizardInitiate.world.isRemote) {
							// Create warming particles between apprentice and player
							Vec3d apprenticePos = entityWizardInitiate.getPositionEyes(1);
							Vec3d playerPos = player.getPositionEyes(1);

							for (int i = 0; i < 30; i++) {
								double progress = i / 30.0;
								double x = apprenticePos.x + (playerPos.x - apprenticePos.x) * progress;
								double y = apprenticePos.y + (playerPos.y - apprenticePos.y) * progress;
								double z = apprenticePos.z + (playerPos.z - apprenticePos.z) * progress;

								// Warm orange/red particles
								ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
										.pos(x, y, z)
										.vel(0, 0.05 + rand.nextDouble() * 0.05, 0)
										.clr(1.0F, 0.6F + rand.nextFloat() * 0.3F, 0.2F)
										.time(20 + rand.nextInt(10))
										.spawn(entityWizardInitiate.world);
							}

							// Particles around the player
							for (int i = 0; i < 30; i++) {
								double x = player.posX - 1 + rand.nextDouble() * 2;
								double y = player.posY + rand.nextDouble() * 2;
								double z = player.posZ - 1 + rand.nextDouble() * 2;

								ParticleBuilder.create(ParticleBuilder.Type.SPARKLE)
										.pos(x, y, z)
										.vel((rand.nextDouble() - 0.5) * 0.1,
												rand.nextDouble() * 0.1,
												(rand.nextDouble() - 0.5) * 0.1)
										.clr(1.0F, 0.7F, 0.3F)
										.spawn(entityWizardInitiate.world);
							}
						}

						// Sound effect
						entityWizardInitiate.world.playSound(null, player.posX, player.posY, player.posZ,
								SoundEvents.BLOCK_FIRE_AMBIENT, SoundCategory.NEUTRAL,
								1.0F, 1.0F);

						// Say something
						entityWizardInitiate.sayImmediately(player, new TextComponentTranslation(
								"message.arcaneapprentices:warden_frost_warm"));

						// Set cooldown
					}
				}
				break;

			case MERCHANT:
				// Every 2 Minecraft days (24000 ticks), offer a trade to the player
				if (!entityWizardInitiate.world.isRemote && entityWizardInitiate.ticksExisted % 2400 == 0) {
					entityWizardInitiate.setTrades(new WildcardTradeList());
					entityWizardInitiate.addRandomRecipes(1);
				}
				break;
		}
	}
}
