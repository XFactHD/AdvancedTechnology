package xfacthd.advtech.common.capability.energy;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;

public class ATEnergyStorage extends EnergyStorage
{
    public ATEnergyStorage(int capacity) { super(capacity); }

    public ATEnergyStorage(int capacity, int maxTransfer) { super(capacity, maxTransfer); }

    public ATEnergyStorage(int capacity, int maxReceive, int maxExtract) { super(capacity, maxReceive, maxExtract); }

    public ATEnergyStorage(int capacity, int maxReceive, int maxExtract, int energy) { super(capacity, maxReceive, maxExtract, energy); }

    public final void reconfigure(int capacity, int maxReceive, int maxExtract)
    {
        this.capacity = Math.max(this.capacity, capacity);
        this.maxReceive = Math.max(this.maxReceive, maxReceive);
        this.maxExtract = Math.max(this.maxExtract, maxExtract);
    }

    public final void setEnergyStored(int energy) { this.energy = energy; }

    public int getMaxReceive() { return maxReceive; }

    public int getMaxExtract() { return maxExtract; }

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