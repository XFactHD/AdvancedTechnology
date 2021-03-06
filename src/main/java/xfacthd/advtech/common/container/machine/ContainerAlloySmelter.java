package xfacthd.advtech.common.container.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.container.ContainerProducer;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.tileentity.machine.TileEntityAlloySmelter;

public class ContainerAlloySmelter extends ContainerProducer<TileEntityAlloySmelter>
{
    public ContainerAlloySmelter(int id, TileEntityAlloySmelter tile, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypeAlloySmelter, id, ATContent.blockAlloySmelter, tile, inventory);

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