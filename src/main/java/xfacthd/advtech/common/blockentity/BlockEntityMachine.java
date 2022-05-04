package xfacthd.advtech.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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

public abstract class BlockEntityMachine extends BlockEntityBase implements MenuProvider
{
    private static final int STATE_CHANGED_TIMEOUT = 20;

    protected MachineLevel level = MachineLevel.BASIC;
    protected RedstoneMode redstoneMode = RedstoneMode.OFF;

    private LazyOptional<EnergyMachine> lazyEnergyHandler = LazyOptional.empty();
    protected final EnhancementItemStackHandler upgradeInventory;
    protected EnergyMachine energyHandler;

    protected boolean active = false;
    private long lastStateChange = 0;
    private boolean stateDeferred = false;
    private boolean stateTarget = false;

    public BlockEntityMachine(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean capInit)
    {
        super(type, pos, state);

        if (capInit) { initCapabilities(); }
        upgradeInventory = supportsEnhancements() ? new EnhancementItemStackHandler(this) : null;
    }

    private boolean firstTick = true;
    public final void tick()
    {
        if (firstTick) //TODO: remove when the onLoad() PR is merged
        {
            firstTick = false;
            onLoad();
        }

        tickInternal();

        if (!level().isClientSide())
        {
            if (stateDeferred && level().getGameTime() - lastStateChange > STATE_CHANGED_TIMEOUT)
            {
                stateDeferred = false;
                setActive(stateTarget);
            }

            if (needsSync())
            {
                sendSyncPacket();
            }
        }
    }

    protected abstract void tickInternal();

    @Override
    public void onLoad()
    {
        super.onLoad();

        if (needPower())
        {
            lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
        }

        onLevelChanged();
        setActive(false);
        markFullUpdate();
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();

        if (needPower())
        {
            lazyEnergyHandler.invalidate();
        }
    }

    /*
     * Getters / Setters
     */

    public boolean canUpgrade() { return true; }

    public final void upgrade(MachineLevel level)
    {
        this.level = level;
        onLevelChanged();
        markForSync();
    }

    public final MachineLevel getMachineLevel() { return level; }

    protected void setActive(boolean active)
    {
        if (this.active != active)
        {
            if (level().getGameTime() - lastStateChange < STATE_CHANGED_TIMEOUT)
            {
                stateDeferred = true;
                stateTarget = active;
            }
            else if (!stateDeferred || stateTarget != active)
            {
                stateDeferred = false;
                lastStateChange = level().getGameTime();
                level().setBlockAndUpdate(worldPosition, getBlockState().setValue(PropertyHolder.ACTIVE, active));
            }

            this.active = active;
            markForSync();
        }
    }

    public boolean isActive() { return active; }

    public abstract float getProgress();

    public RedstoneMode getRedstoneMode() { return redstoneMode; }

    public void setRedstoneMode(RedstoneMode mode)
    {
        this.redstoneMode = mode;
        setChanged();
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
            boolean powered = level().hasNeighborSignal(worldPosition); //TODO: check if this can (if needed) be cached easily

            if (redstoneMode == RedstoneMode.HIGH && !powered)
            {
                return false;
            }
            else if (redstoneMode == RedstoneMode.LOW && powered)
            {
                return false;
            }
        }

        if (active) { return true; }

        return level().getGameTime() - lastStateChange > STATE_CHANGED_TIMEOUT;
    }

    /*
     * Capability stuff
     */

    @Override
    public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side)
    {
        if (cap == CapabilityEnergy.ENERGY && supportsEnergyOnSide(side))
        {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * Wether the machine uses power
     * MUST be constant
     */
    protected boolean needPower() { return true; }

    protected boolean supportsEnergyOnSide(Direction side) { return needPower(); }

    protected abstract void initCapabilities();

    protected abstract int getBaseEnergyCapacity();

    /*
     * NBT stuff
     */

    @Override
    public void readNetworkNBT(CompoundTag nbt)
    {
        level = MachineLevel.values()[nbt.getInt("level")];
        active = nbt.getBoolean("active");
    }

    @Override
    public void writeNetworkNBT(CompoundTag nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putBoolean("active", active);
    }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer)
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
    public void writeSyncPacket(FriendlyByteBuf buffer)
    {
        buffer.writeInt(level.ordinal());
        buffer.writeBoolean(active);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        level = MachineLevel.values()[nbt.getInt("level")];
        redstoneMode = RedstoneMode.values()[nbt.getInt("redstone")];

        if (needPower())
        {
            energyHandler.deserializeNBT(nbt.getCompound("energy"));
        }
        if (supportsEnhancements())
        {
            upgradeInventory.deserializeNBT(nbt.getCompound("upgrades"));
        }
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("level", level.ordinal());
        nbt.putInt("redstone", redstoneMode.ordinal());
        if (needPower())
        {
            nbt.put("energy", energyHandler.serializeNBT());
        }
        if (supportsEnhancements())
        {
            nbt.put("upgrades", upgradeInventory.serializeNBT());
        }
    }

    public void writeToItemData(CompoundTag nbt)
    {
        nbt.putInt("level", level.ordinal());
        if (needPower())
        {
            nbt.put("energy", energyHandler.serializeNBT());
        }
        if (supportsEnhancements())
        {
            nbt.put("upgrades", upgradeInventory.serializeNBT());
        }
    }
}