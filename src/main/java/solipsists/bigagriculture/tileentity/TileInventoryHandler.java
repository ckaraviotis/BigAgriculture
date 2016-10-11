package solipsists.bigagriculture.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;

public class TileInventoryHandler extends TileEnergyGeneric implements ICapabilityProvider {

    private ItemStackHandler itemStackHandler;

    public TileInventoryHandler(int slots) {
        this(slots, 0);
    }

    ;

    public TileInventoryHandler(int slots, int capacity) {
        this(slots, capacity, capacity);
    }

    public TileInventoryHandler(int slots, int capacity, int maxTransmit) {
        this(slots, capacity, maxTransmit, maxTransmit);
    }

    public TileInventoryHandler(int slots, int capacity, int maxReceive, int maxExtract) {
        super(capacity, maxReceive, maxExtract);
        itemStackHandler = new ItemStackHandler(slots) {
            @Override
            protected void onContentsChanged(int slot) {
                TileInventoryHandler.this.markDirty();
            }
        };
    }

    @Override
    public void readFromNBT(NBTTagCompound compound) {
        super.readFromNBT(compound);
        if (compound.hasKey("items")) {
            itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
        }
    }

    @Override
    public NBTTagCompound writeToNBT(NBTTagCompound compound) {
        super.writeToNBT(compound);
        compound.setTag("items", itemStackHandler.serializeNBT());
        return compound;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return true;

        return super.hasCapability(capability, facing);
    }

    @Override
    public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) itemStackHandler;

        return super.getCapability(capability, facing);
    }

    public boolean canInteractWith(EntityPlayer playerIn) {
        // If we are too far away from this tile entity you cannot use it
        return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
    }
}
