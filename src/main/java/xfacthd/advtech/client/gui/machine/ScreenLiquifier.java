package xfacthd.advtech.client.gui.machine;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.client.gui.ScreenProducer;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.menu.machine.ContainerMenuLiquifier;

public class ScreenLiquifier extends ScreenProducer<ContainerMenuLiquifier>
{
    private static final ResourceLocation BACKGROUND = background("liquifier");
    private static final ResourceLocation WORK_ICON = widget("liquifier_work");

    public ScreenLiquifier(ContainerMenuLiquifier container, Inventory inventory, Component title)
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
        drawWorkingIcon(leftPos + 54, topPos + 66, WORK_ICON);

        drawEnergyBar(leftPos + 10, topPos + 24);
        drawTankContents(leftPos + 150, topPos + 24, menu.getFluidStored(), menu.getFluidCapacity());

        drawSlotOverlay(leftPos +  51, topPos + 43, Slot.IN_MAIN);
        drawTankOverlay(leftPos + 148, topPos + 22, Slot.OUT_MAIN);
    }

    @Override
    protected void renderLabels(PoseStack poseStack, int mouseX, int mouseY)
    {
        super.renderLabels(poseStack, mouseX, mouseY);

        drawEnergyTooltip(poseStack, mouseX, mouseY, leftPos + 10, topPos + 24);
        drawTankTooltip(poseStack, mouseX, mouseY, leftPos + 150, topPos + 24, menu.getFluidStored(), menu.getFluidCapacity());
        drawProgressTooltip(poseStack, mouseX, mouseY, leftPos + 77, topPos + 45);
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}