package xfacthd.advtech.common.container.generator;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.container.ContainerGenerator;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.tileentity.generator.TileEntityBurnerGenerator;

public class ContainerBurnerGenerator extends ContainerGenerator<TileEntityBurnerGenerator> //TODO: move fluid stuff to steam generator
{
    private final IntReferenceHolder waterStoredHolder = IntReferenceHolder.single();
    private final IntReferenceHolder waterCapacityHolder = IntReferenceHolder.single();
    private final IntReferenceHolder steamStoredHolder = IntReferenceHolder.single();
    private final IntReferenceHolder steamCapacityHolder = IntReferenceHolder.single();

    public ContainerBurnerGenerator(int id, TileEntityBurnerGenerator tile, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypeBurnerGenerator, id, ATContent.blockBurnerGenerator, tile, inventory);

        layoutPlayerInventorySlots(8, 105);

        machine.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
                addSlot(new SlotItemHandler(handler, 0, 80, 45)));

        fluidHandler.ifPresent(handler ->
        {
            trackRealInt(waterStoredHolder);
            trackRealInt(waterCapacityHolder);
            trackRealInt(steamStoredHolder);
            trackRealInt(steamCapacityHolder);
        });
    }

    @Override
    public void detectAndSendChanges()
    {
        fluidHandler.ifPresent(handler ->
        {
            waterStoredHolder.set(handler.getFluidInTank(0).getAmount());
            waterCapacityHolder.set(handler.getTankCapacity(0));
            steamStoredHolder.set(handler.getFluidInTank(1).getAmount());
            steamCapacityHolder.set(handler.getTankCapacity(1));
        });

        super.detectAndSendChanges();
    }

    public int getWaterStored() { return waterStoredHolder.get(); }

    public int getWaterCapacity() { return waterCapacityHolder.get(); }

    public int getSteamStored() { return steamStoredHolder.get(); }

    public int getSteamCapacity() { return steamCapacityHolder.get(); }
}