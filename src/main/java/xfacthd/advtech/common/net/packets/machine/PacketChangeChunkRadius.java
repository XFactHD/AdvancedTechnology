package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.advtech.common.blockentity.utility.BlockEntityChunkLoader;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketChangeChunkRadius extends AbstractPacket
{
    private final BlockPos pos;
    private final boolean inc;

    public PacketChangeChunkRadius(BlockPos pos, boolean inc)
    {
        this.pos = pos;
        this.inc = inc;
    }

    public PacketChangeChunkRadius(FriendlyByteBuf buffer)
    {
        pos = buffer.readBlockPos();
        inc = buffer.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(inc);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            Level level = ctx.get().getSender().level;
            //noinspection deprecation
            if (!level.hasChunkAt(pos)) { return; }

            if (level.getBlockEntity(pos) instanceof BlockEntityChunkLoader be)
            {
                int radius = be.getRadius();
                radius = inc ? radius + 1 : radius - 1;
                be.setRadius(radius);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}