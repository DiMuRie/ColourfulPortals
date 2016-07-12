package com.tmtravlr.colourfulportalsmod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

import javax.annotation.Nullable;

import com.tmtravlr.colourfulportalsmod.init.ColorfulSounds;
import com.tmtravlr.colourfulportalsmod.init.ColourfulBlocks;
import com.tmtravlr.colourfulportalsmod.init.ColourfulItems;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketEntityEffect;
import net.minecraft.network.play.server.SPacketRespawn;
import net.minecraft.network.play.server.SPacketSetExperience;
import net.minecraft.potion.PotionEffect;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockColourfulPortal
extends BlockBreakable
{
	public static final PropertyEnum AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, new EnumFacing.Axis[] {EnumFacing.Axis.X, EnumFacing.Axis.Z, EnumFacing.Axis.Y});
	private static final boolean debug = false;
	private static final int PLAYER_MIN_DELAY = 0;
	private static final int PLAYER_MAX_DELAY = 10;
	private static LinkedList<Entity> entitiesTeleported = new LinkedList();
	public static BlockColourfulPortal instance = new BlockColourfulPortal("colourful_portal", Material.PORTAL);
	private static int goCrazyX = -1;
	private static int goCrazyY = -1;
	private static int goCrazyZ = -1;
	private static HashMap<Entity, Entity> toStack = new HashMap<Entity, Entity>();
	private static int stackDelay = 0;
	protected static final AxisAlignedBB AABB_X = new AxisAlignedBB(0D, 0D, 0.0625*6D, 1D, 1D, 0.0625*10D);
	protected static final AxisAlignedBB AABB_Y = new AxisAlignedBB(0D, 0.625*6D, 0D, 1D, 0.625D, 1D);
	protected static final AxisAlignedBB AABB_Z = new AxisAlignedBB(0.0625*6D, 0D, 0D, 0.625D, 1, 1D);

	public BlockColourfulPortal(String texture, Material material)
	{
		super(material, false);
		setTickRandomly(true);
	}

	
	@Override
	 public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);
	}

	@Nullable
	@Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
		EnumFacing.Axis axis = (EnumFacing.Axis)state.getValue(AXIS);
        switch (axis)
        {
            case Z:
                return AABB_Z;
            case Y:
                return AABB_Y;
                default:
            case X:
                return AABB_X;
        }
    }

	public boolean isOpaqueCube()
	{
		return false;
	}

//	public boolean renderAsNormalBlock()
//	{
//		return false;
//	}

	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		ColourfulPortalsMod.deletePortal(new ColourfulPortalsMod.ColourfulPortalLocation(pos, world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadata(state)));
	}
	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block other)
	{
		boolean xDir = true;
		boolean yDir = true;
		boolean zDir = true;
		int i = 0;
		int maxSize = ColourfulPortalsMod.maxPortalSizeCheck * ColourfulPortalsMod.maxPortalSizeCheck + 1;
		for (i = 0; (i < maxSize) && (ColourfulPortalsMod.isCPBlock(world.getBlockState(pos.add(i, 0, 0)).getBlock())); i++) {}
		if (!ColourfulPortalsMod.isFrameBlock(world.getBlockState(pos.add(i, 0, 0)).getBlock()))
		{
			zDir = false;
			yDir = false;
		}
		for (i = 0; (i < maxSize) && (ColourfulPortalsMod.isCPBlock(world.getBlockState(pos.add(-i, 0, 0)).getBlock())); i++) {}
		if (!ColourfulPortalsMod.isFrameBlock(world.getBlockState(pos.add(-i, 0, 0)).getBlock()))
		{
			zDir = false;
			yDir = false;
		}
		for (i = 0; (i < maxSize) && (ColourfulPortalsMod.isCPBlock(world.getBlockState(pos.add(0, i, 0)).getBlock())); i++) {}
		if (!ColourfulPortalsMod.isFrameBlock(world.getBlockState(pos.add(0, i, 0)).getBlock()))
		{
			zDir = false;
			xDir = false;
		}
		for (i = 0; (i < maxSize) && (ColourfulPortalsMod.isCPBlock(world.getBlockState(pos.add(0, -i, 0)).getBlock())); i++) {}
		if (!ColourfulPortalsMod.isFrameBlock(world.getBlockState(pos.add(0, -i, 0)).getBlock()))
		{
			zDir = false;
			xDir = false;
		}
		for (i = 0; (i < maxSize) && (ColourfulPortalsMod.isCPBlock(world.getBlockState(pos.add(0, 0, i)).getBlock())); i++) {}
		if (!ColourfulPortalsMod.isFrameBlock(world.getBlockState(pos.add(0, 0, i)).getBlock()))
		{
			xDir = false;
			yDir = false;
		}
		for (i = 0; (i < maxSize) && (ColourfulPortalsMod.isCPBlock(world.getBlockState(pos.add(0, 0, -i)).getBlock())); i++) {}
		if (!ColourfulPortalsMod.isFrameBlock(world.getBlockState(pos.add(0, 0, -i)).getBlock()))
		{
			xDir = false;
			yDir = false;
		}
		if ((!xDir) && (!yDir) && (!zDir))
		{
			ColourfulPortalsMod.CPLSet visited = new ColourfulPortalsMod.CPLSet();
			Stack<ColourfulPortalsMod.ColourfulPortalLocation> toVisit = new Stack();

			toVisit.push(new ColourfulPortalsMod.ColourfulPortalLocation(pos, world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadata(world, pos)));

			visited.add(toVisit.peek());
			while (!toVisit.empty())
			{
				ColourfulPortalsMod.ColourfulPortalLocation current = (ColourfulPortalsMod.ColourfulPortalLocation)toVisit.pop();

				int[][] dispArray = { { 0, 0, -1 }, { 0, 0, 1 }, { 0, -1, 0 }, { 0, 1, 0 }, { -1, 0, 0 }, { 1, 0, 0 } };
				for (int[] disps : dispArray) {
					BlockPos currentPos = new BlockPos(current.xPos + disps[0], current.yPos + disps[1], current.zPos + disps[2]);
					if (ColourfulPortalsMod.isFramedCPBlock(world.getBlockState(currentPos).getBlock()))
					{
						ColourfulPortalsMod.ColourfulPortalLocation temp = new ColourfulPortalsMod.ColourfulPortalLocation(currentPos, world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadata(world, currentPos));
						if (!visited.contains(temp))
						{
							toVisit.push(temp);
							visited.add(temp);
						}
					}
				}
			}
			for (ColourfulPortalsMod.ColourfulPortalLocation toDelete : visited) {
				world.setBlockToAir(new BlockPos(toDelete.xPos, toDelete.yPos, toDelete.zPos));
			}
		}
	}

	public boolean shouldSideBeRendered(IBlockAccess iba, BlockPos pos, EnumFacing side)
	{		
		return true;
	}

	public int quantityDropped(Random par1Random)
	{
		return 0;
	}

	public int getRenderBlockPass()
	{
		return 1;
	}
