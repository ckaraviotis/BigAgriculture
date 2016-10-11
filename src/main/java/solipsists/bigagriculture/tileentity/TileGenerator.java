package solipsists.bigagriculture.tileentity;

import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.IEnergyStorage;

public class TileGenerator extends TileEnergyGeneric implements ITickable, IEnergyStorage, ICapabilityProvider {

    private final static int capacity = 10000;
    private final static int transfer = 20;

    public TileGenerator() {
        super(capacity, transfer);
    }

    @Override
	public void update() {
        this.generate(transfer);
        this.push(transfer);
    }

	@Override
	public boolean canReceive() {
		return false;
	}

}
