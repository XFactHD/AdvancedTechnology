/*  Copyright (C) <2016>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.common.utils.caps.energy;

import XFactHD.advtech.common.items.energy.ItemBattery;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import javax.annotation.Nullable;

public class CombinedEnergyStorageItem implements ICapabilitySerializable<NBTTagCompound>, IEnergyStorage, ITeslaConsumer, ITeslaProducer, ITeslaHolder
{
    private boolean loadFromStack = false;
    private ItemStack stack;
    private long capacity = 0;
    private long stored = 0;
    private int maxTransfer = 0;

    public CombinedEnergyStorageItem(ItemStack stack, NBTTagCompound nbt)
    {
        this.stack = stack;
        if (nbt != null && nbt.hasKey("advtech:battery")) { deserializeNBT(nbt); }
        else { loadFromStack = true; }
    }

    @Override
    public long givePower(long amount, boolean simulate)
    {
        long toReceive = Math.min(Math.min(amount, maxTransfer), capacity - stored);
        if (!simulate) { stored += toReceive; }
        return toReceive;
    }

    @Override
    public long takePower(long amount, boolean simulate)
    {
        long toExtract = Math.min(Math.min(amount, maxTransfer), stored);
        if (!simulate) { stored -= toExtract; }
        return toExtract;
    }

    @Override
    public long getStoredPower()
    {
        return stored;
    }

    @Override
    public long getCapacity()
    {
        return capacity;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        int toReceive = (int) Math.min(Math.min(maxReceive, maxTransfer), capacity - stored);
        if (!simulate) { stored += toReceive; }
        return toReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        int toExtract = (int) Math.min(Math.min(maxExtract, maxTransfer), stored);
        if (!simulate) { stored -= toExtract; }
        return toExtract;
    }

    @Override
    public int getEnergyStored()
    {
        return (int) Math.min(Integer.MAX_VALUE, stored);
    }

    @Override
    public int getMaxEnergyStored()
    {
        return (int) Math.min(Integer.MAX_VALUE, capacity);
    }

    @Override
    public boolean canExtract()
    {
        return true;
    }

    @Override
    public boolean canReceive()
    {
        return true;
    }

    public boolean shouldLoadFromStack()
    {
        return loadFromStack;
    }

    public void loadFromItemStack()
    {
        capacity = ItemBattery.EnumType.values()[stack.getMetadata()].getCapacity();
        maxTransfer = ItemBattery.EnumType.values()[stack.getMetadata()].getTransfer();
        loadFromStack = false;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        NBTTagCompound tag = new NBTTagCompound();
        tag.setLong("capacity", capacity);
        tag.setLong("stored", stored);
        tag.setInteger("maxTransfer", maxTransfer);
        nbt.setTag("advtech:battery", tag);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        NBTTagCompound tag = nbt.getCompoundTag("advtech:battery");
        capacity = tag.getLong("capacity");
        stored = tag.getLong("stored");
        maxTransfer = tag.getInteger("maxTransfer");
    }

    @Override
    public boolean hasCapability(Capability<?> capability, @Nullable EnumFacing facing)
    {
        return capability == TeslaCapabilities.CAPABILITY_CONSUMER || capability == TeslaCapabilities.CAPABILITY_PRODUCER ||
               capability == TeslaCapabilities.CAPABILITY_HOLDER || capability == CapabilityEnergy.ENERGY;
    }

    @Override
    @SuppressWarnings("unchecked")
    public<T> T getCapability(Capability<T> capability, @Nullable EnumFacing facing)
    {
        if (hasCapability(capability, facing))
        {
            return (T)this;
        }
        return null;
    }
}