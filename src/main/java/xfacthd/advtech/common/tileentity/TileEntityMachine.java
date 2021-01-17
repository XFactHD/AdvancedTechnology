package xfacthd.advtech.common.tileentity;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import xfacthd.advtech.common.capability.energy.EnergyMachine;
import xfacthd.advtech.common.capability.item.EnhancementItemStackHandler;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.states.RedstoneMode;
import xfacthd.advtech.common.data.subtypes.Enhancement;
import xfacthd.advtech.common.item.tool.ItemEnhancement;
import xfacthd.advtech.common.util.data.PropertyHolder;

public abstract class TileEntityMachine extends TileEntityBase implements ITickableTileEntity, INamedContainerProvider
{
    private static final int HICCUP_TIMEOUT = 20;

    protected MachineLevel level = MachineLevel.BASIC;
    protected RedstoneMode redstoneMode = RedstoneMode.OFF;

    private LazyOptional<EnergyMachine> lazyEnergyHandler = LazyOptional.empty();
    protected final EnhancementItemStackHandler upgradeInventory = new EnhancementItemStackHandler(this);
    protected EnergyMachine energyHandler;

    protected boolean active = false;
    private boolean firstTick = true;
    private long lastHiccup = 0;

    public TileEntityMachine(TileEntityType<?> type, boolean capInit)
    {
        super(type);

        if (capInit) { initCapabilities(); }
    }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (firstTick)
            {
                firstTick();
                firstTick = false;
            }

            if (needsSync())
            {
                sendSyncPacket();
            }
        }
    }

    protected void firstTick()
    {
        onLevelChanged();
        setActive(false);
        markFullUpdate();
    }

    /*
     * Getters / Setters
     */

    public final void upgrade(MachineLevel level)
    {
        this.level = level;
        onLevelChanged();
        markForSync();
    }

    public final MachineLevel getLevel() { return level; }

    protected void setActive(boolean active)
    {
        if (active != this.active)
        {
            if (!active)
            {
                //noinspection ConstantConditions
                lastHiccup = world.getGameTime();
            }

            //noinspection ConstantConditions
            world.setBlockState(pos, getBlockState().with(PropertyHolder.ACTIVE, active));
            this.active = active;
            markForSync();
        }
    }

    public final boolean isActive() { return active; }

    public abstract float getProgress();

    public RedstoneMode getRedstoneMode() { return redstoneMode; }

    public void setRedstoneMode(RedstoneMode mode)
    {
        this.redstoneMode = mode;
        markDirty();
    }

    /*
     * Upgrade system
     */

    /**
     * Returns whether the machine supports upgrades
     */
    public abstract boolean supportsEnhancements();

    /**
     * Returns wether the {@link Enhancement upgrade} can be installed in this machine
     * Default impl checks whether the given {@link Enhancement} is already installed
     * @param upgrade The {@link Enhancement} to install
     * @return If the given {@link Enhancement} can be installed
     */
    public boolean canInstallEnhancement(Enhancement upgrade)
    {
        if (!supportsEnhancements()) { return false; }

        for (int i = 0; i < upgradeInventory.getSlots(); i++)
        {
            ItemStack stack = upgradeInventory.getStackInSlot(i);
            if (stack.getItem() instanceof ItemEnhancement)
            {
                Enhancement type = ((ItemEnhancement) stack.getItem()).getType();
                if (type == upgrade && upgrade.singleInstance())
                {
                    return false;
                }
            }
            else if (stack.isEmpty())
            {
                return true;
            }
        }
        return false;
    }

    public void installEnhancement(Enhancement upgrade, int level) { }

    public void removeEnhancement(Enhancement upgrade) { }

    public EnhancementItemStackHandler getUpgradeInventory() { return upgradeInventory; }

    /*
     * Helpers
     */

    public abstract void onLevelChanged();

    /**
     * @param cycleStart Wether a new work cycle starts (ie next item in furnace), used to prevent interrupting
     *                   a running cycle with a redstone mode change
     */
    protected boolean canRun(boolean cycleStart)
    {
        if (redstoneMode != RedstoneMode.OFF && cycleStart)
        {
            //noinspection ConstantConditions
            int power = world.getRedstonePowerFromNeighbors(pos); //TODO: check if this can be cached easily

            if (redstoneMode == RedstoneMode.HIGH && power == 0)
            {
                return false;
            }
            else if (redstoneMode == RedstoneMode.LOW && power > 0)
            {
                return false;
            }
        }

        if (active) { return true; }

        //noinspection ConstantConditions
        return world.getGameTime() - lastHiccup > HICCUP_TIMEOUT;
    }

    /*
     * Capability stuff
     */

    @Override
    public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side)
    {
        if (cap == CapabilityEnergy.ENERGY)
        {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    protected abstract void initCapabilities();

    protected abstract int getBaseEnergyCapacity();

    /*
     * TE internal stuff
     */

    @Override
    public void validate()
    {
        super.validate();

        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
    }

    @Override
    public void remove()
    {
        super.remove();

        lazyEnergyHandler.invalidate();
    }

    /*
     * NBT stuff
     */

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        level = MachineLevel.values()[nbt.getInt("level")];
        active = nbt.getBoolean("active");
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putBoolean("active", active);
    }

    @Override
    protected void readSyncPacket(PacketBuffer buffer)
    {
        int level = buffer.readInt();
        if (level != this.level.ordinal())
        {
            this.level = MachineLevel.values()[level];
            markRenderUpdate();
        }

        active = buffer.readBoolean();
    }

    @Override
    public void writeSyncPacket(PacketBuffer buffer)
    {
        buffer.writeInt(level.ordinal());
        buffer.writeBoolean(active);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        level = MachineLevel.values()[nbt.getInt("level")];
        redstoneMode = RedstoneMode.values()[nbt.getInt("redstone")];

        energyHandler.deserializeNBT(nbt.getCompound("energy"));
        upgradeInventory.deserializeNBT(nbt.getCompound("upgrades"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putInt("redstone", redstoneMode.ordinal());
        nbt.put("energy", energyHandler.serializeNBT());
        nbt.put("upgrades", upgradeInventory.serializeNBT());
        return super.write(nbt);
    }

    public void writeToItemData(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.put("energy", energyHandler.serializeNBT());
        nbt.put("upgrades", upgradeInventory.serializeNBT());
    }
}