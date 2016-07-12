package com.tmtravlr.colourfulportalsmod.init;

import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.CraftingManager;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

public final class ColourfulRecipes {

	public static void initOreRecipes(){
		for (int f = 0; f < ColourfulPortalsMod.frameBlocks.size(); f++) {
			for (int i = 0; i < 16; i++)
			{
				ItemStack frame = new ItemStack(ColourfulPortalsMod.frameBlocks.get(f), 1, i);
				ItemStack sCPStack = new ItemStack(ColourfulPortalsMod.scpBlocks.get(f), 1, i);
				addOreDictRecipe(sCPStack,
						frame,frame,frame, 
						frame, "full_colourful_water_bucket",frame,
						frame,frame,frame);
			}
		}
		addShapelessOreDictRecipe(new ItemStack(ColourfulItems.bucketColourfulWaterPartMixed, 1),
				"full_water_bucket", "empty_colourful__water_bucket", "dyeBlack", 
				"dyeRed", "dyeGreen","dyeBrown", 
				"dyeBlue", "dyeYellow", "dyeWhite");
		addShapelessOreDictRecipe(new ItemStack(ColourfulItems.bucketColourfulWaterUnmixed, 1),
				"full_water_bucket", "first_stage_colourful_water_bucket", "dyeBlack", 
				"dyeRed", "dyeGreen", "dyeBrown",
				"dyeBlue", "dyeYellow", "dyeWhite" );
		addShapelessOreDictRecipe(new ItemStack(ColourfulItems.enderPearlColoured, 1),
				"enderpearl", "dyeBlack", "dyeRed", 
				"dyeGreen", "dyeBrown", "dyeBlue", 
				"dyeYellow", "dyeWhite");
		addOreDictRecipe(new ItemStack(ColourfulItems.bucketColourfulWaterFirst, 1),
				"IGI", " I ", "   ",
				'G', "ingotGold",
				'I', "ingotIron");
		addOreDictRecipe(new ItemStack(ColourfulItems.bucketColourfulWaterFirst, 1),
				"GB", "   ", "   ",
				'G', "ingotGold",
				'B', "empty_water_bucket");
		addOreDictRecipe(new ItemStack(ColourfulItems.enderPearlColouredReflective, 1),
				" Q ", "QPQ", " Q ",
				'Q', "gemQuartz",
				'P', "coloured_ender_pearl");
		addShapelessOreDictRecipe(new ItemStack(ColourfulItems.bucketColourfulWaterFirst),
				'S', "empty_colourful__water_bucket");
		addShapelessOreDictRecipe(new ItemStack(Items.BUCKET),
				"first_stage_colourful_water_bucket");
		if(ColourfulPortalsMod.xpBottleCrafting) {
		addShapelessOreDictRecipe(new ItemStack(ColourfulItems.bucketColourfulWater),
				"exp_bottle","exp_bottle","exp_bottle","exp_bottle","exp_bottle","exp_bottle","exp_bottle","exp_bottle");
		}
	}
	private static void addOreDictRecipe(ItemStack output, Object... recipe) {
		CraftingManager.getInstance().getRecipeList().add(new ShapedOreRecipe(output, recipe));
	}
	private static void addShapelessOreDictRecipe(ItemStack output, Object... recipe) {
		CraftingManager.getInstance().getRecipeList().add(new ShapelessOreRecipe(output, recipe));
	}
	
	
}
