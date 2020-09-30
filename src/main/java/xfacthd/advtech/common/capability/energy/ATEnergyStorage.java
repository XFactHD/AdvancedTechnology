package xfacthd.advtech.common.capability.energy;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;

public class ATEnergyStorage extends EnergyStorage implements INBTSerializable<CompoundNBT>
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
    public CompoundNBT serializeNBT()
    {
        CompoundNBT nbt = new CompoundNBT();
        nbt.putInt("capacity", capacity);
        nbt.putInt("stored", energy);
        return nbt;
    }

    @Override
    public void deserializeNBT(CompoundNBT nbt)
    {
        capacity = nbt.getInt("capacity");
        energy = nbt.getInt("stored");
    }
}