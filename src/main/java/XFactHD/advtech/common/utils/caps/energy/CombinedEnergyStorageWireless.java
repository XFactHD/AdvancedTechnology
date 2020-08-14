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

import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaHolder;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class CombinedEnergyStorageWireless implements IEnergyStorage, ITeslaConsumer, ITeslaHolder, ITeslaProducer, INBTSerializable<NBTTagCompound>
{
    private static final long BASE_CAPACITY = 5000;
    private long maxTransfer = 1000;
    private long capacity = 5000;
    private long stored = 0;

    @Override
    public long givePower(long amount, boolean simulate)
    {
        long toReceive = Math.min(Math.min(amount, maxTransfer), capacity - stored);
        if (!simulate)
        {
            stored += toReceive;
        }
        return toReceive;
    }

    //WARNING: for internal use only!!!
    @Override
    public long takePower(long amount, boolean simulate)
    {
        long toSend = Math.min(Math.min(amount, maxTransfer), stored);
        if (!simulate)
        {
            stored -= toSend;
        }
        return toSend;
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
        int toReceive = (int) Math.min(Integer.MAX_VALUE, Math.min(Math.min(maxReceive, maxTransfer), capacity - stored));
        if (!simulate)
        {
            stored += toReceive;
        }
        return toReceive;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return 0;
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
        return false;
    }

    @Override
    public boolean canReceive()
    {
        return true;
    }

    public long getMaxTransfer()
    {
        return maxTransfer;
    }

    public void adjustCapacity(int feeders)
    {
        capacity = BASE_CAPACITY * feeders;
        maxTransfer = capacity / 5;
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("maxTransfer", maxTransfer);
        nbt.setLong("capacity", capacity);
        nbt.setLong("stored", stored);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        maxTransfer = nbt.getLong("maxTransfer");
        capacity = nbt.getLong("capacity");
        stored = nbt.getLong("stored");
    }
}