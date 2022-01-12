package xfacthd.advtech.common.net.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

@SuppressWarnings("unused")
public abstract class AbstractPacket
{
    public abstract void encode(FriendlyByteBuf buffer);

    public abstract void handle(Supplier<NetworkEvent.Context> ctx);
}