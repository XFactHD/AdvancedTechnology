package xfacthd.advtech.common.menu.generator;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuGenerator;
import xfacthd.advtech.common.blockentity.generator.BlockEntityBurnerGenerator;

public class ContainerMenuBurnerGenerator extends ContainerMenuGenerator<BlockEntityBurnerGenerator> //TODO: move fluid stuff to steam generator
{
    private final DataSlot waterStoredHolder = DataSlot.standalone();
    private final DataSlot waterCapacityHolder = DataSlot.standalone();
    private final DataSlot steamStoredHolder = DataSlot.standalone();
    private final DataSlot steamCapacityHolder = DataSlot.standalone();

    public ContainerMenuBurnerGenerator(int id, BlockEntityBurnerGenerator tile, Inventory inventory)
    {
        super(ATContent.MENU_BURNER_GENERATOR.get(), id, ATContent.machineBlock(MachineType.BURNER_GENERATOR), tile, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
                addSlot(new SlotItemHandler(handler, 0, 80, 45)));

        fluidHandler.ifPresent(handler ->
        {
            addIntDataSlot(waterStoredHolder);
            addIntDataSlot(waterCapacityHolder);
            addIntDataSlot(steamStoredHolder);
            addIntDataSlot(steamCapacityHolder);
        });
    }

    @Override
    public void broadcastChanges()
    {
        fluidHandler.ifPresent(handler ->
        {
            waterStoredHolder.set(handler.getFluidInTank(0).getAmount());
            waterCapacityHolder.set(handler.getTankCapacity(0));
            steamStoredHolder.set(handler.getFluidInTank(1).getAmount());
            steamCapacityHolder.set(handler.getTankCapacity(1));
        });

        super.broadcastChanges();
    }

    public int getWaterStored() { return waterStoredHolder.get(); }

    public int getWaterCapacity() { return waterCapacityHolder.get(); }

    public int getSteamStored() { return steamStoredHolder.get(); }

    public int getSteamCapacity() { return steamCapacityHolder.get(); }
}