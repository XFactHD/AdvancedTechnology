package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.tileentity.TileEntityProducer;

import java.util.function.Supplier;

public class PacketSwitchActiveOutput extends AbstractPacket
{
    private BlockPos pos;

    public PacketSwitchActiveOutput(BlockPos pos) { this.pos = pos;}

    public PacketSwitchActiveOutput(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer) { buffer.writeBlockPos(pos); }

    @Override
    public void decode(PacketBuffer buffer) { pos = buffer.readBlockPos(); }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            World world = ctx.get().getSender().world;
            if (!world.isAreaLoaded(pos, 0)) { return; }

            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityProducer)
            {
                ((TileEntityProducer) te).switchForceOutput();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}