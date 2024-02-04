package com.windanesz.arcaneapprentices.entity.living;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.Settings;
import com.windanesz.arcaneapprentices.Utils;
import com.windanesz.arcaneapprentices.client.gui.AAGuiHandler;
import com.windanesz.arcaneapprentices.data.JourneyType;
import com.windanesz.arcaneapprentices.data.PlayerData;
import com.windanesz.arcaneapprentices.data.Speech;
import com.windanesz.arcaneapprentices.entity.MessageEntry;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIAttackMelee;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIAttackRangedBow;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIAttackSpellWithCost;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIFollowOwner;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIGoHome;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIIdentify;
import com.windanesz.arcaneapprentices.entity.ai.WizardAILookAround;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIOwnerHurtByTarget;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIOwnerHurtTarget;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIPanicAtLowHP;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIStudy;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIWander;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIWatchClosest;
import com.windanesz.arcaneapprentices.entity.ai.WizardAIWatchClosest2;
import com.windanesz.arcaneapprentices.handler.EventHandler;
import com.windanesz.arcaneapprentices.handler.XpProgression;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardInitiateInventory;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardInventory;
import com.windanesz.arcaneapprentices.items.ItemArtefactWithSlots;
import com.windanesz.arcaneapprentices.registry.AAAdvancementTriggers;
import com.windanesz.arcaneapprentices.registry.AAItems;
import com.windanesz.arcaneapprentices.registry.LootRegistry;
import com.windanesz.wizardryutils.tools.WizardryUtilsTools;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.constants.Tier;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.item.ItemWand;
import electroblob.wizardry.misc.WildcardTradeList;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.BlockUtils;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.Location;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import electroblob.wizardry.util.WandHelper;
import io.netty.buffer.ByteBuf;
import net.minecraft.advancements.Advancement;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.block.BlockBed;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.INpc;
import net.minecraft.entity.IRangedAttackMob;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.entity.projectile.EntityPotion;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArrow;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootTable;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class EntityWizardInitiate extends EntityCreature
		implements INpc, ISpellCaster, IEntityAdditionalSpawnData, IInventoryChangedListener, IEntityOwnable, IRangedAttackMob {

	public static final float RARE_EVENT_CHANCE = 0.05f;
	public static final int OFF_HAND_SLOT = 1;
	public static final int ARTEFACT_SLOT = 22;
	/**
	 * The increase in progression for casting spells of the matching element.
	 */
	private static final float ELEMENTAL_PROGRESSION_MODIFIER = 1.2f;
	private static final float DISCOVERY_PROGRESSION_MODIFIER = 5f;
	/**
	 * The increase in progression for tiers that the player has already reached.
	 */
	private static final float SECOND_TIME_PROGRESSION_MODIFIER = 1.5f;
	/**
	 * The fraction of progression lost when all recently-cast spells are the same as the one being cast.
	 */
	private static final float MAX_PROGRESSION_REDUCTION = 0.75f;
	private static final DataParameter<Integer> HEAL_COOLDOWN = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ELEMENT = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<String> CONTINUOUS_SPELL = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.STRING);
	private static final DataParameter<String> CURRENT_SPELL = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.STRING);
	private static final DataParameter<Integer> SPELL_COUNTER = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> CURRENT_TASK = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> PREVIOUS_TASK_BEFORE_SLEEPING = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> XP = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> TIME_TILL_NEXT_SCHEDULED_MESSAGE = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Float> FOOD_LEVEL = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> FOOD_SATURATION = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> STUDY_PROGRESS = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.BOOLEAN);
	private static final DataParameter<Boolean> IS_SLEEPING = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.BOOLEAN);
	private static final DataParameter<NBTTagCompound> KNOWN_SPELLS = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<NBTTagCompound> DISABLED_SPELLS = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<Optional<UUID>> OWNER_UNIQUE_ID = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.OPTIONAL_UNIQUE_ID);
	private static final DataParameter<NBTTagCompound> SCHEDULED_MESSAGES = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.COMPOUND_TAG);
	private static final DataParameter<BlockPos> BED_POSITION = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.BLOCK_POS);
	private static final DataParameter<Boolean> SWINGING_ARMS = EntityDataManager.<Boolean>createKey(EntityWizardInitiate.class, DataSerializers.BOOLEAN);
	private static final int MAINHAND = 0;
	public int textureIndex = 0;
	public int adventureRemainingDuration = -1;
	public ContainerWizardInventory inventory;
	public BlockPos currentStayPos = new BlockPos(0, 0, 0);
	protected Predicate<Entity> targetSelector;
	List<MessageEntry> scheduledMessages = new ArrayList<>();
	private JourneyType journeyType = JourneyType.NOT_ADVENTURING;
	private WizardAIAttackSpellWithCost spellCastingAI = new WizardAIAttackSpellWithCost(this, 0.5, 14.0F, 30, 50, false);
	private WizardAIAttackMelee meleeAI = new WizardAIAttackMelee(this, 0.5D, false);
	private WizardAIAttackRangedBow<EntityWizardInitiate> bowAI = new WizardAIAttackRangedBow<>(this, 0.5D, 25, 15.0F);
	private MerchantRecipeList trades;
	private BlockPos lectern;
	private boolean isEating = false;
	// This variable is used when foodLevel either exceeds 17 or is at zero. Increases in each tick up to 80, then it either heals or deals a half heart damage (starving) then resets to 0
	private int foodTickTimer = 0;
	private Location home = new Location(BlockPos.ORIGIN, 0);
	/**
	 * The fraction of progression lost when all recently-cast spells are the same as the one being cast.
	 */
	@Nullable
	private EntityPlayer customer;
	private int timeUntilReset;
	private boolean updateRecipes;
	// FORGE
	private net.minecraftforge.items.IItemHandler itemHandler = null;
	private Set<BlockPos> towerBlocks;
	private float wizardWidth = -1.0F;
	private float wizardHeight;
	private int chatCooldown = 0;
	private int rareEventCooldown = 0;
	private int rareEventMaxCooldown = 6000;

	public EntityWizardInitiate(World world) {
		super(world);
		this.tasks.addTask(3, this.spellCastingAI);
		this.setSize(0.6F, 1.95F);
		initInventory();
	}

	protected void entityInit() {
		super.entityInit();
		this.dataManager.register(HEAL_COOLDOWN, -1);
		this.dataManager.register(ELEMENT, 0);
		this.dataManager.register(CONTINUOUS_SPELL, "ebwizardry:none");
		this.dataManager.register(CURRENT_SPELL, "ebwizardry:none");
		this.dataManager.register(SPELL_COUNTER, 0);
		this.dataManager.register(LEVEL, 1);
		this.dataManager.register(CURRENT_TASK, 0);
		this.dataManager.register(PREVIOUS_TASK_BEFORE_SLEEPING, 0);
		this.dataManager.register(XP, 0);
		this.dataManager.register(TIME_TILL_NEXT_SCHEDULED_MESSAGE, 0);
		this.dataManager.register(FOOD_LEVEL, 20f);
		this.dataManager.register(FOOD_SATURATION, 5f);
		this.dataManager.register(IS_CHILD, false);
		this.dataManager.register(BED_POSITION, BlockPos.ORIGIN);
		this.dataManager.register(IS_SLEEPING, false);
		this.dataManager.register(STUDY_PROGRESS, 0f);
		this.dataManager.register(KNOWN_SPELLS, new NBTTagCompound());
		this.dataManager.register(DISABLED_SPELLS, new NBTTagCompound());
		this.dataManager.register(SCHEDULED_MESSAGES, new NBTTagCompound());
		this.dataManager.register(OWNER_UNIQUE_ID, Optional.absent());
		this.dataManager.register(SWINGING_ARMS, Boolean.valueOf(false));
	}

	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new WizardAIPanicAtLowHP(this, 1.15D));
		this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(5, new WizardAIFollowOwner(this, 0.70D, 10.0F, 2.0F));
		this.tasks.addTask(5, new WizardAIGoHome(this, 0.70D, 20));
		this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
		this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.6));
		this.tasks.addTask(6, new WizardAIStudy(this, 10, 1));
		this.tasks.addTask(6, new WizardAIIdentify(this, 10, 1));
		//		this.tasks.addTask(6, new EntityAIWatchClosestLectern(this,  3));
		this.tasks.addTask(7, new WizardAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(7, new WizardAIWander(this, 0.4, 10));
		this.tasks.addTask(8, new WizardAIWatchClosest(this, EntityLiving.class, 8.0F));
		this.tasks.addTask(7, new WizardAILookAround(this, 8.0F, 0.1f));
		this.targetTasks.addTask(1, new WizardAIOwnerHurtByTarget(this));
		this.targetTasks.addTask(2, new WizardAIOwnerHurtTarget(this));
		this.targetSelector = (entity) -> {
			return entity != null && !entity.isInvisible() && entity != getOwner() && AllyDesignationSystem.isValidTarget(this, entity) && (entity instanceof IMob || entity instanceof ISummonedCreature || Arrays.asList(Wizardry.settings.summonedCreatureTargetsWhitelist).contains(EntityList.getKey(entity.getClass()))) && !Arrays.asList(Wizardry.settings.summonedCreatureTargetsBlacklist).contains(EntityList.getKey(entity.getClass()));
		};
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, false, new Class[0]));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, this.targetSelector));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getAttributeMap().registerAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).setBaseValue(1.0D);
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
	}

	// This is now public because artefacts use it
	public static SpellModifiers calculateModifiers(ItemStack stack, EntityWizardInitiate npc, Spell spell) {

		SpellModifiers modifiers = new SpellModifiers();

		// Now we only need to add multipliers if they are not 1.
		int level = WandHelper.getUpgradeLevel(stack, WizardryItems.range_upgrade);
		if (level > 0) {modifiers.set(WizardryItems.range_upgrade, 1.0f + level * electroblob.wizardry.constants.Constants.RANGE_INCREASE_PER_LEVEL, true);}

		level = WandHelper.getUpgradeLevel(stack, WizardryItems.duration_upgrade);
		if (level > 0) {
			modifiers.set(WizardryItems.duration_upgrade, 1.0f + level * electroblob.wizardry.constants.Constants.DURATION_INCREASE_PER_LEVEL, false);
		}

		level = WandHelper.getUpgradeLevel(stack, WizardryItems.blast_upgrade);
		if (level > 0) {
			modifiers.set(WizardryItems.blast_upgrade, 1.0f + level * electroblob.wizardry.constants.Constants.BLAST_RADIUS_INCREASE_PER_LEVEL, true);
		}

		level = WandHelper.getUpgradeLevel(stack, WizardryItems.cooldown_upgrade);
		if (level > 0) {
			modifiers.set(WizardryItems.cooldown_upgrade, 1.0f - level * electroblob.wizardry.constants.Constants.COOLDOWN_REDUCTION_PER_LEVEL, true);
		}

		float progressionModifier = 1.0f - MAX_PROGRESSION_REDUCTION;

		if (stack.getItem() instanceof ItemWand) {
			if (((ItemWand) stack.getItem()).element == spell.getElement()) {
				modifiers.set(SpellModifiers.POTENCY, 1.0f + (((ItemWand) stack.getItem()).tier.level + 1) * electroblob.wizardry.constants.Constants.POTENCY_INCREASE_PER_TIER, true);
				progressionModifier *= ELEMENTAL_PROGRESSION_MODIFIER;
			}
		}

		modifiers.set(SpellModifiers.PROGRESSION, progressionModifier, false);

		return modifiers;
	}

	public static void mergeItemStacks(IInventory inventory) {
		int slots = inventory.getSizeInventory();
		boolean merged;

		do {
			merged = false;
			for (int i = 0; i < slots - 1; i++) {
				ItemStack currentStack = inventory.getStackInSlot(i);

				if (!currentStack.isEmpty() && currentStack.getCount() < currentStack.getMaxStackSize()) {
					for (int j = i + 1; j < slots; j++) {
						ItemStack targetStack = inventory.getStackInSlot(j);

						if (areStacksCompatible(currentStack, targetStack)) {
							int spaceInStack = currentStack.getMaxStackSize() - currentStack.getCount();
							int transferAmount = Math.min(spaceInStack, targetStack.getCount());
							currentStack.grow(transferAmount);
							targetStack.shrink(transferAmount);

							if (targetStack.isEmpty()) {
								inventory.setInventorySlotContents(j, ItemStack.EMPTY);
							}

							merged = true;

							if (currentStack.getCount() >= currentStack.getMaxStackSize()) {
								break;
							}
						}
					}
				}
			}
		} while (merged);
	}

	private static boolean areStacksCompatible(ItemStack stack1, ItemStack stack2) {
		return ItemStack.areItemsEqual(stack1, stack2) && ItemStack.areItemStackTagsEqual(stack1, stack2);
	}

	public JourneyType getJourneyType() {
		return journeyType;
	}

	public void setJourneyType(JourneyType journeyType) {
		this.journeyType = journeyType;
	}

	public boolean verifyWandManaRequirementForJourney(JourneyType journeyType) {
		if (this.getHeldItemMainhand().getItem() instanceof ItemWand) {
			ItemWand wand = (ItemWand) this.getHeldItemMainhand().getItem();
			float manaPercent = (float) wand.getMana(this.getHeldItemMainhand()) / (float) wand.getManaCapacity(this.getHeldItemMainhand());
			return manaPercent > 0.5f;
		}

		return false;
	}

	public void goOnJourney() {

		if (!this.world.isRemote && getJourneyType() != JourneyType.NOT_ADVENTURING && getOwner() instanceof EntityPlayer) {
			if (this.getHome().pos == BlockPos.ORIGIN) {
				setHome(new Location(new BlockPos(this.posX, this.posY, this.posZ), this.dimension));
			}

			Speech.WIZARD_GOING_ON_JOURNEY.say(this);
			consumeFoodForJourney();
			consumeManaForJourney();
			adventureRemainingDuration = journeyType.getRandomAdventureDuration(this);
			if (PlayerData.storeAdventuringApprentice((EntityPlayer) this.getOwner(), this)) {
				world.removeEntity(this);
				AAAdvancementTriggers.apprentice_go_on_journey.triggerFor((EntityPlayer) getOwner());
			}
		}
	}

	public void consumeFoodForJourney() {
		if (!Settings.journeySettings.JOURNEY_REQUIRE_FOOD) {
			return;
		}
		String duration = getJourneyType().getDuration();

		float cost;
		if (duration.equals("SHORT")) {
			cost = 30;
		} else if (duration.equals("MEDIUM")) {
			cost = 90;
		} else {
			cost = 200;
		}
		cost *= Settings.journeySettings.JOURNEY_FOOD_REQUIREMENT_MODIFIER;
		for (int i = 1; i < inventory.getSizeInventory(); i++) {
			ItemStack stack = inventory.getStackInSlot(i);
			if (cost > 0 && stack.getItem() instanceof ItemFood && stack.getItem() != Items.ROTTEN_FLESH) {
				float healAmount = ((ItemFood) stack.getItem()).getHealAmount(stack);
				int count = stack.getCount();
				for (int j = 0; j < count; j++) {
					stack.shrink(1);
					cost -= healAmount;
					if (cost < 0) {break;}
				}
			}
		}
	}

	public void consumeManaForJourney() {
		String duration = getJourneyType().getDuration();

		float cost;
		if (duration.equals("SHORT")) {
			cost = 0.2f;
		} else if (duration.equals("MEDIUM")) {
			cost = 0.5f;
		} else {
			cost = 0.7f;
		}

		cost += rand.nextBoolean() ? -(float) rand.nextInt(3) / 10 : (float) rand.nextInt(3) / 10;

		if (this.getHeldItemMainhand().getItem() instanceof ItemWand) {
			ItemWand wand = (ItemWand) this.getHeldItemMainhand().getItem();
			float v = (float) (Math.sqrt(wand.getManaCapacity(this.getHeldItemMainhand())) * 30 * cost);
			wand.setMana(this.getHeldItemMainhand(), (int) Math.max(0, wand.getMana(this.getHeldItemMainhand()) - v));
		}
	}

	public Location getHome() {
		return home;
	}

	public void setHome(Location home) {
		this.home = home;
	}

	private void resetTimeTillNextScheduledMessage() {
		dataManager.set(TIME_TILL_NEXT_SCHEDULED_MESSAGE, 0);
	}

	private int getTimeTillNextScheduledMessage() {
		return dataManager.get(TIME_TILL_NEXT_SCHEDULED_MESSAGE);
	}

	private void setTimeTillNextScheduledMessage(int timer) {
		dataManager.set(TIME_TILL_NEXT_SCHEDULED_MESSAGE, timer);
	}

	public boolean hasScheduledMessage() {
		return !scheduledMessages.isEmpty();
	}

	public int getChatCooldown() {
		return chatCooldown;
	}

	public void resetChatCooldown() {
		this.chatCooldown = 200 + world.rand.nextInt(100);
	}

	public void decrementChatCooldown() {
		if (this.chatCooldown > 0) {
			this.chatCooldown--;
		}
	}

	public void decrementScheduledMessageTimer() {
		if (!scheduledMessages.isEmpty()) {
			MessageEntry nextMessage = scheduledMessages.get(0);
			nextMessage.decrementDelay();
		}
	}

	public void decrementRareEventCooldown() {
		if (this.rareEventCooldown > 0) {
			this.rareEventCooldown--;
		}
	}

	public void scheduleNextMessage() {
		MessageEntry entry = MessageEntry.peekNextMessage(MessageEntry.deserializeMessages(getScheduledMessagesCompound()));
		if (entry != null) {
			setTimeTillNextScheduledMessage(entry.getDelay());
		}
	}

	private void sayNextScheduledMessage() {
		if (!scheduledMessages.isEmpty()) {
			MessageEntry entry = scheduledMessages.get(0);
			if (entry != null) {
				if (entry.getDelay() > 0) {
					entry.decrementDelay();
				} else {
					sayImmediately(new TextComponentTranslation(entry.getMessage()));
					scheduledMessages.remove(0);
				}
			}
		}
	}

	public void sayWithoutSpam(String message) {
		if (chatCooldown == 0) {
			WizardryUtilsTools.sendMessage(this.getOwner(), "message.arcaneapprentices:wizard_chat_message", false, getChatPrefix(), message);
			resetChatCooldown();
		}
	}

	public void sayWithoutSpam(TextComponentTranslation message) {
		if (this.getOwner() != null) {
			sayWithoutSpam((EntityPlayer) this.getOwner(), message);
		}
	}

	public void sayWithoutSpam(EntityPlayer player, TextComponentTranslation message) {
		if (chatCooldown == 0 && player != null && this.getDistance(player) < 20) {
			WizardryUtilsTools.sendMessage(player, "message.arcaneapprentices:wizard_chat_message", false, getChatPrefix(), message);
			this.faceEntity(player, 30.0F, 30.0F);
			resetChatCooldown();
		}
	}

	public void sayImmediately(TextComponentTranslation message) {
		if (this.getOwner() != null) {
			sayImmediately((EntityPlayer) this.getOwner(), message);
		}
	}

	public void sayImmediately(EntityPlayer player, TextComponentTranslation message) {
		if (player != null && this.getDistance(player) < 20) {
			this.faceEntity(player, 30.0F, 30.0F);
			WizardryUtilsTools.sendMessage(player, "message.arcaneapprentices:wizard_chat_message", false, getChatPrefix(), message);
			resetChatCooldown();
		}
	}

	public int getRareEventCooldown() {
		return rareEventCooldown;
	}

	public void setRareEventCooldown(int rareEventCooldown) {
		this.rareEventCooldown = rareEventCooldown;
	}

	public boolean rareEventReady() {
		return getRareEventCooldown() == 0;
	}

	public void resetRareEventCooldown() {
		this.rareEventCooldown = rareEventMaxCooldown;
	}

	public void resetRareEventCooldown(float totalPercent) {
		this.rareEventCooldown = (int) (rareEventMaxCooldown * totalPercent);
	}

	protected final void setSize(float width, float height) {
		boolean flag = this.wizardWidth > 0.0F && this.wizardHeight > 0.0F;
		this.wizardWidth = width;
		this.wizardHeight = height;

		if (!flag) {
			this.multiplySize(1.0F);
		}
	}

	protected final void multiplySize(float size) {
		super.setSize(this.wizardWidth * size, this.wizardHeight * size);
	}

	public double getYOffset() {
		return this.isChild() ? 0.0D : -0.45D;
	}

	public boolean isChild() {
		return ((float) this.getLevel() / XpProgression.getMaxLevel()) <= 0.4f;
		//return this.getDataManager().get(IS_CHILD).booleanValue();
	}

	public void setChild(boolean isChild) {
		this.getDataManager().set(IS_CHILD, Boolean.valueOf(isChild));
		this.setChildSize(isChild);
	}

	public boolean isLyingInBed() {
		return this.getDataManager().get(IS_SLEEPING);
	}

	public void setLyingInBed(boolean sleep) {
		this.getDataManager().set(IS_SLEEPING, sleep);
	}

	public float getStudyProgress() {
		return this.getDataManager().get(STUDY_PROGRESS).floatValue();
	}

	public float getFoodLevel() {
		return this.getDataManager().get(FOOD_LEVEL);
	}

	public void setFoodLevel(float foodLevel) {
		// always maximum 20
		this.getDataManager().set(FOOD_LEVEL, Math.min(20f, Math.max(0, foodLevel)));
	}

	public void modifyFoodLevel(float amount) {
		float newAmount = getFoodLevel() + amount;
		this.setFoodLevel(newAmount);
	}

	public float getSaturation() {
		return this.getDataManager().get(FOOD_SATURATION);
	}

	public void setSaturation(float amount) {
		// maximum the same as the current food level
		this.getDataManager().set(FOOD_SATURATION, Math.min(getFoodLevel(), Math.max(0, amount)));
	}

	public void modifySaturation(float amount) {
		float newAmount = getSaturation() + amount;
		this.setSaturation(newAmount);
	}

	public BlockPos getBedPos() {
		return this.getDataManager().get(BED_POSITION);
	}

	public void setBedPos(BlockPos pos) {
		this.getDataManager().set(BED_POSITION, pos);
	}

	public void addStudyProgress(float amount) {
		float oldAmount = getStudyProgress();
		float newAmount = Math.min(1.0f, oldAmount + amount);
		this.getDataManager().set(STUDY_PROGRESS, newAmount);
	}

	public void resetStudyProgress() {
		this.getDataManager().set(STUDY_PROGRESS, 0f);
	}

	public boolean isStudyComplete() {
		return getStudyProgress() == 1.0f;
	}

	public NBTTagCompound getSpellCompound() {
		return this.dataManager.get(KNOWN_SPELLS);
	}

	protected void setSpellCompound(NBTTagCompound tag) {
		this.dataManager.set(KNOWN_SPELLS, tag);
	}

	public void removeAllKnownSpells() {
		this.dataManager.set(KNOWN_SPELLS, new NBTTagCompound());
		this.dataManager.set(DISABLED_SPELLS, new NBTTagCompound());
	}

	private NBTTagCompound getDisabledSpellCompound() {
		return this.dataManager.get(DISABLED_SPELLS);
	}

	protected void setDisabledSpellCompound(NBTTagCompound tag) {
		this.dataManager.set(DISABLED_SPELLS, tag);
	}

	public NBTTagCompound getScheduledMessagesCompound() {
		return this.dataManager.get(SCHEDULED_MESSAGES);
	}

	private void setScheduledMessagesCompound(NBTTagCompound nbt) {
		this.dataManager.set(SCHEDULED_MESSAGES, nbt);
	}

	public void scheduleMessage(MessageEntry messageEntry) {
		this.scheduledMessages.add(messageEntry);
	}

	public float getEyeHeight() {
		float f = 1.74F;

		if (this.isChild()) {
			f = (float) ((double) f - 0.81D);
		}

		return f;
	}

	public void notifyDataManagerChange(DataParameter<?> key) {
		if (IS_CHILD.equals(key)) {
			this.setChildSize(this.isChild());
		}

		super.notifyDataManagerChange(key);
	}

	public void setChildSize(boolean isChild) {
		this.multiplySize(isChild ? 0.5F : 1.0F);
	}

	private int getHealCooldown() {
		return (Integer) this.dataManager.get(HEAL_COOLDOWN);
	}

	private void setHealCooldown(int cooldown) {
		this.dataManager.set(HEAL_COOLDOWN, cooldown);
	}

	public Element getElement() {
		return Element.values()[(Integer) this.dataManager.get(ELEMENT)];
	}

	public void setElement(Element element) {
		this.dataManager.set(ELEMENT, element.ordinal());
	}

	public List<Spell> getSpells() {
		return (List<Spell>) NBTExtras.NBTToList(getSpellCompound().getTagList("spells", Constants.NBT.TAG_INT), (NBTTagInt tag) -> Spell.byMetadata(tag.getInt()));
	}

	public List<Spell> getDisabledSpells() {
		return (List<Spell>) NBTExtras.NBTToList(getDisabledSpellCompound().getTagList("spells", Constants.NBT.TAG_INT), (NBTTagInt tag) -> Spell.byMetadata(tag.getInt()));
	}

	public SpellModifiers getModifiers() {
		return calculateModifiers(this.getHeldItemMainhand(), this, getCurrentSpell());
	}

	public SpellModifiers getModifiers(Spell spell) {
		return calculateModifiers(this.getHeldItemMainhand(), this, spell);
	}

	public Spell getContinuousSpell() {
		return Spell.get((String) this.dataManager.get(CONTINUOUS_SPELL));
	}

	public void setContinuousSpell(Spell spell) {
		this.dataManager.set(CONTINUOUS_SPELL, spell.getRegistryName().toString());
	}

	public boolean setCurrentSpell(Spell spell) {
		this.dataManager.set(CURRENT_SPELL, spell.getRegistryName().toString());
		return true;
	}

	public Spell getCurrentSpell() {
		return Spell.get((String) this.dataManager.get(CURRENT_SPELL));
	}

	public int getSpellCounter() {
		return (Integer) this.dataManager.get(SPELL_COUNTER);
	}

	public void setSpellCounter(int count) {
		this.dataManager.set(SPELL_COUNTER, count);
	}

	public int getAimingError(EnumDifficulty difficulty) {
		switch (difficulty) {
			case EASY:
				return 7;
			case NORMAL:
				return 4;
			case HARD:
				return 1;
			default:
				return 7;
		}
	}

	public BlockPos getPos() {
		return new BlockPos(this);
	}

	protected boolean canDespawn() {
		return false;
	}

	protected SoundEvent getAmbientSound() {
		return WizardrySounds.ENTITY_WIZARD_AMBIENT;
	}

	protected SoundEvent getHurtSound(DamageSource source) {
		return WizardrySounds.ENTITY_WIZARD_HURT;
	}

	protected SoundEvent getDeathSound() {
		return WizardrySounds.ENTITY_WIZARD_DEATH;
	}

	public void onLivingUpdate() {
		super.onLivingUpdate();
		decrementChatCooldown();
		decrementRareEventCooldown();
		EventHandler.tickArtefacts(this);

		if (!world.isRemote) {
			sayNextScheduledMessage();
		}

		foodTickTimer++;

		if (isPotionActive(MobEffects.HUNGER)) {
			modifySaturation(-0.005F * (float) (getActivePotionEffect(MobEffects.HUNGER).getAmplifier() + 1));
		} else if (isPotionActive(MobEffects.SATURATION)) {
			modifyFoodLevel(0.005F * (float) (getActivePotionEffect(MobEffects.SATURATION).getAmplifier() + 1));
		}

		if (this.getOwner() != null && foodTickTimer >= 80) {
			if (shouldHeal()) {
				this.heal(0.5f);
				modifySaturation(-0.8f);
			} else if (getFoodLevel() == 0) {
				if (getHealth() > 0.5f) {
					this.attackEntityFrom(DamageSource.STARVE, 0.5f);
					Speech.WIZARD_STARVING.sayWithoutSpam(this);
				}
			}
			foodTickTimer = 0;
		}

		if (!world.isRemote && ticksExisted % 180 == 0 && !isEating && this.getOwner() != null) {
			modifySaturation(-0.1f);
			if (getSaturation() == 0) {
				modifyFoodLevel(-0.1f);
			}

			if (this.getAttackTarget() == null && getFoodLevel() / 20 < 0.85f) {
				// starting from inventory index 1 to skip mainhand
				for (int i = 1; i < this.inventory.getSizeInventory(); i++) {
					ItemStack stack = this.inventory.getStackInSlot(i).copy();
					if (stack.getItem() instanceof ItemFood) {
						ItemStack oldHeldItem = getHeldItemMainhand().copy();
						// 7 is first inventory slot, 0 is mainhand
						ItemStack oldFirstItem = inventory.getStackInSlot(7).copy();
						// first slot item goes to i (food slot)...
						this.inventory.setInventorySlotContents(i, oldFirstItem);
						// then the food goes to the mainhand
						inventory.setInventorySlotContents(MAINHAND, stack);
						// then the old held item goes to the first slot
						inventory.setInventorySlotContents(7, oldHeldItem);
						isEating = true;
						break;
					}
				}
			}
		}

		if (this.isEating) {
			if (this.getHeldItemMainhand().getItem() instanceof ItemFood) {
				this.setActiveHand(EnumHand.MAIN_HAND);
			} else if (!this.isHandActive()) {
				isEating = false;
			}
		}

		if (this.rand.nextFloat() < 0.1F) {
			List<Potion> potionsToWishFor = new ArrayList<>();
			if (this.isBurning() && !this.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
				potionsToWishFor.add(MobEffects.FIRE_RESISTANCE);
			} else if (this.getHealth() / this.getMaxHealth() < 0.5f) {
				potionsToWishFor.add(MobEffects.INSTANT_HEALTH);
				if (!isPotionActive(MobEffects.REGENERATION)) {
					potionsToWishFor.add(MobEffects.REGENERATION);
				}
				if (!isPotionActive(MobEffects.INVISIBILITY)) {
					potionsToWishFor.add(MobEffects.INVISIBILITY);
				}
			}
			if (potionsToWishFor.isEmpty()) {return;}

			for (int i = 1; i < this.inventory.getSizeInventory(); i++) {
				ItemStack stack = this.inventory.getStackInSlot(i).copy();
				if (stack.getItem() instanceof ItemPotion && PotionUtils.getEffectsFromStack(stack).stream().map(PotionEffect::getPotion).anyMatch(potionsToWishFor::contains)) {
					if (stack.getItem() == Items.SPLASH_POTION) {
						EntityPotion entitypotion = new EntityPotion(this.world, this, stack);
						entitypotion.setPosition(this.posX, this.posY + getEyeHeight(), this.posZ);
						this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_SPLASH_POTION_THROW, this.getSoundCategory(), 1.0F, 0.8F + this.rand.nextFloat() * 0.4F);
						this.setHeldItem(EnumHand.MAIN_HAND, ItemStack.EMPTY);
						this.world.spawnEntity(entitypotion);
						this.inventory.getStackInSlot(i).shrink(1);
						return;
					}
					ItemStack oldHeldItem = getHeldItemMainhand().copy();
					// 7 is first inventory slot, 0 is mainhand
					ItemStack oldFirstItem = inventory.getStackInSlot(7).copy();
					// first slot item goes to i (food slot)...
					this.inventory.setInventorySlotContents(i, oldFirstItem);
					// then the food goes to the mainhand
					inventory.setInventorySlotContents(MAINHAND, stack);
					// then the old held item goes to the first slot
					inventory.setInventorySlotContents(7, oldHeldItem);
					this.setActiveHand(EnumHand.MAIN_HAND);
					break;
				}
			}
		}

		// sleeping
		if ((this.getTask() == Task.STAY || this.getTask() == Task.STUDY || this.getTask() == Task.IDENTIFY) && !this.world.isDaytime()) {
			BlockPos bedPos = findBed();
			if (bedPos != null) {
				this.getNavigator().tryMoveToXYZ(bedPos.getX() + 0.5f, bedPos.getY(), bedPos.getZ() + 0.5, 0.5f);
				this.setPreviousTaskBeforeSleeping(this.getTask());
				this.setTask(Task.TRY_TO_SLEEP);
				setBedPos(bedPos);
			}
		} else if (this.getTask() == Task.TRY_TO_SLEEP && this.ticksExisted % 23 == 0) {
			if (this.world.isDaytime()) {
				this.setTask(getPreviousTaskBeforeSleeping());
				setLyingInBed(false);
			} else {

				if (!(world.getBlockState(getBedPos()).getBlock() instanceof BlockBed)) {
					BlockPos bedPos = findBed();
					if (bedPos != null) {setBedPos(bedPos);}
				}

				if (!this.getPos().equals(getBedPos())) {
					this.getNavigator().tryMoveToXYZ(getBedPos().getX() + 0.5f, getBedPos().getY(), getBedPos().getZ() + 0.5, 0.5f);
					setLyingInBed(false);
				} else {
					setLyingInBed(this.getPos().equals(getBedPos()));
				}
			}
		}

		if (this.getTask() == Task.FOLLOW && this.ticksExisted % 100 == 0) {
			this.addExperience(1);
		}

		if (!world.isRemote && isLyingInBed() && this.ticksExisted % 100 == 0 && getMaxHealth() > getHealth()) {
			this.heal(0.5f);
		}
	}

	public BlockPos findBed() {
		for (BlockPos pos : BlockUtils.getBlockSphere(this.getPosition(), 15)) {
			IBlockState state = world.getBlockState(pos);
			if (state.getBlock() instanceof BlockBed) {
				if (state.getValue(BlockBed.PART) != BlockBed.EnumPartType.FOOT) {
					continue;
				}
				if (!state.getValue(BlockBed.OCCUPIED)) { // occupied check
					return pos;
				}
			}
		}

		return null;
	}

	@Override
	protected void onItemUseFinish() {
		boolean mainHand = ItemStack.areItemStacksEqual(this.activeItemStack, getHeldItemMainhand());
		if (!this.activeItemStack.isEmpty() && this.isHandActive() && this.activeItemStack.getItem() instanceof ItemFood) {
			ItemFood food = (ItemFood) this.getHeldItemMainhand().getItem();
			float foodHealAmount = food.getHealAmount(this.getHeldItemMainhand());
			float saturation = food.getSaturationModifier(this.getHeldItemMainhand());
			modifyFoodLevel(foodHealAmount);
			modifySaturation(saturation * 4);
			this.isEating = false;
		}

		super.onItemUseFinish();
		if (mainHand && getHeldItemMainhand().isEmpty()) {
			inventory.setInventorySlotContents(MAINHAND, inventory.getStackInSlot(7));
		} else if (mainHand) {
			ItemStack backup = this.getHeldItemMainhand().copy();
			inventory.setInventorySlotContents(MAINHAND, inventory.getStackInSlot(7));
			inventory.setInventorySlotContents(7, backup);
		}

	}

	public boolean hasOwner() {
		return getOwnerId() != null;
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand) {

		if (player == this.getOwner()) {
			if (!player.getHeldItemMainhand().isEmpty() && player.getHeldItemMainhand().getItem().getRegistryName().toString().equals("ancientspellcraft:amnesia_scroll")) {
				if (world.isRemote) {
					Vec3d origin = this.getPositionEyes(1);
					for (int i = 0; i < 30; i++) {
						double x = origin.x - 1 + world.rand.nextDouble() * 2;
						double y = origin.y - 0.25 + world.rand.nextDouble() * 0.5;
						double z = origin.z - 1 + world.rand.nextDouble() * 2;
						if (world.rand.nextBoolean()) {
							ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z)
									.vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f)
									.clr(140, 140, 140).spawn(world);
						} else {
							ParticleBuilder.create(ParticleBuilder.Type.SPARKLE).pos(x, y, z)
									.vel(0, 0.1, 0).fade(0, 0, 0).spin(0.3f, 0.03f)
									.clr(99, 1, 110).spawn(world);
						}
					}
				} else {
					this.removeAllKnownSpells();
					player.getHeldItemMainhand().shrink(1);
				}
			}

		}
		if (player.isCreative() && player.isSneaking() && !player.world.isRemote && hand == EnumHand.MAIN_HAND && player.getHeldItemMainhand().getItem() == Items.NETHER_STAR) {
			this.addExperience(80);
			this.setOwner(player);
			return false;
		}

		//		if (player.isCreative() && player.isSneaking() && !player.world.isRemote && hand == EnumHand.MAIN_HAND && player.getHeldItemMainhand().getItem() == WizardryItems.ring_condensing) {
		//			this.addExperience(80000);
		//			this.setOwner(player);
		//			return false;
		//		}

		if (!player.world.isRemote && !hasOwner() && player.getHeldItemMainhand().getItem() == WizardryItems.wizard_handbook) {
			Advancement requirement1 = ((WorldServer) world).getAdvancementManager().getAdvancement(new ResourceLocation("ebwizardry:master"));
			Advancement requirement2 = ((WorldServer) world).getAdvancementManager().getAdvancement(new ResourceLocation("ebwizardry:discover_master_spell"));
			PlayerAdvancements advancements = ((EntityPlayerMP) player).getAdvancements();
			if (requirement1 != null && !advancements.getProgress(requirement1).isDone() || requirement2 != null && !advancements.getProgress(requirement2).isDone()) {
				sayImmediately(player, new TextComponentTranslation(Speech.PLAYER_GIVES_HANDBOOK_WITHOUT_REQUIREMENTS.getRandom(), player.getDisplayName()));
				AAAdvancementTriggers.no_requirements_met.triggerFor(player);
			} else if (PlayerData.addApprenticeForPlayer(player, this) || PlayerData.getApprentices(player).stream().anyMatch(a -> a.equals(this.getUniqueID()))) {
				sayImmediately(player, new TextComponentTranslation(Speech.PLAYER_GIVES_HANDBOOK.getRandom(), player.getDisplayName()));
				this.setOwner(player);
				this.setHome(new Location(this.getPos(), this.dimension));
				WizardryUtilsTools.sendMessage(player, "message.arcaneapprentices:apprentice_taken", false, this.getName());
				setTask(Task.FOLLOW);
				//	AAAdvancementTriggers.take_apprentice.triggerFor(player);
				return true;
			} else {
				// reached apprentice cap
				Utils.sendMessage(player, "info.arcaneapprentices:reached_apprentice_cap", false, this.getName());
				return false;
			}

			player.getHeldItemMainhand().shrink(1);
		}

		if (player.isSneaking() && !player.world.isRemote && hand == EnumHand.MAIN_HAND && player == this.getOwner()) {
			if (world.rand.nextFloat() < 0.15) {
				sayImmediately(new TextComponentTranslation(Speech.GREET.getRandom(), player.getDisplayName()));
			}
			player.openGui(ArcaneApprentices.MODID, AAGuiHandler.WIZARD_INVENTORY_GUI, this.world, this.getEntityId(), 0, 0);
		} else {
			if (!hasOwner()) {
				sayWithoutSpam(player, new TextComponentTranslation(Speech.GREET_HANDBOOK_HINT.getRandom(), player.getDisplayName()));
			} else {
				sayWithoutSpam(player, new TextComponentTranslation(Speech.GREET.getRandom(), player.getDisplayName()));
			}
		}

		return true;
	}

	@Override
	public void onDeath(DamageSource cause) {
		if (!world.isRemote && (Settings.generalSettings.APPRENTICES_CAN_BE_RESURRECTED || Settings.generalSettings.APPRENTICES_RESPAWN_AT_PLAYER_SPAWNPOINT) && this.hasOwner()) {
			if (this.getOwner() instanceof EntityPlayer) {
				this.heal(10);
				PlayerData.storeDeadApprentice((EntityPlayer) this.getOwner(), this);
			}
			if (!this.world.isRemote && this.world.getGameRules().getBoolean("showDeathMessages") && this.getOwner() instanceof EntityPlayerMP) {
				this.getOwner().sendMessage(this.getCombatTracker().getDeathMessage());
			}
		}
		super.onDeath(cause);
	}

	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (this.trades != null) {
			NBTExtras.storeTagSafely(nbt, "trades", this.trades.getRecipiesAsTags());
		}

		if (getOwnerId() != null) {
			//noinspection DataFlowIssue
			nbt.setUniqueId("OwnerUUID", getOwnerId());
		}
		Element element = this.getElement();
		nbt.setInteger("element", element == null ? 0 : element.ordinal());
		nbt.setInteger("skin", this.textureIndex);

		if (this.towerBlocks != null && this.towerBlocks.size() > 0) {
			NBTExtras.storeTagSafely(nbt, "towerBlocks", NBTExtras.listToNBT(this.towerBlocks, NBTUtil::createPosTag));
		}

		nbt.setBoolean("IsChild", isChild());
		nbt.setInteger("Task", getTask().ordinal());
		nbt.setInteger("PreviousTaskBeforeSleeping", getPreviousTaskBeforeSleeping().ordinal());
		NBTTagList nbttaglist = new NBTTagList();

		for (int i = 0; i < this.inventory.getSizeInventory(); ++i) {
			ItemStack itemstack = this.inventory.getStackInSlot(i);

			if (!itemstack.isEmpty()) {
				NBTTagCompound nbttagcompound = new NBTTagCompound();
				nbttagcompound.setByte("Slot", (byte) i);
				itemstack.writeToNBT(nbttagcompound);
				nbttaglist.appendTag(nbttagcompound);
			}
		}
		nbt.setTag("Spells", getSpellCompound());
		nbt.setTag("DisabledSpells", getDisabledSpellCompound());
		nbt.setTag("Items", nbttaglist);
		nbt.setInteger("Xp", getTotalXp());
		nbt.setInteger("Level", getLevel());
		nbt.setFloat("FoodLevel", getFoodLevel());
		nbt.setFloat("FoodSaturation", getSaturation());
		nbt.setTag("CurrentStayPos", NBTUtil.createPosTag(currentStayPos));
		nbt.setTag("HomePos", getHome().toNBT());
		nbt.setInteger("RareEventCooldown", getRareEventCooldown());
		nbt.setLong("BedPos", getBedPos().toLong());
		nbt.setInteger("AdventureRemainingDuration", adventureRemainingDuration);
		nbt.setString("JourneyType", journeyType.toString());
	}

	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("trades")) {
			NBTTagCompound nbttagcompound1 = nbt.getCompoundTag("trades");
			this.trades = new WildcardTradeList(nbttagcompound1);
		}
		if (nbt.hasUniqueId("OwnerUUID")) {
			this.setOwnerId(nbt.getUniqueId("OwnerUUID"));
		}
		this.setElement(Element.values()[nbt.getInteger("element")]);
		this.currentStayPos = NBTUtil.getPosFromTag(nbt.getCompoundTag("CurrentStayPos"));
		this.textureIndex = nbt.getInteger("skin");
		if (nbt.hasKey("Task")) {
			this.setTask(Task.values()[nbt.getInteger("Task")]);
		}
		if (nbt.hasKey("PreviousTaskBeforeSleeping")) {
			this.setTask(Task.values()[nbt.getInteger("PreviousTaskBeforeSleeping")]);
		}
		this.setSpellCompound(nbt.getCompoundTag("Spells"));
		this.setDisabledSpellCompound(nbt.getCompoundTag("DisabledSpells"));
		this.setChild(nbt.getBoolean("IsChild"));

		NBTTagList nbttaglist = nbt.getTagList("Items", 10);
		this.initInventory();
		for (int i = 0; i < nbttaglist.tagCount(); ++i) {
			NBTTagCompound nbttagcompound = nbttaglist.getCompoundTagAt(i);
			int j = nbttagcompound.getByte("Slot") & 255;

			if (j < this.inventory.getSizeInventory()) {
				this.inventory.setInventorySlotContents(j, new ItemStack(nbttagcompound));
			}
		}
		this.setLevel(nbt.getInteger("Level"));
		this.setXp(nbt.getInteger("Xp"));
		this.setFoodLevel(nbt.getInteger("FoodLevel"));
		this.setSaturation(nbt.getFloat("FoodSaturation"));
		this.adventureRemainingDuration = nbt.getInteger("AdventureRemainingDuration");

		if (nbt.hasKey("JourneyType")) {
			this.journeyType = JourneyType.valueOf(nbt.getString("JourneyType"));
		}

		this.setRareEventCooldown(nbt.getInteger("RareEventCooldown"));
		if (nbt.hasKey("HomePos")) {
			this.setHome(Location.fromNBT((NBTTagCompound) nbt.getTag("HomePos")));
		}
		nbt.setTag("CurrentStayPos", NBTUtil.createPosTag(currentStayPos));
		if (nbt.hasKey("BedPos")) {
			setBedPos(BlockPos.fromLong(nbt.getLong("BedPos")));
		}
	}

	protected int getInventorySize() {
		return 23;
	}

	protected void initInventory() {
		ContainerWizardInventory wizardInventory = this.inventory;
		this.inventory = new ContainerWizardInventory("WizardInventory", false, this.getInventorySize());
		this.inventory.setCustomName(this.getName());

		if (wizardInventory != null) {
			wizardInventory.removeInventoryChangeListener(this);
			int i = Math.min(wizardInventory.getSizeInventory(), this.inventory.getSizeInventory());

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = wizardInventory.getStackInSlot(j);

				if (!itemstack.isEmpty()) {
					this.inventory.setInventorySlotContents(j, itemstack.copy());
				}
			}
		}

		this.inventory.addInventoryChangeListener(this);
		this.itemHandler = new net.minecraftforge.items.wrapper.InvWrapper(this.inventory);
	}

	public boolean consumeArcaneTome(Tier tier) {
		int ordinal = Math.max(1, tier.ordinal());
		for (int i = 0; i < this.inventory.getSizeInventory(); i++) {
			ItemStack stack = this.inventory.getStackInSlot(i);
			if (stack.getItem() == WizardryItems.arcane_tome && stack.getMetadata() == ordinal) {
				this.inventory.decrStackSize(i, 1);
				return true;
			}
		}
		return false;
	}

	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag(Utils.generateWizardName(world));
		this.textureIndex = this.rand.nextInt(2);
		if (this.rand.nextBoolean()) {
			this.setElement(Element.values()[this.rand.nextInt(Element.values().length - 1) + 1]);
		} else {
			this.setElement(Element.MAGIC);
		}

		setSaturation(5f);
		setFoodLevel(20f);
		//setLevel(1);
		Element element = this.getElement();
		EntityEquipmentSlot[] var4 = InventoryUtils.ARMOUR_SLOTS;
		int var5 = var4.length;

		int var6;
		EntityEquipmentSlot slot;
		for (var6 = 0; var6 < var5; ++var6) {
			slot = var4[var6];
			//this.setItemStackToSlot(slot, new ItemStack(WizardryItems.getArmour(element, slot)));
		}

		var4 = EntityEquipmentSlot.values();
		var5 = var4.length;

		//		for (var6 = 0; var6 < var5; ++var6) {
		//			slot = var4[var6];
		//			this.setDropChance(slot, 0.0F);
		//		}

		//this.spells.add(Spells.magic_missile);
		//		Tier maxTier = populateSpells(this, this.spells, element, false, 3, this.rand);
		//		ItemStack wand = new ItemStack(WizardryItems.getWand(maxTier, element));
		//		ArrayList<Spell> list = new ArrayList(this.spells);
		//		list.add(Spells.heal);
		//		WandHelper.setSpells(wand, (Spell[])list.toArray(new Spell[5]));
		//		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, wand);
		this.setHealCooldown(50);
		this.setHome(new Location(this.getPos(), this.dimension));
		return livingdata;
	}

	public void writeSpawnData(ByteBuf data) {
		data.writeInt(this.textureIndex);
		data.writeInt(getOwner() != null ? getOwner().getEntityId() : -1);
	}

	public void readSpawnData(ByteBuf data) {
		this.textureIndex = data.readInt();
		int id = data.readInt();
		// We're on the client side here, so we can safely use Minecraft.getMinecraft().world via proxies.
		if (id > -1) {
			Entity entity = Wizardry.proxy.getTheWorld().getEntityByID(id);
			if (entity instanceof EntityLivingBase) {setOwner((EntityLivingBase) entity);} else {
				Wizardry.logger.warn("Received a spawn packet for entity {}, but no living entity matched the supplied ID", this);
			}
		}
	}

	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (rand.nextInt(5) == 0) {
			if (source.getTrueSource() instanceof EntityPlayer) {
				if (((EntityPlayer) source.getTrueSource()).getHeldItemMainhand().getItem().getRegistryName().toString().contains("sword")) {
					Speech.WIZARD_TAKE_DAMAGE_BY_SWORD.say(this);
				} else {
					sayImmediately(new TextComponentTranslation(Speech.WIZARD_TAKE_DAMAGE_FROM_PLAYER.getRandom(), source.getTrueSource().getDisplayName()));
				}
			} else {
				Speech.WIZARD_TAKE_DAMAGE.say(this);
			}
		}

		return super.attackEntityFrom(source, damage);
	}

	//	@Override
	public void onInventoryChanged(IInventory inventory) {
		ItemStack oldItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		ItemStack newItem = this.inventory.getStackInSlot(0);
		if (ItemStack.areItemStacksEqual(oldItem, newItem)) {
			this.resetStudyProgress();
		}

		//		setHeldItem(EnumHand.MAIN_HAND, inventory.getStackInSlot(0));
		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, inventory.getStackInSlot(0));
		this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, inventory.getStackInSlot(1));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, inventory.getStackInSlot(2));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, inventory.getStackInSlot(3));
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, inventory.getStackInSlot(4));
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, inventory.getStackInSlot(5));

		if (inventory.getStackInSlot(0).getItem() instanceof ItemSword) {
			this.tasks.removeTask(spellCastingAI);
			this.tasks.removeTask(bowAI);
			this.tasks.addTask(3, meleeAI);
		} else if (inventory.getStackInSlot(0).getItem() instanceof ItemBow) {
			this.tasks.removeTask(meleeAI);
			this.tasks.removeTask(spellCastingAI);
			this.tasks.addTask(3, bowAI);
		} else {
			this.tasks.removeTask(meleeAI);
			this.tasks.removeTask(bowAI);
			this.tasks.addTask(3, spellCastingAI);
		}
	}

	@SideOnly(Side.CLIENT)
	public float getBedRotation() {
		BlockPos bedLocation = getBedPos();
		IBlockState state = bedLocation == null ? null : world.getBlockState(bedLocation);
		if (state != null && state.getBlock().isBed(state, world, bedLocation, null)) {
			EnumFacing direction = state.getBlock().getBedDirection(state, world, bedLocation);
			switch (direction) {
				case EAST:
					return 180;
				case WEST:
					return 0;
				case NORTH:
					return 270;
				default:
					return 90;
			}
		}
		return -1F;
	}

	@Override
	public void setHeldItem(EnumHand hand, ItemStack stack) {
		if (hand == EnumHand.MAIN_HAND) {
			this.inventory.setInventorySlotContents(0, stack);
			//this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, stack);
		} else {
			if (hand != EnumHand.OFF_HAND) {
				throw new IllegalArgumentException("Invalid hand " + hand);
			}

			this.inventory.setInventorySlotContents(1, stack);
		}
	}

	public int getInventoryColumns() {return 5;}

	@SuppressWarnings("unchecked")
	@Override
	@Nullable
	public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing) {
		if (capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {return (T) itemHandler;}
		return super.getCapability(capability, facing);
	}

	@Override
	public boolean hasCapability(net.minecraftforge.common.capabilities.Capability<?> capability, @Nullable net.minecraft.util.EnumFacing facing) {
		return capability == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
	}

	@Nullable
	public UUID getOwnerId() {
		return this.dataManager.get(OWNER_UNIQUE_ID).orNull();
	}

	public void setOwnerId(@Nullable UUID uniqueId) {
		this.dataManager.set(OWNER_UNIQUE_ID, Optional.fromNullable(uniqueId));
	}

	@Nullable
	@Override
	public Entity getOwner() {
		Entity entity = EntityUtils.getEntityByUUID(this.world, getOwnerId());
		if (entity != null && !(entity instanceof EntityLivingBase)) { // Should never happen
			ArcaneApprentices.logger.warn("{} has a non-living owner!", this);
			return null;
		}

		return entity;
	}

	public void setOwner(@Nullable EntityLivingBase owner) {
		setOwnerId(owner == null ? null : owner.getUniqueID());
	}

	public boolean isStudying() {
		if (getHeldItemOffhand().getItem() instanceof ItemSpellBook && !isSpellKnown(Spell.byMetadata(getHeldItemOffhand().getItemDamage()))) {
			return true;
		}
		return false;
	}

	public boolean isSpellKnown(Spell spell) {
		return getSpells().contains(spell);
	}

	public boolean isSpellDisabled(Spell spell) {
		return getDisabledSpells().contains(spell);
	}

	public int getCurrentSpellSlotCap() {
		return Math.max(1, Math.round(Settings.generalSettings.MAX_WIZARD_SPELL_SLOTS * ((float) getLevel() / XpProgression.getMaxLevel())));
	}

	public int getCurrentTierCap() {
		float levellingProgress = (float) getLevel() / XpProgression.getMaxLevel();
		if (levellingProgress <= 0.25f) {
			return 0;
		} else if (levellingProgress <= 0.5f) {
			return Math.min(Settings.generalSettings.MAX_WIZARD_SPELL_TIER, 1);
		} else if (levellingProgress <= 0.75f) {
			return Math.min(Settings.generalSettings.MAX_WIZARD_SPELL_TIER, 2);
		} else {
			return Math.min(Settings.generalSettings.MAX_WIZARD_SPELL_TIER, 3);
		}
	}

	public boolean canLearnNewSpell() {
		return getSpells().size() < getCurrentSpellSlotCap();
	}

	public void learnSpell(Spell spell) {
		if (!isSpellKnown(spell)) {
			if (getOwner() instanceof EntityPlayer) {
				AAAdvancementTriggers.apprentice_learn_spell.triggerFor((EntityPlayer) getOwner());
			}
			List<Spell> spells = getSpells();
			spells.add(spell);
			NBTTagCompound newSpellNbt = new NBTTagCompound();
			NBTExtras.storeTagSafely(newSpellNbt, "spells", NBTExtras.listToNBT(spells, s -> new NBTTagInt(s.metadata())));
			setSpellCompound(newSpellNbt);
			this.resetStudyProgress();
		}
		if (getSpells().size() == Settings.generalSettings.MAX_WIZARD_SPELL_SLOTS) {
			if (getOwner() instanceof EntityPlayer) {
				AAAdvancementTriggers.apprentice_learn_max_spell.triggerFor((EntityPlayer) getOwner());
			}
		}
	}

	/**
	 * Toggles the disablement status of a given spell for the wizard entity.
	 * <p>
	 * If the spell is currently disabled, it will be enabled, and vice versa.
	 *
	 * @param spell The spell to toggle disablement for.
	 * @return True if the spell is now disabled after the toggle; false if it is now enabled.
	 */
	public boolean toggleSpellDisablement(Spell spell) {
		List<Spell> alreadyDisabledSpells = getDisabledSpells();
		boolean spellIsNowDisabled;
		if (alreadyDisabledSpells.contains(spell)) {
			alreadyDisabledSpells.remove(spell);
			spellIsNowDisabled = false;
		} else {
			alreadyDisabledSpells.add(spell);
			spellIsNowDisabled = true;
		}
		NBTTagCompound newSpellNbt = new NBTTagCompound();
		NBTExtras.storeTagSafely(newSpellNbt, "spells", NBTExtras.listToNBT(alreadyDisabledSpells, s -> new NBTTagInt(s.metadata())));
		setDisabledSpellCompound(newSpellNbt);
		return spellIsNowDisabled;
	}

	public BlockPos getLectern() {
		return lectern;
	}

	public void setLectern(BlockPos lectern) {
		this.lectern = lectern;
	}

	/**
	 * Calculates the study progress for a given Spell object based on its Tier and a constant NPC_SPELL_STUDY_TIME_MODIFIER.
	 * <p>
	 * The study progress is calculated using the formula:
	 * progress = 30 / (Math.pow((spellTier / 2 + 5), NPC_SPELL_STUDY_TIME_MODIFIER))
	 *
	 * @param spell The Spell object for which to calculate the study progress.
	 * @return The study progress as a float value.
	 */
	public float getStudyProgressForSpell(Spell spell, int tickFrequency) {
		// Calculate half the ordinal value of the Spell's Tier as a floating-point number
		double halfOrdinal = (double) spell.getTier().ordinal() / 2f;

		// Add 5 to the calculated half ordinal value to bump it up a bit
		double valuePlusFive = halfOrdinal + 5f;

		// Calculate the study progress using the simplified formula
		return (tickFrequency / (float) Math.pow(valuePlusFive, Settings.generalSettings.NPC_SPELL_STUDY_TIME_MODIFIER)) * (isArtefactActive(AAItems.head_knowledge) ? 1.33f : 1f);
	}

	public Task getTask() {
		return Task.values()[this.dataManager.get(CURRENT_TASK)];
	}

	public void setTask(Task task) {
		if (task == Task.STAY) {
			currentStayPos = getPos();
		}
		this.dataManager.set(CURRENT_TASK, task.ordinal());
	}

	public Task getPreviousTaskBeforeSleeping() {
		return Task.values()[this.dataManager.get(PREVIOUS_TASK_BEFORE_SLEEPING)];
	}

	public void setPreviousTaskBeforeSleeping(Task task) {
		this.dataManager.set(PREVIOUS_TASK_BEFORE_SLEEPING, task.ordinal());
	}

	public int getLevel() {
		return this.dataManager.get(LEVEL);
	}

	public void setLevel(int level) {
		this.dataManager.set(LEVEL, level);
		double currentMaxHP = this.getMaxHealth();
		double newMaxHP = Math.min(Settings.generalSettings.WIZARD_MAX_HEALTH_CAP, Settings.generalSettings.WIZARD_HP_GAIN_PER_LEVEL * level + Settings.generalSettings.WIZARD_MINIMUM_HP);
		float healAmount = (float) (newMaxHP - currentMaxHP);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(newMaxHP);
		// also grant the newly gained health points
		heal(healAmount);
	}

	public int getTotalXp() {
		return this.dataManager.get(XP);
	}

	public void setXp(int amount) {
		this.dataManager.set(XP, amount);
	}

	public void addExperience(int amount) {
		int newAmount = getTotalXp() + amount;
		int xpForNewLevel = (int) XpProgression.calculateTotalXpRequired(getLevel() + 1);

		this.dataManager.set(XP, newAmount);
		if (newAmount >= xpForNewLevel && getLevel() < XpProgression.getMaxLevel()) {
			// level up
			if (getOwner() instanceof EntityPlayer) {
				AAAdvancementTriggers.apprentice_levels_up.triggerFor((EntityPlayer) getOwner());
			}
			if (getLevel() + 1 == XpProgression.getMaxLevel()) {
				if (getOwner() instanceof EntityPlayer) {
					AAAdvancementTriggers.apprentice_max_level.triggerFor((EntityPlayer) getOwner());
				}
			}
			setLevel(getLevel() + 1);
			sayImmediately(new TextComponentTranslation(Speech.LEVEL_UP.getRandom()));
			this.world.playSound(null, this.posX, this.posY, this.posZ, SoundEvents.ENTITY_PLAYER_LEVELUP, this.getSoundCategory(), 0.75F, 1.0F);
		}
	}

	@Override
	public ITextComponent getDisplayName() {
		if (getOwnerId() != null && getOwner() != null) {
			return new TextComponentTranslation("entity.arcaneapprentices:owned_wizard.nameplate", getOwner().getName(), this.hasCustomName() ? super.getDisplayName() : this.getElement().getWizardName());
		} else {
			return super.getDisplayName();
		}
	}

	public ITextComponent getDisplayNameWithoutOwner() {
		return super.getDisplayName();
	}

	public ITextComponent getChatPrefix() {
		return new TextComponentTranslation("message.arcaneapprentices:wizard_chat_prefix", this.getCustomName());
	}

	public String getCustomName() {
		return this.getCustomNameTag();
	}

	public List<ItemStack> getActiveArtefacts() {
		List<ItemStack> list = new ArrayList<>();
		list.add(inventory.getStackInSlot(ARTEFACT_SLOT));
		list.add(inventory.getStackInSlot(OFF_HAND_SLOT));
		return list;
	}

	public List<ItemStack> getHeldItems() {
		List<ItemStack> list = new ArrayList<>();
		list.add(inventory.getStackInSlot(MAINHAND));
		list.add(inventory.getStackInSlot(OFF_HAND_SLOT));
		return list;
	}

	public boolean isArtefactActive(Item artefact) {
		return getActiveArtefacts().stream().anyMatch(s -> s.getItem() == artefact);
	}

	@Override
	public Iterable<ItemStack> getArmorInventoryList() {
		NonNullList<ItemStack> inventoryArmor = NonNullList.<ItemStack>withSize(4, ItemStack.EMPTY);
		inventoryArmor.set(0, inventory.getStackInSlot(2));
		inventoryArmor.set(0, inventory.getStackInSlot(3));
		inventoryArmor.set(0, inventory.getStackInSlot(4));
		inventoryArmor.set(0, inventory.getStackInSlot(5));
		return inventoryArmor;
	}

	public boolean shouldHeal() {
		return getFoodLevel() / 20 > 0.75f && this.getHealth() < this.getMaxHealth();
	}

	public void returnFromAdventuring() {
		if (journeyType != JourneyType.NOT_ADVENTURING) {
			fillWithLoot(LootRegistry.getLootTableFor(journeyType), journeyType.getBonusLootItemCount());
		}

		Speech.WIZARD_RETURNED_FROM_JOURNEY.say(this);
		if (getOwner() instanceof EntityPlayer) {
			AAAdvancementTriggers.apprentice_returns_from_journey.triggerFor((EntityPlayer) getOwner());
		}
		this.getNavigator().tryMoveToEntityLiving(this.getOwner(), 0.7);
	}

	public void fillWithLoot(ResourceLocation lootTable, int extraItemCount) {
		if (this.getOwner() instanceof EntityPlayer) {
			EntityPlayer player = (EntityPlayer) this.getOwner();
			if (lootTable != null) {
				LootTable loottable = this.world.getLootTableManager().getLootTableFromLocation(lootTable);
				Random random;

				random = new Random();

				LootContext.Builder lootcontext$builder = new LootContext.Builder((WorldServer) this.world);
				lootcontext$builder.withLuck(player.getLuck()).withPlayer(player); // Forge: add player to LootContext
				ContainerWizardInventory temporaryInventory = new ContainerWizardInventory("WizardInventory", false, 64);
				loottable.fillInventory(temporaryInventory, random, lootcontext$builder.build());
				mergeItemStacks(temporaryInventory);
				List<ItemStack> itemStackList = new ArrayList<>();

				// TODO: artefact that adds more stuff
				if (isArtefactActive(AAItems.ring_serendipity)) {
					extraItemCount += Math.max(3, rand.nextInt(6));
				}
				if (extraItemCount > 0) {
					LootTable extraItems = this.world.getLootTableManager().getLootTableFromLocation(LootRegistry.STRUCTURES);
					LootContext.Builder lootcontext$builder2 = new LootContext.Builder((WorldServer) this.world);
					lootcontext$builder2.withLuck(player.getLuck()).withPlayer(player); // Forge: add player to LootContext
					ContainerWizardInventory fewItemContainer = new ContainerWizardInventory("WizardInventory", false, 64);
					extraItems.fillInventory(fewItemContainer, random, lootcontext$builder2.build());
					mergeItemStacks(fewItemContainer);

					List<ItemStack> itemStackList2 = new ArrayList<>();
					for (int t = 0; t < fewItemContainer.getSizeInventory(); t++) {
						if (!fewItemContainer.getStackInSlot(t).isEmpty()) {
							itemStackList2.add(fewItemContainer.getStackInSlot(t).copy());
						}
					}
					Collections.shuffle(itemStackList2);
					for (int i = 0; i < extraItemCount; i++) {
						if (i < itemStackList2.size()) {
							temporaryInventory.addItem(itemStackList2.get(i).copy());
						}
					}
				}

				mergeItemStacks(temporaryInventory);

				for (int t = 0; t < temporaryInventory.getSizeInventory(); t++) {
					if (!temporaryInventory.getStackInSlot(t).isEmpty()) {
						itemStackList.add(temporaryInventory.getStackInSlot(t).copy());
					}
				}

				Iterator<ItemStack> listIterator = itemStackList.iterator();

				while (listIterator.hasNext()) {
					ItemStack stack = listIterator.next();
					boolean flag = true;
					// main inventory
					for (int i = 6; i < 23; i++) {
						if (!ContainerWizardInitiateInventory.isSlotUnlocked(i, this)) {
							continue;
						}

						if (this.inventory.getStackInSlot(i).isEmpty()) {
							this.inventory.setInventorySlotContents(i, stack);
							flag = false;
							listIterator.remove();
							break;
						} else if (areStacksCompatible(this.inventory.getStackInSlot(i), stack)) {
							int available = 64 - this.inventory.getStackInSlot(i).getCount();
							if (stack.getCount() <= available) {
								// merge full stack
								ItemStack newStack = this.inventory.getStackInSlot(i).copy();
								newStack.setCount(this.inventory.getStackInSlot(i).getCount() + stack.getCount());
								this.inventory.setInventorySlotContents(i, newStack);
								flag = false;
								listIterator.remove();
								break;
							} else {
								//partial merge
								ItemStack newStack = this.inventory.getStackInSlot(i).copy();
								int remainder = stack.getCount() - available;
								newStack.setCount(64);
								this.inventory.setInventorySlotContents(i, newStack);
								stack.setCount(remainder);
							}
						}
					}

					if (flag && isArtefactActive(AAItems.charm_bag_9)) {
						ItemStack bag = inventory.getStackInSlot(ARTEFACT_SLOT).copy();
						for (int i = 0; i < 9; i++) {
							if (ItemArtefactWithSlots.isSlotEmpty(bag, i)) {
								ItemArtefactWithSlots.setItemForSlot(bag, stack, i);
								flag = false;
								listIterator.remove();
								break;
							}
						}
						this.inventory.setInventorySlotContents(ARTEFACT_SLOT, bag);
					} else if (flag && isArtefactActive(AAItems.charm_bag_27)) {
						ItemStack bag = inventory.getStackInSlot(ARTEFACT_SLOT).copy();
						for (int i = 0; i < 27; i++) {
							if (ItemArtefactWithSlots.isSlotEmpty(bag, i)) {
								ItemArtefactWithSlots.setItemForSlot(bag, stack, i);
								flag = false;
								listIterator.remove();
								break;
							}
						}
						this.inventory.setInventorySlotContents(ARTEFACT_SLOT, bag);
					}

					if (flag) {
						// discarding the item
						listIterator.remove();
					}
				}
			}

		}
	}

	private List<Integer> getEmptySlotsRandomized() {
		List<Integer> list = Lists.<Integer>newArrayList();

		for (int i = 0; i < inventory.getSizeInventory(); ++i) {
			if (inventory.getStackInSlot(i).isEmpty()) {
				list.add(Integer.valueOf(i));
			}
		}

		Collections.shuffle(list, rand);
		return list;
	}

	public boolean attackEntityAsMob(Entity entityIn) {
		float f = (float) this.getEntityAttribute(SharedMonsterAttributes.ATTACK_DAMAGE).getAttributeValue();
		int i = 0;

		if (entityIn instanceof EntityLivingBase) {
			f += EnchantmentHelper.getModifierForCreature(this.getHeldItemMainhand(), ((EntityLivingBase) entityIn).getCreatureAttribute());
			i += EnchantmentHelper.getKnockbackModifier(this);
		}

		boolean flag = entityIn.attackEntityFrom(DamageSource.causeMobDamage(this), f);

		if (flag) {
			if (i > 0 && entityIn instanceof EntityLivingBase) {
				((EntityLivingBase) entityIn).knockBack(this, (float) i * 0.5F, (double) MathHelper.sin(this.rotationYaw * 0.017453292F), (double) (-MathHelper.cos(this.rotationYaw * 0.017453292F)));
				this.motionX *= 0.6D;
				this.motionZ *= 0.6D;
			}

			int j = EnchantmentHelper.getFireAspectModifier(this);

			if (j > 0) {
				entityIn.setFire(j * 4);
			}

			if (entityIn instanceof EntityPlayer) {
				EntityPlayer entityplayer = (EntityPlayer) entityIn;
				ItemStack itemstack = this.getHeldItemMainhand();
				ItemStack itemstack1 = entityplayer.isHandActive() ? entityplayer.getActiveItemStack() : ItemStack.EMPTY;

				if (!itemstack.isEmpty() && !itemstack1.isEmpty() && itemstack.getItem().canDisableShield(itemstack, itemstack1, entityplayer, this) && itemstack1.getItem().isShield(itemstack1, entityplayer)) {
					float f1 = 0.25F + (float) EnchantmentHelper.getEfficiencyModifier(this) * 0.05F;

					if (this.rand.nextFloat() < f1) {
						entityplayer.getCooldownTracker().setCooldown(itemstack1.getItem(), 100);
						this.world.setEntityState(entityplayer, (byte) 30);
					}
				}
			}

			this.applyEnchantments(this, entityIn);
		}

		return flag;
	}

	public void attackEntityWithRangedAttack(EntityLivingBase target, float distanceFactor) {
		EntityArrow entityarrow = this.getArrow(distanceFactor);
		if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow) {
			if (entityarrow == null && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, this.getHeldItemMainhand()) > 0) {
				ItemStack arrow = new ItemStack(Items.ARROW);
				entityarrow = ((ItemArrow) arrow.getItem()).createArrow(world, arrow, this);
			}
		}

		if (entityarrow != null) {

			if (this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow) {
				entityarrow = ((net.minecraft.item.ItemBow) this.getHeldItemMainhand().getItem()).customizeArrow(entityarrow);
			}
			double d0 = target.posX - this.posX;
			double d1 = target.getEntityBoundingBox().minY + (double) (target.height / 3.0F) - entityarrow.posY;
			double d2 = target.posZ - this.posZ;
			double d3 = (double) MathHelper.sqrt(d0 * d0 + d2 * d2);
			entityarrow.shoot(d0, d1 + d3 * 0.20000000298023224D, d2, 1.6F, (float) (14 - this.world.getDifficulty().getId() * 4));
			this.playSound(SoundEvents.ENTITY_SKELETON_SHOOT, 1.0F, 1.0F / (this.getRNG().nextFloat() * 0.4F + 0.8F));
			this.world.spawnEntity(entityarrow);
		} else {
			sayWithoutSpam(new TextComponentTranslation(Speech.WIZARD_NO_ARROWS.getRandom()));
		}
	}

	@Override
	public void setSwingingArms(boolean swingingArms) {
		this.dataManager.set(SWINGING_ARMS, Boolean.valueOf(swingingArms));
	}

	protected EntityArrow getArrow(float distanceFactor) {
		for (int i = 1; i < this.inventory.getSizeInventory(); i++) {
			if (this.inventory.getStackInSlot(i).getItem() instanceof ItemArrow) {
				EntityArrow arrow = ((ItemArrow) this.inventory.getStackInSlot(i).getItem()).createArrow(world, this.inventory.getStackInSlot(i).copy(), this);
				arrow.pickupStatus = EntityArrow.PickupStatus.ALLOWED;
				arrow.setEnchantmentEffectsFromEntity(this, distanceFactor);
				this.inventory.getStackInSlot(i).shrink(1);
				return arrow;
			}
		}
		return null;
	}

	public boolean hasArrow() {
		for (int i = 1; i < this.inventory.getSizeInventory(); i++) {
			if (this.inventory.getStackInSlot(i).getItem() instanceof ItemArrow) {
				return true;
			}
		}

		return this.getHeldItemMainhand().getItem() instanceof net.minecraft.item.ItemBow && EnchantmentHelper.getEnchantmentLevel(Enchantments.INFINITY, this.getHeldItemMainhand()) > 0;
	}

	public enum Task {
		FOLLOW, STAY, ADVENTURE, GO_HOME, STUDY, TRY_TO_SLEEP, IDENTIFY
	}
}
