package xfacthd.advtech.common.capability.fluid;

import net.minecraft.util.Direction;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import xfacthd.advtech.common.util.interfaces.ITileFluidHandler;

public class SidedFluidHandler implements IFluidHandler
{
    private final ITileFluidHandler tile;
    private final IFluidHandler internalHandler;
    private final Direction side;

    public SidedFluidHandler(ITileFluidHandler tile, IFluidHandler internalHandler, Direction side)
    {
        this.tile = tile;
        this.internalHandler = internalHandler;
        this.side = side;
    }

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