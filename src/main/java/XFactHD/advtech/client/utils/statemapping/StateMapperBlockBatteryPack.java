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

package XFactHD.advtech.client.utils.statemapping;

import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class StateMapperBlockBatteryPack extends StateMapperBase
{
    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        ResourceLocation resLoc = new ResourceLocation(Reference.MOD_ID, "blockBatteryPack_" + state.getValue(PropertyHolder.BATTERY_TYPE).getName());
        String variant = "";
        variant +=  "set_d=" + state.getValue(PropertyHolder.SETTING_D).getName();
        variant += ",set_e=" + state.getValue(PropertyHolder.SETTING_E).getName();
        variant += ",set_n=" + state.getValue(PropertyHolder.SETTING_N).getName();
        variant += ",set_s=" + state.getValue(PropertyHolder.SETTING_S).getName();
        variant += ",set_u=" + state.getValue(PropertyHolder.SETTING_U).getName();
        variant += ",set_w=" + state.getValue(PropertyHolder.SETTING_W).getName();
        return new ModelResourceLocation(resLoc, variant);
    }
}