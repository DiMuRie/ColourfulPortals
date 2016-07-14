package com.tmtravlr.cp.block;

import java.util.List;
import java.util.Random;

import javax.annotation.Nullable;

import com.tmtravlr.cp.CPLib;
import com.tmtravlr.cp.CPLocations;
import com.tmtravlr.cp.EntityCPortalFX;
import com.tmtravlr.cp.init.ColourfulItems;
import com.tmtravlr.cp.proxy.ClientProxy;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStandaloneCP
  extends BlockColourfulPortal
{
  @SideOnly(Side.CLIENT)
  //private IIcon[] iconArray;
  private static final boolean debug = false;
  public static final double HEIGHT = 0.8D;
	protected static final AxisAlignedBB AABB_DIS = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.8D, 1D);
  
  public BlockStandaloneCP(String texture,Material material)
  {
    super(texture, material);
    setCreativeTab(ColourfulItems.cpTab);
  }
  
  public void onNeighborBlockChange(World world, int x, int y, int z, Block other) {}
  
  public int getRenderType()
  {
    return CPLib.colourfulPortalRenderId;
  }
  
  public int quantityDropped(Random rand)
  {
    return 1;
  }
  
  public boolean shouldSideBeRendered(IBlockAccess iba, BlockPos pos, EnumFacing side)
  {
    return true;
  }
  
	@Nullable
	@Override
  public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
  {
      return AABB_DIS;
  }
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
  {
		return AABB_DIS;
  }
  
  public boolean canRenderInPass(int pass)
  {
    ClientProxy.renderPass = pass;
    
    return true;
  }
  
  public int getRenderBlockPass()
  {
    return 1;
  }
  
  @SideOnly(Side.CLIENT)
  public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
  {
    super.randomDisplayTick(world, pos, state, rand);
    for (int i = 0; i < 2; i++)
    {
      float x = pos.getX() + rand.nextFloat() * 2.0F - 0.5F;
      float z = pos.getY() + rand.nextFloat() * 2.0F - 0.5F;
      float y = pos.getZ() + rand.nextFloat() + 0.5F;
      
      float xVel = rand.nextFloat() * 0.2F;
      float zVel = rand.nextFloat() * 0.2F;
      float yVel = rand.nextFloat() * 0.2F;
      if (rand.nextBoolean()) {
        xVel = -xVel;
      }
      if (rand.nextBoolean()) {
        zVel = -zVel;
      }
      Particle entityfx = new EntityCPortalFX(world, x, y, z, xVel, yVel, zVel);
      Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
    }
  }
  
  @SideOnly(Side.CLIENT)
  public void getSubBlocks(Item item, CreativeTabs creativeTabs, List list)
  {
    for (int j = 0; j < 16; j++) {
      list.add(new ItemStack(item, 1, j));
    }
  }
  
  public int damageDropped(int meta)
  {
    return meta;
  }
  
//  @SideOnly(Side.CLIENT)
//  public void registerBlockIcons(IIconRegister iconRegister)
//  {
//    super.registerBlockIcons(iconRegister);
//  }
  
//  public IIcon getIcon(int side, int meta)
//  {
//    return this.blockIcon;
//  }
  
  public ItemStack getPickBlock(RayTraceResult target, World world, BlockPos pos)
  {
    return new ItemStack(this, 1, CPLib.getMeta(world, pos));
  }
  
  public void onBlockAdded(World world, BlockPos pos, IBlockState state)
  {
    if (state.getBlock() == this) {
      if (!CPLib.canCreatePortal(world, pos, state))
      {
        dropBlockAsItem(world, pos, state, CPLib.getMeta(state));
        world.setBlockToAir(pos);
      }
      else
      {
    	  CPLib.addPortalToList(new CPLocations(pos, world.provider.getDimension(), CPLib.getShiftedCPMetadata(world, pos)));
      }
    }
  }
  
  public void breakBlock(World world, BlockPos pos, IBlockState state)
  {
    if (state.getBlock() == this) {
    	CPLib.deletePortal(new CPLocations(pos, world.provider.getDimension(), CPLib.getShiftedCPMetadata(world, pos)));
    }
  }
}