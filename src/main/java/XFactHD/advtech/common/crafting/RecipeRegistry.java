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

package XFactHD.advtech.common.crafting;

import net.minecraft.item.ItemStack;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class RecipeRegistry
{
    public static final RecipeRegistry INSTANCE = new RecipeRegistry();



    private boolean doObjectsMatch(Object one, Object two)
    {
        if (one instanceof ItemStack && two instanceof ItemStack)
        {
            return doItemStacksMatch((ItemStack)one, (ItemStack)two);
        }
        else if (one instanceof FluidStack && two instanceof FluidStack)
        {
            return doFluidStacksMatch((FluidStack)one, (FluidStack)two);
        }
        else if (one instanceof String && two instanceof String)
        {
            return one.equals(two);
        }
        else
        {
            throw new UnsupportedOperationException();
        }
    }

    private boolean doItemStacksMatch(ItemStack stackOne, ItemStack stackTwo)
    {
        return (stackOne.getItem() == stackTwo.getItem() && stackOne.getMetadata() == stackTwo.getMetadata()) || doItemStacksMatchOreDict(stackOne, stackTwo);
    }

    private boolean doFluidStacksMatch(FluidStack stackOne, FluidStack stackTwo)
    {
        return stackOne.getFluid() == stackTwo.getFluid() && stackOne.amount == stackTwo.amount && stackOne.tag == stackTwo.tag;
    }

    private boolean doItemStacksMatchOreDict(ItemStack stackOne, ItemStack stackTwo)
    {
        int[] idsStackOne = OreDictionary.getOreIDs(stackOne);
        int[] idsStackTwo = OreDictionary.getOreIDs(stackTwo);
        if (idsStackOne.length == 0 || idsStackTwo.length == 0) { return false; }
        for (int idStackOne : idsStackOne)
        {
            for (int idStackTwo : idsStackTwo)
            {
                if (OreDictionary.getOreName(idStackOne).equals(OreDictionary.getOreName(idStackTwo)))
                {
                    return true;
                }
            }
        }
        return false;
    }
}