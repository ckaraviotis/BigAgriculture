package solipsists.bigagriculture.multiblock;

import net.minecraft.util.math.BlockPos;

public class MultiblockEntry {
	public BlockPos pos;
	public Boolean checked;
	public Boolean valid;
	public enum type {
		CONTROLLER,
		EXPANDER,
		FERTILIZER
	}
	
	public MultiblockEntry (BlockPos pos) {
		this.pos = pos;
	}
}
