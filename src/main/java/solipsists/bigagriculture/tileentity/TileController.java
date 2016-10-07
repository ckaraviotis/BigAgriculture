package solipsists.bigagriculture.tileentity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.BlockCrops;
import net.minecraft.block.BlockDirt;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
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
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.block.BlockExpander;
import solipsists.bigagriculture.block.BlockFertilizer;
import solipsists.bigagriculture.block.BlockMultiblock;
import solipsists.bigagriculture.multiblock.Multiblock;

public class TileController extends TileMultiblock implements ITickable {

	// Crash when placing on top of a Generator

	private static final int BASE_RADIUS = 1;
	private int radius = BASE_RADIUS;
	public int tick = 0;
	private Random rand = new Random();

	public static final int SLOTS = 10;

	public boolean hasIrrigator = true;
	public boolean hasFertilizer = false;
	private int fertilizerChance = 0;
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

	/*private void printMB() {
		BigAgriculture.logger.log(Level.INFO, "Multiblock contains the following:");
		for (HashMap.Entry<BlockPos, Boolean> pair : tMultiBlock.entrySet()) {
			int x = pair.getKey().getX();
			int y = pair.getKey().getY();
			int z = pair.getKey().getZ();
			Boolean valid = pair.getValue();			
			BigAgriculture.logger.log(Level.INFO, "Coords [" + x + "," + y + "," + z + "] in set. Valid=" + valid);
		}
	}*/

	private int getFertilizerChance() {
		int chance = 0;
		/*
		for (HashMap.Entry<BlockPos, Boolean> pair : tMultiBlock.entrySet()) {
			Block b = this.worldObj.getBlockState(pair.getKey()).getBlock();

			if (b instanceof BlockFertilizer) {
				TileFertilizer t = (TileFertilizer)worldObj.getTileEntity(pair.getKey());
				chance += t.CHANCE;
			}
		}

		if (chance > 100)
			chance = 100;
	 	*/
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
		for (int i = 1; i < itemStackHandler.getSlots(); i++) {
			ItemStack itemStack = itemStackHandler.getStackInSlot(i);

			if (itemStack != null) {
				int stack = itemStackHandler.getStackInSlot(i).stackSize;
				int max = itemStackHandler.getStackInSlot(i).getMaxStackSize();

				if (stack < max)
					return false;
			}

		}
		return true;
	}

	@Override
	public void update() {
		if(!this.worldObj.isRemote) {
			multiBlockRefresh++;

			if (multiBlockRefresh >= 100) {
				multiBlockRefresh = 0;
				// Check we have a valid MB, and set the controller active as required
				// TODO: Re-check inventory when items are removed				
				multiblock.buildMultiblock(worldObj, pos, true);
				boolean multiblockValid = multiblock.isValid();

				//printMB();

				if (!multiblockValid && owner != null)
					owner.addChatComponentMessage(new TextComponentString("Multiblock structure is invalid."));

				isActive = multiblockValid;// && inventoryHasRoom;
				//clearRemovedMBBlocks();
				fertilizerChance = getFertilizerChance();
				hasFertilizer = fertilizerChance > 0;
			}

			boolean cropActionTaken = false;	
			boolean fertActionTaken = false;

			// TODO Radius should start from MB boundary, not controller 
			// Add up radius mods.
			this.radius = multiblock.getMultiblockRadius(worldObj, BASE_RADIUS);

			for (int i = -radius; i <= radius; i++) {
				for (int j = -radius; j <= radius; j++) {

					if (i != 0 || j != 0) {
						BlockPos earthPos = this.getPos().add(i,-1,j);	
						IBlockState earthState = worldObj.getBlockState(earthPos);
						Block earth = earthState.getBlock();
						BlockPos plantPos = earthPos.up();		

						boolean isFarmland = earth instanceof BlockFarmland;
						boolean isDirt = earth instanceof BlockDirt || earth instanceof BlockGrass;

						// Till & water the earth
						if (isDirt && (!isFarmland || earthState.getValue(BlockFarmland.MOISTURE) < 7)) {
							IBlockState stateToModify = isFarmland ? earthState : Blocks.FARMLAND.getDefaultState();
							if (hasIrrigator)
								worldObj.setBlockState(earthPos, stateToModify.withProperty(BlockFarmland.MOISTURE, 7), 2);
							else 
								worldObj.setBlockState(earthPos, stateToModify, 2);								
						}

						isFarmland = earth instanceof BlockFarmland;
						if (!cropActionTaken) {	
							// Plant seeds!
							if (isFarmland && this.worldObj.isAirBlock(plantPos)) {
								ItemStack inputStack = itemStackHandler.getStackInSlot(0);

								if (inputStack != null && inputStack.getItem() instanceof IPlantable) {								
									IPlantable crop = (IPlantable) itemStackHandler.getStackInSlot(0).getItem();
									IBlockState cropState = crop.getPlant(worldObj, plantPos);

									//inputStack = decrementStack(inputStack, 1);

									if (inputStack != null)
										worldObj.setBlockState(plantPos, cropState, 7);
									cropActionTaken = true;
								}

							}


							// Tick the crops
							// FIXME only ticks first couple of crops because of the way we loop through the blocks
							if (!fertActionTaken && hasFertilizer && rand.nextInt(100) <= fertilizerChance) {
								if (!this.worldObj.isAirBlock(plantPos)) {
									IBlockState plantState = this.worldObj.getBlockState(plantPos);
									Block plant = plantState.getBlock();

									if ((plant instanceof IGrowable || plant instanceof IPlantable) && !(plant instanceof BlockGrass)) {
										plant.updateTick(this.worldObj, plantPos, plantState, rand);
										IBlockState newState = this.worldObj.getBlockState(plantPos);
										if (newState.getBlock().getMetaFromState(newState) != plant.getMetaFromState(plantState)) {
											this.worldObj.playEvent(2005,  plantPos,  0);
										}
										fertActionTaken = true;
									}
								}
							}

							// Harvest the crops							
							IBlockState cropState = this.worldObj.getBlockState(plantPos);
							Block cropBlock = cropState.getBlock();

							if (cropBlock instanceof IGrowable) {
								if (!((IGrowable)cropBlock).canGrow(worldObj, plantPos, cropState, true)) {
									List<ItemStack> drops = cropBlock.getDrops(worldObj, plantPos, cropState, 0);
									worldObj.removeTileEntity(plantPos);
									worldObj.setBlockToAir(plantPos);

									if(drops != null) {
										for(ItemStack drop : drops) {
											// Start at slot 1. Refresh slot 0 later.
											inventoryHasRoom = addToItemStackHandler(itemStackHandler, drop, 1, "+-> ");
										}
									}
									cropActionTaken = true;
								}
							}


						}
					}
				}
			}
			//}



		}

	}


}
