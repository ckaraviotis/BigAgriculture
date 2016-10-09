package solipsists.bigagriculture.block;

import java.util.Random;

import net.minecraft.block.BlockFarmland;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.EnumPlantType;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.tileentity.TileController;

public class BlockIrrigatedFarmland extends BlockFarmland {
	

    public BlockIrrigatedFarmland()
    {
        super();
        this.setDefaultState(this.blockState.getBaseState().withProperty(MOISTURE, Integer.valueOf(7)));
        this.setTickRandomly(false);
        this.setLightOpacity(255);       
        
		setUnlocalizedName(BigAgriculture.MODID + ".irrigatedFarmland");
		setRegistryName("irrigatedFarmland");			
		
		// Register
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
    }
    
    @Override
	public boolean canSustainPlant(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing direction, IPlantable plantable) {
		EnumPlantType plantType = plantable.getPlantType(world, pos);
		if (plantType != EnumPlantType.Crop && plantType != EnumPlantType.Plains) {
			return false;
		}

		return true;
    }
    
		
	@SideOnly(Side.CLIENT)
	public void initModel() {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(this), 0, new ModelResourceLocation(getRegistryName(), "inventory"));
	}

    
	@Override
    public void updateTick(World worldIn, BlockPos pos, IBlockState state, Random rand)
    {		
        int i = ((Integer)state.getValue(MOISTURE)).intValue();
        
        // TODO: Revert to dirt if no controller nearby
        //worldIn.setBlockState(pos, Blocks.DIRT.getDefaultState());

        if (i < 7)
        {
            worldIn.setBlockState(pos, state.withProperty(MOISTURE, Integer.valueOf(7)), 2);
        }
    }

}
