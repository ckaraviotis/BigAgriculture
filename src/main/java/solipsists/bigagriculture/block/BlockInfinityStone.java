package solipsists.bigagriculture.block;

import net.minecraft.block.material.Material;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.multiblock.Multiblock;
import solipsists.bigagriculture.multiblock.Multiblock.TYPE;
import solipsists.bigagriculture.tileentity.TileFertilizer;

/***
 * Provides infinite plantable to slot0
 */
public class BlockInfinityStone extends BlockMultiblock {
	
	public static final Multiblock.TYPE type = TYPE.INFINITY_STONE;

	public BlockInfinityStone() {
		super(Material.ROCK);
		
		setUnlocalizedName(BigAgriculture.MODID + ".infinity_stone");
		setRegistryName("infinity_stone");			
		
		// Register
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
	@Override
	public Multiblock.TYPE getType() {
		return type;
	}

}
