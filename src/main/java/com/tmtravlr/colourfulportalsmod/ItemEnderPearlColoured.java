package com.tmtravlr.colourfulportalsmod;

import java.util.List;

import com.mojang.realmsclient.gui.ChatFormatting;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemEnderPearl;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.translation.I18n;
import net.minecraft.world.World;

public class ItemEnderPearlColoured
  extends ItemEnderPearl
{
  boolean isReflective = false;
  
  public ItemEnderPearlColoured(boolean reflective)
  {
    setUnlocalizedName(reflective ? "colourfulEnderPearlReflective" : "colourfulEnderPearl");
    this.isReflective = reflective;
    setMaxStackSize(1);
    setCreativeTab(ColourfulPortalsMod.cpTab);
  }
  
  public void addInformation(ItemStack itemStack, EntityPlayer player, List list, boolean notSure)
  {
    list.add(ChatFormatting.ITALIC + I18n.translateToLocal("item.colourfulEnderPearl.info.1"));
    list.add(ChatFormatting.ITALIC + I18n.translateToLocal("item.colourfulEnderPearl.info.2"));
    list.add(ChatFormatting.ITALIC + I18n.translateToLocal(this.isReflective ? "item.colourfulEnderPearl.info.3.reflective" : "item.colourfulEnderPearl.info.3"));
    list.add(ChatFormatting.ITALIC + I18n.translateToLocal("item.colourfulEnderPearl.info.4"));
  }
  
  public EnumActionResult onItemUse(ItemStack itemStack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing side, float par8, float par9, float par10)
  {
    if ((ColourfulPortalsMod.isCPBlock(world.getBlockState(pos).getBlock())) && (!world.isRemote))
    {
      if (this.isReflective) {
        BlockColourfulPortal.tryToCreateDestination(world, pos, world.getBlockState(pos), false);
      } else {
        BlockColourfulPortal.tryToCreateDestination(world, pos, world.getBlockState(pos), true);
      }
      itemStack.stackSize -= 1;
      return EnumActionResult.SUCCESS;
    }
    return EnumActionResult.FAIL;
    //return super.onItemUse(itemStack, player, world, pos, hand, side, par8, par9, par10);
  }
  
//  @SideOnly(Side.CLIENT)
//  public void registerIcons(IIconRegister iconRegister)
//  {
//    this.itemIcon = iconRegister.registerIcon("colourfulPortalsMod:enderpearlColour" + (this.isReflective ? "Reflective" : ""));
//  }
}