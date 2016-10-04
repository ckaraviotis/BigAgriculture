package solipsists.bigagriculture;

import org.apache.logging.log4j.Level;

import net.minecraftforge.common.config.Configuration;

public class Config {
	private static final String CATEGORY_GENERAL = "general";
	
	public static boolean testValue = true;
	
	public static void readConfig() {
		Configuration cfg = BigAgriculture.config;
		try {
			cfg.load();
			initGeneralConfig(cfg);
		} catch (Exception ex) {
			BigAgriculture.logger.log(Level.ERROR, "Problem loading config", ex);
		} finally {
			if (cfg.hasChanged()) {
				cfg.save();
			}
		}
	}
	
	private static void initGeneralConfig(Configuration c) {
		c.addCustomCategoryComment(CATEGORY_GENERAL, "General Configuration");
		testValue = c.getBoolean("testValue", CATEGORY_GENERAL, testValue, "A test boolean value for the configuration file.");
	}
}
