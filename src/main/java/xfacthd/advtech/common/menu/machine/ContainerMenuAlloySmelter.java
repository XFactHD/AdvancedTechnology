package xfacthd.advtech.common.menu.machine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.machine.BlockEntityAlloySmelter;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;

public class ContainerMenuAlloySmelter extends ContainerMenuInventoryMachine<BlockEntityAlloySmelter>
{
    public ContainerMenuAlloySmelter(int id, BlockEntityAlloySmelter machine, Inventory inventory)
    {
        super(ATContent.MENU_ALLOY_SMELTER.get(), id, ATContent.machineBlock(MachineType.ALLOY_SMELTER), machine, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
        {
            addSlot(new SlotItemHandler(handler, 0,  33, 45));
            addSlot(new SlotItemHandler(handler, 1,  53, 45));
            addSlot(new SlotItemHandler(handler, 2, 107, 45));
            addSlot(new SlotItemHandler(handler, 3, 107, 65));
        });
    }
}