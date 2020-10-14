package xfacthd.advtech.common.net.packets.energy;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.tileentity.storage.TileEntityEnergyCube;

import java.util.function.Supplier;

public class PacketConfigEnergyCubeSide extends AbstractPacket
{
    private BlockPos pos;
    private Side side;
    private int dir;

    public PacketConfigEnergyCubeSide(BlockPos pos, Side side, int dir)
    {
        this.pos = pos;
        this.side = side;
        this.dir = dir;
    }

    public PacketConfigEnergyCubeSide(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(side.ordinal());
        buffer.writeInt(dir);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        pos = buffer.readBlockPos();
        side = Side.values()[buffer.readInt()];
        dir = buffer.readInt();
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
            if (te instanceof TileEntityEnergyCube)
            {
                TileEntityEnergyCube cube = (TileEntityEnergyCube)te;

                SideAccess setting = SideAccess.NONE;
                if (dir == 1) { setting = cube.getNextPortSetting(side); }
                else if (dir == -1) { setting = cube.getPriorPortSetting(side); }
                cube.setSidePort(side, setting);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}