package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.tileentity.machine.TileEntityPlanter;

import java.util.function.Supplier;

public class PacketPlanterSetFilter extends AbstractPacket
{
    private BlockPos pos;
    private boolean clear;

    public PacketPlanterSetFilter(BlockPos pos, boolean clear)
    {
        this.pos = pos;
        this.clear = clear;
    }

    public PacketPlanterSetFilter(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(clear);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        pos = buffer.readBlockPos();
        clear = buffer.readBoolean();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            World world = ctx.get().getSender().world;
            if (!world.isAreaLoaded(pos, 0)) { return; }

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityPlanter)
            {
                ((TileEntityPlanter) te).configureFilter(clear);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}