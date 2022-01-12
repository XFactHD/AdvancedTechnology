package xfacthd.advtech.client.gui.utility;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;
import xfacthd.advtech.client.gui.tabs.TabMachinePort;
import xfacthd.advtech.client.gui.widgets.FilterSlot;
import xfacthd.advtech.common.menu.utility.ContainerMenuItemSplitter;

public class ScreenItemSplitter extends ScreenInventoryMachine<ContainerMenuItemSplitter>
{
    private static final ResourceLocation BACKGROUND = background("item_splitter");
    private static final int[] FILTER_X = new int[] { 41, 41, 41, 61, 79, 97, 117, 117, 117 };
    private static final int[] FILTER_Y = new int[] { 16, 34, 52, 72, 72, 72,  52,  34,  16 };

    private final FilterSlot[] filterSlots = new FilterSlot[ContainerMenuItemSplitter.FILTER_COUNT];

    public ScreenItemSplitter(ContainerMenuItemSplitter menu, Inventory inventory, Component title)
    {
        super(menu, inventory, title, 176, 187);
    }

    @Override
    protected void init()
    {
        super.init();

        for (int i = 0; i < ContainerMenuItemSplitter.FILTER_COUNT; i++)
        {
            filterSlots[i] = addRenderableWidget(new FilterSlot(this, leftPos + FILTER_X[i], topPos + FILTER_Y[i], i, menu::getFilter));
        }
    }

    @Override
    protected void gatherTabs()
    {
        addTab(new TabMachinePort(this));
    }

    @Override
    protected void renderLabels(PoseStack pPoseStack, int pMouseX, int pMouseY)
    {
        super.renderLabels(pPoseStack, pMouseX, pMouseY);

        for (FilterSlot slot : filterSlots)
        {
            if (slot.isHoveredOrFocused() && !slot.getFilter().isEmpty())
            {
                renderTooltip(pPoseStack, slot.getFilter(), pMouseX - leftPos, pMouseY - topPos);
            }
        }
    }

    @Override
    public void handleFilterSlotClicked(FilterSlot slot, int idx, int button)
    {
        ItemStack handStack = menu.getCarried(); //TODO: check if this is the correct stack
        if ((button == 0 && !handStack.isEmpty()) || (button == 1 && !slot.getFilter().isEmpty()))
        {
            menu.sendFilterConfig(idx, button == 0 ? handStack : ItemStack.EMPTY);
        }
    }

    @Override
    protected ResourceLocation getBackground() { return BACKGROUND; }
}