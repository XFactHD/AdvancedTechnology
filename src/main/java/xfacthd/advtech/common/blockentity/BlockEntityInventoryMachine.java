package xfacthd.advtech.common.blockentity;

import com.google.common.base.Preconditions;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.capability.item.SidedItemStackHandler;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.util.MachineUtils;
import xfacthd.advtech.common.util.interfaces.ISidedMachine;

import java.util.EnumMap;
import java.util.Map;

public abstract class BlockEntityInventoryMachine extends BlockEntityMachine implements ISidedMachine
{
    protected final Map<Side, SideAccess> ports = new EnumMap<>(Side.class);
    protected final Map<Direction, SideAccess> cardinalPorts = new EnumMap<>(Direction.class);

    private LazyOptional<MachineItemStackHandler> lazyInternalItemHandler = LazyOptional.empty();
    private final Map<Direction, LazyOptional<SidedItemStackHandler>> lazyItemHandlers = new EnumMap<>(Direction.class);
    private final Map<Direction, LazyOptional<IItemHandler>> neighborCache = new EnumMap<>(Direction.class);

    protected final Map<Direction, SidedItemStackHandler> itemHandlers = new EnumMap<>(Direction.class);
    protected MachineItemStackHandler internalItemHandler;

    private int changedSlot = -1;
    private boolean cycleComplete = false;
    private boolean forceOutput = false;

    public BlockEntityInventoryMachine(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, false);

        for (Side side : Side.values()) { ports.put(side, SideAccess.NONE); }
        for (Direction side : Direction.values()) { cardinalPorts.put(side, SideAccess.NONE); }

        initCapabilities();
    }

    @Override
    protected void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (changedSlot != -1)
            {
                onSlotChangedInternal(changedSlot);
                changedSlot = -1;
            }

            if (cycleComplete && forceOutput)
            {
                cycleComplete = false;
                pushOutputs();
            }
        }
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        for (Direction dir : Direction.values())
        {
            SideAccess setting = cardinalPorts.get(dir);
            if (!lazyItemHandlers.get(dir).isPresent() && !setting.isDisabled())
            {
                lazyItemHandlers.put(dir, LazyOptional.of(() -> itemHandlers.get(dir)));
            }
        }

        lazyInternalItemHandler = LazyOptional.of(() -> internalItemHandler);

        remapPorts();
    }

    @Override
    public void setRemoved()
    {
        super.setRemoved();

        for (Direction dir : Direction.values())
        {
            LazyOptional<SidedItemStackHandler> handler = lazyItemHandlers.get(dir);
            if (handler.isPresent()) { handler.invalidate(); }
        }

        lazyInternalItemHandler.invalidate();
    }

    @Override
    public final SideAccess getSidePort(Side side) { return ports.get(side); }

    @Override
    public final void setSidePort(Side side, SideAccess port)
    {
        ports.put(side, port);
        remapPorts();
    }

    public SideAccess getNextPortSetting(Side side) //TODO: check if these can be made protected
    {
        if (side == getFrontSide()) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        return SideAccess.values()[(port.ordinal() + 1) % SideAccess.values().length];
    }

    public SideAccess getPriorPortSetting(Side side) //TODO: check if these can be made protected
    {
        if (side == getFrontSide()) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        int ord = (port.ordinal() - 1) % SideAccess.values().length;
        return SideAccess.values()[ord >= 0 ? ord : SideAccess.values().length - 1];
    }

    /**
     * Returns the {@link Side} that shows the machine specific texture and therefore cannot be configured as a port
     * Must be constant!!!
     */
    public Side getFrontSide() { return Side.FRONT; }

    @Override
    public final void remapPorts()
    {
        MachineUtils.remapMachinePorts(this, ports, cardinalPorts, itemHandlers, lazyItemHandlers);
    }

    protected boolean canFitInSlot(int slot, ItemStack candidate)
    {
        if (candidate.isEmpty()) { return true; }

        ItemStack stack = internalItemHandler.getStackInSlot(slot);
        if (stack.isEmpty()) { return true; }
        if (stack.getCount() >= stack.getMaxStackSize()) { return false; }

        return stack.sameItemStackIgnoreDurability(candidate);
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
     * Super call MUST happen after initializing the {@link BlockEntityInventoryMachine#internalItemHandler}
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
            BlockEntity te = level().getBlockEntity(worldPosition.relative(side));
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

    /**
     * Wether the machine supports force-pushing
     * Must be constant!!!
     */
    public abstract boolean canForcePush();

    public void setForceOutput(boolean force)
    {
        forceOutput = force;
        setChanged();
    }

    public void switchForceOutput() { setForceOutput(!forceOutput); }

    public boolean shouldForceOutput() { return forceOutput; }

    protected abstract void pushOutputs();

    public abstract boolean canInsert(Direction side, int slot);

    public abstract boolean canExtract(Direction side, int slot);

    public abstract boolean isItemValid(int slot, ItemStack stack);

    protected boolean needSlotNotification() { return true; }

    public final void onSlotChanged(int slot)
    {
        if (needSlotNotification())
        {
            changedSlot = slot;
        }
    }

    protected abstract void onSlotChangedInternal(int slot);

    protected void setCycleComplete() { cycleComplete = true; }

    @Override
    public void readNetworkNBT(CompoundTag nbt)
    {
        super.readNetworkNBT(nbt);

        for (Side side : Side.values()) { ports.put(side, SideAccess.byId(nbt.getInt(side.getSerializedName()))); }

        if (level != null) { remapPorts(); }
    }

    @Override
    public void writeNetworkNBT(CompoundTag nbt)
    {
        super.writeNetworkNBT(nbt);

        for (Side side : Side.values()) { nbt.putInt(side.getSerializedName(), ports.get(side).ordinal()); }
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer)
    {
        super.writeSyncPacket(buffer);

        for (Side side : Side.values()) { buffer.writeInt(ports.get(side).ordinal()); }
    }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer)
    {
        super.readSyncPacket(buffer);

        for (Side side : Side.values())
        {
            int mode = buffer.readInt();
            if (mode != ports.get(side).ordinal())
            {
                ports.put(side, SideAccess.values()[mode]);
                markRenderUpdate();
            }
        }
        remapPorts();
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        for (Side side : Side.values()) { ports.put(side, SideAccess.byId(nbt.getInt(side.getSerializedName()))); }

        internalItemHandler.deserializeNBT(nbt.getCompound("inventory"));
        forceOutput = nbt.getBoolean("force");
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);

        for (Side side : Side.values()) { nbt.putInt(side.getSerializedName(), ports.get(side).ordinal()); }

        nbt.put("inventory", internalItemHandler.serializeNBT());
        nbt.putBoolean("force", forceOutput);
    }

    @Override
    public void writeToItemData(CompoundTag nbt)
    {
        super.writeToItemData(nbt);
        for (Side side : Side.values()) { nbt.putInt(side.getSerializedName(), ports.get(side).ordinal()); }
        nbt.putBoolean("force", forceOutput);
    }
}