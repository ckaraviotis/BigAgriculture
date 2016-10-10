package solipsists.bigagriculture;

import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ClientInfo {

	private BlockPos highlightBlock;
	private long expireHighlight = 0;
	
	public void highlightBlock(BlockPos pos, long expire) {
		highlightBlock = pos;
		expireHighlight = expire;
	}
	
	public BlockPos getHighlighted() {
		return highlightBlock;
	}
	
	public long getExpire() {
		return expireHighlight;
	}
	
	@SideOnly(Side.CLIENT)
	public static World getWorld() {
		return Minecraft.getMinecraft().theWorld;
	}
}
