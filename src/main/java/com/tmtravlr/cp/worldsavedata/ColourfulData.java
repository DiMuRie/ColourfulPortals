package com.tmtravlr.cp.worldsavedata;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.sun.org.apache.xerces.internal.xs.StringList;
import com.tmtravlr.cp.CPLocations;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class ColourfulData extends WorldSavedData {

	  private final static String DATA_NAME = "CPL[]";
	  public final static boolean IS_GLOBAL = true;
	  //BlockPos pos;
	  //int dim;
	  //int meta;
	  private final LinkedList<CPLocations> colourfulPortals = new LinkedList();
	  private NBTTagList nbtag = new NBTTagList();
	  private CPLocations CPL;
	  
	  public LinkedList<CPLocations> getCPL(){
		  return colourfulPortals;
	  }

	  public ColourfulData() {
	    super(DATA_NAME);
	  }
	  public ColourfulData(String s) {
	    super(s);
	  }
	
	  public static ColourfulData get(World world) {
		  // The IS_GLOBAL constant is there for clarity, and should be simplified into the right branch.
		  MapStorage storage = IS_GLOBAL ? world.getMapStorage() : world.getPerWorldStorage();
		  //ColourfulData instance = (ColourfulData) storage.loadData(ColourfulData.class, DATA_NAME);
		  ColourfulData instance = (ColourfulData) storage.getOrLoadData(ColourfulData.class, DATA_NAME);
		  
		  if (instance == null) {
		    instance = new ColourfulData();
		    storage.setData(DATA_NAME, instance);
		  }
		  return instance;
		}
	  
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		CPL.portalMetadata = compound.getInteger("metadata");
		CPL.xPos = compound.getInteger("posX");
		CPL.yPos = compound.getInteger("posY");
		CPL.zPos = compound.getInteger("posZ");
		CPL.dimension = compound.getInteger("dimension");
	}
	NBTTagCompound ntag = new NBTTagCompound();
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		System.out.println("\n==> Iterator Example...");
		Iterator<CPLocations> cplIterator = ((List) nbtag).iterator();
		while (cplIterator.hasNext()) {
			//System.out.println(cplIterator.next());
		}
		compound.setInteger("metadata", CPL.portalMetadata);
		compound.setInteger("posX", CPL.xPos);
        compound.setInteger("posY", CPL.yPos);
        compound.setInteger("posZ", CPL.zPos);
        compound.setInteger("dimension", CPL.dimension);
        nbtag.appendTag(compound);
		return compound;
	}

}
