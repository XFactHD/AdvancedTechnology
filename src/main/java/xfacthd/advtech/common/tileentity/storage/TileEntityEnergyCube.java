package xfacthd.advtech.common.tileentity.storage;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.capability.energy.ATEnergyStorage;
import xfacthd.advtech.common.container.energy.ContainerEnergyCube;
import xfacthd.advtech.common.data.states.*;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityEnergyHandler;
import xfacthd.advtech.common.util.data.PropertyHolder;

import java.util.EnumMap;
import java.util.Map;

public class TileEntityEnergyCube extends TileEntityEnergyHandler implements INamedContainerProvider
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".energy_cube");
    private static final int BASE_CAPACITY = 10000;
    private static final int BASE_TRANSFER = 250;

    private final Map<Side, SideAccess> ports = new EnumMap<>(Side.class);
    private final Map<Direction, SideAccess> cardinalPorts = new EnumMap<>(Direction.class);
    private MachineLevel level = MachineLevel.BASIC;
    private boolean firstTick = true;
    private Direction facing = Direction.NORTH;
    private int energyLevel = 0;

    public TileEntityEnergyCube()
    {
        super(TileEntityTypes.tileTypeEnergyCube);

        for (Side side : Side.values()) { ports.put(side, SideAccess.NONE); }
        for (Direction side : Direction.values()) { cardinalPorts.put(side, SideAccess.NONE); }
    }

    @Override
    public void tick()
    {
        if (firstTick) { firstTick(); }

        super.tick();

        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            float stored = internalEnergyHandler.getEnergyStored();
            float capacity = internalEnergyHandler.getMaxEnergyStored();
            int newLevel = (int) (10F * (stored / capacity));
            if (newLevel != energyLevel)
            {
                energyLevel = newLevel;
                markFullUpdate();
            }
        }
    }

    private void firstTick()
    {
        onLevelChanged();
        remapPorts();
        firstTick = false;
    }

    public final SideAccess getNextPortSetting(Side side)
    {
        SideAccess mode = ports.get(side);
        switch (mode)
        {
            case NONE: return SideAccess.INPUT_ALL;
            case INPUT_ALL: return SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL: return SideAccess.NONE;
            default: throw new IllegalStateException("Invalid port setting: " + mode.getName());
        }
    }

    public final SideAccess getPriorPortSetting(Side side)
    {
        SideAccess mode = ports.get(side);
        switch (mode)
        {
            case NONE: return SideAccess.OUTPUT_ALL;
            case INPUT_ALL: return SideAccess.NONE;
            case OUTPUT_ALL: return SideAccess.INPUT_ALL;
            default: throw new IllegalStateException("Invalid port setting: " + mode.getName());
        }
    }

    public final void setSidePort(Side side, SideAccess port)
    {
        ports.put(side, port);
        remapPorts();
    }

    public SideAccess getCardinalPort(Direction side) { return cardinalPorts.get(side); }

    public SideAccess getSidePort(Side side) { return ports.get(side); }

    private void remapPorts() { remapPortsToFacing(getBlockState().get(PropertyHolder.FACING_HOR)); }

    public final void remapPortsToFacing(Direction facing)
    {
        this.facing = facing;

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

            if (lazyEnergyHandlers.get(dir).isPresent() && setting.isDisabled())
            {
                lazyEnergyHandlers.get(dir).invalidate();
            }
            else if (!lazyEnergyHandlers.get(dir).isPresent() && !setting.isDisabled())
            {
                lazyEnergyHandlers.put(dir, LazyOptional.of(() -> energyHandlers.get(dir)));
            }
        }

        //noinspection ConstantConditions
        if (changed || world.isRemote()) { markFullUpdate(); }
    }

    public MachineLevel getLevel() { return level; }

    public void upgrade(MachineLevel level)
    {
        this.level = level;
        onLevelChanged();
        markFullUpdate();
    }

    public void onLevelChanged()
    {
        int mult = (int)Math.pow(2, level.ordinal());
        int capacity = BASE_CAPACITY * mult;
        int maxTransfer = BASE_TRANSFER * 10;
        internalEnergyHandler.reconfigure(capacity, maxTransfer, maxTransfer);
    }

    @Override
    protected void initCapabilities()
    {
        internalEnergyHandler = new ATEnergyStorage(BASE_CAPACITY, BASE_TRANSFER);
        super.initCapabilities();
    }

    @Override
    public boolean isSideActive(Direction side) { return !cardinalPorts.get(side).isDisabled(); }

    @Override
    public boolean canReceiveEnergy(Direction side) { return cardinalPorts.get(side).isInput(); }

    @Override
    public boolean canExtractEnergy(Direction side) { return cardinalPorts.get(side).isOutput(); }

    public Direction getFacing() { return facing; }

    public int getEnergyLevel() { return energyLevel; }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }
        nbt.putInt("facing", facing.getHorizontalIndex());
        nbt.putInt("energyLevel", energyLevel);
    }

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        level = MachineLevel.values()[nbt.getInt("level")];
        for (Side side : Side.values()) { ports.put(side, SideAccess.values()[nbt.getInt(side.getName())]); }
        facing = Direction.byHorizontalIndex(nbt.getInt("facing"));
        energyLevel = nbt.getInt("energyLevel");

        if (world != null) { remapPortsToFacing(getBlockState().get(PropertyHolder.FACING_HOR)); }
    }

    @Override
    public CompoundNBT write(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }
        return super.write(nbt);
    }

    @Override
    public void read(CompoundNBT nbt)
    {
        super.read(nbt);
        level = MachineLevel.values()[nbt.getInt("level")];
        for (Side side : Side.values()) { ports.put(side, SideAccess.values()[nbt.getInt(side.getName())]); }
    }

    public void writeToItemData(CompoundNBT nbt)
    {
        nbt.putInt("level", level.ordinal());
        for (Side side : Side.values()) { nbt.putInt(side.getName(), ports.get(side).ordinal()); }

        nbt.put("energy", internalEnergyHandler.serializeNBT());
    }

    @Override
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerEnergyCube(id, this, inventory);
    }
}