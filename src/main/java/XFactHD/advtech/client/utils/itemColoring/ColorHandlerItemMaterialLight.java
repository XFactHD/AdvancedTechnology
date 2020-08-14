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

public class ColorHandlerItemMaterialLight implements IItemColor
{
    private static final int[] colors = new int[]{
            0xffffff, //White
            0xd87f33, //Orange
            0xb24cd8, //Magenta
            0x6699d8, //Light Blue
            0xe5e533, //Yellow
            0x7fcc19, //Lime
            0xf27fa5, //Pink
            0x4c7f99, //Cyan
            0x7f3fb2, //Purple
            0x334cb2, //Blue
            0x667f33, //Green
            0x993333  //Red
    };

    @Override
    public int getColorFromItemstack(ItemStack stack, int layer)
    {
        int meta = stack.getMetadata();
        if (meta > 0 && meta < 26 && meta != 13)
        {
            if (meta > 13) { meta -= 14; }
            else { meta -= 1; }
            if (layer == 1)
            {
                return colors[meta];
            }
        }
        else if (meta > 27)
        {
            meta -= 28;
            return colors[meta];
        }
        return -1;
    }
}