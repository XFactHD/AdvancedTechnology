package xfacthd.advtech.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MenuType;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketConfigureSide;
import xfacthd.advtech.common.net.packets.machine.PacketSwitchActiveOutput;
import xfacthd.advtech.common.util.sync.BoolDataSlot;
import xfacthd.advtech.common.util.sync.ByteDataSlot;

public abstract class ContainerMenuInventoryMachine<T extends BlockEntityInventoryMachine> extends ContainerMenuMachine<T>
{
    protected final ByteDataSlot[] portArray = new ByteDataSlot[6];
    protected final BoolDataSlot forceSlot = new BoolDataSlot();

    protected ContainerMenuInventoryMachine(MenuType<?> type, int windowId, BlockMachine block, T machine, Inventory playerInv)
    {
        super(type, windowId, block, machine, playerInv);

        for (int i = 0; i < 6; i++)
        {
            portArray[i] = new ByteDataSlot();
            addByteDataSlot(portArray[i]);
        }
        addBoolDataSlot(forceSlot);
    }

    @Override
    public void broadcastChanges()
    {
        for (Side side : Side.values())
        {
            portArray[side.ordinal()].set((byte) machine.getSidePort(side).ordinal());
        }

        forceSlot.set(machine.shouldForceOutput());

        super.broadcastChanges();
    }

    public final SideAccess getPortSetting(Side side) { return SideAccess.byId(portArray[side.ordinal()].get()); }

    public void configureSide(Side side, int dir)
    {
        NetworkHandler.sendToServer(new PacketConfigureSide(machine.getBlockPos(), side, dir));
    }

    public Side getFrontSide() { return machine.getFrontSide(); }

    public boolean canForcePush() { return machine.canForcePush(); }

    public boolean shouldForceOutput() { return forceSlot.get(); }

    public void switchActiveOutput() { NetworkHandler.sendToServer(new PacketSwitchActiveOutput(machine.getBlockPos())); }
}