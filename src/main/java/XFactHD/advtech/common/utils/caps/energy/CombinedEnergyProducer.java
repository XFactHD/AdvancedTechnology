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

import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class CombinedEnergyProducer implements ITeslaProducer, IEnergyStorage, INBTSerializable<NBTTagCompound>
{
    private long stored;
    private long capacity;
    private long maxExtract;

    public CombinedEnergyProducer(long capacity, long maxExtract)
    {
        this.capacity = capacity;
        this.maxExtract = maxExtract;
    }

    @Override
    public long takePower(long amount, boolean simulate)
    {
        long toReturn = Math.min(Math.min(amount, maxExtract), stored);
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
        long toReturn = Math.min(Math.min(amount, maxExtract), stored);
        if (toReturn > Integer.MAX_VALUE) { toReturn = Integer.MAX_VALUE; }
        if (!simulate)
        {
            stored -= toReturn;
        }
        return (int) toReturn;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        return 0;
    }

    @Override
    public boolean canExtract()
    {
        return true;
    }

    @Override
    public boolean canReceive()
    {
        return false;
    }

    @Override
    public int getEnergyStored()
    {
        return stored > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) stored;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return capacity > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) capacity;
    }

    public void setCapacity(long capacity)
    {
        this.capacity = capacity;
    }

    public void setMaxExtract(long maxExtract)
    {
        this.maxExtract = maxExtract;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("stored", stored);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        stored = nbt.getLong("stored");
        if (stored < 0) { stored = 0; }
        if (stored > capacity) { stored = capacity; }
    }

    public int getMaxExtractInt()
    {
        return maxExtract > Integer.MAX_VALUE ? Integer.MAX_VALUE : (int) maxExtract;
    }

    public long getMaxExtract()
    {
        return maxExtract;
    }
}