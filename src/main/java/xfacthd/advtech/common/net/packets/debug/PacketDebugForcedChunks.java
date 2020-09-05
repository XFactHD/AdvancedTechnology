package xfacthd.advtech.common.net.packets.debug;

import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.client.event.DebugRenderHandler;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketDebugForcedChunks extends AbstractPacket
{
    private LongSet chunks;

    private PacketDebugForcedChunks(LongSet chunks) { this.chunks = chunks;}

    public PacketDebugForcedChunks(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeLongArray(chunks.toLongArray()); }

    @Override
    public void decode(PacketBuffer buffer) { chunks = new LongOpenHashSet(buffer.readLongArray(null)); }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> DebugRenderHandler.onForcedChunksPacket(chunks));
        ctx.get().setPacketHandled(true);
    }

    public static void sendToClient(ServerWorld world)
    {
        if (!FMLEnvironment.production)
        {
            NetworkHandler.sendToAllPlayers(new PacketDebugForcedChunks(world.getForcedChunks()));
        }
    }
}