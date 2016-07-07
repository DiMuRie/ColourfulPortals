package com.tmtravlr.colourfulportalsmod;

import java.io.File;
import java.io.IOException;
import java.nio.file.CopyOption;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class CommonProxy
{
  protected static final boolean debug = false;
  
  public void registerSounds() {}
  
  public void registerRenderers() {}
  
  public TextureAtlasSprite registerTexture(ResourceLocation location) {
	  return null;
  }
  
  public void registerEventHandlers()
  {
    System.out.println("cp - Registering Event Handlers.");
    
    FMLCommonHandler.instance().bus().register(new ServerTickEvents());
  }
  
  public File getSaveLocation()
    throws IOException
  {
    File saveDirectory = new File(MinecraftServer.getServer().getFolderName());
    saveDirectory.mkdir();
    
    File portalLocations = new File(saveDirectory, "colourful_portal_locations.dat");
    if (!portalLocations.exists())
    {
      File oldDirectory = new File(MinecraftServer.getServer().getFile("saves"), MinecraftServer.getServer().getFolderName());
      oldDirectory.mkdir();
      File oldLocations = new File(oldDirectory, "colourful_portal_locations.dat");
      if (oldLocations.exists()) {
        Files.copy(oldLocations.toPath(), portalLocations.toPath(), new CopyOption[] { StandardCopyOption.REPLACE_EXISTING });
      }
    }
    return portalLocations;
  }
}