package xfacthd.advtech.common.blockentity.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityBase;

import java.util.EnumMap;
import java.util.Map;

public class BlockEntityCreativeEnergySource extends BlockEntityBase
{
    private final IEnergyStorage source = new InfiniteSource();
    private LazyOptional<IEnergyStorage> lazySource = LazyOptional.empty();
    private final Map<Direction, LazyOptional<IEnergyStorage>> neighbors = new EnumMap<>(Direction.class);

    public BlockEntityCreativeEnergySource(BlockPos pos, BlockState state)
    {
        super(ATContent.BE_TYPE_CREATIVE_ENERGY_SOURCE.get(), pos, state);

        for (Direction dir : Direction.values()) { neighbors.put(dir, LazyOptional.empty()); }
    }

    private boolean firstTick = true;
    public void tick()
    {
        if (firstTick) //TODO: remove when the onLoad() PR is merged
        {
            firstTick = false;
            onLoad();
        }

        if (!level().isClientSide())
        {
            for (Direction dir : Direction.values())
            {
                if (level().isEmptyBlock(worldPosition.relative(dir))) { continue; }

                LazyOptional<IEnergyStorage> neighbor = neighbors.get(dir);
                if (!neighbor.isPresent())
                {
                    BlockEntity be = level().getBlockEntity(worldPosition.relative(dir));
                    if (be != null && !(be instanceof BlockEntityCreativeEnergySource))
                    {
                        neighbor = be.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
                        if (neighbor.isPresent())
                        {
                            neighbor.addListener((storage) -> invalidateNeighbor(dir));
                            neighbors.put(dir, neighbor);
                        }
                    }
                }

                neighbor.ifPresent(storage -> storage.receiveEnergy(Integer.MAX_VALUE, false));
            }
        }
    }

    private void invalidateNeighbor(Direction side) { neighbors.put(side, LazyOptional.empty()); }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        if (cap == CapabilityEnergy.ENERGY)
        {
            return lazySource.cast();
        }
        return super.getCapability(cap, side);
    }

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
    public void writeNetworkNBT(CompoundTag nbt) { }

    @Override
    public void readNetworkNBT(CompoundTag nbt) { }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer) { }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer) { }

    private static final class InfiniteSource implements IEnergyStorage
    {
        @Override
        public int receiveEnergy(int maxReceive, boolean simulate) { return 0; }

        @Override
        public int extractEnergy(int maxExtract, boolean simulate) { return Integer.MAX_VALUE; }

        @Override
        public int getEnergyStored() { return Integer.MAX_VALUE; }

        @Override
        public int getMaxEnergyStored() { return Integer.MAX_VALUE; }

        @Override
        public boolean canExtract() { return true; }

        @Override
        public boolean canReceive() { return false; }
    }
}