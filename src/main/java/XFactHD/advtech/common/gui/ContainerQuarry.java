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

package XFactHD.advtech.common.gui;

import XFactHD.advtech.common.blocks.machine.TileEntityQuarry;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.items.SlotItemHandler;

public class ContainerQuarry extends ContainerPlayerInv
{
    private TileEntityQuarry te;
    private int stored = 0;
    private int capacity = 0;

    public ContainerQuarry(EntityPlayer player, TileEntityQuarry te)
    {
        super(player, 7, 20);
        this.te = te;
        for (int y = 0; y < 4; y++)
        {
            for (int x = 0; x < 4; x++)
            {
                addSlotToContainer(new SlotItemHandler(te.getItemHandler(), x + y * 4, 112 + x * 18, 18 + y * 18));
            }
        }
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners)
        {
            if (stored != te.getEnergyStored())
            {
                listener.sendProgressBarUpdate(this, 0, te.getEnergyStored());
            }
            if (capacity != te.getMaxEnergyStored())
            {
                listener.sendProgressBarUpdate(this, 1, te.getMaxEnergyStored());
            }
        }
        stored = te.getEnergyStored();
        capacity = te.getMaxEnergyStored();
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        super.updateProgressBar(id, data);
        if (id == 0)
        {
            stored = data;
        }
        else if (id == 1)
        {
            capacity = data;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    public float getEnergyScaled()
    {
        return (float) stored / (float) capacity;
    }

    public int getEnergyStored()
    {
        return stored;
    }

    public int getCapacity()
    {
        return capacity;
    }
}