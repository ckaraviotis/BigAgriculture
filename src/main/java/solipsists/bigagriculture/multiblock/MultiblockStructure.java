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
	 
	private boolean boundsContain(BlockPos pos) {
		
		for(Iterator<BlockPos> it = BlockPos.getAllInBox(boundsMin, boundsMax).iterator(); it.hasNext();) {
			BlockPos current = it.next();
			if (pos.equals(current)) {
				return true;
			}
		}		
		return false;

	}
		
	private void adjustBounds(BlockPos pos) {		
		
		if (boundsMin == null) {
			boundsMin = pos;
		}
		
		if (boundsMax == null) {
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
	
	public void add(BlockPos pos) {		
		adjustBounds(pos);			
		
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
	
	private void fakeRender(World world) {	
		// TODO add new block with material air to work as boundary marker
		IBlockState marker = Blocks.LAPIS_BLOCK.getDefaultState();
		world.setBlockState(boundsMin, marker);
		world.setBlockState(boundsMax, marker);
	}
	
	public void render(World world) {
		fakeRender(world);
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
