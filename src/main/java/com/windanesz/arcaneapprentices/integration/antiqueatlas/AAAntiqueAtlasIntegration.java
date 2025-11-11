package com.windanesz.arcaneapprentices.integration.antiqueatlas;

import com.windanesz.arcaneapprentices.ArcaneApprentices;
import com.windanesz.arcaneapprentices.Settings;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.api.AtlasAPI;
import hunternif.mc.atlas.marker.GlobalMarkersData;
import hunternif.mc.atlas.marker.Marker;
import hunternif.mc.atlas.registry.MarkerType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * This class handles all of Arcane Apprentices' integration with the <i>Antique Atlas</i> mod.
 * This class contains only the code that requires Antique Atlas to be loaded in order to run.
 * Conversely, all code that requires Antique Atlas to be loaded is located within this class
 * or another class in the package {@code com.windanesz.arcaneapprentices.integration.antiqueatlas}.
 *
 * @author WinDanesz
 */
@Mod.EventBusSubscriber
public class AAAntiqueAtlasIntegration {

	public static final String ANTIQUE_ATLAS_MOD_ID = "antiqueatlas";

	private static final ResourceLocation SCHOOL_MARKER = new ResourceLocation(ArcaneApprentices.MODID, "school_marker");

	private static boolean antiqueAtlasLoaded;

	public static void init(){
		antiqueAtlasLoaded = Loader.isModLoaded(ANTIQUE_ATLAS_MOD_ID);
		ArcaneApprentices.proxy.registerAtlasMarkers(); // Needs routing through the proxies to make sure it's only client-side
	}

	public static boolean enabled(){
		return Settings.generalSettings.antique_atlas_integration && antiqueAtlasLoaded;
	}

	/** Places a global school marker in all antique atlases at the given coordinates in the given world if
	 * the setting is enabled. Server side only! */
	public static void markSchool(World world, int x, int z){
		if(enabled() && Settings.generalSettings.school_map_markers){
			AtlasAPI.getMarkerAPI().putGlobalMarker(world, false, SCHOOL_MARKER.toString(), "integration.antiqueatlas.marker." + SCHOOL_MARKER.toString().replace(':', '.'), x, z);
		}
	}

	/** Registers the marker icons with Antique Atlas. Client side only! */
	public static void registerMarkers(){

		if(!enabled()) return;

		AtlasAPI.getMarkerAPI().registerMarker(new MarkerType(SCHOOL_MARKER, new ResourceLocation(ArcaneApprentices.MODID, "textures/integration/antiqueatlas/school_marker.png")));

	}

	@SubscribeEvent
	public static void onWorldLoadEvent(WorldEvent.Load event){

		if(!enabled()) return;

		// Backwards compatibility for existing markers using the old translation key format (with colons)
		GlobalMarkersData data = AntiqueAtlasMod.globalMarkersData.getData();
		for(Marker marker : data.getMarkersInDimension(event.getWorld().provider.getDimension())){
			if(marker.getLabel().contains(":")){
				// Remove old-format markers and replace them with new ones
				data.removeMarker(marker.getId());
				AtlasAPI.getMarkerAPI().putGlobalMarker(event.getWorld(), marker.isVisibleAhead(), marker.getType(),
						marker.getLabel().replace(':', '.'), marker.getX(), marker.getZ());
			}
		}
	}

}
