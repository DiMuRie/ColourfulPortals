package com.tmtravlr.cp.worldsavedata;

import com.tmtravlr.cp.CPLib;
import com.tmtravlr.cp.CPLib.ColourfulPortalLocation;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;

public class ColourfulData extends WorldSavedData {

	  private static final String DATA_NAME = "CPL[meta=" + CPLib.ColourfulPortalLocation.portalMetadata + ", x=" + CPLib.ColourfulPortalLocation.xPos + ", y=" + CPLib.ColourfulPortalLocation.yPos + ", z=" + CPLib.ColourfulPortalLocation.zPos + ", dim=" + CPLib.ColourfulPortalLocation.dimension + "]";
	  public static boolean IS_GLOBAL = true;
	  BlockPos pos;
	  int dim;
	  int meta;
	  CPLib.ColourfulPortalLocation cploc = new ColourfulPortalLocation(pos, dim, meta);
	  String ColourfulPortalLocation = "CPL[meta=" + CPLib.ColourfulPortalLocation.portalMetadata + ", x=" + CPLib.ColourfulPortalLocation.xPos + ", y=" + CPLib.ColourfulPortalLocation.yPos + ", z=" + CPLib.ColourfulPortalLocation.zPos + ", dim=" + CPLib.ColourfulPortalLocation.dimension + "]";

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
        NBTTagList list = nbt.getTagList(cploc.toString(), 10);
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
		NBTTagList list = new NBTTagList();
        nbt.setString("cploc", cploc.toString());
        if(!nbt.getString("cploc").isEmpty()){
        	nbt.setString("cploc1", cploc.toString());
        }
		/*CPLib.colourfulPortals = new LinkedList();
		CPLib.checkForPortalChanges();
		nbt.setString("", ColourfulPortalLocation);
		markDirty();
		return nbt;*/
		return nbt;
	}

}
