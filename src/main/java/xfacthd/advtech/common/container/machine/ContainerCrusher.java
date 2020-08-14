package xfacthd.advtech.common.container.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.machine.BlockCrusher;
import xfacthd.advtech.common.container.ContainerProducer;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.tileentity.machine.TileEntityCrusher;

public class ContainerCrusher extends ContainerProducer<BlockCrusher, TileEntityCrusher>
{
    public ContainerCrusher(int id, TileEntityCrusher tile, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypeCrusher, id, (BlockCrusher) ATContent.blockCrusher, tile, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
        {
            addSlot(new SlotItemHandler(handler, 0,  53, 45));
            addSlot(new SlotItemHandler(handler, 1, 107, 45));
            addSlot(new SlotItemHandler(handler, 2, 107, 65));
        });
    }

    @Override
    public MachineType getMachineType() { return MachineType.CRUSHER; }
}