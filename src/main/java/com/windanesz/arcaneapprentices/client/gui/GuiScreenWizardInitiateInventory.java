package com.windanesz.arcaneapprentices.client.gui;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.entity.living.EntityWizardInitiate;
import com.windanesz.arcaneapprentices.inventory.ContainerWizardInitiateInventory;
import com.windanesz.arcaneapprentices.packet.AAPacketHandler;
import com.windanesz.arcaneapprentices.packet.PacketControlInput;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiScreenWizardInitiateInventory extends GuiContainer {
	private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation(ArcaneApprentices.MODID, "textures/gui/wizard_inventory.png");
	private final IInventory playerInventory;
	private final IInventory horseInventory;
	private final EntityWizardInitiate wizard;
	private float mousePosx;
	private float mousePosY;
	private GuiButton followBtn;
	private GuiButton HoldPositionBtn;
	private GuiButton studyBtn;
	private GuiButton statsBtn;
	private GuiButton dismissBtn;
	private GuiButton setHomeBtn;
	private GuiButton goHomeBtn;
	private GuiButton identifyBtn;
	private GuiButton journeyBtn;

	public GuiScreenWizardInitiateInventory(IInventory playerInv, IInventory horseInv, EntityWizardInitiate wizard) {
		super(new ContainerWizardInitiateInventory(playerInv, horseInv, wizard, Minecraft.getMinecraft().player));
		this.playerInventory = playerInv;
		this.horseInventory = horseInv;
		this.wizard = wizard;
		this.allowUserInput = false;
		this.xSize = 176;
		this.ySize = 196;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		//        this.fontRenderer.drawString(this.horseInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
		this.fontRenderer.drawString(this.wizard.getDisplayNameWithoutOwner().getUnformattedText(), 82, 13, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
		this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();

		if ((Minecraft.getMinecraft().player != null && this.wizard.getOwner() != null) &&
				(this.wizard.getOwner() == Minecraft.getMinecraft().player || Minecraft.getMinecraft().player.isCreative())) {
			this.followBtn = this.addButton(new GuiButton(0, this.width / 2 - 190, 50, 98, 20, I18n.format("gui.arcaneapprentices:follow_me_button")) {

			});
			this.HoldPositionBtn = this.addButton(new GuiButton(1, this.width / 2 - 190, 50 + 25, 98, 20, I18n.format("gui.arcaneapprentices:hold_this_position_button")));
		}
		this.studyBtn = this.addButton(new GuiButton(2, this.width / 2 - 190, 50 + 50, 98, 20, I18n.format("gui.arcaneapprentices:study_button")));
		this.statsBtn = this.addButton(new GuiButton(3, this.width / 2 - 190, 50 + 75, 98, 20, I18n.format("gui.arcaneapprentices:stats_button")));
		this.dismissBtn = this.addButton(new GuiButton(4, this.width / 2 - 190, 50 + 100, 98, 20, I18n.format("gui.arcaneapprentices:dismiss_button")));
		this.setHomeBtn = this.addButton(new GuiButton(5, this.width / 2 - 190, 50 + 125, 98, 20, I18n.format("gui.arcaneapprentices:set_home_button")));
		this.goHomeBtn = this.addButton(new GuiButton(5, this.width / 2 - 190, 50 + 150, 98, 20, I18n.format("gui.arcaneapprentices:go_home_button")));

		// right side
		this.identifyBtn = this.addButton(new GuiButton(5, this.width / 2 + 90, 50, 98, 20, I18n.format("gui.arcaneapprentices:identify_button")));
		this.journeyBtn = this.addButton(new GuiButton(5, this.width / 2 + 90, 75, 98, 20, I18n.format("gui.arcaneapprentices:journey_button")));
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		if (button.enabled) {
			if (button == followBtn) {
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.FOLLOW_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			} else if (button == HoldPositionBtn) {
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.STAY_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			} else if (button == studyBtn) {
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.STUDY_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			} else if (button == statsBtn) {
				this.mc.displayGuiScreen(null);
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.OPEN_STATS_GUI_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			} else if (button == dismissBtn) {
				this.mc.displayGuiScreen(null);
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.OPEN_DISMISS_WIZARD_GUI_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			} else if (button == setHomeBtn) {
				this.mc.displayGuiScreen(null);
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.SET_HOME_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			} else if (button == goHomeBtn) {
				this.mc.displayGuiScreen(null);
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.GO_HOME_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			} else if (button == identifyBtn) {
				this.mc.displayGuiScreen(null);
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.IDENTIFY_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			} else if (button == journeyBtn) {
				this.mc.displayGuiScreen(null);
				IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.OPEN_JOURNEY_GUI_BUTTON);
				AAPacketHandler.net.sendToServer(msg);
			}
		}
	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		// render health
		Minecraft.getMinecraft().renderEngine.bindTexture(ICONS);
		int effectX = 0;
		int effectY = 0;


		// somehow minecraft is bugged and was always considering the effects active even after they expire in the client side
		//		if (this.wizard.isPotionActive(MobEffects.WITHER)) {
		//			effectX = 72;
		//		} else if (this.wizard.isPotionActive(MobEffects.POISON)) {
		//			effectX = 36;
		//		}

		int lft = (this.width / 2 - xSize / 2) + 5;
		int tp = this.height / 2 - this.ySize / 2 + 90;
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
				this.drawTexturedModalRect(lft + l * 9, tp, 61 + effectX, effectY, 9, 9);
			} else if (l <= currentHearts - 1 && currentHearts > 1) {
				this.drawTexturedModalRect(lft + l * 9, tp, 52 + effectX, effectY, 9, 9);
			}
			fullhearts++;
		}

		GuiInventory.drawEntityOnScreen(i + 51, j + 55, 17, (float) (i + 51) - this.mousePosx, (float) (j + 75 - 50) - this.mousePosY, this.wizard);
	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		this.mousePosx = (float) mouseX;
		this.mousePosY = (float) mouseY;
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);
	}
}
