package xfacthd.advtech.common.util.compat.jei.gui_handlers;

import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.gui.machine.ScreenLiquifier;

public class LiquifierGuiHandler extends AdvancedGuiHandler<ScreenLiquifier>
{
    @Override
    public Object getIngredientUnderMouse(ScreenLiquifier screen, double mouseX, double mouseY)
    {
        int left = screen.getGuiLeft();
        int top = screen.getGuiTop();
        if (mouseX >= left + 98 && mouseX < left + 114 && mouseY >= top + 1 && mouseY < top + 61)
        {
            FluidStack fluid = screen.getMenu().getFluidStored();
            return fluid.isEmpty() ? null : fluid;
        }
        return null;
    }
}