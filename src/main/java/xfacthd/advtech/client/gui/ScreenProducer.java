package xfacthd.advtech.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;
import xfacthd.advtech.common.menu.ContainerMenuMachine;

public abstract class ScreenProducer<M extends ContainerMenuInventoryMachine<?>> extends ScreenInventoryMachine<M>
{
    protected ScreenProducer(M menu, Inventory inventory, Component title, int xSize, int ySize)
    {
        super(menu, inventory, title, xSize, ySize);
    }

    protected void drawWorkingIcon(int x, int y, ResourceLocation texture)
    {
        int stored = menu.getStoredEnergy();
        int capacity = menu.getEnergyCapacity();
        float energy = (float) stored / (float) capacity;

        drawWorkingIcon(x, y, energy, texture);
    }

    protected void drawProgressTooltip(PoseStack poseStack, int mouseX, int mouseY, int xMin, int yMin)
    {
        drawProgressTooltip(poseStack, mouseX, mouseY, xMin, yMin, 22, 16);
    }
}