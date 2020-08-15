package xfacthd.advtech.common.tileentity.debug;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityBase;

import java.util.EnumMap;
import java.util.Map;

public class TileEntityCreativeEnergySource extends TileEntityBase implements ITickableTileEntity
{
    private final IEnergyStorage source = new InfiniteSource();
    private LazyOptional<IEnergyStorage> lazySource = LazyOptional.empty();
    private final Map<Direction, LazyOptional<IEnergyStorage>> neighbors = new EnumMap<>(Direction.class);

    public TileEntityCreativeEnergySource()
    {
        super(TileEntityTypes.tileTypeCreativeEnergySource);

        for (Direction dir : Direction.values()) { neighbors.put(dir, LazyOptional.empty()); }
    }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            for (Direction dir : Direction.values())
            {
                if (world.isAirBlock(pos.offset(dir))) { continue; }

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
    public void writeNetworkNBT(CompoundNBT nbt) { }

    @Override
    public void readNetworkNBT(CompoundNBT nbt) { }

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