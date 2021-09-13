package xfacthd.advtech.common.capability.fluid;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import xfacthd.advtech.common.blockentity.storage.BlockEntityFluidTank;

public class TankFluidHandler extends FluidTank
{
    private final BlockEntityFluidTank tile;
    private int maxTransfer;

    public TankFluidHandler(BlockEntityFluidTank tile, int capacity, int maxTransfer)
    {
        super(capacity);
        this.tile = tile;
        this.maxTransfer = maxTransfer;
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        if (resource.isEmpty() || !isFluidValid(resource)) { return 0; }

        if (action.simulate())
        {
            if (fluid.isEmpty())
            {
                return Math.min(maxTransfer, Math.min(capacity, resource.getAmount()));
            }

            if (!fluid.isFluidEqual(resource)) { return 0; }

            return Math.min(maxTransfer, Math.min(capacity - fluid.getAmount(), resource.getAmount()));
        }

        if (fluid.isEmpty())
        {
            int toTransfer = Math.min(maxTransfer, Math.min(capacity, resource.getAmount()));
            fluid = new FluidStack(resource, toTransfer);
            onContentsChanged();
            return toTransfer;
        }

        if (!fluid.isFluidEqual(resource)) { return 0; }

        int toTransfer = Math.min(maxTransfer, Math.min(capacity - fluid.getAmount(), resource.getAmount()));
        if (toTransfer > 0)
        {
            fluid.grow(toTransfer);
            onContentsChanged();
        }
        return toTransfer;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        int drained = maxDrain;
        if (maxTransfer < drained) { drained = maxTransfer; }
        if (fluid.getAmount() < drained) { drained = fluid.getAmount(); }

        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0)
        {
            fluid.shrink(drained);
            onContentsChanged();
        }
        return stack;
    }

    @Override
    protected void onContentsChanged() { tile.onContentsChanged(); }

    public void reconfigure(int capacity, int maxTransfer)
    {
        setCapacity(capacity);
        this.maxTransfer = maxTransfer;
    }

    @Override
    public CompoundTag writeToNBT(CompoundTag nbt)
    {
        nbt.putInt("capacity", capacity);
        return super.writeToNBT(nbt);
    }

    @Override
    public FluidTank readFromNBT(CompoundTag nbt)
    {
        capacity = nbt.getInt("capacity");
        return super.readFromNBT(nbt);
    }
}