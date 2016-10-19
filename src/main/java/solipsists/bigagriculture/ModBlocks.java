package solipsists.bigagriculture;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.block.*;

public class ModBlocks {
	
	public static BlockController controller;
	public static BlockGenerator generator;
	public static BlockExpander expander;
	public static BlockFertilizer fertilizer;
	public static BlockIrrigatedFarmland irrigatedFarmland;
	public static BlockInfinityStone infinity_stone;
	public static BlockIrrigator irrigator;
	public static BlockVoidStone void_stone;
	public static BlockCapacitor capacitor;
    public static BlockGlowingAir glowing_air;
    public static BlockIlluminator illuminator;
    public static BlockGroundLevel ground_level;

    public static void init() {
		controller = new BlockController();	
		generator = new BlockGenerator();
		expander = new BlockExpander();
		fertilizer = new BlockFertilizer();
		irrigatedFarmland = new BlockIrrigatedFarmland();
		infinity_stone = new BlockInfinityStone();
		irrigator = new BlockIrrigator();
		void_stone = new BlockVoidStone();
		capacitor = new BlockCapacitor();
        glowing_air = new BlockGlowingAir();
        illuminator = new BlockIlluminator();
        ground_level = new BlockGroundLevel();
    }
	
	@SideOnly(Side.CLIENT)
	public static void initModels() {
		controller.initModel();
		generator.initModel();
		expander.initModel();
		fertilizer.initModel();
		irrigatedFarmland.initModel();
		infinity_stone.initModel();
		irrigator.initModel();
		void_stone.initModel();
		capacitor.initModel();
        glowing_air.initModel();
        illuminator.initModel();
        ground_level.initModel();
    }
	
	@SideOnly(Side.CLIENT)
	public static void initItemModels() {
		
	}
}
