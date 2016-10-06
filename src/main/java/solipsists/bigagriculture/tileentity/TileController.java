package solipsists.bigagriculture.tileentity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.apache.logging.log4j.Level;

import net.minecraft.block.Block;
import net.minecraft.block.BlockFarmland;
import net.minecraft.block.BlockGrass;
import net.minecraft.block.IGrowable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.block.BlockController;
import solipsists.bigagriculture.block.BlockExpander;

public class TileController extends TileEntity implements ITickable {
	
	// Crash when placing on top of a Generator

	private static final int BASE_RADIUS = 1;
	private int radius = BASE_RADIUS;
	public int tick = 0;
	private Random rand = new Random();
	
	public static final int SLOTS = 1;
	
	public boolean hasIrrigator = true;
	public boolean hasFertilizer = true;
	public boolean hasUnderground = false; // Build multiblock beneath the crops?
	
	// Multiblock vars
	public int multiBlockRefresh = 100;
	public boolean isActive = false;	// is multiblock complete?
	protected Set<BlockPos> expanders = new HashSet<BlockPos>();
	protected Set<BlockPos> multiblock = new HashSet<BlockPos>();
	protected Set<BlockPos> mbChecked = new HashSet<BlockPos>();
	
	private ItemStack itemStack = new ItemStack(Items.WHEAT_SEEDS, 1);
	
	public boolean changeItem(ItemStack item, EntityPlayer player) {	
		Item a = item.getItem();
		if (item.getItem() instanceof IPlantable) {
			player.inventory.decrStackSize(player.inventory.currentItem, 1);
			this.itemStack = new ItemStack(item.getItem(), 1);
			return true;
		}		
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
	
	/***
	 * Validate the multiblock structure
	 * @param pos
	 * @return
	 */
	private boolean isMultiblockValid(BlockPos pos) {
		List<BlockPos> neighbours = new ArrayList<BlockPos>(); 
		
		// TODO This implementation sucks.
		neighbours.add( pos.up() );
		//neighbours.add( pos.down() );
		neighbours.add( pos.north() );
		neighbours.add( pos.south() );
		neighbours.add( pos.east() );
		neighbours.add( pos.west() );
		
		for(BlockPos neighbour : neighbours) {			
			Block b = this.worldObj.getBlockState(neighbour).getBlock();
			
			
			if (!mbChecked.contains(neighbour)) {
				mbChecked.add(neighbour);
				boolean isValidBlock = b instanceof BlockController || b instanceof BlockExpander;
				boolean isAir = this.worldObj.isAirBlock(neighbour);
				boolean isCrop = b instanceof IGrowable;				
				
				if(!isValidBlock && !isAir && !isCrop) {
					BigAgriculture.logger.log(Level.INFO, "Invalid block "+ b.getUnlocalizedName() + " at location: " + neighbour.getX() + ", " + neighbour.getY() + ", " + neighbour.getZ());
					return false;
				}
					
				if (isValidBlock) {
					// Add to multiblock set
					multiblock.add(neighbour);
					
					// Add to separate set of expanders
					if (b instanceof BlockExpander) {
						TileExpander t = (TileExpander) this.worldObj.getTileEntity(neighbour);
						t.CHECKED = true;
						expanders.add(neighbour);
					}
					
					if (!isMultiblockValid(neighbour))
						return false;
				}
			}
		}		
		
		return true;
	}
	
	private void clearRemovedExpanders() {
		// Purge removed expanders
		for (Iterator<BlockPos> i = expanders.iterator(); i.hasNext();) {
			try {
				BlockPos bp = i.next();
				Block b = this.worldObj.getBlockState(bp).getBlock();
				if (!(b instanceof BlockExpander)) {
					i.remove();
			}
			} catch (Exception e) {
				BigAgriculture.logger.log(Level.ERROR, "Something went wrong removing an expander!", e);
			}
		}

	}
	
	private int getMultiblockRadius(int base) {
		int rad = BASE_RADIUS;
		for (BlockPos e : expanders) {
			TileExpander t = (TileExpander)worldObj.getTileEntity(e);
			rad += t.RADIUS;
		}
		return rad;
	}
	
	@Override
	public void update() {
		if(!this.worldObj.isRemote) {
			tick++;
			multiBlockRefresh--;
			
			if (multiBlockRefresh <= 0) {
				multiBlockRefresh = 100;
				// Check we have a valid MB, and set the controller active as required
				isActive = isMultiblockValid(this.getPos());
				clearRemovedExpanders();
			}

			
			if (tick > 20 && isActive){
				tick = 0;

				// Add up multiblock mods.
				this.radius = getMultiblockRadius(BASE_RADIUS);
				
				for (int i = -radius; i <= radius; i++) {
					for (int j = -radius; j <= radius; j++) {
						if (i != 0 || j != 0) {
							BlockPos earthPos = this.getPos().add(i,-1,j);	
							IBlockState earthState = worldObj.getBlockState(earthPos);
							Block earth = earthState.getBlock();
							BlockPos plantPos = earthPos.up();		
							
							boolean isFarmland = earth instanceof BlockFarmland;
							
							// Till & water the earth
							if (!isFarmland || earthState.getValue(BlockFarmland.MOISTURE) < 7) {
								IBlockState stateToModify = isFarmland ? earthState : Blocks.FARMLAND.getDefaultState();
								if (hasIrrigator)
									worldObj.setBlockState(earthPos, stateToModify.withProperty(BlockFarmland.MOISTURE, 7), 2);
								else 
									worldObj.setBlockState(earthPos, stateToModify, 2);								
							}
							
							isFarmland = earth instanceof BlockFarmland;
							
							// Plant seeds!
							if (isFarmland && this.worldObj.isAirBlock(plantPos)) {
								IBlockState p = ((IPlantable)itemStack.getItem()).getPlant(worldObj, plantPos);
								worldObj.setBlockState(plantPos, p, 7);
							}
					
							
							// Tick the crops
							if (!this.worldObj.isAirBlock(plantPos)) {
								IBlockState plantState = this.worldObj.getBlockState(plantPos);
								Block plant = plantState.getBlock();
								
								if ((plant instanceof IGrowable || plant instanceof IPlantable) && !(plant instanceof BlockGrass)) {
									plant.updateTick(this.worldObj, plantPos, plantState, rand);
									IBlockState newState = this.worldObj.getBlockState(plantPos);
									if (newState.getBlock().getMetaFromState(newState) != plant.getMetaFromState(plantState)) {
										this.worldObj.playEvent(2005,  plantPos,  0);
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
											EntityItem e = new EntityItem(worldObj, plantPos.getX(), plantPos.getY(), plantPos.getZ(), drop);
											float f3 = 0.05F;
											e.motionX = (double)((float)worldObj.rand.nextGaussian() * f3);
											e.motionY = (double)((float)worldObj.rand.nextGaussian() * f3 + 0.2F);
											e.motionZ = (double)((float)worldObj.rand.nextGaussian() * f3);
											worldObj.spawnEntityInWorld(e);
										}
									}
								}
							}


						}
						
					}
				}
			}
			
			

		}
		
	}

	
}
