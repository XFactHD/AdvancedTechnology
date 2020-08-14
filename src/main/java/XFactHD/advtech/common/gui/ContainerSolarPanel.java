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

import XFactHD.advtech.common.blocks.energy.TileEntitySolarPanel;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ContainerSolarPanel extends Container
{
    private TileEntitySolarPanel te;
    private boolean canSeeTheSun = false;
    private int capacity = 0;
    private int stored = 0;

    public ContainerSolarPanel(TileEntitySolarPanel te)
    {
        this.te = te;
        this.capacity = te.getCapacity();
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        for (IContainerListener listener : listeners)
        {
            if (canSeeTheSun != te.canSeeTheSun())
            {
                listener.sendProgressBarUpdate(this, 0, te.canSeeTheSun() ? 1 : 0);
            }
            if (stored != te.getEnergyStored())
            {
                listener.sendProgressBarUpdate(this, 1, te.getEnergyStored());
            }
            if (capacity != te.getCapacity())
            {
                listener.sendProgressBarUpdate(this, 2, te.getCapacity());
            }
        }
        canSeeTheSun = te.canSeeTheSun();
        stored = te.getEnergyStored();
        capacity = te.getCapacity();
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
        {
            canSeeTheSun = data == 1;
        }
        else if (id == 1)
        {
            stored = data;
        }
        else if (id == 2)
        {
            capacity = data;
        }
    }

    public int getCapacity()
    {
        return capacity;
    }

    public int getEnergyStored()
    {
        return stored;
    }

    public boolean canSeeTheSun()
    {
        return canSeeTheSun;
    }

    public boolean isAdvanced()
    {
        return te.isAdvanced();
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    public float getEnergyStoredScaled()
    {
        return ((float) stored) / ((float) capacity);
    }
}