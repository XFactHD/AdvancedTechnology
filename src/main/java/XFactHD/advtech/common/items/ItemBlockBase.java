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

import XFactHD.advtech.common.blocks.BlockBase;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;

public class ItemBlockBase extends ItemBlock
{
    protected BlockBase block;

    public ItemBlockBase(BlockBase block)
    {
        super(block);
        this.block = block;
        setHasSubtypes(block.getSubnames() != null);
        setRegistryName(block.getRegistryName());
        setUnlocalizedName(block.getUnlocalizedName());
    }

    @Override
    public String getUnlocalizedName(ItemStack stack)
    {
        if (block.getSubnames() != null && stack.getMetadata() < block.getSubnames().length)
        {
            return getUnlocalizedName() + "_" + block.getSubnames()[stack.getMetadata()];
        }
        return super.getUnlocalizedName(stack);
    }

    @Override
    public int getMetadata(int damage)
    {
        return block.getSubnames() != null ? damage : 0;
    }
}