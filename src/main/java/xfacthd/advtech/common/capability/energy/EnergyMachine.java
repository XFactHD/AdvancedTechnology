package xfacthd.advtech.common.capability.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;

public class EnergyMachine extends EnergyStorage
{
    public EnergyMachine(int capacity, int maxReceive, int maxExtract) { super(capacity, maxReceive, maxExtract); }

    public int extractEnergyInternal(int maxExtract, boolean simulate)
    {
        int energyExtracted = Math.min(energy, Math.min(this.maxReceive, maxExtract));
        if (!simulate) { energy -= energyExtracted; }
        return energyExtracted;
    }

    public int receiveEnergyInternal(int maxReceive, boolean simulate)
    {
        int energyReceived = Math.min(capacity - energy, Math.min(this.maxExtract, maxReceive));
        if (!simulate) { energy += energyReceived; }
        return energyReceived;
    }

    public void reconfigure(int capacity, int maxReceive, int maxExtract)
    {
        this.capacity = Math.max(this.capacity, capacity);
        this.maxReceive = Math.max(this.maxReceive, maxReceive);
        this.maxExtract = Math.max(this.maxExtract, maxExtract);
    }

    @Override
    public Tag serializeNBT()
    {
        CompoundTag nbt = new CompoundTag();
        nbt.putInt("capacity", capacity);
        nbt.putInt("stored", energy);
        return nbt;
    }

    @Override
    public void deserializeNBT(Tag tag)
    {
        if (!(tag instanceof CompoundTag nbt)) { throw new IllegalArgumentException("Expected CompoundTag"); }
        capacity = nbt.getInt("capacity");
        energy = nbt.getInt("stored");
    }
}