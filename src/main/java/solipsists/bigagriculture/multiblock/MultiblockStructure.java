package solipsists.bigagriculture.multiblock;

import java.awt.Color;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.Level;
import org.lwjgl.opengl.GL11;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.block.BlockExpander;
import solipsists.bigagriculture.tileentity.TileExpander;

public class MultiblockStructure {
	
	private HashMap<String, MultiblockEntry> structure = new HashMap();	

	private BlockPos boundsMin;
	private BlockPos boundsMax;

	// The physical machine blocks (ie no Air blocks)
	private BlockPos actualBoundsMin;
	private BlockPos actualBoundsMax;
	 
	public boolean boundsContain(BlockPos pos) {		
		for(Iterator<BlockPos> it = BlockPos.getAllInBox(boundsMin, boundsMax).iterator(); it.hasNext();) {
			BlockPos current = it.next();
			if (pos.equals(current)) {
				return true;
			}
		}		
		return false;
	}
	
	public boolean actualBoundsContain(BlockPos pos) {
		for(Iterator<BlockPos> it = BlockPos.getAllInBox(actualBoundsMin, actualBoundsMax).iterator(); it.hasNext();) {
			BlockPos current = it.next();
			if (pos.equals(current)) {
				return true;
			}
		}		
		return false;
	}
	
	private int size() {
		return structure.size();
	}
		
	private void adjustBounds(BlockPos pos) {		
		
		if (boundsMin == null) {
			boundsMin = pos;
			actualBoundsMin = pos;
		}
		
		if (boundsMax == null) {
			boundsMax = pos;
			actualBoundsMax = pos;
		}
		
		// Not in bounds! Re-create catching new coords
		if (!boundsContain(pos)) {
			
			double minX, minY, minZ, maxX, maxY, maxZ;
			minX = boundsMin.getX();
			minY = boundsMin.getY();
			minZ = boundsMin.getZ();
			
			maxX = boundsMax.getX();
			maxY = boundsMax.getY();
			maxZ = boundsMax.getZ();
			
			
			if (pos.getX() < minX) {
				minX = pos.getX();
			}
			if (pos.getY() < minY) {
				minY = pos.getY();
			}
			if (pos.getZ() < minZ) {
				minZ = pos.getZ();
			}
			
			if (pos.getX() > maxX) {
				maxX = pos.getX();
			}
			if (pos.getY() > maxY) {
				maxY = pos.getY();
			}
			if (pos.getZ() > maxZ) {
				maxZ = pos.getZ();
			}
			
			boundsMin = new BlockPos(minX, minY, minZ);
			boundsMax = new BlockPos(maxX, maxY, maxZ);
			
			actualBoundsMin = boundsMin.add(1,0,1);
			actualBoundsMax = boundsMax.add(-1,-1,-1);
		}
	}
	
	public void add(BlockPos pos, Multiblock.TYPE type) {		
		adjustBounds(pos);			
		
		MultiblockEntry m = new MultiblockEntry(pos);
		m.type = type;
		MultiblockEntry old = structure.put(key(pos), m);
	}	
	
	public void remove(BlockPos pos) {
		structure.remove(key(pos));
	}
	
	public void clear() {
		structure.clear();
	}
	
	public boolean getChecked(BlockPos pos) {
		return structure.get(key(pos)).checked;
	}
	
	public void setChecked(BlockPos pos, boolean checked) {
		structure.get(key(pos)).checked = checked;
	}
	
	public boolean getValid(BlockPos pos) {
		return structure.get(key(pos)).valid;
	}
	
	public void setValid(BlockPos pos, boolean valid) {
		structure.get(key(pos)).valid = valid;
	}
	
	public boolean contains(BlockPos pos) {				
		if (structure.containsKey(key(pos)))
			return true;
		else
			return false;
	}
	
	// TODO better way of iterating over the structure?
	public boolean isValid() {
		for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
			if (pair.getValue().checked == false)
				return false;
		}
		
		return true;
	}
	
	private String key(BlockPos pos) {
		return "mb:" + pos.getX() + pos.getY() + pos.getZ();
	}
	
	private void fakeRender(World world) {	
		// TODO add new block with material air to work as boundary marker
		IBlockState marker = Blocks.LAPIS_BLOCK.getDefaultState();
		world.setBlockState(boundsMin, marker);
		
		marker = Blocks.REDSTONE_BLOCK.getDefaultState();
		world.setBlockState(boundsMax, marker);
		
		/*
		// These will break the Multiblock
		marker = Blocks.EMERALD_BLOCK.getDefaultState();
		world.setBlockState(actualBoundsMin, marker);
		
		marker = Blocks.DIAMOND_BLOCK.getDefaultState();
		world.setBlockState(actualBoundsMax, marker);
		*/
	}
	
	public void render(World world) {
		fakeRender(world);
	}

	/**
	 * Get the operating radius of the multiblock, after counting Expanders.
	 * @param world
	 * @return
	 */
	public int getMultiblockRadius(World world){ //, int radius) {
		int rad = 1;
		
		for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
			Block b = world.getBlockState(pair.getValue().pos).getBlock();
			if (b instanceof BlockExpander) {
				TileExpander t = (TileExpander)world.getTileEntity(pair.getValue().pos);
				rad += t.RADIUS;
			}
		}
		return rad;
	}
	
	public int getBlocksOfType(Multiblock.TYPE type) {
		int b = 0;
		for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
			MultiblockEntry entry = pair.getValue();
			
			if (entry.type == type) {
				b += 1;
			}
		}
		/*
		for (HashMap.Entry<BlockPos, Boolean> pair : tMultiBlock.entrySet()) {
			Block b = this.worldObj.getBlockState(pair.getKey()).getBlock();

			if (b instanceof BlockFertilizer) {
				TileFertilizer t = (TileFertilizer)worldObj.getTileEntity(pair.getKey());
				chance += t.CHANCE;
			}
		}

		if (chance > 100)
			chance = 100;
		*/
		return b;
	}

}
