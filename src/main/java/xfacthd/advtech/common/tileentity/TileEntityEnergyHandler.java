package xfacthd.advtech.common.tileentity;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import xfacthd.advtech.common.capability.energy.ATEnergyStorage;
import xfacthd.advtech.common.capability.energy.SidedEnergyHandler;
import xfacthd.advtech.common.tileentity.debug.TileEntityCreativeEnergySource;

import java.util.EnumMap;
import java.util.Map;

public abstract class TileEntityEnergyHandler extends TileEntityBase implements ITickableTileEntity
{
    protected final Map<Direction, SidedEnergyHandler> energyHandlers = new EnumMap<>(Direction.class);
    protected ATEnergyStorage internalEnergyHandler;

    protected final Map<Direction, LazyOptional<SidedEnergyHandler>> lazyEnergyHandlers = new EnumMap<>(Direction.class);
    private LazyOptional<ATEnergyStorage> lazyInternalEnergyHandler;
    private final Map<Direction, LazyOptional<IEnergyStorage>> neighbors = new EnumMap<>(Direction.class);

    public TileEntityEnergyHandler(TileEntityType<?> type)
    {
        super(type);

        for (Direction side : Direction.values())
        {
            lazyEnergyHandlers.put(side, LazyOptional.empty());
            neighbors.put(side, LazyOptional.empty());
        }

        initCapabilities();
    }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            for (Direction dir : Direction.values())
            {
                if (!canExtractEnergy(dir) || world.isAirBlock(pos.offset(dir))) { continue; }

                LazyOptional<IEnergyStorage> neighbor = neighbors.get(dir);
                if (!neighbor.isPresent())
                {
                    TileEntity te = world.getTileEntity(pos.offset(dir));
                    if (te != null && !(te instanceof TileEntityCreativeEnergySource))
                    {
                        neighbor = te.getCapability(CapabilityEnergy.ENERGY, dir.getOpposite());
                        if (neighbor.isPresent())
                        {
                            neighbor.addListener((storage) -> invalidateNeighbor(dir));
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

            if (needsSync()) { sendSyncPacket(); }
        }
    }

    protected void invalidateNeighbor(Direction side) { neighbors.put(side, LazyOptional.empty()); }

    /**
     * Super call MUST happen after initializing the {@link TileEntityEnergyHandler#internalEnergyHandler}
     */
    protected void initCapabilities()
    {
        for (Direction side : Direction.values())
        {
            energyHandlers.put(side, new SidedEnergyHandler(this, internalEnergyHandler, side));
        }
    }

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
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
    public void validate()
    {
        super.validate();

        for (Direction side : Direction.values())
        {
            if (!lazyEnergyHandlers.get(side).isPresent() && isSideActive(side))
            {
                lazyEnergyHandlers.put(side, LazyOptional.of(() -> energyHandlers.get(side)));
            }
        }

        lazyInternalEnergyHandler = LazyOptional.of(() -> internalEnergyHandler);
    }

    @Override
    public void remove()
    {
        super.remove();

        for (Direction side : Direction.values())
        {
            LazyOptional<SidedEnergyHandler> handler = lazyEnergyHandlers.get(side);
            if (handler.isPresent()) { handler.invalidate(); }
        }

        lazyInternalEnergyHandler.invalidate();
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.put("energy", internalEnergyHandler.serializeNBT());
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        internalEnergyHandler.deserializeNBT(nbt.getCompound("energy"));
    }
}