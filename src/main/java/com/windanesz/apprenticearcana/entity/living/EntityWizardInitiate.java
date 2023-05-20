package com.windanesz.apprenticearcana.entity.living;

import com.google.common.base.Predicate;
import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.Utils;
import com.windanesz.apprenticearcana.client.gui.AAGuiHandler;
import com.windanesz.apprenticearcana.entity.ai.EntityAIPanicAtLowHP;
import com.windanesz.apprenticearcana.entity.ai.EntityAIStudy;
import com.windanesz.apprenticearcana.entity.ai.EntityAIWatchClosestLectern;
import com.windanesz.apprenticearcana.inventory.ContainerWizardInventory;
import electroblob.wizardry.Wizardry;
import electroblob.wizardry.constants.Element;
import electroblob.wizardry.entity.living.EntityAIAttackSpell;
import electroblob.wizardry.entity.living.EntityWizard;
import electroblob.wizardry.entity.living.ISpellCaster;
import electroblob.wizardry.entity.living.ISummonedCreature;
import electroblob.wizardry.item.ItemSpellBook;
import electroblob.wizardry.misc.WildcardTradeList;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.registry.WizardryAdvancementTriggers;
import electroblob.wizardry.registry.WizardryItems;
import electroblob.wizardry.registry.WizardryPotions;
import electroblob.wizardry.registry.WizardrySounds;
import electroblob.wizardry.spell.Spell;
import electroblob.wizardry.util.AllyDesignationSystem;
import electroblob.wizardry.util.EntityUtils;
import electroblob.wizardry.util.InventoryUtils;
import electroblob.wizardry.util.NBTExtras;
import electroblob.wizardry.util.ParticleBuilder;
import electroblob.wizardry.util.SpellModifiers;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.IEntityOwnable;
import net.minecraft.entity.INpc;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.EntityAIHurtByTarget;
import net.minecraft.entity.ai.EntityAIMoveTowardsRestriction;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.ai.EntityAIOpenDoor;
import net.minecraft.entity.ai.EntityAIRestrictOpenDoor;
import net.minecraft.entity.ai.EntityAISwimming;
import net.minecraft.entity.ai.EntityAIWander;
import net.minecraft.entity.ai.EntityAIWatchClosest;
import net.minecraft.entity.ai.EntityAIWatchClosest2;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.ContainerHorseChest;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.IInventoryChangedListener;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.village.MerchantRecipeList;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.EnumDifficulty;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class EntityWizardInitiate extends EntityCreature implements INpc, ISpellCaster, IEntityAdditionalSpawnData, IInventoryChangedListener, IEntityOwnable {

	private EntityAIAttackSpell<EntityWizardInitiate> spellCastingAI = new EntityAIAttackSpell(this, 0.5, 14.0F, 30, 50);

	public int textureIndex = 0;
	protected Predicate<Entity> targetSelector;
	private MerchantRecipeList trades;
	private BlockPos lectern;

	private UUID ownerUUID;

	@Nullable
	private EntityPlayer customer;
	private int timeUntilReset;
	private boolean updateRecipes;
	private static final DataParameter<Integer> HEAL_COOLDOWN = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> ELEMENT = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<String> CONTINUOUS_SPELL = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.STRING);
	private static final DataParameter<Integer> SPELL_COUNTER = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Integer> LEVEL = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.VARINT);
	private static final DataParameter<Float> XP = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> HUNGER = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.FLOAT);
	private static final DataParameter<Float> STUDY_PROGRESS = EntityDataManager.createKey(EntityWizardInitiate.class, DataSerializers.FLOAT);
	private static final DataParameter<Boolean> IS_CHILD = EntityDataManager.<Boolean>createKey(EntityWizardInitiate.class, DataSerializers.BOOLEAN);
	public ContainerWizardInventory inventory;

	private List<Spell> spells = new ArrayList(4);
	private Set<BlockPos> towerBlocks;
	private float wizardWidth = -1.0F;
	private float wizardHeight;

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
		this.dataManager.register(SPELL_COUNTER, 0);
		this.dataManager.register(LEVEL, 1);
		this.dataManager.register(XP, 0f);
		this.dataManager.register(HUNGER, 0f);
		this.dataManager.register(IS_CHILD, false);
		this.dataManager.register(STUDY_PROGRESS, 0f);
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
		return this.getDataManager().get(IS_CHILD).booleanValue();
	}

	public void setChild(boolean isChild) {
		this.getDataManager().set(IS_CHILD, Boolean.valueOf(isChild));
		this.setChildSize(isChild);
	}

	public float getStudyProgress() {
		System.out.println("progress: " + this.getDataManager().get(STUDY_PROGRESS).floatValue());
		return this.getDataManager().get(STUDY_PROGRESS).floatValue();
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

	protected void initEntityAI() {
		this.tasks.addTask(0, new EntityAISwimming(this));
		this.tasks.addTask(1, new EntityAIPanicAtLowHP(this, 1.15D));
		//		this.tasks.addTask(1, new EntityWizard.EntityAITradePlayer(this));
		//		this.tasks.addTask(1, new EntityWizard.EntityAILookAtTradePlayer(this));
		this.tasks.addTask(4, new EntityAIRestrictOpenDoor(this));
		this.tasks.addTask(5, new EntityAIOpenDoor(this, true));
		this.tasks.addTask(6, new EntityAIMoveTowardsRestriction(this, 0.6));
		this.tasks.addTask(6, new EntityAIStudy(this, 10, 1));
//		this.tasks.addTask(6, new EntityAIWatchClosestLectern(this,  3));
		this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityPlayer.class, 3.0F, 1.0F));
		this.tasks.addTask(7, new EntityAIWatchClosest2(this, EntityWizard.class, 5.0F, 0.02F));
		this.tasks.addTask(7, new EntityAIWander(this, 0.6));
		this.tasks.addTask(8, new EntityAIWatchClosest(this, EntityLiving.class, 8.0F));
		this.targetSelector = (entity) -> {
			return entity != null && !entity.isInvisible() && AllyDesignationSystem.isValidTarget(this, entity) && (entity instanceof IMob || entity instanceof ISummonedCreature || Arrays.asList(Wizardry.settings.summonedCreatureTargetsWhitelist).contains(EntityList.getKey(entity.getClass()))) && !Arrays.asList(Wizardry.settings.summonedCreatureTargetsBlacklist).contains(EntityList.getKey(entity.getClass()));
		};
		this.targetTasks.addTask(1, new EntityAIHurtByTarget(this, true, new Class[0]));
		this.targetTasks.addTask(0, new EntityAINearestAttackableTarget(this, EntityLiving.class, 0, false, true, this.targetSelector));
	}

	protected void applyEntityAttributes() {
		super.applyEntityAttributes();
		this.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED).setBaseValue(0.5);
		this.getEntityAttribute(SharedMonsterAttributes.MAX_HEALTH).setBaseValue(30.0);
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
	//	if (inventory.getStackInSlot(7).getItem() instanceof ItemSpellBook) {
			List<Spell> spells = new ArrayList(4);
		//	spells.add(Spell.byMetadata(inventory.getStackInSlot(7).getItem().getDamage(inventory.getStackInSlot(7))));
	//		return spells;
	//	}
		return this.spells;
	}

	public SpellModifiers getModifiers() {
		return new SpellModifiers();
	}

	public void setContinuousSpell(Spell spell) {
		this.dataManager.set(CONTINUOUS_SPELL, spell.getRegistryName().toString());
	}

	public Spell getContinuousSpell() {
		return Spell.get((String) this.dataManager.get(CONTINUOUS_SPELL));
	}

	public void setSpellCounter(int count) {
		this.dataManager.set(SPELL_COUNTER, count);
	}

	public int getSpellCounter() {
		return (Integer) this.dataManager.get(SPELL_COUNTER);
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

	public ITextComponent getDisplayName() {
		return this.hasCustomName() ? super.getDisplayName() : this.getElement().getWizardName();
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
		int healCooldown = this.getHealCooldown();
		if (healCooldown == 0 && this.getHealth() < this.getMaxHealth() && this.getHealth() > 0.0F && !this.isPotionActive(WizardryPotions.arcane_jammer)) {
			this.heal(this.getElement() == Element.HEALING ? 8.0F : 4.0F);
			this.setHealCooldown(-1);
		} else if (healCooldown == -1 && this.deathTime == 0) {
			if (this.world.isRemote) {
				ParticleBuilder.spawnHealParticles(this.world, this);
			} else {
				if (this.getHealth() < 10.0F) {
					this.setHealCooldown(150);
				} else {
					this.setHealCooldown(400);
				}

				this.playSound(Spells.heal.getSounds()[0], 0.7F, this.rand.nextFloat() * 0.4F + 1.0F);
			}
		}

		if (healCooldown > 0) {
			this.setHealCooldown(healCooldown - 1);
		}

	}

	protected void updateAITasks() {
		//		if (!this.isTrading() && this.timeUntilReset > 0) {
		//			--this.timeUntilReset;
		//			if (this.timeUntilReset <= 0) {
		//				if (this.updateRecipes) {
		//					Iterator var1 = this.trades.iterator();
		//
		//					while(var1.hasNext()) {
		//						MerchantRecipe merchantrecipe = (MerchantRecipe)var1.next();
		//						if (merchantrecipe.isRecipeDisabled()) {
		//							merchantrecipe.increaseMaxTradeUses(this.rand.nextInt(6) + this.rand.nextInt(6) + 2);
		//						}
		//					}
		//
		//					if (this.trades.size() < 12) {
		//						this.addRandomRecipes(1);
		//					}
		//
		//					this.updateRecipes = false;
		//				}
		//
		//				this.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, 200, 0));
		//			}
		//		}

		super.updateAITasks();
	}

	public boolean processInteract(EntityPlayer player, EnumHand hand) {

		if (player.isSneaking()) {
			player.openGui(ApprenticeArcana.MODID, AAGuiHandler.WIZARD_GUI, this.world, this.getEntityId(), 0, 0);
			//setChild(true);
		} else {
			this.setOwner(player);
		}
		//else {
		//	this.setChild(false);
		//}
		//		ItemStack stack = player.getHeldItem(hand);
		//		if (player.isCreative() && stack.getItem() instanceof ItemSpellBook) {
		//			Spell spell = Spell.byMetadata(stack.getItemDamage());
		//			if (this.spells.size() >= 4 && spell.canBeCastBy(this, true)) {
		//				player.sendMessage(new TextComponentTranslation("item.ebwizardry:spell_book.apply_to_wizard", new Object[]{this.getDisplayName(), ((Spell)this.spells.set(this.rand.nextInt(3) + 1, spell)).getNameForTranslationFormatted(), spell.getNameForTranslationFormatted()}));
		//				return true;
		//			}
		//		}
		//
		//		if (this.isEntityAlive() && !this.isTrading() && !this.isChild() && !player.isSneaking() && this.getAttackTarget() != player) {
		//			if (!this.world.isRemote) {
		//				this.setCustomer(player);
		//				player.displayVillagerTradeGui(this);
		//			}
		//
		//			return true;
		//		} else {
		return false;
		//		}
	}

	public void writeEntityToNBT(NBTTagCompound nbt) {
		super.writeEntityToNBT(nbt);
		if (this.trades != null) {
			NBTExtras.storeTagSafely(nbt, "trades", this.trades.getRecipiesAsTags());
		}

		if (this.getOwner() != null) {
			nbt.setUniqueId("ownerUUID", this.getOwnerId());
		}

		Element element = this.getElement();
		nbt.setInteger("element", element == null ? 0 : element.ordinal());
		nbt.setInteger("skin", this.textureIndex);
		NBTExtras.storeTagSafely(nbt, "spells", NBTExtras.listToNBT(this.spells, (spell) -> new NBTTagInt(spell.metadata())));

		if (this.towerBlocks != null && this.towerBlocks.size() > 0) {
			NBTExtras.storeTagSafely(nbt, "towerBlocks", NBTExtras.listToNBT(this.towerBlocks, NBTUtil::createPosTag));
		}

		nbt.setBoolean("IsChild", isChild());

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
		nbt.setTag("Items", nbttaglist);
	}

	public void readEntityFromNBT(NBTTagCompound nbt) {
		super.readEntityFromNBT(nbt);
		if (nbt.hasKey("trades")) {
			NBTTagCompound nbttagcompound1 = nbt.getCompoundTag("trades");
			this.trades = new WildcardTradeList(nbttagcompound1);
		}
		this.setOwnerId(nbt.getUniqueId("ownerUUID"));
		this.setElement(Element.values()[nbt.getInteger("element")]);
		this.textureIndex = nbt.getInteger("skin");
		//		this.spells = (List)NBTExtras.NBTToList(nbt.getTagList("spells", 3), (tag) -> {
		//			return Spell.byMetadata(tag.getInt());
		//		});
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
	}

	protected int getInventorySize() {
		return 23;
	}

	protected void initInventory() {
		ContainerWizardInventory containerhorsechest = this.inventory;
		this.inventory = new ContainerWizardInventory("WizardInventory", false, this.getInventorySize());
		this.inventory.setCustomName(this.getName());

		if (containerhorsechest != null) {
			containerhorsechest.removeInventoryChangeListener(this);
			int i = Math.min(containerhorsechest.getSizeInventory(), this.inventory.getSizeInventory());

			for (int j = 0; j < i; ++j) {
				ItemStack itemstack = containerhorsechest.getStackInSlot(j);

				if (!itemstack.isEmpty()) {
					this.inventory.setInventorySlotContents(j, itemstack.copy());
				}
			}
		}

		this.inventory.addInventoryChangeListener(this);
		this.itemHandler = new net.minecraftforge.items.wrapper.InvWrapper(this.inventory);
	}

	public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, IEntityLivingData livingdata) {
		livingdata = super.onInitialSpawn(difficulty, livingdata);
		this.setCustomNameTag(Utils.generateWizardName(world));
		// this.textureIndex = this.rand.nextInt(1);
		this.textureIndex = 0; //this.rand.nextInt(1);
		if (this.rand.nextBoolean()) {
			this.setElement(Element.values()[this.rand.nextInt(Element.values().length - 1) + 1]);
		} else {
			this.setElement(Element.MAGIC);
		}

		Element element = this.getElement();
		EntityEquipmentSlot[] var4 = InventoryUtils.ARMOUR_SLOTS;
		int var5 = var4.length;

		int var6;
		EntityEquipmentSlot slot;
		for (var6 = 0; var6 < var5; ++var6) {
			slot = var4[var6];
			this.setItemStackToSlot(slot, new ItemStack(WizardryItems.getArmour(element, slot)));
		}

		var4 = EntityEquipmentSlot.values();
		var5 = var4.length;

		for (var6 = 0; var6 < var5; ++var6) {
			slot = var4[var6];
			this.setDropChance(slot, 0.0F);
		}

		this.spells.add(Spells.magic_missile);
		//		Tier maxTier = populateSpells(this, this.spells, element, false, 3, this.rand);
		//		ItemStack wand = new ItemStack(WizardryItems.getWand(maxTier, element));
		//		ArrayList<Spell> list = new ArrayList(this.spells);
		//		list.add(Spells.heal);
		//		WandHelper.setSpells(wand, (Spell[])list.toArray(new Spell[5]));
		//		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, wand);
		this.setHealCooldown(50);
		return livingdata;
	}

	public void writeSpawnData(ByteBuf data) {
		data.writeInt(this.textureIndex);
	}

	public void readSpawnData(ByteBuf data) {
		this.textureIndex = data.readInt();
	}

	public boolean attackEntityFrom(DamageSource source, float damage) {
		if (source.getTrueSource() instanceof EntityPlayer) {
			WizardryAdvancementTriggers.anger_wizard.triggerFor((EntityPlayer) source.getTrueSource());
		}

		return super.attackEntityFrom(source, damage);
	}

	@Override
	public void onInventoryChanged(IInventory inventory) {
		//boolean flag = this.isHorseSaddled();
		//	this.updateHorseSlots();
		ItemStack oldItem = this.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
		ItemStack newItem = this.inventory.getStackInSlot(0);
		if (ItemStack.areItemStacksEqual(oldItem, newItem)) {
			this.resetStudyProgress();
		}

		this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, inventory.getStackInSlot(0));
		this.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, inventory.getStackInSlot(1));
		this.setItemStackToSlot(EntityEquipmentSlot.HEAD, inventory.getStackInSlot(2));
		this.setItemStackToSlot(EntityEquipmentSlot.CHEST, inventory.getStackInSlot(3));
		this.setItemStackToSlot(EntityEquipmentSlot.LEGS, inventory.getStackInSlot(4));
		this.setItemStackToSlot(EntityEquipmentSlot.FEET, inventory.getStackInSlot(5));
