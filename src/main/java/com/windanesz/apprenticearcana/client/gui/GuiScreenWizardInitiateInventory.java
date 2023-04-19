package com.windanesz.apprenticearcana.client.gui;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import com.windanesz.apprenticearcana.inventory.ContainerWizardInitiateInventory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.passive.AbstractChestHorse;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraft.entity.passive.EntityLlama;
import net.minecraft.inventory.ContainerHorseInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class GuiScreenWizardInitiateInventory extends GuiContainer
{
    private static final ResourceLocation HORSE_GUI_TEXTURES = new ResourceLocation(ApprenticeArcana.MODID, "textures/gui/wizard.png");
    private final IInventory playerInventory;
    private final IInventory horseInventory;
    private final EntityWizardInitiate wizard;
    private float mousePosx;
    private float mousePosY;

    public GuiScreenWizardInitiateInventory(IInventory playerInv, IInventory horseInv, EntityWizardInitiate wizard)
    {
        super(new ContainerWizardInitiateInventory(playerInv, horseInv, wizard, Minecraft.getMinecraft().player));
        this.playerInventory = playerInv;
        this.horseInventory = horseInv;
        this.wizard = wizard;
        this.allowUserInput = false;
        this.xSize = 176;
        this.ySize = 196;
    }

    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
//        this.fontRenderer.drawString(this.horseInventory.getDisplayName().getUnformattedText(), 8, 6, 4210752);
        this.fontRenderer.drawString(this.wizard.getDisplayName().getUnformattedText(), 82, 13, 4210752);
        this.fontRenderer.drawString(this.playerInventory.getDisplayName().getUnformattedText(), 8, this.ySize - 96 + 2, 4210752);
    }

    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.mc.getTextureManager().bindTexture(HORSE_GUI_TEXTURES);
        int i = (this.width - this.xSize) / 2;
        int j = (this.height - this.ySize) / 2;
        this.drawTexturedModalRect(i, j, 0, 0, this.xSize, this.ySize);

        if (this.wizard instanceof EntityWizardInitiate)
        {
            EntityWizardInitiate wizardInitiate = (EntityWizardInitiate)this.wizard;

            if (wizardInitiate.hasChest())
            {
           //     this.drawTexturedModalRect(i + 79, j + 17, 0, this.ySize, wizardInitiate.getInventoryColumns() * 18, 54);
            }
        }

        if (this.wizard.canBeSaddled())
        {
       //     this.drawTexturedModalRect(i + 7, j + 35 - 28, 18, this.ySize + 54, 18, 18);
        }

        if (this.wizard.wearsArmor())
        {
//            if (this.wizard instanceof EntityLlama)
//            {
//                this.drawTexturedModalRect(i + 7, j + 35, 36, this.ySize + 54, 18, 18);
//            }
//            else
            {
           //     this.drawTexturedModalRect(i + 7, j + 35, 0, this.ySize + 54, 18, 18);
            }
        }

        GuiInventory.drawEntityOnScreen(i + 51, j + 55, 17, (float)(i + 51) - this.mousePosx, (float)(j + 75 - 50) - this.mousePosY, this.wizard);
    }

    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        this.mousePosx = (float)mouseX;
        this.mousePosY = (float)mouseY;
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }
}
