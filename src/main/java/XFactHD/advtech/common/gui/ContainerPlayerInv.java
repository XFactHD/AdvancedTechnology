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

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.function.Function;

public abstract class ContainerPlayerInv extends Container
{
    protected static final Function<ItemStack, Boolean> FALSE_CONDITION = new Function<ItemStack, Boolean>() {
        @Override
        public Boolean apply(ItemStack stack)
        {
            return false;
        }
    };

    public ContainerPlayerInv(EntityPlayer player, int xOffset, int yOffset)
    {
        for (int y = 0; y < 3; ++y)
        {
            for (int x = 0; x < 9; ++x)
            {
                addSlotToContainer(new Slot(player.inventory, x + y * 9 + 9, 8 + x * 18 + xOffset, 84 + y * 18 + yOffset));
            }
        }
        for (int x = 0; x < 9; ++x)
        {
            addSlotToContainer(new Slot(player.inventory, x, 8 + xOffset + x * 18, 142 + yOffset));
        }
    }
}