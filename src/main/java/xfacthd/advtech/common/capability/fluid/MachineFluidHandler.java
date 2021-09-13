package xfacthd.advtech.common.capability.fluid;

import com.google.common.base.Preconditions;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraftforge.common.util.Constants;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

import java.util.function.BiPredicate;

public class MachineFluidHandler implements IFluidHandler, INBTSerializable<CompoundTag>
{
    private final int tanks;
    private int capacity;
    private final BiPredicate<FluidStack, Integer> filter;
    private final int defaultDrain;
    private final NonNullList<FluidStack> contents;

    public MachineFluidHandler(int tanks, int capacity, BiPredicate<FluidStack, Integer> filter)
    {
        this(tanks, capacity, filter, 0);
    }

    public MachineFluidHandler(int tanks, int capacity, BiPredicate<FluidStack, Integer> filter, int defaultDrain)
    {
        this.tanks = tanks;
        this.capacity = capacity;
        this.filter = filter;
        this.defaultDrain = defaultDrain;
        this.contents = NonNullList.withSize(tanks, FluidStack.EMPTY);
    }

    @Override
    public int getTanks() { return tanks; }

    @Override
    public FluidStack getFluidInTank(int tank)
    {
        Preconditions.checkArgument(tank < tanks, "Invalid tank index!");
        return contents.get(tank);
    }

    @Override
    public int getTankCapacity(int tank) { return capacity; }

    @Override
    public boolean isFluidValid(int tank, FluidStack stack)
    {
        Preconditions.checkArgument(tank < tanks, "Invalid tank index!");
        return stack.isEmpty() || filter.test(stack, tank);
    }

    @Override
    public int fill(FluidStack resource, FluidAction action)
    {
        int tank = findTank(resource);
        if (tank == -1 || resource.isEmpty() || !isFluidValid(tank, resource)) { return 0; }

        FluidStack fluid = contents.get(tank);
        if (action.simulate())
        {
            if (fluid.isEmpty())
            {
                return Math.min(capacity, resource.getAmount());
            }
            if (!fluid.isFluidEqual(resource))
            {
                return 0;
            }
            return Math.min(capacity - fluid.getAmount(), resource.getAmount());
        }

        if (fluid.isEmpty())
        {
            fluid = new FluidStack(resource, Math.min(capacity, resource.getAmount()));
            contents.set(tank, fluid);
            onContentsChanged();
            return fluid.getAmount();
        }

        if (!fluid.isFluidEqual(resource)) { return 0; }

        int filled = capacity - fluid.getAmount();
        if (resource.getAmount() < filled)
        {
            fluid.grow(resource.getAmount());
            filled = resource.getAmount();
        }
        else
        {
            fluid.setAmount(capacity);
        }

        if (filled > 0)
        {
            contents.set(tank, fluid);
            onContentsChanged();
        }
        return filled;
    }

    @Override
    public FluidStack drain(FluidStack resource, FluidAction action)
    {
        int tank = findTank(resource);
        if (tank == -1) { return FluidStack.EMPTY; }

        FluidStack fluid = contents.get(tank);
        int drained = resource.getAmount();
        if (fluid.getAmount() < drained)
        {
            drained = fluid.getAmount();
        }

        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0)
        {
            fluid.shrink(drained);
            contents.set(tank, fluid);
            onContentsChanged();
        }
        return stack;
    }

    @Override
    public FluidStack drain(int maxDrain, FluidAction action)
    {
        FluidStack fluid = contents.get(defaultDrain);
        int drained = maxDrain;
        if (fluid.getAmount() < drained)
        {
            drained = fluid.getAmount();
        }

        FluidStack stack = new FluidStack(fluid, drained);
        if (action.execute() && drained > 0)
        {
            fluid.shrink(drained);
            contents.set(defaultDrain, fluid);
            onContentsChanged();
        }
        return stack;
    }

    public void setCapacity(int capacity) { this.capacity = capacity; }

    private void onContentsChanged() { }

    private int findTank(FluidStack fluid)
    {
        for (int i = 0; i < tanks; i++)
        {
            if (filter.test(fluid, i)) { return i; }
        }
        return -1;
    }

    @Override
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();

        nbt.putInt("capacity", capacity);

        ListTag list = new ListTag();
        for (int i = 0; i < tanks; i++)
        {
            CompoundTag tag = new CompoundTag();
            contents.get(i).writeToNBT(tag);
            list.add(i, tag);
        }
        nbt.put("contents", list);

        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        capacity = nbt.getInt("capacity");

        ListTag list = nbt.getList("contents", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            CompoundTag tag = list.getCompound(i);
            contents.set(i, FluidStack.loadFluidStackFromNBT(tag));
        }
    }
}