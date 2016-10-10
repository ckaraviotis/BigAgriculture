package solipsists.bigagriculture;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.item.DebugWand;
import solipsists.bigagriculture.item.ItemGenericBA;

public class ModItems {

	public static ItemGenericBA debugWand;
	
	public static void init() {
		debugWand = new DebugWand();
	}
	
	@SideOnly(Side.CLIENT)
	public static void initClient() {
		debugWand.initModel();
	}
	
}
