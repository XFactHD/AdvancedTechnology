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

public enum ConType implements IStringSerializable
{
    NONE,
    CABLE,
    BLOCK;

    @Override
    public String getName()
    {
        return toString().toLowerCase(Locale.ENGLISH);
    }

    public static String getStateForIndices(int up, int down, int north, int east, int south, int west)
    {
        return getStateForTypes(values()[up], values()[down], values()[north], values()[east], values()[south], values()[west]);
    }

    public static String getStateForTypes(ConType up, ConType down, ConType north, ConType east, ConType south, ConType west)
    {
        String state = "";
        state += "con_type_d=" + down.getName() + ",";
        state += "con_type_e=" + east.getName() + ",";
        state += "con_type_n=" + north.getName() + ",";
        state += "con_type_s=" + south.getName() + ",";
        state += "con_type_u=" + up.getName() + ",";
        state += "con_type_w=" + west.getName();
        return state;
    }
}