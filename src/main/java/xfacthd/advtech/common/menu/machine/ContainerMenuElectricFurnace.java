package xfacthd.advtech.common.menu.machine;

import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.machine.BlockEntityElectricFurnace;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuInventoryMachine;

public class ContainerMenuElectricFurnace extends ContainerMenuInventoryMachine<BlockEntityElectricFurnace>
{
    public ContainerMenuElectricFurnace(int id, BlockEntityElectricFurnace tile, Inventory inventory)
    {
        super(ATContent.MENU_FURNACE.get(), id, ATContent.machineBlock(MachineType.ELECTRIC_FURNACE), tile, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
        {
            addSlot(new SlotItemHandler(handler, 0,  53, 45));
            addSlot(new SlotItemHandler(handler, 1, 107, 45));
        });
    }
}