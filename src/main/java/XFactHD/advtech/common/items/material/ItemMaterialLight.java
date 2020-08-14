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

package XFactHD.advtech.common.items.material;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.items.ItemBase;

public class ItemMaterialLight extends ItemBase
{
    public ItemMaterialLight()
    {
        super("itemMaterialLight", 64, AdvancedTechnology.creativeTab, EnumType.getAsStringArray());
    }

    public enum EnumType
    {
        FILAMENT("Filament"),
        LAMP_WHITE("LampWhite"),
        LAMP_ORANGE("LampOrange"),
        LAMP_MAGENTA("LampMagenta"),
        LAMP_LIGHTBLUE("LampLightBlue"),
        LAMP_YELLOW("LampYellow"),
        LAMP_LIME("LampLime"),
        LAMP_PINK("LampPink"),
        LAMP_CYAN("LampCyan"),
        LAMP_PURPLE("LampPurple"),
        LAMP_BLUE("LampBlue"),
        LAMP_GREEN("LampGreen"),
        LAMP_RED("LampRed"),

        LED_ASSEMBLY("LEDAssembly"), //made from silicon, carbon (coal dust)
        LED_WHITE("LEDWhite"),
        LED_ORANGE("LEDOrange"),
        LED_MAGENTA("LEDMagenta"),
        LED_LIGHTBLUE("LEDLightBlue"),
        LED_YELLOW("LEDYellow"),
        LED_LIME("LEDLime"),
        LED_PINK("LEDPink"),
        LED_CYAN("LEDCyan"),
        LED_PURPLE("LEDPurple"),
        LED_BLUE("LEDBlue"),
        LED_GREEN("LEDGreen"),
        LED_RED("LEDRed"),

        LASER_DIODE_ASSEMBLY("LaserDiodeAssembly"), //made from zinc, cadmium, sulfur
        LASER_DIODE("LaserDiode"),

        LED_CLUSTER_WHITE("LEDClusterWhite"),
        LED_CLUSTER_ORANGE("LEDClusterOrange"),
        LED_CLUSTER_MAGENTA("LEDClusterMagenta"),
        LED_CLUSTER_LIGHTBLUE("LEDClusterLightBlue"),
        LED_CLUSTER_YELLOW("LEDClusterYellow"),
        LED_CLUSTER_LIME("LEDClusterLime"),
        LED_CLUSTER_PINK("LEDClusterPink"),
        LED_CLUSTER_CYAN("LEDClusterCyan"),
        LED_CLUSTER_PURPLE("LEDClusterPurple"),
        LED_CLUSTER_BLUE("LEDClusterBlue"),
        LED_CLUSTER_GREEN("LEDClusterGreen"),
        LED_CLUSTER_RED("LEDClusterRed");

        private String name;

        EnumType(String name)
        {
            this.name = name;
        }

        public static String[] getAsStringArray()
        {
            String[] strings = new String[values().length];
            for (EnumType type : values())
            {
                strings[type.ordinal()] = type.name;
            }
            return strings;
        }

        public String getName()
        {
            return name;
        }

        public int getMeta()
        {
            return ordinal();
        }
    }
}