package com.tmtravlr.cp;

import com.tmtravlr.cp.init.ColourfulFluids;
import com.tmtravlr.cp.init.ColourfulItems;
import com.tmtravlr.cp.proxy.ICommonProxy;

import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = CPLib.MODID, name = CPLib.NAME, version = CPLib.VERSION/*, acceptedMinecraftVersions = ""*/)
public class CP {
	
	{
		FluidRegistry.enableUniversalBucket();
	}
	@Instance
	public static CP cpInstance;
	
	@SidedProxy(clientSide = CPLib.CLIENT_PROXY_CLASS, serverSide = CPLib.SERVER_PROXY_CLASS)
	public static ICommonProxy proxy;
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent e){
		System.out.println(CPLib.MODID + " preinitialized");
		ColourfulItems.init();
		ColourfulItems.register();
		ColourfulFluids.dummyRegister();
	}
	
	@EventHandler
	public void init(FMLInitializationEvent e){
		System.out.println(CPLib.MODID + " initialized");
		proxy.init();
	}
	@EventHandler
	public void postInit(FMLPostInitializationEvent e){
		System.out.println(CPLib.MODID + " postinitialized");
	}
	
}
