package xfacthd.advtech.common.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IntReferenceHolder;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketSwitchActiveOutput;
import xfacthd.advtech.common.tileentity.TileEntityProducer;

public abstract class ContainerProducer<B extends BlockMachine, T extends TileEntityProducer> extends ContainerMachine<B, T>
{
    protected final IntReferenceHolder forceHolder = IntReferenceHolder.single();

    protected ContainerProducer(ContainerType<?> type, int id, B block, T machine, PlayerInventory inventory)
    {
        super(type, id, block, machine, inventory);

        trackInt(forceHolder);
    }

    @Override
    public void detectAndSendChanges()
    {
        forceHolder.set(machine.shouldForceOutput() ? 1 : 0);

        super.detectAndSendChanges();
    }

    public boolean shouldForceOutput() { return forceHolder.get() != 0; }

    public void switchActiveOutput() { NetworkHandler.sendToServer(new PacketSwitchActiveOutput(machine.getPos())); }
}