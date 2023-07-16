package com.windanesz.apprenticearcana.client.render;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import electroblob.wizardry.client.model.ModelWizard;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.entity.Entity;
import net.minecraft.util.ResourceLocation;

import java.text.NumberFormat;

public class RenderWizardInitiate extends RenderBiped<EntityWizardInitiate> {
	static final ResourceLocation[] TEXTURES = new ResourceLocation[6];
	private boolean isSleeping;

	public RenderWizardInitiate(RenderManager renderManager) {
		super(renderManager, new ModelWizard(), 0.5F);

		for (int i = 0; i < 6; ++i) {
			TEXTURES[i] = new ResourceLocation(ApprenticeArcana.MODID, "textures/entity/wizard/wizard_" + i + ".png");
		}

		this.addLayer(new LayerBipedArmor(this));
	}

	protected ResourceLocation getEntityTexture(EntityWizardInitiate wizard) {
		return TEXTURES[wizard.textureIndex];
	}

	@Override
	protected void applyRotations(EntityWizardInitiate wizard, float ageInTicks, float rotationYaw, float partialTicks) {
		isSleeping = wizard.isLyingInBed();
		if (isSleeping) {
			float bedRotation = wizard.getBedRotation();
			if (bedRotation != -1) {
				GlStateManager.rotate(bedRotation, 0.0F, 1.0F, 0.0F);
				GlStateManager.rotate(this.getDeathMaxRotation(wizard), 0.0F, 0.0F, 1.0F);
				GlStateManager.rotate(270.0F, 0.0F, 1.0F, 0.0F);
				isSleeping = false;
				return;
			}
		}

		super.applyRotations(wizard, ageInTicks, rotationYaw, partialTicks);
	}

	@Override
	public void doRender(EntityWizardInitiate entity, double x, double y, double z, float entityYaw, float partialTicks) {
		super.doRender(entity, x, y, z, entityYaw, partialTicks);

		if (entity.getTask() == EntityWizardInitiate.Task.STUDY || entity.getTask() == EntityWizardInitiate.Task.IDENTIFY) {
			renderLecternProgress(entity, x, y, z, entityYaw, partialTicks);
		}
	}

	public void renderLecternProgress(EntityWizardInitiate entity, double x, double y, double z, float entityYaw, float partialTicks) {
		NumberFormat percentFormat = NumberFormat.getPercentInstance();
		percentFormat.setMinimumFractionDigits(1);
		String percentString = percentFormat.format(entity.getStudyProgress());
		renderLivingLabel(entity, percentString, x, y + 0.3, z, 20);
	}

}
