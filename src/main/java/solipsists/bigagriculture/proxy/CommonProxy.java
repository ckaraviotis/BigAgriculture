package solipsists.bigagriculture.proxy;

import java.io.File;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.Config;

public class CommonProxy {

	//public static Configuration config;
	
	public void preInit(FMLPreInitializationEvent e) {
		// Init blocks, items etc.
		File directory = e.getModConfigurationDirectory();
		BigAgriculture.config = new Configuration(new File(directory.getPath(), "bigagriculture.cfg"));
		Config.readConfig();
	}

	public void init(FMLInitializationEvent e) {

	}

	public void postInit(FMLPostInitializationEvent e) {
		if (BigAgriculture.config.hasChanged()) {
			BigAgriculture.config.save();
		}
	}
}