package solipsists.bigagriculture.tileentity;

import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.util.ITickable;
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
        this.push(TRANSFER);
        markDirty();

        if (!worldObj.isRemote) {
            IBlockState state = this.worldObj.getBlockState(pos);
            final int flags = 3;
            this.worldObj.notifyBlockUpdate(pos, state, state, flags);
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
