package com.tmtravlr.colourfulportalsmod;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;
import java.util.TreeSet;

import com.tmtravlr.colourfulportalsmod.init.ColourfulBlocks;
import com.tmtravlr.colourfulportalsmod.init.ColourfulConfig;
import com.tmtravlr.colourfulportalsmod.init.ColourfulItems;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;

@Mod(modid="colourfulportalsmod", name="Colourful Portals Mod", version="1.4.3")
public class ColourfulPortalsMod
{
	@Mod.Instance("colourfulPortalsMod")
	public static ColourfulPortalsMod colourfulPortalsMod;
	@SidedProxy(clientSide="com.tmtravlr.colourfulportalsmod.ClientProxy", serverSide="com.tmtravlr.colourfulportalsmod.CommonProxy")
	public static CommonProxy proxy;
	public static final ColourfulFluid colourfulFluid = new ColourfulFluid();
	public static BlockColourfulPortal BlockColourfulPortal;
	//public static HashMap<Integer, BlockColourfulPortal> cpBlocks = new HashMap();
	//public static HashMap<Integer, BlockStandaloneCP> scpBlocks = new HashMap();
	public static List<BlockColourfulPortal> cpBlocks = new ArrayList();
	public static List<BlockStandaloneCP> scpBlocks = new ArrayList();
	public static HashMap<Integer, Block> frameBlocks = new HashMap();
	//public static List<String> frameNames = new ArrayList();
	//public static List<String> frameNames = new ArrayList<String>();
	private static final boolean debug = false;
	public static int colourfulPortalRenderId;
	public static int maxPortalGenerationDistance = 3000;
	public static int maxPortalsPerType = -1;
	public static int maxPortalSizeCheck = 16;
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
	public static String colourfulPortalIdPrefix = "cp_";
	public static String standaloneCPIdPrefix = "scp_";
	public static CreativeTabs cpTab = new CreativeTabs("colourfulPortals")
	{
		public Item getTabIconItem()
		{
			return ColourfulItems.bucketColourfulWater;
		}
	};
	private static LinkedList<ColourfulPortalLocation> colourfulPortals = new LinkedList();
	private File saveLocation;
	private boolean loaded;
	public String currentFolder;
	public static ColourfulConfig ColourfulConfig;
	public static ColourfulItems ColourfulItems;
	public static ColourfulBlocks ColourfulBlocks;

	public ColourfulPortalsMod()
	{
		this.loaded = false;
		this.currentFolder = "";
	}
	IBlockState state;
	static{
		FluidRegistry.enableUniversalBucket();
	}

	//public static RenderStandaloneCP standaloneRenderer = new RenderStandaloneCP();
	@Mod.EventHandler
	public void preInit(FMLPreInitializationEvent event)
	{
		//GameRegistry.registerBlock(colourfulWater, colourfulWaterId);
		//FluidContainerRegistry.registerFluidContainer(colourfulFluid, new ItemStack(ColourfulItems.bucketColourfulWater), new ItemStack(ColourfulItems.bucketColourfulWaterEmpty));
		FluidRegistry.addBucketForFluid(colourfulFluid);
		/*for (int i = 0; i < frameBlockNames.length; i++)
		{
			int e = 8;
			Block frameBlock = Block.getBlockFromName(frameBlockNames[i]);
			
			if(frameBlock == null || frameBlock == Blocks.AIR) {
				FMLLog.warning("[Colourful Portals] Error! Couldn't find a block with name '" + frameBlockNames[i] + "'!");
				continue;
			}
			//cpBlocks.put(i, (BlockColourfulPortal)new BlockColourfulPortal("portal_colour", Material.PORTAL).setHardness(-1.0F).setLightLevel(0.75F).setUnlocalizedName("colourfulPortal").setRegistryName(ColourfulPortalsMod.colourfulPortalIdPrefix + ColourfulPortalsMod.frameNames.get(i)));
			cpBlocks.add(e, (BlockColourfulPortal)new BlockColourfulPortal("portal_colour", Material.PORTAL).setHardness(-1.0F).setLightLevel(0.75F).setUnlocalizedName("colourfulPortal").setRegistryName(ColourfulPortalsMod.colourfulPortalIdPrefix + ColourfulPortalsMod.frameNames.get(i)));
			//scpBlocks.put(i, (BlockStandaloneCP)new BlockStandaloneCP("portal_colour", frameBlock.getMaterial(state)).setHardness(0.8F).setLightLevel(0.75F).setUnlocalizedName("standaloneColourfulPortal").setRegistryName(ColourfulPortalsMod.standaloneCPIdPrefix + ColourfulPortalsMod.frameNames.get(i)));
			scpBlocks.add(i, (BlockStandaloneCP)new BlockStandaloneCP("portal_colour", frameBlock.getMaterial(state)).setHardness(0.8F).setLightLevel(0.75F).setUnlocalizedName("standaloneColourfulPortal").setRegistryName(ColourfulPortalsMod.standaloneCPIdPrefix + ColourfulPortalsMod.frameNames.get(i)));
			frameBlocks.put(i, frameBlock);
			
			int colonIndex = frameBlockNames[i].indexOf(":");
			if (colonIndex != -1) {
				//frameNames.put(i, frameBlockNames[i].substring(0, colonIndex) + "_" + frameBlockNames[i].substring(colonIndex + 1));
				frameNames.add(i, frameBlockNames[i].substring(0, colonIndex) + "_" + frameBlockNames[i].substring(colonIndex + 1));
			}
			else {
				//frameNames.put(i, frameBlockNames[i]);
				frameNames.add(i, frameBlockNames[i]);
			}
		}*/
		/*for (int i = 0; i < cpBlocks.size(); i++)
		{
			GameRegistry.register((Block)scpBlocks.get(i));
			GameRegistry.register((Block)cpBlocks.get(i));
			//GameRegistry.register(new ItemStandaloneCP(BlockStandaloneCP).setRegistryName(BlockStandaloneCP.getRegistryName()));
		}*/
		//ColourfulConfig.config();
		ColourfulItems.instantiateItems();
		ColourfulItems.registerItems();
		ColourfulBlocks.instantiateBlocks();
		ColourfulBlocks.registerBlocks();
	}

