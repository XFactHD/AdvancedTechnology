package xfacthd.advtech.common.blockentity.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.*;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityBase;

import java.util.EnumMap;
import java.util.Map;

public class BlockEntityCreativeFluidSource extends BlockEntityBase
{
    private final InfiniteSource source = new InfiniteSource();
    private LazyOptional<IFluidHandler> lazySource = LazyOptional.empty();
    private final Map<Direction, LazyOptional<IFluidHandler>> neighbors = new EnumMap<>(Direction.class);

    public BlockEntityCreativeFluidSource(BlockPos pos, BlockState state)
    {
        super(ATContent.BE_TYPE_CREATIVE_FLUID_SOURCE.get(), pos, state);

        for (Direction dir : Direction.values()) { neighbors.put(dir, LazyOptional.empty()); }
    }

    private boolean firstTick = true;
    public void tick()
    {
        if (firstTick) //TODO: remove when onLoad() PR is merged
        {
            firstTick = false;
            onLoad();
        }

        if (!level().isClientSide() && !source.getFluidInTank(0).isEmpty())
        {
            FluidStack fluid = source.getFluidInTank(0);
            for (Direction dir : Direction.values())
            {
                if (level().isEmptyBlock(worldPosition.relative(dir))) { continue; }

                LazyOptional<IFluidHandler> neighbor = neighbors.get(dir);
                if (!neighbor.isPresent())
                {
                    BlockEntity be = level().getBlockEntity(worldPosition.relative(dir));
                    if (be != null && !(be instanceof BlockEntityCreativeFluidSource))
                    {
                        neighbor = be.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, dir.getOpposite());
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

        if (!level().isClientSide() && needsSync()) { sendSyncPacket(); }
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
                if (!level().isClientSide()) { source.setFluid(fluid); }
                return true;
            }
            else if (!source.getFluidInTank(0).isEmpty() && fluid.isEmpty())
            {
                if (!level().isClientSide()) { source.setFluid(FluidStack.EMPTY); }
                return true;
            }
            return false;
        }).orElse(false);

        if (success) { markForSync(); }

        return true;
    }

    public FluidStack getContents() { return source.getFluidInTank(0); }

    @Override
    public void onLoad()
    {
        super.onLoad();

        lazySource = LazyOptional.of(() -> source);
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();

        lazySource.invalidate();
        lazySource = LazyOptional.empty();
    }

    @Override
    public void writeNetworkNBT(CompoundTag nbt) { nbt.put("fluid", source.serializeNBT()); }

    @Override
    public void readNetworkNBT(CompoundTag nbt) { source.deserializeNBT(nbt.getCompound("fluid")); }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer) { buffer.writeNbt(source.serializeNBT()); }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer) { source.deserializeNBT(buffer.readNbt()); }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.put("fluid", source.serializeNBT());
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        source.deserializeNBT(nbt.getCompound("fluid"));
    }

    private static final class InfiniteSource implements IFluidHandler, INBTSerializable<CompoundTag>
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
        public CompoundTag serializeNBT()
        {
            CompoundTag nbt = new CompoundTag();
            fluid.writeToNBT(nbt);
            return nbt;
        }

        @Override
        public void deserializeNBT(CompoundTag nbt) { fluid = FluidStack.loadFluidStackFromNBT(nbt); }
    }
}