package com.windanesz.apprenticearcana.client.gui;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.Settings;
import com.windanesz.apprenticearcana.data.JourneyType;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import com.windanesz.apprenticearcana.handler.JourneySurvivalHandler;
import com.windanesz.apprenticearcana.inventory.ContainerWizardInititateAdventure;
import com.windanesz.apprenticearcana.packet.AAPacketHandler;
import com.windanesz.apprenticearcana.packet.PacketControlInput;
import com.windanesz.apprenticearcana.registry.AAItems;
import electroblob.wizardry.client.DrawingUtils;
import electroblob.wizardry.util.ParticleBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiTextField;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.init.Items;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemFood;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShears;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.io.IOException;

@SideOnly(Side.CLIENT)
public class GuiScreenWizardInitiateAdventure extends GuiContainer {
	private static final ResourceLocation GUI_BACKGROUND = new ResourceLocation(ApprenticeArcana.MODID, "textures/gui/wizard_adventuring.png");
	private static final int TEXTURE_WIDTH = 256;
	private static final int TEXTURE_HEIGHT = 256;
	private final EntityWizardInitiate wizard;
	private GuiTextField confirmField;
	private GuiButton infoButton;
	private GuiButton confirmButton;
	private GuiButton cancelButton;
	private GuiButtonWithDescription shortDuration;
	private GuiButtonWithDescription mediumDuration;
	private GuiButtonWithDescription longDuration;
	private GuiButtonWithDescription gatheringButton;
	private GuiButtonWithDescription mobSlayingButton;
	private GuiButtonWithDescription journeyButton;
	private GuiButtonWithDescription spellHuntButton;
	private GuiButtonWithDescription treasureHuntButton;
	private GuiButtonWithDescription netherAdventureButton;
	private GuiButtonWithDescription oceanAdventureButton;


