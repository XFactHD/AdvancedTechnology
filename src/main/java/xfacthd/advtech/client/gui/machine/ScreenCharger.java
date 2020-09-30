package xfacthd.advtech.client.gui.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.gui.tabs.TabMachinePort;
import xfacthd.advtech.client.gui.tabs.TabMachineUpgrades;
import xfacthd.advtech.common.container.machine.ContainerCharger;

public class ScreenCharger extends ScreenInventoryMachine<ContainerCharger>
{
    private static final ResourceLocation BACKGROUND = background("charger");
    private static final ResourceLocation WORK_ICON = widget("charger_work");

    public ScreenCharger(ContainerCharger container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, inventory, title, 176, 187);
    }

    @Override
    protected void gatherTabs()
    {
        addTab(new TabMachineUpgrades(this));
        //addTab(new TabRedstoneSettings(this));
        addTab(new TabMachinePort(this));
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        int stored = container.getStoredEnergy();
        int capacity = container.getEnergyCapacity();
        float energy = (float) stored / (float) capacity;
        drawWorkingIcon(guiLeft + 53, guiTop + 66, energy, WORK_ICON);

        drawEnergyBar(guiLeft + 10, guiTop + 24);

        drawSlotOverlay(guiLeft +  51, guiTop + 43, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 105, guiTop + 43, Slot.OUT_MAIN);
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        super.renderHoveredToolTip(mouseX, mouseY);

        drawEnergyTooltip(mouseX, mouseY, guiLeft + 10, guiTop + 24);
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}