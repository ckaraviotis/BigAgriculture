package solipsists.bigagriculture.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.util.ResourceLocation;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.inventory.ContainerController;
import solipsists.bigagriculture.tileentity.TileController;

public class GuiContainerController extends GuiContainer {

	public static final int WIDTH = 180;
	public static final int HEIGHT = 152;
	
	private static final ResourceLocation background = new ResourceLocation(BigAgriculture.MODID, "textures/gui/container.png");

	public GuiContainerController(TileController tile, ContainerController container) {
		super(container);
		
		xSize = WIDTH;
		ySize = HEIGHT;
	}

	@Override
	protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
		mc.getTextureManager().bindTexture(background);
		drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);
	}
	
	
}
