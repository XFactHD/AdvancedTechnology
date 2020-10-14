package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.tileentity.utility.TileEntityChunkLoader;

import java.util.function.Supplier;

public class PacketChangeChunkRadius extends AbstractPacket
{
    private BlockPos pos;
    private boolean inc;

    public PacketChangeChunkRadius(BlockPos pos, boolean inc)
    {
        this.pos = pos;
        this.inc = inc;
    }

    public PacketChangeChunkRadius(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(inc);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        pos = buffer.readBlockPos();
        inc = buffer.readBoolean();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            World world = ctx.get().getSender().world;
            //noinspection deprecation
            if (!world.isBlockLoaded(pos)) { return; }

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityChunkLoader)
            {
                int radius = ((TileEntityChunkLoader) te).getRadius();
                radius = inc ? radius + 1 : radius - 1;
                ((TileEntityChunkLoader) te).setRadius(radius);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}