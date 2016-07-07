package com.tmtravlr.colorfulportals;

import java.io.File;
import java.io.FileOutputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Stack;
import java.util.TreeSet;

import org.apache.logging.log4j.Logger;

import com.tmtravlr.colourfulportalsmod.BlockColourfulWater;
import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod;
import com.tmtravlr.colourfulportalsmod.ItemBucketColourfulWater;
import com.tmtravlr.colourfulportalsmod.ItemEnderPearlColoured;
import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod.CPLSet;
import com.tmtravlr.colourfulportalsmod.ColourfulPortalsMod.ColorfulPortalLocation;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDispenser;
import net.minecraft.block.state.IBlockState;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.IBlockSource;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntityDispenser;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid = ColorfulPortals.MODID, name = ColorfulPortals.MODNAME, version = ColorfulPortals.MODVERSION, dependencies = "required-after:Forge@[11.16.0.1865,)", useMetadata = true)
public class ColorfulPortals {
	


	    public static final String MODID = "colourfulportalsmod";
	    public static final String MODNAME = "Colourful Portals Mod";
	    public static final String MODVERSION = "2.0.0";
	    public static HashMap<Integer, BlockColorfulPortal> cpBlocks = new HashMap();
		public static HashMap<Integer, BlockStandaloneCP> scpBlocks = new HashMap();
		public static HashMap<Integer, Block> frameBlocks = new HashMap();
		public static int colourfulPortalRenderId;
		public static int maxPortalsPerType = -1;
		private static LinkedList<ColorfulPortalLocation> colorfulPortals = new LinkedList();
		private File saveLocation;
		private boolean loaded;
		public String currentFolder;
		public static ColorfulPortals cpmod;
		public static int maxPortalSizeCheck = 16;
		private static final boolean debug = false;
		public static int maxPortalGenerationDistance = 3000;
		public static int xpLevelMixingCost = 5;
		public static int xpLevelRemixingCost = 2;
		public static boolean useDestinationBlackList = true;
		public static boolean useDestinationWhiteList = false;
		public static boolean xpBottleCrafting = false;
		public static int[] destinationBlackList = { 1 };
		public static int[] destinationWhiteList = { 0, -1 };
		public static boolean useDimensionBlackList = false;
		public static boolean useDimensionWhiteList = false;
		public static int[] dimensionBlackList = new int[0];
		public static int[] dimensionWhiteList = { 0, 1, -1 };
		public static String[] frameBlockNames = { "wool", "stained_hardened_clay", "stained_glass" };
		private static String bucketColourfulWaterEmptyId = "bucket_colourful_water_empty";
		private static String bucketColourfulWaterId = "bucket_colourful_water";
		private static String bucketColourfulWaterUnmixedId = "bucket_colourful_water_unmixed";
		private static String bucketColourfulWaterPartMixedId = "bucket_colourful_water_part_mixed";
		private static String bucketColourfulWaterFirstId = "bucket_colourful_water_first";
		private static String colourfulEnderPearlId = "colourful_ender_pearl";
		private static String colourfulEnderPearlReflectiveId = "colourful_ender_pearl_reflective";
		private static String colourfulWaterId = "colourful_water";
		private static String colourfulPortalIdPrefix = "cp_";
		private static String standaloneCPIdPrefix = "scp_";

	    @SidedProxy
	    public static CommonProxy proxy;

	    @Mod.Instance
	    public static ColorfulPortals instance;

	    public static Logger logger;

