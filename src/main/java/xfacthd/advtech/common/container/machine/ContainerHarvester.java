package xfacthd.advtech.common.container.machine;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.machine.BlockHarvester;
import xfacthd.advtech.common.container.ContainerInventoryMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.tileentity.machine.TileEntityHarvester;

public class ContainerHarvester extends ContainerInventoryMachine<BlockHarvester, TileEntityHarvester>
{
    public ContainerHarvester(int id, TileEntityHarvester machine, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypeHarvester, id, (BlockHarvester) ATContent.blockHarvester, machine, inventory);

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

    @Override
    public MachineType getMachineType() { return MachineType.HARVESTER; }
}