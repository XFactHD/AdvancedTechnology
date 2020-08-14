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

package XFactHD.advtech.common.blocks.light;

import XFactHD.advtech.common.blocks.TileEntityBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.lang.ref.WeakReference;

public class TileEntityGasLanternStandingBase extends TileEntityBase
{
    private WeakReference<TileEntityGasLanternStandingLamp> lamp = new WeakReference<>(null);

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return side == EnumFacing.DOWN && getLamp() != null;
        }
        return super.hasCapability(capability, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing side)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && side == EnumFacing.DOWN)
        {
            return getLamp() == null ? null : (T) getLamp().getFluidHandler();
        }
        return super.getCapability(capability, side);
    }

    private TileEntityGasLanternStandingLamp getLamp()
    {
        if (lamp.get() != null) { return lamp.get(); }
        TileEntity te = worldObj.getTileEntity(pos.up(2));
        if (te instanceof TileEntityGasLanternStandingLamp)
        {
            lamp = new WeakReference<>((TileEntityGasLanternStandingLamp)te);
        }
        return lamp.get();
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {

    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {

    }
}