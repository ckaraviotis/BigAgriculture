package solipsists.bigagriculture.item;

import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.BigAgriculture;

public class ItemGenericBA extends Item {

	public ItemGenericBA(String name) {
		super();
		
		this.setUnlocalizedName(name);
		this.setRegistryName(name);
		this.setCreativeTab(BigAgriculture.tabBigAgriculture);
		GameRegistry.register(this);
	}
	
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(this, 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}
	
}
