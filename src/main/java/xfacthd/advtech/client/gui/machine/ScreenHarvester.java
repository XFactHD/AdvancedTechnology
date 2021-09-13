package xfacthd.advtech.client.gui.machine;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.menu.machine.ContainerMenuHarvester;

public class ScreenHarvester extends ScreenInventoryMachine<ContainerMenuHarvester>
{
    private static final ResourceLocation BACKGROUND = background("harvester");

    public ScreenHarvester(ContainerMenuHarvester container, Inventory inventory, Component title)
    {
        super(container, inventory, title, 176, 187);
    }

    @Override
    public void init()
    {
        super.init();

        addRenderableWidget(new Button(leftPos + 58, topPos + 84, 60, 20, SHOW_AREA, btn -> menu.switchShowArea()));
    }

    @Override
    protected void gatherTabs()
    {
        addTab(new TabMachineUpgrades(this));
        addTab(new TabRedstoneSettings(this));
        addTab(new TabMachinePort(this));
    }

    @Override
    protected void renderBg(PoseStack poseStack, float partialTicks, int mouseX, int mouseY)
    {
        super.renderBg(poseStack, partialTicks, mouseX, mouseY);

        drawEnergyBar(leftPos + 10, topPos + 24);

        drawSlotOverlay(leftPos + 58, topPos + 24, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 78, topPos + 24, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 98, topPos + 24, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 58, topPos + 44, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 78, topPos + 44, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 98, topPos + 44, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 58, topPos + 64, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 78, topPos + 64, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 98, topPos + 64, Slot.OUT_MAIN);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);

        drawEnergyTooltip(poseStack, mouseX, mouseY, leftPos + 10, topPos + 24);
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}