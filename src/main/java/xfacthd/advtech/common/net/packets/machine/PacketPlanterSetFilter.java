package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.advtech.common.blockentity.machine.BlockEntityPlanter;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketPlanterSetFilter extends AbstractPacket
{
    private final BlockPos pos;
    private final boolean clear;

    public PacketPlanterSetFilter(BlockPos pos, boolean clear)
    {
        this.pos = pos;
        this.clear = clear;
    }

    public PacketPlanterSetFilter(FriendlyByteBuf buffer)
    {
        pos = buffer.readBlockPos();
        clear = buffer.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(clear);
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

            if (level.getBlockEntity(pos) instanceof BlockEntityPlanter be)
            {
                be.configureFilter(clear);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}