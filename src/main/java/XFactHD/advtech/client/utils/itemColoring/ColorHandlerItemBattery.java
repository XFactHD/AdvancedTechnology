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

package XFactHD.advtech.client.utils.itemColoring;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;

public class ColorHandlerItemBattery implements IItemColor
{
    private static final int[] colors = new int[]{
            0xadbfea, //NiCd
            0xd3d8ec, //NiMh
            0x818ebe, //Pb
            0xb9d6d9  //LiIon
    };

    @Override
    public int getColorFromItemstack(ItemStack stack, int tintIndex)
    {
        return tintIndex == 1 ? colors[stack.getMetadata()] : -1;
    }
}