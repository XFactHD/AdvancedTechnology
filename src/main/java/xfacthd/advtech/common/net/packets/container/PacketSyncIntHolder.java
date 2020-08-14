package xfacthd.advtech.common.net.packets.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.container.AdvancedContainer;
import xfacthd.advtech.common.net.packets.AbstractPacket;

import java.util.function.Supplier;

public class PacketSyncIntHolder extends AbstractPacket
{
    private int windowId;
    private int property;
    private int value;

    public PacketSyncIntHolder(int windowId, int property, int value)
    {
        this.windowId = windowId;
        this.property = property;
        this.value = value;
    }

    public PacketSyncIntHolder(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeByte(windowId);
        buffer.writeShort(property);
        buffer.writeInt(value);
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        windowId = buffer.readUnsignedByte();
        property = buffer.readShort();
        value = buffer.readInt();
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            PlayerEntity player = AdvancedTechnology.SIDED_HELPER.getPlayer();
            Container container = player.openContainer;
            if (container != null && container.windowId == windowId && container instanceof AdvancedContainer)
            {
                ((AdvancedContainer) container).handleTrackedInt(property, value);
            }
        });
        ctx.get().setPacketHandled(true);
    }
}