package solipsists.bigagriculture.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
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
import solipsists.bigagriculture.tileentity.TileController;

public class BlockController extends Block implements ITileEntityProvider {

	public BlockController() {
		super(Material.ROCK);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
				
		setUnlocalizedName(BigAgriculture.MODID + ".controller");
		setRegistryName("controller");			
		
		// Register
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TileController.class, BigAgriculture.MODID + "_controller");
	}
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileController t = this.getTE(worldIn, pos);
		boolean b = t.changeItem(heldItem, playerIn);
		return true;
	}
		
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileController();
	}
	
	private TileController getTE(IBlockAccess world, BlockPos pos) {
		return (TileController) world.getTileEntity(pos);
	}
}
