package solipsists.bigagriculture.proxy;

import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import solipsists.bigagriculture.BigAgriculture;

public class ClientProxy extends CommonProxy {
	@Override
	public void preInit(FMLPreInitializationEvent e) {
		// init models etc.
		super.preInit(e);
		OBJLoader.INSTANCE.addDomain(BigAgriculture.MODID);;	
	}
	
	@Override
	public void init(FMLInitializationEvent e) {
	}
	
}