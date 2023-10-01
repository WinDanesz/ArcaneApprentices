package com.windanesz.arcaneapprentices.client.gui;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardInititateDismissal;
import com.windanesz.arcaneapprentices.packet.AAPacketHandler;
import com.windanesz.arcaneapprentices.packet.PacketControlInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiScreenWizardInitiateDismissal extends GuiContainer {
	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(ArcaneApprentices.MODID, "textures/gui/wizard_dismiss.png");
	private final EntityWizardInitiate wizard;
	private GuiTextField confirmField;

	private GuiButton confirmButton;
	private GuiButton cancelButton;

	public GuiScreenWizardInitiateDismissal(EntityWizardInitiate wizard) {
		super(new ContainerWizardInititateDismissal(wizard, Minecraft.getMinecraft().player));
		this.wizard = wizard;
		this.allowUserInput = true;
		this.xSize = 176;
		this.ySize = 196;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		this.fontRenderer.drawSplitString(I18n.format("gui.arcaneapprentices:dismiss_wizard_confirmation", this.wizard.getDisplayNameWithoutOwner().getFormattedText()), 5, 13, 170, 4210752);
		this.fontRenderer.drawSplitString(I18n.format("gui.arcaneapprentices:dismiss_wizard_type_name", this.wizard.getDisplayNameWithoutOwner().getFormattedText()), 5, 13 + 95,170, 4210752);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		int left = (this.width / 2 - xSize / 2);
		int top = this.height / 2 - this.ySize / 2;

		this.buttonList.add(this.confirmButton = new GuiButton(0, left + 100, top + 150, I18n.format("gui.arcaneapprentices:confirm_button")));
		this.buttonList.add(this.cancelButton = new GuiButton(1, left + 20, top + 150, I18n.format("gui.arcaneapprentices:cancel_button")));
		this.confirmButton.enabled = false;
		confirmButton.width = 50;
		cancelButton.width = 50;

		this.confirmField = new GuiTextField(0, this.fontRenderer, this.guiLeft + 16, this.guiTop + 130, this.ySize / 2 + 42, this.fontRenderer.FONT_HEIGHT);
		this.confirmField.setMaxStringLength(50);
		this.confirmField.setEnableBackgroundDrawing(true);
		this.confirmField.setVisible(true);
		this.confirmField.setTextColor(16777215);
		this.confirmField.setCanLoseFocus(false);
		this.confirmField.height = 15;
		this.confirmField.setFocused(true);
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button == confirmButton) {
			IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.DISMISS_WIZARD_BUTTON);
			AAPacketHandler.net.sendToServer(msg);
		} else {
			// cancel button
			IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.OPEN_WIZARD_INVENTORY_BUTTON);
			AAPacketHandler.net.sendToServer(msg);
		}
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		// draw background
		this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		this.confirmField.drawTextBox(); // Easier to do this last, then we don't need to re-bind the GUI texture twice

	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		if (this.confirmField.textboxKeyTyped(typedChar, keyCode)) {
			confirmButton.enabled = confirmField.getText().equalsIgnoreCase(this.wizard.getDisplayNameWithoutOwner().getUnformattedText());
		} else {
			super.keyTyped(typedChar, keyCode);
		}
	}
}
