package solipsists.bigagriculture.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileExpander extends TileMultiblock {

	public static int RADIUS = 1;
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("radius")) {
			this.RADIUS = compound.getInteger("radius");
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("radius", RADIUS);
		return compound;
	}
	
}
