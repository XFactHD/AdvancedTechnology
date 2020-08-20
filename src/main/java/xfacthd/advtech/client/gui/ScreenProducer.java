package xfacthd.advtech.client.gui;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.common.container.ContainerProducer;

public abstract class ScreenProducer<C extends ContainerProducer<?, ?>> extends ScreenInventoryMachine<C>
{
    protected ScreenProducer(C container, PlayerInventory inventory, ITextComponent title, int xSize, int ySize)
    {
        super(container, inventory, title, xSize, ySize);
    }

    protected void drawWorkingIcon(int x, int y, ResourceLocation texture)
    {
        int stored = container.getStoredEnergy();
        int capacity = container.getEnergyCapacity();
        float energy = (float) stored / (float) capacity;

        drawWorkingIcon(x, y, energy, texture);
    }

    protected void drawProgressTooltip(int mouseX, int mouseY, int xMin, int yMin)
    {
        drawProgressTooltip(mouseX, mouseY, xMin, yMin, 22, 16);
    }
}