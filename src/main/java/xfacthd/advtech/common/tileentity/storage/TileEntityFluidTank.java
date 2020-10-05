package xfacthd.advtech.common.tileentity.storage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.ITickableTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import xfacthd.advtech.common.capability.fluid.SidedFluidHandler;
import xfacthd.advtech.common.capability.fluid.TankFluidHandler;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityBase;
import xfacthd.advtech.common.util.interfaces.ITileFluidHandler;

public class TileEntityFluidTank extends TileEntityBase implements ITickableTileEntity, ITileFluidHandler
{
    private static final int BASE_CAPACITY = 5000;
    private static final int BASE_TRANSFER = 1000;

    private final TankFluidHandler internalFluidHandler = new TankFluidHandler(this, BASE_CAPACITY, BASE_TRANSFER);
    private final SidedFluidHandler topFluidHandler = new SidedFluidHandler(this, internalFluidHandler, Direction.UP);
    private final SidedFluidHandler bottomFluidHandler  = new SidedFluidHandler(this, internalFluidHandler, Direction.DOWN);

    private LazyOptional<IFluidHandler> topLazyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> bottomLazyHandler = LazyOptional.empty();
    private LazyOptional<IFluidHandler> neighborLazyHandler = LazyOptional.empty();

    private MachineLevel level = MachineLevel.BASIC;
    private SideAccess mode = SideAccess.NONE;
    private boolean firstTick = true;
    private int fluidLevel = 0;

    public TileEntityFluidTank() { super(TileEntityTypes.tileTypeFluidTank); }

    @Override
    public void tick()
    {
        if (firstTick) { firstTick(); }

        //noinspection ConstantConditions
        if (!world.isRemote() && mode.isOutput() && !internalFluidHandler.isEmpty())
        {
            if (!neighborLazyHandler.isPresent())
            {
                TileEntity te = world.getTileEntity(pos.down());
                if (te != null)
                {
                    neighborLazyHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, Direction.UP);
                    if (neighborLazyHandler.isPresent())
                    {
                        neighborLazyHandler.addListener(handler -> neighborLazyHandler = LazyOptional.empty());
                    }
                }
            }

            neighborLazyHandler.ifPresent(handler ->
            {
                FluidStack fluid = internalFluidHandler.drain(Integer.MAX_VALUE, IFluidHandler.FluidAction.SIMULATE);
                if (!fluid.isEmpty())
                {
                    int toTransfer = handler.fill(fluid, IFluidHandler.FluidAction.EXECUTE);
                    if (toTransfer > 0)
                    {
                        internalFluidHandler.drain(toTransfer, IFluidHandler.FluidAction.EXECUTE);
                        markForSync();
                    }
                }
            });
        }

