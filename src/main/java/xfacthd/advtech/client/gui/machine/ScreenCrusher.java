package xfacthd.advtech.client.gui.machine;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.client.gui.ScreenProducer;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.menu.machine.ContainerMenuCrusher;

public class ScreenCrusher extends ScreenProducer<ContainerMenuCrusher>
{
    public static final ResourceLocation BACKGROUND = background("crusher");
    private static final ResourceLocation WORK_ICON = widget("crusher_work");

    public ScreenCrusher(ContainerMenuCrusher menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 176, 187);
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

        drawWorkingIcon(leftPos + 54, topPos + 66, WORK_ICON);

        drawEnergyBar(leftPos + 10, topPos + 24);

        drawSlotOverlay(leftPos +  51, topPos + 43, Slot.IN_MAIN);
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