	    @Mod.EventHandler
	    public void preInit(FMLPreInitializationEvent event){
	        logger = event.getModLog();
	        proxy.preInit(event);
	        cpmod = this;
			boolean addColourfulWaterToDungeonChests = true;

			Configuration config = new Configuration(event.getSuggestedConfigurationFile());

			config.load();


			maxPortalGenerationDistance = config.getInt("Random Portal Generation Max Distance", "other", 3000, 0, 30000000, "Maximum distance away from you a randomized portal will generate.");
			maxPortalsPerType = config.getInt("Maximum Number of Portals per Type (Colour and Material)", "other", -1, -1, Integer.MAX_VALUE, "Maximum number of portals you can make per type. -1 means unlimited.");
			maxPortalSizeCheck = config.getInt("Maximum Portal Size (Make Bigger for Larger Portals)", "other", 16, 1, Integer.MAX_VALUE, "Limit on the maximum size of portal to prevent lag from accidentally creating massive portals.\nThe portal will create up to this number of blocks away from where you place the colourful water.");
			xpLevelMixingCost = config.getInt("Number of XP Levels Needed to Mix Colourful Water", "other", 5, 0, Integer.MAX_VALUE, "Levels of XP you need to mix the colourful water from a bucket of dyes.");
			xpLevelRemixingCost = config.getInt("Number of XP Levels Needed to Re-Mix Colourful Water", "other", 2, 0, Integer.MAX_VALUE, "Levels of XP that you need to mix the partially enchanted bucket of dyes.");
			xpBottleCrafting = config.getBoolean("Allow crafting of colourful water with XP bottles (for automation)", "other", false, "Adds a crafting recipe for the colourful water using XP bottles.");
			addColourfulWaterToDungeonChests = config.getBoolean("Add Buckets of Colourful Water to Dungeon Chests?", "other", true, "If set to true, full and empty buckets of colourful water will occasionally spawn in chests.");
			if (xpLevelRemixingCost > xpLevelMixingCost) {
				xpLevelRemixingCost = xpLevelMixingCost;
			}
			config.addCustomCategoryComment("random_destination_blacklist", "If set to true, random destination portals with random dimensions\nwill not generate in any of the dimensions in this list. They can\nstill be created with same-dimension random destinations or placed\nmanually. Defaults to true. Takes precedence over the whitelist.");

			config.addCustomCategoryComment("random_destination_whitelist", "If set to true, random destination portals with random dimensions\nwill only generate in these dimensions. Ones with the same dimension\ncan still generate elsewhere. Defaults to false.");

			config.addCustomCategoryComment("dimension_blacklist", "If set to true, portals cannot be created in these dimensions\nat all, whether framed, single block, or 'random destination'\nportals. Takes precedence over the whitelist. Defaults to false.");

			config.addCustomCategoryComment("dimension_whitelist", "If set to true, portals can ONLY be created in the given dimensions,\nwhether framed, single block, or 'random destination' portals.\nDefaults to false.");

			int[] defaultBlackList = { 1 };
			int[] defaultWhiteList = { 0, -1 };
			int[] fullDefaultList = { 0, 1, -1 };
			int[] emptyList = new int[0];

			useDestinationBlackList = config.getBoolean("Use this Blacklist?", "random_destination_blacklist", true, "");
			useDestinationWhiteList = config.getBoolean("Use this Whitelist?", "random_destination_whitelist", false, "");
			destinationBlackList = config.get("random_destination_blacklist", "List of Blacklisted Dimensions for Random Generation", defaultBlackList).getIntList();
			destinationWhiteList = config.get("random_destination_whitelist", "List of Whitelisted Dimensions for Random Generation", defaultWhiteList).getIntList();
			
			useDimensionBlackList = config.getBoolean("Use this Blacklist?", "dimension_blacklist", false, "");
			useDimensionWhiteList = config.getBoolean("Use this Whitelist?", "dimension_whitelist", false, "");
			dimensionBlackList = config.get("dimension_blacklist", "List of Blacklisted Dimensions for all Portals", emptyList).getIntList();
			dimensionWhiteList = config.get("dimension_whitelist", "List of Whitelisted Dimensions for all Portals", fullDefaultList).getIntList();

			config.addCustomCategoryComment("portal_frame_types", "Blocks that can be used to make portals out of.\nThey should have 16 metadata types that represent\ncolours in the same way as wool.");

			String[] defaultPortalTypes = { "wool", "stained_hardened_clay", "stained_glass" };
			//No support for custom portal types at the moment!
			frameBlockNames = defaultPortalTypes;//= config.get("portal_frame_types", "Portal Frame Blocks", defaultPortalTypes).getStringList();
			
			config.save();
			
			/*BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(bucketColourfulWaterEmpty, new BehaviorDefaultDispenseItem()
			{
				private final BehaviorDefaultDispenseItem field_150840_b = new BehaviorDefaultDispenseItem();
				
				public ItemStack dispenseStack(IBlockSource blockSource, ItemStack itemStack)
				{
					EnumFacing enumfacing = BlockDispenser.getFacing(blockSource.getBlockMetadata());
					World world = blockSource.getWorld();
					int x = MathHelper.floor_double(blockSource.getX() + enumfacing.getFrontOffsetX());
					int y = MathHelper.floor_double(blockSource.getY() + enumfacing.getFrontOffsetY());
					int z = MathHelper.floor_double(blockSource.getZ() + enumfacing.getFrontOffsetZ());
					BlockPos pos = new BlockPos(x, y, z);
					int meta = getMeta(world, pos);
					Item item = itemStack.getItem();
					if ((world.getBlockState(pos) == ColourfulPortalsMod.colourfulWater) && (meta == 0)) {
						item = ColourfulPortalsMod.bucketColourfulWater;
					} else {
						return super.dispenseStack(blockSource, itemStack);
					}
					world.setBlockToAir(pos);
					if (--itemStack.stackSize == 0)
					{
						itemStack.setItem(item);
						itemStack.stackSize = 1;
					}
					else if (((TileEntityDispenser)blockSource.getBlockTileEntity()).addItemStack(new ItemStack(item)) < 0)
					{
						this.field_150840_b.dispense(blockSource, new ItemStack(item));
					}
					return itemStack;
				}
			}*/
	    }

