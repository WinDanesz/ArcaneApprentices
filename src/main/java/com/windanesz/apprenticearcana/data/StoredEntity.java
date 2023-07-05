package com.windanesz.apprenticearcana.data;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;

import javax.annotation.concurrent.Immutable;

/**
 * Simple wrapper class that stores info of an entity in NBT.
 */
@Immutable
public class StoredEntity {

	public final NBTTagCompound nbtTagCompound;

	public StoredEntity(EntityLivingBase entity) {
		this.nbtTagCompound = entity.serializeNBT();
	}

	private StoredEntity(NBTTagCompound nbt) {
		this.nbtTagCompound = nbt;
	}

	/**
	 * Returns true if the two entities are the same
	 */
	@Override
	public boolean equals(Object that) {

		if (this == that) {return true;}

		if (that instanceof EntityLivingBase) {
			return ((EntityLivingBase) that).getUniqueID() == nbtTagCompound.getUniqueId("UUID");
		}

		return false;
	}

	public NBTTagCompound toNBT() {
		return nbtTagCompound;
	}

	public static StoredEntity fromNBT(NBTTagCompound nbt) {
		return new StoredEntity(nbt);
	}

	public NBTTagCompound getNbtTagCompound() {
		return nbtTagCompound;
	}
}
