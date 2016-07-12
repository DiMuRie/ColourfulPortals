package com.tmtravlr.colourfulportalsmod.init;

import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod;
import com.tmtravlr.colourfulportalsmod.ItemBucketColourfulWater;
import com.tmtravlr.colourfulportalsmod.ItemColourfulBucketFull;
import com.tmtravlr.colourfulportalsmod.ItemEnderPearlColoured;

import net.minecraft.item.Item;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class ColourfulItems {

	public static Item bucketColourfulWaterEmpty;
	public static Item bucketColourfulWater;
	public static Item bucketColourfulWaterUnmixed;
	public static Item bucketColourfulWaterPartMixed;
	public static Item bucketColourfulWaterFirst;
	public static Item enderPearlColoured;
	public static Item enderPearlColouredReflective;
	public static Item colourfulBucketFull;
	
	public static void instantiateItems(){
		bucketColourfulWaterEmpty = new ItemBucketColourfulWater(true, true, false).setUnlocalizedName("bucket_colourful_water_empty").setRegistryName("bucket_colourful_water_empty");
		bucketColourfulWater = new ItemBucketColourfulWater(true, true, true).setUnlocalizedName("bucket_colourful_water").setRegistryName("bucket_colourful_water");
		bucketColourfulWaterUnmixed = new ItemBucketColourfulWater(false, false, true).setUnlocalizedName("bucket_colourful_water_unmixed").setRegistryName("bucket_colourful_water_unmixed");
		bucketColourfulWaterPartMixed = new ItemBucketColourfulWater(true, false, true).setUnlocalizedName("bucket_colourful_water_part_mixed").setRegistryName("bucket_colourful_water_part_mixed");
		bucketColourfulWaterFirst = new ItemBucketColourfulWater(false, true, false).setUnlocalizedName("bucket_colourful_water_first").setRegistryName("bucket_colourful_water_first");
		enderPearlColoured = new ItemEnderPearlColoured(false).setRegistryName("colourful_ender_pearl");
		enderPearlColouredReflective = new ItemEnderPearlColoured(true).setRegistryName("colourful_ender_pearl_reflective");
		colourfulBucketFull = new ItemColourfulBucketFull(true, true, true).setUnlocalizedName("bucket_colourful_water_full").setRegistryName("bucket_colourful_water_full");
	}
	public static void registerItems(){
		GameRegistry.register(bucketColourfulWaterEmpty);
		GameRegistry.register(bucketColourfulWater);
		GameRegistry.register(bucketColourfulWaterUnmixed);
		GameRegistry.register(bucketColourfulWaterPartMixed);
		GameRegistry.register(bucketColourfulWaterFirst);
		GameRegistry.register(enderPearlColoured);
		GameRegistry.register(enderPearlColouredReflective);
		GameRegistry.register(colourfulBucketFull);
	}
	
}
