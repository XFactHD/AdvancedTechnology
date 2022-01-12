package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketConfigureSide extends AbstractPacket
{
    private final BlockPos pos;
    private final Side side;
    private final int dir;

    public PacketConfigureSide(BlockPos pos, Side side, int dir)
    {
        this.pos = pos;
        this.side = side;
        this.dir = dir;
    }

    public PacketConfigureSide(FriendlyByteBuf buffer)
    {
        pos = buffer.readBlockPos();

        int ord = buffer.readInt();
        side = ord == -1 ? null : Side.values()[ord];

        dir = buffer.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(side != null ? side.ordinal() : -1);
        buffer.writeInt(dir);
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

            if (level.getBlockEntity(pos) instanceof BlockEntityInventoryMachine machine)
            {
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