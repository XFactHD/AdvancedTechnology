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

import XFactHD.advtech.api.block.IInventoryTile;
import XFactHD.advtech.common.blocks.TileEntityOrientable;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.IItemHandler;

public class TileEntityElectricFurnace extends TileEntityOrientable implements IInventoryTile
{
    @Override
    public IItemHandler getInventory()
    {
        return null;
    }

    @Override
    public void readCustomNBT(NBTTagCompound nbt)
    {
        super.readCustomNBT(nbt);
    }

    @Override
    public void writeCustomNBT(NBTTagCompound nbt)
    {
        super.writeCustomNBT(nbt);
    }
}