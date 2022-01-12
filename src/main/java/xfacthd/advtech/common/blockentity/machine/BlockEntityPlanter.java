package xfacthd.advtech.common.blockentity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.util.IRangedMachine;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.capability.energy.EnergySink;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.machine.ContainerMenuPlanter;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.subtypes.Enhancement;
import xfacthd.advtech.common.util.LogHelper;
import xfacthd.advtech.common.util.Utils;

public class BlockEntityPlanter extends BlockEntityInventoryMachine implements IRangedMachine
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".planter");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;
    private static final int ENERGY_PER_BLOCK = 400;

    private final ItemStack[] filters = new ItemStack[9];
    private int energyConsumption = 0;
    private int radius = 1;
    private int patchSize = 1;
    private int progress = 0;
    private BlockPos.MutableBlockPos scanPos = BlockPos.ZERO.mutable();
    private boolean showArea = false;

    public BlockEntityPlanter(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.PLANTER), pos, state);
        for (int i = 0; i < 9; i++) { filters[i] = ItemStack.EMPTY; }
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
                        tryPlantCrop();

                        int diffX = scanPos.getX() - worldPosition.getX();
                        if (diffX >= radius)
                        {
                            scanPos = scanPos.move(Direction.WEST, radius * 2);

                            int diffZ = scanPos.getZ() - worldPosition.getZ();
                            if (diffZ >= radius)
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

    private void tryPlantCrop()
    {
        if (level().isEmptyBlock(scanPos))
        {
            int patchX = ((scanPos.getX() - worldPosition.getX()) + radius) / patchSize;
            int patchZ = ((scanPos.getZ() - worldPosition.getZ()) + radius) / patchSize;
            int slot = (patchZ * 3) + patchX;

            if (slot < 0 || slot >= 9)
            {
                LogHelper.warn("Invalid slot! Slot:" + slot + " Pos: " + scanPos + " PatchX: " + patchX + " PatchZ: " + patchZ);
                return;
            }

            ItemStack seeds = internalItemHandler.getStackInSlot(slot);
            if (!seeds.isEmpty())
            {
                BlockPos soilPos = scanPos.below();
                BlockState soilState = level().getBlockState(soilPos);

                BlockState plantState = null;
                if (seeds.getItem() instanceof IPlantable plantable)
                {
                    if (plantable.getPlantType(level(), scanPos) == PlantType.CROP)
                    {
                        if (soilState.canSustainPlant(level(), soilPos, Direction.UP, plantable))
                        {
                            plantState = plantable.getPlant(level(), scanPos);
                        }
                    }
                }
                else
                {
                    plantState = findVanillaCropState(seeds, soilState);
                }

                if (plantState != null)
                {
                    level().setBlockAndUpdate(scanPos, plantState);
                    plantState.getBlock().setPlacedBy(level(), scanPos, plantState, null, seeds);

                    playPlaceSound(plantState);

                    internalItemHandler.extractItem(slot, 1, false);
                }
            }
        }
    }

    private BlockState findVanillaCropState(ItemStack stack, BlockState soilState)
    {
        boolean farmland = soilState.getBlock() == Blocks.FARMLAND;
        boolean sand = soilState.getBlock() == Blocks.SAND;

        Item seeds = stack.getItem();
        if (seeds == Items.WHEAT_SEEDS && farmland)
        {
            return Blocks.WHEAT.defaultBlockState();
        }
        else if (seeds == Items.POTATO && farmland)
        {
            return Blocks.POTATOES.defaultBlockState();
        }
        else if (seeds == Items.CARROT && farmland)
        {
            return Blocks.CARROTS.defaultBlockState();
        }
        else if (seeds == Items.BEETROOT_SEEDS && farmland)
        {
            return Blocks.BEETROOTS.defaultBlockState();
        }
        else if (seeds == Items.SUGAR_CANE && sand)
        {
            return null;//return Blocks.SUGAR_CANE.getDefaultState(); //TODO: implement special casing in harvester before activating
        }
        return null;
    }

    private void playPlaceSound(BlockState plantState)
    {
        SoundType type = plantState.getSoundType(level(), scanPos, null);
        SoundEvent event = type.getPlaceSound();

        level().playSound(null, scanPos, event, SoundSource.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
    }

    @Override
    public void onLoad()
    {
        super.onLoad();

        if (!level().isClientSide())
        {
            resetScanPos();
        }
    }

    @Override
    public boolean canInsert(Direction side, int slot) { return cardinalPorts.get(side).isInput() && slot < 9; }

    @Override
    public boolean canExtract(Direction side, int slot) { return false; }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot < 9)
        {
            if (stack.isEmpty()) { return true; }

            if (filters[slot].isEmpty())
            {
                Item item = stack.getItem();
                if (item == Items.WHEAT_SEEDS) { return true; }
                else if (item == Items.POTATO) { return true; }
                else if (item == Items.CARROT) { return true; }
                else if (item == Items.BEETROOT_SEEDS) { return true; }
                else if (item == Items.NETHER_WART) { return true; }

                if (item instanceof IPlantable)
                {
                    return ((IPlantable) item).getPlantType(level(), worldPosition) == PlantType.CROP;
                }
            }
            return filters[slot].sameItemStackIgnoreDurability(stack);
        }
        return false;
    }

    @Override
    protected boolean needSlotNotification() { return false; }

    @Override
    protected void onSlotChangedInternal(int slot) { }

    @Override
    public boolean canForcePush() { return false; }

    @Override
    protected void pushOutputs() { /*NOOP*/ }

    @Override
    public Side getFrontSide() { return Side.TOP; }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.TOP) { return SideAccess.NONE; }

        SideAccess mode = ports.get(side);
        return switch (mode)
        {
            case NONE -> SideAccess.INPUT_ALL;
            case INPUT_ALL -> SideAccess.NONE;
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
            case NONE -> SideAccess.INPUT_ALL;
            case INPUT_ALL -> SideAccess.NONE;
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
    public float getProgress() { return (float)progress / (float)ENERGY_PER_BLOCK; }

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
            switch (level)
            {
                case 0 -> {
                    radius = 4;
                    patchSize = 3;
                }
                case 1 -> {
                    radius = 7;
                    patchSize = 5;
                }
                case 2 -> {
                    radius = 10;
                    patchSize = 7;
                }
                case 3 -> {
                    radius = 13;
                    patchSize = 9;
                }
            }

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
            patchSize = 1;

            resetScanPos();
            markForSync();
        }
    }

    public ItemStack getFilterStack(int idx) { return filters[idx]; }

    public void configureFilter(boolean clear) //FIXME: setting the filter clears the inventory
    {
        for (int i = 0; i < 9; i++)
        {
            if (clear)
            {
                filters[i] = ItemStack.EMPTY;
            }
            else
            {
                ItemStack stack = internalItemHandler.getStackInSlot(i);
                if (!stack.isEmpty()) { stack.setCount(1); }
                filters[i] = stack;
            }
        }
        markForSync();
    }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    public int getRadius() { return radius; }

    @Override
    public boolean isAreaCentered() { return true; }

    @Override
    public boolean showArea() { return showArea; }

    public void switchShowArea() { showArea = !showArea; }

    @Override
    public int getAreaOffsetY() { return 2; }

    @Override
    public boolean showScanPos() { return showArea && active; }

    @Override
    public BlockPos getScanPos() { return scanPos; }

    private void resetScanPos() { scanPos = worldPosition.mutable().move(Direction.NORTH, radius).move(Direction.WEST, radius).move(Direction.UP, 2); }

    @Override
    public void writeNetworkNBT(CompoundTag nbt)
    {
        super.writeNetworkNBT(nbt);

        nbt.putInt("radius", radius);
        nbt.putLong("scanpos", scanPos.asLong());

        ListTag filterList = new ListTag();
        for (int i = 0; i < 9; i++)
        {
            filterList.add(filters[i].save(new CompoundTag()));
        }
        nbt.put("filters", filterList);
    }

    @Override
    public void readNetworkNBT(CompoundTag nbt)
    {
        super.readNetworkNBT(nbt);

        radius = nbt.getInt("radius");
        scanPos = BlockPos.of(nbt.getLong("scanpos")).mutable();

        ListTag filterList = nbt.getList("filters", Tag.TAG_COMPOUND);
        for (int i = 0; i < 9; i++)
        {
            filters[i] = ItemStack.of(filterList.getCompound(i));
        }
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer)
    {
        super.writeSyncPacket(buffer);

        buffer.writeInt(radius);
        buffer.writeBlockPos(scanPos);

        for (int i = 0; i < 9; i++)
        {
            buffer.writeItemStack(filters[i], true);
        }
    }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer)
    {
        super.readSyncPacket(buffer);

        radius = buffer.readInt();
        scanPos = buffer.readBlockPos().mutable();

        for (int i = 0; i < 9; i++)
        {
            filters[i] = buffer.readItem();
        }
    }

    @Override
    public CompoundTag save(CompoundTag nbt)
    {
        ListTag filterList = new ListTag();
        for (int i = 0; i < 9; i++)
        {
            filterList.add(filters[i].save(new CompoundTag()));
        }
        nbt.put("filters", filterList);

        nbt.putInt("radius", radius);
        nbt.putInt("patchSize", patchSize);

        return super.save(nbt);
    }

    @Override
    public void load(CompoundTag nbt)
    {
        super.load(nbt);

        ListTag filterList = nbt.getList("filters", Tag.TAG_COMPOUND);
        for (int i = 0; i < 9; i++)
        {
            filters[i] = ItemStack.of(filterList.getCompound(i));
        }

        radius = nbt.getInt("radius");
        patchSize = nbt.getInt("patchSize");
    }

    @Override
    public void writeToItemData(CompoundTag nbt)
    {
        super.writeToItemData(nbt);

        ListTag filterList = nbt.getList("filters", Tag.TAG_COMPOUND);
        for (int i = 0; i < 9; i++)
        {
            filters[i] = ItemStack.of(filterList.getCompound(i));
        }
    }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
    {
        return new ContainerMenuPlanter(windowId, this, inventory);
    }

    @Override
    public AABB getRenderBoundingBox() { return showArea ? INFINITE_EXTENT_AABB : Utils.NULL_AABB; }
}