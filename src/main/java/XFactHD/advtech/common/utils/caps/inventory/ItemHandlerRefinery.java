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

package XFactHD.advtech.common.utils.caps.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

public class ItemHandlerRefinery extends ItemStackHandler
{
    private boolean internal = false;

    public ItemHandlerRefinery()
    {
        super(4);
    }

    @Override
    public ItemStack insertItem(int slot, ItemStack stack, boolean simulate)
    {
        if (internal)
        {
            internal = false;
            return super.insertItem(slot, stack, simulate);
        }
        return stack;
    }

    public void setInternal()
    {
        internal = true;
    }

    public ItemStack[] getStacks()
    {
        return stacks;
    }

    public boolean addStack(ItemStack stack, boolean simulate)
    {
        for (int i = 0; i < getSlots(); i++)
        {
            setInternal();
            if (insertItem(i, stack, simulate) == null)
            {
                return true;
            }
        }
        return false;
    }
}