package solipsists.bigagriculture.tileentity;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import solipsists.bigagriculture.BigAgriculture;

public class TileController extends TileEntity implements ITickable {

	private int radius = 32;

	@Override
	public void update() {
		
		// Till & water the earth
		for (int i = -radius; i <= radius; i++) {
			for (int j = -radius; j <= radius; j++) {
				if (i != 0 || j != 0) {
					BlockPos blockPos = this.getPos().add(i,-1,j);	
					IBlockState state = worldObj.getBlockState(blockPos);
					Block block = state.getBlock();
					
					boolean isFarmland = block instanceof BlockFarmland;
					
					if (!isFarmland || state.getValue(BlockFarmland.MOISTURE) < 7) {
						IBlockState stateToModify = isFarmland ? state : Blocks.FARMLAND.getDefaultState();
						worldObj.setBlockState(blockPos, stateToModify.withProperty(BlockFarmland.MOISTURE, 7), 2);
					}
				}
				
			}
		}
		
	}

	
}
