package com.tmtravlr.colorfulportals;

import java.util.List;
import java.util.Random;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockStandaloneCP extends BlockColorfulPortal {

	@SideOnly(Side.CLIENT)
	  //private IIcon[] iconArray;
	  private static final boolean debug = false;
	  public static final double HEIGHT = 0.8D;
	  
	  public BlockStandaloneCP(Material material)
	  {
	    super(material);
	    setCreativeTab(ColorfulPortalsItemLoader.tabCP);
	  }
	  
	  public void onNeighborBlockChange(World world, int x, int y, int z, Block other) {}
	  
	  @SideOnly(Side.CLIENT)
	    public BlockRenderLayer getBlockLayer()
	    {
	        return BlockRenderLayer.TRANSLUCENT;
	    }
	  public EnumBlockRenderType getRenderType(IBlockState state)
	    {
	        return EnumBlockRenderType.MODEL;
	    }
	  
	  public int quantityDropped(Random rand)
	  {
	    return 1;
	  }
	  
	  public boolean shouldSideBeRendered(IBlockAccess iba, BlockPos pos, EnumFacing side)
	  {
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
	  public void onBlockAdded(World world, BlockPos pos, IBlockState state)
	  {
	    if (state.getBlock() == this) {
	      if (!ColorfulPortals.canCreatePortal(world, pos, state))
	      {
	        dropBlockAsItem(world, pos, state, ColorfulPortals.getMeta(state));
	        world.setBlockToAir(pos);
	      }
	      else
	      {
	        ColorfulPortals.addPortalToList(new ColorfulPortals.ColourfulPortalLocation(pos, world.provider.getDimensionType(), ColorfulPortals.getShiftedCPMetadata(world, pos)));
	      }
	    }
	  }
	  
	  public void breakBlock(World world, BlockPos pos, IBlockState state)
	  {
	    if (state.getBlock() == this) {
	      ColorfulPortals.deletePortal(new ColorfulPortals.ColourfulPortalLocation(pos, world.provider.getDimensionType(), ColorfulPortals.getShiftedCPMetadata(world, pos)));
	    }
	  }
	
}
