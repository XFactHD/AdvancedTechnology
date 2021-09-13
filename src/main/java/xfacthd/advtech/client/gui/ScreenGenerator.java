package xfacthd.advtech.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.common.menu.ContainerMenuGenerator;

public abstract class ScreenGenerator<C extends ContainerMenuGenerator<?>> extends ScreenInventoryMachine<C>
{
    protected ScreenGenerator(C container, Inventory inventory, Component title, int xSize, int ySize)
    {
        super(container, inventory, title, xSize, ySize);
    }

    protected void drawWorkingIcon(int x, int y, ResourceLocation texture)
    {
        float progress = menu.getProgress();
        drawWorkingIcon(x, y, 1F - progress, texture);
    }

    protected void drawProgressTooltip(PoseStack poseStack, int mouseX, int mouseY, int xMin, int yMin)
    {
        drawProgressTooltip(poseStack, mouseX, mouseY, xMin, yMin, 16, 16);
    }
}