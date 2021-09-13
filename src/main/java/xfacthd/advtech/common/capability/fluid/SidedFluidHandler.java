package xfacthd.advtech.common.capability.fluid;

import net.minecraft.core.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xfacthd.advtech.common.util.interfaces.ITileFluidHandler;

public record SidedFluidHandler(ITileFluidHandler tile,
                                IFluidHandler internalHandler,
                                Direction side) implements IFluidHandler
{

    @Override
    public int getTanks() { return internalHandler.getTanks(); }

    @Override
    public FluidStack getFluidInTank(int tank) { return internalHandler.getFluidInTank(tank); }

    @Override
    public int getTankCapacity(int tank) { return internalHandler.getTankCapacity(tank); }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack) { return internalHandler.isFluidValid(tank, stack); }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (!tile.canFill(side)) { return 0; }
        return internalHandler.fill(resource, action);
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        if (!tile.canDrain(side)) { return FluidStack.EMPTY; }
        return internalHandler.drain(resource, action);
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        if (!tile.canDrain(side)) { return FluidStack.EMPTY; }
        return internalHandler.drain(maxDrain, action);
    }
}