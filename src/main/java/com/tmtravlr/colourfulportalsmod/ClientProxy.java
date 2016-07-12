package com.tmtravlr.colourfulportalsmod;

import java.io.File;
import java.io.IOException;

import com.tmtravlr.colourfulportalsmod.init.ColourfulBlocks;
import com.tmtravlr.colourfulportalsmod.init.ColourfulItems;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class ClientProxy
  extends CommonProxy
{
  public static int renderPass;
  public static ColourfulItems ColourfulItems;
  public static ColourfulBlocks ColourfulBlocks;
  
  public void registerSounds() {}
  
  public void registerRenderers()
  {
	  Minecraft mc = Minecraft.getMinecraft();
	  mc.getBlockRendererDispatcher().getBlockModelShapes().registerBuiltInBlocks(ColourfulBlocks.colourfulWater);
	  
	  mc.getRenderItem().getItemModelMesher().register(ColourfulItems.bucketColourfulWaterEmpty, 0, new ModelResourceLocation("colourfulportalsmod:bucket_colourful_water_first", "inventory"));
	  mc.getRenderItem().getItemModelMesher().register(ColourfulItems.bucketColourfulWaterFirst, 0, new ModelResourceLocation("colourfulportalsmod:bucket_colourful_water_first", "inventory"));
	  mc.getRenderItem().getItemModelMesher().register(ColourfulItems.bucketColourfulWater, 0, new ModelResourceLocation("colourfulportalsmod:bucket_colourful_water", "inventory"));
	  mc.getRenderItem().getItemModelMesher().register(ColourfulItems.bucketColourfulWaterUnmixed, 0, new ModelResourceLocation("colourfulportalsmod:bucket_colourful_water_unmixed", "inventory"));
	  mc.getRenderItem().getItemModelMesher().register(ColourfulItems.bucketColourfulWaterPartMixed, 0, new ModelResourceLocation("colourfulportalsmod:bucket_colourful_water_unmixed", "inventory"));
	  
	  mc.getRenderItem().getItemModelMesher().register(ColourfulItems.enderPearlColoured, 0, new ModelResourceLocation("colourfulportalsmod:colourful_ender_pearl", "inventory"));
	  mc.getRenderItem().getItemModelMesher().register(ColourfulItems.enderPearlColouredReflective, 0, new ModelResourceLocation("colourfulportalsmod:colourful_ender_pearl_reflective", "inventory"));
	  
    //System.out.println("cp - Loading the rendering handler.");
    
    //RenderingRegistry.registerBlockHandler(ColourfulPortalsMod.standaloneRenderer);
  }
  
  public TextureAtlasSprite registerTexture(ResourceLocation location) {
	  return Minecraft.getMinecraft().getTextureMapBlocks().registerSprite(location);
  }
  
  public File getSaveLocation()
    throws IOException
  {
    File saveDirectory = new File(Minecraft.getMinecraft().mcDataDir, "saves/" + FMLCommonHandler.instance().getMinecraftServerInstance().getFolderName());
    saveDirectory.mkdir();
    
    return new File(saveDirectory, "colourful_portal_locations.dat");
  }
}