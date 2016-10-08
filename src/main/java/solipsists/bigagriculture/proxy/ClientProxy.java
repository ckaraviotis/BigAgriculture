package solipsists.bigagriculture.proxy;

import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.ModBlocks;
import solipsists.bigagriculture.multiblock.MultiblockStructure;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		// init models etc.
		super.preInit(e);
		OBJLoader.INSTANCE.addDomain(BigAgriculture.MODID);
		
		ModBlocks.initModels();
	}
	
	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		ModBlocks.initItemModels();
	}
	
}