package xfacthd.advtech.common.net.packets.container;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xfacthd.advtech.client.util.ClientHelper;
import xfacthd.advtech.common.menu.AdvancedContainerMenu;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketSyncBoolSlot extends AbstractPacket
{
    private final int windowId;
    private final int property;
    private final boolean value;

    public PacketSyncBoolSlot(int windowId, int property, boolean value)
    {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

    public PacketSyncBoolSlot(FriendlyByteBuf buffer)
    {
        windowId = buffer.readUnsignedByte();
        property = buffer.readShort();
        value = buffer.readBoolean();
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeByte(windowId);
        buffer.writeShort(property);
        buffer.writeBoolean(value);
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            AbstractContainerMenu container = ClientHelper.getClientPlayer().containerMenu;
            if (container != null && container.containerId == windowId && container instanceof AdvancedContainerMenu menu)
            {
                menu.handleBoolSlot(property, value);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}