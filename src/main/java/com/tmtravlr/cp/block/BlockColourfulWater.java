package com.tmtravlr.cp.block;

import java.util.Random;

import com.tmtravlr.cp.CPLib;
import com.tmtravlr.cp.init.ColourfulBlocks;
import com.tmtravlr.cp.init.ColourfulFluids;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockColourfulWater extends BlockFluidClassic
{
	private static final boolean debug = false;

	public BlockColourfulWater()
	{
		super(ColourfulFluids.cfluid, Material.WATER);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
		setUnlocalizedName("colourfulwater");
		setRegistryName("colourfulwater");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
		ColourfulFluids.cfluid.setBlock(this).setUnlocalizedName(getUnlocalizedName());
	}
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
	
	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if ((CPLib.getMeta(state) == 0) && (state.getBlock() == this) && (CPLib.isFrameBlock(world.getBlockState(pos.down()).getBlock())) && (CPLib.canCreatePortal(world, pos, world.getBlockState(pos.down())))) {
			BlockColourfulPortal.tryToCreatePortal(world, pos);
		}
		else {
			super.updateTick(world, pos, state, rand);
		}
	}
	public static ColourfulBlocks ColourfulBlocks;
	@Override
	protected void flowIntoBlock(World world, BlockPos pos, int meta)
    {
        if (meta < 0) return;
        if (displaceIfPossible(world, pos))
        {
        	world.setBlockState(pos, ColourfulBlocks.cwater.getDefaultState().withProperty(LEVEL, meta));
        }
    }

	@Override
	public boolean displaceIfPossible(World world, BlockPos pos)
	{
		IBlockState state = world.getBlockState(pos);
		if (world.getBlockState(pos).getBlock().getMaterial(state) == Material.WATER) {
			return false;
		}
		return super.displaceIfPossible(world, pos);
	}
}