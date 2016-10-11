package solipsists.bigagriculture.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import solipsists.bigagriculture.tileentity.TileCapacitor;

public class ContainerCapacitor extends ContainerGeneric {

    protected TileCapacitor tc;

    public ContainerCapacitor(IInventory playerInventory, TileCapacitor te) {
        super(playerInventory, te);

        tc = te;
        addOwnSlots();
        this.addPlayerSlots(playerInventory);
    }

    private void addOwnSlots() {
        IItemHandler itemHandler = this.getIItemHandler();

        int x = 55;
        int y = 47;

        for (int i = 0; i < itemHandler.getSlots(); i++) {
            addSlotToContainer(new SlotItemHandler(itemHandler, i, x, y));
            x += 18;
        }
    }

}
