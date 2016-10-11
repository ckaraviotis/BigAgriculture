package solipsists.bigagriculture.tileentity;

import net.minecraft.util.ITickable;

public class TileGenerator extends TileEnergyGeneric implements ITickable {

    private final static int capacity = 10000;
    private final static int transfer = 20;

    public TileGenerator() {
        super(capacity, 0, transfer);
    }

    @Override
	public void update() {
        this.generate(transfer);
        this.push(transfer);
    }
}
