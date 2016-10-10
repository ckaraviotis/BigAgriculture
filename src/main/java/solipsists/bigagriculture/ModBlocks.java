package solipsists.bigagriculture;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.block.BlockController;
import solipsists.bigagriculture.block.BlockExpander;
import solipsists.bigagriculture.block.BlockFertilizer;
import solipsists.bigagriculture.block.BlockGenerator;
import solipsists.bigagriculture.block.BlockInfinityStone;
import solipsists.bigagriculture.block.BlockIrrigatedFarmland;

public class ModBlocks {
	
	public static BlockController controller;
	public static BlockGenerator generator;
	public static BlockExpander expander;
	public static BlockFertilizer fertilizer;
	public static BlockIrrigatedFarmland irrigatedFarmland;
	public static BlockInfinityStone infinity_stone;
	
	public static void init() {
		controller = new BlockController();	
		generator = new BlockGenerator();
		expander = new BlockExpander();
		fertilizer = new BlockFertilizer();
		irrigatedFarmland = new BlockIrrigatedFarmland();
		infinity_stone = new BlockInfinityStone();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		controller.initModel();
		generator.initModel();
		expander.initModel();
		fertilizer.initModel();
		irrigatedFarmland.initModel();
		infinity_stone.initModel();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initItemModels() {
		
	}
}