	    @Mod.EventHandler
	    public void init(FMLInitializationEvent e) {
	        proxy.init(e);
	    }

	    @Mod.EventHandler
	    public void postInit(FMLPostInitializationEvent e) {
	        proxy.postInit(e);
	    }
	    //shananighanz start here
	    public static boolean isCPBlock(Block block)
		{
			return (isStandaloneCPBlock(block)) || (isFramedCPBlock(block));
		}
	    public static boolean isStandaloneCPBlock(Block block)
		{
			return scpBlocks.containsValue(block);
		}
	    public static boolean isFramedCPBlock(Block block)
		{
			return cpBlocks.containsValue(block);
		}
	    public static int getShiftedCPMetadata(IBlockAccess iba, BlockPos pos)
		{
			IBlockState state = iba.getBlockState(pos); 
			return getShiftedCPMetadata(state);
		}
	    public static int getShiftedCPMetadata(IBlockState state)
		{
			for (int i = 0; i < frameBlocks.size(); i++) {
				if ((cpBlocks.get(i) == state.getBlock()) || (scpBlocks.get(i) == state.getBlock())) {
					return getMeta(state) + 16 * i;
				}
			}
			return -1;
		}
	    public static boolean canCreatePortal(World world, BlockPos pos, IBlockState state)
		{
			if (tooManyPortals(state)) {
				return false;
			}
			if (!isDimensionValidAtAll(world.provider.getDimension())) {
				return false;
			}
			return true;
		}
	    public static boolean tooManyPortals(IBlockState state)
		{
			if (maxPortalsPerType < 0) {
				return false;
			}
			if (maxPortalsPerType == 0) {
				return true;
			}
			int portalsWithType = 0;
			for (int i = 0; i < colorfulPortals.size(); i++) {
				if (((ColorfulPortalLocation)colorfulPortals.get(i)).portalMetadata == getShiftedCPMetadata(state)) {
					portalsWithType++;
				}
			}
			if (portalsWithType >= maxPortalsPerType) {
				return true;
			}
			return false;
		}
	    public static boolean isDimensionValidAtAll(int i)
		{
			/*if (useDimensionWhiteList)
			{
				if (dimensionWhiteList.length == 0) {
					return false;
				}
				boolean inWhiteList = false;
				for (int i = 0; i < dimensionWhiteList.length; i++) {
					if (dimensionType == dimensionWhiteList[i]) {
						inWhiteList = true;
					}
				}
				if (!inWhiteList) {
					return false;
				}
			}
			if (useDimensionBlackList) {
				for (int i = 0; i < dimensionBlackList.length; i++) {
					if (dimensionType == dimensionBlackList[i]) {
						return false;
					}
				}
			}*/
			return true;
		}
	    public static int getMeta(IBlockAccess iba, BlockPos pos) {
			return getMeta(iba.getBlockState(pos));
		}
		
