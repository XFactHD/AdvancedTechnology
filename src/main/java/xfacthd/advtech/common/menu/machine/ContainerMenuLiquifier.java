package xfacthd.advtech.common.menu.machine;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketUpdateFluid;
import xfacthd.advtech.common.util.interfaces.IContainerFluidHandler;
import xfacthd.advtech.common.blockentity.machine.BlockEntityLiquifier;

public class ContainerMenuLiquifier extends ContainerMenuInventoryMachine<BlockEntityLiquifier> implements IContainerFluidHandler
{
    private final DataSlot fluidCapacityHolder = DataSlot.standalone();
    private FluidStack lastFluid = FluidStack.EMPTY;
    private int lastAmount = 0;

    public ContainerMenuLiquifier(int id, BlockEntityLiquifier machine, Inventory inventory)
    {
        super(ATContent.MENU_LIQUIFIER.get(), id, ATContent.machineBlock(MachineType.LIQUIFIER), machine, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
                .ifPresent(handler -> addSlot(new SlotItemHandler(handler, 0, 53, 45)));

        addIntDataSlot(fluidCapacityHolder);
    }

    @Override
    public void broadcastChanges()
    {
        fluidCapacityHolder.set(machine.getFluidCapacity());

        FluidStack fluid = machine.getFluidStored();
        boolean fluidEqual = fluid.isFluidEqual(lastFluid);
        if (!fluidEqual || fluid.getAmount() != lastAmount)
        {
            lastFluid = fluid;
            lastAmount = fluid.getAmount();
            NetworkHandler.sendToPlayer(new PacketUpdateFluid(containerId, 0, fluid, fluidEqual), (ServerPlayer)player);
        }

        super.broadcastChanges();
    }

    @Override
    public void handleFluidUpdate(int tank, FluidStack fluid) { lastFluid = fluid; }

    @Override
    public void handleFluidAmountUpdate(int tank, int amount) { lastFluid.setAmount(amount); }

    public FluidStack getFluidStored() { return lastFluid; }

    public int getFluidCapacity() { return fluidCapacityHolder.get(); }
}