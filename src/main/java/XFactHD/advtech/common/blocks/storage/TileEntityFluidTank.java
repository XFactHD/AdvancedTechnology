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

package XFactHD.advtech.common.blocks.storage;

import XFactHD.advtech.common.blocks.TileEntityBase;
import XFactHD.advtech.common.utils.caps.fluid.FluidHandlerFluidTank;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class TileEntityFluidTank extends TileEntityBase implements ITickable
{
    private BlockFluidTank.EnumType type = null;
    private FluidHandlerFluidTank fluidHandler = null;

    @SuppressWarnings("unused")
    public TileEntityFluidTank()
    {
        fluidHandler = new FluidHandlerFluidTank(0, this);
    }

    public TileEntityFluidTank(BlockFluidTank.EnumType type)
    {
        this.type = type;
        fluidHandler = new FluidHandlerFluidTank(type.getCapacity(), this);
    }

    @Override
    public void update()
    {
        TileEntity te = worldObj.getTileEntity(pos.down());
        if (te != null && te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP))
        {
            IFluidHandler handler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, EnumFacing.UP);
            if (handler.fill(fluidHandler.getFluid(), false) > 0)
            {
                fluidHandler.drainInternal(handler.fill(fluidHandler.getFluid(), true), true);
            }
        }
    }

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return side == EnumFacing.UP || side == EnumFacing.DOWN;
        }
        return super.hasCapability(capability, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing side)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == EnumFacing.UP || side == EnumFacing.DOWN))
        {
            fluidHandler.setCurrentSide(side);
            return (T) fluidHandler;
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void notifyInventoryUpdate()
    {
        notifyBlockUpdate();
    }

    public FluidHandlerFluidTank getFluidHandler()
    {
        return fluidHandler;
    }

    public NBTTagCompound writeTankToNBT()
    {
        return fluidHandler.writeToNBT(new NBTTagCompound());
    }

    public void readTankFromNBT(NBTTagCompound nbt)
    {
        fluidHandler.readFromNBT(nbt);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        nbt.setInteger("type", type.ordinal());
        nbt.setTag("tank", fluidHandler.writeToNBT(new NBTTagCompound()));
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        type = BlockFluidTank.EnumType.values()[nbt.getInteger("type")];
        fluidHandler.readFromNBT(nbt.getCompoundTag("tank"));
    }
}