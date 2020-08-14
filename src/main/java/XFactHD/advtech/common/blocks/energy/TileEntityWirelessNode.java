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

package XFactHD.advtech.common.blocks.energy;

import XFactHD.advtech.common.blocks.TileEntityBase;
import net.darkhax.tesla.api.ITeslaProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;

import java.util.UUID;

public class TileEntityWirelessNode extends TileEntityBase implements ITeslaProducer, IEnergyStorage
{
    private UUID owner = null;

    public void setOwner(UUID owner)
    {
        this.owner = owner;
    }

    public UUID getOwner()
    {
        return owner;
    }

    public long receivePowerFromNetwork(long maxReceive, boolean simulate)
    {
        long toTransmit = 0;
        for (EnumFacing side : EnumFacing.values())
        {
            TileEntity te = worldObj.getTileEntity(pos.offset(side));
            if (te != null)
            {
                if (te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side.getOpposite()))
                {
                    toTransmit += te.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side.getOpposite()).givePower(maxReceive, simulate);
                }
                else if (te.hasCapability(CapabilityEnergy.ENERGY, side.getOpposite()))
                {
                    toTransmit += te.getCapability(CapabilityEnergy.ENERGY, side.getOpposite()).receiveEnergy((int) Math.min(Integer.MAX_VALUE, maxReceive), simulate);
                }
            }
        }
        return toTransmit;
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == CapabilityEnergy.ENERGY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == TeslaCapabilities.CAPABILITY_PRODUCER || capability == CapabilityEnergy.ENERGY)
        {
            return (T)this;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public long takePower(long amount, boolean simulate)
    {
        return 0;
    }

    @Override
    public int receiveEnergy(int maxReceive, boolean simulate)
    {
        return 0;
    }

    @Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
        return 0;
    }

    @Override
    public int getEnergyStored()
    {
        return 0;
    }

    @Override
    public int getMaxEnergyStored()
    {
        return 0;
    }

    @Override
    public boolean canExtract()
    {
        return false;
    }

    @Override
    public boolean canReceive()
    {
        return false;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setString("owner", owner != null ? owner.toString() : "");
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        owner = UUID.fromString(nbt.getString("owner"));
    }
}