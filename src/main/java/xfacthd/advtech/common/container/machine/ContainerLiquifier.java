package xfacthd.advtech.common.container.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.inventory.container.IContainerListener;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.container.ContainerProducer;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketUpdateFluid;
import xfacthd.advtech.common.tileentity.machine.TileEntityLiquifier;
import xfacthd.advtech.common.util.interfaces.IContainerFluidHandler;

public class ContainerLiquifier extends ContainerProducer<TileEntityLiquifier> implements IContainerFluidHandler
{
    private final IntReferenceHolder fluidCapacityHolder = IntReferenceHolder.single();
    private FluidStack lastFluid = FluidStack.EMPTY;
    private int lastAmount = 0;

    public ContainerLiquifier(int id, TileEntityLiquifier machine, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypeLiquifier, id, ATContent.blockLiquifier, machine, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
                addSlot(new SlotItemHandler(handler, 0, 53, 45)));

        trackRealInt(fluidCapacityHolder);
    }

    @Override
    public void detectAndSendChanges()
    {
        fluidCapacityHolder.set(machine.getFluidCapacity());

        FluidStack fluid = machine.getFluidStored();
        boolean fluidEqual = fluid.isFluidEqual(lastFluid);
        if (!fluidEqual || fluid.getAmount() != lastAmount)
        {
            lastFluid = fluid;
            lastAmount = fluid.getAmount();
            for (IContainerListener listener : listeners)
            {
                if (listener instanceof ServerPlayerEntity)
                {
                    NetworkHandler.sendToPlayer(new PacketUpdateFluid(windowId, 0, fluid, fluidEqual), (ServerPlayerEntity)listener);
                }
            }
        }

        super.detectAndSendChanges();
    }

    @Override
    public void handleFluidUpdate(int tank, FluidStack fluid) { lastFluid = fluid; }

    @Override
    public void handleFluidAmountUpdate(int tank, int amount) { lastFluid.setAmount(amount); }

    public FluidStack getFluidStored() { return lastFluid; }

    public int getFluidCapacity() { return fluidCapacityHolder.get(); }
}