	public static BlockStandaloneCP BlockStandaloneCP;
	
	@Mod.EventHandler
	public void load(FMLInitializationEvent event)
	{
		
		/*for (int i = 0; i < frameBlockNames.length; i++)
		{
			Block frameBlock = Block.getBlockFromName(frameBlockNames[i]);
			
			if(frameBlock == null || frameBlock == Blocks.AIR) {
				FMLLog.warning("[Colourful Portals] Error! Couldn't find a block with name '" + frameBlockNames[i] + "'!");
				continue;
			}
			cpBlocks.put(i, (BlockColourfulPortal)new BlockColourfulPortal("portal_colour", Material.PORTAL).setHardness(-1.0F).setLightLevel(0.75F).setUnlocalizedName("colourfulPortal"));
			scpBlocks.put(i, (BlockStandaloneCP)new BlockStandaloneCP("portal_colour", frameBlock.getMaterial(IBlockState)).setHardness(0.8F).setLightLevel(0.75F).setUnlocalizedName("standaloneColourfulPortal"));
			frameBlocks.put(i, frameBlock);
			
			int colonIndex = frameBlockNames[i].indexOf(":");
			if (colonIndex != -1) {
				frameNames.put(i, frameBlockNames[i].substring(0, colonIndex) + "_" + frameBlockNames[i].substring(colonIndex + 1));
			}
			else {
				frameNames.put(i, frameBlockNames[i]);
			}
		}
		
		for (int i = 0; i < cpBlocks.size(); i++)
		{
			GameRegistry.register((Block)scpBlocks.get(i));
			GameRegistry.register((Block)cpBlocks.get(i));
		}

		
		for (int f = 0; f < frameBlocks.size(); f++) {
			for (int i = 0; i < 16; i++)
			{
				ItemStack frame = new ItemStack(frameBlocks.get(f), 1, i);
				ItemStack sCPStack = new ItemStack(scpBlocks.get(f), 1, i);

				GameRegistry.addRecipe(sCPStack, new Object[] { "WWW", "WBW", "WWW", Character.valueOf('W'), frame, Character.valueOf('B'), bucketColourfulWater });
			}
		}*/
		//GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(bucketColourfulWaterPartMixed, 1), new Object[] { Items.WATER_BUCKET, bucketColourfulWaterEmpty, "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyeYellow", "dyeWhite" }));
		//GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(bucketColourfulWaterUnmixed, 1), new Object[] { Items.WATER_BUCKET, bucketColourfulWaterFirst, "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyeYellow", "dyeWhite" }));
		//GameRegistry.addRecipe(new ShapelessOreRecipe(new ItemStack(enderPearlColoured, 1), new Object[] { Items.ENDER_PEARL, "dyeBlack", "dyeRed", "dyeGreen", "dyeBrown", "dyeBlue", "dyeYellow", "dyeWhite" }));
		//GameRegistry.addRecipe(new ItemStack(bucketColourfulWaterFirst, 1), new Object[] { "G", "B", Character.valueOf('G'), Items.GOLD_INGOT, Character.valueOf('B'), Items.BUCKET });
		//GameRegistry.addRecipe(new ItemStack(bucketColourfulWaterFirst, 1), new Object[] { "IGI", " I ", Character.valueOf('G'), Items.GOLD_INGOT, Character.valueOf('I'), Items.IRON_INGOT });
		//GameRegistry.addRecipe(new ItemStack(enderPearlColouredReflective, 1), new Object[] { " Q ", "QPQ", " Q ", Character.valueOf('Q'), Items.QUARTZ, Character.valueOf('P'), enderPearlColoured });
		//GameRegistry.addShapelessRecipe(new ItemStack(bucketColourfulWaterFirst), new Object[] { new ItemStack(bucketColourfulWaterEmpty) });
		//GameRegistry.addShapelessRecipe(new ItemStack(Items.BUCKET), new Object[] { new ItemStack(bucketColourfulWaterFirst) });
		/*if(xpBottleCrafting) {
			GameRegistry.addShapelessRecipe(new ItemStack(bucketColourfulWater), new Object[] {new ItemStack(bucketColourfulWaterUnmixed), new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE)});
			GameRegistry.addShapelessRecipe(new ItemStack(bucketColourfulWater), new Object[] {new ItemStack(bucketColourfulWaterPartMixed), new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE),new ItemStack(Items.EXPERIENCE_BOTTLE)});
		}*/
		//proxy.registerOreThings();
		proxy.registerSounds();
		proxy.registerRenderers();
		proxy.registerEventHandlers();
	}

