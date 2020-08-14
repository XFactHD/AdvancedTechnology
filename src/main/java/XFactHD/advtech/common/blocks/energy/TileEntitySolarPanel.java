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

package XFactHD.advtech.common.blocks.energy;

import XFactHD.advtech.common.blocks.TileEntityOrientable;
import XFactHD.advtech.common.utils.caps.energy.CombinedEnergyProducer;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.energy.CapabilityEnergy;

public class TileEntitySolarPanel extends TileEntityOrientable implements ITickable
{
    private boolean canSeeTheSun = false;
    private boolean advanced = false;
    private CombinedEnergyProducer producer = new CombinedEnergyProducer(10000, 250);

    @Override
    public void update()
    {
        if (!worldObj.isRemote)
        {
            canSeeTheSun = worldObj.canBlockSeeSky(pos.up()) && worldObj.isDaytime();
            if (canSeeTheSun)
            {
                producer.addPowerInternal(advanced ? 100 : 25, false);
            }
            TileEntity te = worldObj.getTileEntity(pos.down());
            if (te != null)
            {
                if (te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, EnumFacing.UP))
                {
                    long toTransfer = te.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, EnumFacing.UP).givePower(producer.getMaxExtract(), true);
                    te.getCapability(TeslaCapabilities.CAPABILITY_CONSUMER, EnumFacing.UP).givePower(producer.takePower(toTransfer, false), false);
                }
                else if (te.hasCapability(CapabilityEnergy.ENERGY, EnumFacing.UP))
                {
                    int toTransfer = te.getCapability(CapabilityEnergy.ENERGY, EnumFacing.UP).receiveEnergy(producer.getMaxExtractInt(), true);
                    te.getCapability(CapabilityEnergy.ENERGY, EnumFacing.UP).receiveEnergy(producer.extractEnergy(toTransfer, false), false);
                }
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return facing == EnumFacing.DOWN && (capability == CapabilityEnergy.ENERGY || capability == TeslaCapabilities.CAPABILITY_PRODUCER);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (facing == EnumFacing.DOWN)
        {
            if (capability == CapabilityEnergy.ENERGY || capability == TeslaCapabilities.CAPABILITY_PRODUCER)
            {
                return (T)producer;
            }
        }
        return super.getCapability(capability, facing);
    }

    public void setAdvanced(boolean advanced)
    {
        this.advanced = advanced;
        if (advanced)
        {
            producer.setCapacity(40000);
            producer.setMaxExtract(1000);
        }
        notifyBlockUpdate();
    }

    public boolean canSeeTheSun()
    {
        return canSeeTheSun;
    }

    public int getCapacity()
    {
        return producer.getMaxEnergyStored();
    }

    public int getEnergyStored()
    {
        return producer.getEnergyStored();
    }

    public boolean isAdvanced()
    {
        return advanced;
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setBoolean("advanced", advanced);
        nbt.setTag("storage", producer.serializeNBT());
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        advanced = nbt.getBoolean("advanced");
        producer.deserializeNBT(nbt.getCompoundTag("storage"));
    }
}