package xfacthd.advtech.common.net;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.net.packets.container.*;
import xfacthd.advtech.common.net.packets.debug.*;
import xfacthd.advtech.common.net.packets.energy.*;
import xfacthd.advtech.common.net.packets.machine.*;
import xfacthd.advtech.common.net.packets.tileentity.*;

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
        CHANNEL.registerMessage(idx++, PacketConfigureSide.class,        PacketConfigureSide::encode,        PacketConfigureSide::new,        PacketConfigureSide::handle);
        CHANNEL.registerMessage(idx++, PacketSwitchActiveOutput.class,   PacketSwitchActiveOutput::encode,   PacketSwitchActiveOutput::new,   PacketSwitchActiveOutput::handle);
        CHANNEL.registerMessage(idx++, PacketConfigEnergyCubeSide.class, PacketConfigEnergyCubeSide::encode, PacketConfigEnergyCubeSide::new, PacketConfigEnergyCubeSide::handle);
        CHANNEL.registerMessage(idx++, PacketChangeChunkRadius.class,    PacketChangeChunkRadius::encode,    PacketChangeChunkRadius::new,    PacketChangeChunkRadius::handle);
        CHANNEL.registerMessage(idx++, PacketPlanterSetFilter.class,     PacketPlanterSetFilter::encode,     PacketPlanterSetFilter::new,     PacketPlanterSetFilter::handle);
        CHANNEL.registerMessage(idx++, PacketSetRedstoneMode.class,      PacketSetRedstoneMode::encode,      PacketSetRedstoneMode::new,      PacketSetRedstoneMode::handle);

        //Server to client packets
        CHANNEL.registerMessage(idx++, PacketSyncByteHolder.class,      PacketSyncByteHolder::encode,       PacketSyncByteHolder::new,      PacketSyncByteHolder::handle);
        CHANNEL.registerMessage(idx++, PacketSyncIntHolder.class,       PacketSyncIntHolder::encode,        PacketSyncIntHolder::new,       PacketSyncIntHolder::handle);
        CHANNEL.registerMessage(idx++, PacketSyncBoolHolder.class,      PacketSyncBoolHolder::encode,       PacketSyncBoolHolder::new,      PacketSyncBoolHolder::handle);
        CHANNEL.registerMessage(idx++, PacketDebugForcedChunks.class,   PacketDebugForcedChunks::encode,    PacketDebugForcedChunks::new,   PacketDebugForcedChunks::handle);
        CHANNEL.registerMessage(idx++, PacketUpdateFluid.class,         PacketUpdateFluid::encode,          PacketUpdateFluid::new,         PacketUpdateFluid::handle);
        CHANNEL.registerMessage(idx++, PacketTileSync.class,            PacketTileSync::encode,             PacketTileSync::new,            PacketTileSync::handle);
    }

    public static void sendToServer(AbstractPacket packet) { CHANNEL.sendToServer(packet); }

    public static void sendToPlayer(AbstractPacket packet, ServerPlayerEntity player)
    {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), packet);
    }

    public static void sendToPlayersTrackingChunk(AbstractPacket packet, Chunk chunk)
    {
        CHANNEL.send(PacketDistributor.TRACKING_CHUNK.with(() -> chunk), packet);
    }

    public static void sendToAllPlayers(AbstractPacket packet) { CHANNEL.send(PacketDistributor.ALL.noArg(), packet); }
}