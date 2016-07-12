package com.tmtravlr.colourfulportalsmod.init;

import java.io.File;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;

import com.tmtravlr.colourfulportalsmod.init.MezzLog;

import com.sun.org.apache.bcel.internal.Constants;
import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ColourfulConfig {

	public static FMLPreInitializationEvent event;
	private static File cpConfigurationDir;
	
	
	public static void config(){
		Configuration config=new Configuration();
	//Configuration config = new Configuration(event.getSuggestedConfigurationFile());
	config.load();


	ColourfulPortalsMod.maxPortalGenerationDistance = config.getInt("Random Portal Generation Max Distance", "other", 3000, 0, 30000000, "Maximum distance away from you a randomized portal will generate.");
	ColourfulPortalsMod.maxPortalsPerType = config.getInt("Maximum Number of Portals per Type (Colour and Material)", "other", -1, -1, Integer.MAX_VALUE, "Maximum number of portals you can make per type. -1 means unlimited.");
	ColourfulPortalsMod.maxPortalSizeCheck = config.getInt("Maximum Portal Size (Make Bigger for Larger Portals)", "other", 16, 1, Integer.MAX_VALUE, "Limit on the maximum size of portal to prevent lag from accidentally creating massive portals.\nThe portal will create up to this number of blocks away from where you place the colourful water.");
	ColourfulPortalsMod.xpLevelMixingCost = config.getInt("Number of XP Levels Needed to Mix Colourful Water", "other", 5, 0, Integer.MAX_VALUE, "Levels of XP you need to mix the colourful water from a bucket of dyes.");
	ColourfulPortalsMod.xpLevelRemixingCost = config.getInt("Number of XP Levels Needed to Re-Mix Colourful Water", "other", 2, 0, Integer.MAX_VALUE, "Levels of XP that you need to mix the partially enchanted bucket of dyes.");
	ColourfulPortalsMod.xpBottleCrafting = config.getBoolean("Allow crafting of colourful water with XP bottles (for automation)", "other", false, "Adds a crafting recipe for the colourful water using XP bottles.");
	//addColourfulWaterToDungeonChests = config.getBoolean("Add Buckets of Colourful Water to Dungeon Chests?", "other", true, "If set to true, full and empty buckets of colourful water will occasionally spawn in chests.");
	if (ColourfulPortalsMod.xpLevelRemixingCost > ColourfulPortalsMod.xpLevelMixingCost) {
		ColourfulPortalsMod.xpLevelRemixingCost = ColourfulPortalsMod.xpLevelMixingCost;
	}
	config.addCustomCategoryComment("random_destination_blacklist", "If set to true, random destination portals with random dimensions\nwill not generate in any of the dimensions in this list. They can\nstill be created with same-dimension random destinations or placed\nmanually. Defaults to true. Takes precedence over the whitelist.");

	config.addCustomCategoryComment("random_destination_whitelist", "If set to true, random destination portals with random dimensions\nwill only generate in these dimensions. Ones with the same dimension\ncan still generate elsewhere. Defaults to false.");

	config.addCustomCategoryComment("dimension_blacklist", "If set to true, portals cannot be created in these dimensions\nat all, whether framed, single block, or 'random destination'\nportals. Takes precedence over the whitelist. Defaults to false.");

	config.addCustomCategoryComment("dimension_whitelist", "If set to true, portals can ONLY be created in the given dimensions,\nwhether framed, single block, or 'random destination' portals.\nDefaults to false.");

	int[] defaultBlackList = { 1 };
	int[] defaultWhiteList = { 0, -1 };
	int[] fullDefaultList = { 0, 1, -1 };
	int[] emptyList = new int[0];

	ColourfulPortalsMod.useDestinationBlackList = config.getBoolean("Use this Blacklist?", "random_destination_blacklist", true, "");
	ColourfulPortalsMod.useDestinationWhiteList = config.getBoolean("Use this Whitelist?", "random_destination_whitelist", false, "");
	ColourfulPortalsMod.destinationBlackList = config.get("random_destination_blacklist", "List of Blacklisted Dimensions for Random Generation", defaultBlackList).getIntList();
	ColourfulPortalsMod.destinationWhiteList = config.get("random_destination_whitelist", "List of Whitelisted Dimensions for Random Generation", defaultWhiteList).getIntList();
	
	ColourfulPortalsMod.useDimensionBlackList = config.getBoolean("Use this Blacklist?", "dimension_blacklist", false, "");
	ColourfulPortalsMod.useDimensionWhiteList = config.getBoolean("Use this Whitelist?", "dimension_whitelist", false, "");
	ColourfulPortalsMod.dimensionBlackList = config.get("dimension_blacklist", "List of Blacklisted Dimensions for all Portals", emptyList).getIntList();
	ColourfulPortalsMod.dimensionWhiteList = config.get("dimension_whitelist", "List of Whitelisted Dimensions for all Portals", fullDefaultList).getIntList();

	config.addCustomCategoryComment("portal_frame_types", "Blocks that can be used to make portals out of.\nThey should have 16 metadata types that represent\ncolours in the same way as wool.");

	String[] defaultPortalTypes = { "wool", "stained_hardened_clay", "stained_glass" };
	//No support for custom portal types at the moment!
	ColourfulPortalsMod.frameBlockNames = defaultPortalTypes;//= config.get("portal_frame_types", "Portal Frame Blocks", defaultPortalTypes).getStringList();
	
	config.save();
	
	}
}