public static ColourfulItems ColourfulItems;
	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		int max = 2;
		boolean crazy = false;
		if ((goCrazyX == pos.getX()) && (goCrazyY == pos.getY()) && (goCrazyZ == pos.getZ()))
		{
			max = 50;
			crazy = true;

			goCrazyX = BlockColourfulPortal.goCrazyZ = BlockColourfulPortal.goCrazyY = -1;
		}
		for (int i = 0; i < max; i++)
		{
			float x = pos.getX() + rand.nextFloat();
			float y = pos.getY() + rand.nextFloat();
			float z = pos.getZ() + rand.nextFloat();
			float xVel = (rand.nextFloat() - 0.5F) * 0.5F;
			float yVel = (rand.nextFloat() - 0.5F) * 0.5F;
			float zVel = (rand.nextFloat() - 0.5F) * 0.5F;
			int dispX = rand.nextInt(2) * 2 - 1;
			int dispZ = rand.nextInt(2) * 2 - 1;

			x = pos.getX() + 0.5F + 0.25F * dispX;
			xVel = rand.nextFloat() * 2.0F * dispX;

			z = pos.getZ() + 0.5F + 0.25F * dispZ;
			zVel = rand.nextFloat() * 2.0F * dispZ;

			Particle particle = new EntityCPortalFX(world, x, y, z, xVel, yVel, zVel, crazy);
			Minecraft.getMinecraft().effectRenderer.addEffect(particle);
		}
		
	}

	public ItemStack getPickBlock(RayTraceResult target, World world, BlockPos pos)
	{
		return null;
	}

	public void onEntityCollidedWithBlock(World world, BlockPos pos, Entity entity)
	{
		if ((entity instanceof EntityLivingBase))
		{
			EntityLivingBase livingEntity = (EntityLivingBase)entity;
			if (livingEntity.getActivePotionEffect(MobEffects.NAUSEA) == null) {}
			livingEntity.addPotionEffect(new PotionEffect(MobEffects.NAUSEA, 80, 0, true, false));
		}
		
		if (!world.isRemote)
		{
			//Check for colourful ender pearls
			if ((entity instanceof EntityItem))
			{
				ItemStack item = ((EntityItem)entity).getEntityItem();
				if (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(ColourfulItems.enderPearlColoured))
				{
					tryToCreateDestination(world, pos, world.getBlockState(pos), true);

					entity.setDead();
				}
				else if (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(ColourfulItems.enderPearlColouredReflective))
				{
					tryToCreateDestination(world, pos, world.getBlockState(pos), false);

					entity.setDead();
				}
			}

			//Find the bottom entity of the stack
			
			while(entity.getRidingEntity() != null) {
				entity = entity.getRidingEntity();
			}
			
			//Go through to the top entity of the stack
			
			boolean doTeleport = true;
			Entity nextEntity = entity;
			
			//Go through the stack and make sure all entities can teleport.
			
			do {
				entity = nextEntity;
				
				if(!entitySatisfiesTeleportConditions(world, pos, entity)) {
					
					entity.getEntityData().setInteger("ColourfulPortalDelay", 10);
					if (!(entity instanceof EntityPlayer))
					{
						entity.getEntityData().setBoolean("InColourfulPortal", true);
					}
					
					doTeleport = false;
				}
				
				if (!entitiesTeleported.contains(entity))
				{
					entitiesTeleported.add(entity);
				}
				
				nextEntity = entity.getRidingEntity();
			}
			while(nextEntity != null);
			
			if (doTeleport && entitySatisfiesTeleportConditions(world, pos, entity))
			{
				
				teleportColourfully(world, pos, entity);

			}
			else
			{
				entity.getEntityData().setInteger("ColourfulPortalDelay", 10);
				if (!(entity instanceof EntityPlayer))
				{
					entity.getEntityData().setBoolean("InColourfulPortal", true);
				}
			}
			if (!entitiesTeleported.contains(entity))
			{
				entitiesTeleported.add(entity);
			}
			
			
		}
		
	}
