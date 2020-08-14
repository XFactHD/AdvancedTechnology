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

import XFactHD.advtech.common.blocks.light.TileEntityChemoLuminator;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;

import javax.annotation.Nullable;

public class ColorHandlerBlockChemoLuminator implements IBlockColor, IItemColor
{
    private static final int offColor = 0x000000;
    private static final int baseColor = 0x8eff8c;

    @Override
    public int colorMultiplier(IBlockState state, @Nullable IBlockAccess world, @Nullable BlockPos pos, int tintIndex)
    {
        if (tintIndex != 1) { return -1; }
        if (!state.getValue(PropertyHolder.ACTIVE)) { return offColor; }
        if (world == null || pos == null) { return baseColor; }
        TileEntity te = world.getTileEntity(pos);
        int color = 0;
        if (te instanceof TileEntityChemoLuminator)
        {
            color = ((TileEntityChemoLuminator)te).getColor();
        }
        return color == 0 ? baseColor : color;
    }

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex)
    {
        return tintIndex == 1 ? baseColor : -1;
    }
}