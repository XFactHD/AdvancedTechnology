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

package XFactHD.advtech.common.utils.caps.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerQuarry extends ItemStackHandler
{
    private EnumFacing side = null;
    public ItemHandlerQuarry()
    {
        super(16);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (side == null)
        {
            return super.insertItem(slot, stack, simulate);
        }
        side = null;
        return stack;
    }

    public void setSide(EnumFacing side)
    {
        this.side = side;
    }
}