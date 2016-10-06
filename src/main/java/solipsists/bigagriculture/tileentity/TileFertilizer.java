package solipsists.bigagriculture.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;

public class TileFertilizer extends TileMultiblock {

	public static int CHANCE = 5;
	
	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("chance")) {
			this.CHANCE = compound.getInteger("chance");
		}
	}
	
	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setInteger("chance", CHANCE);
		return compound;
	}
	
}
