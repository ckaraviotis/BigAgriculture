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
import solipsists.bigagriculture.multiblock.Multiblock.TYPE;

public class BlockIlluminator extends BlockMultiblock {

    public static final TYPE type = TYPE.ILLUMINATOR;

    public BlockIlluminator() {
        super(Material.ROCK);

        setUnlocalizedName(BigAgriculture.MODID + ".illuminator");
        setRegistryName("illuminator");

        // Register
        GameRegistry.register(this);
        GameRegistry.register(new ItemBlock(this), getRegistryName());
    }

    @SideOnly(Side.CLIENT)
    public void initModel() {
        ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
    }

    @Override
    public TYPE getType() {
        return type;
    }

}
