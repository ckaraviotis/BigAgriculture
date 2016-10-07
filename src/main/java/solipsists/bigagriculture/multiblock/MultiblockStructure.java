package solipsists.bigagriculture.multiblock;

import java.util.HashMap;

import net.minecraft.block.Block;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import solipsists.bigagriculture.block.BlockExpander;
import solipsists.bigagriculture.tileentity.TileExpander;

public class MultiblockStructure {
	
	private HashMap<String, MultiblockEntry> structure = new HashMap();	
	
	public void add(BlockPos pos) {
		MultiblockEntry m = new MultiblockEntry(pos);
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

	public void removeInvalidBlocks(World world) {
		/*
		 * or (Iterator<BlockPos> i = multiblock.iterator(); i.hasNext();) {
			try {
				BlockPos bp = i.next();
				Block b = this.worldObj.getBlockState(bp).getBlock();
				if (!(b instanceof BlockMultiblock)) {
					i.remove();
				}
			} catch (Exception e) {
				BigAgriculture.logger.log(Level.ERROR, "Something went wrong removing a multiblock block!", e);
			}
		}
		 */
	}

	public int getMultiblockRadius(World world, int radius) {
		int rad = radius;
		
		for (HashMap.Entry<String, MultiblockEntry> pair : structure.entrySet()) {
			Block b = world.getBlockState(pair.getValue().pos).getBlock();
			if (b instanceof BlockExpander) {
				TileExpander t = (TileExpander)world.getTileEntity(pair.getValue().pos);
				rad += t.RADIUS;
			}
		}
		return rad;
	}
}
