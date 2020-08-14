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

import XFactHD.advtech.common.blocks.energy.TileEntityCable;
import net.darkhax.tesla.api.ITeslaConsumer;
import net.darkhax.tesla.api.ITeslaProducer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

public class CombinedEnergyCable implements ITeslaConsumer, ITeslaProducer, IEnergyStorage, INBTSerializable<NBTTagCompound>
{
    private TileEntityCable te;
    private long capacity;
    private long maxTransfer;
    private long stored = 0;
    private EnumFacing currentSide = null;

    public CombinedEnergyCable(TileEntityCable te, long maxTransfer)
    {
        this.te = te;
        this.maxTransfer = maxTransfer;
        this.capacity = maxTransfer * 6;
    }

    @Override
    public long givePower(long amount, boolean simulate)
    {
        long toAdd = Math.min(Math.min(amount, maxTransfer), capacity - stored);
        if (!simulate)
        {
            stored += toAdd;
            te.addSideToIgnore(currentSide);
        }
        currentSide = null;
        return toAdd;
    }

    @Override
    public long takePower(long amount, boolean simulate)
    {
        long toRemove = Math.min(Math.min(amount, maxTransfer), stored);
        if (!simulate)
        {
            stored -= toRemove;
        }
        currentSide = null;
        return toRemove;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        int toAdd = (int) Math.min(Integer.MAX_VALUE, Math.min(Math.min(maxReceive, maxTransfer), capacity - stored));
        if (!simulate)
        {
            stored += toAdd;
            te.addSideToIgnore(currentSide);
        }
        currentSide = null;
        return toAdd;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        int toRemove = (int) Math.min(Integer.MAX_VALUE, Math.min(Math.min(maxExtract, maxTransfer), stored));
        if (!simulate) { stored -= toRemove; }
        return toRemove;
    }

    @Override
    public int getEnergyStored()
    {
        return (int)Math.min(Integer.MAX_VALUE, stored);
    }

    @Override
    public int getMaxEnergyStored()
    {
        return (int) Math.min(Integer.MAX_VALUE, capacity);
    }

    @Override
    public boolean canReceive()
    {
        return true;
    }

    @Override
    public boolean canExtract()
    {
        return true;
    }

    public void setCurrentSide(EnumFacing currentSide)
    {
        this.currentSide = currentSide;
    }

    public void setMaxTransfer(long maxTransfer)
    {
        this.maxTransfer = maxTransfer;
        this.capacity = maxTransfer * 6;
    }

    public int getMaxTransfer()
    {
        return (int) Math.min(Integer.MAX_VALUE, maxTransfer);
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        capacity = nbt.getLong("capacity");
        maxTransfer = nbt.getLong("maxTransfer");
        stored = nbt.getLong("stored");
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        nbt.setLong("capacity", capacity);
        nbt.setLong("maxTransfer", maxTransfer);
        nbt.setLong("stored", stored);
        return nbt;
    }
}