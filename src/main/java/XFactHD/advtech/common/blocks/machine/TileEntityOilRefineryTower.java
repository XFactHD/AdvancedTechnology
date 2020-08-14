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

package XFactHD.advtech.common.blocks.machine;

import XFactHD.advtech.common.blocks.TileEntityOrientable;
import XFactHD.advtech.common.utils.caps.fluid.FluidHandlerRefinery;
import XFactHD.advtech.common.utils.utilClasses.TowerLevel;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.lang.ref.WeakReference;

//Light gas, gasoline, petroleum, diesel and lubricant are removed here, each on their own level
public class TileEntityOilRefineryTower extends TileEntityOrientable
{
    private WeakReference<TileEntityOilRefineryMaster> master = new WeakReference<>(null);
    private WeakReference<FluidHandlerRefinery> fluidHandler = new WeakReference<>(null);
    private TowerLevel level = TowerLevel.ONE;

    @Override
    public boolean hasCapability(Capability<?> capability, EnumFacing side)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            return side == getFacing().rotateY() || side == getFacing().rotateYCCW();
        }
        return super.hasCapability(capability, side);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T getCapability(Capability<T> capability, EnumFacing side)
    {
        if (capability == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY && (side == getFacing().rotateY() || side == getFacing().rotateYCCW()))
        {
            return (T) getFluidHandlerForLevel(pos, level);
        }
        return super.getCapability(capability, side);
    }

    public FluidHandlerRefinery getFluidHandler()
    {
        return getFluidHandlerForLevel(pos, level);
    }

    @SuppressWarnings("ConstantConditions")
    private FluidHandlerRefinery getFluidHandlerForLevel(BlockPos pos, TowerLevel level)
    {
        if (fluidHandler.get() != null) { return fluidHandler.get(); }
        TileEntityOilRefineryMaster te = master.get();
        if (te == null)
        {
            BlockPos masterPos = getMasterPosFromTowerLevel(pos, level);
            TileEntity tile = worldObj.getTileEntity(masterPos);
            if (tile instanceof TileEntityOilRefineryMaster)
            {
                te = ((TileEntityOilRefineryMaster)tile);
                master = new WeakReference<>(te);
            }
        }
        switch (level)
        {
            case ONE:   fluidHandler = new WeakReference<>(te.getHandlerLubricant()); break;
            case TWO:   fluidHandler = new WeakReference<>(te.getHandlerDiesel()); break;
            case THREE: fluidHandler = new WeakReference<>(te.getHandlerPetroleum()); break;
            case FOUR:  fluidHandler = new WeakReference<>(te.getHandlerGasoline()); break;
            case FIVE:  fluidHandler = new WeakReference<>(te.getHandlerGas()); break;
            default:    return te.getHandlerLubricant();
        }
        return fluidHandler.get();
    }

    private BlockPos getMasterPosFromTowerLevel(BlockPos pos, TowerLevel level)
    {
        switch (level)
        {
            case BASE:  return pos;
            case ONE:   return pos.down(1);
            case TWO:   return pos.down(2);
            case THREE: return pos.down(3);
            case FOUR:  return pos.down(4);
            case FIVE:  return pos.down(5);
            default: return pos;
        }
    }

    public void setLevel(TowerLevel level)
    {
        this.level = level;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
        level = TowerLevel.values()[nbt.getInteger("level")];
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
        nbt.setInteger("level", level.ordinal());
    }
}