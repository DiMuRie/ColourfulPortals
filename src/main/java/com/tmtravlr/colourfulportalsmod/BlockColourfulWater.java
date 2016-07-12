package com.tmtravlr.colourfulportalsmod;

import java.util.Random;

import com.tmtravlr.colourfulportalsmod.init.ColourfulBlocks;

import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fml.common.registry.GameRegistry;

public class BlockColourfulWater extends BlockFluidClassic
{
	private static final boolean debug = false;

	public BlockColourfulWater()
	{
		super(ColourfulPortalsMod.colourfulFluid, Material.WATER);
		this.setDefaultState(this.blockState.getBaseState().withProperty(LEVEL, Integer.valueOf(0)));
		setUnlocalizedName("colourfulwater");
		setRegistryName("colourfulwater");
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this).setRegistryName(this.getRegistryName()));
		ColourfulPortalsMod.colourfulFluid.setBlock(this).setUnlocalizedName(getUnlocalizedName());
	}

	@Override
	public void updateTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		if ((ColourfulPortalsMod.getMeta(state) == 0) && (state.getBlock() == this) && (ColourfulPortalsMod.isFrameBlock(world.getBlockState(pos.down()).getBlock())) && (ColourfulPortalsMod.canCreatePortal(world, pos, world.getBlockState(pos.down())))) {
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
        	world.setBlockState(pos, ColourfulBlocks.colourfulWater.getDefaultState().withProperty(LEVEL, meta));
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