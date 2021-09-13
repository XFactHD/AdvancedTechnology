package xfacthd.advtech.common.menu.machine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.machine.BlockEntityHarvester;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;

public class ContainerMenuHarvester extends ContainerMenuInventoryMachine<BlockEntityHarvester>
{
    public ContainerMenuHarvester(int id, BlockEntityHarvester machine, Inventory inventory)
    {
        super(ATContent.MENU_HARVESTER.get(), id, ATContent.machineBlock(MachineType.HARVESTER), machine, inventory);

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

    public void switchShowArea() { machine.switchShowArea(); }
}