package solipsists.bigagriculture.multiblock;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.block.BlockExpander;
import solipsists.bigagriculture.tileentity.TileExpander;

public class MultiblockStructure {
	
	private HashMap<String, MultiblockEntry> structure = new HashMap();	

	// The bounds of the multiblock, with 1block padding on each facing except .down()
	private BlockPos boundsMin;
	private BlockPos boundsMax;

	// The actual bounds, ie no padding.
	private BlockPos actualBoundsMin;
	private BlockPos actualBoundsMax;
	
	/**
	 * Render around all blocks contained in structure 
	 */
	public void highlight() {		
		Set<BlockPos> set = new HashSet<BlockPos>();
		
		for (Iterator<BlockPos> it = BlockPos.getAllInBox(actualBoundsMin, actualBoundsMax).iterator(); it.hasNext();) {
			set.add(it.next());
		}
		
		BigAgriculture.instance.clientInfo.highlightBlocks(set, System.currentTimeMillis() + 10000);
	}
	 
	/**
	 * Return true if the bounds contain the given BlockPos
	 * @param pos
	 * @return
	 */
	public boolean boundsContain(BlockPos pos) {		
		for(Iterator<BlockPos> it = BlockPos.getAllInBox(boundsMin, boundsMax).iterator(); it.hasNext();) {
			BlockPos current = it.next();
			if (pos.equals(current)) {
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * Return true if the ACTUAL bounds contain the given BlockPos.
	 * @param pos
	 * @return
	 */
	public boolean actualBoundsContain(BlockPos pos) {
		for(Iterator<BlockPos> it = BlockPos.getAllInBox(actualBoundsMin, actualBoundsMax).iterator(); it.hasNext();) {
			BlockPos current = it.next();
			if (pos.equals(current)) {
				return true;
			}
		}		
		return false;
	}
	
	/**
	 * The number of blocks in the structure
	 * @return
	 */
	private int size() {
		return structure.size();
	}
		
	/**
	 * Extend the bounds of the structure to include the given BlockPos
	 * @param pos
	 */
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
	
	/**
	 * Add a BlockPos of a given Type to the structure.
	 * @param pos The BlockPos to add to the structure.
	 * @param type The Type of the multiblock.
	 */
	public void add(BlockPos pos, Multiblock.TYPE type) {		
		adjustBounds(pos);			
		
		MultiblockEntry m = new MultiblockEntry(pos);
		m.type = type;
		MultiblockEntry old = structure.put(key(pos), m);
	}	
	
	/**
	 * Remove a given BlockPos from the structure.
	 * @param pos
	 */
	public void remove(BlockPos pos) {
		structure.remove(key(pos));
	}
	
	/**
	 * Reset the structure.
	 */
	public void clear() {
		structure.clear();
	}
	
	/**
	 * Return the checked flag for the given BlockPos
	 * @param pos
	 * @return
	 */
	public boolean getChecked(BlockPos pos) {
		return structure.get(key(pos)).checked;
	}
	
	/**
	 * Set the checked flag for the given BlockPos
	 * @param pos
	 * @param checked
	 */
	public void setChecked(BlockPos pos, boolean checked) {
		structure.get(key(pos)).checked = checked;
	}
	
	/**
	 * Get the valid flag for the given BlockPos
	 * @param pos
	 * @return
	 */
	public boolean getValid(BlockPos pos) {
		return structure.get(key(pos)).valid;
	}
	
	/**
	 * Set the valid flag for the given BlockPos
	 * @param pos
	 * @param valid
	 */
	public void setValid(BlockPos pos, boolean valid) {
		structure.get(key(pos)).valid = valid;
	}
	
	/**
	 * Return true if the structure contains the given BlockPos.
	 * @param pos
	 * @return
	 */
	public boolean contains(BlockPos pos) {				
		if (structure.containsKey(key(pos)))
			return true;
		else
			return false;
	}
	
	
	/**
	 * Return true if the structure is a valid multiblock.
	 * @return
	 */
	public boolean isValid() {
		for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
			if (pair.getValue().checked == false)
				return false;
		}
		
		return true;
	}
	
	/**
	 * Generate the key for the structure map.
	 * @param pos
	 * @return
	 */
	private String key(BlockPos pos) {
		return "mb:" + pos.getX() + pos.getY() + pos.getZ();
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
	
	/**
	 * Get the number of blocks of a specific type in the structure.
	 * @param type
	 * @return
	 */
	public int getBlocksOfType(Multiblock.TYPE type) {
		int b = 0;
		for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
			MultiblockEntry entry = pair.getValue();
			
			if (entry.type == type) {
				b += 1;
			}
		}
		return b;
	}

}
