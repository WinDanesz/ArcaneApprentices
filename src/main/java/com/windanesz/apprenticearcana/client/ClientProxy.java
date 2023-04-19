package com.windanesz.apprenticearcana.client;

import com.windanesz.apprenticearcana.CommonProxy;
import com.windanesz.apprenticearcana.client.render.RenderWizardInitiate;
import com.windanesz.apprenticearcana.entity.living.EntityWizardInitiate;
import net.minecraft.client.settings.KeyBinding;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.relauncher.Side;

@Mod.EventBusSubscriber(Side.CLIENT)
public class ClientProxy extends CommonProxy {

	public static KeyBinding KEY_ACTIVATE_MORPH_ABILITY;

	/**
	 * Called from preInit() in the main mod class to initialise the renderers.
	 */
	public void registerRenderers() {
		RenderingRegistry.registerEntityRenderingHandler(EntityWizardInitiate.class, RenderWizardInitiate::new);
	}

	@Override
	public void init() {
		registerKeybindings();
	}

	public void preInit() {
		//		MinecraftForge.EVENT_BUS.unregister(Morph.eventHandlerClient);
	}

	private void registerKeybindings() {
		// Initializing
//		KEY_ACTIVATE_MORPH_ABILITY = new KeyBinding("key.apprenticearcana.activate_morph_ability", Keyboard.KEY_K, "key.apprenticearcana.category");
//		ClientRegistry.registerKeyBinding(KEY_ACTIVATE_MORPH_ABILITY);
	}
}