package xfacthd.advtech.common.tileentity.debug;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityBase;

import java.util.EnumMap;
import java.util.Map;

public class TileEntityCreativeFluidSource extends TileEntityBase implements ITickableTileEntity
{
    private final InfiniteSource source = new InfiniteSource();
    private LazyOptional<IFluidHandler> lazySource = LazyOptional.empty();
    private final Map<Direction, LazyOptional<IFluidHandler>> neighbors = new EnumMap<>(Direction.class);

    public TileEntityCreativeFluidSource()
    {
        super(TileEntityTypes.tileTypeCreativeFluidSource);

        for (Direction dir : Direction.values()) { neighbors.put(dir, LazyOptional.empty()); }
    }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote() && !source.getFluidInTank(0).isEmpty())
        {
            FluidStack fluid = source.getFluidInTank(0);
            for (Direction dir : Direction.values())
            {
                if (world.isAirBlock(pos.offset(dir))) { continue; }

                LazyOptional<IFluidHandler> neighbor = neighbors.get(dir);
                if (!neighbor.isPresent())
                {
                    TileEntity te = world.getTileEntity(pos.offset(dir));
                    if (te != null && !(te instanceof TileEntityCreativeFluidSource))
                    {
                        neighbor = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());
                        if (neighbor.isPresent())
                        {
                            neighbor.addListener(storage -> invalidateNeighbor(dir));
                            neighbors.put(dir, neighbor);
                        }
                    }
                }

                neighbor.ifPresent(handler -> handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE));
            }
        }
    }

    private void invalidateNeighbor(Direction side) { neighbors.put(side, LazyOptional.empty()); }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return lazySource.cast();
        }
        return super.getCapability(cap, side);
    }

    public boolean handleContainerInteraction(ItemStack stack)
    {
        LazyOptional<IFluidHandlerItem> fluidHandler = stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY);
        if (!fluidHandler.isPresent()) { return false; }

        boolean success = fluidHandler.map(handler ->
        {
            FluidStack fluid = handler.getFluidInTank(0);
            if (source.getFluidInTank(0).isEmpty() && !fluid.isEmpty())
            {
                //noinspection ConstantConditions
                if (!world.isRemote()) { source.setFluid(fluid); }
                return true;
            }
            else if (!source.getFluidInTank(0).isEmpty() && fluid.isEmpty())
            {
                //noinspection ConstantConditions
                if (!world.isRemote()) { source.setFluid(FluidStack.EMPTY); }
                return true;
            }
            return false;
        }).orElse(false);

        if (success) { markFullUpdate(); }

        return true;
    }

    public FluidStack getContents() { return source.getFluidInTank(0); }

    @Override
    public void validate()
    {
        super.validate();

        lazySource = LazyOptional.of(() -> source);
    }

    @Override
    public void remove()
    {
        super.remove();

        lazySource.invalidate();
        lazySource = LazyOptional.empty();
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt) { nbt.put("fluid", source.serializeNBT()); }

    @Override
    public void readNetworkNBT(CompoundNBT nbt) { source.deserializeNBT(nbt.getCompound("fluid")); }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("fluid", source.serializeNBT());
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        source.deserializeNBT(nbt.getCompound("fluid"));
    }

    private static final class InfiniteSource implements IFluidHandler, INBTSerializable<CompoundNBT>
    {
        private FluidStack fluid = FluidStack.EMPTY;

        @Override
        public int getTanks() { return 1; }

        @Override
        public FluidStack getFluidInTank(int tank) { return fluid; }

        @Override
        public int getTankCapacity(int tank) { return Integer.MAX_VALUE; }

        @Override
        public boolean isFluidValid(int tank, FluidStack stack) { return false; }

        @Override
        public int fill(FluidStack resource, FluidAction action) { return 0; }

        @Override
        public FluidStack drain(FluidStack resource, FluidAction action) { return resource; }

        @Override
        public FluidStack drain(int maxDrain, FluidAction action)
        {
            FluidStack copy = fluid.copy();
            copy.setAmount(maxDrain);
            return copy;
        }

        public void setFluid(FluidStack fluid)
        {
            if (fluid.isEmpty()) { this.fluid = FluidStack.EMPTY; }
            else
            {
                this.fluid = fluid.copy();
                this.fluid.setAmount(Integer.MAX_VALUE);
            }
        }

        @Override
        public CompoundNBT serializeNBT()
        {
            CompoundNBT nbt = new CompoundNBT();
            fluid.writeToNBT(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundNBT nbt) { fluid = FluidStack.loadFluidStackFromNBT(nbt); }
    }
}