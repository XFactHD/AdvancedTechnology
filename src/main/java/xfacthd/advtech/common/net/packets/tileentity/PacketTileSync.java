package xfacthd.advtech.common.net.packets.tileentity;

import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.tileentity.TileEntityBase;

import java.util.function.Supplier;

public class PacketTileSync extends AbstractPacket
{
    private TileEntityBase tile;
    private BlockPos pos;
    private PacketBuffer buffer;

    public PacketTileSync(TileEntityBase tile) { this.tile = tile; }

    public PacketTileSync(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeBlockPos(tile.getPos());
        tile.writeSyncPacket(buffer);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        pos = buffer.readBlockPos();
        this.buffer = buffer;
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            World world = AdvancedTechnology.SIDED_HELPER.getWorld();
            //noinspection deprecation
            if (world.isBlockLoaded(pos))
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityBase)
                {
                    ((TileEntityBase)te).handleSyncPacket(buffer);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}