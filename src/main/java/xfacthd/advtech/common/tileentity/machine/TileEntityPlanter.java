package xfacthd.advtech.common.tileentity.machine;

import net.minecraft.block.*;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.IPlantable;
import net.minecraftforge.common.PlantType;
import net.minecraftforge.common.util.Constants;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.util.IRangedMachine;
import xfacthd.advtech.common.capability.energy.EnergySink;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.container.machine.ContainerPlanter;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.subtypes.Enhancement;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityInventoryMachine;
import xfacthd.advtech.common.util.LogHelper;
import xfacthd.advtech.common.util.Utils;

public class TileEntityPlanter extends TileEntityInventoryMachine implements IRangedMachine
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".planter");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;
    private static final int ENERGY_PER_BLOCK = 400;

    private final ItemStack[] filters = new ItemStack[9];
    private int energyConsumption = 0;
    private int radius = 1;
    private int patchSize = 1;
    private int progress = 0;
    private BlockPos scanPos = BlockPos.ZERO;
    private boolean showArea = false;

    public TileEntityPlanter()
    {
        super(TileEntityTypes.tileTypePlanter);
        for (int i = 0; i < 9; i++) { filters[i] = ItemStack.EMPTY; }
    }

    @Override
    public void tick()
    {
        super.tick();

        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (active)
            {
                if (energyHandler.getEnergyStored() < BASE_CONSUMPTION)
                {
                    setActive(false);
                }
                else
                {
                    float mult = (float) energyHandler.getEnergyStored() / (float) energyHandler.getMaxEnergyStored();
                    int actualConsumption = Math.max((int) Math.ceil(energyConsumption * mult), BASE_CONSUMPTION);
                    int result = energyHandler.extractEnergyInternal(actualConsumption, false);

                    progress += result;

                    if (progress >= ENERGY_PER_BLOCK)
                    {
                        progress = 0;
                        tryPlantCrop();

                        int diffX = scanPos.getX() - pos.getX();
                        if (diffX >= radius)
                        {
                            scanPos = scanPos.west(radius * 2);

                            int diffZ = scanPos.getZ() - pos.getZ();
                            if (diffZ >= radius)
                            {
                                scanPos = scanPos.north(radius * 2);
                            }
                            else
                            {
                                scanPos = scanPos.south();
                            }
                        }
                        else
                        {
                            scanPos = scanPos.east();
                        }
                    }
                }
            }
            else if (energyHandler.getEnergyStored() >= BASE_CONSUMPTION && canStart())
            {
                setActive(true);
            }
        }
    }

    private void tryPlantCrop()
    {
        //noinspection ConstantConditions
        if (world.isAirBlock(scanPos))
        {
            int patchX = ((scanPos.getX() - pos.getX()) + radius) / patchSize;
            int patchZ = ((scanPos.getZ() - pos.getZ()) + radius) / patchSize;
            int slot = (patchZ * 3) + patchX;

            if (slot < 0 || slot >= 9)
            {
                LogHelper.warn("Invalid slot! Slot:" + slot + " Pos: " + scanPos + " PatchX: " + patchX + " PatchZ: " + patchZ);
                return;
            }

            ItemStack seeds = internalItemHandler.getStackInSlot(slot);
            if (!seeds.isEmpty())
            {
                BlockPos soilPos = scanPos.down();
                BlockState soilState = world.getBlockState(soilPos);

                BlockState plantState = null;
                if (seeds.getItem() instanceof IPlantable)
                {
                    IPlantable plantable = ((IPlantable) seeds.getItem());

                    if (plantable.getPlantType(world, scanPos) == PlantType.Crop)
                    {
                        if (soilState.canSustainPlant(world, soilPos, Direction.UP, plantable))
                        {
                            plantState = plantable.getPlant(world, scanPos);
                        }
                    }
                }
                else
                {
                    plantState = findVanillaCropState(seeds, soilState);
                }

                if (plantState != null)
                {
                    world.setBlockState(scanPos, plantState);
                    plantState.getBlock().onBlockPlacedBy(world, scanPos, plantState, null, seeds);

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
            return Blocks.WHEAT.getDefaultState();
        }
        else if (seeds == Items.POTATO && farmland)
        {
            return Blocks.POTATOES.getDefaultState();
        }
        else if (seeds == Items.CARROT && farmland)
        {
            return Blocks.CARROTS.getDefaultState();
        }
        else if (seeds == Items.BEETROOT_SEEDS && farmland)
        {
            return Blocks.BEETROOTS.getDefaultState();
        }
        else if (seeds == Items.SUGAR_CANE && sand)
        {
            return null;//return Blocks.SUGAR_CANE.getDefaultState(); //TODO: implement special casing in harvester before activating
        }
        return null;
    }

    private void playPlaceSound(BlockState plantState)
    {
        SoundType type = plantState.getSoundType(world, scanPos, null);
        SoundEvent event = type.getPlaceSound();

        //noinspection ConstantConditions
        world.playSound(null, scanPos, event, SoundCategory.BLOCKS, (type.getVolume() + 1.0F) / 2.0F, type.getPitch() * 0.8F);
    }

    @Override
    protected void firstTick()
    {
        super.firstTick();

        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            scanPos = pos.north(radius).west(radius).up(2);
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
                    return ((IPlantable) item).getPlantType(world, pos) == PlantType.Crop;
                }
            }
            return filters[slot].isItemEqual(stack);
        }
        return false;
    }

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
        switch (mode)
        {
            case NONE: return SideAccess.INPUT_ALL;
            case INPUT_ALL: return SideAccess.NONE;
            default: throw new IllegalStateException("Invalid port setting: " + mode.getName());
        }
    }

    @Override
    public SideAccess getPriorPortSetting(Side side)
    {
        if (side == Side.TOP) { return SideAccess.NONE; }

        SideAccess mode = ports.get(side);
        switch (mode)
        {
            case NONE: return SideAccess.INPUT_ALL;
            case INPUT_ALL: return SideAccess.NONE;
            default: throw new IllegalStateException("Invalid port setting: " + mode.getName());
        }
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
        //noinspection ConstantConditions
        if (world.isRemote()) { return; }

        if (upgrade == Enhancement.RANGE)
        {
            switch (level)
            {
                case 0:
                {
                    radius = 4;
                    patchSize = 3;
                    break;
                }
                case 1:
                {
                    radius = 7;
                    patchSize = 5;
                    break;
                }
                case 2:
                {
                    radius = 10;
                    patchSize = 7;
                    break;
                }
                case 3:
                {
                    radius = 13;
                    patchSize = 9;
                    break;
                }
            }

            scanPos = pos.north(radius).west(radius).up(2);
            markFullUpdate();
        }
    }

    @Override
    public void removeEnhancement(Enhancement upgrade)
    {
        //noinspection ConstantConditions
        if (world.isRemote()) { return; }

        if (upgrade == Enhancement.RANGE)
        {
            radius = 1;
            patchSize = 1;

            scanPos = pos.north().west().up(2);

            markFullUpdate();
        }
    }

    public ItemStack getFilterStack(int idx) { return filters[idx]; }

    public void configureFilter(boolean clear)
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
        markFullUpdate();
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
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        super.writeNetworkNBT(nbt);

        nbt.putInt("radius", radius);

        ListNBT filterList = new ListNBT();
        for (int i = 0; i < 9; i++)
        {
            filterList.add(filters[i].write(new CompoundNBT()));
        }
        nbt.put("filters", filterList);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);

        radius = nbt.getInt("radius");

        ListNBT filterList = nbt.getList("filters", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < 9; i++)
        {
            filters[i] = ItemStack.read(filterList.getCompound(i));
        }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        ListNBT filterList = new ListNBT();
        for (int i = 0; i < 9; i++)
        {
            filterList.add(filters[i].write(new CompoundNBT()));
        }
        nbt.put("filters", filterList);

        nbt.putInt("radius", radius);
        nbt.putInt("patchSize", patchSize);

        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);

        ListNBT filterList = nbt.getList("filters", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < 9; i++)
        {
            filters[i] = ItemStack.read(filterList.getCompound(i));
        }

        radius = nbt.getInt("radius");
        patchSize = nbt.getInt("patchSize");
    }

    @Override
    public void writeToItemData(CompoundNBT nbt)
    {
        super.writeToItemData(nbt);

        ListNBT filterList = nbt.getList("filters", Constants.NBT.TAG_COMPOUND);
        for (int i = 0; i < 9; i++)
        {
            filters[i] = ItemStack.read(filterList.getCompound(i));
        }
    }

    @Override
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerPlanter(windowId, this, inventory);
    }

    @Override
    public AxisAlignedBB getRenderBoundingBox() { return showArea ? INFINITE_EXTENT_AABB : Utils.NULL_AABB; }
}