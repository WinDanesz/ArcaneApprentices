package com.windanesz.arcaneapprentices.entity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

import java.util.ArrayList;
import java.util.List;

public class MessageEntry {
	private String message;
	private int delay;

	public MessageEntry(String message, int delay) {
		this.message = message;
		this.delay = delay;
	}

	public String getMessage() {
		return message;
	}

	public int getDelay() {
		return delay;
	}

	public void decrementDelay() {
		if (delay > 0) delay--;
	}

	public static NBTTagCompound serializeMessages(List<MessageEntry> entries) {
		NBTTagCompound compound = new NBTTagCompound();
		NBTTagList messageList = new NBTTagList();

		for (MessageEntry entry : entries) {
			NBTTagCompound messageCompound = new NBTTagCompound();
			String message = entry.getMessage();
			int delay = entry.getDelay();

			messageCompound.setString("message", message);
			messageCompound.setInteger("delay", delay);

			messageList.appendTag(messageCompound);
		}

		compound.setTag("messages", messageList);
		return compound;
	}

	public static List<MessageEntry> deserializeMessages(NBTTagCompound compound) {
		List<MessageEntry> entries = new ArrayList<>();

		if (compound.hasKey("messages")) {
			NBTTagList messageList = compound.getTagList("messages", compound.getId());

			for (int i = 0; i < messageList.tagCount(); i++) {
				NBTTagCompound messageCompound = messageList.getCompoundTagAt(i);
				String message = messageCompound.getString("message");
				int value = messageCompound.getInteger("delay");

				MessageEntry entry = new MessageEntry(message, value);
				entries.add(entry);
			}
		}

		return entries;
	}

	public static MessageEntry removeNextMessage(List<MessageEntry> messageList) {
		if (!messageList.isEmpty()) {
			return messageList.remove(0);
		} else {
			return null;
		}
	}

	public static MessageEntry peekNextMessage(List<MessageEntry> messageList) {
		if (!messageList.isEmpty()) {
			return messageList.get(0);
		} else {
			return null;
		}
	}
}
