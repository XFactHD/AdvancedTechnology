package xfacthd.advtech.common.container;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.util.IWorldPosCallable;
import net.minecraft.util.IntReferenceHolder;
import net.minecraftforge.energy.CapabilityEnergy;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.tileentity.TileEntityMachine;
import xfacthd.advtech.common.util.sync.BoolReferenceHolder;
import xfacthd.advtech.common.util.sync.ByteReferenceHolder;

public abstract class ContainerMachine<B extends BlockMachine, T extends TileEntityMachine> extends AdvancedContainer
{
    protected final B block;
    protected final T machine;
    protected final IWorldPosCallable worldPosCallable;
    protected final IntReferenceHolder energyHolder = IntReferenceHolder.single();
    protected final IntReferenceHolder capacityHolder = IntReferenceHolder.single();
    protected final BoolReferenceHolder activeHolder = new BoolReferenceHolder();
    protected final ByteReferenceHolder progressHolder = new ByteReferenceHolder();

    protected ContainerMachine(ContainerType<?> type, int id, B block, T machine, PlayerInventory inventory)
    {
        super(type, id, inventory);

        this.block = block;
        this.machine = machine;

        if (machine.supportsEnhancements())
        {
            //TODO: handle upgrade inventory slots
        }

        //noinspection ConstantConditions
        worldPosCallable = IWorldPosCallable.of(machine.getWorld(), machine.getPos());

        trackRealInt(energyHolder);
        trackRealInt(capacityHolder);
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

        activeHolder.set(machine.isActive());
        progressHolder.set((byte)(100F * machine.getProgress()));

        super.detectAndSendChanges();
    }

    public boolean canInteractWith(PlayerEntity player) { return isWithinUsableDistance(worldPosCallable, player, block); }

    public final int getStoredEnergy() { return energyHolder.get(); }

    public final int getEnergyCapacity() { return capacityHolder.get(); }

    public boolean isActive() { return activeHolder.get(); }

    public float getProgress() { return (float)progressHolder.get() / 100F; }

    public abstract MachineType getMachineType();
}