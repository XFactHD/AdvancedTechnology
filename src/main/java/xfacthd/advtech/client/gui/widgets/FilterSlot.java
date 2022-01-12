package xfacthd.advtech.client.gui.widgets;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.client.gui.ScreenInventoryMachine;

import java.util.function.Function;

public class FilterSlot extends Button
{
    private final ScreenInventoryMachine<?> screen;
    private final int idx;
    private final Function<Integer, ItemStack> filterGetter;

    public FilterSlot(ScreenInventoryMachine<?> screen, int pX, int pY, int idx, Function<Integer, ItemStack> filterGetter)
    {
        super(pX, pY, 18, 18, new TextComponent(""), btn -> {});
        this.screen = screen;
        this.idx = idx;
        this.filterGetter = filterGetter;
    }

    @Override
    public void renderButton(PoseStack poseStack, int mouseX, int mouseY, float partialTicks)
    {
        if (!filterGetter.apply(idx).isEmpty())
        {
            Minecraft.getInstance().getItemRenderer().renderAndDecorateItem(filterGetter.apply(idx), x + 1, y + 1);
        }

        if (isHovered)
        {
            AbstractContainerScreen.renderSlotHighlight(poseStack, x + 1, y + 1, getBlitOffset());
        }
    }

    @Override
    public boolean mouseClicked(double pMouseX, double pMouseY, int pButton)
    {
        if (active && visible)
        {
            if (clicked(pMouseX, pMouseY))
            {
                screen.handleFilterSlotClicked(this, idx, pButton);
                return true;
            }
        }
        return false;
    }

    public ItemStack getFilter() { return filterGetter.apply(idx); }
}