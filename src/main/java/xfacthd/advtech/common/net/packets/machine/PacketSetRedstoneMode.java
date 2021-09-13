package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.data.states.RedstoneMode;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketSetRedstoneMode extends AbstractPacket
{
    private final BlockPos pos;
    private final RedstoneMode mode;

    public PacketSetRedstoneMode(BlockPos pos, RedstoneMode mode)
    {
        this.pos = pos;
        this.mode = mode;
    }

    public PacketSetRedstoneMode(FriendlyByteBuf buffer)
    {
        pos = buffer.readBlockPos();
        mode = RedstoneMode.values()[buffer.readInt()];
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(mode.ordinal());
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            Level level = ctx.get().getSender().level;
            //noinspection deprecation
            if (level.hasChunkAt(pos))
            {
                if (level.getBlockEntity(pos) instanceof BlockEntityMachine be)
                {
                    be.setRedstoneMode(mode);
                }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}