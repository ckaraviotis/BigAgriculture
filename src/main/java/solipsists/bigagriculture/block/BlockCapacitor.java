package solipsists.bigagriculture.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.tileentity.TileCapacitor;
import solipsists.bigagriculture.tileentity.TileEnergyGeneric;
import solipsists.bigagriculture.tileentity.TileGenerator;

public class BlockCapacitor extends Block implements ITileEntityProvider {

	public BlockCapacitor() {
		super(Material.ROCK);
				
		setUnlocalizedName(BigAgriculture.MODID + ".capacitor");
		setRegistryName("capacitor");			
		
		// Register
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TileCapacitor.class, BigAgriculture.MODID + "_capacitor");
		//GameRegistry.registerTileEntity(TileEnergyGeneric.class, BigAgriculture.MODID + "_energyGeneric");
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		// Bail on server
		if (worldIn.isRemote) {
			return true;
		}
		
		TileEntity te = this.getTE(worldIn, pos);
		if (te != null) {
			((TileCapacitor) te).getStatus();
		}
		
		//playerIn.openGui(BigAgriculture.instance, GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
		
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileCapacitor();
	}
	
	private TileCapacitor getTE(IBlockAccess world, BlockPos pos) {
		return (TileCapacitor) world.getTileEntity(pos);
	}
	
}
