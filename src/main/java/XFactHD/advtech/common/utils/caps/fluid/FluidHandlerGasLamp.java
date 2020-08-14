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

package XFactHD.advtech.common.utils.caps.fluid;

import XFactHD.advtech.common.Content;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidHandlerGasLamp extends FluidTank
{
    public FluidHandlerGasLamp()
    {
        super(2000);
    }

    @Override
    public boolean canDrain()
    {
        return false;
    }

    @Override
    public boolean canFillFluidType(FluidStack fluid)
    {
        return fluid != null && (fluid.getFluid() == Content.fluidBiogas || fluid.getFluid() == Content.fluidNaturalGas);
    }
}