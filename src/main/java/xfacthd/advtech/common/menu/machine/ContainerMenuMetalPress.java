package xfacthd.advtech.common.menu.machine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.machine.BlockEntityMetalPress;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;

public class ContainerMenuMetalPress extends ContainerMenuInventoryMachine<BlockEntityMetalPress>
{
    public ContainerMenuMetalPress(int id, BlockEntityMetalPress machine, Inventory inventory)
    {
        super(ATContent.MENU_METAL_PRESS.get(), id, ATContent.machineBlock(MachineType.METAL_PRESS), machine, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
        {
            addSlot(new SlotItemHandler(handler, 0,  33, 45));
            addSlot(new SlotItemHandler(handler, 1,  53, 45));
            addSlot(new SlotItemHandler(handler, 2, 107, 45));
        });
    }
}