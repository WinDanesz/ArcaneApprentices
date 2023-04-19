package com.windanesz.apprenticearcana.handler;

import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

@Mod.EventBusSubscriber
public final class EventHandler {


	@SubscribeEvent
	public static void onPlayerTickEvent(ServerChatEvent event) {
		if (event.getPlayer() != null) {
			String message = event.getMessage();

		}
	}
}
