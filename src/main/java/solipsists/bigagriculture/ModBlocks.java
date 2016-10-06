package solipsists.bigagriculture;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.block.BlockController;
import solipsists.bigagriculture.block.BlockExpander;
import solipsists.bigagriculture.block.BlockGenerator;

public class ModBlocks {
	
	public static BlockController controller;
	public static BlockGenerator generator;
	public static BlockExpander expander;
	
	public static void init() {
		controller = new BlockController();	
		generator = new BlockGenerator();
		expander = new BlockExpander();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		controller.initModel();
		generator.initModel();
		expander.initModel();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initItemModels() {
		
	}
}
