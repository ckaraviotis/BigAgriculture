package solipsists.bigagriculture.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import solipsists.bigagriculture.util.EnergyHandler;

public class TileEnergyGeneric  extends TileEntity implements IEnergyStorage, ICapabilityProvider {
	
	private EnergyHandler energy;
	
	public TileEnergyGeneric(int capacity) {
		this(capacity, capacity);
	}
	
	public TileEnergyGeneric(int capacity, int maxTransmit) {
		this(capacity, maxTransmit, maxTransmit);
	}
	
	public TileEnergyGeneric(int capacity, int maxReceive, int maxExtract) {
		energy = new EnergyHandler(capacity, maxReceive, maxExtract);
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int rf = compound.getInteger("energy");
		energy.setEnergy(rf);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("energy", energy.getEnergyStored());
		return compound;
	}
	
	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		//return 0;
		return energy.receiveEnergy(maxReceive, simulate);
	}

	@Override
	public int extractEnergy(int maxExtract, boolean simulate) {
		return energy.extractEnergy(maxExtract, simulate);
	}

	@Override
	public int getEnergyStored() {
		return energy.getEnergyStored();
	}

	@Override
	public int getMaxEnergyStored() {
		return energy.getMaxEnergyStored();
	}

	@Override
	public boolean canExtract() {
		return true;
	}

	@Override
	public boolean canReceive() {
		return true;
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
