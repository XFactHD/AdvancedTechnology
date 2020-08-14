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

package XFactHD.advtech.client.utils.blockColoring;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class ColorHandlerBlockFluidTank implements IBlockColor, IItemColor
{
    private int[] colors = new int[]
            {
                    0xc89042, //Bronze
                    0xa4a4a4, //Steel
                    0xd3d8ec, //Nickel
                    0x383241  //Tungsten
            };

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex)
    {
        if (tintIndex == 1)
        {
            return colors[state.getBlock().getMetaFromState(state)];
        }
        return -1;
    }

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex)
    {
        if (tintIndex == 1)
        {
            return colors[stack.getMetadata()];
        }
        return -1;
    }
}