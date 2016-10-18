package solipsists.bigagriculture.multiblock;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.block.BlockExpander;
import solipsists.bigagriculture.tileentity.TileExpander;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class MultiblockStructure {

    private HashMap<String, MultiblockEntry> structure = new HashMap<String, MultiblockEntry>();

    // The bounds of the multiblock
    private BlockPos boundsMin;
	private BlockPos boundsMax;

    /**
	 * Render around all blocks contained in structure 
	 */
    public void highlight() {
        Set<BlockPos> set = new HashSet<BlockPos>();

        for (Iterator<BlockPos> it = BlockPos.getAllInBox(boundsMin, boundsMax).iterator(); it.hasNext(); ) {
            set.add(it.next());
		}
		
		BigAgriculture.instance.clientInfo.highlightBlocks(set, System.currentTimeMillis() + 10000);
    }

    public BlockPos findYourCenter() {
        if (boundsMin == null || boundsMax == null)
            return null;

        double x = (boundsMin.getX() + boundsMax.getX()) / 2;
        double z = (boundsMin.getZ() + boundsMax.getZ()) / 2;
        double y = boundsMin.getY();

        BlockPos center = new BlockPos(x, y, z);
        return center;
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
	 * The number of blocks in the structure
     * @return size of the structure
     */
	private int size() {
		return structure.size();
	}
		
	/**
	 * Extend the bounds of the structure to include the given BlockPos
     * @param pos The position to add to the bounds
     */
	private void adjustBounds(BlockPos pos) {		
		
		if (boundsMin == null) {
			boundsMin = pos;
            boundsMin = pos;
        }
		
		if (boundsMax == null) {
			boundsMax = pos;
            boundsMax = pos;
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
		}
	}
	
	/**
	 * Add a BlockPos of a given Type to the structure.
	 * @param pos The BlockPos to add to the structure.
	 * @param type The Type of the multiblock.
	 */
    public void add(BlockPos pos, Multiblock.TYPE type) {
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
        boundsMin = null;
        boundsMax = null;
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
        // Unchecked block in structure
        for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
            if (!pair.getValue().checked)
                return false;
		}
		
		return true;
	}

    /**
     * Remove invalid blocks from the structure.
     */
    public void pruneInvalid() {
        HashSet<String> keysToRemove = new HashSet<String>();

        for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
            if (!pair.getValue().valid)
                keysToRemove.add(pair.getKey());
        }

        for (String key : keysToRemove) {
            structure.remove(key);
        }

        for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
            adjustBounds(pair.getValue().pos);
        }
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
     * @param world The world
     * @return The radius of the multiblock
     */
    public int getMultiblockRadius(World world) {
        // Work out starting radius based on multiblock base size
        double xDiff = Math.ceil(((double) boundsMax.getX() - (double) boundsMin.getX()) / 2);
        double zDiff = Math.ceil(((double) boundsMax.getZ() - (double) boundsMin.getZ()) / 2);

        int rad = 1 + (int) Math.max(xDiff, zDiff);

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
     * @param type The type of block to look for
     * @return The number of blocks found
     */
	public int getBlocksOfType(Multiblock.TYPE type) {
        int blocksOfType = 0;
        for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
			MultiblockEntry entry = pair.getValue();
			
			if (entry.type == type) {
                blocksOfType += 1;
            }
		}
        return blocksOfType;
    }

}
