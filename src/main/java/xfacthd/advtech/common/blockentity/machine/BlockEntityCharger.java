package xfacthd.advtech.common.blockentity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.capability.energy.EnergySink;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.machine.ContainerMenuCharger;

public class BlockEntityCharger extends BlockEntityInventoryMachine
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".charger");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;

    private int energyConsumption = 0;

    public BlockEntityCharger(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.CHARGER), pos, state);
    }

    @Override
    protected void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (slotNotEmpty(0) && canRun(true) && !energyItemFull())
            {
                if (active && energyHandler.getEnergyStored() < BASE_CONSUMPTION)
                {
                    setActive(false);
                }
                else
                {
                    if (!active) { setActive(true); }

                    float mult = (float) energyHandler.getEnergyStored() / (float) energyHandler.getMaxEnergyStored();
                    int actualConsumption = Math.max((int) Math.ceil(energyConsumption * mult), BASE_CONSUMPTION);

                    LazyOptional<IEnergyStorage> energy = internalItemHandler.getStackInSlot(0).getCapability(CapabilityEnergy.ENERGY);
                    energy.ifPresent(handler ->
                    {
                        int result = energyHandler.extractEnergyInternal(actualConsumption, true);
                        result = handler.receiveEnergy(result, false);
                        energyHandler.extractEnergyInternal(result, false);
                    });
                }
            }
            else if (active)
            {
                setActive(false);
            }

            if (energyItemFull())
            {
                ItemStack stack = internalItemHandler.getStackInSlot(0);
                stack = internalItemHandler.insertItem(1, stack, false);
                if (stack.isEmpty()) { internalItemHandler.extractItem(0, 1, false); }

                setCycleComplete();
            }
        }

        super.tickInternal();
    }

    private boolean energyItemFull()
    {
        ItemStack stack = internalItemHandler.getStackInSlot(0);
        if (stack.isEmpty()) { return false; }

        return stack.getCapability(CapabilityEnergy.ENERGY).map(handler ->
                handler.getEnergyStored() >= handler.getMaxEnergyStored()
        ).orElse(false);
    }

    @Override
    public boolean canForcePush() { return true; }

    @Override
    protected void pushOutputs()
    {
        if (internalItemHandler.getStackInSlot(1).isEmpty()) { return; }

        boolean worked = false;
        for (Direction dir : Direction.values())
        {
            SideAccess mode = cardinalPorts.get(dir);
            if (mode.isOutput())
            {
                LazyOptional<IItemHandler> adj = getNeighboringHandler(dir);
                worked |= adj.map(handler ->
                {
                    ItemStack stack = internalItemHandler.getStackInSlot(1);
                    if (!stack.isEmpty())
                    {
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                        internalItemHandler.setStackInSlot(1, remainder);
                        return true;
                    }
                    return false;
                }).orElse(false);
                if (internalItemHandler.getStackInSlot(1).isEmpty()) { break; }
            }
        }
        if (worked) { setChanged(); }
    }

    @Override
    public boolean canInsert(Direction side, int slot) { return slot == 0 && cardinalPorts.get(side).isInput(); }

    @Override
    public boolean canExtract(Direction side, int slot) { return slot == 1 && cardinalPorts.get(side).isOutput(); }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return stack.getCapability(CapabilityEnergy.ENERGY).isPresent(); }

    @Override
    protected boolean needSlotNotification() { return false; }

    @Override
    protected void onSlotChangedInternal(int slot) { }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        return switch (port)
        {
            case NONE -> SideAccess.INPUT_ALL;
            case INPUT_ALL -> SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL -> SideAccess.NONE;
            default -> throw new IllegalStateException("Invalid port setting: " + port.getSerializedName());
        };
    }

    @Override
    public SideAccess getPriorPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        return switch (port)
        {
            case NONE -> SideAccess.OUTPUT_ALL;
            case INPUT_ALL -> SideAccess.NONE;
            case OUTPUT_ALL -> SideAccess.INPUT_ALL;
            default -> throw new IllegalStateException("Invalid port setting: " + port.getSerializedName());
        };
    }

    @Override
    protected void initCapabilities()
    {
        internalItemHandler = new MachineItemStackHandler(this, 2);
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
    public float getProgress() { return 0; }

    @Override
    public boolean supportsEnhancements() { return false; }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player player)
    {
        return new ContainerMenuCharger(windowId, this, inventory);
    }
}