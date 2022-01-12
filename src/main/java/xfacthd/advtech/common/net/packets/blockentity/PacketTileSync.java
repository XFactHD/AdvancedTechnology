package xfacthd.advtech.common.net.packets.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.advtech.client.util.ClientHelper;
import xfacthd.advtech.common.blockentity.BlockEntityBase;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketTileSync extends AbstractPacket
{
    private final BlockEntityBase blockEntity;
    private final BlockPos pos;
    private final FriendlyByteBuf buffer;

    public PacketTileSync(BlockEntityBase blockEntity)
    {
        this.blockEntity = blockEntity;
        this.pos = blockEntity.getBlockPos();
        this.buffer = null;
    }

    public PacketTileSync(FriendlyByteBuf buffer)
    {
        this.blockEntity = null;
        this.pos = buffer.readBlockPos();
        this.buffer = buffer;
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(blockEntity.getBlockPos());
        blockEntity.writeSyncPacket(buffer); //TODO: move writing to the buffer to the constructor (for details see DataScreen impl)
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Level level = ClientHelper.getClientLevel();
            //noinspection deprecation
            if (level.hasChunkAt(pos))
            {
                if (level.getBlockEntity(pos) instanceof BlockEntityBase be)
                {
                    be.handleSyncPacket(buffer);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}