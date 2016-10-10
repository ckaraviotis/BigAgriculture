package solipsists.bigagriculture;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.VertexBuffer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.event.RenderWorldLastEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class RenderWorldLastEventHandler {

	public static void tick(RenderWorldLastEvent event) {
		renderHighlightedBlock(event);
	}

	private static void renderHighlightedBlock(RenderWorldLastEvent event) {
		BlockPos c = BigAgriculture.instance.clientInfo.getHighlighted();
		if (c == null) {
			return;
		}
		Minecraft mc = Minecraft.getMinecraft();
		long time = System.currentTimeMillis();

		if (time > BigAgriculture.instance.clientInfo.getExpire()) {
			BigAgriculture.instance.clientInfo.highlightBlock(null, -1);
			return;
		}

		if (((time / 500) & 1) == 0) {
			return;
		}

		EntityPlayerSP p = mc.thePlayer;
		double doubleX = p.lastTickPosX + (p.posX - p.lastTickPosX) * event.getPartialTicks();
		double doubleY = p.lastTickPosY + (p.posY - p.lastTickPosY) * event.getPartialTicks();
		double doubleZ = p.lastTickPosZ + (p.posZ - p.lastTickPosZ) * event.getPartialTicks();

		GlStateManager.pushMatrix();
		GlStateManager.color(1.0f, 0, 0);
		GlStateManager.glLineWidth(3);
		GlStateManager.translate(-doubleX, -doubleY, -doubleZ);

		GlStateManager.disableDepth();
		GlStateManager.disableTexture2D();

		Tessellator tessellator = Tessellator.getInstance();
		VertexBuffer buffer = tessellator.getBuffer();
		float mx = c.getX();
		float my = c.getY();
		float mz = c.getZ();
		buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION_COLOR);
		solipsists.bigagriculture.util.RenderHelper.renderHighLightedBlocksOutline(buffer, mx, my, mz, 1.0f, 0.0f, 0.0f, 1.0f);

		tessellator.draw();

		GlStateManager.enableTexture2D();
		GlStateManager.popMatrix();
	}

}
