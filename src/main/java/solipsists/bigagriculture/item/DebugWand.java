package solipsists.bigagriculture.item;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.tileentity.TileController;

public class DebugWand extends ItemGenericBA {

	public DebugWand() {
		super("debug_wand");
		setMaxStackSize(1);
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack stack, World world, EntityPlayer player, EnumHand hand) {
		// Change modes here
		return super.onItemRightClick(stack, world, player, hand);
	}
	
	@Override
	public EnumActionResult onItemUse(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ) {
		
		if (!world.isRemote) {
			if (player.isSneaking()) {
				TileEntity te = world.getTileEntity(pos);
				if (te != null && te instanceof TileController) {
					((TileController)te).highlightMultiblock();
				}
			}
		}
		return EnumActionResult.SUCCESS;
	}
}
