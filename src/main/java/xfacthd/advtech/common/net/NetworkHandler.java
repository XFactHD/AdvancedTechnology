package xfacthd.advtech.common.net;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.net.packets.blockentity.*;
import xfacthd.advtech.common.net.packets.container.*;
import xfacthd.advtech.common.net.packets.energy.*;
import xfacthd.advtech.common.net.packets.machine.*;

public class NetworkHandler
{
    private static final String PROTOCOL_VERSION = "1"; //INFO: change when adding packets due to the indices changing

    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(AdvancedTechnology.MODID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    @SuppressWarnings("UnusedAssignment")
    public static void initPackets()
    {
        int idx = 0;

        //Client to server packets
        CHANNEL.registerMessage(idx++, PacketConfigureSide.class,         PacketConfigureSide::encode,         PacketConfigureSide::new,         PacketConfigureSide::handle);
        CHANNEL.registerMessage(idx++, PacketSwitchActiveOutput.class,    PacketSwitchActiveOutput::encode,    PacketSwitchActiveOutput::new,    PacketSwitchActiveOutput::handle);
        CHANNEL.registerMessage(idx++, PacketConfigEnergyCubeSide.class,  PacketConfigEnergyCubeSide::encode,  PacketConfigEnergyCubeSide::new,  PacketConfigEnergyCubeSide::handle);
        CHANNEL.registerMessage(idx++, PacketChangeChunkRadius.class,     PacketChangeChunkRadius::encode,     PacketChangeChunkRadius::new,     PacketChangeChunkRadius::handle);
        CHANNEL.registerMessage(idx++, PacketPlanterSetFilter.class,      PacketPlanterSetFilter::encode,      PacketPlanterSetFilter::new,      PacketPlanterSetFilter::handle);
        CHANNEL.registerMessage(idx++, PacketSetRedstoneMode.class,       PacketSetRedstoneMode::encode,       PacketSetRedstoneMode::new,       PacketSetRedstoneMode::handle);
        CHANNEL.registerMessage(idx++, PacketItemSplitterSetFilter.class, PacketItemSplitterSetFilter::encode, PacketItemSplitterSetFilter::new, PacketItemSplitterSetFilter::handle);

        //Server to client packets
        CHANNEL.registerMessage(idx++, PacketSyncByteSlot.class,           PacketSyncByteSlot::encode,           PacketSyncByteSlot::new,           PacketSyncByteSlot::handle);
        CHANNEL.registerMessage(idx++, PacketSyncIntSlot.class,            PacketSyncIntSlot::encode,            PacketSyncIntSlot::new,            PacketSyncIntSlot::handle);
        CHANNEL.registerMessage(idx++, PacketSyncBoolSlot.class,           PacketSyncBoolSlot::encode,           PacketSyncBoolSlot::new,           PacketSyncBoolSlot::handle);
        //CHANNEL.registerMessage(idx++, PacketDebugForcedChunks.class,      PacketDebugForcedChunks::encode,      PacketDebugForcedChunks::new,      PacketDebugForcedChunks::handle);
        CHANNEL.registerMessage(idx++, PacketUpdateFluid.class,            PacketUpdateFluid::encode,            PacketUpdateFluid::new,            PacketUpdateFluid::handle);
        CHANNEL.registerMessage(idx++, PacketTileSync.class,               PacketTileSync::encode,               PacketTileSync::new,               PacketTileSync::handle);
        CHANNEL.registerMessage(idx++, PacketItemSplitterSyncFilter.class, PacketItemSplitterSyncFilter::encode, PacketItemSplitterSyncFilter::new, PacketItemSplitterSyncFilter::handle);
    }

    public static void sendToServer(AbstractPacket packet) { CHANNEL.sendToServer(packet); }

    public static void sendToPlayer(AbstractPacket packet, ServerPlayer player)
    {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToPlayersTrackingChunk(AbstractPacket packet, LevelChunk chunk)
    {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }

    public static void sendToAllPlayers(AbstractPacket packet) { CHANNEL.send(PacketDistributor.ALL.noArg(), packet); }
}