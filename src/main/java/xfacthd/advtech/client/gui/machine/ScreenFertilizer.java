package xfacthd.advtech.client.gui.machine;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.common.container.machine.ContainerFertilizer;

public class ScreenFertilizer extends ScreenInventoryMachine<ContainerFertilizer>
{
    private static final ResourceLocation BACKGROUND = background("fertilizer");

    public ScreenFertilizer(ContainerFertilizer container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, inventory, title, 176, 187);
    }

    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);

        addButton(new Button(guiLeft + 58, guiTop + 84, 60, 20, SHOW_AREA.getFormattedText(), btn -> container.switchShowArea()));
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

        drawEnergyBar(guiLeft + 10, guiTop + 24);

        drawSlotOverlay(guiLeft + 58, guiTop + 24, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 78, guiTop + 24, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 98, guiTop + 24, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 58, guiTop + 44, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 78, guiTop + 44, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 98, guiTop + 44, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 58, guiTop + 64, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 78, guiTop + 64, Slot.IN_MAIN);
        drawSlotOverlay(guiLeft + 98, guiTop + 64, Slot.IN_MAIN);
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