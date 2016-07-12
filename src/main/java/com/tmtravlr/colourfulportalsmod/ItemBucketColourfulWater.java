package com.tmtravlr.colourfulportalsmod;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;
import com.tmtravlr.colourfulportalsmod.init.ColourfulBlocks;
import com.tmtravlr.colourfulportalsmod.init.ColourfulItems;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemBucketColourfulWater
  extends Item
{
  private boolean isEnchanted;
  private boolean isMixed;
  private boolean isFull;
  private static final boolean debug = false;
  public static ColourfulBlocks ColourfulBlocks;
  
  public ItemBucketColourfulWater(boolean setIsEnchanted, boolean setIsMixed, boolean setIsFull)
  {
    this.isEnchanted = setIsEnchanted;
    this.isMixed = setIsMixed;
    this.isFull = setIsFull;
    this.maxStackSize = (this.isFull ? 1 : 16);
    setCreativeTab(ColourfulPortalsMod.cpTab);
  }


  @SideOnly(Side.CLIENT)
  public boolean hasEffect(ItemStack stack)
  {
    return this.isEnchanted;
  }
  
  public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean notSure)
  {
    if (!this.isMixed)
    {
      list.add(ChatFormatting.ITALIC + I18n.translateToLocal("item.bucketColourfulWaterUnmixed.info.1"));
      list.add(ChatFormatting.ITALIC + String.valueOf(this.isEnchanted ? ColourfulPortalsMod.xpLevelRemixingCost : ColourfulPortalsMod.xpLevelMixingCost) + I18n.translateToLocal("item.bucketColourfulWaterUnmixed.info.2"));
    }
    if ((this.isMixed) && (!this.isEnchanted) && (!this.isFull)) {
      list.add(ChatFormatting.ITALIC + I18n.translateToLocal("item.bucketColourfulWaterFirst.info.1"));
    }
    if (this.isEnchanted) {
      list.add(I18n.translateToLocal("item.bucketColourfulWater.info.enchant"));
    }
  }
  
