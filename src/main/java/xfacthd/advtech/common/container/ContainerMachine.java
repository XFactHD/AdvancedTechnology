package xfacthd.advtech.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.*;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraftforge.items.wrapper.InvWrapper;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketConfigureSide;
import xfacthd.advtech.common.tileentity.TileEntityMachine;
import xfacthd.advtech.common.util.sync.BoolReferenceHolder;
import xfacthd.advtech.common.util.sync.ByteReferenceHolder;

public abstract class ContainerMachine<B extends BlockMachine, T extends TileEntityMachine> extends AdvancedContainer
{
    protected final B block;
    protected final T machine;
    protected final IItemHandler playerInventory;
    protected final IWorldPosCallable worldPosCallable;
    protected final IntReferenceHolder energyHolder = IntReferenceHolder.single();
    protected final IntReferenceHolder capacityHolder = IntReferenceHolder.single();
    protected final IIntArray portArray = new IntArray(6);
    protected final BoolReferenceHolder activeHolder = new BoolReferenceHolder();
    protected final ByteReferenceHolder progressHolder = new ByteReferenceHolder();

    protected ContainerMachine(ContainerType<?> type, int id, B block, T machine, PlayerInventory inventory)
    {
        super(type, id);

        this.block = block;
        this.machine = machine;
        this.playerInventory = new InvWrapper(inventory);

        //noinspection ConstantConditions
        worldPosCallable = IWorldPosCallable.of(machine.getWorld(), machine.getPos());

        trackRealInt(energyHolder);
        trackRealInt(capacityHolder);
        trackIntArray(portArray);
        trackBool(activeHolder);
        trackByte(progressHolder);
    }

    @Override
    public void detectAndSendChanges()
    {
        machine.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy ->
        {
            energyHolder.set(energy.getEnergyStored());
            capacityHolder.set(energy.getMaxEnergyStored());
        });

        for (Side side : Side.values())
        {
            portArray.set(side.ordinal(), machine.getSidePort(side).ordinal());
        }

        activeHolder.set(machine.isActive());
        progressHolder.set((byte)(100F * machine.getProgress()));

        super.detectAndSendChanges();
    }

    public boolean canInteractWith(PlayerEntity player) { return isWithinUsableDistance(worldPosCallable, player, block); }

    public final int getStoredEnergy() { return energyHolder.get(); }

    public final int getEnergyCapacity() { return capacityHolder.get(); }

    public final SideAccess getPortSetting(Side side) { return SideAccess.values()[portArray.get(side.ordinal())]; }

    public boolean isActive() { return activeHolder.get(); }

    public float getProgress() { return (float)progressHolder.get() / 100F; }

    public void configureSide(Side side, int dir)
    {
        NetworkHandler.sendToServer(new PacketConfigureSide(machine.getPos(), side, dir));
    }

    public abstract MachineType getMachineType();

    protected void layoutPlayerInventorySlots(int posX, int posY)
    {
        // Player inventory
        for (int y = 0; y < 3; y++)
        {
            for (int x = 0; x < 9; x++)
            {
                addSlot(new SlotItemHandler(playerInventory, (y * 9) + x + 9, posX + (x * 18), posY + (y * 18)));
            }
        }

        // Hotbar
        posY += 58;
        for (int x = 0; x < 9; x++)
        {
            addSlot(new SlotItemHandler(playerInventory, x, posX + (x * 18), posY));
        }
    }
}