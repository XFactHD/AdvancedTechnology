package xfacthd.advtech.common.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.*;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketConfigureSide;
import xfacthd.advtech.common.net.packets.machine.PacketSwitchActiveOutput;
import xfacthd.advtech.common.tileentity.TileEntityInventoryMachine;
import xfacthd.advtech.common.util.sync.BoolReferenceHolder;

public abstract class ContainerInventoryMachine<T extends TileEntityInventoryMachine> extends ContainerMachine<T>
{
    protected final IIntArray portArray = new IntArray(6);
    protected final BoolReferenceHolder forceHolder = new BoolReferenceHolder();

    protected ContainerInventoryMachine(ContainerType<?> type, int id, BlockMachine block, T machine, PlayerInventory inventory)
    {
        super(type, id, block, machine, inventory);

        trackIntArray(portArray);
        trackBool(forceHolder);
    }

    @Override
    public void detectAndSendChanges()
    {
        for (Side side : Side.values())
        {
            portArray.set(side.ordinal(), machine.getSidePort(side).ordinal());
        }

        forceHolder.set(machine.shouldForceOutput());

        super.detectAndSendChanges();
    }

    public final SideAccess getPortSetting(Side side) { return SideAccess.values()[portArray.get(side.ordinal())]; }

    public void configureSide(Side side, int dir)
    {
        NetworkHandler.sendToServer(new PacketConfigureSide(machine.getPos(), side, dir));
    }

    public Side getFrontSide() { return machine.getFrontSide(); }

    public boolean canForcePush() { return machine.canForcePush(); }

    public boolean shouldForceOutput() { return forceHolder.get(); }

    public void switchActiveOutput() { NetworkHandler.sendToServer(new PacketSwitchActiveOutput(machine.getPos())); }
}