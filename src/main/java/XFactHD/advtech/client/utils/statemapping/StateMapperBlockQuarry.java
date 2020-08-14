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

import XFactHD.advtech.common.blocks.machine.BlockQuarry;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class StateMapperBlockQuarry extends StateMapperBase
{
    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        if (state.getValue(PropertyHolder.QUARRY_TYPE) == BlockQuarry.EnumType.MACHINE)
        {
            return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "blockQuarry"), "normal");
        }
        String variant = "";
        variant += "con_u=" + state.getValue(PropertyHolder.CONNECTED_U);
        variant += "con_d=" + state.getValue(PropertyHolder.CONNECTED_D);
        variant += "con_n=" + state.getValue(PropertyHolder.CONNECTED_N);
        variant += "con_e=" + state.getValue(PropertyHolder.CONNECTED_E);
        variant += "con_s=" + state.getValue(PropertyHolder.CONNECTED_S);
        variant += "con_w=" + state.getValue(PropertyHolder.CONNECTED_W);
        return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "blockQuarryFrame"), variant);
    }
}