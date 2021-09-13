package xfacthd.advtech.common.blockentity.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.energy.CapabilityEnergy;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityEnergyHandler;
import xfacthd.advtech.common.capability.energy.ATEnergyStorage;
import xfacthd.advtech.common.capability.energy.ItemEnergyStorage;
import xfacthd.advtech.common.data.states.*;
import xfacthd.advtech.common.menu.storage.ContainerMenuEnergyCube;
import xfacthd.advtech.common.util.MachineUtils;
import xfacthd.advtech.common.util.data.PropertyHolder;
import xfacthd.advtech.common.util.interfaces.ISidedMachine;
import xfacthd.advtech.common.util.interfaces.IUpgradableMachine;

import java.util.*;

public class BlockEntityEnergyCube extends BlockEntityEnergyHandler implements MenuProvider, ISidedMachine, IUpgradableMachine
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".energy_cube");
    public static final int BASE_CAPACITY = 10000;
    public static final int BASE_TRANSFER = 250;

    private final Map<Side, SideAccess> ports = new EnumMap<>(Side.class);
    private final Map<Direction, SideAccess> cardinalPorts = new EnumMap<>(Direction.class);
    private MachineLevel machineLevel = MachineLevel.BASIC;
    private int energyLevel = 0;

    public BlockEntityEnergyCube(BlockPos pos, BlockState state)
    {
        super(ATContent.BE_TYPE_ENERGY_CUBE.get(), pos, state);

        for (Side side : Side.values()) { ports.put(side, SideAccess.NONE); }
        for (Direction side : Direction.values()) { cardinalPorts.put(side, SideAccess.NONE); }
    }

    @Override
    public void tickInternal()
    {
        super.tickInternal();

        if (!level().isClientSide())
        {
            float stored = internalEnergyHandler.getEnergyStored();
            float capacity = internalEnergyHandler.getMaxEnergyStored();
            int newLevel = (int) (10F * (stored / capacity));
            if (newLevel != energyLevel)
            {
                energyLevel = newLevel;
                markForSync();
            }
        }
    }

    @Override
    public void onLoad()
    {
        super.onLoad();
        onLevelChanged();
        remapPorts();
    }

    @Override
    protected ATEnergyStorage initCapability() { return new ATEnergyStorage(BASE_CAPACITY, BASE_TRANSFER); }



    @Override
    public boolean isSideActive(Direction side) { return !cardinalPorts.get(side).isDisabled(); }

    @Override
    public boolean canReceiveEnergy(Direction side) { return cardinalPorts.get(side).isInput(); }

    @Override
    public boolean canExtractEnergy(Direction side) { return cardinalPorts.get(side).isOutput(); }

    public Direction getFacing() { return getBlockState().getValue(PropertyHolder.FACING_HOR); }

    public int getEnergyLevel() { return energyLevel; }

    @Override
    public MachineLevel getMachineLevel() { return machineLevel; }

    public SideAccess getCardinalPort(Direction side) { return cardinalPorts.get(side); }

    @Override
    public SideAccess getSidePort(Side side) { return ports.get(side); }

    @Override
    public void setSidePort(Side side, SideAccess port)
    {
        ports.put(side, port);
        remapPorts();
    }

    public final SideAccess getNextPortSetting(Side side)
    {
        SideAccess mode = ports.get(side);
        return switch (mode)
        {
            case NONE -> SideAccess.INPUT_ALL;
            case INPUT_ALL -> SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL -> SideAccess.NONE;
            default -> throw new IllegalStateException("Invalid port setting: " + mode.getSerializedName());
        };
    }

    public final SideAccess getPriorPortSetting(Side side)
    {
        SideAccess mode = ports.get(side);
        return switch (mode)
        {
            case NONE -> SideAccess.OUTPUT_ALL;
            case INPUT_ALL -> SideAccess.NONE;
            case OUTPUT_ALL -> SideAccess.INPUT_ALL;
            default -> throw new IllegalStateException("Invalid port setting: " + mode.getSerializedName());
        };
    }

    public final void switchSidePort(Side side, int dir)
    {
        SideAccess port = SideAccess.NONE;
        if (dir == 1) { port = getNextPortSetting(side); }
        else if (dir == -1) { port = getPriorPortSetting(side); }

        setSidePort(side, port);
    }

    @Override
    public void remapPorts() { MachineUtils.remapMachinePorts(this, ports, cardinalPorts, energyHandlers, lazyEnergyHandlers); }

    @Override
    public void upgrade(MachineLevel level)
    {
        machineLevel = level;
        onLevelChanged();
        markForSync();
    }

    public void onLevelChanged()
    {
        int mult = (int)Math.pow(2, machineLevel.ordinal());
        int capacity = BASE_CAPACITY * mult;
        int maxTransfer = BASE_TRANSFER * mult;
        internalEnergyHandler.reconfigure(capacity, maxTransfer, maxTransfer);
    }



    @Override
    public void writeNetworkNBT(CompoundTag nbt)
    {
        nbt.putInt("level", machineLevel.ordinal());
        Arrays.stream(Side.values()).forEach(side -> nbt.putInt(side.getSerializedName(), ports.get(side).ordinal()));
        nbt.putInt("energy_level", energyLevel);
    }

    @Override
    public void readNetworkNBT(CompoundTag nbt)
    {
        machineLevel = MachineLevel.byId(nbt.getInt("level"));
        Arrays.stream(Side.values()).forEach(side -> ports.put(side, SideAccess.byId(nbt.getInt(side.getSerializedName()))));
        energyLevel = nbt.getInt("energy_level");

        if (level != null) { remapPorts(); }
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer)
    {
        buffer.writeInt(machineLevel.ordinal());
        Arrays.stream(Side.values()).forEach(side -> buffer.writeInt(ports.get(side).ordinal()));
        buffer.writeInt(energyLevel);
    }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer)
    {
        boolean changed = false;

        MachineLevel level = MachineLevel.byId(buffer.readInt());
        if (machineLevel != level)
        {
            machineLevel = level;
            changed = true;
        }

        for (Side side : Side.values())
        {
            SideAccess mode = SideAccess.byId(buffer.readInt());
            if (ports.get(side) != mode)
            {
                ports.put(side, mode);
                changed = true;
            }
        }

        energyLevel = buffer.readInt();

        if (changed) { markRenderUpdate(); }
    }

    public void writeToItemData(ItemStack stack)
    {
        CompoundTag nbt = stack.getOrCreateTagElement("BlockEntityTag");
        nbt.putInt("level", machineLevel.ordinal());
        for (Side side : Side.values()) { nbt.putInt(side.getSerializedName(), ports.get(side).ordinal()); }
        nbt.put("energy", internalEnergyHandler.serializeNBT());

        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler ->
        {
            ItemEnergyStorage storage = (ItemEnergyStorage)handler;
            storage.initFromTile(
                    internalEnergyHandler.getMaxEnergyStored(),
                    internalEnergyHandler.getMaxReceive(),
                    internalEnergyHandler.getEnergyStored()
            );
        });
    }

    public void readFromItemData(ItemStack stack)
    {
        stack.getCapability(CapabilityEnergy.ENERGY).ifPresent(handler ->
        {
            ItemEnergyStorage storage = (ItemEnergyStorage)handler;
            internalEnergyHandler.reconfigure(
                    storage.getMaxEnergyStored(),
                    storage.getMaxTransfer(),
                    storage.getMaxTransfer()
            );
            internalEnergyHandler.setEnergyStored(storage.getEnergyStored());
            markForSync();
        });
    }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inv, Player player)
    {
        return new ContainerMenuEnergyCube(windowId, this, inv);
    }
}