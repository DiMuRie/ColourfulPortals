package com.tmtravlr.cp;

import java.util.*;

import com.tmtravlr.cp.block.BlockColourfulPortal;
import com.tmtravlr.cp.block.BlockColourfulWater;
import com.tmtravlr.cp.block.BlockStandaloneCP;
import com.tmtravlr.cp.fluids.ColourfulFluid;
import com.tmtravlr.cp.worldsavedata.ColourfulData;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;

public class CPLib {

	public static final String MODID = "colourfulportals";
	public static final String VERSION = "2.0-alpha";
	public static final String NAME = "ColourfulPortalsMod";

	public static final String CLIENT_PROXY_CLASS = "com.tmtravlr.cp.proxy.ClientProxy";
	public static final String SERVER_PROXY_CLASS = "com.tmtravlr.cp.proxy.ServerProxy";

	//
	public static final ColourfulFluid colourfulFluid = new ColourfulFluid();
	//public static HashMap<Integer, BlockColourfulPortal> cpBlocks = new HashMap();
	//public static HashMap<Integer, BlockStandaloneCP> scpBlocks = new HashMap();
	public static List<BlockColourfulWater> cpBlocks = new ArrayList<BlockColourfulWater>();
	public static List<BlockStandaloneCP> scpBlocks = new ArrayList<BlockStandaloneCP>();
	public static HashMap<Integer, Block> frameBlocks = new HashMap<Integer, Block>();
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
	//public static LinkedList<CPLocations> colourfulPortals = new LinkedList();
	//private File saveLocation;
	//private boolean loaded;
	public String currentFolder;

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
		if (tooManyPortals(world, state)) {
			return false;
		}
		if (!isDimensionValidAtAll(world.provider.getDimension())) {
			return false;
		}
		return true;
	}

	public static boolean tooManyPortals(World world, IBlockState state)
	{
		if (maxPortalsPerType < 0) {
			return false;
		}
		if (maxPortalsPerType == 0) {
			return true;
		}
		int portalsWithType = 0;

		final ColourfulData colourfulData = ColourfulData.get(world);
		if (colourfulData != null) {
			final List<CPLocation> locations = colourfulData.getLocationList();
			for (CPLocation location : locations) {
				//if (((CPLocations)colourfulPortals.get(i)).portalMetadata == getShiftedCPMetadata(state)) {
				if (location.portalMetadata == getShiftedCPMetadata(state)) {
					portalsWithType++;
				}
			}
		}

		return portalsWithType >= maxPortalsPerType;
	}

	/*FileInputStream fin;
	ObjectInputStream oInput;
	public void loadPortalsList()
	
	{
		//FileInputStream fInput = null;
		//ObjectInputStream oInput = null;
		try
		{
			this.saveLocation = proxy.getSaveLocation();
			if (this.saveLocation.exists())
			{
				fin = new FileInputStream(this.saveLocation);//fInput = new FileInputStream(this.saveLocation);
				oInput = new ObjectInputStream(fin);//oInput = new ObjectInputStream(fInput);
				colourfulPortals = (LinkedList)oInput.readObject();
				oInput.close();
				fin.close();//fInput.close();

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
				//if (fInput != null) {
				//	fInput.close();
				//}
				if (fin != null) {
					fin.close();
				}
			}
			catch (IOException ioe) {}
		}
	}*/

	public static void checkForPortalChanges(World world)
	{
		ArrayList<CPLocation> toDelete = new ArrayList<CPLocation>();
		final ColourfulData colourfulData = ColourfulData.get(world);

		if (colourfulData != null) {
			for (CPLocation portal : colourfulData.getLocationList())
			{
				WorldServer currentWS = world.getMinecraftServer().worldServerForDimension(portal.dimension);
				BlockPos currentPos = portal.pos;
				if ((getCPBlockByShiftedMetadata(portal.portalMetadata) != currentWS.getBlockState(currentPos).getBlock()) && (getStandaloneCPBlockByShiftedMetadata(portal.portalMetadata) != currentWS.getBlockState(currentPos).getBlock())) {
					if (!BlockColourfulPortal.tryToCreatePortal(currentWS, currentPos, false)) {
						toDelete.add(portal);
					}
				}
			}

			for (CPLocation deleted : toDelete) {
				colourfulData.removeLocation(deleted);
			}

			colourfulData.save(world);
		}
	}

	public static CPLocation getColourfulDestination(World world, BlockPos pos)
	{
		final ColourfulData colourfulData = ColourfulData.get(world);
		if (colourfulData == null || colourfulData.getLocationList().size() <= 0) {
			return new CPLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
		}

		final List<CPLocation> locationList = colourfulData.getLocationList();
		CPLocation start = findCPLocation(world, pos);

		int originalPos = locationList.indexOf(start);
		if (originalPos == -1) {
			return new CPLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
		}
		int size = locationList.size();
		for (int i = 0; i < size; i++) {
			int index = i + originalPos + 1;
			if (index >= size) {
				index -= size;
			}
			CPLocation current = locationList.get(index);
			if (current.portalMetadata == start.portalMetadata) {
				if (world.getMinecraftServer().worldServerForDimension(current.dimension) != null) {
					return current;
				}
			}
		}

		return start;
	}

	public static CPLocation findCPLocation(World world, BlockPos pos)
	{
		if (!isCPBlock(world.getBlockState(pos).getBlock())) {
			return null;
		}
		if (isStandaloneCPBlock(world.getBlockState(pos).getBlock())) {
			return new CPLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos));
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
		Stack<CPLocation> toVisit = new Stack<CPLocation>();

		toVisit.push(new CPLocation(pos, world.provider.getDimension(), getShiftedCPMetadata(world, pos)));

		visited.add(toVisit.peek());
		final ColourfulData colourfulData = ColourfulData.get(world);
		if (colourfulData == null) {
			return null;
		}

		final List<CPLocation> locationList = colourfulData.getLocationList();

		while (!toVisit.empty()) {
			CPLocation current = toVisit.pop();
			if (locationList.contains(current)) {
				return current;
			}
			BlockPos currentPos = current.pos;
			if ((zDir) || (xDir)) {
				if (isCPBlock(world.getBlockState(currentPos.add(0, 1, 0)).getBlock())) {
					CPLocation temp = new CPLocation(currentPos.add(0, 1, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 1, 0)));
					if (!visited.contains(temp)) {
						toVisit.push(temp);
						visited.add(temp);
					}
				}
				if (isCPBlock(world.getBlockState(currentPos.add(0, -1, 0)).getBlock())) {
					CPLocation temp = new CPLocation(currentPos.add(0, -1, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, -1, 0)));
					if (!visited.contains(temp)) {
						toVisit.push(temp);
						visited.add(temp);
					}
				}
			}
			if ((zDir) || (yDir)) {
				if (isCPBlock(world.getBlockState(currentPos.add(1, 0, 0)).getBlock())) {
					CPLocation temp = new CPLocation(currentPos.add(1, 0, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(1, 0, 0)));
					if (!visited.contains(temp)) {
						toVisit.push(temp);
						visited.add(temp);
					}
				}
				if (isCPBlock(world.getBlockState(currentPos.add(-1, 0, 0)).getBlock())) {
					CPLocation temp = new CPLocation(currentPos.add(-1, 0, 0), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(-1, 0, 0)));
					if (!visited.contains(temp)) {
						toVisit.push(temp);
						visited.add(temp);
					}
				}
			}
			if ((yDir) || (xDir)) {
				if (isCPBlock(world.getBlockState(currentPos.add(0, 0, 1)).getBlock())) {
					CPLocation temp = new CPLocation(currentPos.add(0, 0, 1), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 0, 1)));
					if (!visited.contains(temp)) {
						toVisit.push(temp);
						visited.add(temp);
					}
				}
				if (isCPBlock(world.getBlockState(currentPos.add(0, 0, -1)).getBlock())) {
					CPLocation temp = new CPLocation(currentPos.add(0, 0, -1), world.provider.getDimension(), getShiftedCPMetadata(world, currentPos.add(0, 0, -1)));
					if (!visited.contains(temp)) {
						toVisit.push(temp);
						visited.add(temp);
					}
				}
			}
		}

		return null;
	}

	public static void deletePortal(World world, CPLocation locToDelete)
	{
		final ColourfulData colourfulData = ColourfulData.get(world);
		if (colourfulData != null) {
			colourfulData.removeLocation(locToDelete);
			colourfulData.save(world);
		}
	}

	public static boolean addPortalToList(World world, CPLocation newLocation)
	{
		final ColourfulData colourfulData = ColourfulData.get(world);
		if (colourfulData != null && !colourfulData.getLocationList().contains(newLocation)) {
			colourfulData.addLocation(newLocation);
			colourfulData.save(world);

			return true;
		}
		return false;
	}

	/*private static void savePortals()
	{
		try
		{
			FileOutputStream fOut = new FileOutputStream(CPLib.saveLocation);
			ObjectOutputStream oOut = new ObjectOutputStream(fOut);
			oOut.writeObject(colourfulPortals);
			oOut.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}*/

	public static class CPLSet extends TreeSet<CPLocation>
	{
		public CPLSet()
		{
			super(CPLcomparator);
		}
	}

	public static Comparator<CPLocation> CPLcomparator = new Comparator<CPLocation>()
			{

		@Override
		public int compare(CPLocation first, CPLocation second)
		{
			if (first.portalMetadata != second.portalMetadata) {
				return second.portalMetadata - first.portalMetadata;
			}
			if (first.dimension != second.dimension) {
				return second.dimension - first.dimension;
			}
			return first.pos.compareTo(second.pos);
		}
			};
}
