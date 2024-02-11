package com.windanesz.arcaneapprentices.client.gui;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.data.PlayerData;
import com.windanesz.arcaneapprentices.data.StoredEntity;
import com.windanesz.arcaneapprentices.inventory.ContainerCharmItinerary;
import electroblob.wizardry.util.Location;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiScreenCharmItinerary extends GuiContainer {
	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(ArcaneApprentices.MODID, "textures/gui/charm_itinerary.png");

	public GuiScreenCharmItinerary() {
		super(new ContainerCharmItinerary());
		this.allowUserInput = false;
		this.xSize = 176;
		this.ySize = 196;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		EntityPlayerSP playerSP = Minecraft.getMinecraft().player;
		if (playerSP != null) {
			int i = 25;
			this.fontRenderer.drawString("Apprentices",20, 15, 13);
			List<StoredEntity> entityList = PlayerData.getAdventuringApprentices(playerSP);
			if (entityList.isEmpty()) {
				this.fontRenderer.drawSplitString(I18n.format("gui.arcaneapprentices:charm_itinerary_no_apprentices"), 20, i, 105, 1);
				return;
			}
			for (StoredEntity entity : entityList) {
				if (entity.nbtTagCompound.hasKey("CustomName")) {
					String name = entity.nbtTagCompound.getString("CustomName");
					int minutes = entity.nbtTagCompound.getInteger("AdventureRemainingDuration") / 20 / 60;
					if (entity.nbtTagCompound.hasKey("HomePos")) {
						Location homePos = Location.fromNBT(entity.getNbtTagCompound().getCompoundTag("HomePos"));
						this.fontRenderer.drawSplitString(I18n.format("gui.arcaneapprentices:charm_itinerary_entry_with_pos", name, minutes, homePos.pos.getX(),  homePos.pos.getY(),  homePos.pos.getZ()), 20, i, 105, 1);
					} else {
						this.fontRenderer.drawSplitString(I18n.format("gui.arcaneapprentices:charm_itinerary_entry", name, minutes), 20, i, 105, 1);
					}

					//this.fontRenderer.drawString(String.valueOf(),20 + 60 , i, 10);
				}
				i += 24;
			}
		}
		//				(int) XpProgression.calculateTotalXpRequired(this.wizard.getLevel() + 1)), 5, 13 + 12, 4210752);
//		this.fontRenderer.drawString(I18n.format("gui.arcaneapprentices:wizard_level", this.wizard.getLevel(), this.wizard.getTotalXp(),
//				(int) XpProgression.calculateTotalXpRequired(this.wizard.getLevel() + 1)), 5, 13 + 12, 4210752);
//		this.fontRenderer.drawString(I18n.format("gui.arcaneapprentices:spell_slots"), 5, 13 + 24, 4210752);
//		List<Spell> spells = this.wizard.getSpells();
//		int i = 0;
	}

	@Override
	public void initGui() {
		super.initGui();
//		this.buttonList.clear();
//		int ij = 0;
//		int left = (this.width / 2 - xSize / 2);
//		int top = this.height / 2 - this.ySize / 2;
//
//		for (int i = 0; i < wizard.getCurrentSpellSlotCap(); i++) {
//			int left1 = left + 4;
//			this.buttonList.add(new GuiButtonSpellToggle(i, left1 + 6 + (ij * 20), top + 50));
//			ij++;
//		}
//
//		buttonList.add(new NextPageButton(buttonList.size(), left + 140, top + 170));
	}

	@Override
	public void drawHoveringText(String text, int x, int y) {
		super.drawHoveringText(text, x, y);
		//
		//		List<Spell> spells = this.wizard.getSpells();
		//		int k = 0;
		//		for (Spell spell : spells) {
		//			TextComponentString spellName = new TextComponentString(spell.getDisplayName());
		//			HoverEvent hoverEvent = new HoverEvent(HoverEvent.Action.SHOW_TEXT, spellName);
		//			ITextComponent component = new TextComponentString("Spell: " + spell.getDisplayName());
		//			component.getStyle().setHoverEvent(hoverEvent);
		//			String displayText = component.getFormattedText();
		//			// Draws spell illustration on opposite page, underneath the book so it shows through the hole.
		//			Minecraft.getMinecraft().renderEngine.bindTexture(spell.getIcon());
		//			//  : Spells.none.getIcon()
		//			int left = this.width/2 - xSize/2;
		//			int top = this.height/2 - this.ySize/2;
		//			DrawingUtils.drawTexturedRect(left + 5+ (k * 74), top + 50, 0, 0, 16, 16, 16, 16);
		//			drawHoveringText(displayText, x, y);
		//			k++;
		//		}
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {

		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		// draw background
		this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

}
