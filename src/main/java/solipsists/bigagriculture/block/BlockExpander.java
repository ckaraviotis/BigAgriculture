package solipsists.bigagriculture.block;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.tileentity.TileController;
import solipsists.bigagriculture.tileentity.TileExpander;

public class BlockExpander extends BlockMultiblock implements ITileEntityProvider {

	public BlockExpander() {
		super(Material.ROCK);
		this.setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
				
		setUnlocalizedName(BigAgriculture.MODID + ".expander");
		setRegistryName("expander");			
		
		// Register
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TileExpander.class, BigAgriculture.MODID + "_expander");
	}
	
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileExpander();
	}
	
	private TileExpander getTE(IBlockAccess world, BlockPos pos) {
		return (TileExpander) world.getTileEntity(pos);
	}
	
}
