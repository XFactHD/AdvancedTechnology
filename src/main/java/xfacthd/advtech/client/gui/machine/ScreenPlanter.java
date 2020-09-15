package xfacthd.advtech.client.gui.machine;

import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.gui.tabs.TabMachinePort;
import xfacthd.advtech.client.gui.tabs.TabMachineUpgrades;
import xfacthd.advtech.common.container.machine.ContainerPlanter;

public class ScreenPlanter extends ScreenInventoryMachine<ContainerPlanter>
{
    private static final ResourceLocation BACKGROUND = background("planter");
    public static final ITextComponent SET_FILTER = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".set_filter");
    public static final ITextComponent CLEAR_FILTER = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".clear_filter");

    public ScreenPlanter(ContainerPlanter container, PlayerInventory inventory, ITextComponent title)
    {
        super(container, inventory, title, 176, 187);
    }

    @Override
    public void init(Minecraft mc, int width, int height)
    {
        super.init(mc, width, height);

        addButton(new Button(guiLeft + 120, guiTop + 31, 50, 20, SET_FILTER.getFormattedText(), btn -> container.configureFilter(false)));
        addButton(new Button(guiLeft + 120, guiTop + 57, 50, 20, CLEAR_FILTER.getFormattedText(), btn -> container.configureFilter(true)));
        addButton(new Button(guiLeft +  58, guiTop + 84, 60, 20, SHOW_AREA.getFormattedText(), btn -> container.switchShowArea()));
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

        drawEnergyBar(guiLeft + 10, guiTop + 24);

        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 0.25F); //FIXME: item is not half transparent because renderItemAndEffectIntoGUI explicitly sets the color to white
        for (int i = 0; i < 9; i++)
        {
            net.minecraft.inventory.container.Slot slot = container.getSlot(i + 40);
            if (!slot.getHasStack())
            {
                ItemStack filter = container.getFilterStack(i);
                if (!filter.isEmpty())
                {
                    itemRenderer.renderItemAndEffectIntoGUI(filter, guiLeft + slot.xPos, guiTop + slot.yPos);
                }
            }
        }
        RenderSystem.color4f(1.0F, 1.0F, 1.0F, 1.0F);

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