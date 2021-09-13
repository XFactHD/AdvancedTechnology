package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fmllegacy.network.NetworkEvent;
import xfacthd.advtech.client.util.ClientHelper;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.util.interfaces.IContainerFluidHandler;

import java.util.function.Supplier;

public class PacketUpdateFluid extends AbstractPacket
{
    private final int windowId;
    private final int tank;
    private final FluidStack fluid;
    private final int amount;
    private final boolean simple;

    public PacketUpdateFluid(int windowId, int tank, FluidStack fluid, boolean simple)
    {
        this.windowId = windowId;
        this.tank = tank;
        this.fluid = fluid;
        this.amount = fluid.getAmount();
        this.simple = simple;
    }

    public PacketUpdateFluid(FriendlyByteBuf buffer)
    {
        windowId = buffer.readInt();
        tank = buffer.readInt();
        simple = buffer.readBoolean();
        if (simple)
        {
            fluid = null;
            amount = buffer.readInt();
        }
        else
        {
            fluid = buffer.readFluidStack();
            amount = -1;
        }
    }

    @Override
    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeInt(windowId);
        buffer.writeInt(tank);
        buffer.writeBoolean(simple);

        if (simple) { buffer.writeInt(fluid.getAmount()); }
        else { buffer.writeFluidStack(fluid); }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            AbstractContainerMenu cont = ClientHelper.getClientPlayer().containerMenu;
            if (cont instanceof IContainerFluidHandler handler && cont.containerId == windowId)
            {
                if (simple) { handler.handleFluidAmountUpdate(tank, amount); }
                else { handler.handleFluidUpdate(tank, fluid); }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}