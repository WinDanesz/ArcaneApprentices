package com.windanesz.apprenticearcana;

import com.windanesz.apprenticearcana.client.gui.AAGuiHandler;
import com.windanesz.apprenticearcana.data.PlayerData;
import com.windanesz.apprenticearcana.packet.AAPacketHandler;
import com.windanesz.apprenticearcana.registry.BlockRegistry;
import com.windanesz.apprenticearcana.registry.LootRegistry;
import com.windanesz.apprenticearcana.village.StructureWizardHouse;
import com.windanesz.wizardryutils.registry.ItemModelRegistry;
import electroblob.wizardry.api.WizardryEnumHelper;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;
import java.util.Random;
import java.util.stream.Collectors;

@Mod(modid = ApprenticeArcana.MODID, name = ApprenticeArcana.NAME, version = "@VERSION@", acceptedMinecraftVersions = "[@MCVERSION@]",
		dependencies = "required-after:ebwizardry@[@WIZARDRY_VERSION@,4.4);required-after:wizardryutils@[1.1.1,2.0.0);")
public class ApprenticeArcana {

	public static final String MODID = "apprenticearcana";
	public static final String NAME = "Rise of the Animagus";
	public static Logger logger;
	public static Random rand = new Random();

	// The instance of the mod that Forge uses.
	@Mod.Instance(ApprenticeArcana.MODID)
	public static ApprenticeArcana instance;

	public static Settings settings = new Settings();

	// Location of the proxy code, used by Forge.
	@SidedProxy(clientSide = "com.windanesz.apprenticearcana.client.ClientProxy", serverSide = "com.windanesz.apprenticearcana.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ItemModelRegistry.registerModForAutoItemModelRegistry(MODID);
		WizardryEnumHelper.addSpellType("NPCSPELL", "npcspell");
		logger = event.getModLog();
		proxy.registerRenderers();
		LootRegistry.preInit();
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {

		MinecraftForge.EVENT_BUS.register(instance);
		proxy.registerParticles();
		proxy.init();
		StructureWizardHouse.init();
		AAPacketHandler.initPackets();
		NetworkRegistry.INSTANCE.registerGuiHandler(this, new AAGuiHandler());

	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		BlockRegistry.registerOreDictionaryEntries();
		PlayerData.init();
		proxy.postInit();

	}

	@EventHandler
	public void serverStartup(FMLServerStartingEvent event) { }

}
