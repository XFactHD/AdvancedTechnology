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

import XFactHD.advtech.common.blocks.TileEntityBase;
import XFactHD.advtech.common.utils.caps.fluid.FluidHandlerGasLamp;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ITickable;

public class TileEntityGasLanternStandingLamp extends TileEntityBase implements ITickable
{
    private FluidHandlerGasLamp fluidHandler = new FluidHandlerGasLamp();
    private boolean active = false;
    private boolean automatic = false;

    @Override
    public void update()
    {
        if (!worldObj.isRemote)
        {
            if (fluidHandler.getFluidAmount() >= 3 && shouldActivate())
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

    private void setActive(boolean active)
    {
        if (this.active != active)
        {
            this.active = active;
            markDirty();
            notifyBlockUpdate();
        }
    }

    public boolean isActive()
    {
        return active;
    }

    public void setAutomatic(boolean automatic)
    {
        this.automatic = automatic;
        markDirty();
        notifyBlockUpdate();
    }

    public boolean isAutomatic()
    {
        return automatic;
    }

    public FluidHandlerGasLamp getFluidHandler()
    {
        return fluidHandler;
    }

    private boolean shouldActivate()
    {
        if (!automatic) { return true; }
        return !worldObj.isDaytime();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        fluidHandler.readFromNBT(nbt.getCompoundTag("tank"));
        active = nbt.getBoolean("active");
        automatic = nbt.getBoolean("auto");
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setTag("tank", fluidHandler.writeToNBT(new NBTTagCompound()));
        nbt.setBoolean("active", active);
        nbt.setBoolean("auto", automatic);
    }
}