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

package XFactHD.advtech.common.utils;

import java.util.Locale;

public class Utils
{
    public static int getOpposite(int index, int range)
    {
        return range - index;
    }

    public static String toTitleCase(String input)
    {
        String first = input.substring(0, 1);
        String rest = input.substring(1, input.length());
        return first.toUpperCase(Locale.ENGLISH) + rest;
    }
}