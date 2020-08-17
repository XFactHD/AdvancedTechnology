package xfacthd.advtech.common.capability.energy;

import net.minecraft.util.Direction;
import net.minecraftforge.energy.IEnergyStorage;
import xfacthd.advtech.common.tileentity.TileEntityEnergyHandler;

public class SidedEnergyHandler implements IEnergyStorage
{
    private final TileEntityEnergyHandler tile;
    private final IEnergyStorage internal;
    private final Direction side;

    public SidedEnergyHandler(TileEntityEnergyHandler tile, IEnergyStorage internal, Direction side)
    {
        this.tile = tile;
        this.internal = internal;
        this.side = side;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate) { return internal.receiveEnergy(maxReceive, simulate); }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate) { return internal.extractEnergy(maxExtract, simulate); }

    @Override
    public int getEnergyStored() { return internal.getEnergyStored(); }

    @Override
    public int getMaxEnergyStored() { return internal.getMaxEnergyStored(); }

    @Override
    public boolean canExtract() { return internal.canExtract() && tile.canExtractEnergy(side); }

    @Override
    public boolean canReceive() { return internal.canReceive() && tile.canReceiveEnergy(side); }
}