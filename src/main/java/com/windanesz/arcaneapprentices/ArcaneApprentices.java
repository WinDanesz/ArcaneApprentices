package com.windanesz.arcaneapprentices;

import com.windanesz.arcaneapprentices.client.gui.AAGuiHandler;
import com.windanesz.arcaneapprentices.command.CommandResetApprenticeData;
import com.windanesz.arcaneapprentices.data.PlayerData;
import com.windanesz.arcaneapprentices.data.Talent;
import com.windanesz.arcaneapprentices.packet.AAPacketHandler;
import com.windanesz.arcaneapprentices.registry.AAAdvancementTriggers;
import com.windanesz.arcaneapprentices.registry.BlockRegistry;
import com.windanesz.arcaneapprentices.registry.LootRegistry;
import com.windanesz.arcaneapprentices.village.StructureWizardHouse;
import com.windanesz.wizardryutils.registry.ItemModelRegistry;
import electroblob.wizardry.api.WizardryEnumHelper;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import org.apache.logging.log4j.Logger;

import java.util.Random;

@Mod(modid = ArcaneApprentices.MODID, name = ArcaneApprentices.NAME, version = "@VERSION@", acceptedMinecraftVersions = "[@MCVERSION@]",
		dependencies = "required-after:ebwizardry@[@WIZARDRY_VERSION@,4.4);required-after:wizardryutils@[1.1.1,2.0.0);")
public class ArcaneApprentices {

	public static final String MODID = "arcaneapprentices";
	public static final String NAME = "Rise of the Animagus";
	public static Logger logger;
	public static Random rand = new Random();

	// The instance of the mod that Forge uses.
	@Mod.Instance(ArcaneApprentices.MODID)
	public static ArcaneApprentices instance;

	public static Settings settings = new Settings();

	// Location of the proxy code, used by Forge.
	@SidedProxy(clientSide = "com.windanesz.arcaneapprentices.client.ClientProxy", serverSide = "com.windanesz.arcaneapprentices.CommonProxy")
	public static CommonProxy proxy;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		ItemModelRegistry.registerModForAutoItemModelRegistry(MODID);
		WizardryEnumHelper.addSpellType("NPCSPELL", "npcspell");
		logger = event.getModLog();
		proxy.registerRenderers();
		LootRegistry.preInit();
		AAAdvancementTriggers.register();
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
		Talent.TalentSettings.init();
	}

	@EventHandler
	public void serverStarting(FMLServerStartingEvent event) {
		event.registerServerCommand(new CommandResetApprenticeData());

	}

}
