package solipsists.bigagriculture;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.blocks.Controller;

public class ModBlocks {
	
	public static Controller controller;
	
	public static void init() {
		controller = new Controller();		
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		controller.initModel();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initItemModels() {
		
	}
}
