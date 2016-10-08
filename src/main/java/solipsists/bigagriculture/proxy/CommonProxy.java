package solipsists.bigagriculture.proxy;

import java.io.File;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.Config;
import solipsists.bigagriculture.ModBlocks;

public class CommonProxy {

	public static Configuration config;
	
	public void preInit(FMLPreInitializationEvent e) {
		// Init blocks, items etc.
		File directory = e.getModConfigurationDirectory();
		config = new Configuration(new File(directory.getPath(), "bigagriculture.cfg"));
		Config.readConfig();
		
		ModBlocks.init();
	}

	public void init(FMLInitializationEvent e) {
		NetworkRegistry.INSTANCE.registerGuiHandler(BigAgriculture.instance, new GuiProxy());
	}

	public void postInit(FMLPostInitializationEvent e) {
		if (config.hasChanged()) {
			config.save();
		}
	}
}