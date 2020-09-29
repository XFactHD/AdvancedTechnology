package xfacthd.advtech.client.gui.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.client.gui.ScreenProducer;
import xfacthd.advtech.client.gui.tabs.TabMachinePort;
import xfacthd.advtech.client.gui.tabs.TabMachineUpgrades;
import xfacthd.advtech.common.container.machine.ContainerLiquifier;

public class ScreenLiquifier extends ScreenProducer<ContainerLiquifier>
{
    private static final ResourceLocation BACKGROUND = background("liquifier");
    private static final ResourceLocation WORK_ICON = widget("liquifier_work");

    public ScreenLiquifier(ContainerLiquifier container, PlayerInventory inventory, ITextComponent title)
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
        drawTankContents(guiLeft + 150, guiTop + 24, container.getFluidStored(), container.getFluidCapacity());

        drawSlotOverlay(guiLeft +  51, guiTop + 43, Slot.IN_MAIN);
        drawTankOverlay(guiLeft + 148, guiTop + 22, Slot.OUT_MAIN);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        super.renderHoveredToolTip(mouseX, mouseY);

        drawEnergyTooltip(mouseX, mouseY, guiLeft + 10, guiTop + 24);
        drawTankTooltip(mouseX, mouseY, guiLeft + 150, guiTop + 24, container.getFluidStored(), container.getFluidCapacity());
        drawProgressTooltip(mouseX, mouseY, guiLeft + 77, guiTop + 45);
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}