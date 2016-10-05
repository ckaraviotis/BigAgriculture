package solipsists.bigagriculture.proxy;

import org.apache.logging.log4j.Level;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.gui.GuiTestContainer;
import solipsists.bigagriculture.tileentity.TileController;
import solipsists.bigagriculture.util.TestContainer;

public class GuiProxy implements IGuiHandler {

	@Override
	public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof TileController) {
		    return new TestContainer(player.inventory, (TileController) te);
		}
		return null;
	}

	@Override
	public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z) {
		BlockPos pos = new BlockPos(x, y, z);
		TileEntity te = world.getTileEntity(pos);
		
		if (te instanceof TileController) {
			TileController containerTileEntity = (TileController) te;
		    return new GuiTestContainer(containerTileEntity, new TestContainer(player.inventory, containerTileEntity));
		}
		return null;
	}

}
