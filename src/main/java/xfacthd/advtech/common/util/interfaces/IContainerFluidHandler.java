package xfacthd.advtech.common.util.interfaces;

import net.minecraftforge.fluids.FluidStack;

public interface IContainerFluidHandler
{
    void handleFluidUpdate(int tank, FluidStack fluid);

    void handleFluidAmountUpdate(int tank, int amount);
}