package xfacthd.advtech.common.menu.utility;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.DataSlot;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.utility.BlockEntityChunkLoader;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.ContainerMenuMachine;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketChangeChunkRadius;

public class ContainerMenuChunkLoader extends ContainerMenuMachine<BlockEntityChunkLoader>
{
    private final DataSlot radiusHolder = DataSlot.standalone();
    private final DataSlot countHolder = DataSlot.standalone();
    private final DataSlot maxCountHolder = DataSlot.standalone();

    public ContainerMenuChunkLoader(int id, BlockEntityChunkLoader machine, Inventory inventory)
    {
        super(ATContent.MENU_CHUNK_LOADER.get(), id, ATContent.machineBlock(MachineType.CHUNK_LOADER), machine, inventory);

        addDataSlot(radiusHolder);
        addDataSlot(countHolder);
        addDataSlot(maxCountHolder);
    }

    @Override
    public void broadcastChanges()
    {
        radiusHolder.set(machine.getRadius());
        countHolder.set(machine.getMaxChunksInRadius());
        maxCountHolder.set(machine.getMaxChunks());

        super.broadcastChanges();
    }

    public int getRadius() { return radiusHolder.get(); }

    public int getChunkCount() { return countHolder.get(); }

    public int getMaxChunkCount() { return maxCountHolder.get(); }

    public void changeRadius(boolean add)
    {
        NetworkHandler.sendToServer(new PacketChangeChunkRadius(machine.getBlockPos(), add));
    }

    public boolean showChunks() { return machine.showChunks(); }

    public boolean switchShowChunks() { return machine.switchShowChunks(); }
}