public static ColourfulBlocks ColourfulBlocks;
	public static boolean tryToCreatePortal(World par1World, BlockPos pos)
	{
		return tryToCreatePortal(par1World, pos, true);
	}

	public static boolean tryToCreatePortal(World world, BlockPos pos, boolean addLocationToList)
	{
		int x = pos.getX();
		int y = pos.getY();
		int z = pos.getZ();
		
		if (!world.isRemote)
		{
			int maxSize = ColourfulPortalsMod.maxPortalSizeCheck * ColourfulPortalsMod.maxPortalSizeCheck - 1;
			if (!world.isAirBlock(pos.up()) && world.getBlockState(pos.up()).getBlock() != ColourfulBlocks.colourfulWater) {
				return false;
			}
			if (!ColourfulPortalsMod.isFrameBlock(world.getBlockState(pos.down()).getBlock())) {
				return false;
			}
			IBlockState frameState = world.getBlockState(pos.down());
			Block frameBlock = frameState.getBlock();
			int frameMeta = frameBlock.getMetaFromState(frameState);

			boolean[] dirs = { true, true, true };
			int i = 0;
			int thisId = 0;
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.south(i))) || (world.getBlockState(pos.south(i)).getBlock() == ColourfulBlocks.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.south(i)).getBlock() != frameBlock) || (ColourfulPortalsMod.getMeta(world, pos.south(i)) != frameMeta))
			{
				dirs[2] = false;
				dirs[1] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.north(i))) || (world.getBlockState(pos.north(i)).getBlock() == ColourfulBlocks.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.north(i)).getBlock() != frameBlock) || (ColourfulPortalsMod.getMeta(world, pos.north(i)) != frameMeta))
			{
				dirs[2] = false;
				dirs[1] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.up(i))) || (world.getBlockState(pos.up(i)).getBlock() == ColourfulBlocks.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.up(i)).getBlock() != frameBlock) || (ColourfulPortalsMod.getMeta(world, pos.up(i)) != frameMeta))
			{
				dirs[2] = false;
				dirs[0] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.down(i))) || (world.getBlockState(pos.down(i)).getBlock() == ColourfulBlocks.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.down(i)).getBlock() != frameBlock) || (ColourfulPortalsMod.getMeta(world, pos.down(i)) != frameMeta))
			{
				dirs[2] = false;
				dirs[0] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.east(i))) || (world.getBlockState(pos.east(i)).getBlock() == ColourfulBlocks.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.east(i)).getBlock() != frameBlock) || (ColourfulPortalsMod.getMeta(world, pos.east(i)) != frameMeta))
			{
				dirs[0] = false;
				dirs[1] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.west(i))) || (world.getBlockState(pos.west(i)).getBlock() == ColourfulBlocks.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.west(i)).getBlock() != frameBlock) || (ColourfulPortalsMod.getMeta(world, pos.west(i)) != frameMeta))
			{
				dirs[0] = false;
				dirs[1] = false;
			}
			for (int d = 0; d < 3; d++) {
				if (dirs[d])
				{
					boolean xLook = false;
					boolean yLook = false;
					boolean zLook = false;
					if (d == 0) {
						xLook = true;
					} else if (d == 1) {
						yLook = true;
					} else {
						zLook = true;
					}
					ColourfulPortalsMod.CPLSet visited = new ColourfulPortalsMod.CPLSet();
					Stack<ColourfulPortalsMod.ColourfulPortalLocation> toVisit = new Stack();

					toVisit.push(new ColourfulPortalsMod.ColourfulPortalLocation(pos, world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadata(world, pos)));

					visited.add(toVisit.peek());

					int maxSizeTotal = (ColourfulPortalsMod.maxPortalSizeCheck * ColourfulPortalsMod.maxPortalSizeCheck - 1) * (ColourfulPortalsMod.maxPortalSizeCheck * ColourfulPortalsMod.maxPortalSizeCheck - 1);
					for (int j = 0; (j < maxSizeTotal) && (!toVisit.empty()) && (dirs[d]); j++)
					{
						ColourfulPortalsMod.ColourfulPortalLocation current = (ColourfulPortalsMod.ColourfulPortalLocation)toVisit.pop();
						BlockPos currentPos = new BlockPos(current.xPos, current.yPos, current.zPos);
						if ((dirs[0]) || (dirs[2]))
						{
							Block nextBlock = world.getBlockState(currentPos.up()).getBlock();
							int nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.up()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColourfulBlocks.colourfulWater)) || (Math.abs(current.xPos - x) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.yPos + 1 - y) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.zPos - z) > ColourfulPortalsMod.maxPortalSizeCheck)) {
								if (xLook) {
									dirs[0] = false;
								} else if (zLook) {
									dirs[2] = false;
								}
							}
							nextBlock = world.getBlockState(currentPos.down()).getBlock();
							nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.down()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColourfulBlocks.colourfulWater)) || (Math.abs(current.xPos - x) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.yPos - 1 - y) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.zPos - z) > ColourfulPortalsMod.maxPortalSizeCheck)) {
								if (xLook) {
									dirs[0] = false;
								} else if (zLook) {
									dirs[2] = false;
								}
							}
						}
						if ((dirs[0]) || (dirs[1]))
						{
							Block nextBlock = world.getBlockState(currentPos.east()).getBlock();
							int nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.east()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColourfulBlocks.colourfulWater)) || (Math.abs(current.xPos - x) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.yPos - y) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.zPos + 1 - z) > ColourfulPortalsMod.maxPortalSizeCheck)) {
								if (xLook) {
									dirs[0] = false;
								} else if (yLook) {
									dirs[1] = false;
								}
							}
							nextBlock = world.getBlockState(currentPos.west()).getBlock();
							nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.west()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColourfulBlocks.colourfulWater)) || (Math.abs(current.xPos - x) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.yPos - y) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.zPos - 1 - z) > ColourfulPortalsMod.maxPortalSizeCheck)) {
								if (xLook) {
									dirs[0] = false;
								} else if (yLook) {
									dirs[1] = false;
								}
							}
						}
						if ((dirs[1]) || (dirs[2]))
						{
							Block nextBlock = world.getBlockState(currentPos.south()).getBlock();
							int nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.south()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColourfulBlocks.colourfulWater)) || (Math.abs(current.xPos + 1 - x) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.yPos - y) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.zPos - z) > ColourfulPortalsMod.maxPortalSizeCheck)) {
								if (yLook) {
									dirs[1] = false;
								} else if (zLook) {
									dirs[2] = false;
								}
							}
							nextBlock = world.getBlockState(currentPos.north()).getBlock();
							nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.north()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColourfulBlocks.colourfulWater)) || (Math.abs(current.xPos - 1 - x) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.yPos - y) > ColourfulPortalsMod.maxPortalSizeCheck) || (Math.abs(current.zPos - z) > ColourfulPortalsMod.maxPortalSizeCheck)) {
								if (yLook) {
									dirs[1] = false;
								} else if (zLook) {
									dirs[2] = false;
								}
							}
						}
						if ((dirs[d]) && (Math.abs(x - current.xPos) < ColourfulPortalsMod.maxPortalSizeCheck) && (y <= 256) && (y > 0) && (Math.abs(z - current.zPos) < ColourfulPortalsMod.maxPortalSizeCheck))
						{
							if ((zLook) || (xLook))
							{
								if (world.isAirBlock(currentPos.up()) || world.getBlockState(currentPos.up()).getBlock() == ColourfulBlocks.colourfulWater)
								{
									ColourfulPortalsMod.ColourfulPortalLocation temp = new ColourfulPortalsMod.ColourfulPortalLocation(currentPos.up(), world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
								if (world.isAirBlock(currentPos.down()) || world.getBlockState(currentPos.down()).getBlock() == ColourfulBlocks.colourfulWater)
								{
									ColourfulPortalsMod.ColourfulPortalLocation temp = new ColourfulPortalsMod.ColourfulPortalLocation(currentPos.down(), world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
							}
							if ((zLook) || (yLook))
							{
								if (world.isAirBlock(currentPos.south()) || world.getBlockState(currentPos.south()).getBlock() == ColourfulBlocks.colourfulWater)
								{
									ColourfulPortalsMod.ColourfulPortalLocation temp = new ColourfulPortalsMod.ColourfulPortalLocation(currentPos.south(), world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
								if (world.isAirBlock(currentPos.north()) || world.getBlockState(currentPos.north()).getBlock() == ColourfulBlocks.colourfulWater)
								{
									ColourfulPortalsMod.ColourfulPortalLocation temp = new ColourfulPortalsMod.ColourfulPortalLocation(currentPos.north(), world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
							}
							if ((yLook) || (xLook))
							{
								if (world.isAirBlock(currentPos.east()) || world.getBlockState(currentPos.east()).getBlock() == ColourfulBlocks.colourfulWater)
								{
									ColourfulPortalsMod.ColourfulPortalLocation temp = new ColourfulPortalsMod.ColourfulPortalLocation(currentPos.east(), world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
								if (world.isAirBlock(currentPos.west()) || world.getBlockState(currentPos.west()).getBlock() == ColourfulBlocks.colourfulWater)
								{
									ColourfulPortalsMod.ColourfulPortalLocation temp = new ColourfulPortalsMod.ColourfulPortalLocation(currentPos.west(), world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
							}
						}
					}
					if (dirs[d])
					{
						for (ColourfulPortalsMod.ColourfulPortalLocation cpl : visited) {
							if (((dirs[0]) && (cpl.xPos == x)) || ((dirs[1]) && (cpl.yPos == y)) || ((dirs[2]) && (cpl.zPos == z))) {
								ColourfulPortalsMod.setFramedCPBlock(world, new BlockPos(cpl.xPos, cpl.yPos, cpl.zPos), frameBlock, frameMeta, 2);
							}
						}
						int shiftedMeta = ColourfulPortalsMod.getShiftedCPMetadata(world, pos);
						boolean creationSuccess = true;
						if (addLocationToList) {
							creationSuccess = ColourfulPortalsMod.addPortalToList(new ColourfulPortalsMod.ColourfulPortalLocation(pos, world.provider.getDimension(), shiftedMeta));
						}
						return creationSuccess;
					}
				}
			}
		}
		return false;
	}

	public static void tryToCreateDestination(World world, BlockPos pos, IBlockState state, boolean sameDim)
	{
		boolean creationSuccess = false;
		if (!ColourfulPortalsMod.tooManyPortals(state))
		{
			ColourfulPortalsMod.ColourfulPortalLocation destination = createDestination(sameDim, world.provider.getDimension(), ColourfulPortalsMod.getShiftedCPMetadata(world, pos), world, pos);
			if (destination == null) {
				return;
			}
			creationSuccess = ColourfulPortalsMod.addPortalToList(destination);
		}
		float soundPitch = 1.8F;
		if (sameDim) {
			soundPitch = 1.5F;
		}
		goCrazyX = pos.getX();
		goCrazyY = pos.getY();
		goCrazyZ = pos.getZ();
		Random random = new Random();
		world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, ColorfulSounds.TELEPORT, SoundCategory.BLOCKS, 0.5F, random.nextFloat() * 0.4F + 0.8F, false);
	}

	private static ColourfulPortalsMod.ColourfulPortalLocation createDestination(boolean isSameDim, int oldDim, int meta, World world, BlockPos pos)
	{
		int unshiftedMeta = ColourfulPortalsMod.unshiftCPMetadata(meta);
		Block portalBlock = ColourfulPortalsMod.getCPBlockByShiftedMetadata(meta);
		Block frameBlock = ColourfulPortalsMod.getFrameBlockByShiftedMetadata(meta);
		IBlockState state = world.getBlockState(pos);
		
		byte var2 = 16;
		double var3 = -1.0D;

		int maxDistance = ColourfulPortalsMod.maxPortalGenerationDistance;
		if (maxDistance < 0) {
			maxDistance = -maxDistance;
		}
		if (maxDistance > 29999872) {
			maxDistance = 29999872;
		}
		Random rand = new Random();
		int var5 = rand.nextInt(maxDistance * 2);
		var5 -= maxDistance;
		int var6 = 0;
		int var7 = rand.nextInt(maxDistance * 2);
		var7 -= maxDistance;
		int dimension;
		WorldServer worldServer;    
		if (isSameDim)
		{
			worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(oldDim);
			dimension = oldDim;
		}
		else
		{
			WorldServer[] wServers = FMLCommonHandler.instance().getMinecraftServerInstance().worldServers;
			int indexStart = rand.nextInt(wServers.length);
			int index = indexStart;

			worldServer = null;
			do
			{
				if (ColourfulPortalsMod.isDimensionValidForDestination(wServers[index].provider.getDimension())) {
					worldServer = wServers[index];
				}
				index++;
				if (index >= wServers.length) {
					index -= wServers.length;
				}
			} while ((index != indexStart) && (worldServer == null));
			if (worldServer == null) {
				return null;
			}
			dimension = worldServer.provider.getDimension();
		}
		int var8 = var5;
		int var9 = var6;
		int var10 = var7;
		int var11 = 0;
		int var12 = rand.nextInt(4);
		for (int var13 = var5 - var2; var13 <= var5 + var2; var13++)
		{
			double var14 = var13 + 0.5D - var5;
			for (int var16 = var7 - var2; var16 <= var7 + var2; var16++)
			{
				double var17 = var16 + 0.5D - var7;
				label609:
					for (int var19 = worldServer.getActualHeight() - 1; var19 >= 0; var19--) {
						if (worldServer.isAirBlock(new BlockPos(var13, var19, var16)))
						{
							while ((var19 > 0) && (worldServer.isAirBlock(new BlockPos(var13, var19 - 1, var16)))) {
								var19--;
							}
							for (int var20 = var12; var20 < var12 + 4; var20++)
							{
								int var21 = var20 % 2;
								int var22 = 1 - var21;
								if (var20 % 4 >= 2)
								{
									var21 = -var21;
									var22 = -var22;
								}
								for (int var23 = 0; var23 < 3; var23++) {
									for (int var24 = 0; var24 < 4; var24++) {
										for (int var25 = -1; var25 < 4; var25++)
										{
											int var26 = var13 + (var24 - 1) * var21 + var23 * var22;
											int var27 = var19 + var25;
											int var28 = var16 + (var24 - 1) * var22 - var23 * var21;
											if (((var25 < 0) && (!worldServer.getBlockState(new BlockPos(var26, var27, var28)).getBlock().getMaterial(state).isSolid())) || ((var25 >= 0) && (!worldServer.isAirBlock(new BlockPos(var26, var27, var28))))) {
												break label609;
											}
										}
									}
								}
								double var32 = var19 + 0.5D - var6;
								double var31 = var14 * var14 + var32 * var32 + var17 * var17;
								if ((var3 < 0.0D) || (var31 < var3))
								{
									var3 = var31;
									var8 = var13;
									var9 = var19;
									var10 = var16;
									var11 = var20 % 4;
								}
							}
						}
					}
			}
		}
		if (var3 < 0.0D) {
			for (int var13 = var5 - var2; var13 <= var5 + var2; var13++)
			{
				double var14 = var13 + 0.5D - var5;
				for (int var16 = var7 - var2; var16 <= var7 + var2; var16++)
				{
					double var17 = var16 + 0.5D - var7;
					label957:
						for (int var19 = worldServer.getActualHeight() - 1; var19 >= 0; var19--) {
							if (worldServer.isAirBlock(new BlockPos(var13, var19, var16)))
							{
								while ((var19 > 0) && (worldServer.isAirBlock(new BlockPos(var13, var19 - 1, var16)))) {
									var19--;
								}
								for (int var20 = var12; var20 < var12 + 2; var20++)
								{
									int var21 = var20 % 2;
									int var22 = 1 - var21;
									for (int var23 = 0; var23 < 4; var23++) {
										for (int var24 = -1; var24 < 4; var24++)
										{
											int var25 = var13 + (var23 - 1) * var21;
											int var26 = var19 + var24;
											int var27 = var16 + (var23 - 1) * var22;
											if (((var24 < 0) && (!worldServer.getBlockState(new BlockPos(var25, var26, var27)).getBlock().getMaterial(state).isSolid())) || ((var24 >= 0) && (!worldServer.isAirBlock(new BlockPos(var25, var26, var27))))) {
												break label957;
											}
										}
									}
									double var32 = var19 + 0.5D - var6;
									double var31 = var14 * var14 + var32 * var32 + var17 * var17;
									if ((var3 < 0.0D) || (var31 < var3))
									{
										var3 = var31;
										var8 = var13;
										var9 = var19;
										var10 = var16;
										var11 = var20 % 2;
									}
								}
							}
						}
				}
			}
		}
		int var29 = var8;
		int var15 = var9;
		int var16 = var10;
		int var30 = var11 % 2;
		int var18 = 1 - var30;
		if (var11 % 4 >= 2)
		{
			var30 = -var30;
			var18 = -var18;
		}
		if (var3 < 0.0D)
		{
			if (var9 < 70) {
				var9 = 70;
			}
			if (var9 > worldServer.getActualHeight() - 10) {
				var9 = worldServer.getActualHeight() - 10;
			}
			var15 = var9;
			for (int var19 = -1; var19 <= 1; var19++) {
				for (int var20 = 1; var20 < 3; var20++) {
					for (int var21 = -1; var21 < 3; var21++)
					{
						int var22 = var29 + (var20 - 1) * var30 + var19 * var18;
						int var23 = var15 + var21;
						int var24 = var16 + (var20 - 1) * var18 - var19 * var30;
						boolean var33 = var21 < 0;
						worldServer.setBlockState(new BlockPos(var22, var23, var24), var33 ? frameBlock.getStateFromMeta(unshiftedMeta) : Blocks.AIR.getDefaultState());
					}
				}
			}
		}
		for (int var19 = 0; var19 < 4; var19++)
		{
			for (int var20 = 0; var20 < 4; var20++) {
				for (int var21 = -1; var21 < 4; var21++)
				{
					int var22 = var29 + (var20 - 1) * var30;
					int var23 = var15 + var21;
					int var24 = var16 + (var20 - 1) * var18;
					boolean var33 = (var20 == 0) || (var20 == 3) || (var21 == -1) || (var21 == 3);
					worldServer.setBlockState(new BlockPos(var22, var23, var24), var33 ? frameBlock.getStateFromMeta(unshiftedMeta) : portalBlock.getStateFromMeta(unshiftedMeta), 2);
				}
			}
			for (int var20 = 0; var20 < 4; var20++) {
				for (int var21 = -1; var21 < 4; var21++)
				{
					int var22 = var29 + (var20 - 1) * var30;
					int var23 = var15 + var21;
					int var24 = var16 + (var20 - 1) * var18;
					worldServer.notifyNeighborsOfStateChange(new BlockPos(var22, var23, var24), worldServer.getBlockState(new BlockPos(var22, var23, var24)).getBlock());
				}
			}
		}
		return new ColourfulPortalsMod.ColourfulPortalLocation(new BlockPos(var29, var15, var16), dimension, meta);
	}

	public static void playColourfulTeleportSound(World world, double x, double y, double z,BlockPos pos)
	{
		Random rand = new Random();
		world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, ColorfulSounds.TELEPORT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
	}

	private boolean entitySatisfiesTeleportConditions(World world, BlockPos pos, Entity entity)
	{
		if (world.isRemote) {
			return false;
		}
		if (((entity instanceof EntityPlayer)) && (entity.getEntityData().getInteger("ColourfulPortalPlayerDelay") >= 10) && (entity.isSneaking())) {
			return true;
		}
		return !entity.getEntityData().getBoolean("InColourfulPortal");
	}

	private Entity teleportColourfully(World world, BlockPos startPos, Entity entity)
	{
		ColourfulPortalsMod.ColourfulPortalLocation destination = ColourfulPortalsMod.getColourfulDestination(world, startPos);
		//Make sure the dimension we are trying to teleport to exists first!
		if(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(destination.dimension) == null) {
			return entity;
		}
		int meta = destination.portalMetadata;
		double x = destination.xPos + 0.5D;
		double y = destination.yPos + 0.1D + (ColourfulPortalsMod.isStandaloneCPBlock(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(destination.dimension).getBlockState(new BlockPos(destination.xPos, destination.yPos, destination.zPos)).getBlock()) ? 1.0D : 0.0D);
		double z = destination.zPos + 0.5D;
		WorldServer newWorldServer = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(destination.dimension);
		
		Entity ridingEntity = entity.getRidingEntity();
		if(ridingEntity != null) {
			entity.dismountRidingEntity();
			ridingEntity = teleportColourfully(world, startPos, ridingEntity);
		}

		entity.getEntityData().setInteger("ColourfulPortalDelay", 10);
		entity.getEntityData().setBoolean("InColourfulPortal", true);
		
		EntityPlayerMP player = null;
		if ((entity instanceof EntityPlayer))
		{
			Iterator iterator = ((List<Entity>) FMLCommonHandler.instance().getMinecraftServerInstance().getPlayerList()).iterator();//.playerEntityList.iterator();
			EntityPlayerMP entityplayermp = null;
			do
			{
				if (!iterator.hasNext()) {
					break;
				}
				entityplayermp = (EntityPlayerMP)iterator.next();
			} while (!entityplayermp.getName().equalsIgnoreCase(entity.getName()));
			player = entityplayermp;
		}
		if (player != null)
		{
			player.getEntityData().setInteger("ColourfulPortalPlayerDelay", 0);

			teleportPlayerColourfully(newWorldServer, x, y, z, player, destination);
		}
		else
		{
			entity = teleportEntityColourfully(newWorldServer, x, y, z, entity, destination);
		}
		
		doAfterTeleportStuff(world, x, y, z, meta, entity, newWorldServer, destination.xPos, destination.yPos, destination.zPos);

		//This isn't working for some reason.
		
		if(ridingEntity != null) {
			toStack.put(ridingEntity, entity);
			stackDelay = 2;
		}
		
		return entity;
	}

	private static Entity teleportEntityColourfully(World world, double x, double y, double z, Entity entity, ColourfulPortalsMod.ColourfulPortalLocation destination)
	{
		int meta = destination.portalMetadata;
		int dimension = destination.dimension;
		int currentDimension = entity.worldObj.provider.getDimension();
		if (dimension != currentDimension)
		{
			entitiesTeleported.remove(entity);
			return transferEntityToDimension(entity, dimension, x, y, z);
		}
		
		entity.setLocationAndAngles(x, y, z, entity.rotationYaw, 0.0F);
		
		return entity;
	}

	private static void teleportPlayerColourfully(World world, double x, double y, double z, EntityPlayerMP player, ColourfulPortalsMod.ColourfulPortalLocation destination)
	{
		int meta = destination.portalMetadata;
		int dimension = destination.dimension;
		int currentDimension = player.worldObj.provider.getDimension();
		if (currentDimension != dimension)
		{
			if (!world.isRemote) {
				if (currentDimension != 1) {
					player.mcServer.getPlayerList().transferPlayerToDimension(player, dimension, new ColourfulTeleporter(player.mcServer.worldServerForDimension(dimension), x, y, z));
				} else {
					forceTeleportPlayerFromEnd(player, dimension, new ColourfulTeleporter(player.mcServer.worldServerForDimension(dimension), x, y, z));
				}
				player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));				
			}
		}
		else {
			player.connection.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
			world.updateEntityWithOptionalForce(player, false);
		}
	}

	private static void doAfterTeleportStuff(World world, double x, double y, double z, int meta, Entity entity, World newWorld, double newX, double newY, double newZ)
	{
		BlockPos pos = new BlockPos(x,y,z);
		BlockPos newPos = new BlockPos(newX,newY,newZ);
		playColourfulTeleportSound(world, x, y, z, pos);
		playColourfulTeleportSound(newWorld, newX, newY, newZ, newPos);
		if (!entitiesTeleported.contains(entity)) {
			entitiesTeleported.add(entity);
		}
	}

	private static void forceTeleportPlayerFromEnd(EntityPlayerMP player, int newDimension, Teleporter colourfulTeleporter)
	{
		int j = player.dimension;
		WorldServer worldServerOld = player.mcServer.worldServerForDimension(player.dimension);
		player.dimension = newDimension;
		WorldServer worldServerNew = player.mcServer.worldServerForDimension(player.dimension);
		player.connection.sendPacket(new SPacketRespawn(player.dimension, player.worldObj.getDifficulty(), player.worldObj.getWorldInfo().getTerrainType(), Minecraft.getMinecraft().playerController.getCurrentGameType()));
		worldServerOld.removeEntityDangerously(player);
		player.isDead = false;

		WorldProvider pOld = worldServerOld.provider;
		WorldProvider pNew = worldServerNew.provider;
		double moveFactor = pOld.getMovementFactor() / pNew.getMovementFactor();
		double d0 = player.posX * moveFactor;
		double d1 = player.posZ * moveFactor;
		double d3 = player.posX;
		double d4 = player.posY;
		double d5 = player.posZ;
		float f = player.rotationYaw;

		worldServerOld.theProfiler.startSection("placing");
		d0 = MathHelper.clamp_int((int)d0, -29999872, 29999872);
		d1 = MathHelper.clamp_int((int)d1, -29999872, 29999872);
		if (player.isEntityAlive())
		{
			player.setLocationAndAngles(d0, player.posY, d1, player.rotationYaw, player.rotationPitch);
			colourfulTeleporter.placeInPortal(player, f);
			worldServerNew.spawnEntityInWorld(player);
			worldServerNew.updateEntityWithOptionalForce(player, false);
		}
		worldServerOld.theProfiler.endSection();

		player.setWorld(worldServerNew);

		player.mcServer.getPlayerList().preparePlayer(player, worldServerOld);
		player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.setWorld(worldServerNew);
		player.mcServer.getPlayerList().updateTimeAndWeatherForPlayer(player, worldServerNew);
		player.mcServer.getPlayerList().syncPlayerInventory(player);
		Iterator iterator = player.getActivePotionEffects().iterator();
		while (iterator.hasNext())
		{
			PotionEffect potioneffect = (PotionEffect)iterator.next();
			player.connection.sendPacket(new SPacketEntityEffect(player.getEntityId(), potioneffect));
		}
		FMLCommonHandler.instance().firePlayerChangedDimensionEvent(player, j, newDimension);
	}

	public static Entity transferEntityToDimension(Entity toTeleport, int newDimension, double x, double y, double z)
	{
		if (!toTeleport.isDead)
		{
			toTeleport.worldObj.theProfiler.startSection("changeDimension");
			MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance();
			int oldDimension = toTeleport.dimension;
			WorldServer worldServerOld = minecraftserver.worldServerForDimension(oldDimension);
			WorldServer worldServerNew = minecraftserver.worldServerForDimension(newDimension);
			toTeleport.dimension = newDimension;

			toTeleport.worldObj.removeEntity(toTeleport);
			toTeleport.isDead = false;
			toTeleport.worldObj.theProfiler.startSection("reposition");
			if (oldDimension == 1) {
				forceTeleportEntityFromEnd(toTeleport, newDimension, new ColourfulTeleporter(worldServerOld, x, y, z), worldServerNew);
			} else {
				minecraftserver.getPlayerList().transferEntityToWorld(toTeleport, oldDimension, worldServerOld, worldServerNew, new ColourfulTeleporter(worldServerOld, x, y, z));
			}
			toTeleport.worldObj.theProfiler.endStartSection("reloading");
			Entity entity = EntityList.createEntityByName(EntityList.getEntityString(toTeleport), worldServerNew);
			if (entity != null)
			{
				//entity.copyDataFromOld(toTeleport);
				entity.getEntityData().copy();
				worldServerNew.spawnEntityInWorld(entity);
			}
			toTeleport.isDead = true;
			toTeleport.worldObj.theProfiler.endSection();
			worldServerOld.resetUpdateEntityTick();
			worldServerNew.resetUpdateEntityTick();
			toTeleport.worldObj.theProfiler.endSection();

			return entity;
		}
		return toTeleport;
	}

	private static void forceTeleportEntityFromEnd(Entity entity, int newDimension, Teleporter colourfulTeleporter, WorldServer worldServerNew)
	{
		worldServerNew.spawnEntityInWorld(entity);
		entity.setLocationAndAngles(entity.posX, entity.posY, entity.posZ, entity.rotationYaw, entity.rotationPitch);
		worldServerNew.updateEntityWithOptionalForce(entity, false);
		colourfulTeleporter.placeInPortal(entity, 0.0F);

		entity.setWorld(worldServerNew);
	}

	public void serverTick()
	{
		ArrayList<Entity> toRemove = new ArrayList();
		for (Entity entity : entitiesTeleported) {
			if (entity.isDead)
			{
				toRemove.add(entity);
			}
			else
			{
				World world = entity.worldObj;
				boolean inCP = true;
				if ((!ColourfulPortalsMod.isCPBlock(entity.worldObj.getBlockState(new BlockPos((int)Math.floor(entity.posX), (int)Math.floor(entity.posY), (int)Math.floor(entity.posZ))).getBlock())) && (!ColourfulPortalsMod.isCPBlock(entity.worldObj.getBlockState(new BlockPos((int)Math.floor(entity.posX), (int)Math.floor(entity.posY - 1.0D), (int)Math.floor(entity.posZ))).getBlock())) && (entity.getEntityData().getInteger("ColourfulPortalDelay") > 0) && (inCP)) {
					entity.getEntityData().setInteger("ColourfulPortalDelay", entity.getEntityData().getInteger("ColourfulPortalDelay") - 1);
				}
				if (entity.getEntityData().getInteger("ColourfulPortalDelay") <= 0) {
					inCP = false;
				}
				if ((entity instanceof EntityPlayer))
				{
					int delay = entity.getEntityData().getInteger("ColourfulPortalPlayerDelay");
					if (delay < 10) {
						entity.getEntityData().setInteger("ColourfulPortalPlayerDelay", delay + 1);
					}
				}
				if (!inCP)
				{
					entity.getEntityData().setBoolean("InColourfulPortal", false);
					if ((entity instanceof EntityPlayer)) {
						entity.getEntityData().setInteger("ColourfulPortalPlayerDelay", 0);
					}
					toRemove.add(entity);
				}
			}
		}
		for (Entity entity : toRemove) {
			entitiesTeleported.remove(entity);
		}
		
		if(stackDelay <= 0) {
			//Restack any stacked entities
			for(Entity mount : toStack.keySet()) {
				Entity riding = toStack.get(mount);
				if(riding instanceof EntityPlayer) {
					riding.worldObj.updateEntityWithOptionalForce(riding, true);
				}
				riding.dismountRidingEntity();
			}
			
			toStack.clear();
		}
		else {
			stackDelay--;
		}
	}
}