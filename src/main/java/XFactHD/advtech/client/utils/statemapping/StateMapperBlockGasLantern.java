/*  Copyright (C) <2016>  <XFactHD, DrakoAlcarus>

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

import XFactHD.advtech.common.blocks.light.BlockGasLantern;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.utilClasses.TowerLevel;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class StateMapperBlockGasLantern extends StateMapperBase
{
    @Override
    protected ModelResourceLocation getModelResourceLocation(IBlockState state)
    {
        if (state.getValue(PropertyHolder.GAS_LANTERN_TYPE) == BlockGasLantern.EnumType.WALLMOUNT)
        {
            String variant = "active=" + state.getValue(PropertyHolder.ACTIVE) + ",facing=" + state.getValue(PropertyHolder.FACING_FULL).getName();
            return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "blockGasLanternWall"), variant);
        }
        else
        {
            if (state.getValue(PropertyHolder.LANTERN_HEIGHT) == TowerLevel.TWO)
            {
                String variant = "active=" + state.getValue(PropertyHolder.ACTIVE) + ",auto=" + state.getValue(PropertyHolder.AUTO);
                return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "blockGasLantern"), variant);
            }
            else
            {
                String variant = "level=" + (state.getValue(PropertyHolder.LANTERN_HEIGHT) == TowerLevel.BASE ? "bottom" : "top");
                return new ModelResourceLocation(new ResourceLocation(Reference.MOD_ID, "blockGasLanternStand"), variant);
            }
        }
    }
}