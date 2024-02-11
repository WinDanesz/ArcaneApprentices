package com.windanesz.arcaneapprentices.data;

import com.windanesz.arcaneapprentices.Settings;
import electroblob.wizardry.data.IStoredVariable;
import electroblob.wizardry.data.Persistence;
import electroblob.wizardry.data.WizardData;
import electroblob.wizardry.util.NBTExtras;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PlayerData {

	// For some reason 'the diamond' doesn't work if I chain methods onto this. Type inference is weird.
	public static final IStoredVariable<List<UUID>> WIZARD_APPRENTICES = new IStoredVariable.StoredVariable<>("wizardApprentices",
			s -> NBTExtras.listToNBT(s, NBTUtil::createUUIDTag),
			// For some reason gradle screams at me unless I explicitly declare the type of there, despite IntelliJ being fine without it
			(NBTTagList t) -> new ArrayList<>(NBTExtras.NBTToList(t, NBTUtil::getUUIDFromTag)),
			Persistence.ALWAYS);

	// For some reason 'the diamond' doesn't work if I chain methods onto this. Type inference is weird.
	public static final IStoredVariable<List<UUID>> CURRENT_PARTY = new IStoredVariable.StoredVariable<>("currentApprenticeParty",
			s -> NBTExtras.listToNBT(s, NBTUtil::createUUIDTag),
			// For some reason gradle screams at me unless I explicitly declare the type of there, despite IntelliJ being fine without it
			(NBTTagList t) -> new ArrayList<>(NBTExtras.NBTToList(t, NBTUtil::getUUIDFromTag)),
			Persistence.ALWAYS);

	public static final IStoredVariable<List<StoredEntity>> DEAD_APPRENTICES = new IStoredVariable.StoredVariable<List<StoredEntity>, NBTTagList>("deadApprentices",
			s -> NBTExtras.listToNBT(s, StoredEntity::toNBT), t -> new ArrayList<>(NBTExtras.NBTToList(t, StoredEntity::fromNBT)), Persistence.ALWAYS).setSynced();

	public static final IStoredVariable<List<StoredEntity>> ADVENTURING_APPRENTICES = new IStoredVariable.StoredVariable<List<StoredEntity>, NBTTagList>("adventuringApprentices",
			s -> NBTExtras.listToNBT(s, StoredEntity::toNBT), t -> new ArrayList<>(NBTExtras.NBTToList(t, StoredEntity::fromNBT)), Persistence.ALWAYS).setSynced();

	public static final IStoredVariable<List<StoredEntity>> PENDING_HOME_APPRENTICES = new IStoredVariable.StoredVariable<List<StoredEntity>, NBTTagList>("pendingHomeApprentices",
			s -> NBTExtras.listToNBT(s, StoredEntity::toNBT), t -> new ArrayList<>(NBTExtras.NBTToList(t, StoredEntity::fromNBT)), Persistence.ALWAYS).setSynced();

	// no instances!
	private PlayerData() {}

	public static void init() {
		WizardData.registerStoredVariables(WIZARD_APPRENTICES);
		WizardData.registerStoredVariables(CURRENT_PARTY);
		WizardData.registerStoredVariables(DEAD_APPRENTICES);
		WizardData.registerStoredVariables(PENDING_HOME_APPRENTICES);
		WizardData.registerStoredVariables(ADVENTURING_APPRENTICES);
	}

	private static List<UUID> getUUIDList(EntityPlayer player, IStoredVariable<List<UUID>> variable) {
		WizardData data = WizardData.get(player);
		List<UUID> uuids = data.getVariable(variable);
		if (uuids == null) {
			return new ArrayList<>();
		} else {
			return uuids;
		}
	}

	private static boolean addToUUIDList(EntityPlayer player, IStoredVariable<List<UUID>> variable, UUID uuid, int limit) {
		WizardData data = WizardData.get(player);
		List<UUID> uuidList = getUUIDList(player, variable);
		if (limit == -1 || uuidList.size() < limit && !uuidList.contains(uuid)) {
			uuidList.add(uuid);
			data.setVariable(variable, uuidList);
			return true;
		}
		// reached cap
		return false;
	}

	private static boolean removeFromUUIDList(EntityPlayer player, IStoredVariable<List<UUID>> variable, UUID uuid) {
		WizardData data = WizardData.get(player);
		List<UUID> uuidList = getUUIDList(player, variable);
		if (uuidList.contains(uuid)) {
			uuidList.remove(uuid);
			data.setVariable(variable, uuidList);
			return true;
		}
		// entity wasn't in the list
		return false;
	}

	private static boolean clearUUIDList(EntityPlayer player, IStoredVariable<List<UUID>> variable) {
		WizardData data = WizardData.get(player);
		data.setVariable(variable, new ArrayList<>());
		return true;
	}

	public static boolean addApprenticeForPlayer(EntityPlayer player, EntityLivingBase entity) {
		return addToUUIDList(player, WIZARD_APPRENTICES, entity.getUniqueID(), Settings.generalSettings.MAXIMUM_APPRENTICE_COUNT);
	}

	// Deliberately not returning an entity list as that would not show currently unloaded entities...
	public static List<UUID> getApprentices(EntityPlayer player) {
		return getUUIDList(player, WIZARD_APPRENTICES);
	}

	public static boolean removeApprentice(EntityPlayer player, EntityLivingBase entity) {
		return removeFromUUIDList(player, WIZARD_APPRENTICES, entity.getUniqueID());
	}

	public static boolean clearApprentices(EntityPlayer player) {
		return clearUUIDList(player, WIZARD_APPRENTICES);
	}

	// Deliberately not returning an entity list as that would not show currently unloaded entities...
	public static List<UUID> getParty(EntityPlayer player) {
		return getUUIDList(player, CURRENT_PARTY);
	}

	public static boolean addCurrentToParty(EntityPlayer player, EntityLivingBase entity) {
		return addToUUIDList(player, CURRENT_PARTY, entity.getUniqueID(), Settings.generalSettings.MAXIMUM_PARTY_SIZE);
	}

	public static boolean removeFromCurrentParty(EntityPlayer player, EntityLivingBase entity) {
		return removeFromUUIDList(player, CURRENT_PARTY, entity.getUniqueID());
	}

	public static List<StoredEntity> getDeadApprentices(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = data.getVariable(DEAD_APPRENTICES);
		if (list == null) {
			return new ArrayList<>();
		} else {
			return list;
		}
	}

	public static List<StoredEntity> getAdventuringApprentices(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = data.getVariable(ADVENTURING_APPRENTICES);
		if (list == null) {
			return new ArrayList<>();
		} else {
			return list;
		}
	}

	public static List<StoredEntity> getPendingHomeApprentices(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = data.getVariable(PENDING_HOME_APPRENTICES);
		if (list == null) {
			return new ArrayList<>();
		} else {
			return list;
		}
	}

	public static boolean storeDeadApprentice(EntityPlayer player, EntityLivingBase entity) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = getDeadApprentices(player);
		StoredEntity entityToStore = new StoredEntity(entity);

		if (!list.contains(entityToStore)) {
			list.add(entityToStore);
			data.setVariable(DEAD_APPRENTICES, list);
			return true;
		}

		return false;
	}

	public static boolean storeAdventuringApprentice(EntityPlayer player, EntityLivingBase entity) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = getAdventuringApprentices(player);
		StoredEntity entityToStore = new StoredEntity(entity);

		if (!list.contains(entityToStore)) {
			list.add(entityToStore);
			data.setVariable(ADVENTURING_APPRENTICES, list);
			data.sync();
			return true;
		}

		return false;
	}

	public static boolean storeAdventuringApprentice(EntityPlayer player, StoredEntity entityToStore) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = getAdventuringApprentices(player);

		if (!list.contains(entityToStore)) {
			list.add(entityToStore);
			data.setVariable(ADVENTURING_APPRENTICES, list);
			data.sync();
			return true;
		}

		return false;
	}

	public static boolean storePendingHomeOrFollowingApprentice(EntityPlayer player, EntityLivingBase entity, boolean following) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = getPendingHomeApprentices(player);
		StoredEntity entityToStore = new StoredEntity(entity);
		if (following) {
			entityToStore.nbtTagCompound.setBoolean("Follower", true);
		}
		if (!list.contains(entityToStore)) {
			list.add(entityToStore);
			data.setVariable(PENDING_HOME_APPRENTICES, list);
			return true;
		}

		return false;
	}

	public static boolean removeDeadApprentice(EntityPlayer player, UUID uuid) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = getDeadApprentices(player);

		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			StoredEntity entity = list.get(i);
			if (entity.getNbtTagCompound().getUniqueId("UUID").equals(uuid)) {
				index = i;
			}
		}
		if (index != -1) {
			list.remove(index);
			data.setVariable(DEAD_APPRENTICES, list);
			return true;
		}

		return false;
	}

	public static boolean removeAdventuringApprentice(EntityPlayer player, UUID uuid) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = getAdventuringApprentices(player);

		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			StoredEntity entity = list.get(i);
			if (entity.getNbtTagCompound().getUniqueId("UUID").equals(uuid)) {
				index = i;
			}
		}
		if (index != -1) {
			list.remove(index);
			data.setVariable(ADVENTURING_APPRENTICES, list);
			data.sync();
			return true;
		}

		return false;
	}

	public static boolean removePendingHomeApprentice(EntityPlayer player, UUID uuid) {
		WizardData data = WizardData.get(player);
		List<StoredEntity> list = getPendingHomeApprentices(player);

		int index = -1;
		for (int i = 0; i < list.size(); i++) {
			StoredEntity entity = list.get(i);
			if (entity.getNbtTagCompound().getUniqueId("UUID").equals(uuid)) {
				index = i;
			}
		}
		if (index != -1) {
			list.remove(index);
			data.setVariable(PENDING_HOME_APPRENTICES, list);
			data.sync();
			return true;
		}

		return false;
	}

	public static boolean clearDeadEntitiesList(EntityPlayer player) {
		WizardData data = WizardData.get(player);
		data.setVariable(DEAD_APPRENTICES, new ArrayList<>());
		return false;
	}


}
