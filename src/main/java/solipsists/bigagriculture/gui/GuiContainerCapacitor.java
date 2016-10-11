package solipsists.bigagriculture.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import solipsists.bigagriculture.BigAgriculture;
import solipsists.bigagriculture.inventory.ContainerCapacitor;
import solipsists.bigagriculture.tileentity.TileCapacitor;
import solipsists.bigagriculture.tileentity.TileEnergyGeneric;

import java.util.ArrayList;
import java.util.List;

public class GuiContainerCapacitor extends GuiContainer {

    public static final int WIDTH = 179;
    public static final int HEIGHT = 151;

    private static final int POWER_LEFT = 31;
    private static final int POWER_TOP = 22;
    private static final int POWER_WIDTH = 124;
    private static final int POWER_HEIGHT = 16;
    private static final ResourceLocation background = new ResourceLocation(BigAgriculture.MODID, "textures/gui/guiCapacitor.png");
    private int energyStored;
    private int maxEnergyStored;
    private double percentageFull;
    private TileCapacitor tc;

    public GuiContainerCapacitor(TileCapacitor tile, ContainerCapacitor container) {
        super(container);
        tc = tile;

        xSize = WIDTH;
        ySize = HEIGHT;
    }

    public static boolean isInRect(int x, int y, int xSize, int ySize, int mouseX, int mouseY) {
        return ((mouseX >= x && mouseX <= x + xSize) && (mouseY >= y && mouseY <= y + ySize));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY) {
        mc.getTextureManager().bindTexture(background);
        GlStateManager.color(1f, 1f, 1f, 1f);
        drawTexturedModalRect(guiLeft, guiTop, 0, 0, xSize, ySize);

        tc.getUpdatePacket();

        energyStored = ((TileEnergyGeneric) tc).getEnergyStored();
        maxEnergyStored = tc.getMaxEnergyStored();
        percentageFull = ((double) energyStored / (double) maxEnergyStored) * 100.0;
        double w = (double) POWER_WIDTH / (double) 100;
        int actualWidth = (int) (percentageFull * w);

        // Draw power bar
        drawTexturedModalRect(guiLeft + POWER_LEFT, guiTop + POWER_TOP, 0, 152, actualWidth, POWER_HEIGHT);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY) {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        List<String> tooltip = new ArrayList<String>();

        if (isInRect(guiLeft + POWER_LEFT, guiTop + POWER_TOP, POWER_WIDTH, POWER_HEIGHT, mouseX, mouseY)) {
            tooltip.add("" + energyStored + " / " + maxEnergyStored + " NRG.");
            tooltip.add("" + Math.floor(percentageFull) + "% full.");
        }

        if (!tooltip.isEmpty()) {
            drawHoveringText(tooltip, mouseX - guiLeft, mouseY - guiTop, fontRendererObj);
        }
    }


}
