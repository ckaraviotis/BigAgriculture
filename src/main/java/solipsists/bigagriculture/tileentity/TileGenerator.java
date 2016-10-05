package solipsists.bigagriculture.tileentity;

import org.apache.logging.log4j.Level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.util.EnergyHandler;

public class TileGenerator extends TileEntity implements ITickable, IEnergyStorage {

	public static int rfpertick = 20;
	public static int capacity = 500000;
	public static int maxReceive = 500000;
	public static int maxExtract = 500000;
	
	protected EnergyHandler energyStorage;
	int counter = 0;
	
	public TileGenerator() {
		energyStorage = new EnergyHandler(capacity, maxReceive, maxExtract);
	}
	
	@Override
	public void update() {
		if (!worldObj.isRemote) {
			counter++;
			
			if (energyStorage.receiveEnergy(rfpertick, true) > 0) {
				energyStorage.receiveEnergy(rfpertick, false);
				markDirty();
			}
				
			
			if (counter % 20 == 0) {
				counter = 1;
				BigAgriculture.logger.log(Level.INFO, "Current Energy: " + getEnergyStored());			
			}
		}			
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int rf = compound.getInteger("energyStored");
		energyStorage.setEnergy(rf);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("energyStored", energyStorage.getEnergyStored());
		return compound;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		return 0;
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return energyStorage.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return energyStorage.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return energyStorage.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return false;
	}
	
	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY) {
			return true;
		}
		return super.hasCapability(capability, facing);
	}
	
	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityEnergy.ENERGY)
			return (T) this;
		
		return super.getCapability(capability, facing);
	}

}