	public GuiScreenWizardInitiateAdventure(EntityWizardInitiate wizard) {
		super(new ContainerWizardInititateAdventure(wizard, Minecraft.getMinecraft().player));
		this.wizard = wizard;
		this.allowUserInput = true;
		this.xSize = 176;
		this.ySize = 196;
		this.ySize = 230;
	}

	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
		//	this.fontRenderer.drawSplitString(I18n.format("gui.apprenticearcana:wizard_journey_gui_info", this.wizard.getDisplayNameWithoutOwner().getFormattedText()), 5, 88, 170, 4210752);
		this.fontRenderer.drawSplitString(I18n.format("gui.apprenticearcana:distance_to_travel"), 5, 140 - 32, 170, 4210752);
		this.fontRenderer.drawSplitString(I18n.format("gui.apprenticearcana:what_to_do"), 5, 180 - 35, 170, 4210752);

	}

	@Override
	public void initGui() {
		super.initGui();
		this.buttonList.clear();
		int left = (this.width / 2 - xSize / 2);
		int top = this.height / 2 - this.ySize / 2;

		this.buttonList.add(this.infoButton = new GuiButtonInfo(0, this.width / 2 - 8, top + 87, I18n.format("gui.apprenticearcana:confirm_button"), "confirm"));
		this.buttonList.add(this.confirmButton = new GuiButton(1, left + 100, top + 202, I18n.format("gui.apprenticearcana:confirm_button")));
		this.buttonList.add(this.cancelButton = new GuiButton(2, left + 20, top + 202, I18n.format("gui.apprenticearcana:cancel_button")));
		int offset = 35;
		this.buttonList.add(this.shortDuration = new GuiButtonWithDescription(3, left + 26, top + 155 - offset, 40, 20, I18n.format("gui.apprenticearcana:short"), "short_duration", 1));
		this.buttonList.add(this.mediumDuration = new GuiButtonWithDescription(4, left + 68, top + 155 - offset, 40, 20, I18n.format("gui.apprenticearcana:medium"), "medium_duration", 1));
		this.buttonList.add(this.longDuration = new GuiButtonWithDescription(5, left + 110, top + 155 - offset, 40, 20, I18n.format("gui.apprenticearcana:long"), "long_duration", 1));
		this.buttonList.add(this.gatheringButton = new GuiButtonWithDescription(6, left + 15, top + 190 - offset, 70, 20, I18n.format("gui.apprenticearcana:gather"), "gather", 2));
		this.buttonList.add(this.mobSlayingButton = new GuiButtonWithDescription(7, left + 90, top + 190 - offset, 70, 20, I18n.format("gui.apprenticearcana:slay_mobs"), "slay_mobs", 2));

		// default adventure
		this.buttonList.add(this.journeyButton = new GuiButtonWithDescription(8, left + 15, top + 213 - offset, 70, 20, I18n.format("gui.apprenticearcana:adventure"), "adventure", 2));

		// Fourth button
		if (wizard.isArtefactActive(AAItems.charm_spell_compass)) {
			this.buttonList.add(this.spellHuntButton = new GuiButtonWithDescription(9, left + 90, top + 213 - offset, 70, 20, I18n.format("gui.apprenticearcana:spell_hunt"), "spell_hunt", 2));
		} else if (wizard.isArtefactActive(AAItems.charm_treasure_map)) {
			this.buttonList.add(this.treasureHuntButton = new GuiButtonWithDescription(9, left + 90, top + 213 - offset, 70, 20, I18n.format("gui.apprenticearcana:treasure_hunt"), "treasure_hunt", 2));
		} else if (wizard.isArtefactActive(AAItems.charm_withering_atlas)) {
			this.buttonList.add(this.netherAdventureButton = new GuiButtonWithDescription(9, left + 90, top + 213 - offset, 70, 20, I18n.format("gui.apprenticearcana:nether_adventure"), "nether_adventure", 2));
		} else if (wizard.isArtefactActive(AAItems.charm_golden_lure)) {
			this.buttonList.add(this.oceanAdventureButton = new GuiButtonWithDescription(9, left + 90, top + 213 - offset, 70, 20, I18n.format("gui.apprenticearcana:ocean_adventure"), "ocean_adventure", 2));
		}

		confirmButton.width = 50;
		cancelButton.width = 50;
		updateButtonState();
	}

	@Override
	protected void actionPerformed(GuiButton button) throws IOException {
		String suffix = "";
		//	if (button == confirmButton) {
		//		IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.DISMISS_WIZARD_BUTTON);
		//		AAPacketHandler.net.sendToServer(msg);
		//	} else {
		//		// CLOSE_WINDOW
		//		Minecraft.getMinecraft().player.closeScreen();
		//	}

		int selectedCategories = 0;
		if (button instanceof GuiButtonWithDescription) {
			int currentCategory = ((GuiButtonWithDescription) button).category;
			for (GuiButton button1 : buttonList) {
				if (button1 != button && button1 instanceof GuiButtonWithDescription && ((GuiButtonWithDescription) button1).category == currentCategory && ((GuiButtonWithDescription) button1).requirementsMet) {
					button1.enabled = true;
					((GuiButtonWithDescription) button1).selected = false;
				}

			}
			button.enabled = false;
			((GuiButtonWithDescription) button).selected = true;
		}

		for (GuiButton button1 : buttonList) {

			if (button1 instanceof GuiButtonWithDescription && ((GuiButtonWithDescription) button1).requirementsMet) {
				if (((GuiButtonWithDescription) button1).selected) {
					selectedCategories += 1;
				}
			}

		}
		confirmButton.enabled = selectedCategories == 2;

		if (button == cancelButton) {
			IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.OPEN_WIZARD_INVENTORY_BUTTON);
			AAPacketHandler.net.sendToServer(msg);
		} else if (button == confirmButton) {

			if (getJourneyType().toLowerCase().toString().contains("gather")) {
				if (wizard.getHeldItemOffhand().getItem() instanceof ItemAxe) {
					suffix = "_AXE";
				} else if (wizard.getHeldItemOffhand().getItem() instanceof ItemPickaxe) {
					suffix = "_PICKAXE";
				} else if (wizard.getHeldItemOffhand().getItem() instanceof ItemShears) {
					suffix = "_SHEARS";
				}
			}

			JourneyType type = JourneyType.valueOf((getJourneyType() + "_" + getDuration()).toUpperCase() + suffix);
			IMessage msg = new PacketControlInput.Message(PacketControlInput.ControlType.JOURNEY_CONFIRM_BUTTON, type);
			AAPacketHandler.net.sendToServer(msg);

			if (wizard.verifyWandManaRequirementForJourney(type)) {
				for(int i = 0; i < 20; i++){

					float brightness = wizard.world.rand.nextFloat() * 0.1f + 0.1f;
					ParticleBuilder.create(ParticleBuilder.Type.CLOUD, wizard.world.rand, wizard.posX +1, wizard.posY, wizard.posZ, 1, false)
							.clr(brightness, brightness, brightness).time(80 + wizard.world.rand.nextInt(12)).shaded(true).spawn(wizard.world);


				}
			}

		}
		//		if (button == shortDuration || button == mediumDuration || button == longDuration) {
		//			shortDuration.enabled = button != shortDuration && hasEnoughFood(AdventureType.GATHERING_LOW_TIER);
		//			mediumDuration.enabled = button != mediumDuration && hasEnoughFood(AdventureType.GATHERING_MEDIUM_TIER);
		//			longDuration.enabled = button != longDuration && hasEnoughFood(AdventureType.GATHERING_HARD_TIER);
		//		} else
		//		if (button == gatheringButton || button == mobSlayingButton || button == journeyButton) {
		//			gatheringButton.enabled = button != gatheringButton;
		//			mobSlayingButton.enabled = button != mobSlayingButton;
		//			journeyButton.enabled = button != journeyButton;
		//		}

	}

	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

		// draw background
		this.mc.getTextureManager().bindTexture(GUI_BACKGROUND);
		int i = (this.width - this.xSize) / 2;
		int j = (this.height - this.ySize) / 2;
		this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

		//	this.confirmField.drawTextBox(); // Easier to do this last, then we don't need to re-bind the GUI texture twice

	}

	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.drawDefaultBackground();
		super.drawScreen(mouseX, mouseY, partialTicks);
		this.renderHoveredToolTip(mouseX, mouseY);


		// render button tooltips
		for (GuiButton button : buttonList) {
			if (button instanceof GuiButtonWithDescription && button.isMouseOver()) {
				int tooltipX = mouseX + 12;
				int tooltipY = mouseY + 12;
				if (button != infoButton) {
					if (((GuiButtonWithDescription) button).category == 1 && !((GuiButtonWithDescription) button).requirementsMet) {
						drawHoveringText(I18n.format("gui.apprenticearcana:not_enough_food"), tooltipX, tooltipY);
					} else if (((GuiButtonWithDescription) button).category == 2 && !((GuiButtonWithDescription) button).requirementsMet) {
						drawHoveringText(I18n.format("gui.apprenticearcana:too_low_level"), tooltipX, tooltipY);
					} else {
						drawHoveringText(I18n.format(((GuiButtonWithDescription) button).getDescriptionLanguageKey()), tooltipX, tooltipY);
					}
				} else {
					drawHoveringText(I18n.format("gui.apprenticearcana:wizard_journey_gui_info", this.wizard.getDisplayNameWithoutOwner().getFormattedText()), tooltipX, tooltipY);
				}
			}
		}
	}

	@Override
	protected void keyTyped(char typedChar, int keyCode) throws IOException {
		//if (this.confirmField.textboxKeyTyped(typedChar, keyCode)) {
		//	confirmButton.enabled = confirmField.getText().equalsIgnoreCase(this.wizard.getDisplayNameWithoutOwner().getUnformattedText());
		//} else {
		super.keyTyped(typedChar, keyCode);
		//}
	}

	public boolean hasEnoughFood(JourneyType type) {
		if (!Settings.journeySettings.JOURNEY_REQUIRE_FOOD) {
			return true;
		}

		String duration = type.getDuration();
		float totalFoodValue = 0f;
		for (int i = 1; i < wizard.inventory.getSizeInventory(); i++) {
			ItemStack stack = wizard.inventory.getStackInSlot(i);
			if (stack.getItem() instanceof ItemFood && stack.getItem() != Items.ROTTEN_FLESH) {
				totalFoodValue += ((ItemFood) stack.getItem()).getHealAmount(stack) * stack.getCount();
			}
		}

		float cost;
		if (duration.equals("SHORT")) {
			cost = 30;
		} else if (duration.equals("MEDIUM")) {
			cost = 90;
		} else {
			cost = 200;
		}

		cost *= Settings.journeySettings.JOURNEY_FOOD_REQUIREMENT_MODIFIER;
		return totalFoodValue >= cost;
	}

	private void updateButtonState() {
		confirmButton.enabled = false;
		shortDuration.enabled = shortDuration.requirementsMet = hasEnoughFood(JourneyType.GATHER_SHORT_DURATION);
		mediumDuration.enabled = mediumDuration.requirementsMet = hasEnoughFood(JourneyType.GATHER_MEDIUM_DURATION);
		longDuration.enabled = longDuration.requirementsMet = hasEnoughFood(JourneyType.GATHER_LONG_DURATION);
		gatheringButton.enabled = gatheringButton.requirementsMet = requirementsMet(gatheringButton.buttonCodeName);
		mobSlayingButton.enabled = mobSlayingButton.requirementsMet = requirementsMet(mobSlayingButton.buttonCodeName);
		journeyButton.enabled = journeyButton.requirementsMet = requirementsMet(journeyButton.buttonCodeName);
	}

	private boolean requirementsMet(String adventureTypeString) {
		String[] levelsRequired = Settings.journeySettings.LEVELS_REQUIRED_BY_EACH_JOURNEY_TYPE;

		for (String levelRequired : levelsRequired) {
			String[] parts = levelRequired.split(":");
			String adventureType = parts[0];
			int requiredLevel = Integer.parseInt(parts[1]);

			if (adventureType.equals(adventureTypeString)) {
				int wizardLevel = wizard.getLevel();
				return wizardLevel >= requiredLevel;
			}
		}

		return true;
	}

	private String getDuration() {
		for (GuiButton button : buttonList) {
			if (button instanceof GuiButtonWithDescription && ((GuiButtonWithDescription) button).category == 1 && ((GuiButtonWithDescription) button).selected) {
				return ((GuiButtonWithDescription) button).buttonCodeName;
			}
		}
		return "none";
	}

	private String getJourneyType() {
		for (GuiButton button : buttonList) {
			if (button instanceof GuiButtonWithDescription && ((GuiButtonWithDescription) button).category == 2 && ((GuiButtonWithDescription) button).selected) {
				return ((GuiButtonWithDescription) button).buttonCodeName;
			}
		}
		return "not_adventuring";
	}

	private static class GuiButtonWithDescription extends GuiButton {

		public boolean selected = false;
		public int category = 0;
		public boolean requirementsMet = true;
		String buttonCodeName;

		public GuiButtonWithDescription(int buttonId, int x, int y, int widthIn, int heightIn, String buttonText, String buttonCodeName, int category) {
			super(buttonId, x, y, widthIn, heightIn, buttonText);
			this.buttonCodeName = buttonCodeName;
			this.category = category;
		}

		public GuiButtonWithDescription(int buttonId, int x, int y, String buttonText, String buttonCodeName) {
			super(buttonId, x, y, buttonText);
			this.buttonCodeName = buttonCodeName;
		}

		public String getDescriptionLanguageKey() {
			return "gui." + ApprenticeArcana.MODID + ":" + buttonCodeName + ".description";
		}
	}

	private static class GuiButtonInfo extends GuiButtonWithDescription {

		public GuiButtonInfo(int id, int x, int y, String buttonText, String buttonCodeName) {
			super(id, x, y, 16, 16, buttonText, buttonCodeName, 0);
		}

		@Override
		public void drawButton(Minecraft minecraft, int mouseX, int mouseY, float partialTicks) {

			// Whether the button is highlighted
			this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

			int k = 176;
			int l = 0;

			if (this.enabled) {
				if (this.hovered) {
					k += this.width;
				}
			}

			DrawingUtils.drawTexturedRect(this.x, this.y, k, l, this.width, this.height, TEXTURE_WIDTH, TEXTURE_HEIGHT);
		}
	}

}
