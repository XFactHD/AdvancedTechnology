package xfacthd.advtech.client.gui.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.client.gui.ScreenProducer;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.container.machine.ContainerElectricFurnace;

public class ScreenElectricFurnace extends ScreenProducer<ContainerElectricFurnace>
{
    private static final ResourceLocation BACKGROUND = background("electric_furnace");
    private static final ResourceLocation WORK_ICON = widget("furnace_work");

    public ScreenElectricFurnace(ContainerElectricFurnace container, PlayerInventory inventory, ITextComponent title)
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
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        drawProgressBar(guiLeft + 77, guiTop + 45, container.getProgress());

        drawWorkingIcon(guiLeft + 53, guiTop + 66, WORK_ICON);

        drawEnergyBar(guiLeft + 10, guiTop + 24);

        drawSlotOverlay(guiLeft +  51, guiTop + 43, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 105, guiTop + 43, Slot.OUT_MAIN);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        super.renderHoveredToolTip(mouseX, mouseY);

        drawEnergyTooltip(mouseX, mouseY, guiLeft + 10, guiTop + 24);
        drawProgressTooltip(mouseX, mouseY, guiLeft + 77, guiTop + 45);
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}