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

package XFactHD.advtech.common.items;

import XFactHD.advtech.common.utils.Reference;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.oredict.OreDictionary;

import java.util.List;

public class ItemBase extends Item
{
    private String[] subnames;

    public ItemBase(String name, int stacksize, CreativeTabs creativeTab, String[] subnames)
    {
        this(name, stacksize, creativeTab, subnames, null);
    }

    public ItemBase(String name, int stacksize, CreativeTabs creativeTab, String[] subnames, String[] oreDictNames)
    {
        this.subnames = subnames;
        setHasSubtypes(subnames != null);
        setRegistryName(new ResourceLocation(Reference.MOD_ID, name));
        setUnlocalizedName(getRegistryName().toString());
        setMaxStackSize(stacksize);
        setCreativeTab(creativeTab);
        GameRegistry.register(this);
        registerOreDictEntries(oreDictNames);
    }

    @Override
    public void getSubItems(Item item, CreativeTabs tab, List<ItemStack> subItems)
    {
        if (subnames != null)
        {
            for (int i = 0; i < subnames.length; i++)
            {
                subItems.add(new ItemStack(item, 1, i));
            }
        }
        else
        {
            subItems.add(new ItemStack(item));
        }
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        int meta = stack.getMetadata();
        if (subnames != null && meta < subnames.length)
        {
            return getUnlocalizedName() + "_" + subnames[meta];
        }
        return super.getUnlocalizedName(stack);
    }

    private void registerOreDictEntries(String[] oreDictNames)
    {
        if (oreDictNames != null)
        {
            for (int i = 0; i < oreDictNames.length; i++)
            {
                if (!oreDictNames[i].equals(""))
                {
                    OreDictionary.registerOre(oreDictNames[i], new ItemStack(this, 1, i));
                }
            }
        }
    }
}