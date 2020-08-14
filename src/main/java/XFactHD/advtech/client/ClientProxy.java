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

package XFactHD.advtech.client;

import XFactHD.advtech.client.utils.event.RenderBlockHighlightEventHandler;
import XFactHD.advtech.client.utils.ModelManager;
import XFactHD.advtech.client.utils.TextureManager;
import XFactHD.advtech.common.CommonProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy
{
    @Override
    public void preInit(FMLPreInitializationEvent event)
    {
        super.preInit(event);
        MinecraftForge.EVENT_BUS.register(new RenderBlockHighlightEventHandler());
        MinecraftForge.EVENT_BUS.register(ModelManager.INSTANCE);
        MinecraftForge.EVENT_BUS.register(TextureManager.INSTANCE);
        ClientManager.registerStateMappers();
        ClientManager.registerBasicModels();
        ClientManager.registerAdvancedModels();
        ClientManager.registerRenderers();
    }

    @Override
    public void init(FMLInitializationEvent event)
    {
        super.init(event);
        ClientManager.registerColorHandlers();
    }

    @Override
    public void postInit(FMLPostInitializationEvent event)
    {
        super.postInit(event);
    }
}