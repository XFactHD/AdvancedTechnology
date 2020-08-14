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

package XFactHD.advtech.common.utils.caps.fluid;

import XFactHD.advtech.common.Content;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidHandlerFluidMap;

public class FluidHandlerChemoLuminator extends FluidHandlerFluidMap implements INBTSerializable<NBTTagCompound>
{
    public FluidHandlerChemoLuminator()
    {
        super();
        addHandler(Content.fluidHydrogenPeroxide, new FluidHandlerSpecific(Content.fluidHydrogenPeroxide));
        addHandler(Content.fluidOxalAcid, new FluidHandlerSpecific(Content.fluidOxalAcid));
    }

    @Override
    public NBTTagCompound serializeNBT()
    {
        NBTTagCompound nbt = new NBTTagCompound();
        IFluidHandler handlerHP = handlers.get(Content.fluidHydrogenPeroxide);
        NBTTagCompound handlerTagHP = new NBTTagCompound();
        FluidStack fluidStackHP = handlerHP.getTankProperties()[0].getContents();
        if (fluidStackHP != null)
        {
            fluidStackHP.writeToNBT(handlerTagHP);
        }
        IFluidHandler handlerOA = handlers.get(Content.fluidOxalAcid);
        NBTTagCompound handlerTagOA = new NBTTagCompound();
        FluidStack fluidStackOA = handlerOA.getTankProperties()[0].getContents();
        if (fluidStackOA != null)
        {
            fluidStackOA.writeToNBT(handlerTagHP);
        }
        nbt.setTag("contentsHP", handlerTagHP);
        nbt.setTag("contentsOA", handlerTagOA);
        return nbt;
    }

    @Override
    public void deserializeNBT(NBTTagCompound nbt)
    {
        FluidStack fluidStackHP = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("contentsHP"));
        FluidStack fluidStackOA = FluidStack.loadFluidStackFromNBT(nbt.getCompoundTag("contentsOA"));
        ((FluidHandlerSpecific)handlers.get(Content.fluidHydrogenPeroxide)).setContents(fluidStackHP);
        ((FluidHandlerSpecific)handlers.get(Content.fluidOxalAcid)).setContents(fluidStackOA);
    }

    public FluidHandlerSpecific getHandler(Fluid fluid)
    {
        return (FluidHandlerSpecific) handlers.get(fluid);
    }

    public static class FluidHandlerSpecific extends FluidTank
    {
        private Fluid allowedFluid;

        public FluidHandlerSpecific(Fluid allowedFluid)
        {
            super(5000);
            this.allowedFluid = allowedFluid;
            setCanDrain(false);
        }

        @Override
        public boolean canFillFluidType(FluidStack fluid)
        {
            return fluid.getFluid() == allowedFluid;
        }

        @Override
        public boolean canDrainFluidType(FluidStack fluid)
        {
            return false;
        }

        public void setContents(FluidStack stack)
        {
            this.fluid = stack;
        }
    }
}