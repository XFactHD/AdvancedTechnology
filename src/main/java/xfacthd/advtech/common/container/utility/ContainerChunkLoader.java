package xfacthd.advtech.common.container.utility;

import net.minecraft.entity.player.*;
import net.minecraft.util.IntReferenceHolder;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.utility.BlockChunkLoader;
import xfacthd.advtech.common.container.ContainerMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.data.types.ContainerTypes;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketChangeChunkRadius;
import xfacthd.advtech.common.tileentity.utility.TileEntityChunkLoader;

public class ContainerChunkLoader extends ContainerMachine<TileEntityChunkLoader>
{
    private final IntReferenceHolder radiusHolder = IntReferenceHolder.single();
    private final IntReferenceHolder countHolder = IntReferenceHolder.single();
    private final IntReferenceHolder maxCountHolder = IntReferenceHolder.single();

    public ContainerChunkLoader(int id, TileEntityChunkLoader machine, PlayerInventory inventory)
    {
        super(ContainerTypes.containerTypeChunkLoader, id, ATContent.blockChunkLoader, machine, inventory);

        trackInt(radiusHolder);
        trackInt(countHolder);
        trackInt(maxCountHolder);
    }

    @Override
    public void detectAndSendChanges()
    {
        radiusHolder.set(machine.getRadius());
        countHolder.set(machine.getLoadedChunkCount());
        maxCountHolder.set(machine.getMaxChunkCount());

        super.detectAndSendChanges();
    }

    public int getRadius() { return radiusHolder.get(); }

    public int getChunkCount() { return countHolder.get(); }

    public int getMaxChunkCount() { return maxCountHolder.get(); }

    public void changeRadius(boolean add)
    {
        NetworkHandler.sendToServer(new PacketChangeChunkRadius(machine.getPos(), add));
    }

    public boolean showChunks() { return machine.showChunks(); }

    public boolean switchShowChunks() { return machine.switchShowChunks(); }
}