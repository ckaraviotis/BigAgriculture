package solipsists.bigagriculture.multiblock;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.block.BlockExpander;
import solipsists.bigagriculture.block.BlockMultiblock;
import solipsists.bigagriculture.tileentity.TileExpander;
import solipsists.bigagriculture.tileentity.TileMultiblock;

/***
 * Container for multiblock structure data
 * @author Chris
 *
 */
public class Multiblock {

	private MultiblockStructure structure = new MultiblockStructure();

	public boolean isValid() {
		return structure.isValid();
	}
	
	public void cleanRemovedBlocks() {
		//structure.cleanRemovedBlocks();
	}
	
	/**
	 * Recursively build a multiblock structure, starting from a BlockPos. 
	 * @param current The starting BlockPos.
	 */
	public void buildMultiblock(World world, BlockPos current, boolean clear) {
		if (clear)
			structure.clear();
		
		List<BlockPos> neighbours = new ArrayList<BlockPos>(); 

		// TODO This implementation sucks.
		neighbours.add( current.up() );
		neighbours.add( current.north() );
		neighbours.add( current.south() );
		neighbours.add( current.east() );
		neighbours.add( current.west() );

		for(BlockPos neighbour : neighbours) {			
			Block b = world.getBlockState(neighbour).getBlock();			

			if (!structure.contains(neighbour)) {
				structure.add(neighbour);
				BigAgriculture.logger.log(Level.INFO, "Added block "+ b.getUnlocalizedName() + " at location: " + neighbour.getX() + ", " + neighbour.getY() + ", " + neighbour.getZ());
				
				boolean isValidBlock = b instanceof BlockMultiblock;
				boolean isAir = world.isAirBlock(neighbour);
				boolean isCrop = b instanceof BlockCrops;

				if(!isValidBlock && !isAir && !isCrop) {
					structure.setValid(neighbour, false);
					structure.setChecked(neighbour, true);
					BigAgriculture.logger.log(Level.INFO, "Block "+ b.getUnlocalizedName() + " marked as invalid.");
				}

				if (isValidBlock) {
					structure.setValid(neighbour, true);
					BigAgriculture.logger.log(Level.INFO, "Block "+ b.getUnlocalizedName() + " marked as valid.");
					
					TileMultiblock t = (TileMultiblock)world.getTileEntity(neighbour);
					t.CHECKED = true;
					structure.setChecked(neighbour, true);

					buildMultiblock(world, neighbour, false);
				}
				
				structure.setChecked(neighbour, true);
			}
		}		
	}
	
	public int getMultiblockRadius(World world, int radius) {
		return structure.getMultiblockRadius(world, radius);
	}
	
}
