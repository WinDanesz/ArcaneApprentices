package com.windanesz.apprenticearcana.client.render;

import com.windanesz.apprenticearcana.ApprenticeArcana;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import electroblob.wizardry.client.model.ModelWizard;
import net.minecraft.client.renderer.entity.RenderBiped;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.entity.layers.LayerBipedArmor;
import net.minecraft.util.ResourceLocation;

public class RenderWizardInitiate extends RenderBiped<EntityWizardInitiate> {
	static final ResourceLocation[] TEXTURES = new ResourceLocation[6];

	public RenderWizardInitiate(RenderManager renderManager) {
		super(renderManager, new ModelWizard(), 0.5F);

		for(int i = 0; i < 6; ++i) {
			TEXTURES[i] = new ResourceLocation(ApprenticeArcana.MODID, "textures/entity/wizard/wizard_" + i + ".png");
		}

		this.addLayer(new LayerBipedArmor(this));
	}

	protected ResourceLocation getEntityTexture(EntityWizardInitiate wizard) {
		return TEXTURES[wizard.textureIndex];
	}
}
