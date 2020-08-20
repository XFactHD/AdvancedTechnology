package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.tileentity.TileEntityInventoryMachine;

import java.util.function.Supplier;

public class PacketConfigureSide extends AbstractPacket
{
    private BlockPos pos;
    private Side side;
    private int dir;

    public PacketConfigureSide(BlockPos pos, Side side, int dir)
    {
        this.pos = pos;
        this.side = side;
        this.dir = dir;
    }

    public PacketConfigureSide(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(side != null ? side.ordinal() : -1);
        buffer.writeInt(dir);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        pos = buffer.readBlockPos();

        int ord = buffer.readInt();
        side = ord == -1 ? null : Side.values()[ord];

        dir = buffer.readInt();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            World world = ctx.get().getSender().world;
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityInventoryMachine)
            {
                TileEntityInventoryMachine machine = (TileEntityInventoryMachine)te;

                if (side != null)
                {
                    SideAccess setting = SideAccess.NONE;
                    if (dir == 1) { setting = machine.getNextPortSetting(side); }
                    else if (dir == -1) { setting = machine.getPriorPortSetting(side); }
                    machine.setSidePort(side, setting);
                }
                else
                {
                    for (Side side : Side.values())
                    {
                        machine.setSidePort(side, SideAccess.NONE);
                    }
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}