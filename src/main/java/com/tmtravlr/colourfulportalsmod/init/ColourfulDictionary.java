package com.tmtravlr.colourfulportalsmod.init;

import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;

public class ColourfulDictionary {
	
	public static void oreRegistration(){
    	OreDictionary.registerOre("full_water_bucket", new ItemStack(Items.WATER_BUCKET));
    	OreDictionary.registerOre("empty_colourful__water_bucket", new ItemStack(ColourfulItems.bucketColourfulWaterEmpty));
    	OreDictionary.registerOre("first_stage_colourful_water_bucket", new ItemStack(ColourfulItems.bucketColourfulWaterFirst));
    	OreDictionary.registerOre("empty_water_bucket", new ItemStack(Items.BUCKET));
    	OreDictionary.registerOre("coloured_ender_pearl", new ItemStack(ColourfulItems.enderPearlColoured));
    	OreDictionary.registerOre("exp_bottle", new ItemStack(Items.EXPERIENCE_BOTTLE));
    	OreDictionary.registerOre("full_colourful_water_bucket", new ItemStack(ColourfulItems.bucketColourfulWater));
    }
}
