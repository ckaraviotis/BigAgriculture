package solipsists.bigagriculture.block;

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
import solipsists.bigagriculture.multiblock.Multiblock;
import solipsists.bigagriculture.multiblock.Multiblock.TYPE;
import solipsists.bigagriculture.tileentity.TileFertilizer;

public class BlockFertilizer extends BlockMultiblock implements ITileEntityProvider {
	
	public static final Multiblock.TYPE type = TYPE.FERTILIZER;
	public static final double CHANCE = 0.5;
	
	public BlockFertilizer() {
		super(Material.ROCK);
				
		setUnlocalizedName(BigAgriculture.MODID + ".fertilizer");
		setRegistryName("fertilizer");			
		
		// Register
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TileFertilizer.class, BigAgriculture.MODID + "_fertilizer");
	}
	
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public TileEntity createNewTileEntity(World worldIn, int meta) {
		return new TileFertilizer();
	}
	
	private TileFertilizer getTE(IBlockAccess world, BlockPos pos) {
		return (TileFertilizer) world.getTileEntity(pos);
	}
	
	@Override
	public Multiblock.TYPE getType() {
		return type;
	}
	
}
