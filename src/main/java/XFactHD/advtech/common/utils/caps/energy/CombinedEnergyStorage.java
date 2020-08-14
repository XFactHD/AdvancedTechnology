/*  Copyright (C) <2017>  <XFactHD>

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

import XFactHD.advtech.common.utils.utilClasses.SideSetting;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class CombinedEnergyStorage implements ITeslaConsumer, ITeslaProducer, IEnergyStorage, INBTSerializable<NBTTagCompound>
{
    private long stored;
    private long capacity;
    private long maxReceive;
    private long maxExtract;
    private SideSetting setting = null;

    public CombinedEnergyStorage(long capacity, long maxReceive, long maxExtract)
    {
        this.capacity = capacity;
        this.maxReceive = maxReceive;
        this.maxExtract = maxExtract;
    }

    @Override
    public long takePower(long amount, boolean simulate)
    {
        if (setting != SideSetting.OUT) { return 0; }
        long toReturn = Math.min(Math.min(amount, maxExtract), stored);
        if (!simulate)
        {
            stored -= toReturn;
            setting = null;
        }
        return toReturn;
    }

    @Override
    public long givePower(long amount, boolean simulate)
    {
        if (setting != SideSetting.IN) { return 0; }
        long toReturn = Math.min(Math.min(amount, maxReceive), capacity - stored);
        if (!simulate)
        {
            stored += toReturn;
            setting = null;
        }
        return toReturn;
    }

    public long removePowerInternal(long amount, boolean simulate)
    {
        long toReturn = Math.min(amount, stored);
        if (!simulate)
        {
            stored -= toReturn;
        }
        return toReturn;
    }

    public long addPowerInternal(long amount, boolean simulate)
    {
        long toReturn = Math.min(amount, capacity - stored);
        if (!simulate)
        {
            stored += toReturn;
        }
        return toReturn;
    }

    @Override
    public int extractEnergy(int amount, boolean simulate)
    {
        if (setting != SideSetting.OUT) { return 0; }
        long toReturn = Math.min(Integer.MAX_VALUE, Math.min(Math.min(amount, maxExtract), stored));
        if (!simulate)
        {
            stored -= toReturn;
        }
        setting = null;
        return (int) toReturn;
    }

    @Override
    public int receiveEnergy(int amount, boolean simulate)
    {
        if (setting != SideSetting.IN) { return 0; }
        long toReturn = Math.min(Integer.MAX_VALUE, Math.min(amount, capacity - stored));
        if (!simulate)
        {
            stored += toReturn;
        }
        setting = null;
        return (int) toReturn;
    }

    @Override
    public boolean canExtract()
    {
        return setting == null || setting == SideSetting.OUT;
    }

    @Override
    public boolean canReceive()
    {
        return setting == null || setting == SideSetting.IN;
    }

    public void setSetting(SideSetting setting)
    {
        this.setting = setting;
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

    public long getMaxExtract()
    {
        return maxExtract;
    }

    public long getMaxReceive()
    {
        return maxReceive;
    }

    public long getStored()
    {
        return stored;
    }

    public long getCapacity()
    {
        return capacity;
    }

    public void setCapacity(long capacity)
    {
        this.capacity = capacity;
        if (stored > capacity) { stored = capacity; }
    }

    public void setMaxTransfer(int maxTransfer)
    {
        this.maxReceive = maxTransfer;
        this.maxExtract = maxTransfer;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("stored", stored);
        nbt.setLong("capacity", capacity);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        stored = nbt.getLong("stored");
        capacity = nbt.getLong("capacity");
        if (stored < 0) { stored = 0; }
        if (stored > capacity) { stored = capacity; }
    }
}