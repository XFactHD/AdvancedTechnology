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

package XFactHD.advtech.common.gui;

import XFactHD.advtech.common.blocks.storage.TileEntityFluidTank;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;

public class ContainerFluidTank extends Container
{
    private EntityPlayer player;
    private TileEntityFluidTank te;
    private int capacity = 0;
    private int amount = 0;
    private Fluid fluid = null;

    public ContainerFluidTank(EntityPlayer player, TileEntityFluidTank te)
    {
        this.player = player;
        this.te = te;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (capacity != te.getFluidHandler().getCapacity())
        {
            capacity = te.getFluidHandler().getCapacity();
            for (IContainerListener listener : listeners)
            {
                listener.sendProgressBarUpdate(this, 0, capacity);
            }
        }
        if (amount != te.getFluidHandler().getFluidAmount())
        {
            amount = te.getFluidHandler().getFluidAmount();
            for (IContainerListener listener : listeners)
            {
                listener.sendProgressBarUpdate(this, 1, amount);
            }
        }
        if (fluid != (te.getFluidHandler().getFluid() != null ? te.getFluidHandler().getFluid().getFluid() : null))
        {
            fluid = (te.getFluidHandler().getFluid() != null ? te.getFluidHandler().getFluid().getFluid() : null);
            for (IContainerListener listener : listeners)
            {
                listener.sendProgressBarUpdate(this, 2, fluid == null ? -1 : FluidRegistry.getFluidID(fluid));
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        if (id == 0) { capacity = data; }
        if (id == 1) { amount = data; }
        if (id == 2) { fluid = (data == -1 ? null : FluidRegistry.getFluid(data)); }
    }

    public int getCapacity()
    {
        return capacity;
    }

    public int getAmount()
    {
        return amount;
    }

    public Fluid getFluid()
    {
        return fluid;
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }
}