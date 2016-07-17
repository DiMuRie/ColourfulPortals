package com.tmtravlr.cp;

import net.minecraft.util.math.BlockPos;

public class CPLocation {

	public BlockPos pos;
	public int dimension;
	public int portalMetadata;

	public CPLocation(int xPos, int yPos, int zPos, int dimension, int portalMetadata) {
		this(new BlockPos(xPos, yPos, zPos), dimension, portalMetadata);
	}

	public CPLocation(BlockPos pos, int dimension, int portalMetadata) {
		this.pos = pos;
		this.dimension = dimension;
		this.portalMetadata = portalMetadata;
	}

	@Override
	public boolean equals(Object o) {
		if ((o == null) || (!(o instanceof CPLocation))) {
			return false;
		}

		final CPLocation other = (CPLocation) o;
		return (this.pos.equals(other.pos)) && (this.dimension == other.dimension) && (this.portalMetadata == other.portalMetadata);
	}

	@Override
	public String toString() {
		return "CPL[meta=" + this.portalMetadata + ", pos=" + this.pos.toString() + ", dim=" + this.dimension + "]";
	}
}
