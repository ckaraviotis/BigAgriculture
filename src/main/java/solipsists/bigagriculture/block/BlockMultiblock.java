package solipsists.bigagriculture.block;

import net.minecraft.block.material.Material;
import solipsists.bigagriculture.multiblock.Multiblock;
import solipsists.bigagriculture.multiblock.Multiblock.TYPE;

/***
 * Extend me to be a valid multiblock
 */
public class BlockMultiblock extends BlockGenericBA {
	
	public static final Multiblock.TYPE type = TYPE.DEFAULT;

	public BlockMultiblock(Material material) {
		super(material);
	}
	
	public Multiblock.TYPE getType() {
		return type;
	}

}
