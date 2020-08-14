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

package XFactHD.advtech.common.utils.utilClasses;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

public class SlotItemHandlerSpecial extends SlotItemHandler
{
    private boolean canExtract;
    private Function<ItemStack, Boolean> condition;
    private List<ItemStack> filter;
    private boolean active = true;

    public SlotItemHandlerSpecial(IItemHandler handler, int index, int xPosition, int yPosition, boolean canExtract, Function<ItemStack, Boolean> condition, ItemStack... filter)
    {
        super(handler, index, xPosition, yPosition);
        this.canExtract = canExtract;
        this.condition = condition;
        this.filter = Arrays.asList(filter);
    }

    @Override
    public boolean isItemValid(ItemStack stack)
    {
        if (filter.isEmpty())
        {
            return condition != null ? condition.apply(stack) : true;
        }
        for (ItemStack filterStack : filter)
        {
            if (areStacksEqual(stack, filterStack))
            {
                if (condition != null)
                {
                    if (condition.apply(stack)) { return true; }
                }
                else
                {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean isHere(IInventory inv, int slot)
    {
        return super.isHere(inv, slot) && active;
    }

    @Override
    public boolean canBeHovered()
    {
        return super.canBeHovered() && active;
    }

    @Override
    public boolean canTakeStack(EntityPlayer player)
    {
        return super.canTakeStack(player) && canExtract && active;
    }

    public void setActive(boolean active)
    {
        this.active = active;
    }

    public boolean isActive()
    {
        return active;
    }

    private boolean areStacksEqual(ItemStack stack, ItemStack filter)
    {
        if (stack == null) { return true; }
        if (stack.getItem() != filter.getItem()) { return false; }
        if (stack.getMetadata() != filter.getMetadata()) { return false; }
        return true;
    }
}