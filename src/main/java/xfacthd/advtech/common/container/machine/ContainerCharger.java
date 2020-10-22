package xfacthd.advtech.common.container.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.container.ContainerInventoryMachine;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.tileentity.machine.TileEntityCharger;

public class ContainerCharger extends ContainerInventoryMachine<TileEntityCharger>
{
    public ContainerCharger(int windowId, TileEntityCharger machine, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypeCharger, windowId, ATContent.blockCharger, machine, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
        {
            addSlot(new SlotItemHandler(handler, 0,  53, 45));
            addSlot(new SlotItemHandler(handler, 1, 107, 45));
        });
    }
}