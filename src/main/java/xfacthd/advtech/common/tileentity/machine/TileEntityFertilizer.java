package xfacthd.advtech.common.tileentity.machine;

import net.minecraft.block.BlockState;
import net.minecraft.block.CropsBlock;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.BoneMealItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.client.util.IRangedMachine;
import xfacthd.advtech.common.capability.energy.EnergySink;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.container.machine.ContainerFertilizer;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.subtypes.Enhancement;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityInventoryMachine;
import xfacthd.advtech.common.util.data.PropertyHolder;

public class TileEntityFertilizer extends TileEntityInventoryMachine implements IRangedMachine
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".fertilizer");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;
    private static final int ENERGY_PER_BLOCK = 400;

    private int energyConsumption = 0;
    private int radius = 1;
    private int progress = 0;
    private Direction facing = Direction.NORTH;
    private BlockPos scanPos = BlockPos.ZERO;
    private ItemStack boneMealCache = ItemStack.EMPTY;
    private boolean showArea = false;

    public TileEntityFertilizer() { super(TileEntityTypes.tileTypeFertilizer); }

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
                        tryFertilizeGrowable();

                        boolean positive = facing.getAxisDirection() == Direction.AxisDirection.POSITIVE;
                        int diffCross = 0;
                        int diffFace = 0;

                        switch (facing)
                        {
                            case NORTH:
                            case SOUTH:
                            {
                                int scanX = scanPos.getX();
                                int posX = pos.getX();
                                diffCross = positive ? (scanX - posX) : (posX - scanX);
                                diffFace = Math.abs(scanPos.getZ() - pos.getZ());
                                break;
                            }
                            case EAST:
                            case WEST:
                            {
                                int scanZ = scanPos.getZ();
                                int posZ = pos.getZ();
                                diffCross = positive ? (scanZ - posZ) : (posZ - scanZ);
                                diffFace = Math.abs(scanPos.getX() - pos.getX());
                                break;
                            }
                        }

                        if (diffCross >= radius)
                        {
                            scanPos = scanPos.offset(facing.rotateYCCW(), radius * 2);

                            if (diffFace >= ((radius * 2) + 1))
                            {
                                scanPos = scanPos.offset(facing.getOpposite(), radius * 2);
                            }
                            else
                            {
                                scanPos = scanPos.offset(facing);
                            }
                        }
                        else
                        {
                            scanPos = scanPos.offset(facing.rotateY());
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

    private void tryFertilizeGrowable()
    {
        //noinspection ConstantConditions
        BlockState state = world.getBlockState(scanPos);
        if (state.getBlock() instanceof CropsBlock)
        {
            CropsBlock crops = (CropsBlock)state.getBlock();
            if (!crops.isMaxAge(state))
            {
                ItemStack stack = findBoneMealStack();
                if (stack.isEmpty()) { return; }

                //noinspection deprecation Have to use this one because there is no player here
                boolean success = BoneMealItem.applyBonemeal(stack, world, scanPos);
                if (success)
                {
                    markDirty();
                }
            }
        }
    }

    private ItemStack findBoneMealStack()
    {
        if (!boneMealCache.isEmpty()) { return boneMealCache; }

        for (int i = 0; i < 9; i++)
        {
            ItemStack stack = internalItemHandler.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                boneMealCache = stack;
            }
        }
        return boneMealCache;
    }

    @Override
    public boolean canForcePush() { return false; }

    @Override
    protected void pushOutputs() { /*NOOP*/ }

    @Override
    public boolean canInsert(Direction side, int slot) { return cardinalPorts.get(side).isInput() && slot < 9; }

    @Override
    public boolean canExtract(Direction side, int slot) { return false; }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return stack.getItem() instanceof BoneMealItem; }

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
        //noinspection ConstantConditions
        if (world.isRemote()) { return; }

        if (upgrade == Enhancement.RANGE)
        {
            switch (level)
            {
                case 0:
                {
                    radius = 4;
                    break;
                }
                case 1:
                {
                    radius = 7;
                    break;
                }
                case 2:
                {
                    radius = 10;
                    break;
                }
                case 3:
                {
                    radius = 13;
                    break;
                }
            }

            Direction facing = getBlockState().get(PropertyHolder.FACING_HOR);
            scanPos = pos.offset(facing).offset(facing.rotateYCCW(), radius);
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

            Direction facing = getBlockState().get(PropertyHolder.FACING_HOR);
            scanPos = pos.offset(facing).offset(facing.rotateYCCW());

            markFullUpdate();
        }
    }

    @Override
    protected void onPortMappingChanged(Direction facing)
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            scanPos = pos.offset(facing).offset(facing.rotateYCCW());
            this.facing = facing;
        }
    }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

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
        if (side == Side.FRONT) { return SideAccess.NONE; }

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

    public void switchShowArea() { showArea = !showArea; } //TODO: add button in gui

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        super.writeNetworkNBT(nbt);

        nbt.putInt("radius", radius);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);

        radius = nbt.getInt("radius");
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("radius", radius);

        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);

        radius = nbt.getInt("radius");
    }

    @Override
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerFertilizer(windowId, this, inventory);
    }
}