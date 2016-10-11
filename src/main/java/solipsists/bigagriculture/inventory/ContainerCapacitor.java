package solipsists.bigagriculture.inventory;

import net.minecraft.inventory.IInventory;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import solipsists.bigagriculture.tileentity.TileCapacitor;

public class ContainerCapacitor extends ContainerGeneric {

    public ContainerCapacitor(IInventory playerInventory, TileCapacitor te) {
        super(playerInventory, te);

        addOwnSlots();
        this.addPlayerSlots(playerInventory);
    }

    private void addOwnSlots() {
        IItemHandler itemHandler = this.getIItemHandler();

        // Input slot
        addSlotToContainer(new SlotItemHandler(itemHandler, 0, 82, 29));

        int x = 117;
        int y = 11;

        for (int i = 1; i < itemHandler.getSlots(); i++) {
            if (x > 153) {
                x = 117;
                y += 18;
            }
            addSlotToContainer(new SlotItemHandler(itemHandler, i, x, y));
            x += 18;
        }
    }

}