//		this.setItemStackToSlot(slot, inventory.getStackInSlot(slot.getIndex() + 2));
//		for (EntityEquipmentSlot slot : EntityEquipmentSlot.values()) {
//			if (!inventory.getStackInSlot(slot.getIndex() + 2).isEmpty()) {
//				System.out.println("index: " + slot.getIndex() + 2 + "item: " + inventory.getStackInSlot(slot.getIndex() + 2).getDisplayName() );
//				this.setItemStackToSlot(slot, inventory.getStackInSlot(slot.getIndex() + 2));
//			}
//		}
	}

	public boolean canBeSaddled() {
		return true;
	}

	public boolean isArmor(ItemStack stack) {
		return false;
	}

	public boolean wearsArmor() {
		return false;
	}

	public boolean hasChest() {
		return true;
	}

	public int getInventoryColumns() {
		return 5;
	}

	// FORGE
	private net.minecraftforge.items.IItemHandler itemHandler = null; // Initialized by initHorseChest above.

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

	@Override
	public UUID getOwnerId() {
		return ownerUUID;
	}

	public void setOwnerId(UUID uuid) {
		this.ownerUUID = uuid;
	}

	public void setOwner(@Nullable EntityLivingBase owner) {
		setOwnerId(owner == null ? null : owner.getUniqueID());
	}

	@Nullable
	@Override
	public Entity getOwner() {

		Entity entity = EntityUtils.getEntityByUUID(this.world, getOwnerId());

		if (entity != null && !(entity instanceof EntityLivingBase)) { // Should never happen
			ApprenticeArcana.logger.warn("{} has a non-living owner!", this);
			return null;
		}

		return entity;
	}

	public boolean isStudying() {
		if (getHeldItemOffhand().getItem() instanceof ItemSpellBook && !isSpellKnown(Spell.byMetadata(getHeldItemOffhand().getItemDamage()))) {
			return true;
		}
		return false;
	}

	public boolean isSpellKnown(Spell spell) {
		return this.spells.contains(spell);
	}

	public void learnSpell(Spell spell) {
		this.spells.add(spell);
		this.resetStudyProgress();
	}

	public BlockPos getLectern() {
		return lectern;
	}

	public void setLectern(BlockPos lectern) {
		this.lectern = lectern;
	}

	public float getStudyProgressForSpell(Spell spell) {
		double modifier = Math.pow(((double) spell.getTier().ordinal() / 2f) + 3f, 2f);
		return (float) (0.05f / modifier);
	}
}
