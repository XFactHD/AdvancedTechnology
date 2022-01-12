package xfacthd.advtech.common.net.packets.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.network.NetworkEvent;
import xfacthd.advtech.client.util.ClientHelper;
import xfacthd.advtech.common.menu.AdvancedContainerMenu;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketSyncIntSlot extends AbstractPacket
{
    private final int windowId;
    private final int property;
    private final int value;

    public PacketSyncIntSlot(int windowId, int property, int value)
    {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

    public PacketSyncIntSlot(FriendlyByteBuf buffer)
    {
        windowId = buffer.readUnsignedByte();
        property = buffer.readShort();
        value = buffer.readInt();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeByte(windowId);
        buffer.writeShort(property);
        buffer.writeInt(value);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            AbstractContainerMenu container = ClientHelper.getClientPlayer().containerMenu;
            if (container != null && container.containerId == windowId && container instanceof AdvancedContainerMenu menu)
            {
                menu.handleIntSlot(property, value);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}