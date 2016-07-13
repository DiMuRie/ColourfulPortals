package com.tmtravlr.cp.proxy;

import com.tmtravlr.cp.init.ColourfulItems;

/*This is the client proxy class
 * for the Colourful Portals Mod
 * Created by DiMuRie
 */
public class ClientProxy implements ICommonProxy {
	public static int renderPass;

	@Override
	public void init() {
		ColourfulItems.initModels();
	}

}
