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

package XFactHD.advtech;

import XFactHD.advtech.common.CommonProxy;
import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.utils.CreativeTabAdvTech;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.helpers.LogHelper;
//import com.google.common.base.Stopwatch;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

import java.util.concurrent.TimeUnit;

@Mod(modid = Reference.MOD_ID, name = Reference.MOD_NAME, version = Reference.VERSION, dependencies = Reference.DEPENDENCIES)
public class AdvancedTechnology
{
    private static final boolean deobfEnv = (Boolean) Launch.blackboard.get("fml.deobfuscatedEnvironment");

    static
    {
        FluidRegistry.enableUniversalBucket();
        MinecraftForge.EVENT_BUS.register(Content.class);
    }

    @Mod.Instance
    public static AdvancedTechnology INSTANCE;

    @SidedProxy(serverSide = Reference.SERVER_PROXY, clientSide = Reference.CLIENT_PROXY)
    public static CommonProxy proxy;

    @Mod.EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        //final Stopwatch timer = Stopwatch.createStarted();
        LogHelper.info("Hello Minecraft!");
        LogHelper.info("Starting PreInit!");
        if (deobfEnv) { System.getProperties().setProperty("forge.verboseMissingModelLoggingCount", "20"); }
        proxy.preInit(event);
        //LogHelper.info("Finished PreInit after " + timer.elapsed(TimeUnit.MILLISECONDS) + "ms!");
    }

    @Mod.EventHandler
    public void init(FMLInitializationEvent event)
    {
        //final Stopwatch timer = Stopwatch.createStarted();
        LogHelper.info("Starting Init!");
        proxy.init(event);
        //LogHelper.info("Finished Init after " + timer.elapsed(TimeUnit.MILLISECONDS) + "ms!");
    }

    @Mod.EventHandler
    public void postInit(FMLPostInitializationEvent event)
    {
        //final Stopwatch timer = Stopwatch.createStarted();
        LogHelper.info("Starting PostInit!");
        proxy.postInit(event);
        //LogHelper.info("Finished PostInit after " + timer.elapsed(TimeUnit.MILLISECONDS) + "ms!");
    }

    public static final class NET
    {
        public static SimpleNetworkWrapper ADVTECH_NET_WRAPPER = NetworkRegistry.INSTANCE.newSimpleChannel(Reference.MOD_ID);

        public static <REQ extends IMessage, REPLY extends IMessage> void registerMessageForServerSide(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, int id)
        {
            ADVTECH_NET_WRAPPER.registerMessage(handler, message, id, Side.SERVER);
        }

        public static <REQ extends IMessage, REPLY extends IMessage> void registerMessageForClientSide(Class<REQ> message, Class<? extends IMessageHandler<REQ, REPLY>> handler, int id)
        {
            ADVTECH_NET_WRAPPER.registerMessage(handler, message, id, Side.CLIENT);
        }

        public static void sendMessageToServer(IMessage message)
        {
            ADVTECH_NET_WRAPPER.sendToServer(message);
        }

        public static void sendMessageToAllClients(IMessage message)
        {
            ADVTECH_NET_WRAPPER.sendToAll(message);
        }

        public static void sendMessageToClient(IMessage message, EntityPlayer player)
        {
            ADVTECH_NET_WRAPPER.sendTo(message, (EntityPlayerMP) player);
        }

        public static void sendMessageToArea(IMessage message, NetworkRegistry.TargetPoint target)
        {
            ADVTECH_NET_WRAPPER.sendToAllAround(message, target);
        }
    }

    public static CreativeTabAdvTech creativeTab = new CreativeTabAdvTech()
    {
        @Override
        public void prepareTabSorter()
        {

        }
    };
}