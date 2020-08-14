/*  Copyright (C) <2016>  <XFactHD, DrakoAlcarus>

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

package XFactHD.advtech.common.blocks.light;

import XFactHD.advtech.common.blocks.TileEntityOrientable;
import XFactHD.advtech.common.utils.caps.fluid.FluidHandlerGasLamp;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileEntityGasLanternWallmount extends TileEntityOrientable implements ITickable
{
    private FluidHandlerGasLamp fluidHandler = new FluidHandlerGasLamp();
    private boolean active = false;

    @Override
    public void update()
    {
        if (!worldObj.isRemote)
        {
            if (fluidHandler.getFluidAmount() >= 3)
            {
                setActive(true);
                fluidHandler.drainInternal(3, true);
            }
            else
            {
                setActive(false);
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return side == getFacing();
        }
        return super.hasCapability(capability, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing side)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side == getFacing())
        {
            return (T) fluidHandler;
        }
        return super.getCapability(capability, side);
    }

    public FluidHandlerGasLamp getFluidHandler()
    {
        return fluidHandler;
    }

    public void setActive(boolean active)
    {
        if (this.active != active)
        {
            this.active = active;
            worldObj.setBlockState(pos, getState().withProperty(PropertyHolder.ACTIVE, active));
            markDirty();
            notifyBlockUpdate();
        }
    }

    public boolean isActive()
    {
        return active;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        fluidHandler.readFromNBT(nbt.getCompoundTag("tank"));
        active = nbt.getBoolean("active");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setTag("tank", fluidHandler.writeToNBT(new NBTTagCompound()));
        nbt.setBoolean("active", active);
    }
}