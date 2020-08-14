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

package XFactHD.advtech.common.utils.helpers;

import XFactHD.advtech.client.gui.*;
import XFactHD.advtech.common.blocks.energy.*;
import XFactHD.advtech.common.blocks.light.*;
import XFactHD.advtech.common.blocks.machine.*;
import XFactHD.advtech.common.blocks.storage.*;
import XFactHD.advtech.common.gui.*;
import XFactHD.advtech.common.utils.Reference;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GuiHandler implements IGuiHandler
{
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID)
        {
            case Reference.GUI_ID_SOLAR_PANEL: return new GuiSolarPanel((TileEntitySolarPanel)te);
            case Reference.GUI_ID_BATTERY_PACK: return new GuiBatteryPack(player, (TileEntityBatteryPack)te);
            case Reference.GUI_ID_CRUSHER: return null;
            case Reference.GUI_ID_QUARRY: return new GuiQuarry(player, (TileEntityQuarry)te);
            case Reference.GUI_ID_CHEMO_LUMINATOR: return new GuiChemoLuminator(player, (TileEntityChemoLuminator)te);
            case Reference.GUI_ID_ELECTRIC_FURNACE: return null;
            case Reference.GUI_ID_ALLOY_FURNACE: return null;
            case Reference.GUI_ID_METAL_PRESS: return null;
            case Reference.GUI_ID_CHEMICAL_REACTION_CHAMBER: return null;
            case Reference.GUI_ID_WIRELESS_FEEDER: return null;
            case Reference.GUI_ID_REFINERY: return null;
            case Reference.GUI_ID_FLUID_TANK: return new GuiFluidTank(player, (TileEntityFluidTank)te);
            default: return null;
        }
    }

    @Override
    public Object getServerGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        switch (ID)
        {
            case Reference.GUI_ID_SOLAR_PANEL: return new ContainerSolarPanel((TileEntitySolarPanel)te);
            case Reference.GUI_ID_BATTERY_PACK: return new ContainerBatteryPack(player, (TileEntityBatteryPack)te);
            case Reference.GUI_ID_CRUSHER: return null;
            case Reference.GUI_ID_QUARRY: return new ContainerQuarry(player, (TileEntityQuarry)te);
            case Reference.GUI_ID_CHEMO_LUMINATOR: return new ContainerChemoLuminator(player, (TileEntityChemoLuminator)te);
            case Reference.GUI_ID_ELECTRIC_FURNACE: return null;
            case Reference.GUI_ID_ALLOY_FURNACE: return null;
            case Reference.GUI_ID_METAL_PRESS: return null;
            case Reference.GUI_ID_CHEMICAL_REACTION_CHAMBER: return null;
            case Reference.GUI_ID_WIRELESS_FEEDER: return null;
            case Reference.GUI_ID_REFINERY: return null;
            case Reference.GUI_ID_FLUID_TANK: return new ContainerFluidTank(player, (TileEntityFluidTank)te);
            default: return null;
        }
    }
}