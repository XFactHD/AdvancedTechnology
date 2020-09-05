package xfacthd.advtech.common.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.tileentity.TileEntityProducer;

public abstract class ContainerProducer<B extends BlockMachine, T extends TileEntityProducer> extends ContainerInventoryMachine<B, T>
{
    protected ContainerProducer(ContainerType<?> type, int id, B block, T machine, PlayerInventory inventory)
    {
        super(type, id, block, machine, inventory);
    }

    @Override
    public void detectAndSendChanges()
    {
        super.detectAndSendChanges();
    }
}