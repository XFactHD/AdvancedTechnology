package xfacthd.advtech.common.capability.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import xfacthd.advtech.common.tileentity.storage.TileEntityEnergyCube;

public class ItemEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundNBT>
{
    private int capacity = TileEntityEnergyCube.BASE_CAPACITY;
    private int maxTransfer = TileEntityEnergyCube.BASE_TRANSFER;
    private int energy = 0;

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        int energyReceived = Math.min(capacity - energy, Math.min(maxTransfer, maxReceive));
        if (!simulate) { energy += energyReceived; }
        return energyReceived;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        int energyExtracted = Math.min(energy, Math.min(maxTransfer, maxExtract));
        if (!simulate) { energy -= energyExtracted; }
        return energyExtracted;
    }

    @Override
    public int getEnergyStored() { return energy; }

    @Override
    public int getMaxEnergyStored() { return capacity; }

    public int getMaxTransfer() { return maxTransfer; }

    @Override
    public boolean canExtract() { return true; }

    @Override
    public boolean canReceive() { return true; }

    public void initFromTile(int capacity, int maxTransfer, int stored)
    {
        this.capacity = capacity;
        this.maxTransfer = maxTransfer;
        this.energy = stored;
    }

    @Override
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("capacity", capacity);
        nbt.putInt("transfer", maxTransfer);
        nbt.putInt("stored", energy);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        capacity = nbt.getInt("capacity");
        maxTransfer = nbt.getInt("transfer");
        energy = nbt.getInt("stored");
    }
}