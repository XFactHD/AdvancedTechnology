package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketSwitchActiveOutput extends AbstractPacket
{
    private final BlockPos pos;

    public PacketSwitchActiveOutput(BlockPos pos) { this.pos = pos;}

    public PacketSwitchActiveOutput(FriendlyByteBuf buffer) { pos = buffer.readBlockPos(); }

    @Override
    public void encode(FriendlyByteBuf buffer) { buffer.writeBlockPos(pos); }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            //noinspection ConstantConditions
            Level level = ctx.get().getSender().level;

            //noinspection deprecation
            if (!level.hasChunkAt(pos)) { return; }

            if (level.getBlockEntity(pos) instanceof BlockEntityInventoryMachine be)
            {
                be.switchForceOutput();
            }
        });
        ctx.get().setPacketHandled(true);
    }
}