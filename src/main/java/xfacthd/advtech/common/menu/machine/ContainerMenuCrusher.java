package xfacthd.advtech.common.menu.machine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.machine.BlockEntityCrusher;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;

public class ContainerMenuCrusher extends ContainerMenuInventoryMachine<BlockEntityCrusher>
{
    public ContainerMenuCrusher(int windowId, BlockEntityCrusher machine, Inventory playerInv)
    {
        super(ATContent.MENU_CRUSHER.get(), windowId, ATContent.machineBlock(MachineType.CRUSHER), machine, playerInv);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
        {
            addSlot(new SlotItemHandler(handler, 0,  53, 45));
            addSlot(new SlotItemHandler(handler, 1, 107, 45));
            addSlot(new SlotItemHandler(handler, 2, 107, 65));
        });
    }
}