package xfacthd.advtech.common.tileentity;

import com.google.common.base.Preconditions;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.capability.item.SidedItemStackHandler;
import xfacthd.advtech.common.data.states.*;
import xfacthd.advtech.common.util.data.PropertyHolder;

import java.util.EnumMap;
import java.util.Map;

public abstract class TileEntityInventoryMachine extends TileEntityMachine
{
    protected final Map<Side, SideAccess> ports = new EnumMap<>(Side.class);
    protected final Map<Direction, SideAccess> cardinalPorts = new EnumMap<>(Direction.class);

    private LazyOptional<MachineItemStackHandler> lazyInternalItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<SidedItemStackHandler>> lazyItemHandlers = new EnumMap<>(Direction.class);
    private final Map<Direction, LazyOptional<IItemHandler>> neighborCache = new EnumMap<>(Direction.class);

    protected final Map<Direction, SidedItemStackHandler> itemHandlers = new EnumMap<>(Direction.class);
    protected MachineItemStackHandler internalItemHandler;

    private int changedSlot = -1;

    public TileEntityInventoryMachine(TileEntityType<?> type)
    {
        super(type);

        for (Side side : Side.values()) { ports.put(side, SideAccess.NONE); }
        for (Direction side : Direction.values()) { cardinalPorts.put(side, SideAccess.NONE); }
    }

    @Override
    public void tick()
    {
        super.tick();

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

    @Override
    protected void firstTick()
    {
        remapPorts();
        super.firstTick();
    }

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

        //noinspection ConstantConditions
        if (changed || world.isRemote()) { markFullUpdate(); }
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

    @Override
    public <C> LazyOptional<C> getCapability(Capability<C> cap, Direction side)
    {
        if (cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (side == null) { return lazyInternalItemHandler.cast(); }

            if (cardinalPorts.get(side).isDisabled()) { return LazyOptional.empty(); }
            return lazyItemHandlers.get(side).cast();
        }
        return super.getCapability(cap, side);
    }

    /**
     * Super call MUST happen after initializing the {@link TileEntityInventoryMachine#internalItemHandler}
     */
    @Override
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

    /*
     * Inventory stuff
     */

    public abstract boolean canInsert(Direction side, int slot); //TODO: default impl

    public abstract boolean canExtract(Direction side, int slot);

    public abstract boolean isItemValid(int slot, ItemStack stack);

    public final void onSlotChanged(int slot) { changedSlot = slot; }

    protected abstract void onSlotChangedInternal(int slot);

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
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);

        for (Side side : Side.values()) { ports.put(side, SideAccess.values()[nbt.getInt(side.getName())]); }

        if (world != null) { remapPortsToFacing(getBlockState().get(PropertyHolder.FACING_HOR)); }
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        super.writeNetworkNBT(nbt);

        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);

        for (Side side : Side.values()) { ports.put(side, SideAccess.values()[nbt.getInt(side.getName())]); }

        internalItemHandler.deserializeNBT(nbt.getCompound("inventory"));
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }

        nbt.put("inventory", internalItemHandler.serializeNBT());

        return super.write(nbt);
    }

    @Override
    public void writeToItemData(CompoundNBT nbt)
    {
        super.writeToItemData(nbt);
        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }
    }
}