package solipsists.bigagriculture.block;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import solipsists.bigagriculture.BigAgriculture;

public class BlockGenericBA extends Block {
	
	public BlockGenericBA(Material material) {
		super(material);
		
		this.setCreativeTab(BigAgriculture.tabBigAgriculture);
	}

}
