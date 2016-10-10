package solipsists.bigagriculture.proxy;

import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.ModBlocks;
import solipsists.bigagriculture.RenderWorldLastEventHandler;

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
		
		//MinecraftForge.EVENT_BUS.register(new RenderWorldLastEventHandler());
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	@SubscribeEvent
	public void renderWorldLastEvent(RenderWorldLastEvent event) {
		RenderWorldLastEventHandler.tick(event);
	}
	
}