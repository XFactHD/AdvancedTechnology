package xfacthd.advtech.common.net.packets.energy;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.blockentity.storage.BlockEntityEnergyCube;

import java.util.function.Supplier;

public class PacketConfigEnergyCubeSide extends AbstractPacket
{
    private final BlockPos pos;
    private final Side side;
    private final int dir;

    public PacketConfigEnergyCubeSide(BlockPos pos, Side side, int dir)
    {
        this.pos = pos;
        this.side = side;
        this.dir = dir;
    }

    public PacketConfigEnergyCubeSide(FriendlyByteBuf buffer)
    {
        pos = buffer.readBlockPos();
        side = Side.values()[buffer.readInt()];
        dir = buffer.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(side.ordinal());
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

            if (level.getBlockEntity(pos) instanceof BlockEntityEnergyCube cube)
            {
                SideAccess setting = SideAccess.NONE;
                if (dir == 1) { setting = cube.getNextPortSetting(side); }
                else if (dir == -1) { setting = cube.getPriorPortSetting(side); }
                cube.setSidePort(side, setting);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}