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

import XFactHD.advtech.common.blocks.light.TileEntityChemoLuminator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IContainerListener;

public class ContainerChemoLuminator extends Container
{
    private TileEntityChemoLuminator te;
    private EntityPlayer player;
    private int amountHP = 0;
    private int amountOA = 0;

    public ContainerChemoLuminator(EntityPlayer player, TileEntityChemoLuminator te)
    {
        this.te = te;
        this.player = player;
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
        if (amountHP != te.getAmountHydrogenPeroxide())
        {
            amountHP = te.getAmountHydrogenPeroxide();
            for (IContainerListener listener : listeners)
            {
                listener.sendProgressBarUpdate(this, 0, amountHP);
            }
        }
        if (amountOA != te.getAmountOxalAcid())
        {
            amountOA = te.getAmountOxalAcid();
            for (IContainerListener listener : listeners)
            {
                listener.sendProgressBarUpdate(this, 1, amountOA);
            }
        }
    }

    @Override
    public void updateProgressBar(int id, int data)
    {
        if (id == 0)
        {
            amountHP = data;
        }
        else if (id == 1)
        {
            amountOA = data;
        }
    }

    @Override
    public boolean canInteractWith(EntityPlayer player)
    {
        return true;
    }

    public int getAmountHydrogenPeroxide()
    {
        return amountHP;
    }

    public int getAmountOxalAcid()
    {
        return amountOA;
    }
}