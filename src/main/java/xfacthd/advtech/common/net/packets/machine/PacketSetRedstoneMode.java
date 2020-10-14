package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.common.data.states.RedstoneMode;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.tileentity.TileEntityMachine;

import java.util.function.Supplier;

public class PacketSetRedstoneMode extends AbstractPacket
{
    private BlockPos pos;
    private RedstoneMode mode;

    public PacketSetRedstoneMode(BlockPos pos, RedstoneMode mode)
    {
        this.pos = pos;
        this.mode = mode;
    }

    public PacketSetRedstoneMode(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(mode.ordinal());
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        pos = buffer.readBlockPos();
        mode = RedstoneMode.values()[buffer.readInt()];
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            World world = ctx.get().getSender().world;
            //noinspection deprecation
            if (world.isBlockLoaded(pos))
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityMachine)
                {
                    ((TileEntityMachine)te).setRedstoneMode(mode);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}