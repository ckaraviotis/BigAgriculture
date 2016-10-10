package solipsists.bigagriculture.tileentity;

import java.util.List;
import java.util.Random;
import java.util.Set;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import solipsists.bigagriculture.ModBlocks;
import solipsists.bigagriculture.block.BlockFertilizer;
import solipsists.bigagriculture.block.BlockIrrigatedFarmland;
import solipsists.bigagriculture.multiblock.Multiblock;

public class TileController extends TileMultiblock implements ITickable {

	// Crash when placing on top of a Generator

	//private static final int BASE_RADIUS = 1;
	private int radius;
	public int tickCounter = 0;
	private int operationInterval = 0;
	private Random rand = new Random();

	public static final int SLOTS = 10;

	public boolean hasIrrigator = true;
	public boolean hasFertilizer = false;
	private double fertilizerChance = 0;
	public boolean hasUnderground = false; // Build multiblock beneath the crops?

	public boolean inventoryHasRoom = true;

	private EntityPlayer owner;

	// Multiblock vars
	public int multiBlockRefresh = 1000;
	public boolean isActive = false;	// is multiblock complete?
	private Multiblock multiblock = new Multiblock();
	
	public void setOwner(EntityPlayer p) {
		this.owner = p;
	}

	@Deprecated
	public boolean changeItem(ItemStack item, EntityPlayer player) {	
		return false;
	}
	
	public void highlightMultiblock() {
		multiblock.highlight();
	}

	// Inventory slots
	private ItemStackHandler itemStackHandler = new ItemStackHandler(SLOTS) {
		@Override
		protected void onContentsChanged(int slot) {
			TileController.this.markDirty();
		}
	};

	public boolean canInteractWith(EntityPlayer playerIn) {
		// If we are too far away from this tile entity you cannot use it
		return !isInvalid() && playerIn.getDistanceSq(pos.add(0.5D, 0.5D, 0.5D)) <= 64D;
	}

	@Override
	public void readFromNBT(NBTTagCompound compound) {
		super.readFromNBT(compound);
		if (compound.hasKey("items")) {
			itemStackHandler.deserializeNBT((NBTTagCompound) compound.getTag("items"));
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound compound) {
		super.writeToNBT(compound);
		compound.setTag("items",  itemStackHandler.serializeNBT());
		return compound;
	}

	@Override
	public boolean hasCapability(Capability<?> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return true;

		return super.hasCapability(capability, facing);
	}

	@Override
	public <T> T getCapability(Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
			return (T) itemStackHandler;

		return super.getCapability(capability, facing);
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
		if (hasIrrigator && !(b instanceof BlockIrrigatedFarmland)) {
			IBlockState irrigated = ModBlocks.irrigatedFarmland.getDefaultState();
			worldObj.setBlockState(pos, irrigated, 2);	
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
		
		// Increase Age of current crop if
		if (hasFertilizer && rand.nextDouble() <= fertilizerChance) {
			if (plant instanceof BlockCrops) {
				PropertyInteger AGE = PropertyInteger.create("age", 0, 7);
				int i = state.getValue(AGE);
				if (i < 7) {
					worldObj.setBlockState(pos, state.withProperty(AGE, i+1));
					worldObj.playEvent(2005,  pos,  0);
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
				boolean multiblockValid = multiblock.isValid();
				
				if (!multiblockValid && owner != null)
					owner.addChatComponentMessage(new TextComponentString("Multiblock structure is invalid."));

				isActive = multiblockValid;
				fertilizerChance = getFertilizerChance();
				hasFertilizer = fertilizerChance > 0; 
			}

			// TODO: Add block to increase refresh rate! Variable by number of blocks
			// between per tick up to 2s
			
			// TODO: Energy acceptor block. Farm requires energy (Forge)
			// TODO: Move storage into a separate block. More blocks = more storage.
			// TODO: I/O Block to receive and export items
			// TODO: Fluid I/O block. Allows water to be piped in. Cheaper than irrigator.
			// TODO: Add irrigator block. Code already here for it to work. Should be expensive.
			// TODO: Allow planting of pumpkins / melons. Stems marked as invalid blocks & fertilizer doesn't work.
			// TODO: Allow planting of Trees & Reeds.
			// TODO: Look at code for TFTools Storage Scanner for block-highlighting code (for outlining multiblocks)
			// TODO: Irrigated soil degrades into sand/dirt if multiblock is removed.
			// TODO: Radius determined from multiblock center, not controller.
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
				worldObj.setBlockState(pos, state);
			}
		}
		
	}


}
