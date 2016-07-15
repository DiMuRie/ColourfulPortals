package com.tmtravlr.cp;

import java.io.Serializable;
import java.util.LinkedList;

import net.minecraft.util.math.BlockPos;

public class CPLocations implements Serializable {
	
	public int xPos;
	public int yPos;
	public int zPos;
	public int dimension;
	public int portalMetadata;


	public CPLocations(BlockPos pos, int dim, int meta)
	{
		this.xPos = pos.getX();
		this.yPos = pos.getY();
		this.zPos = pos.getZ();
		this.dimension = dim;
		this.portalMetadata = meta;
	}

	public boolean equals(Object o)
	{
		if ((o == null) || (!(o instanceof CPLocations))) {
			return false;
		}
		CPLocations other = (CPLocations)o;
		return (this.xPos == other.xPos) && (this.yPos == other.yPos) && (this.zPos == other.zPos) && (this.dimension == other.dimension) && (this.portalMetadata == other.portalMetadata);
	}

	public String toString()
	{
		return "CPL[meta=" + this.portalMetadata + ", x=" + this.xPos + ", y=" + this.yPos + ", z=" + this.zPos + ", dim=" + this.dimension + "]";
	}
}
