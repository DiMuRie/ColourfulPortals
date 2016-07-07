package com.tmtravlr.colorfulportals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

import javax.annotation.Nullable;

import com.tmtravlr.colourfulportalsmod.ColourfulTeleporter;

import net.minecraft.block.Block;
import net.minecraft.block.BlockBreakable;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyEnum;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.MobEffects;
import net.minecraft.init.SoundEvents;
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
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.Teleporter;
import net.minecraft.world.World;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class BlockColorfulPortal extends BlockBreakable {

	protected BlockColorfulPortal(Material materialIn) {
		super(materialIn, false);
		setTickRandomly(true);
		GameRegistry.register(this);
	}
	public static final PropertyEnum AXIS = PropertyEnum.create("axis", EnumFacing.Axis.class, new EnumFacing.Axis[] {EnumFacing.Axis.X, EnumFacing.Axis.Z, EnumFacing.Axis.Y});
	private static final boolean debug = false;
	private static final int PLAYER_MIN_DELAY = 0;
	private static final int PLAYER_MAX_DELAY = 10;
	private static LinkedList<Entity> entitiesTeleported = new LinkedList();
	public static BlockColorfulPortal instance = new BlockColorfulPortal(Material.PORTAL);
	private static int goCrazyX = -1;
	private static int goCrazyY = -1;
	private static int goCrazyZ = -1;
	private static HashMap<Entity, Entity> toStack = new HashMap<Entity, Entity>();
	private static int stackDelay = 0;
	protected static final AxisAlignedBB AABB_X = new AxisAlignedBB(0D, 0D, 0.0625*6D, 1D, 1D, 0.0625*10D);
	protected static final AxisAlignedBB AABB_Y = new AxisAlignedBB(0D, 0.625*6D, 0D, 1D, 0.625D, 1D);
	protected static final AxisAlignedBB AABB_Z = new AxisAlignedBB(0.0625*6D, 0D, 0D, 0.625D, 1, 1D);

	/*public BlockColourfulPortal(String texture, Material material, boolean ignoreSimilarityIn)
	{
		super(material, false);
		
		setTickRandomly(true);
	}*/

	@Override
	 public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {
        super.updateTick(worldIn, pos, state, rand);
	}

	@Nullable
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, World worldIn, BlockPos pos)
    {
        return NULL_AABB;
    }
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
	@SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state)
	{
		ColorfulPortals.deletePortal(new ColorfulPortals.ColorfulPortalLocation(pos, world.provider.getDimension(), ColorfulPortals.getShiftedCPMetadata(state)));
	}

	public void onNeighborBlockChange(World world, BlockPos pos, IBlockState state, Block other)
	{
		boolean xDir = true;
		boolean yDir = true;
		boolean zDir = true;
		int i = 0;
		int maxSize = ColorfulPortals.maxPortalSizeCheck * ColorfulPortals.maxPortalSizeCheck + 1;
		for (i = 0; (i < maxSize) && (ColorfulPortals.isCPBlock(world.getBlockState(pos.add(i, 0, 0)).getBlock())); i++) {}
		if (!ColorfulPortals.isFrameBlock(world.getBlockState(pos.add(i, 0, 0)).getBlock()))
		{
			zDir = false;
			yDir = false;
		}
		for (i = 0; (i < maxSize) && (ColorfulPortals.isCPBlock(world.getBlockState(pos.add(-i, 0, 0)).getBlock())); i++) {}
		if (!ColorfulPortals.isFrameBlock(world.getBlockState(pos.add(-i, 0, 0)).getBlock()))
		{
			zDir = false;
			yDir = false;
		}
		for (i = 0; (i < maxSize) && (ColorfulPortals.isCPBlock(world.getBlockState(pos.add(0, i, 0)).getBlock())); i++) {}
		if (!ColorfulPortals.isFrameBlock(world.getBlockState(pos.add(0, i, 0)).getBlock()))
		{
			zDir = false;
			xDir = false;
		}
		for (i = 0; (i < maxSize) && (ColorfulPortals.isCPBlock(world.getBlockState(pos.add(0, -i, 0)).getBlock())); i++) {}
		if (!ColorfulPortals.isFrameBlock(world.getBlockState(pos.add(0, -i, 0)).getBlock()))
		{
			zDir = false;
			xDir = false;
		}
		for (i = 0; (i < maxSize) && (ColorfulPortals.isCPBlock(world.getBlockState(pos.add(0, 0, i)).getBlock())); i++) {}
		if (!ColorfulPortals.isFrameBlock(world.getBlockState(pos.add(0, 0, i)).getBlock()))
		{
			xDir = false;
			yDir = false;
		}
		for (i = 0; (i < maxSize) && (ColorfulPortals.isCPBlock(world.getBlockState(pos.add(0, 0, -i)).getBlock())); i++) {}
		if (!ColorfulPortals.isFrameBlock(world.getBlockState(pos.add(0, 0, -i)).getBlock()))
		{
			xDir = false;
			yDir = false;
		}
		if ((!xDir) && (!yDir) && (!zDir))
		{
			ColorfulPortals.CPLSet visited = new ColorfulPortals.CPLSet();
			Stack<ColorfulPortals.ColorfulPortalLocation> toVisit = new Stack();

			toVisit.push(new ColorfulPortals.ColorfulPortalLocation(pos, world.provider.getDimension(), ColorfulPortals.getShiftedCPMetadata(world, pos)));

			visited.add(toVisit.peek());
			while (!toVisit.empty())
			{
				ColorfulPortals.ColorfulPortalLocation current = (ColorfulPortals.ColorfulPortalLocation)toVisit.pop();

				int[][] dispArray = { { 0, 0, -1 }, { 0, 0, 1 }, { 0, -1, 0 }, { 0, 1, 0 }, { -1, 0, 0 }, { 1, 0, 0 } };
				for (int[] disps : dispArray) {
					BlockPos currentPos = new BlockPos(current.xPos + disps[0], current.yPos + disps[1], current.zPos + disps[2]);
					if (ColorfulPortals.isFramedCPBlock(world.getBlockState(currentPos).getBlock()))
					{
						ColorfulPortals.ColorfulPortalLocation temp = new ColorfulPortals.ColorfulPortalLocation(currentPos, world.provider.getDimension(), ColorfulPortals.getShiftedCPMetadata(world, currentPos));
						if (!visited.contains(temp))
						{
							toVisit.push(temp);
							visited.add(temp);
						}
					}
				}
			}
			for (ColorfulPortals.ColorfulPortalLocation toDelete : visited) {
				world.setBlockToAir(new BlockPos(toDelete.xPos, toDelete.yPos, toDelete.zPos));
			}
		}
	}

	public boolean shouldSideBeRendered(IBlockAccess iba, BlockPos pos, EnumFacing side)
	{		
		if (((side == EnumFacing.DOWN) && (this.minY > 0.0D)) || ((side == EnumFacing.UP) && (this.maxY < 1.0D)) || ((side == EnumFacing.NORTH) && (this.minZ > 0.0D)) || ((side == EnumFacing.SOUTH) && (this.maxZ < 1.0D)) || ((side == EnumFacing.WEST) && (this.minX > 0.0D)) || ((side == EnumFacing.EAST) && (this.maxX < 1.0D))) {
			return true;
		}
		if (ColorfulPortals.isPortalOrFrameBlock(iba, pos)) {
			return false;
		}
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

	@SideOnly(Side.CLIENT)
	public void randomDisplayTick(World world, BlockPos pos, IBlockState state, Random rand)
	{
		int max = 2;
		boolean crazy = false;
		if ((goCrazyX == pos.getX()) && (goCrazyY == pos.getY()) && (goCrazyZ == pos.getZ()))
		{
			max = 50;
			crazy = true;

			goCrazyX = BlockColorfulPortal.goCrazyZ = BlockColorfulPortal.goCrazyY = -1;
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


			Particle entityfx = new EntityCPortalFX(world, x, y, z, xVel, yVel, zVel, crazy);
			Minecraft.getMinecraft().effectRenderer.addEffect(entityfx);
		}
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
				if (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(ColorfulPortals.enderPearlColoured))
				{
					tryToCreateDestination(world, pos, world.getBlockState(pos), true);

					entity.setDead();
				}
				else if (Item.getIdFromItem(item.getItem()) == Item.getIdFromItem(ColorfulPortals.enderPearlColouredReflective))
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
			int maxSize = ColorfulPortals.maxPortalSizeCheck * ColorfulPortals.maxPortalSizeCheck - 1;
			if (!world.isAirBlock(pos.up()) && world.getBlockState(pos.up()).getBlock() != ColorfulPortals.colourfulWater) {
				return false;
			}
			if (!ColorfulPortals.isFrameBlock(world.getBlockState(pos.down()).getBlock())) {
				return false;
			}
			IBlockState frameState = world.getBlockState(pos.down());
			Block frameBlock = frameState.getBlock();
			int frameMeta = frameBlock.getMetaFromState(frameState);

			boolean[] dirs = { true, true, true };
			int i = 0;
			int thisId = 0;
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.south(i))) || (world.getBlockState(pos.south(i)).getBlock() == ColorfulPortals.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.south(i)).getBlock() != frameBlock) || (ColorfulPortals.getMeta(world, pos.south(i)) != frameMeta))
			{
				dirs[2] = false;
				dirs[1] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.north(i))) || (world.getBlockState(pos.north(i)).getBlock() == ColorfulPortals.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.north(i)).getBlock() != frameBlock) || (ColorfulPortals.getMeta(world, pos.north(i)) != frameMeta))
			{
				dirs[2] = false;
				dirs[1] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.up(i))) || (world.getBlockState(pos.up(i)).getBlock() == ColorfulPortals.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.up(i)).getBlock() != frameBlock) || (ColorfulPortals.getMeta(world, pos.up(i)) != frameMeta))
			{
				dirs[2] = false;
				dirs[0] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.down(i))) || (world.getBlockState(pos.down(i)).getBlock() == ColorfulPortals.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.down(i)).getBlock() != frameBlock) || (ColorfulPortals.getMeta(world, pos.down(i)) != frameMeta))
			{
				dirs[2] = false;
				dirs[0] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.east(i))) || (world.getBlockState(pos.east(i)).getBlock() == ColorfulPortals.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.east(i)).getBlock() != frameBlock) || (ColorfulPortals.getMeta(world, pos.east(i)) != frameMeta))
			{
				dirs[0] = false;
				dirs[1] = false;
			}
			for (i = 0; (i < maxSize + 1) && ((world.isAirBlock(pos.west(i))) || (world.getBlockState(pos.west(i)).getBlock() == ColorfulPortals.colourfulWater)); i++) {}
			if ((world.getBlockState(pos.west(i)).getBlock() != frameBlock) || (ColorfulPortals.getMeta(world, pos.west(i)) != frameMeta))
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
					ColorfulPortals.CPLSet visited = new ColorfulPortals.CPLSet();
					Stack<ColorfulPortals.ColorfulPortalLocation> toVisit = new Stack();

					toVisit.push(new ColorfulPortals.ColorfulPortalLocation(pos, world.provider.getDimension(), ColorfulPortals.getShiftedCPMetadata(world, pos)));

					visited.add(toVisit.peek());

					int maxSizeTotal = (ColorfulPortals.maxPortalSizeCheck * ColorfulPortals.maxPortalSizeCheck - 1) * (ColorfulPortals.maxPortalSizeCheck * ColorfulPortals.maxPortalSizeCheck - 1);
					for (int j = 0; (j < maxSizeTotal) && (!toVisit.empty()) && (dirs[d]); j++)
					{
						ColorfulPortals.ColorfulPortalLocation current = (ColorfulPortals.ColorfulPortalLocation)toVisit.pop();
						BlockPos currentPos = new BlockPos(current.xPos, current.yPos, current.zPos);
						if ((dirs[0]) || (dirs[2]))
						{
							Block nextBlock = world.getBlockState(currentPos.up()).getBlock();
							int nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.up()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColorfulPortals.colourfulWater)) || (Math.abs(current.xPos - x) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.yPos + 1 - y) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.zPos - z) > ColorfulPortals.maxPortalSizeCheck)) {
								if (xLook) {
									dirs[0] = false;
								} else if (zLook) {
									dirs[2] = false;
								}
							}
							nextBlock = world.getBlockState(currentPos.down()).getBlock();
							nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.down()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColorfulPortals.colourfulWater)) || (Math.abs(current.xPos - x) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.yPos - 1 - y) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.zPos - z) > ColorfulPortals.maxPortalSizeCheck)) {
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
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColorfulPortals.colourfulWater)) || (Math.abs(current.xPos - x) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.yPos - y) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.zPos + 1 - z) > ColorfulPortals.maxPortalSizeCheck)) {
								if (xLook) {
									dirs[0] = false;
								} else if (yLook) {
									dirs[1] = false;
								}
							}
							nextBlock = world.getBlockState(currentPos.west()).getBlock();
							nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.west()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColorfulPortals.colourfulWater)) || (Math.abs(current.xPos - x) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.yPos - y) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.zPos - 1 - z) > ColorfulPortals.maxPortalSizeCheck)) {
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
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColorfulPortals.colourfulWater)) || (Math.abs(current.xPos + 1 - x) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.yPos - y) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.zPos - z) > ColorfulPortals.maxPortalSizeCheck)) {
								if (yLook) {
									dirs[1] = false;
								} else if (zLook) {
									dirs[2] = false;
								}
							}
							nextBlock = world.getBlockState(currentPos.north()).getBlock();
							nextMeta = nextBlock.getMetaFromState(world.getBlockState(currentPos.north()));
							if (((nextBlock != frameBlock) && (nextMeta != frameMeta) && (nextBlock != Blocks.AIR) && (nextBlock != ColorfulPortals.colourfulWater)) || (Math.abs(current.xPos - 1 - x) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.yPos - y) > ColorfulPortals.maxPortalSizeCheck) || (Math.abs(current.zPos - z) > ColorfulPortals.maxPortalSizeCheck)) {
								if (yLook) {
									dirs[1] = false;
								} else if (zLook) {
									dirs[2] = false;
								}
							}
						}
						if ((dirs[d]) && (Math.abs(x - current.xPos) < ColorfulPortals.maxPortalSizeCheck) && (y <= 256) && (y > 0) && (Math.abs(z - current.zPos) < ColorfulPortals.maxPortalSizeCheck))
						{
							if ((zLook) || (xLook))
							{
								if (world.isAirBlock(currentPos.up()) || world.getBlockState(currentPos.up()).getBlock() == ColorfulPortals.colourfulWater)
								{
									ColorfulPortals.ColorfulPortalLocation temp = new ColorfulPortals.ColorfulPortalLocation(currentPos.up(), world.provider.getint(), ColorfulPortals.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
								if (world.isAirBlock(currentPos.down()) || world.getBlockState(currentPos.down()).getBlock() == ColorfulPortals.colourfulWater)
								{
									ColorfulPortals.ColorfulPortalLocation temp = new ColorfulPortals.ColorfulPortalLocation(currentPos.down(), world.provider.getint(), ColorfulPortals.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
							}
							if ((zLook) || (yLook))
							{
								if (world.isAirBlock(currentPos.south()) || world.getBlockState(currentPos.south()).getBlock() == ColorfulPortals.colourfulWater)
								{
									ColorfulPortals.ColorfulPortalLocation temp = new ColorfulPortals.ColorfulPortalLocation(currentPos.south(), world.provider.getint(), ColorfulPortals.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
								if (world.isAirBlock(currentPos.north()) || world.getBlockState(currentPos.north()).getBlock() == ColorfulPortals.colourfulWater)
								{
									ColorfulPortals.ColorfulPortalLocation temp = new ColorfulPortals.ColorfulPortalLocation(currentPos.north(), world.provider.getint(), ColorfulPortals.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
							}
							if ((yLook) || (xLook))
							{
								if (world.isAirBlock(currentPos.east()) || world.getBlockState(currentPos.east()).getBlock() == ColorfulPortals.colourfulWater)
								{
									ColorfulPortals.ColorfulPortalLocation temp = new ColorfulPortals.ColorfulPortalLocation(currentPos.east(), world.provider.getDimension(), ColorfulPortals.getShiftedCPMetadataByFrameBlock(frameState));
									if (!visited.contains(temp))
									{
										toVisit.push(temp);
										visited.add(temp);
									}
								}
								if (world.isAirBlock(currentPos.west()) || world.getBlockState(currentPos.west()).getBlock() == ColorfulPortals.colourfulWater)
								{
									ColorfulPortals.ColorfulPortalLocation temp = new ColorfulPortals.ColorfulPortalLocation(currentPos.west(), world.provider.getDimension(), ColorfulPortals.getShiftedCPMetadataByFrameBlock(frameState));
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
						for (ColorfulPortals.ColorfulPortalLocation cpl : visited) {
							if (((dirs[0]) && (cpl.xPos == x)) || ((dirs[1]) && (cpl.yPos == y)) || ((dirs[2]) && (cpl.zPos == z))) {
								ColorfulPortals.setFramedCPBlock(world, new BlockPos(cpl.xPos, cpl.yPos, cpl.zPos), frameBlock, frameMeta, 2);
							}
						}
						int shiftedMeta = ColorfulPortals.getShiftedCPMetadata(world, pos);
						boolean creationSuccess = true;
						if (addLocationToList) {
							creationSuccess = ColorfulPortals.addPortalToList(new ColorfulPortals.ColorfulPortalLocation(pos, world.provider.getDimension(), shiftedMeta));
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
		if (!ColorfulPortals.tooManyPortals(state))
		{
			ColorfulPortals.ColorfulPortalLocation destination = createDestination(sameDim, world.provider.getDimension(), ColorfulPortals.getShiftedCPMetadata(world, pos));
			if (destination == null) {
				return;
			}
			creationSuccess = ColorfulPortals.addPortalToList(destination);
		}
		float soundPitch = 1.8F;
		if (sameDim) {
			soundPitch = 1.5F;
		}
		goCrazyX = pos.getX();
		goCrazyY = pos.getY();
		goCrazyZ = pos.getZ();
		//world.playSound(EntityPlayer, pos, goCrazyZ, "ColorfulPortals:teleport", 1.0F, 1.5F);
		world.playSound(goCrazyX, goCrazyY, goCrazyZ, "", category, 1.0F, 1.5F, distanceDelay);
	}
//this was static
	private ColorfulPortals.ColorfulPortalLocation createDestination(boolean isSameDim, int oldDim, int meta)
	{
		int unshiftedMeta = ColorfulPortals.unshiftCPMetadata(meta);
		Block portalBlock = ColorfulPortals.getCPBlockByShiftedMetadata(meta);
		Block frameBlock = ColorfulPortals.getFrameBlockByShiftedMetadata(meta);

		byte var2 = 16;
		double var3 = -1.0D;

		int maxDistance = ColorfulPortals.maxPortalGenerationDistance;
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
			//worldServer = MinecraftServer.getServer().worldServerForDimension(oldDim);
			worldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().worldServerForDimension(oldDim);
			dimension = oldDim;
		}
		else
		{
			WorldServer[] wServers = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().worldServers;
			int indexStart = rand.nextInt(wServers.length);
			int index = indexStart;

			worldServer = null;
			do
			{
				if (ColorfulPortals.isDimensionValidForDestination(wServers[index].provider.getDimension())) {
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
											if (((var25 < 0) && (!worldServer.getBlockState(new BlockPos(var26, var27, var28)).getBlock().getMaterial(getDefaultState()).isSolid())) || ((var25 >= 0) && (!worldServer.isAirBlock(new BlockPos(var26, var27, var28))))) {
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
											if (((var24 < 0) && (!worldServer.getBlockState(new BlockPos(var25, var26, var27)).getBlock().getMaterial(getDefaultState()).isSolid())) || ((var24 >= 0) && (!worldServer.isAirBlock(new BlockPos(var25, var26, var27))))) {
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
		return new ColorfulPortals.ColorfulPortalLocation(new BlockPos(var29, var15, var16), dimension, meta);
	}

	public static void playColourfulTeleportSound(World world, double x, double y, double z, BlockPos pos, Random rand)
	{
		//world.playSound(x, y, z, "ColorfulPortals:teleport", 1.0F, 1.0F);
		world.playSound((double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, SoundEvents.BLOCK_PORTAL_AMBIENT, SoundCategory.BLOCKS, 0.5F, rand.nextFloat() * 0.4F + 0.8F, false);
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

	private static Entity teleportColourfully(World world, BlockPos startPos, Entity entity)
	{
		ColorfulPortals.ColorfulPortalLocation destination = ColorfulPortals.getColorfulDestination(world, startPos);
		//Make sure the dimension we are trying to teleport to exists first!
		if(FMLCommonHandler.instance().getMinecraftServerInstance().getServer().worldServerForDimension(destination.dimension) == null) {
			return entity;
		}
		int meta = destination.portalMetadata;
		double x = destination.xPos + 0.5D;
		double y = destination.yPos + 0.1D + (ColorfulPortals.isStandaloneCPBlock(FMLCommonHandler.instance().getMinecraftServerInstance().getServer().worldServerForDimension(destination.dimension).getBlockState(new BlockPos(destination.xPos, destination.yPos, destination.zPos)).getBlock()) ? 1.0D : 0.0D);
		double z = destination.zPos + 0.5D;
		WorldServer newWorldServer = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().worldServerForDimension(destination.dimension);
		
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
			Iterator iterator = FMLCommonHandler.instance().getMinecraftServerInstance().getServer().getPlayerList().getPlayerList().iterator();
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

	private static Entity teleportEntityColourfully(World world, double x, double y, double z, Entity entity, ColorfulPortals.ColorfulPortalLocation destination)
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

	private static void teleportPlayerColourfully(World world, double x, double y, double z, EntityPlayerMP player, ColorfulPortals.ColorfulPortalLocation destination)
	{
		int meta = destination.portalMetadata;
		int dimension = destination.dimension;
		int currentDimension = player.worldObj.provider.getDimension();
		if (currentDimension != dimension)
		{
			if (!world.isRemote) {
				if (currentDimension != 1) {
					player.mcServer.getPlayerList ().transferPlayerToDimension(player, dimension, new ColourfulTeleporter(player.mcServer.worldServerForDimension(dimension), x, y, z));
				} else {
					forceTeleportPlayerFromEnd(player, dimension, new ColourfulTeleporter(player.mcServer.worldServerForDimension(dimension), x, y, z));
				}
				player.connection.sendPacket(new SPacketSetExperience(player.experience, player.experienceTotal, player.experienceLevel));
			}
		}
		else {
			//player.playerNetServerHandler.setPlayerLocation(x, y, z, player.rotationYaw, player.rotationPitch);
			player.setLocationAndAngles(x, y, z, player.rotationYaw, player.rotationPitch);
			world.updateEntityWithOptionalForce(player, false);
		}
	}

	private static void doAfterTeleportStuff(World world, double x, double y, double z, int meta, Entity entity, World newWorld, double newX, double newY, double newZ)
	{
		playColourfulTeleportSound(world, x, y, z);
		playColourfulTeleportSound(newWorld, newX, newY, newZ);
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

		player.mcServer.getConfigurationManager().preparePlayer(player, worldServerOld);
		player.connection.setPlayerLocation(player.posX, player.posY, player.posZ, player.rotationYaw, player.rotationPitch);
		player.theItemInWorldManager.setWorld(worldServerNew);
		player.mcServer.getConfigurationManager().updateTimeAndWeatherForPlayer(player, worldServerNew);
		player.mcServer.getConfigurationManager().syncPlayerInventory(player);
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
			MinecraftServer minecraftserver = FMLCommonHandler.instance().getMinecraftServerInstance().getServer();
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
				minecraftserver.getConfigurationManager().transferEntityToWorld(toTeleport, oldDimension, worldServerOld, worldServerNew, new ColourfulTeleporter(worldServerOld, x, y, z));
			}
			toTeleport.worldObj.theProfiler.endStartSection("reloading");
			Entity entity = EntityList.createEntityByName(EntityList.getEntityString(toTeleport), worldServerNew);
			if (entity != null)
			{
				entity.copyDataFromOld(toTeleport);
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
				if ((!ColorfulPortals.isCPBlock(entity.worldObj.getBlockState(new BlockPos((int)Math.floor(entity.posX), (int)Math.floor(entity.posY), (int)Math.floor(entity.posZ))).getBlock())) && (!ColorfulPortals.isCPBlock(entity.worldObj.getBlockState(new BlockPos((int)Math.floor(entity.posX), (int)Math.floor(entity.posY - 1.0D), (int)Math.floor(entity.posZ))).getBlock())) && (entity.getEntityData().getInteger("ColourfulPortalDelay") > 0) && (inCP)) {
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
				//riding.mountEntity(mount);
				riding.dismountRidingEntity();
			}
			
			toStack.clear();
		}
		else {
			stackDelay--;
		}
	}
	
}
