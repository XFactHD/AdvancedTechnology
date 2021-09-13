package xfacthd.advtech.common.blockentity.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.*;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.items.wrapper.PlayerInvWrapper;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.capability.fluid.SidedFluidHandler;
import xfacthd.advtech.common.capability.fluid.TankFluidHandler;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.blockentity.BlockEntityBase;
import xfacthd.advtech.common.util.interfaces.ITileFluidHandler;
import xfacthd.advtech.common.util.interfaces.IUpgradableMachine;

public class BlockEntityFluidTank extends BlockEntityBase implements ITileFluidHandler, IUpgradableMachine
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

    public BlockEntityFluidTank(BlockPos pos, BlockState state)
    {
        super(ATContent.BE_TYPE_FLUID_TANK.get(), pos, state);
    }

    public void tick()
    {
        if (firstTick) { onLoad(); } //TODO: remove when onLoad() PR is merged

        if (!level().isClientSide() && mode.isOutput() && !internalFluidHandler.isEmpty())
        {
            if (!neighborLazyHandler.isPresent())
            {
                BlockEntity te = level().getBlockEntity(worldPosition.below());
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

        if (!level().isClientSide() && needsSync()) { sendSyncPacket(); }
    }

    @Override
    public void onLoad()
    {
        firstTick = false;

        topLazyHandler = LazyOptional.of(() -> topFluidHandler);
        bottomLazyHandler = LazyOptional.of(() -> bottomFluidHandler);
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
            case NONE -> mode = SideAccess.INPUT_ALL;
            case INPUT_ALL -> mode = SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL -> mode = SideAccess.NONE;
        }
        markForSync();
    }

    public MachineLevel getMachineLevel() { return level; }

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

    public ItemStack handleContainerInteraction(ItemStack container, Player player)
    {
        PlayerInvWrapper inv = new PlayerInvWrapper(player.getInventory());

        FluidActionResult result = FluidUtil.tryEmptyContainerAndStow(container, internalFluidHandler, inv, Integer.MAX_VALUE, player, !level().isClientSide());
        if (result.isSuccess()) { return result.getResult(); }

        result = FluidUtil.tryFillContainerAndStow(container, internalFluidHandler, inv, Integer.MAX_VALUE, player, !level().isClientSide());
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
            setChanged();
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
    public void setRemoved()
    {
        super.setRemoved();

        topLazyHandler.invalidate();
        topLazyHandler = LazyOptional.empty();
        bottomLazyHandler.invalidate();
        bottomLazyHandler = LazyOptional.empty();
    }

    /*
     * NBT stuff
     */

    @Override
    public void writeNetworkNBT(CompoundTag nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putInt("mode", mode.ordinal());

        CompoundTag tag = new CompoundTag();
        internalFluidHandler.writeToNBT(tag);
        nbt.put("content", tag);
    }

    @Override
    public void readNetworkNBT(CompoundTag nbt)
    {
        level = MachineLevel.values()[nbt.getInt("level")];
        mode = SideAccess.values()[nbt.getInt("mode")];
        internalFluidHandler.readFromNBT(nbt.getCompound("content"));
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer)
    {
        buffer.writeInt(level.ordinal());
        buffer.writeInt(mode.ordinal());
        buffer.writeNbt(internalFluidHandler.writeToNBT(new CompoundTag()));
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

        int mode = buffer.readInt();
        if (mode != this.mode.ordinal())
        {
            this.mode = SideAccess.values()[mode];
            markRenderUpdate();
        }

        //noinspection ConstantConditions
        internalFluidHandler.readFromNBT(buffer.readNbt());
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putInt("mode", mode.ordinal());

        CompoundTag tag = new CompoundTag();
        internalFluidHandler.writeToNBT(tag);
        nbt.put("content", tag);

        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);
        level = MachineLevel.values()[nbt.getInt("level")];
        mode = SideAccess.values()[nbt.getInt("mode")];

        internalFluidHandler.readFromNBT(nbt.getCompound("content"));
    }

    public void writeToItemData(CompoundTag nbt)
    {
        nbt.putInt("level", level.ordinal());
        nbt.putInt("mode", mode.ordinal());

        CompoundTag tag = new CompoundTag();
        internalFluidHandler.writeToNBT(tag);
        nbt.put("content", tag);
    }
}