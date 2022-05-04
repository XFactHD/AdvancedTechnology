package xfacthd.advtech.common.blockentity.machine;

import net.minecraft.core.*;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.CropBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.util.IRangedMachine;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.capability.energy.EnergySink;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.machine.ContainerMenuHarvester;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.subtypes.Enhancement;
import xfacthd.advtech.common.util.Utils;

import java.util.Collections;
import java.util.List;

public class BlockEntityHarvester extends BlockEntityInventoryMachine implements IRangedMachine
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".harvester");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;
    private static final int ENERGY_PER_BLOCK = 400;

    private int energyConsumption = 0;
    private int radius = 1;
    private int progress = 0;
    private Direction facing = Direction.NORTH;
    private BlockPos startPos = BlockPos.ZERO;
    private BlockPos.MutableBlockPos scanPos = BlockPos.ZERO.mutable();
    private boolean showArea = false;

    public BlockEntityHarvester(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.HARVESTER), pos, state);
    }

    @Override
    protected void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (canRun(progress == 0))
            {
                if (!active && energyHandler.getEnergyStored() >= BASE_CONSUMPTION)
                {
                    setActive(true);
                }
                else if (active && energyHandler.getEnergyStored() < BASE_CONSUMPTION)
                {
                    setActive(false);
                }

                if (active)
                {
                    float mult = (float) energyHandler.getEnergyStored() / (float) energyHandler.getMaxEnergyStored();
                    int actualConsumption = Math.max((int) Math.ceil(energyConsumption * mult), BASE_CONSUMPTION);
                    int result = energyHandler.extractEnergyInternal(actualConsumption, false);

                    progress += result;

                    if (progress >= ENERGY_PER_BLOCK)
                    {
                        progress = 0;
                        tryHarvestCrop();
                        setCycleComplete();

                        int diffX = scanPos.getX() - startPos.getX();
                        if (diffX >= radius * 2)
                        {
                            scanPos = scanPos.move(Direction.WEST, radius * 2);

                            int diffZ = scanPos.getZ() - startPos.getZ();
                            if (diffZ >= radius * 2)
                            {
                                scanPos = scanPos.move(Direction.NORTH, radius * 2);
                            }
                            else
                            {
                                scanPos = scanPos.move(Direction.SOUTH);
                            }
                        }
                        else
                        {
                            scanPos = scanPos.move(Direction.EAST);
                        }
                        markForSync();
                    }
                }
            }
        }

        super.tickInternal();
    }

    private void tryHarvestCrop()
    {
        List<ItemStack> drops = Collections.emptyList();

        BlockState state = level().getBlockState(scanPos);
        if (state.getBlock() instanceof CropBlock crops)
        {
            if (crops.isMaxAge(state))
            {
                drops = Block.getDrops(state, (ServerLevel) level(), scanPos, null);
            }
        }
        //else if (state.getBlock() instanceof ...) { } //TODO: special case certain plants like sugar cane

        if (!drops.isEmpty() && canStoreDrops(drops))
        {
            level().destroyBlock(scanPos, false);

            for (ItemStack stack : drops)
            {
                ItemHandlerHelper.insertItem(internalItemHandler, stack, false);
            }
        }
    }

    private boolean canStoreDrops(List<ItemStack> drops)
    {
        List<ItemStack> cache = NonNullList.withSize(9, ItemStack.EMPTY);

        for (ItemStack stack : drops)
        {
            ItemStack copy = stack.copy();
            for (int i = 0; i < 9; i++)
            {
                ItemStack slot = internalItemHandler.getStackInSlot(i);
                if (!cache.get(i).isEmpty())
                {
                    ItemStack cached = cache.get(i);
                    if (ItemHandlerHelper.canItemStacksStack(copy, cached))
                    {
                        int limit = Math.min(internalItemHandler.getSlotLimit(i), stack.getMaxStackSize());
                        int size = cached.getCount();
                        int total = size + copy.getCount();
                        if (total <= limit)
                        {
                            cached.setCount(total);
                            copy = ItemStack.EMPTY;
                            break;
                        }

                        int toAdd = limit - size;
                        if (toAdd > 0)
                        {
                            cached.grow(toAdd);
                            copy.shrink(toAdd);
                        }
                    }
                }
                else if (!slot.isEmpty())
                {
                    int size = copy.getCount();
                    copy = internalItemHandler.insertItem(i, copy, true);
                    int remainder = copy.getCount();
                    if (remainder < size) //Something was inserted
                    {
                        ItemStack toCache = internalItemHandler.getStackInSlot(i).copy();
                        toCache.grow(size - remainder);
                        cache.set(i, toCache);
                    }

                    if (copy.isEmpty())
                    {
                        break;
                    }
                }
                else //Slot empty and cache empty
                {
                    cache.set(i, copy);
                    copy = ItemStack.EMPTY;
                    break;
                }
            }
            if (!copy.isEmpty())
            {
                return false;
            }
        }

        return true;
    }

    @Override
    public boolean canForcePush() { return true; }

    @Override
    protected void pushOutputs()
    {
        boolean empty = true;
        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = internalItemHandler.getStackInSlot(i);
            if (!stack.isEmpty()) { empty = false; }
        }
        if (empty) { return; }

        boolean worked = false;
        for (Direction dir : Direction.values())
        {
            SideAccess port = cardinalPorts.get(dir);
            if (port.isOutput())
            {
                LazyOptional<IItemHandler> adj = getNeighboringHandler(dir);
                worked |= adj.map(handler ->
                {
                    boolean changed = false;
                    for (int i = 0; i < 9; i++)
                    {
                        ItemStack stack = internalItemHandler.getStackInSlot(i);
                        if (!stack.isEmpty())
                        {
                            ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                            internalItemHandler.setStackInSlot(i, remainder);
                            changed = true;
                        }
                    }
                    return changed;
                }).orElse(false);
            }
        }
        if (worked) { setChanged(); }
    }

    @Override
    public boolean canInsert(Direction side, int slot) { return false; }

    @Override
    public boolean canExtract(Direction side, int slot) { return cardinalPorts.get(side).isOutput() && slot < 9; }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return true; }

    @Override
    protected boolean needSlotNotification() { return false; }

    @Override
    protected void onSlotChangedInternal(int slot) { }

    @Override
    public float getProgress() { return progress; }

    @Override
    public boolean supportsEnhancements() { return true; }

    @Override
    public boolean canInstallEnhancement(Enhancement upgrade)
    {
        if (!super.canInstallEnhancement(upgrade)) { return false; }
        return upgrade == Enhancement.RANGE;
    }

    @Override
    public void installEnhancement(Enhancement upgrade, int level)
    {
        if (level().isClientSide()) { return; }

        if (upgrade == Enhancement.RANGE)
        {
            radius = switch (level)
            {
                case 0 -> 4;
                case 1 -> 7;
                case 2 -> 10;
                case 3 -> 13;
                default -> throw new IllegalArgumentException("Invalid range upgrade level!");
            };

            resetScanPos();
            markForSync();
        }
    }

    @Override
    public void removeEnhancement(Enhancement upgrade)
    {
        if (level().isClientSide()) { return; }

        if (upgrade == Enhancement.RANGE)
        {
            radius = 1;

            resetScanPos();
            markForSync();
        }
    }

    @Override
    public void onPortMappingChanged(Direction facing)
    {
        this.facing = facing;
        if (!level().isClientSide())
        {
            resetScanPos();
        }
    }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.TOP) { return SideAccess.NONE; }

        SideAccess mode = ports.get(side);
        return switch (mode)
        {
            case NONE -> SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL -> SideAccess.NONE;
            default -> throw new IllegalStateException("Invalid port setting: " + mode.getSerializedName());
        };
    }

    @Override
    public SideAccess getPriorPortSetting(Side side)
    {
        if (side == Side.TOP) { return SideAccess.NONE; }

        SideAccess mode = ports.get(side);
        return switch (mode)
        {
            case NONE -> SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL -> SideAccess.NONE;
            default -> throw new IllegalStateException("Invalid port setting: " + mode.getSerializedName());
        };
    }

    @Override
    protected void initCapabilities()
    {
        internalItemHandler = new MachineItemStackHandler(this, 9);
        energyHandler = new EnergySink(getBaseEnergyCapacity(), BASE_CONSUMPTION * 10);

        super.initCapabilities();
    }

    @Override
    public void onLevelChanged()
    {
        int mult = (int)Math.pow(2, level.ordinal());
        energyConsumption = BASE_CONSUMPTION * mult;

        int capacity = getBaseEnergyCapacity() * mult;
        int maxReceive = energyConsumption * 10;
        energyHandler.reconfigure(capacity, maxReceive, 0);
    }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    public int getRadius() { return radius; }

    @Override
    public boolean showArea() { return showArea; }

    public void switchShowArea() { showArea = !showArea; }

    @Override
    public Direction getAreaOffsetDir() { return facing; }

    @Override
    public boolean showScanPos() { return showArea && active; }

    @Override
    public BlockPos getScanPos() { return scanPos; }

    private void resetScanPos()
    {
        if (facing.getAxis() == Direction.Axis.X)
        {
            startPos = worldPosition.north(radius).relative(facing, facing == Direction.EAST ? 1 : (radius * 2 + 1));
        }
        else
        {
            startPos = worldPosition.relative(facing, facing == Direction.SOUTH ? 1 : (radius * 2 + 1)).west(radius);
        }

        scanPos = startPos.mutable();
    }

    @Override
    public void writeNetworkNBT(CompoundTag nbt)
    {
        super.writeNetworkNBT(nbt);

        nbt.putInt("radius", radius);
        nbt.putLong("scanpos", scanPos.asLong());
    }

    @Override
    public void readNetworkNBT(CompoundTag nbt)
    {
        super.readNetworkNBT(nbt);

        radius = nbt.getInt("radius");
        scanPos = BlockPos.of(nbt.getLong("scanpos")).mutable();
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer)
    {
        super.writeSyncPacket(buffer);
        buffer.writeInt(radius);
        buffer.writeBlockPos(scanPos);
    }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer)
    {
        super.readSyncPacket(buffer);
        radius = buffer.readInt();
        scanPos = buffer.readBlockPos().mutable();
    }

    @Override
    public void saveAdditional(CompoundTag nbt)
    {
        super.saveAdditional(nbt);
        nbt.putInt("radius", radius);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        radius = nbt.getInt("radius");
    }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
    {
        return new ContainerMenuHarvester(windowId, this, inventory);
    }

    @Override
    public AABB getRenderBoundingBox() { return showArea ? INFINITE_EXTENT_AABB : Utils.NULL_AABB; }
}