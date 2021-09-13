package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xfacthd.advtech.common.blockentity.utility.BlockEntityItemSplitter;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketItemSplitterSetFilter extends AbstractPacket
{
    private final BlockPos pos;
    private final int slot;
    private final ItemStack stack;

    public PacketItemSplitterSetFilter(BlockPos pos, int slot, ItemStack stack)
    {
        this.pos = pos;
        this.slot = slot;
        this.stack = stack;
    }

    public PacketItemSplitterSetFilter(FriendlyByteBuf buffer)
    {
        pos = buffer.readBlockPos();
        slot = buffer.readByte();
        stack = buffer.readItem();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeByte(slot);
        buffer.writeItem(stack);
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

            if (level.getBlockEntity(pos) instanceof BlockEntityItemSplitter be)
            {
                be.setFilter(slot, stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}