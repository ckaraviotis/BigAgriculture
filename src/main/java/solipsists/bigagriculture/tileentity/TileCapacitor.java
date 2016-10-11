package solipsists.bigagriculture.tileentity;

import org.apache.logging.log4j.Level;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.energy.CapabilityEnergy;
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
		String msg2 = "[n=" + n + ", m=" + m + ", p=" + p;
		BigAgriculture.logger.log(Level.INFO, msg + msg2);
	}

	@Override
	public void update() {

			for (EnumFacing f : EnumFacing.values()) {
				if (this.getEnergyStored() >= this.transfer) {			
					TileEntity te = worldObj.getTileEntity(getPos().offset(f));
				
					if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, f)) {
						
						// TODO: Base class so all energy blocks can call this.
						//boolean canReceive = ((TileEnergyGeneric)te).canReceive();
						int simExtract = this.extractEnergy(this.transfer, true);
						int simReceive = te.getCapability(CapabilityEnergy.ENERGY, f.getOpposite()).receiveEnergy(simExtract,  true);
						if (simExtract > 0 && simReceive > 0) {
							te.getCapability(CapabilityEnergy.ENERGY, f.getOpposite()).receiveEnergy(this.extractEnergy(this.transfer, false),  false);
						}
						
					}
				}
			}
		
		
	}
}
