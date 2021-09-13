package xfacthd.advtech.common.blockentity;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import xfacthd.advtech.common.capability.energy.ATEnergyStorage;
import xfacthd.advtech.common.capability.energy.SidedEnergyHandler;
import xfacthd.advtech.common.util.LogHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;

public abstract class BlockEntityEnergyHandler extends BlockEntityBase
{
    protected final Map<Direction, SidedEnergyHandler> energyHandlers;
    protected final ATEnergyStorage internalEnergyHandler;

    protected final Map<Direction, LazyOptional<SidedEnergyHandler>> lazyEnergyHandlers = new EnumMap<>(Direction.class);
    private LazyOptional<ATEnergyStorage> lazyInternalEnergyHandler;
    private final Map<Direction, LazyOptional<IEnergyStorage>> neighbors = new EnumMap<>(Direction.class);

    public BlockEntityEnergyHandler(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state);

        for (Direction side : Direction.values())
        {
            lazyEnergyHandlers.put(side, LazyOptional.empty());
            neighbors.put(side, LazyOptional.empty());
        }

        internalEnergyHandler = initCapability();
        energyHandlers = Arrays.stream(Direction.values())
                .map(side -> Pair.of(
                        side,
                        new SidedEnergyHandler(this, internalEnergyHandler, side)
                ))
                .collect(
                        () -> new EnumMap<>(Direction.class),
                        (map, pair) -> map.put(pair.getFirst(), pair.getSecond()),
                        EnumMap::putAll
                );

    }

    private boolean firstTick = true;
    public final void tick()
    {
        if (firstTick) //TODO: remove when the onLoad() PR is merged
        {
            firstTick = false;
            onLoad();
        }

        tickInternal();

        if (!level().isClientSide() && needsSync())
        {
            sendSyncPacket();
        }
    }

    protected void tickInternal()
    {
        if (!level().isClientSide())
        {
            for (Direction dir : Direction.values())
            {
                BlockPos neighborPos = worldPosition.relative(dir);

                if (!canExtractEnergy(dir)) { continue; }
                if (level().isEmptyBlock(neighborPos))
                {
                    if (neighbors.get(dir).isPresent())
                    {
                        LogHelper.debug("BlockEntity didn't invalidate its capability, this is a bug!");
                        neighbors.put(dir, LazyOptional.empty());
                    }
                    continue;
                }

                LazyOptional<IEnergyStorage> neighbor = neighbors.get(dir);
                if (!neighbor.isPresent())
                {
                    BlockEntity be = level().getBlockEntity(neighborPos);
                    if (be != null)
                    {
                        neighbor = be.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
                        if (neighbor.isPresent())
                        {
                            neighbor.addListener(storage -> invalidateNeighbor(dir));
                            neighbors.put(dir, neighbor);
                        }
                    }
                }

                neighbor.ifPresent(storage ->
                {
                    int transfer = storage.receiveEnergy(Integer.MAX_VALUE, true);
                    transfer = internalEnergyHandler.extractEnergy(transfer, false);
                    storage.receiveEnergy(transfer, false);
                });
            }
        }
    }

    protected void invalidateNeighbor(Direction side) { neighbors.put(side, LazyOptional.empty()); }

    protected abstract ATEnergyStorage initCapability();

    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side)
    {
        if (cap == CapabilityEnergy.ENERGY)
        {
            if (side == null) { return lazyInternalEnergyHandler.cast(); }

            if (!isSideActive(side)) { return LazyOptional.empty(); }
            return lazyEnergyHandlers.get(side).cast();
        }
        return super.getCapability(cap, side);
    }

    public abstract boolean isSideActive(Direction side);

    public abstract boolean canReceiveEnergy(Direction side);

    public abstract boolean canExtractEnergy(Direction side);

    @Override
    public void onLoad()
    {
        super.onLoad();
        lazyInternalEnergyHandler = LazyOptional.of(() -> internalEnergyHandler);
        Arrays.stream(Direction.values()).forEach(dir -> lazyEnergyHandlers.put(dir, LazyOptional.of(() -> energyHandlers.get(dir))));
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();
        lazyInternalEnergyHandler.invalidate();
        lazyEnergyHandlers.values().forEach(LazyOptional::invalidate);
    }

    @Override
    public CompoundTag save(CompoundTag tag)
    {
        tag.put("energy", internalEnergyHandler.serializeNBT());
        return super.save(tag);
    }

    @Override
    public void load(CompoundTag tag)
    {
        internalEnergyHandler.deserializeNBT(tag.getCompound("energy"));
        super.load(tag);
    }
}