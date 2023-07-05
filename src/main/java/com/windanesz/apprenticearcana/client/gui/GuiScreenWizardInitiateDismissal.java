package com.windanesz.apprenticearcana.client.gui;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import com.windanesz.apprenticearcana.handler.XpProgression;
import com.windanesz.apprenticearcana.inventory.ContainerWizardInfo;
import com.windanesz.apprenticearcana.packet.AAPacketHandler;
import com.windanesz.apprenticearcana.packet.PacketControlInput;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.registry.Spells;
import electroblob.wizardry.spell.Spell;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;
import java.util.List;

@SideOnly(Side.CLIENT)
public class GuiScreenWizardInitiateDismissal extends GuiContainer {
	private static final ResourceLocation WIZARD_INFO_GUI = new ResourceLocation(ApprenticeArcana.MODID, "textures/gui/wizard_stats.png");
	private final IInventory playerInventory;
	private final IInventory horseInventory;
	private final EntityWizardInitiate wizard;
	private float mousePosx;
	private float mousePosY;
	private GuiButton button1;
	private GuiButton button2;
	private GuiButton button3;

	public GuiScreenWizardInitiateDismissal(IInventory playerInv, IInventory horseInv, EntityWizardInitiate wizard) {
		super(new ContainerWizardInfo(wizard, Minecraft.getMinecraft().player));
		this.playerInventory = playerInv;
		this.horseInventory = horseInv;
		this.wizard = wizard;
		this.allowUserInput = false;
		this.xSize = 176;
		this.ySize = 196;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		//        this.fontRenderer.drawString(this.horseInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		this.fontRenderer.drawString(this.wizard.getDisplayNameWithoutOwner().getUnformattedText(), 5, 13, 4210752);
//		this.fontRenderer.drawString(I18n.format("gui.apprenticearcana:wizard_level", this.wizard.getLevel(), this.wizard.getTotalXp(),
//				(int) XpProgression.calculateTotalXpRequired(this.wizard.getLevel() + 1)), 5, 13 + 12, 4210752);
		this.fontRenderer.drawString(I18n.format("gui.apprenticearcana:wizard_level", this.wizard.getLevel(), this.wizard.getTotalXp(),
				(int)	XpProgression.calculateTotalXpRequired(this.wizard.getLevel() + 1)), 5, 13 + 12, 4210752);
		this.fontRenderer.drawString(I18n.format("gui.apprenticearcana:spell_slots"), 5, 13 + 24, 4210752);
		List<Spell> spells = this.wizard.getSpells();
		int i = 0;
		//		for (Spell spell : spells) {
		//			// Draws spell illustration on opposite page, underneath the book so it shows through the hole.
		//			Minecraft.getMinecraft().renderEngine.bindTexture(spell.getIcon());
		//			//  : Spells.none.getIcon()
		//			int left = this.width/2 - xSize/2;
		//			int top = this.height/2 - this.ySize/2;
		//			DrawingUtils.drawTexturedRect(left + 146 + (i * 74), top + 40, 0, 0, 128, 128, 64, 64);
		//			i++;
		//		}
		//	this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		//	this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		int ij = 0;
		int left = (this.width / 2 - xSize / 2);
		int top = this.height / 2 - this.ySize / 2;

		for (int i = 0; i < wizard.getCurrentSpellSlotCap(); i++) {
			int left1 = left + 4;
			this.buttonList.add(new GuiButtonSpellToggle(i, left1 + 6 + (ij * 20), top + 50));
			ij++;
		}

	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		//if (button.enabled) {
		//if (button == this.applyBtn) {
		List<Spell> spells = this.wizard.getSpells();
		if (button.id <= spells.size() -1) {
			IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.SPELL_TOGGLE, spells.get(button.id));
			AAPacketHandler.net.sendToServer(msg);
		}
		//if (button instanceof GuiButtonSpellSort) {
		//	this.arcaneWorkbenchContainer.setSortType(((GuiButtonSpellSort)button).sortType);
		//}
		//}
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
		this.mc.getTextureManager().bindTexture(WIZARD_INFO_GUI);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		// render health
		drawHealthBar();

		// draw spell slots
		drawSpellSlots(partialTicks, mouseX, mouseY);

