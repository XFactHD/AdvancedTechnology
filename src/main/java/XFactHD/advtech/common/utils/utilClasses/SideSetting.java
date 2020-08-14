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

package XFactHD.advtech.common.utils.utilClasses;

import net.minecraft.util.IStringSerializable;

import java.util.Locale;

public enum SideSetting implements IStringSerializable
{
    NONE,
    IN,
    OUT;

    public SideSetting next()
    {
        switch (this)
        {
            case NONE: return IN;
            case IN:   return OUT;
            case OUT:  return NONE;
            default: throw new UnsupportedOperationException("Enum SideSetting got fucked!");
        }
    }

    @Override
    public String getName()
    {
        return toString().toLowerCase(Locale.ENGLISH);
    }

    public static String getStateForIndices(int up, int down, int north, int east, int south, int west)
    {
        String variant = "";
        variant += "set_d=" + values()[down].getName() + ",";
        variant += "set_e=" + values()[east].getName() + ",";
        variant += "set_n=" + values()[north].getName() + ",";
        variant += "set_s=" + values()[south].getName() + ",";
        variant += "set_u=" + values()[up].getName() + ",";
        variant += "set_w=" + values()[west].getName();
        return variant;
    }
}