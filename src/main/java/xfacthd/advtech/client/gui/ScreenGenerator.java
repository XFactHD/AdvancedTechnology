package xfacthd.advtech.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.common.container.ContainerGenerator;

public abstract class ScreenGenerator<C extends ContainerGenerator<?, ?>> extends ScreenMachine<C>
{
    protected ScreenGenerator(C container, PlayerInventory inventory, ITextComponent title, int xSize, int ySize)
    {
        super(container, inventory, title, xSize, ySize);
    }

    protected void drawWorkingIcon(int x, int y, ResourceLocation texture)
    {
        float progress = container.getProgress();
        drawWorkingIcon(x, y, 1F - progress, texture);
    }

    protected void drawProgressTooltip(int mouseX, int mouseY, int xMin, int yMin)
    {
        drawProgressTooltip(mouseX, mouseY, xMin, yMin, 16, 16);
    }
}