//  @SideOnly(Side.CLIENT)
//  public void registerIcons(IIconRegister iconRegister)
//  {
//    if (!this.isFull) {
//      this.itemIcon = iconRegister.registerIcon("colourfulportalsmod:bucketColourfulWaterFirst");
//    } else if (!this.isMixed) {
//      this.itemIcon = iconRegister.registerIcon("colourfulportalsmod:bucketColourfulWaterUnmixed");
//    } else {
//      this.itemIcon = iconRegister.registerIcon("colourfulportalsmod:bucketColourfulWater");
//    }
//  }
  
  public ItemStack onItemRightClick(ItemStack itemStack, World world, EntityPlayer player)
  {
    if ((this.isMixed) && (this.isEnchanted))
    {
      boolean empty = !this.isFull;
      RayTraceResult movingobjectposition = rayTrace(world, player, empty);
      if (movingobjectposition == null) {
        return itemStack;
      }
      
      if (movingobjectposition.typeOfHit == RayTraceResult.Type.BLOCK)
      {
    	  BlockPos pos = movingobjectposition.getBlockPos();
    	  
        if (!world.canMineBlockBody(player, pos)) {
          return itemStack;
        }
        if (empty)
        {
          if (!player.canPlayerEdit(pos, movingobjectposition.sideHit, itemStack)) {
            return itemStack;
          }
          IBlockState state = world.getBlockState(pos);
          Block block = state.getBlock();
          int l = block.getMetaFromState(state);
          if (block == ColourfulBlocks.colourfulWater)
          {
            world.setBlockToAir(pos);
            if (player.capabilities.isCreativeMode) {
              return itemStack;
            }
            if (--itemStack.stackSize <= 0) {
              return new ItemStack(ColourfulItems.bucketColourfulWater);
            }
            if (!player.inventory.addItemStackToInventory(new ItemStack(ColourfulItems.bucketColourfulWater))) {
              //player.dropPlayerItemWithRandomChoice(new ItemStack(ColourfulPortalsMod.bucketColourfulWater, 1, 0), false);
              player.dropItem(new ItemStack(ColourfulItems.bucketColourfulWater, 1, 0), false);
            }
            return itemStack;
          }
        }
        else
        {
          if (!this.isFull) {
            return new ItemStack(ColourfulItems.bucketColourfulWaterEmpty);
          }
          
          pos = pos.offset(movingobjectposition.sideHit);
          
          if (!player.canPlayerEdit(pos, movingobjectposition.sideHit, itemStack)) {
            return itemStack;
          }
          if ((tryPlaceContainedColourfulLiquid(world, pos)) && (!player.capabilities.isCreativeMode)) {
            return new ItemStack(ColourfulItems.bucketColourfulWaterEmpty);
          }
        }
      }
      return itemStack;
    }
    if (!this.isMixed)
    {
      int xpRequirement = ColourfulPortalsMod.xpLevelMixingCost;
      if (this.isEnchanted) {
        xpRequirement = ColourfulPortalsMod.xpLevelRemixingCost;
      }
      if (player.experienceLevel >= xpRequirement)
      {
        player.addExperienceLevel(-xpRequirement);
        
        ItemStack toReturn = new ItemStack(this.isMixed ? ColourfulItems.bucketColourfulWaterEmpty : ColourfulItems.bucketColourfulWater);
        if (--itemStack.stackSize <= 0) {
          return toReturn;
        }
        if (!player.inventory.addItemStackToInventory(toReturn)) {
          player.dropItem(toReturn, false);
        }
        return itemStack;
      }
      return itemStack;
    }
    return itemStack;
  }
  
  public boolean tryPlaceContainedColourfulLiquid(World world, BlockPos pos)
  {
	  IBlockState state = world.getBlockState(pos);
    if (!this.isFull) {
      return false;
    }
    Block block = world.getBlockState(pos).getBlock();
    Block frameBlock = world.getBlockState(pos.down()).getBlock();
    if (((!world.isAirBlock(pos)) && (block.getMaterial(state).isSolid())) || (ColourfulPortalsMod.isCPBlock(block))) {
      return false;
    }
    boolean hasCreatedPortal = false;
    if (ColourfulPortalsMod.isFrameBlock(frameBlock) && !ColourfulPortalsMod.isCPBlock(block) && ColourfulPortalsMod.canCreatePortal(world, pos, ColourfulPortalsMod.getCPBlockByFrameBlock(frameBlock).getDefaultState())) {
      hasCreatedPortal = BlockColourfulPortal.tryToCreatePortal(world, pos);
    }
    if (!hasCreatedPortal) {
      if (world.provider.doesWaterVaporize())
      {
    	  int i = pos.getX();
          int j = pos.getY();
          int k = pos.getZ();
          //world.playSound((double)((float)i + 0.5F), (double)((float)j + 0.5F), (double)((float)k + 0.5F), "random.fizz", 0.5F, 2.6F + (world.rand.nextFloat() - world.rand.nextFloat()) * 0.8F);
//TmTravlr plz create an evaporate sound and put it in colorfulsounds
          for (int l = 0; l < 8; ++l)
          {
              world.spawnParticle(EnumParticleTypes.SMOKE_LARGE, (double)i + Math.random(), (double)j + Math.random(), (double)k + Math.random(), 0.0D, 0.0D, 0.0D, new int[0]);
          }
      }
      else
      {
    	  Material material = world.getBlockState(pos).getBlock().getMaterial(state);
    	  
    	  if (!world.isRemote && !material.isSolid() && !material.isLiquid())
          {
              world.destroyBlock(pos, true);
          }

          world.setBlockState(pos, ColourfulBlocks.colourfulWater.getDefaultState(), 3);
      }
    }
    return true;
  }
}