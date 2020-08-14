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

package XFactHD.advtech.client.utils.blockColoring;

import XFactHD.advtech.common.Content;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class ColorHandlerBlockMetal implements IBlockColor, IItemColor
{
    private static final int[] colors = new int[]{
            0xb4713d, //Copper
            0xe8e8e8, //Tin
            0xdbf0f3, //Silver
            0x818ebe, //Lead
            0xa4a4a4, //Steel
            0xc89042, //Bronze
            0xd3d8ec, //Nickel
            0xb9d6d9, //Lithium
            0x87a6e8, //Cadmium
            0xf1eef5, //Zinc
            0xb3e9f7, //Platinum
            0x383241, //Tungsten
            0xb8c3cf //Neodymium
    };

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int layer)
    {
        return colors[Content.blockMetal.getMetaFromState(state)];
    }

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex)
    {
        return colors[stack.getMetadata()];
    }
}