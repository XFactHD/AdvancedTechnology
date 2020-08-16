package xfacthd.advtech.common.tileentity;

import com.google.common.base.Preconditions;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xfacthd.advtech.common.capability.energy.EnergyMachine;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.capability.item.SidedItemStackHandler;
import xfacthd.advtech.common.data.states.*;
import xfacthd.advtech.common.util.data.PropertyHolder;

import java.util.EnumMap;
import java.util.Map;

//TODO: implement upgrade system
public abstract class TileEntityMachine extends TileEntityBase implements ITickableTileEntity, INamedContainerProvider
{
    private static final int HICCUP_TIMEOUT = 20;

    protected final Map<Side, SideAccess> ports = new EnumMap<>(Side.class);
    protected final Map<Direction, SideAccess> cardinalPorts = new EnumMap<>(Direction.class);
    protected MachineLevel level = MachineLevel.BASIC;

    private LazyOptional<MachineItemStackHandler> lazyInternalItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<SidedItemStackHandler>> lazyItemHandlers = new EnumMap<>(Direction.class);
    private LazyOptional<EnergyMachine> lazyEnergyHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<IItemHandler>> neighborCache = new EnumMap<>(Direction.class);

    protected final Map<Direction, SidedItemStackHandler> itemHandlers = new EnumMap<>(Direction.class);
    protected MachineItemStackHandler internalItemHandler;
    protected EnergyMachine energyHandler;

    protected boolean active = false;
    private boolean firstTick = true;
    private long lastHiccup = 0;
    private int changedSlot = -1;

    public TileEntityMachine(TileEntityType<?> type)
    {
        super(type);

        for (Side side : Side.values()) { ports.put(side, SideAccess.NONE); }
        for (Direction side : Direction.values()) { cardinalPorts.put(side, SideAccess.NONE); }

        initCapabilities();
    }

