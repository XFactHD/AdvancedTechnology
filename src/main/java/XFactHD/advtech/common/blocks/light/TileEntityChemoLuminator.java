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

package XFactHD.advtech.common.blocks.light;

import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.blocks.TileEntityBase;
import XFactHD.advtech.common.utils.caps.fluid.FluidHandlerChemoLuminator;
import XFactHD.advtech.common.utils.caps.fluid.FluidHandlerChemoLuminator.FluidHandlerSpecific;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

public class TileEntityChemoLuminator extends TileEntityBase implements ITickable
{
    private FluidHandlerChemoLuminator fluidHandler = new FluidHandlerChemoLuminator();
    private boolean active = false;
    private int color;

    @Override
    public void update()
    {
        if (!worldObj.isRemote)
        {
            FluidHandlerSpecific handlerHP = fluidHandler.getHandler(Content.fluidHydrogenPeroxide);
            FluidHandlerSpecific handlerOA = fluidHandler.getHandler(Content.fluidOxalAcid);

            FluidStack hp = handlerHP.drainInternal(2, false);
            FluidStack oa = handlerOA.drainInternal(2, false);
            setActive(hp != null && hp.amount == 2 && oa != null && oa.amount == 2);
            if (active)
            {
                handlerHP.drainInternal(2, true);
                handlerOA.drainInternal(2, true);
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

    public void setColor(int color)
    {
        if (this.color != color)
        {
            this.color = color;
            markDirty();
            notifyBlockUpdate();
        }
    }

    public int getColor()
    {
        return color;
    }

    public FluidHandlerChemoLuminator getFluidHandler()
    {
        return fluidHandler;
    }

    public int getAmountHydrogenPeroxide()
    {
        return getFluidHandler().getHandler(Content.fluidHydrogenPeroxide).getFluidAmount();
    }

    public int getAmountOxalAcid()
    {
        return getFluidHandler().getHandler(Content.fluidOxalAcid).getFluidAmount();
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing facing)
    {
        return capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY || super.hasCapability(capability, facing);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing facing)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return (T)fluidHandler;
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        active = nbt.getBoolean("active");
        color = nbt.getInteger("color");
        fluidHandler.deserializeNBT(nbt.getCompoundTag("fluidHandler"));
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setBoolean("active", active);
        nbt.setInteger("color", color);
        nbt.setTag("fluidHandler", fluidHandler.serializeNBT());
    }
}