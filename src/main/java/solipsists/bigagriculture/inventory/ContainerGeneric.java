package solipsists.bigagriculture.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import solipsists.bigagriculture.tileentity.TileInventoryHandler;

import javax.annotation.Nullable;

public class ContainerGeneric extends Container {

    private TileEntity te;

    public ContainerGeneric(IInventory playerInventory, TileEntity te) {
        this.te = te;
        addPlayerSlots(playerInventory);
    }

    public IItemHandler getIItemHandler() {
        if (te instanceof ICapabilityProvider)
            return this.te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        return null;
    }


    protected void addPlayerSlots(IInventory playerInventory) {
        // Slots for the main inventory
        for (int row = 0; row < 3; ++row) {
            for (int col = 0; col < 9; ++col) {
                int x = 9 + col * 18;
                int y = row * 18 + 70;
                this.addSlotToContainer(new Slot(playerInventory, col + row * 9 + 10, x, y));
            }
        }

        // Slots for the hotbar
        for (int row = 0; row < 9; ++row) {
            int x = 9 + row * 18;
            int y = 58 + 70;
            this.addSlotToContainer(new Slot(playerInventory, row, x, y));
        }
    }

    @Nullable
    @Override
    public ItemStack transferStackInSlot(EntityPlayer playerIn, int index) {
        ItemStack previous = null;
        Slot slot = this.inventorySlots.get(index);

        if (slot != null && slot.getHasStack()) {
            ItemStack current = slot.getStack();
            previous = current.copy();

            // TODO: Re-implement stack merging
            /*
            if (index < TileController.SLOTS) {
                if (!this.mergeItemStack(current, TileController.SLOTS, this.inventorySlots.size(), true)) {
                    return null;
                }
            } else if (!this.mergeItemStack(current, 0, TileController.SLOTS, false)) {
                return null;
            }
            */

            if (current.stackSize == 0)
                slot.putStack(null);
            else
                slot.onSlotChanged();

            if (current.stackSize == previous.stackSize)
                return null;
            slot.onPickupFromSlot(playerIn, current);
        }
        return previous;
    }

    @Override
    public boolean canInteractWith(EntityPlayer playerIn) {
        if (te instanceof TileInventoryHandler)
            return ((TileInventoryHandler) te).canInteractWith(playerIn);
        return false;
    }
}
