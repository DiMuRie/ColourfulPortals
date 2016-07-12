package com.tmtravlr.colourfulportalsmod.LT;

import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod;
import com.tmtravlr.colourfulportalsmod.init.ColourfulItems;

import net.minecraft.world.storage.loot.LootEntryItem;
import net.minecraft.world.storage.loot.LootPool;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraft.world.storage.loot.RandomValueRange;
import net.minecraft.world.storage.loot.conditions.LootCondition;
import net.minecraft.world.storage.loot.functions.LootFunction;
import net.minecraft.world.storage.loot.functions.SetDamage;
import net.minecraftforge.event.LootTableLoadEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class LTBucketEmpty {

	public static ColourfulItems ColourfulItems;
	
	@SubscribeEvent
	public void onLootTablesLoaded(LootTableLoadEvent event) {
	 
	    if (event.getName().equals(LootTableList.CHESTS_DESERT_PYRAMID)) {
	 
	        final LootPool pool2 = event.getTable().getPool("pool2");
	 
	        if (pool2 != null) {
	 
	            // pool2.addEntry(new LootEntryItem(ITEM, WEIGHT, QUALITY, FUNCTIONS, CONDITIONS, NAME));
	            pool2.addEntry(new LootEntryItem(ColourfulItems.bucketColourfulWaterEmpty, 10, 0, new LootFunction[] {new SetDamage(new LootCondition[0], new RandomValueRange(50, 50))}, new LootCondition[0], "colourfulportalsmod:LTPE"));
	        }
	    }
	    if (event.getName().equals(LootTableList.CHESTS_JUNGLE_TEMPLE)) {
	   	 
	        final LootPool pool2 = event.getTable().getPool("pool2");
	 
	        if (pool2 != null) {
	 
	            // pool2.addEntry(new LootEntryItem(ITEM, WEIGHT, QUALITY, FUNCTIONS, CONDITIONS, NAME));
	            pool2.addEntry(new LootEntryItem(ColourfulItems.bucketColourfulWaterEmpty, 10, 0, new LootFunction[] {new SetDamage(new LootCondition[0], new RandomValueRange(50, 50))}, new LootCondition[0], "colourfulportalsmod:bed:LTTE"));
	        }
	    }
	    if (event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CROSSING)) {
	   	 
	        final LootPool pool2 = event.getTable().getPool("pool2");
	 
	        if (pool2 != null) {
	 
	            // pool2.addEntry(new LootEntryItem(ITEM, WEIGHT, QUALITY, FUNCTIONS, CONDITIONS, NAME));
	            pool2.addEntry(new LootEntryItem(ColourfulItems.bucketColourfulWater, 10, 0, new LootFunction[] {new SetDamage(new LootCondition[0], new RandomValueRange(50, 50))}, new LootCondition[0], "colourfulportalsmod:bed:LTSCE"));
	        }
	    }
	    if (event.getName().equals(LootTableList.CHESTS_STRONGHOLD_CORRIDOR)) {
	   	 
	        final LootPool pool2 = event.getTable().getPool("pool2");
	 
	        if (pool2 != null) {
	 
	            // pool2.addEntry(new LootEntryItem(ITEM, WEIGHT, QUALITY, FUNCTIONS, CONDITIONS, NAME));
	            pool2.addEntry(new LootEntryItem(ColourfulItems.bucketColourfulWater, 10, 0, new LootFunction[] {new SetDamage(new LootCondition[0], new RandomValueRange(50, 50))}, new LootCondition[0], "colourfulportalsmod:bed:LTSCCE"));
	        }
	    }
	    if (event.getName().equals(LootTableList.CHESTS_SIMPLE_DUNGEON)) {
	   	 
	        final LootPool pool2 = event.getTable().getPool("pool2");
	 
	        if (pool2 != null) {
	 
	            // pool2.addEntry(new LootEntryItem(ITEM, WEIGHT, QUALITY, FUNCTIONS, CONDITIONS, NAME));
	            pool2.addEntry(new LootEntryItem(ColourfulItems.bucketColourfulWater, 10, 0, new LootFunction[] {new SetDamage(new LootCondition[0], new RandomValueRange(50, 50))}, new LootCondition[0], "colourfulportalsmod:bed:LTSDE"));
	        }
	    }
	}
	
}
