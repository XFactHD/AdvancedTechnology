package xfacthd.advtech.client.gui.machine;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.gui.tabs.*;
import xfacthd.advtech.client.util.ItemRenderHelper;
import xfacthd.advtech.common.menu.machine.ContainerMenuPlanter;

public class ScreenPlanter extends ScreenInventoryMachine<ContainerMenuPlanter>
{
    private static final ResourceLocation BACKGROUND = background("planter");
    public static final Component SET_FILTER = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".set_filter");
    public static final Component CLEAR_FILTER = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".clear_filter");

    public ScreenPlanter(ContainerMenuPlanter container, Inventory inventory, Component title)
    {
        super(container, inventory, title, 176, 187);
    }

    @Override
    public void init()
    {
        super.init();

        addRenderableWidget(new Button(leftPos + 120, topPos + 31, 50, 20, SET_FILTER, btn -> menu.configureFilter(false)));
        addRenderableWidget(new Button(leftPos + 120, topPos + 57, 50, 20, CLEAR_FILTER, btn -> menu.configureFilter(true)));
        addRenderableWidget(new Button(leftPos +  58, topPos + 84, 60, 20, SHOW_AREA, btn -> menu.switchShowArea()));
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

        drawEnergyBar(leftPos + 10, topPos + 24);

        for (int i = 0; i < 9; i++)
        {
            net.minecraft.world.inventory.Slot slot = menu.getSlot(i + 40);
            if (!slot.hasItem())
            {
                ItemStack filter = menu.getFilterStack(i);
                if (!filter.isEmpty())
                {
                    //TODO: switch back to alpha variant when the item can actually render half transparent
                    ItemRenderHelper.renderFakeItemTransparent(filter, leftPos + slot.x, topPos + slot.y, .25F);
                    //ItemRenderHelper.renderFakeItem(filter, leftPos + slot.x, topPos + slot.y, .75F, .75F, .75F, 1F);
                }
            }
        }
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

        drawSlotOverlay(leftPos + 58, topPos + 24, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 78, topPos + 24, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 98, topPos + 24, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 58, topPos + 44, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 78, topPos + 44, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 98, topPos + 44, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 58, topPos + 64, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 78, topPos + 64, Slot.IN_MAIN);
        drawSlotOverlay(leftPos + 98, topPos + 64, Slot.IN_MAIN);
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