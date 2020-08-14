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
import XFactHD.advtech.common.utils.utilClasses.ConType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class StateMapperBlockFluidPipe extends StateMapperBase
{
    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        boolean opaque = state.getValue(PropertyHolder.FLUID_PIPE_TYPE).getName().contains("opaque");
        String location = "blockFluidPipe" + (opaque ? "_opaque" : "");
        String variant = ConType.getStateForTypes(
                state.getValue(PropertyHolder.CON_TYPE_U),
                state.getValue(PropertyHolder.CON_TYPE_D),
                state.getValue(PropertyHolder.CON_TYPE_N),
                state.getValue(PropertyHolder.CON_TYPE_E),
                state.getValue(PropertyHolder.CON_TYPE_S),
                state.getValue(PropertyHolder.CON_TYPE_W)
        );
        return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, location), variant);
    }
}