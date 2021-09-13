package xfacthd.advtech.client.gui.machine;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.menu.machine.ContainerMenuCharger;

public class ScreenCharger extends ScreenInventoryMachine<ContainerMenuCharger>
{
    private static final ResourceLocation BACKGROUND = background("charger");
    private static final ResourceLocation WORK_ICON = widget("charger_work");

    public ScreenCharger(ContainerMenuCharger container, Inventory inventory, Component title)
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

        int stored = menu.getStoredEnergy();
        int capacity = menu.getEnergyCapacity();
        float energy = (float) stored / (float) capacity;
        drawWorkingIcon(leftPos + 53, topPos + 66, energy, WORK_ICON);

        drawEnergyBar(leftPos + 10, topPos + 24);

        drawSlotOverlay(leftPos +  51, topPos + 43, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 105, topPos + 43, Slot.OUT_MAIN);
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