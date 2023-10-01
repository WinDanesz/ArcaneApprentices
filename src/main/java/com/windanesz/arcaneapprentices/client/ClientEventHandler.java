package com.windanesz.arcaneapprentices.client;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.Settings;
import electroblob.wizardry.item.ItemArtefact;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public final class ClientEventHandler {

	private ClientEventHandler(){}

	@SubscribeEvent
	public static void onTextureStitchEvent(TextureStitchEvent.Pre event) {

		event.getMap().registerSprite(new ResourceLocation(ArcaneApprentices.MODID, "gui/empty_artefact_slot"));
		event.getMap().registerSprite(new ResourceLocation(ArcaneApprentices.MODID, "gui/empty_main_hand_slot"));
		event.getMap().registerSprite(new ResourceLocation(ArcaneApprentices.MODID, "gui/locked_slot"));
	}

	@SubscribeEvent
	public static void event(ItemTooltipEvent event) {
		if (event.getItemStack().getItem() instanceof ItemArtefact && Settings.isArtefactEnabledForNPC(event.getItemStack().getItem())) {
			event.getToolTip().add(I18n.format("tooltip." + ArcaneApprentices.MODID + ":artefact_usable_by_npcs"));
		}
	}
}
