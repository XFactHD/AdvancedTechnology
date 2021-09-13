package xfacthd.advtech.client.gui.generator;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import xfacthd.advtech.client.gui.ScreenGenerator;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.menu.generator.ContainerMenuBurnerGenerator;

public class ScreenBurnerGenerator extends ScreenGenerator<ContainerMenuBurnerGenerator>
{
    private static final ResourceLocation BACKGROUND = background("burner_generator");
    private static final ResourceLocation WORK_ICON = widget("generator_work");

    public ScreenBurnerGenerator(ContainerMenuBurnerGenerator container, Inventory inventory, Component title)
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

        drawWorkingIcon(leftPos + 80, topPos + 66, WORK_ICON);

        drawEnergyBar(leftPos + 10, topPos + 24);

        drawSlotOverlay(leftPos + 78, topPos + 43, ScreenInventoryMachine.Slot.IN_MAIN);

        if (menu.hasFluidHandler())
        {
            //TODO: draw tanks with input/output overlays
        }
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