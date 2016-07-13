package com.tmtravlr.cp.init;

import com.tmtravlr.cp.item.RainbowTemplate;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ColourfulItems {
	
	public static CreativeTabs cpTab = new CreativeTabs("colourfulPortals")
	{
		public Item getTabIconItem()
		{
			return rainbowTemplate;
		}
	};
	
	public static Item rainbowTemplate;
	
	public static void init(){
		rainbowTemplate = new RainbowTemplate();
	}
	
	public static void register(){
		GameRegistry.register(rainbowTemplate);
	}
	
	public static void initModels(){
		initNormalModel(rainbowTemplate);
	}
	
	private static void initNormalModel(Item item){
		Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
	}
	
}
