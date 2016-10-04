package solipsists.bigagriculture;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.block.BlockController;

public class ModBlocks {
	
	public static BlockController controller;
	
	public static void init() {
		controller = new BlockController();		
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		controller.initModel();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initItemModels() {
		
	}
}
