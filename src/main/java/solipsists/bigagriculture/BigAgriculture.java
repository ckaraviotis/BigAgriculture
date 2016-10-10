package solipsists.bigagriculture;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.proxy.CommonProxy;

@Mod(modid = BigAgriculture.MODID, name = BigAgriculture.MODNAME, version = BigAgriculture.MODVERSION)
public class BigAgriculture {

	public static final String MODID = "bigagriculture";
	public static final String MODNAME = "Big Agriculture";
	public static final String MODVERSION = "0.0.1";

	@SidedProxy(clientSide = "solipsists.bigagriculture.proxy.ClientProxy", serverSide = "solipsists.bigagriculture.proxy.ServerProxy")
	public static CommonProxy proxy;
	
	// Block highlight code from RFTools
	public ClientInfo clientInfo = new ClientInfo();
	
	@Mod.Instance
	public static BigAgriculture instance;
	public static Logger logger;

	@Mod.EventHandler
	public void PreInit(FMLPreInitializationEvent e) {
		logger = e.getModLog();
		proxy.preInit(e);
	}

	@Mod.EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}

	@Mod.EventHandler
	public void PostInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}
}
