package xfacthd.advtech.common.net.packets.machine;

import net.minecraft.inventory.container.Container;
import net.minecraft.network.PacketBuffer;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fml.network.NetworkEvent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.net.packets.AbstractPacket;
import xfacthd.advtech.common.util.interfaces.IContainerFluidHandler;

import java.util.function.Supplier;

public class PacketUpdateFluid extends AbstractPacket
{
    private int windowId;
    private int tank;
    private FluidStack fluid;
    private int amount;
    private boolean simple;

    public PacketUpdateFluid(int windowId, int tank, FluidStack fluid, boolean simple)
    {
        this.windowId = windowId;
        this.tank = tank;
        this.fluid = fluid;
        this.simple = simple;
    }

    public PacketUpdateFluid(PacketBuffer buffer) { decode(buffer); }

    @Override
    public void encode(PacketBuffer buffer)
    {
        buffer.writeInt(windowId);
        buffer.writeInt(tank);
        buffer.writeBoolean(simple);

        if (simple) { buffer.writeInt(fluid.getAmount()); }
        else { buffer.writeFluidStack(fluid); }
    }

    @Override
    public void decode(PacketBuffer buffer)
    {
        windowId = buffer.readInt();
        tank = buffer.readInt();
        simple = buffer.readBoolean();
        if (simple) { amount = buffer.readInt(); }
        else { fluid = buffer.readFluidStack(); }
    }

    @Override
    public void handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
            Container cont = AdvancedTechnology.SIDED_HELPER.getPlayer().openContainer;
            if (cont instanceof IContainerFluidHandler && cont.windowId == windowId)
            {
                IContainerFluidHandler handler = (IContainerFluidHandler)cont;
                if (simple) { handler.handleFluidAmountUpdate(tank, amount); }
                else { handler.handleFluidUpdate(tank, fluid); }
            }
        });
        ctx.get().setPacketHandled(true);
    }
}