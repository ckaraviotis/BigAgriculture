package solipsists.bigagriculture.tileentity;

import org.apache.logging.log4j.Level;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityInject;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.util.EnergyHandler;

public class TileGenerator extends TileEntity implements ITickable, IEnergyStorage, ICapabilityProvider {

	public int rfpertick = 20;
	public int capacity = 500000;
	public int maxReceive = 500000;
	public int maxExtract = 500000;
	
	private EnergyHandler energyStorage;
	private int counter = 0;
	
	public TileGenerator() {
		energyStorage = new EnergyHandler(capacity, maxReceive, maxExtract);
	}
	
	@Override
	public void update() {
		BigAgriculture.instance.clientInfo.highlightBlock(this.pos, System.currentTimeMillis() + 1000);
	
		
		if (!worldObj.isRemote) {
			counter++;
			
			if (energyStorage.receiveEnergy(rfpertick, true) > 0) {
				energyStorage.receiveEnergy(rfpertick, false);
				markDirty();
			}
				
			
			if (counter % 40 == 0) {
				counter = 1;
				BigAgriculture.logger.log(Level.INFO, "Current Energy: " + getEnergyStored());			
			}
			
			if (energyStorage.getEnergyStored() > rfpertick) {
				
				boolean provided = false;
				for (EnumFacing f : EnumFacing.values()) {
					TileEntity te = worldObj.getTileEntity(getPos().offset(f));
					
					if (te != null && te.hasCapability(CapabilityEnergy.ENERGY, f) && !provided) {
						int ex = energyStorage.extractEnergy(rfpertick, false);
						te.getCapability(CapabilityEnergy.ENERGY, f.getOpposite()).receiveEnergy(ex,  false);
						provided = true;
					}
				}
			}
			
		}			
	}
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		int rf = compound.getInteger("energy");
		energyStorage.setEnergy(rf);
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("energy", energyStorage.getEnergyStored());
		return compound;
	}

	@Override
	public int receiveEnergy(int maxReceive, boolean simulate) {
		//return 0;
		return energyStorage.receiveEnergy(maxReceive, simulate);
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
