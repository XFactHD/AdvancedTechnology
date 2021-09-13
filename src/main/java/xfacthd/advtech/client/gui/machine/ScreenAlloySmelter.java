package xfacthd.advtech.client.gui.machine;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.client.gui.ScreenProducer;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.menu.machine.ContainerMenuAlloySmelter;

public class ScreenAlloySmelter extends ScreenProducer<ContainerMenuAlloySmelter>
{
    public static final ResourceLocation BACKGROUND = background("alloy_smelter");
    private static final ResourceLocation WORK_ICON = widget("furnace_work");

    public ScreenAlloySmelter(ContainerMenuAlloySmelter container, Inventory inventory, Component title)
    {
        super(container, inventory, title, 176, 187);
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

        drawProgressBar(leftPos + 77, topPos + 45, menu.getProgress());

        drawWorkingIcon(leftPos + 53, topPos + 66, WORK_ICON);

        drawEnergyBar(leftPos + 10, topPos + 24);

        drawSlotOverlay(leftPos +  31, topPos + 43, Slot.IN_MAIN);
        drawSlotOverlay(leftPos +  51, topPos + 43, Slot.IN_SECOND);
        drawSlotOverlay(leftPos + 105, topPos + 43, Slot.OUT_MAIN);
        drawSlotOverlay(leftPos + 105, topPos + 63, Slot.OUT_SECOND);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);

        drawEnergyTooltip(poseStack, mouseX, mouseY, leftPos + 10, topPos + 24);
        drawProgressTooltip(poseStack, mouseX, mouseY, leftPos + 77, topPos + 45);
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}