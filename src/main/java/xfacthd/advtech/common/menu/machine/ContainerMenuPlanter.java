package xfacthd.advtech.common.menu.machine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.machine.BlockEntityPlanter;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketPlanterSetFilter;

public class ContainerMenuPlanter extends ContainerMenuInventoryMachine<BlockEntityPlanter>
{
    public ContainerMenuPlanter(int id, BlockEntityPlanter machine, Inventory inventory)
    {
        super(ATContent.MENU_PLANTER.get(), id, ATContent.machineBlock(MachineType.PLANTER), machine, inventory);

        layoutPlayerInventorySlots(8, 105);
        layoutEnhancementSlots(200, 23);

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

    public void configureFilter(boolean clear) { NetworkHandler.sendToServer(new PacketPlanterSetFilter(machine.getBlockPos(), clear)); }

    public void switchShowArea() { machine.switchShowArea(); }
}