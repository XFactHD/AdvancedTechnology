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

package XFactHD.advtech.common;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.net.*;
import XFactHD.advtech.common.utils.Sounds;
import XFactHD.advtech.common.utils.helpers.GuiHandler;
import XFactHD.advtech.common.utils.world.WirelessWorldData;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;

public class CommonProxy
{
    public void preInit(FMLPreInitializationEvent event)
    {
        MinecraftForge.EVENT_BUS.register(new EventHandler());
        MinecraftForge.EVENT_BUS.register(WirelessWorldData.class);
        NetworkRegistry.INSTANCE.registerGuiHandler(AdvancedTechnology.INSTANCE, new GuiHandler());
        AdvancedTechnology.NET.registerMessageForServerSide(PacketHandleButtonQuarry.class, PacketHandleButtonQuarry.Handler.class, 0);
        AdvancedTechnology.NET.registerMessageForServerSide(PacketBatteryPackSideSetting.class, PacketBatteryPackSideSetting.Handler.class, 1);
        Content.preInit();
        Sounds.register();
    }

    public void init(FMLInitializationEvent event)
    {
        Content.init();
    }

    public void postInit(FMLPostInitializationEvent event)
    {
        Content.postInit();
    }
}