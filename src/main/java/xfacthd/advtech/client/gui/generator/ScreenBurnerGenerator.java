package xfacthd.advtech.client.gui.generator;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import xfacthd.advtech.client.gui.ScreenGenerator;
import xfacthd.advtech.client.gui.tabs.TabMachinePort;
import xfacthd.advtech.client.gui.tabs.TabMachineUpgrades;
import xfacthd.advtech.common.container.generator.ContainerBurnerGenerator;

public class ScreenBurnerGenerator extends ScreenGenerator<ContainerBurnerGenerator>
{
    private static final ResourceLocation BACKGROUND = background("burner_generator");
    private static final ResourceLocation WORK_ICON = widget("generator_work");

    public ScreenBurnerGenerator(ContainerBurnerGenerator container, PlayerInventory inventory, ITextComponent title)
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

        drawWorkingIcon(guiLeft + 80, guiTop + 66, WORK_ICON);

        drawEnergyBar(guiLeft + 10, guiTop + 24);

        drawSlotOverlay(guiLeft + 78, guiTop + 43, Slot.IN_MAIN);

        if (container.hasFluidHandler())
        {
            //TODO: draw tanks with input/output overlays
        }
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