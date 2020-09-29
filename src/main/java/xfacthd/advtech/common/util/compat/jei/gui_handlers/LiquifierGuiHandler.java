package xfacthd.advtech.common.util.compat.jei.gui_handlers;

import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.client.gui.machine.ScreenLiquifier;

public class LiquifierGuiHandler extends AdvancedGuiHandler<ScreenLiquifier>
{
    @Override
    public Object getIngredientUnderMouse(ScreenLiquifier screen, double mouseX, double mouseY) //FIXME: not called
    {
        if (mouseX >= 98 && mouseX < 114 && mouseY >= 1 && mouseY < 61)
        {
            FluidStack fluid = screen.getContainer().getFluidStored();
            return fluid.isEmpty() ? null : fluid;
        }
        return null;
    }
}