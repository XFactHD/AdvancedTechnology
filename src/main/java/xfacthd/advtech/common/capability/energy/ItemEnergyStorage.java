package xfacthd.advtech.common.capability.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;
import xfacthd.advtech.common.blockentity.storage.BlockEntityEnergyCube;

public class ItemEnergyStorage implements IEnergyStorage, INBTSerializable<CompoundTag>
{
    private int capacity = BlockEntityEnergyCube.BASE_CAPACITY;
    private int maxTransfer = BlockEntityEnergyCube.BASE_TRANSFER;
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
    public CompoundTag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("capacity", capacity);
        nbt.putInt("transfer", maxTransfer);
        nbt.putInt("stored", energy);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt)
    {
        capacity = nbt.getInt("capacity");
        maxTransfer = nbt.getInt("transfer");
        energy = nbt.getInt("stored");
    }
}