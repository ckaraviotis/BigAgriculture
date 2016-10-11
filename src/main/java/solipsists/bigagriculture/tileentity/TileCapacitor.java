package solipsists.bigagriculture.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
import net.minecraftforge.energy.CapabilityEnergy;
import org.apache.logging.log4j.Level;
import solipsists.bigagriculture.BigAgriculture;

import javax.annotation.Nullable;

public class TileCapacitor extends TileInventoryHandler implements ITickable {

    public static final int SLOTS = 4;
    private static final int CAPACITY = 1000000;
    private static final int TRANSFER = 1024;

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
        // Send energy to sides
        this.push(TRANSFER);

        // Send energy to items
        sendEnergyToItems();

        // Fag for Update!
        markDirty();

        if (!worldObj.isRemote) {
            IBlockState state = this.worldObj.getBlockState(pos);
            final int flags = 3;
            this.worldObj.notifyBlockUpdate(pos, state, state, flags);
        }
    }

    private void sendEnergyToItems() {
        for (int i = 0; i < itemStackHandler.getSlots(); i++) {
            ItemStack inputStack = itemStackHandler.getStackInSlot(i);

            if (inputStack != null && inputStack.hasCapability(CapabilityEnergy.ENERGY, null)) {
                int simExtract = this.extractEnergy(TRANSFER, true);
                int simReceive = inputStack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(simExtract, true);
                if (simExtract > 0 && simReceive > 0) {
                    inputStack.getCapability(CapabilityEnergy.ENERGY, null).receiveEnergy(this.extractEnergy(TRANSFER, false), false);
                }
            }
        }
    }

    @Override
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound updateTag = new NBTTagCompound();
        writeToNBT(updateTag);
        return updateTag;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        NBTTagCompound updatePacket = getUpdateTag();
        this.writeToNBT(updatePacket);
        return new SPacketUpdateTileEntity(getPos(), getBlockMetadata(), updatePacket);
    }

    @Override
    public void onDataPacket(net.minecraft.network.NetworkManager net, net.minecraft.network.play.server.SPacketUpdateTileEntity pkt) {
        readFromNBT(pkt.getNbtCompound());
    }

    @Override
    public void handleUpdateTag(NBTTagCompound tag) {
        this.readFromNBT(tag);
    }

}
