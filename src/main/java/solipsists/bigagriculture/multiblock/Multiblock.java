package solipsists.bigagriculture.multiblock;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import net.minecraft.world.World;
import solipsists.bigagriculture.block.BlockMultiblock;

import java.util.*;

/***
 * Container for multiblock structure data
 */
public class Multiblock {

	private MultiblockStructure structure = new MultiblockStructure();
	
	private BlockPos current;	
	private BlockPos controller;
    private BlockPos center;
    private Integer RADIUS;
	
	public Set<BlockPos> getSoil() {
		Set<BlockPos> set = new HashSet<BlockPos>();

        if (center != null) {
            BlockPos topLeft = center.add(-RADIUS, -1, -RADIUS);
            BlockPos botRight = center.add(RADIUS, -1, RADIUS);


            for (Iterator<BlockPos> it = BlockPos.getAllInBox(topLeft, botRight).iterator(); it.hasNext(); ) {
                set.add(it.next());
			}
		}


		return set;
	}

    public BlockPos findYourCenter() {
        return center = structure.findYourCenter();
    }

    /**
	 * Return the next BlockPos in the operating area for the Multiblock,
	 * i.e. the soil to perform an action on.
	 * @return
	 */
	public BlockPos getNext() {
        if (center == null)
            center = controller;

        BlockPos topLeft = center.add(-RADIUS, 0, -RADIUS);
        BlockPos topRight = center.add(RADIUS, 0, -RADIUS);
        BlockPos botLeft = center.add(-RADIUS, 0, RADIUS);
        BlockPos botRight = center.add(RADIUS, 0, RADIUS);

		if (current == null) {
			current = topLeft;
			return current;
		}

		do {
			Vec3i right = new Vec3i(1, 0, 0);
			Vec3i originX = new Vec3i(-RADIUS * 2, 0, 0);
			Vec3i down = new Vec3i(0, 0, 1);
			Vec3i originZ = new Vec3i(0, 0, -RADIUS * 2);

			if (current.getX() < topRight.getX()) {
				current = current.add(right);
            } else if (current.getX() == topRight.getX()) {
                if (current.getZ() == botRight.getZ()) {
					// Reset X & Z
					current = current.add(originX).add(originZ);
				}
				else {
					// Reset X
					current = current.add(originX);
					current = current.add(down);
				}
			}
        } while (structure.boundsContain(current));

		return current;
	}

    /**
     * Confirm the structure is valid.
	 * @return
	 */
	public boolean isValid() {
		return structure.isValid();
	}

    /**
     * Call the recursive build, then remove invalid blocks
     * @param world
     * @param current
     * @param clear
     */
    public void build(World world, BlockPos current, boolean clear) {
        buildMultiblock(world, current, clear);
        structure.pruneInvalid();
        RADIUS = structure.getMultiblockRadius(world);
    }

    /**
     * Recursively build a multiblock structure, starting from a BlockPos.
     * @param current The starting BlockPos.
     */
	public void buildMultiblock(World world, BlockPos current, boolean clear) {
		if (clear) {
			structure.clear();
			controller = current;
		}

        List<BlockPos> neighbours = new ArrayList<BlockPos>();

		// TODO This implementation sucks.
		neighbours.add( current );
		neighbours.add( current.up() );
        neighbours.add(current.down());
        neighbours.add( current.north() );
		neighbours.add( current.south() );
		neighbours.add( current.east() );
		neighbours.add( current.west() );

        for (BlockPos neighbour : neighbours) {
            Block b = world.getBlockState(neighbour).getBlock();

            Multiblock.TYPE type;
            if (b instanceof BlockMultiblock) {
				type = ((BlockMultiblock) b).getType();
			} else {
				type = TYPE.NONMULTI;
			}

			if (!structure.contains(neighbour)) {
                // Add and flag as invalid and unchecked.
                structure.add(neighbour, type);
                structure.setChecked(neighbour, true);
                structure.setValid(neighbour, false);

                boolean isValidBlock = b instanceof BlockMultiblock;
                boolean isAir = world.isAirBlock(neighbour);
				boolean isCrop = b instanceof BlockCrops;

				if(!isValidBlock && !isAir && !isCrop) {
					structure.setValid(neighbour, false);
					structure.setChecked(neighbour, true);
				}
				if (isValidBlock) {
					structure.setValid(neighbour, true);
                    structure.setChecked(neighbour, true);

					buildMultiblock(world, neighbour, false);
				}

            }
        }


    }
	
	/**
	 * Get the radius of the multiblock accounting for expanders.
	 * @param world
	 * @return
	 */
	public int getMultiblockRadius(World world) {
		return structure.getMultiblockRadius(world);
    }

    /**
     * Get the number of blocks of a specific type in the multiblock
	 * @param type
	 * @return
	 */
	public int getBlocksOfType(Multiblock.TYPE type) {
		return structure.getBlocksOfType(type);
	}
	
	/**
	 * Render around all blocks contained in structure
	 */
	public void highlight() {
		structure.highlight();
    }

    public static enum TYPE {
        NONMULTI,
        DEFAULT,
        CONTROLLER,
        EXPANDER,
        FERTILIZER,
        INFINITY_STONE,
        IRRIGATOR,
        VOID_STONE,
        ILLUMINATOR
    }

}
