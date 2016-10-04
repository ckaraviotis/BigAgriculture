package solipsists.bigagriculture;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import solipsists.bigagriculture.proxy.CommonProxy;

@Mod(modid = BigAgriculture.MODID, name = BigAgriculture.MODNAME, version = BigAgriculture.VERSION)
public class BigAgriculture {

	public static final String MODID = "bigagriculture";
	public static final String MODNAME = "Big Agriculture";
	public static final String VERSION = "0.0.1";

	@SidedProxy(clientSide = "solipsists.bigagriculture.proxy.ClientProxy", serverSide = "solipsists.bigagriculture.proxy.ServerProxy")
	public static CommonProxy proxy;

	public static Logger logger;
	public static Configuration config;

	@EventHandler
	public void PreInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
		proxy.preInit(e);
	}

	@EventHandler
	public void Init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@EventHandler
	public void PostInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}
}
