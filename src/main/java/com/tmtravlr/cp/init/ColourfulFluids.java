package com.tmtravlr.cp.init;

import com.tmtravlr.cp.fluids.ColourfulFluid;

import net.minecraftforge.fluids.FluidRegistry;

public class ColourfulFluids {

	public static ColourfulFluid cfluid;
	{
		FluidRegistry.registerFluid(cfluid);
		FluidRegistry.addBucketForFluid(cfluid);
	}
	public static void dummyRegister(){
		
	}
	
}
