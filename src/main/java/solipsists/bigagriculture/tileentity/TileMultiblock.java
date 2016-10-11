package solipsists.bigagriculture.tileentity;

/***
 * Extend me to be a valid multiblock tile
 */
public class TileMultiblock extends TileInventoryHandler {

    public TileMultiblock() {
        super(0);
    }

    public TileMultiblock(int slots) {
        super(slots);
    }

}
