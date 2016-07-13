package com.tmtravlr.cp.init;

import com.tmtravlr.cp.block.BlockColourfulWater;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ColourfulBlocks {

	public static BlockColourfulWater cwater;
	{
		cwater = new BlockColourfulWater();
		GameRegistry.register(cwater);
	}
	public static void initModels(){
		cwater.initModel();
	}
	
}
