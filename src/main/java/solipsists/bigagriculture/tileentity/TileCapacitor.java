package solipsists.bigagriculture.tileentity;

import net.minecraft.util.ITickable;
import org.apache.logging.log4j.Level;
import solipsists.bigagriculture.BigAgriculture;

public class TileCapacitor extends TileInventoryHandler implements ITickable {

    private static final int CAPACITY = 1000000;
    private static final int TRANSFER = 1024;
    private static final int SLOTS = 4;

    public TileCapacitor() {
        super(SLOTS, CAPACITY, TRANSFER);
    }
	
	public void getStatus() {
        int n = this.getEnergyStored();
        int m = this.getMaxEnergyStored();
        double p = ((double) n / (double) m) * 100.0;
		
		String msg = "" + n + "/" + m + " NRG, " + p + "% full.";
        BigAgriculture.logger.log(Level.INFO, msg);
    }

	@Override
	public void update() {
        this.push(TRANSFER);
    }
}