    @Override
    public void tick()
    {
        if (firstTick) { firstTick(); }

        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (changedSlot != -1)
            {
                onSlotChangedInternal(changedSlot);
                changedSlot = -1;
            }
        }
    }

    protected void firstTick()
    {
        firstTick = false;

        onLevelChanged();
        remapPorts();
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

    public final SideAccess getSidePortCardinal(Direction side) { return cardinalPorts.get(side); }

    public final SideAccess getSidePort(Side side) { return ports.get(side); }

    public final void setSidePort(Side side, SideAccess port)
    {
        ports.put(side, port);
        remapPorts();
    }

    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        return SideAccess.values()[(port.ordinal() + 1) % SideAccess.values().length];
    }

    public SideAccess getPriorPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        int ord = (port.ordinal() - 1) % SideAccess.values().length;
        return SideAccess.values()[ord >= 0 ? ord : SideAccess.values().length - 1];
    }

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

    protected final void remapPorts() { remapPortsToFacing(getBlockState().get(PropertyHolder.FACING_HOR)); }

    public final void remapPortsToFacing(Direction facing)
    {
        boolean changed = false;
        for (Side side : Side.values())
        {
            SideAccess setting = ports.get(side);
            Direction dir = side.mapFacing(facing);

            if (cardinalPorts.get(dir) != setting)
            {
                cardinalPorts.put(dir, setting);
                changed = true;
            }

            if (lazyItemHandlers.get(dir).isPresent() && setting.isDisabled())
            {
                lazyItemHandlers.get(dir).invalidate();
            }
            else if (!lazyItemHandlers.get(dir).isPresent() && !setting.isDisabled())
            {
                lazyItemHandlers.put(dir, LazyOptional.of(() -> itemHandlers.get(dir)));
            }
        }

        if (changed) { markFullUpdate(); }
    }

    public abstract void onLevelChanged();

    protected boolean canStart()
    {
        //noinspection ConstantConditions
        return world.getGameTime() - lastHiccup > HICCUP_TIMEOUT;
    }

    protected boolean canFitInSlot(int slot, ItemStack candidate)
    {
        if (candidate.isEmpty()) { return true; }

        ItemStack stack = internalItemHandler.getStackInSlot(slot);
        if (stack.isEmpty()) { return true; }
        if (stack.getCount() >= stack.getMaxStackSize()) { return false; }

        return stack.isItemEqual(candidate);
    }

    protected boolean slotNotEmpty(int slot) { return slotHasAtleast(slot, 1); }

    protected boolean slotHasAtleast(int slot, int count)
    {
        return internalItemHandler.getStackInSlot(slot).getCount() >= count;
    }

    /*
     * Capability stuff
     */

    @Override
    public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (side == null) { return lazyInternalItemHandler.cast(); }

            if (cardinalPorts.get(side).isDisabled()) { return LazyOptional.empty(); }
            return lazyItemHandlers.get(side).cast();
        }
        else if (cap == CapabilityEnergy.ENERGY)
        {
            return lazyEnergyHandler.cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * Super call MUST happen after initializing the {@link TileEntityMachine#internalItemHandler}
     */
    protected void initCapabilities()
    {
        Preconditions.checkState(internalItemHandler != null, "Internal ItemHandler must be initialized before super call!");

        for (Direction side : Direction.values())
        {
            itemHandlers.put(side, new SidedItemStackHandler(this, internalItemHandler, side, internalItemHandler.getSlots()));

            //Filling the map with LazyOptional.empty() saves us some null checks later on
            lazyItemHandlers.put(side, LazyOptional.empty());
        }
    }

    protected final LazyOptional<IItemHandler> getNeighboringHandler(Direction side)
    {
        LazyOptional<IItemHandler> handler = neighborCache.getOrDefault(side, LazyOptional.empty());
        if (!handler.isPresent())
        {
            //noinspection ConstantConditions
            TileEntity te = world.getTileEntity(pos.offset(side));
            if (te != null)
            {
                handler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, side.getOpposite());
                if (handler.isPresent())
                {
                    handler.addListener(itemHandler -> onNeighborInvalidate(side));
                    neighborCache.put(side, handler);
                }
            }
        }
        return handler;
    }

    protected void onNeighborInvalidate(Direction side) { neighborCache.put(side, LazyOptional.empty()); }

    protected abstract int getBaseEnergyCapacity();

    /*
     * Inventory stuff
     */

    public abstract boolean canInsert(Direction side, int slot); //TODO: default impl

    public abstract boolean canExtract(Direction side, int slot);

    public abstract boolean isItemValid(int slot, ItemStack stack);

    public final void onSlotChanged(int slot) { changedSlot = slot; }

    protected abstract void onSlotChangedInternal(int slot);

    /*
     * TE internal stuff
     */

    @Override
    public void validate()
    {
        super.validate();

        for (Direction dir : Direction.values())
        {
            SideAccess setting = cardinalPorts.get(dir);
            if (!lazyItemHandlers.get(dir).isPresent() && !setting.isDisabled())
            {
                lazyItemHandlers.put(dir, LazyOptional.of(() -> itemHandlers.get(dir)));
            }
        }

        lazyInternalItemHandler = LazyOptional.of(() -> internalItemHandler);
        lazyEnergyHandler = LazyOptional.of(() -> energyHandler);
    }

    @Override
    public void remove()
    {
        super.remove();

        for (Direction dir : Direction.values())
        {
            LazyOptional<SidedItemStackHandler> handler = lazyItemHandlers.get(dir);
            if (handler.isPresent()) { handler.invalidate(); }
        }

        lazyInternalItemHandler.invalidate();
        lazyEnergyHandler.invalidate();
    }

    /*
     * NBT stuff
     */

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        level = MachineLevel.values()[nbt.getInt("level")];
        for (Side side : Side.values()) { ports.put(side, SideAccess.values()[nbt.getInt(side.getName())]); }

        active = nbt.getBoolean("active");

        if (world != null) { remapPortsToFacing(getBlockState().get(PropertyHolder.FACING_HOR)); }
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }

        nbt.putBoolean("active", active);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        level = MachineLevel.values()[nbt.getInt("level")];
        for (Side side : Side.values()) { ports.put(side, SideAccess.values()[nbt.getInt(side.getName())]); }

        internalItemHandler.deserializeNBT(nbt.getCompound("inventory"));
        energyHandler.deserializeNBT(nbt.getCompound("energy"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }

        nbt.put("inventory", internalItemHandler.serializeNBT());
        nbt.put("energy", energyHandler.serializeNBT());

        return super.write(nbt);
    }

    public void writeToItemData(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }

        nbt.put("energy", energyHandler.serializeNBT());
    }
}