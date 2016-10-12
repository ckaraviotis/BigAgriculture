package solipsists.bigagriculture.tileentity;

import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.items.ItemStackHandler;
import solipsists.bigagriculture.ModBlocks;
import solipsists.bigagriculture.block.BlockFertilizer;
import solipsists.bigagriculture.block.BlockIrrigatedFarmland;
import solipsists.bigagriculture.multiblock.Multiblock;

import java.util.List;
import java.util.Random;

public class TileController extends TileMultiblock implements ITickable, ICapabilityProvider {

    public static final int SLOTS = 10;
    public int tickCounter = 0;
    public boolean inventoryHasRoom = true;
    public boolean isActive = false;    // is multiblock complete?
    private int radius;
    private int operationInterval = 0;
	private Random rand = new Random();
	private boolean hasIrrigator = false;
	private boolean hasFertilizer = false;
	private double fertilizerChance = 0;
	private boolean hasUnderground = false; // Build multiblock beneath the crops?
	private boolean hasInfinityStone = false;
	private boolean hasVoidStone = false;
	private EntityPlayer owner;
	// Multiblock vars
	private int multiBlockRefresh = 1000;
	private Multiblock multiblock = new Multiblock();

    public TileController() {
        super(SLOTS);
    }

    public void setOwner(EntityPlayer p) {
		this.owner = p;
	}

	public void highlightMultiblock() {
		multiblock.highlight();
	}


	private double getFertilizerChance() {		
		int count = multiblock.getBlocksOfType(Multiblock.TYPE.FERTILIZER);
		double chance = Math.min(count * BlockFertilizer.CHANCE, 1);
		
		return chance;
	}

	private ItemStack decrementStack(ItemStack itemStack, int amount) {
		if (itemStack.stackSize <= amount) {
			return null;			
		}

		ItemStack split = itemStack.splitStack(amount);
		return split;
	}

	private boolean addToItemStackHandler (ItemStackHandler itemStackHandler, ItemStack itemStack, int startSlot, String indent){
		boolean done = false;

		for (int i = startSlot; i < itemStackHandler.getSlots(); i++) {
			if (!done) {
				ItemStack simulate = itemStackHandler.insertItem(i, itemStack, true);
				if (simulate == null) {
					ItemStack remainder = itemStackHandler.insertItem(i, itemStack, false);		
					done = true;
				}
				else if (simulate.getItem() != itemStack.getItem()) {
					// Slot contains incompatible ItemStack
					ItemStack remainder = itemStackHandler.insertItem(i, itemStack, false);
					boolean success = addToItemStackHandler(itemStackHandler, remainder, i + 1, "+- ");

					if (!success)
						return false;

					done = true;
				}
				else if (simulate.getItem() == itemStack.getItem()){
					// Stack has overflowed. Insert and handle remainder.
					if (i == itemStackHandler.getSlots() - 1) {
						return false;
					}
					indent = "|  " + indent;
					ItemStack remainder = itemStackHandler.insertItem(i, itemStack, false);
					boolean success = addToItemStackHandler(itemStackHandler, remainder, i + 1, indent);

					if (!success)
						return false;

					done = true;
				}
				else {
					return false;
				}
			}
		}
		return true;

	}

	private boolean isInventoryFull() {
		// Get stack count for each inv slot. If any are < max,
		// return false.
		if (hasVoidStone)
			return false;
		
		for (int i = 0; i < itemStackHandler.getSlots(); i++) {
			ItemStack itemStack = itemStackHandler.getStackInSlot(i);

			if (itemStack != null) {
				int stack = itemStackHandler.getStackInSlot(i).stackSize;
				int max = itemStackHandler.getStackInSlot(i).getMaxStackSize();

				if (stack < max)
					return false;
			} else {
				return false;
			}

		}
		return true;
	}
	
