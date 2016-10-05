package solipsists.bigagriculture.proxy;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.ModBlocks;

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