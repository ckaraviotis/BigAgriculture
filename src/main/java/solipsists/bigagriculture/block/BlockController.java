package solipsists.bigagriculture.block;

import org.apache.logging.log4j.Level;

import com.mojang.authlib.properties.Property;

import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.properties.PropertyInteger;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.multiblock.Multiblock;
import solipsists.bigagriculture.multiblock.Multiblock.TYPE;
import solipsists.bigagriculture.tileentity.TileController;

public class BlockController extends BlockMultiblock implements ITileEntityProvider {
	
	public static final int GUI_ID = 1;
	public static final PropertyDirection FACING = PropertyDirection.create("facing");
	public static final Multiblock.TYPE type = TYPE.CONTROLLER;

	public BlockController() {
		super(Material.ROCK);
		this.setLightLevel(7f);
				
		setUnlocalizedName(BigAgriculture.MODID + ".controller");
		setRegistryName("controller");			
		
		// Register
		GameRegistry.register(this);
		GameRegistry.register(new ItemBlock(this), getRegistryName());
		GameRegistry.registerTileEntity(TileController.class, BigAgriculture.MODID + "_controller");
	}
	
	@Override
	public void onBlockHarvested(World world, BlockPos pos, IBlockState state, EntityPlayer player)
    {
		// Destroy any Irrigated Farmland
		TileEntity te = world.getTileEntity(pos);
		
		if (te != null) {
			((TileController) te).saltTheEarth();
		}
    }
	
	@Override
	public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ) {
		// Bail on server
		if (worldIn.isRemote) {
			return true;
		}
		
		playerIn.openGui(BigAgriculture.instance, GUI_ID, worldIn, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}
	
   @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack) {
        //world.setBlockState(pos, state.withProperty(FACING, placer.getHorizontalFacing().getOpposite()), 2);
        world.setBlockState(pos, state.withProperty(FACING, getFacingFromEntity(pos, placer)), 2);
        
        TileEntity t = world.getTileEntity(pos);
    	if ((t instanceof TileController)) {
    		TileController tc = (TileController) t;	
    		
    		tc.setOwner((EntityPlayer)placer);
    	}				
        
	}
   
   @Override
   public IBlockState getStateFromMeta(int meta) {
       return getDefaultState().withProperty(FACING, EnumFacing.getFront((meta & 3)));
   }

   @Override
   public int getMetaFromState(IBlockState state) {
       return state.getValue(FACING).getIndex();
   }

   @Override
   protected BlockStateContainer createBlockState() {
       return new BlockStateContainer(this, FACING);
   }
   
   private static EnumFacing getFacingFromEntity(BlockPos pos, EntityLivingBase p)
   {
       if (MathHelper.abs((float)p.posX - (float)pos.getX()) < 2.0F && MathHelper.abs((float)p.posZ - (float)pos.getZ()) < 2.0F)
       {
           double d0 = p.posY + (double)p.getEyeHeight();

           if (d0 - (double)pos.getY() > 2.0D)
           {
               return EnumFacing.UP;
           }

           if ((double)pos.getY() - d0 > 0.0D)
           {
               return EnumFacing.DOWN;
           }
       }

       return p.getHorizontalFacing().getOpposite();
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
