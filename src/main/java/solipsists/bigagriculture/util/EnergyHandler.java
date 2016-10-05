package solipsists.bigagriculture.util;

import net.minecraftforge.energy.EnergyStorage;

public class EnergyHandler extends EnergyStorage {

	public EnergyHandler(int capacity) {
		super(capacity);
	}
	
	public EnergyHandler(int capacity, int maxTransfer) {
		super(capacity, maxTransfer);
	}
	
	public EnergyHandler(int capacity, int maxTransfer, int maxExtract) {
		super(capacity, maxTransfer, maxExtract);
	}
	
	public void setEnergy(int energy) {
		this.energy = energy;
	}

}
