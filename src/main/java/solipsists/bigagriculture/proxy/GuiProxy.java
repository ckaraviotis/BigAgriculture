package solipsists.bigagriculture.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import solipsists.bigagriculture.block.BlockCapacitor;
import solipsists.bigagriculture.block.BlockController;
import solipsists.bigagriculture.gui.GuiContainerCapacitor;
import solipsists.bigagriculture.gui.GuiContainerController;
import solipsists.bigagriculture.inventory.ContainerCapacitor;
import solipsists.bigagriculture.inventory.ContainerController;
import solipsists.bigagriculture.tileentity.TileCapacitor;
import solipsists.bigagriculture.tileentity.TileController;

public class GuiProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);

        if (ID == BlockController.GUI_ID) {
            return new ContainerController(player.inventory, (TileController) te);
		} else if (ID == BlockCapacitor.GUI_ID) {
            return new ContainerCapacitor(player.inventory, (TileCapacitor) te);
        }
        return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);

        if (ID == BlockController.GUI_ID) {
            TileController containerTileEntity = (TileController) te;
			return new GuiContainerController(containerTileEntity, new ContainerController(player.inventory, containerTileEntity));
        } else if (ID == BlockCapacitor.GUI_ID) {
            TileCapacitor capacitor = (TileCapacitor) te;
            return new GuiContainerCapacitor(capacitor, new ContainerCapacitor(player.inventory, capacitor));
        }
		return null;
	}

}
