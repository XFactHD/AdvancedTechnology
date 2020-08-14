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

public class ColorHandlerItemMaterial implements IItemColor
{
    private static final int[] colors = new int[]{
            0xb4713d, //Copper
            0xe8e8e8, //Tin
            0xdbf0f3, //Silver
            0x818ebe, //Lead
            0x8b8b8b, //Steel
            0xc89042, //Bronze
            0xd3d8ec, //Nickel
            0xb9d6d9, //Lithium
            0x87a6e8, //Cadmium
            0xf1eef5, //Zinc
            0xb3e9f7, //Platinum
            0x383241, //Tungsten
            0xb8c3cf, //Neodymium
            0xb6b6b6, //Iron
            0xdede00, //Gold
            0x9fa100, //Sulfur
            0x2b2b2b, //Coal
            0xb31249, //ZnCdS-Mixture
            0x4f4f4f  //Coke
    };

    @Override
    public int getColorFromItemstack(ItemStack stack, int layer)
    {
        int meta = stack.getMetadata();
        if (meta < 57)
        {
            if (meta > 41) { meta -= 42; }
            else if (meta > 26) { meta -= 27; }
            else if (meta > 12) { meta -= 13; }
            return colors[meta];
        }
        else if (meta < 63 && layer == 1)
        {
            meta -= 57;
            if (meta == 3) { meta = 4; }
            else if (meta == 4) { meta = 11; }
            else if (meta == 5) { meta = 14; }
            return colors[meta];
        }
        else if (meta == 63) { return colors[15]; }
        else if (meta == 65) { return colors[16]; }
        else if (meta == 72) { return colors[17]; }
        else if (meta == 73 && layer == 1) { return colors[0]; }
        else if (meta == 76 && layer == 1) { return colors[11]; }
        else if (meta == 77 || meta == 78) { return colors[18]; }

        return -1;
    }
}