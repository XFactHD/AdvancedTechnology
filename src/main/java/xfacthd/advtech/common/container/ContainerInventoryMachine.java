package xfacthd.advtech.common.container;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IIntArray;
import net.minecraft.util.IntArray;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketConfigureSide;
import xfacthd.advtech.common.tileentity.TileEntityInventoryMachine;

public abstract class ContainerInventoryMachine<B extends BlockMachine, T extends TileEntityInventoryMachine> extends ContainerMachine<B, T>
{
    protected final IIntArray portArray = new IntArray(6);

    protected ContainerInventoryMachine(ContainerType<?> type, int id, B block, T machine, PlayerInventory inventory)
    {
        super(type, id, block, machine, inventory);

        trackIntArray(portArray);
    }

    @Override
    public void detectAndSendChanges()
    {
        for (Side side : Side.values())
        {
            portArray.set(side.ordinal(), machine.getSidePort(side).ordinal());
        }

        super.detectAndSendChanges();
    }

    public final SideAccess getPortSetting(Side side) { return SideAccess.values()[portArray.get(side.ordinal())]; }

    public void configureSide(Side side, int dir)
    {
        NetworkHandler.sendToServer(new PacketConfigureSide(machine.getPos(), side, dir));
    }
}