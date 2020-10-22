package xfacthd.advtech.common.container.machine;

import net.minecraft.entity.player.*;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.*;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.container.ContainerInventoryMachine;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketPlanterSetFilter;
import xfacthd.advtech.common.tileentity.machine.TileEntityPlanter;

public class ContainerPlanter extends ContainerInventoryMachine<TileEntityPlanter>
{
    public ContainerPlanter(int id, TileEntityPlanter machine, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypePlanter, id, ATContent.blockPlanter, machine, inventory);

        layoutPlayerInventorySlots(8, 105);
        layoutEnhancementSlots(200, 27);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
        {
            for (int y = 0; y < 3; y++)
            {
                for (int x = 0; x < 3; x++)
                {
                    addSlot(new SlotItemHandler(handler, (y * 3) + x, 60 + (x * 20), 26 + (y * 20)));
                }
            }
        });
    }

    public ItemStack getFilterStack(int idx) { return machine.getFilterStack(idx); }

    public void configureFilter(boolean clear) { NetworkHandler.sendToServer(new PacketPlanterSetFilter(machine.getPos(), clear)); }

    public void switchShowArea() { machine.switchShowArea(); }
}