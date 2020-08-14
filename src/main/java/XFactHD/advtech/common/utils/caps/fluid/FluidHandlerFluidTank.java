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

package XFactHD.advtech.common.utils.caps.fluid;

import XFactHD.advtech.common.blocks.storage.TileEntityFluidTank;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidTank;

public class FluidHandlerFluidTank extends FluidTank
{
    private TileEntityFluidTank te;
    private EnumFacing currentSide = null;

    public FluidHandlerFluidTank(int capacity, TileEntityFluidTank te)
    {
        super(capacity);
        this.te = te;
    }

    @Override
    public boolean canFill()
    {
        return currentSide == EnumFacing.UP || currentSide == null;
    }

    @Override
    public boolean canDrain()
    {
        return currentSide == EnumFacing.DOWN || currentSide == null;
    }

    @Override
    protected void onContentsChanged()
    {
        te.notifyInventoryUpdate();
    }

    @Override
    public int fillInternal(FluidStack resource, boolean doFill)
    {
        currentSide = null;
        return super.fillInternal(resource, doFill);
    }

    @Override
    public FluidStack drainInternal(int maxDrain, boolean doDrain)
    {
        currentSide = null;
        return super.drainInternal(maxDrain, doDrain);
    }

    @Override
    public FluidStack drainInternal(FluidStack resource, boolean doDrain)
    {
        currentSide = null;
        return super.drainInternal(resource, doDrain);
    }

    public void setCurrentSide(EnumFacing currentSide)
    {
        this.currentSide = currentSide;
    }
}