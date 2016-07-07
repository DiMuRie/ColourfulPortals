package com.tmtravlr.colorfulportals;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ColorfulPortalsItemLoader {
	public static final CreativeTabs tabCP = new CreativeTabs("cp")
	{
		@Override public Item getTabIconItem() {
			return ColorfulPortalsItemLoader.BucketColourfulWater;
		}
	};
	public static BucketColourfulWater BucketColourfulWater;
	
	public static void init() {
		BucketColourfulWater = new BucketColourfulWater();
		}
		@SideOnly(Side.CLIENT)
		public static void registerModels()
		{
			BucketColourfulWater.initModel();
		}
}
