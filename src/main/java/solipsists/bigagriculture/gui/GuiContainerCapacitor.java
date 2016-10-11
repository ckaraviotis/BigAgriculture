package solipsists.bigagriculture.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.inventory.ContainerCapacitor;
import solipsists.bigagriculture.tileentity.TileCapacitor;
import solipsists.bigagriculture.tileentity.TileEnergyGeneric;

public class GuiContainerCapacitor extends GuiContainer {

    public static final int WIDTH = 179;
    public static final int HEIGHT = 151;

    private static final ResourceLocation background = new ResourceLocation(BigAgriculture.MODID, "textures/gui/guiCapacitor.png");
    private TileCapacitor tc;

    public GuiContainerCapacitor(TileCapacitor tile, ContainerCapacitor container) {
        super(container);
        tc = tile;

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        tc.getUpdatePacket();

        final int BAR_WIDTH = 124;
        final int BAR_HEIGHT = 16;
        int n = ((TileEnergyGeneric) tc).getEnergyStored();
        int z = tc.getEnergyStored();
        int m = tc.getMaxEnergyStored();
        double p = ((double) n / (double) m) * 100.0;
        double w = (double) BAR_WIDTH / (double) 100;
        int actualWidth = (int) (p * w);

        // Draw power bar
        drawTexturedModalRect(guiLeft + 31, guiTop + 22, 0, 152, actualWidth, 16);
    }


}