	@Mod.EventHandler
	public void postInit(FMLPostInitializationEvent event) {}

	public static boolean isStandaloneCPBlock(Block block)
	{
		//return scpBlocks.containsValue(block);
		return scpBlocks.contains(block);
	}

	public static boolean isFramedCPBlock(Block block)
	{
		//return cpBlocks.containsValue(block);
		return cpBlocks.contains(block);
	}

	public static boolean isCPBlock(Block block)
	{
		return (isStandaloneCPBlock(block)) || (isFramedCPBlock(block));
	}

	public static boolean isFrameBlock(Block block)
	{
		return frameBlocks.containsValue(block);
	}

	public static boolean isPortalOrFrameBlock(IBlockAccess iba, BlockPos pos)
	{
		return (isFramedCPBlock(iba.getBlockState(pos).getBlock())) || (isFrameBlock(iba.getBlockState(pos).getBlock()));
	}

	public static int getShiftedCPMetadata(IBlockAccess iba, BlockPos pos)
	{
		IBlockState state = iba.getBlockState(pos); 
		return getShiftedCPMetadata(state);
	}

	public static int getIndexFromShiftedMetadata(int meta)
	{
		return (int)Math.floor(meta / 16);
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

	public static int getShiftedCPMetadataByFrameBlock(IBlockState state)
	{
		for (int i = 0; i < frameBlocks.size(); i++) {
			if (frameBlocks.get(i) == state.getBlock()) {
				return getMeta(state) + 16 * i;
			}
		}
		return -1;
	}

	public static Block getCPBlockByShiftedMetadata(int meta)
	{
		return (Block)cpBlocks.get(getIndexFromShiftedMetadata(meta));
	}

	public static Block getStandaloneCPBlockByShiftedMetadata(int meta)
	{
		return (Block)scpBlocks.get(getIndexFromShiftedMetadata(meta));
	}

	public static Block getFrameBlockByShiftedMetadata(int meta)
	{
		return frameBlocks.get(getIndexFromShiftedMetadata(meta));
	}

	public static Block getCPBlockByFrameBlock(Block frameBlock)
	{
		for (int i = 0; i < frameBlocks.size(); i++) {
			if (frameBlocks.get(i) == frameBlock) {
				return (Block)cpBlocks.get(i);
			}
		}
		return null;
	}

	public static int unshiftCPMetadata(int meta)
	{
		return meta % 16;
	}

	public static void setFramedCPBlock(World world, BlockPos pos, Block frameBlock, int meta, int flag)
	{
		int index = getIndexFromShiftedMetadata(getShiftedCPMetadataByFrameBlock(frameBlock.getStateFromMeta(meta)));
		Block block = (Block)cpBlocks.get(index);
		world.setBlockState(pos, block.getStateFromMeta(meta), flag);
	}
	
	public static int getMeta(IBlockAccess iba, BlockPos pos) {
		return getMeta(iba.getBlockState(pos));
	}
	
	public static int getMeta(IBlockState state) {
		return state.getBlock().getMetaFromState(state);
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

	public static boolean isDimensionValidAtAll(int dimension)
	{
		if (useDimensionWhiteList)
		{
			if (dimensionWhiteList.length == 0) {
				return false;
			}
			boolean inWhiteList = false;
			for (int i = 0; i < dimensionWhiteList.length; i++) {
				if (dimension == dimensionWhiteList[i]) {
					inWhiteList = true;
				}
			}
			if (!inWhiteList) {
				return false;
			}
		}
		if (useDimensionBlackList) {
			for (int i = 0; i < dimensionBlackList.length; i++) {
				if (dimension == dimensionBlackList[i]) {
					return false;
				}
			}
		}
		return true;
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
		for (int i = 0; i < colourfulPortals.size(); i++) {
			if (((ColourfulPortalLocation)colourfulPortals.get(i)).portalMetadata == getShiftedCPMetadata(state)) {
				portalsWithType++;
			}
		}
		if (portalsWithType >= maxPortalsPerType) {
			return true;
		}
		return false;
	}

	public void loadPortalsList()
	{
		FileInputStream fInput = null;
		ObjectInputStream oInput = null;
		try
		{
			this.saveLocation = proxy.getSaveLocation();
			if (this.saveLocation.exists())
			{
				fInput = new FileInputStream(this.saveLocation);
				oInput = new ObjectInputStream(fInput);
				colourfulPortals = (LinkedList)oInput.readObject();
				oInput.close();
				fInput.close();

				checkForPortalChanges();
			}
			else
			{
				this.saveLocation.createNewFile();





				colourfulPortals = new LinkedList();
			}
		}
		catch (Exception e)
		{
			if (!(e instanceof EOFException)) {
				e.printStackTrace();
			}
			try
			{
				if (oInput != null) {
					oInput.close();
				}
				if (fInput != null) {
					fInput.close();
				}
			}
			catch (IOException ioe) {}
		}
	}

	private void checkForPortalChanges()
	{
		ArrayList<ColourfulPortalLocation> toDelete = new ArrayList();
		for (ColourfulPortalLocation portal : colourfulPortals)
		{
			WorldServer currentWS = FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(portal.dimension);
			BlockPos currentPos = new BlockPos(portal.xPos, portal.yPos, portal.zPos);
			if ((getCPBlockByShiftedMetadata(portal.portalMetadata) != currentWS.getBlockState(currentPos).getBlock()) && (getStandaloneCPBlockByShiftedMetadata(portal.portalMetadata) != currentWS.getBlockState(currentPos).getBlock())) {
				if (!BlockColourfulPortal.tryToCreatePortal(currentWS, currentPos, false)) {
					toDelete.add(portal);
				}
			}
		}
		for (ColourfulPortalLocation deleted : toDelete) {
			colourfulPortals.remove(deleted);
		}
		savePortals();
	}

	public static ColourfulPortalLocation getColourfulDestination(World world, BlockPos pos)
	{
		if (colourfulPortals.size() > 0)
		{
			ColourfulPortalLocation start = findCPLocation(world, pos);


			int originalPos = colourfulPortals.indexOf(start);
			if (originalPos == -1) {
				return new ColourfulPortalLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
			}
			int size = colourfulPortals.size();
			for (int i = 0; i < size; i++)
			{
				int index = i + originalPos + 1;
				if (index >= size) {
					index -= size;
				}
				ColourfulPortalLocation current = (ColourfulPortalLocation)colourfulPortals.get(index);
				if (current.portalMetadata == start.portalMetadata) {
					if(FMLCommonHandler.instance().getMinecraftServerInstance().worldServerForDimension(current.dimension) != null) {
						return current;
					}
				}
			}
			return start;
		}
		return new ColourfulPortalLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
	}

	public static ColourfulPortalLocation findCPLocation(World world, BlockPos pos)
	{
		if (!isCPBlock(world.getBlockState(pos).getBlock())) {
			return null;
		}
		if (isStandaloneCPBlock(world.getBlockState(pos).getBlock())) {
			return new ColourfulPortalLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
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
		Stack<ColourfulPortalLocation> toVisit = new Stack();

		toVisit.push(new ColourfulPortalLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos)));

		visited.add(toVisit.peek());
		while (!toVisit.empty())
		{
			ColourfulPortalLocation current = (ColourfulPortalLocation)toVisit.pop();
			if (colourfulPortals.contains(current)) {
				return current;
			}
			BlockPos currentPos = new BlockPos(current.xPos, current.yPos, current.zPos);
			if ((zDir) || (xDir))
			{
				if (isCPBlock(world.getBlockState(currentPos.add(0, 1, 0)).getBlock()))
				{
					ColourfulPortalLocation temp = new ColourfulPortalLocation(currentPos.add(0, 1, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 1, 0)));
					if (!visited.contains(temp))
					{
						toVisit.push(temp);
						visited.add(temp);
					}
				}
				if (isCPBlock(world.getBlockState(currentPos.add(0, -1, 0)).getBlock()))
				{
					ColourfulPortalLocation temp = new ColourfulPortalLocation(currentPos.add(0, -1, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, -1, 0)));
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
					ColourfulPortalLocation temp = new ColourfulPortalLocation(currentPos.add(1, 0, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(1, 0, 0)));
					if (!visited.contains(temp))
					{
						toVisit.push(temp);
						visited.add(temp);
					}
				}
				if (isCPBlock(world.getBlockState(currentPos.add(-1, 0, 0)).getBlock()))
				{
					ColourfulPortalLocation temp = new ColourfulPortalLocation(currentPos.add(-1, 0, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(-1, 0, 0)));
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
					ColourfulPortalLocation temp = new ColourfulPortalLocation(currentPos.add(0, 0, 1), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 0, 1)));
					if (!visited.contains(temp))
					{
						toVisit.push(temp);
						visited.add(temp);
					}
				}
				if (isCPBlock(world.getBlockState(currentPos.add(0, 0, -1)).getBlock()))
				{
					ColourfulPortalLocation temp = new ColourfulPortalLocation(currentPos.add(0, 0, -1), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 0, -1)));
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

	public static void deletePortal(ColourfulPortalLocation locToDelete)
	{
		if (colourfulPortals.remove(locToDelete)) {
			savePortals();
		}
	}

	public static boolean addPortalToList(ColourfulPortalLocation newLocation)
	{
		if (!colourfulPortals.contains(newLocation))
		{
			colourfulPortals.add(newLocation);





			savePortals();
			return true;
		}
		return false;
	}

	private static void savePortals()
	{
		try
		{
			FileOutputStream fOut = new FileOutputStream(colourfulPortalsMod.saveLocation);
			ObjectOutputStream oOut = new ObjectOutputStream(fOut);
			oOut.writeObject(colourfulPortals);
			oOut.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}

	public static class CPLSet
	extends TreeSet<ColourfulPortalsMod.ColourfulPortalLocation>
	{
		public CPLSet()
		{
			super(CPLcomparator);
		}
	}

	public static Comparator<ColourfulPortalLocation> CPLcomparator = new Comparator<ColourfulPortalLocation>()
			{

		@Override
		public int compare(ColourfulPortalsMod.ColourfulPortalLocation first, ColourfulPortalsMod.ColourfulPortalLocation second)
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

			public static class ColourfulPortalLocation
			implements Serializable
			{
				int xPos;
				int yPos;
				int zPos;
				int dimension;
				int portalMetadata;

				public ColourfulPortalLocation(BlockPos pos, int dim, int meta)
				{
					this.xPos = pos.getX();
					this.yPos = pos.getY();
					this.zPos = pos.getZ();
					this.dimension = dim;
					this.portalMetadata = meta;
				}

				public boolean equals(Object o)
				{
					if ((o == null) || (!(o instanceof ColourfulPortalLocation))) {
						return false;
					}
					ColourfulPortalLocation other = (ColourfulPortalLocation)o;
					return (this.xPos == other.xPos) && (this.yPos == other.yPos) && (this.zPos == other.zPos) && (this.dimension == other.dimension) && (this.portalMetadata == other.portalMetadata);
				}

				public String toString()
				{
					return "CPL[meta=" + this.portalMetadata + ", x=" + this.xPos + ", y=" + this.yPos + ", z=" + this.zPos + ", dim=" + this.dimension + "]";
				}
			}
}