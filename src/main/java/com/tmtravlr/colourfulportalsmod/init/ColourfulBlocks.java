package com.tmtravlr.colourfulportalsmod.init;

import java.util.ArrayList;
import java.util.List;

import com.tmtravlr.colourfulportalsmod.BlockColourfulPortal;
import com.tmtravlr.colourfulportalsmod.BlockColourfulWater;
import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod;
import com.tmtravlr.colourfulportalsmod.ItemBucketColourfulWater;
import com.tmtravlr.colourfulportalsmod.ItemEnderPearlColoured;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ColourfulBlocks {

	public static List<Block> types_portal = new ArrayList();
	{
		types_portal.add(Blocks.WOOL);
		types_portal.add(Blocks.STAINED_HARDENED_CLAY);
		types_portal.add(Blocks.STAINED_GLASS);
		types_portal.add(Blocks.STAINED_GLASS_PANE);
	}
	public static List<String> types_portal_names = new ArrayList();
	{
		types_portal_names.add("wool");
		types_portal_names.add("stained_hardened_clay");
		types_portal_names.add("stained_glass");
		types_portal_names.add("stained_glass_pane");
	}
	public static final String cpsufix = "_colourful_portal";
	public static Block colourfulWater;
	public static Block colourfulPortalBlock;
	
	public static void instantiateBlocks(){
		colourfulWater = new BlockColourfulWater().setDensity(ColourfulPortalsMod.colourfulFluid.getDensity()).setHardness(100.0F).setLightOpacity(3);
		colourfulPortalBlock = new BlockColourfulPortal("colourful_portal", Material.PORTAL).setBlockUnbreakable().setLightLevel(0.75F).setUnlocalizedName(types_portal_names + cpsufix).setRegistryName(types_portal_names + cpsufix);
	}
	public static void registerBlocks(){
		GameRegistry.register(colourfulWater);
		GameRegistry.register(colourfulPortalBlock);
	}
	
}