	private boolean till(BlockPos pos) {
		// Irrigation and tilling happens on the block BELOW current
		pos = pos.add(0,-1,0);
		IBlockState state = worldObj.getBlockState(pos);
		Block b = state.getBlock();
		boolean isFarmland = b instanceof BlockFarmland;
		boolean isDirt = b instanceof BlockDirt || b instanceof BlockGrass;
	
		// Replace vanilla farmland with perma-irrigated
        if (hasIrrigator && !(b instanceof BlockIrrigatedFarmland) && (isDirt || b instanceof BlockFarmland)) {
            IBlockState irrigated = ModBlocks.irrigatedFarmland.getDefaultState();
			worldObj.setBlockState(pos, irrigated, 2);	
		}
		// Irrigator has been removed, reset to farmland
		else if (!hasIrrigator && (b instanceof BlockIrrigatedFarmland)) {
			IBlockState tilled = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7);
			worldObj.setBlockState(pos, tilled, 2);		
		}
		// Till Current Block
		else if (isDirt) {
			IBlockState tilled = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7);
			worldObj.setBlockState(pos, tilled, 2);		
		}
		
		return isFarmland;

	}
	
	private void plant(BlockPos pos, boolean isFarmland) {
		IBlockState state = worldObj.getBlockState(pos);
		Block b = state.getBlock();
		
		// Plant seeds!
		if (isFarmland && this.worldObj.isAirBlock(pos)) {
			ItemStack inputStack = itemStackHandler.getStackInSlot(0);

			if (inputStack != null && inputStack.getItem() instanceof IPlantable) {								
				IPlantable crop = (IPlantable) itemStackHandler.getStackInSlot(0).getItem();
				IBlockState cropState = crop.getPlant(worldObj, pos);

				if (!hasInfinityStone)
					inputStack = decrementStack(inputStack, 1);

				if (inputStack != null)
					worldObj.setBlockState(pos, cropState, 7);
				
				tickCounter = 0;
			}

		}		
	}
	
	private void accelerate(BlockPos pos) {
		IBlockState state = worldObj.getBlockState(pos);
		Block plant = state.getBlock();
		
		IBlockState ground = worldObj.getBlockState(pos.add(0,-1,0));
		
		// Increase Age of current crop if
		if (hasFertilizer && rand.nextDouble() <= fertilizerChance) {
			if (plant instanceof BlockCrops) {	
				if (ground.getBlock().isFertile(worldObj, pos.add(0,-1,0))) {
					int i = state.getValue(BlockCrops.AGE);
					if (i < ((BlockCrops)plant).getMaxAge()) {
						worldObj.setBlockState(pos, state.withProperty(BlockCrops.AGE, i+1));
						worldObj.playEvent(2005,  pos,  0);
					}
				}

			}
		}
	}
	
	private void harvest(BlockPos pos) {
		// Harvest the crops		
		IBlockState state = worldObj.getBlockState(pos);
		Block plant = state.getBlock();

		if (plant instanceof BlockCrops) {
			if (!((IGrowable)plant).canGrow(worldObj, pos, state, true)) {
				List<ItemStack> drops = plant.getDrops(worldObj, pos, state, 0);
				
				// Break and replant
				worldObj.removeTileEntity(pos);
				worldObj.setBlockToAir(pos);
				plant(pos, true);

				if(drops != null) {
					for(ItemStack drop : drops) {
						// Start at slot 1. Refresh slot 0 later.
						inventoryHasRoom = addToItemStackHandler(itemStackHandler, drop, 0, "+-> ");
					}					
				}
				tickCounter = 0;
			}
		}
	}

	@Override
	public void update() {
		if(!this.worldObj.isRemote) {
			tickCounter++;
			multiBlockRefresh++;

			if (multiBlockRefresh >= 100) {
				multiBlockRefresh = 0;
				
				// Check we have a valid MB, and set the controller active as required				
				multiblock.buildMultiblock(worldObj, pos, true);
                BlockPos c = multiblock.findYourCenter();
                boolean multiblockValid = multiblock.isValid();
				
				if (!multiblockValid && owner != null)
					owner.addChatComponentMessage(new TextComponentString("Multiblock structure is invalid."));

				isActive = multiblockValid;
				fertilizerChance = getFertilizerChance();
				hasFertilizer = fertilizerChance > 0; 
				hasInfinityStone = multiblock.getBlocksOfType(Multiblock.TYPE.INFINITY_STONE) > 0;
				hasIrrigator = multiblock.getBlocksOfType(Multiblock.TYPE.IRRIGATOR) > 0;
				hasVoidStone = multiblock.getBlocksOfType(Multiblock.TYPE.VOID_STONE) > 0;
			}

			if (tickCounter > operationInterval) {
				
				BlockPos current = multiblock.getNext();
				IBlockState state = worldObj.getBlockState(current);
				Block block = worldObj.getBlockState(current).getBlock();				
							
				if (current != null) {
					boolean isFarmland = till(current);
					
					plant(current, isFarmland);
					accelerate(current);
					if (!isInventoryFull()) {
						harvest(current);						
					}
				}				
				tickCounter = 0;
			}

		}

	}

	/***
	 * Destroy any Irrigated Farmland blocks
	 */
	public void saltTheEarth() {
		IBlockState state = Blocks.FARMLAND.getDefaultState().withProperty(BlockFarmland.MOISTURE, 7);
		
		if (multiblock.getSoil().size() > 0) {
			for (BlockPos pos : multiblock.getSoil()) {
                Block ground = worldObj.getBlockState(pos).getBlock();

                if (ground instanceof BlockIrrigatedFarmland)
                    worldObj.setBlockState(pos, state);
            }
		}
		
	}


}
