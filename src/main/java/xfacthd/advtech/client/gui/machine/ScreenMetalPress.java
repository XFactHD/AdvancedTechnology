package xfacthd.advtech.client.gui.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.client.gui.ScreenProducer;
import xfacthd.advtech.client.gui.tabs.TabMachinePort;
import xfacthd.advtech.client.gui.tabs.TabMachineUpgrades;
import xfacthd.advtech.common.container.machine.ContainerMetalPress;

public class ScreenMetalPress extends ScreenProducer<ContainerMetalPress>
{
    public static final ResourceLocation BACKGROUND = background("metal_press");
    private static final ResourceLocation WORK_ICON = widget("metal_press_work");

    public ScreenMetalPress(ContainerMetalPress container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, inventory, title, 176, 187);
    }

    @Override
    protected void gatherTabs()
    {
        addTab(new TabMachineUpgrades(this));
        addTab(new TabMachinePort(this));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        drawProgressBar(guiLeft + 77, guiTop + 45, container.getProgress());

        drawWorkingIcon(guiLeft + 54, guiTop + 66, WORK_ICON);

        drawEnergyBar(guiLeft + 10, guiTop + 24);

        drawSlotOverlay(guiLeft +  31, guiTop + 43, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft +  51, guiTop + 43, Slot.IN_SECOND);
        drawSlotOverlay(guiLeft + 105, guiTop + 43, Slot.OUT_SECOND);
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