package xfacthd.advtech.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.IItemHandler;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.data.states.RedstoneMode;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.net.NetworkHandler;
import xfacthd.advtech.common.net.packets.machine.PacketSetRedstoneMode;
import xfacthd.advtech.common.util.inventory.EnhancementSlot;
import xfacthd.advtech.common.util.sync.BoolDataSlot;
import xfacthd.advtech.common.util.sync.ByteDataSlot;

import java.util.*;

public abstract class ContainerMenuMachine<T extends BlockEntityMachine> extends AdvancedContainerMenu
{
    protected final BlockMachine block;
    protected final T machine;
    protected final ContainerLevelAccess levelAccess;
    protected final DataSlot energyHolder = DataSlot.standalone();
    protected final DataSlot capacityHolder = DataSlot.standalone();
    protected final BoolDataSlot activeHolder = new BoolDataSlot();
    protected final ByteDataSlot progressHolder = new ByteDataSlot();
    protected final ByteDataSlot redstoneHolder = new ByteDataSlot();
    protected final List<EnhancementSlot> enhancementSlots = new ArrayList<>();

    protected ContainerMenuMachine(MenuType<?> type, int windowId, BlockMachine block, T machine, Inventory playerInv)
    {
        super(type, windowId, playerInv);

        this.block = block;
        this.machine = machine;

        levelAccess = ContainerLevelAccess.create(machine.level(), machine.getBlockPos());

        addIntDataSlot(energyHolder);
        addIntDataSlot(capacityHolder);
        addBoolDataSlot(activeHolder);
        addByteDataSlot(progressHolder);
        addByteDataSlot(redstoneHolder);
    }

    @Override
    public void broadcastChanges()
    {
        machine.getCapability(CapabilityEnergy.ENERGY).ifPresent(energy ->
        {
            energyHolder.set(energy.getEnergyStored());
            capacityHolder.set(energy.getMaxEnergyStored());
        });

        activeHolder.set(machine.isActive());
        progressHolder.set((byte)(100F * machine.getProgress()));
        redstoneHolder.set((byte) machine.getRedstoneMode().ordinal());

        super.broadcastChanges();
    }

    protected void layoutEnhancementSlots(int posX, int posY)
    {
        IItemHandler inv = machine.getUpgradeInventory();

        enhancementSlots.addAll(Arrays.asList(
                (EnhancementSlot) addSlot(new EnhancementSlot(inv, 0, posX, posY)),
                (EnhancementSlot) addSlot(new EnhancementSlot(inv, 1, posX + 20, posY)),
                (EnhancementSlot) addSlot(new EnhancementSlot(inv, 2, posX, posY + 20)),
                (EnhancementSlot) addSlot(new EnhancementSlot(inv, 3, posX + 20, posY + 20))
        ));
    }

    public void switchEnhancementSlots(boolean open) { enhancementSlots.forEach(slot -> slot.setActive(open)); }

    @Override
    public boolean stillValid(Player player) { return stillValid(levelAccess, player, block); }

    public final int getStoredEnergy() { return energyHolder.get(); }

    public final int getEnergyCapacity() { return capacityHolder.get(); }

    public boolean isActive() { return activeHolder.get(); }

    public float getProgress() { return (float)progressHolder.get() / 100F; }

    public RedstoneMode getRedstoneMode() { return RedstoneMode.values()[redstoneHolder.get()]; }

    public void setRedstoneMode(RedstoneMode mode) { NetworkHandler.sendToServer(new PacketSetRedstoneMode(machine.getBlockPos(), mode)); }

    public final MachineType getMachineType() { return block.getType(); }
}