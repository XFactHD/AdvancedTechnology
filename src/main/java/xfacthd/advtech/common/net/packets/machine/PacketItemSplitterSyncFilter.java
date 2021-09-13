package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xfacthd.advtech.client.util.ClientHelper;
import xfacthd.advtech.common.menu.utility.ContainerMenuItemSplitter;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketItemSplitterSyncFilter extends AbstractPacket
{
    private final int containerId;
    private final int slot;
    private final ItemStack stack;

    public PacketItemSplitterSyncFilter(int containerId, int slot, ItemStack stack)
    {
        this.containerId = containerId;
        this.slot = slot;
        this.stack = stack;
    }

    public PacketItemSplitterSyncFilter(FriendlyByteBuf buffer)
    {
        containerId = buffer.readInt();
        slot = buffer.readByte();
        stack = buffer.readItem();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(containerId);
        buffer.writeByte(slot);
        buffer.writeItem(stack);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            AbstractContainerMenu cont = ClientHelper.getClientPlayer().containerMenu;
            if (cont instanceof ContainerMenuItemSplitter splitter && cont.containerId == containerId)
            {
                splitter.receiveFilterConfig(slot, stack);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}