        if (!world.isRemote() && needsSync()) { sendSyncPacket(); }
    }

    private void firstTick()
    {
        firstTick = false;
        onLevelChanged();
    }

    private void onLevelChanged()
    {
        int mult = (int)Math.pow(2, level.ordinal());
        int capacity = BASE_CAPACITY * mult;
        int maxReceive = BASE_TRANSFER * mult;
        internalFluidHandler.reconfigure(capacity, maxReceive);
        markForSync();
    }

    public void upgrade(MachineLevel level)
    {
        this.level = level;
        onLevelChanged();
    }

    public void switchMode()
    {
        switch (mode)
        {
            case NONE:
            {
                mode = SideAccess.INPUT_ALL;
                break;
            }
            case INPUT_ALL:
            {
                mode = SideAccess.OUTPUT_ALL;
                break;
            }
            case OUTPUT_ALL:
            {
                mode = SideAccess.NONE;
                break;
            }
        }
        markForSync();
    }

    public MachineLevel getLevel() { return level; }

    public SideAccess getPortMode() { return mode; }

    /*
     * Tank handling
     */

    @Override
    public boolean canFill(Direction side)
    {
        if (side == Direction.UP) { return true; }
        if (side == Direction.DOWN) { return mode.isInput(); }
        return false;
    }

    @Override
    public boolean canDrain(Direction side)
    {
        if (side == Direction.DOWN) { return mode.isOutput(); }
        return false;
    }

    public ItemStack handleContainerInteraction(ItemStack container, PlayerEntity player)
    {
        PlayerInvWrapper inv = new PlayerInvWrapper(player.inventory);

        //noinspection ConstantConditions
        FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(container, internalFluidHandler, inv, Integer.MAX_VALUE, player, !world.isRemote());
        if (result.isSuccess()) { return result.getResult(); }

        result = FluidUtil.tryFillContainerAndStow(container, internalFluidHandler, inv, Integer.MAX_VALUE, player, !world.isRemote());
        return result.getResult();
    }

    public FluidStack getContents() { return internalFluidHandler.getFluid(); }

    public float getFluidHeight()
    {
        FluidStack fluid = internalFluidHandler.getFluid();
        if (fluid.isEmpty()) { return 0; }

        int capacity = internalFluidHandler.getCapacity();
        return (float)fluid.getAmount() / (float)capacity;
    }

    public int getLightLevel()
    {
        FluidStack fluid = internalFluidHandler.getFluid();
        if (!fluid.isEmpty())
        {
            return fluid.getFluid().getAttributes().getLuminosity(fluid);
        }
        return 0;
    }

    public void onContentsChanged()
    {
        int newLevel = (int)(getFluidHeight() * 16F);
        if (fluidLevel != newLevel)
        {
            fluidLevel = newLevel;
            markForSync();
        }
        else
        {
            markDirty();
        }
    }

    /*
     * Capability stuff
     */

    @Override
    public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side)
    {
        if (cap == CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY)
        {
            if (side == Direction.UP) { return topLazyHandler.cast(); }
            if (side == Direction.DOWN && !mode.isDisabled()) { return bottomLazyHandler.cast(); }
        }
        return super.getCapability(cap, side);
    }

    @Override
    public void validate()
    {
        super.validate();

        topLazyHandler = LazyOptional.of(() -> topFluidHandler);
        bottomLazyHandler = LazyOptional.of(() -> bottomFluidHandler);
    }

    @Override
    public void remove()
    {
        super.remove();

        topLazyHandler.invalidate();
        topLazyHandler = LazyOptional.empty();
        bottomLazyHandler.invalidate();
        bottomLazyHandler = LazyOptional.empty();
    }

    /*
     * NBT stuff
     */

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putInt("mode", mode.ordinal());

        CompoundNBT tag = new CompoundNBT();
        internalFluidHandler.writeToNBT(tag);
        nbt.put("content", tag);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        level = MachineLevel.values()[nbt.getInt("level")];
        mode = SideAccess.values()[nbt.getInt("mode")];
        internalFluidHandler.readFromNBT(nbt.getCompound("content"));
    }

    @Override
    public void writeSyncPacket(PacketBuffer buffer)
    {
        buffer.writeInt(level.ordinal());
        buffer.writeInt(mode.ordinal());
        buffer.writeCompoundTag(internalFluidHandler.writeToNBT(new CompoundNBT()));
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

        int mode = buffer.readInt();
        if (mode != this.mode.ordinal())
        {
            this.mode = SideAccess.values()[mode];
            markRenderUpdate();
        }

        //noinspection ConstantConditions
        internalFluidHandler.readFromNBT(buffer.readCompoundTag());
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putInt("mode", mode.ordinal());

        CompoundNBT tag = new CompoundNBT();
        internalFluidHandler.writeToNBT(tag);
        nbt.put("content", tag);

        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        level = MachineLevel.values()[nbt.getInt("level")];
        mode = SideAccess.values()[nbt.getInt("mode")];

        internalFluidHandler.readFromNBT(nbt.getCompound("content"));
    }

    public void writeToItemData(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putInt("mode", mode.ordinal());

        CompoundNBT tag = new CompoundNBT();
        internalFluidHandler.writeToNBT(tag);
        nbt.put("content", tag);
    }
}