		// draw food level and saturation
		int left = (this.width / 2 - xSize / 2);
		int top = this.height / 2 - this.ySize / 2;
		this.fontRenderer.drawString(I18n.format("gui.apprenticearcana:food_level", Math.round(this.wizard.getFoodLevel() / 20 * 100)), left + 5, top + 82, 4210752);
		this.fontRenderer.drawString(I18n.format("gui.apprenticearcana:saturation", Math.round(this.wizard.getSaturation() / 20 * 100)), left + 5, top + 92, 4210752);

	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.mousePosx = (float) mouseX;
		this.mousePosY = (float) mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	private static class GuiButtonSpellToggle extends GuiButton {

		public GuiButtonSpellToggle(int id, int x, int y) {
			super(id, x, y, 16, 16, I18n.format("gui.apprenticearcana:spell_toggle_button"));
		}

		public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {}
	}

	public void drawHealthBar() {
		Minecraft.getMinecraft().renderEngine.bindTexture(ICONS);
		int effectX = 0;
		// somehow minecraft is bugged and was always considering the effects active even after they expire in the client side
		//		if (this.wizard.isPotionActive(MobEffects.WITHER)) {
		//			effectX = 72;
		//		} else if (this.wizard.isPotionActive(MobEffects.POISON)) {
		//			effectX = 36;
		//		}

		int lft = (this.width / 2 - xSize / 2) + 5;
		int tp = this.height / 2 - this.ySize / 2 + 70;
		int maxHearts = (int) (wizard.getMaxHealth() / 2);
		float currentHearts = wizard.getHealth() / 2;
		boolean hasHalfHeart = (currentHearts - (int) currentHearts) >= 0.5f || wizard.getHealth() < 1;
		int fullhearts = 0;
		for (int l = 0; l < maxHearts; l++) {
			// heart frame
			this.drawTexturedModalRect(lft + l * 9, tp, 16, 0, 9, 9);
			// current full HP
			//			if (l <= currentHearts && currentHearts > 1) {
			// 				}
			if (hasHalfHeart && (fullhearts == (int) currentHearts) || wizard.getHealth() < 1 && currentHearts == 0) {
				this.drawTexturedModalRect(lft + l * 9, tp, 61 + effectX, 0, 9, 9);
			} else if (l <= currentHearts - 1 && currentHearts > 1) {
				this.drawTexturedModalRect(lft + l * 9, tp, 52 + effectX, 0, 9, 9);
			}
			fullhearts++;
		}
	}

	public void drawSpellSlots(float partialTicks, int mouseX, int mouseY) {

		int left = (this.width / 2 - xSize / 2);
		int top = this.height / 2 - this.ySize / 2;

		List<Spell> spells = this.wizard.getSpells();
		while (spells.size() < 8) {
			spells.add(Spells.none);
		}
		int ij = 0;

		int cap = wizard.getCurrentSpellSlotCap();
		int k = 1;
		for (Spell spell : spells) {
			if (k > cap) {
				continue;
			}
			// Draws spell illustration on opposite page, underneath the book so it shows through the hole.
			Minecraft.getMinecraft().renderEngine.bindTexture(spell.getIcon());
			//  : Spells.none.getIcon()
			int left1 = left + 4;
			DrawingUtils.drawTexturedRect(left1 + 6 + (ij * 20), top + 50, 0, 0, 16, 16, 16, 16);

			int iconX = left1 + 5 + (ij * 20);
			int iconY = top + 50;
			if (mouseX >= iconX && mouseX < iconX + 16 && mouseY >= iconY && mouseY < iconY + 16) {
				String spellName = spell.getDisplayName();
				int textX = iconX + 3;
				int textY = iconY + 30 - mc.fontRenderer.FONT_HEIGHT;
				//mc.fontRenderer.drawString(spellName, textX, textY, 0xFFFFFF);
				this.fontRenderer.drawStringWithShadow(spellName, textX, textY, 0xFFFFFF);
			}
			if (this.wizard.isSpellDisabled(spell) && spell != Spells.none) {
				GlStateManager.pushMatrix();
				GlStateManager.enableBlend();
				GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1);
				Gui.drawRect(left1 + 6 + (ij * 20), top + 50, left1 + 6 + (ij * 20) + 16, top + 50 + 16, 1694433280);
				GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
				GlStateManager.disableBlend();
				GlStateManager.popMatrix();
			}

			ij++;
			k++;
		}
	}
}
