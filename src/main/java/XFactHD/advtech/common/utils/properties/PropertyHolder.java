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

package XFactHD.advtech.common.utils.properties;

import XFactHD.advtech.common.blocks.energy.*;
import XFactHD.advtech.common.blocks.light.BlockGasLantern;
import XFactHD.advtech.common.blocks.machine.*;
import XFactHD.advtech.common.blocks.material.*;
import XFactHD.advtech.common.blocks.storage.*;
import XFactHD.advtech.common.blocks.transport.*;
import XFactHD.advtech.common.items.energy.ItemBattery;
import XFactHD.advtech.common.utils.utilClasses.*;
import net.minecraft.block.properties.*;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.EnumFacing;

public class PropertyHolder
{
    public static final PropertyDirection FACING_FULL     = PropertyDirection.create("facing");
    public static final PropertyDirection FACING_CARDINAL = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

    public static final PropertyEnum<BlockMetal.EnumType>      METAL_TYPE       = PropertyEnum.create("type", BlockMetal.EnumType.class);
    public static final PropertyEnum<BlockOre.EnumType>        ORE_TYPE         = PropertyEnum.create("type", BlockOre.EnumType.class);
    public static final PropertyEnum<BlockStorage.EnumType>    STORAGE_TYPE     = PropertyEnum.create("type", BlockStorage.EnumType.class);
    public static final PropertyEnum<BlockSolarPanel.EnumType> SOLAR_PANEL_TYPE = PropertyEnum.create("type", BlockSolarPanel.EnumType.class);
    public static final PropertyEnum<ItemBattery.EnumType>     BATTERY_TYPE     = PropertyEnum.create("type", ItemBattery.EnumType.class);
    public static final PropertyEnum<BlockCable.EnumType>      CABLE_TYPE       = PropertyEnum.create("type", BlockCable.EnumType.class);
    public static final PropertyEnum<BlockQuarry.EnumType>     QUARRY_TYPE      = PropertyEnum.create("type", BlockQuarry.EnumType.class);
    public static final PropertyEnum<BlockFluidPipe.EnumType>  FLUID_PIPE_TYPE  = PropertyEnum.create("type", BlockFluidPipe.EnumType.class);
    public static final PropertyEnum<BlockFluidTank.EnumType>  FLUID_TANK_TYPE  = PropertyEnum.create("type", BlockFluidTank.EnumType.class);
    public static final PropertyEnum<BlockGasLantern.EnumType> GAS_LANTERN_TYPE = PropertyEnum.create("type", BlockGasLantern.EnumType.class);

    public static final PropertyEnum<ConType> CON_TYPE_U = PropertyEnum.create("con_type_u", ConType.class);
    public static final PropertyEnum<ConType> CON_TYPE_D = PropertyEnum.create("con_type_d", ConType.class);
    public static final PropertyEnum<ConType> CON_TYPE_N = PropertyEnum.create("con_type_n", ConType.class);
    public static final PropertyEnum<ConType> CON_TYPE_E = PropertyEnum.create("con_type_e", ConType.class);
    public static final PropertyEnum<ConType> CON_TYPE_S = PropertyEnum.create("con_type_s", ConType.class);
    public static final PropertyEnum<ConType> CON_TYPE_W = PropertyEnum.create("con_type_w", ConType.class);

    public static final PropertyEnum<EnumDyeColor> COLOR = PropertyEnum.create("color", EnumDyeColor.class);

    public static final PropertyEnum<MachineTier> TIER = PropertyEnum.create("tier", MachineTier.class);

    public static final PropertyEnum<TowerLevel> TOWER_LEVEL    = PropertyEnum.create("tower", TowerLevel.class);
    public static final PropertyEnum<TowerLevel> LANTERN_HEIGHT = PropertyEnum.create("height", TowerLevel.class, TowerLevel.BASE, TowerLevel.ONE, TowerLevel.TWO);

    public static final PropertyBool CONNECTED_U = PropertyBool.create("con_u");
    public static final PropertyBool CONNECTED_D = PropertyBool.create("con_d");
    public static final PropertyBool CONNECTED_N = PropertyBool.create("con_n");
    public static final PropertyBool CONNECTED_E = PropertyBool.create("con_e");
    public static final PropertyBool CONNECTED_S = PropertyBool.create("con_s");
    public static final PropertyBool CONNECTED_W = PropertyBool.create("con_w");

    public static final PropertyEnum<SideSetting> SETTING_U = PropertyEnum.create("set_u", SideSetting.class);
    public static final PropertyEnum<SideSetting> SETTING_D = PropertyEnum.create("set_d", SideSetting.class);
    public static final PropertyEnum<SideSetting> SETTING_N = PropertyEnum.create("set_n", SideSetting.class);
    public static final PropertyEnum<SideSetting> SETTING_E = PropertyEnum.create("set_e", SideSetting.class);
    public static final PropertyEnum<SideSetting> SETTING_S = PropertyEnum.create("set_s", SideSetting.class);
    public static final PropertyEnum<SideSetting> SETTING_W = PropertyEnum.create("set_w", SideSetting.class);

    public static final PropertyBool ACTIVE = PropertyBool.create("active");
    public static final PropertyBool AUTO   = PropertyBool.create("auto");
}