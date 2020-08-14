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

package XFactHD.advtech.common.items.material;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.items.ItemBase;
import XFactHD.advtech.common.utils.utilClasses.MachineTier;

import java.util.Locale;

public class ItemUpgrade extends ItemBase
{
    public ItemUpgrade()
    {
        super("itemUpgrade", 1, AdvancedTechnology.creativeTab, EnumType.getAsStringArray());
    }

    public enum EnumType
    {
        SPEED_I(MachineTier.IMPROVED),
        SPEED_II(MachineTier.ADVANCED),
        SPEED_III(MachineTier.ULTIMATE),
        CAPACITY_I(MachineTier.IMPROVED),
        CAPACITY_II(MachineTier.ADVANCED),
        CAPACITY_III(MachineTier.ULTIMATE);

        private MachineTier neededTier;

        EnumType(MachineTier neededTier)
        {
            this.neededTier = neededTier;
        }

        public MachineTier getNeededTier()
        {
            return neededTier;
        }

        public static String[] getAsStringArray()
        {
            String[] strings = new String[values().length];
            for (EnumType type : values())
            {
                strings[type.ordinal()] = type.toString().toLowerCase(Locale.ENGLISH);
            }
            return strings;
        }
    }
}