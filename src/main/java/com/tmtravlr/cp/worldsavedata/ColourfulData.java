package com.tmtravlr.cp.worldsavedata;

import java.util.Iterator;
import java.util.LinkedList;

import org.apache.commons.lang3.StringUtils;

import com.sun.org.apache.xerces.internal.xs.StringList;
import com.tmtravlr.cp.CPLocations;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class ColourfulData extends WorldSavedData {

	  private static final String DATA_NAME = "CPL[meta=" + CPLocations.portalMetadata + ", x=" + CPLocations.xPos + ", y=" + CPLocations.yPos + ", z=" + CPLocations.zPos + ", dim=" + CPLocations.dimension + "]";
	  public static final boolean IS_GLOBAL = true;
	  //BlockPos pos;
	  //int dim;
	  //int meta;
	  private final LinkedList<CPLocations> colourfulPortals = new LinkedList();
	  
	  public LinkedList<CPLocations> getCPL(){
		  return colourfulPortals;
	  }
	  
	  private CPLocations cploc;
	  World world;

	  // Required constructors
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
	public void readFromNBT(NBTTagCompound nbt) {
        //NBTTagList list = nbt.getTagList(cploc.toString(), 10);
		//NBTTagList meta = nbt.getTagList("meta=", 10);
		//NBTTagList x = nbt.getTagList("x=", 10);
		//NBTTagList y = nbt.getTagList("y=", 10);
		//NBTTagList z = nbt.getTagList("z=", 10);
		//NBTTagList dim = nbt.getTagList("dim=", 10);
        colourfulPortals.clear();
        nbt.getTagList("meta=", 10);
        nbt.getTagList("x=", 10);
        nbt.getTagList("y=", 10);
        nbt.getTagList("z=", 10);
        nbt.getTagList("dim=", 10);
        //nbt.toString("CPL[" +nbt.getTag("meta=") + ", " + nbt.getTag("x=") + ", " + nbt.getTag("y=") + ", " + nbt.getTag("z=") + ", " + nbt.getTag("dim=") + "]");
        
        colourfulPortals.forEach((cploc) -> {
        	//StringUtils.join(list);
        });

	}
	NBTTagList list = new NBTTagList();
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		//Iterator<CPLocations> iterator = this.colourfulPortals.iterator();
		//nbt.setString("cploc", CPLocations.instance.toString());
		NBTTagCompound cpl = new NBTTagCompound();
		cpl.setString("meta=", "" + CPLocations.portalMetadata);
		cpl.setString("x=", "" + CPLocations.xPos);
		cpl.setString("y=", "" + CPLocations.yPos);
		cpl.setString("z=", "" + CPLocations.zPos);
		cpl.setString("dim=", "" + CPLocations.dimension);
		list.appendTag(cpl);
		/*CPLib.colourfulPortals = new LinkedList();
		CPLib.checkForPortalChanges();
		nbt.setString("", ColourfulPortalLocation);
		markDirty();
		return nbt;*/
		return nbt;
	}

}
