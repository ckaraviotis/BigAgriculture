package solipsists.bigagriculture.tileentity;

import net.minecraft.util.ITickable;
import org.apache.logging.log4j.Level;
import solipsists.bigagriculture.BigAgriculture;

public class TileCapacitor extends TileEnergyGeneric implements ITickable {
	
	private static final int capacity = 1000000;
	private static final int transfer = 1024;
	
	public TileCapacitor() {
		super(capacity, transfer);
	}
	
	public void getStatus() {
		int n = getEnergyStored();
		int m = getMaxEnergyStored();
		double p = ((double) n / (double) m) * 100.0;
		
		String msg = "" + n + "/" + m + " NRG, " + p + "% full.";
        BigAgriculture.logger.log(Level.INFO, msg);
    }

	@Override
	public void update() {
        this.push(transfer);
    }
}