		public static int getMeta(IBlockState state) {
			return state.getBlock().getMetaFromState(state);
		}
		public static boolean addPortalToList(ColorfulPortalLocation newLocation)
		{
			if (!colorfulPortals.contains(newLocation))
			{
				colorfulPortals.add(newLocation);

				savePortals();
				return true;
			}
			return false;
		}
		private static void savePortals()
		{
			try
			{
				FileOutputStream fOut = new FileOutputStream(cpmod.saveLocation);
				ObjectOutputStream oOut = new ObjectOutputStream(fOut);
				oOut.writeObject(cpmod);
				oOut.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		public static void deletePortal(ColorfulPortalLocation locToDelete)
		{
			if (colorfulPortals.remove(locToDelete)) {
				savePortals();
			}
		}
		public static boolean isFrameBlock(Block block)
		{
			return frameBlocks.containsValue(block);
		}
		public static boolean isPortalOrFrameBlock(IBlockAccess iba, BlockPos pos)
		{
			return (isFramedCPBlock(iba.getBlockState(pos).getBlock())) || (isFrameBlock(iba.getBlockState(pos).getBlock()));
		}
		public static int getShiftedCPMetadataByFrameBlock(IBlockState state)
		{
			for (int i = 0; i < frameBlocks.size(); i++) {
				if (frameBlocks.get(i) == state.getBlock()) {
					return getMeta(state) + 16 * i;
				}
			}
			return -1;
		}
		public static void setFramedCPBlock(World world, BlockPos pos, Block frameBlock, int meta, int flag)
		{
			int index = getIndexFromShiftedMetadata(getShiftedCPMetadataByFrameBlock(frameBlock.getStateFromMeta(meta)));
			Block block = (Block)cpBlocks.get(index);
			world.setBlockState(pos, block.getStateFromMeta(meta), flag);
		}
		public static int getIndexFromShiftedMetadata(int meta)
		{
			return (int)Math.floor(meta / 16);
		}
		public static int unshiftCPMetadata(int meta)
		{
			return meta % 16;
		}
		public static Block getCPBlockByShiftedMetadata(int meta)
		{
			return (Block)cpBlocks.get(getIndexFromShiftedMetadata(meta));
		}
		public static Block getFrameBlockByShiftedMetadata(int meta)
		{
			return frameBlocks.get(getIndexFromShiftedMetadata(meta));
		}
		public static boolean isDimensionValidForDestination(int dimension)
		{
			if (!isDimensionValidAtAll(dimension)) {
				return false;
			}
			if (useDestinationWhiteList)
			{
				if (destinationWhiteList.length == 0) {
					return false;
				}
				boolean inWhiteList = false;
				for (int i = 0; i < destinationWhiteList.length; i++) {
					if (dimension == destinationWhiteList[i]) {
						inWhiteList = true;
					}
				}
				if (!inWhiteList) {
					return false;
				}
			}
			if (useDestinationBlackList) {
				for (int i = 0; i < destinationBlackList.length; i++) {
					if (dimension == destinationBlackList[i]) {
						return false;
					}
				}
			}
			return true;
		}
		public static ColorfulPortalLocation getColorfulDestination(World world, BlockPos pos)
		{
			if (colorfulPortals.size() > 0)
			{
				ColorfulPortalLocation start = findCPLocation(world, pos);


				int originalPos = colorfulPortals.indexOf(start);
				if (originalPos == -1) {
					return new ColorfulPortalLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
				}
				int size = colorfulPortals.size();
				for (int i = 0; i < size; i++)
				{
					int index = i + originalPos + 1;
					if (index >= size) {
						index -= size;
					}
					ColorfulPortalLocation current = (ColorfulPortalLocation)colorfulPortals.get(index);
					if (current.portalMetadata == start.portalMetadata) {
						if(server.getServer().worldServerForDimension(current.dimension) != null) {
							return current;
						}
					}
				}
				return start;
			}
			return new ColorfulPortalLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
		}
		public static MinecraftServer server;
		
		public static ColorfulPortalLocation findCPLocation(World world, BlockPos pos)
		{
			if (!isCPBlock(world.getBlockState(pos).getBlock())) {
				return null;
			}
			if (isStandaloneCPBlock(world.getBlockState(pos).getBlock())) {
				return new ColorfulPortalLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
			}
			boolean xDir = true;
			boolean yDir = true;
			boolean zDir = true;
			int i = 0;
			int maxSize = maxPortalSizeCheck * maxPortalSizeCheck + 1;
			for (i = 0; (i < maxSize) && (isCPBlock(world.getBlockState(pos.add(i, 0, 0)).getBlock())); i++) {}
			if (!isFrameBlock(world.getBlockState(pos.add(i, 0, 0)).getBlock()))
			{
				zDir = false;
				yDir = false;
			}
			for (i = 0; (i < maxSize) && (isCPBlock(world.getBlockState(pos.add(-i, 0, 0)).getBlock())); i++) {}
			if (!isFrameBlock(world.getBlockState(pos.add(-i, 0, 0)).getBlock()))
			{
				zDir = false;
				yDir = false;
			}
			for (i = 0; (i < maxSize) && (isCPBlock(world.getBlockState(pos.add(0, i, 0)).getBlock())); i++) {}
			if (!isFrameBlock(world.getBlockState(pos.add(0, i, 0)).getBlock()))
			{
				zDir = false;
				xDir = false;
			}
			for (i = 0; (i < maxSize) && (isCPBlock(world.getBlockState(pos.add(0, -i, 0)).getBlock())); i++) {}
			if (!isFrameBlock(world.getBlockState(pos.add(0, -i, 0)).getBlock()))
			{
				zDir = false;
				xDir = false;
			}
			for (i = 0; (i < maxSize) && (isCPBlock(world.getBlockState(pos.add(0, 0, i)).getBlock())); i++) {}
			if (!isFrameBlock(world.getBlockState(pos.add(0, 0, i)).getBlock()))
			{
				xDir = false;
				yDir = false;
			}
			for (i = 0; (i < maxSize) && (isCPBlock(world.getBlockState(pos.add(0, 0, -i)).getBlock())); i++) {}
			if (!isFrameBlock(world.getBlockState(pos.add(0, 0, -i)).getBlock()))
			{
				xDir = false;
				yDir = false;
			}
			if ((!xDir) && (!yDir) && (!zDir)) {
				return null;
			}
			CPLSet visited = new CPLSet();
			Stack<ColorfulPortalLocation> toVisit = new Stack();

			toVisit.push(new ColorfulPortalLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos)));

			visited.add(toVisit.peek());
			while (!toVisit.empty())
			{
				ColorfulPortalLocation current = (ColorfulPortalLocation)toVisit.pop();
				if (colorfulPortals.contains(current)) {
					return current;
				}
				BlockPos currentPos = new BlockPos(current.xPos, current.yPos, current.zPos);
				if ((zDir) || (xDir))
				{
					if (isCPBlock(world.getBlockState(currentPos.add(0, 1, 0)).getBlock()))
					{
						ColorfulPortalLocation temp = new ColorfulPortalLocation(currentPos.add(0, 1, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 1, 0)));
						if (!visited.contains(temp))
						{
							toVisit.push(temp);
							visited.add(temp);
						}
					}
					if (isCPBlock(world.getBlockState(currentPos.add(0, -1, 0)).getBlock()))
					{
						ColorfulPortalLocation temp = new ColorfulPortalLocation(currentPos.add(0, -1, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, -1, 0)));
						if (!visited.contains(temp))
						{
							toVisit.push(temp);
							visited.add(temp);
						}
					}
				}
				if ((zDir) || (yDir))
				{
					if (isCPBlock(world.getBlockState(currentPos.add(1, 0, 0)).getBlock()))
					{
						ColorfulPortalLocation temp = new ColorfulPortalLocation(currentPos.add(1, 0, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(1, 0, 0)));
						if (!visited.contains(temp))
						{
							toVisit.push(temp);
							visited.add(temp);
						}
					}
					if (isCPBlock(world.getBlockState(currentPos.add(-1, 0, 0)).getBlock()))
					{
						ColorfulPortalLocation temp = new ColorfulPortalLocation(currentPos.add(-1, 0, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(-1, 0, 0)));
						if (!visited.contains(temp))
						{
							toVisit.push(temp);
							visited.add(temp);
						}
					}
				}
				if ((yDir) || (xDir))
				{
					if (isCPBlock(world.getBlockState(currentPos.add(0, 0, 1)).getBlock()))
					{
						ColorfulPortalLocation temp = new ColorfulPortalLocation(currentPos.add(0, 0, 1), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 0, 1)));
						if (!visited.contains(temp))
						{
							toVisit.push(temp);
							visited.add(temp);
						}
					}
					if (isCPBlock(world.getBlockState(currentPos.add(0, 0, -1)).getBlock()))
					{
						ColorfulPortalLocation temp = new ColorfulPortalLocation(currentPos.add(0, 0, -1), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 0, -1)));
						if (!visited.contains(temp))
						{
							toVisit.push(temp);
							visited.add(temp);
						}
					}
				}
			}
			return null;
		}
		
		
		
		
		
		
		
		//////////////////////////////////////////////////////////////////////
	    public static class CommonProxy {
	        public void preInit(FMLPreInitializationEvent e) {
	            // Initialize our items,te,blocks,gen,dim etc

	        }

	        public void init(FMLInitializationEvent e) {
	        	/*EKSOredict.oreRegistration();
	        	Craftyeez.initCrafting();
	            Craftyeez.initOreRecipes();
	            MinecraftForge.EVENT_BUS.register(new PigLootTables());
	            MinecraftForge.EVENT_BUS.register(new SheepLootTables());
	            MinecraftForge.EVENT_BUS.register(new DUNGEONLT());
	            MinecraftForge.EVENT_BUS.register(new MOLT());
	            GameRegistry.registerWorldGenerator(new LumberJackGenerate(), 0);*/
	        }

	        public void postInit(FMLPostInitializationEvent e) {

	        }
	    }


	    public static class ClientProxy extends CommonProxy {
	        @Override
	        public void preInit(FMLPreInitializationEvent e) {
	            super.preInit(e);

	            OBJLoader.INSTANCE.addDomain(MODID);
	            // Typically initialization of models and such goes here:
	        }

	        @Override
	        public void init(FMLInitializationEvent e) {
	            super.init(e);
	        }
	    }

	    public static class ServerProxy extends CommonProxy {

	    }
	    /*public static class ColorfulPortalLocation implements Serializable
		{
			int xPos;
			int yPos;
			int zPos;
			int dimension;
			int portalMetadata;

			public ColorfulPortalLocation(BlockPos pos, int dimensionType, int meta)
			{
				this.xPos = pos.getX();
				this.yPos = pos.getY();
				this.zPos = pos.getZ();
				this.dimension = dimensionType;
				this.portalMetadata = meta;
			}

			public boolean equals(Object o)
			{
				if ((o == null) || (!(o instanceof ColorfulPortalLocation))) {
					return false;
				}
				ColorfulPortalLocation other = (ColorfulPortalLocation)o;
				return (this.xPos == other.xPos) && (this.yPos == other.yPos) && (this.zPos == other.zPos) && (this.dimension == other.dimension) && (this.portalMetadata == other.portalMetadata);
			}

			public String toString()
			{
				return "CPL[meta=" + this.portalMetadata + ", x=" + this.xPos + ", y=" + this.yPos + ", z=" + this.zPos + ", dim=" + this.dimension + "]";
			}
		}*/
	    public static class CPLSet
		extends TreeSet<ColorfulPortals.ColorfulPortalLocation>
		{
			public CPLSet()
			{
				super(CPLcomparator);
			}
		}
	    public static Comparator<ColorfulPortalLocation> CPLcomparator = new Comparator<ColorfulPortalLocation>()
		{

	@Override
	public int compare(ColorfulPortals.ColorfulPortalLocation first, ColorfulPortals.ColorfulPortalLocation second)
	{
		if (first.portalMetadata != second.portalMetadata) {
			return second.portalMetadata - first.portalMetadata;
		}
		if (first.dimension != second.dimension) {
			return second.dimension - first.dimension;
		}
		if (first.xPos != second.xPos) {
			return second.xPos - first.xPos;
		}
		if (first.yPos != second.yPos) {
			return second.yPos - first.yPos;
		}
		if (first.zPos != second.zPos) {
			return second.zPos - first.zPos;
		}
		return 0;
	}
		};

		public static class ColorfulPortalLocation implements Serializable
		{
			int xPos;
			int yPos;
			int zPos;
			int dimension;
			int portalMetadata;

			public ColorfulPortalLocation(BlockPos pos, int dim, int meta)
			{
				this.xPos = pos.getX();
				this.yPos = pos.getY();
				this.zPos = pos.getZ();
				this.dimension = dim;
				this.portalMetadata = meta;
			}

			public boolean equals(Object o)
			{
				if ((o == null) || (!(o instanceof ColorfulPortalLocation))) {
					return false;
				}
				ColorfulPortalLocation other = (ColorfulPortalLocation)o;
				return (this.xPos == other.xPos) && (this.yPos == other.yPos) && (this.zPos == other.zPos) && (this.dimension == other.dimension) && (this.portalMetadata == other.portalMetadata);
			}

			public String toString()
			{
				return "CPL[meta=" + this.portalMetadata + ", x=" + this.xPos + ", y=" + this.yPos + ", z=" + this.zPos + ", dim=" + this.dimension + "]";
			}
		}
	}

