package xfacthd.advtech.common.tileentity;

import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import xfacthd.advtech.common.capability.energy.EnergyMachine;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.util.data.PropertyHolder;

//TODO: implement upgrade system
public abstract class TileEntityMachine extends TileEntityBase implements ITickableTileEntity, INamedContainerProvider
{
    private static final int HICCUP_TIMEOUT = 20;

    protected MachineLevel level = MachineLevel.BASIC;

    private LazyOptional<EnergyMachine> lazyEnergyHandler = LazyOptional.empty();
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
        if (firstTick)
        {
            firstTick();
        }
    }

    protected void firstTick()
    {
        firstTick = false;

        onLevelChanged();
        setActive(false);
        lastHiccup = 0; //Set to 0 so the machine can start immediately if possible
        markFullUpdate();
    }

    /*
     * Getters / Setters
     */

    public final void upgrade(MachineLevel level)
    {
        this.level = level;
        onLevelChanged();
        markFullUpdate();
    }

    public final MachineLevel getLevel() { return level; }

    protected void setActive(boolean active)
    {
        if (!active)
        {
            //noinspection ConstantConditions
            lastHiccup = world.getGameTime();
        }

        //noinspection ConstantConditions
        world.setBlockState(pos, getBlockState().with(PropertyHolder.ACTIVE, active));
        this.active = active;
        markFullUpdate();
    }

    public final boolean isActive() { return active; }

    public abstract float getProgress();

    /*
     * Helpers
     */

    public abstract void onLevelChanged();

    protected boolean canStart()
    {
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
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        level = MachineLevel.values()[nbt.getInt("level")];

        energyHandler.deserializeNBT(nbt.getCompound("energy"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());

        nbt.put("energy", energyHandler.serializeNBT());

        return super.write(nbt);
    }

    public void writeToItemData(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.put("energy", energyHandler.serializeNBT());
    }
}