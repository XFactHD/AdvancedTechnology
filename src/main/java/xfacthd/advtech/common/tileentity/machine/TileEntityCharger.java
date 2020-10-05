package xfacthd.advtech.common.tileentity.machine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.capability.energy.EnergySink;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.container.machine.ContainerCharger;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityInventoryMachine;

import java.util.concurrent.atomic.AtomicBoolean;

public class TileEntityCharger extends TileEntityInventoryMachine
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".charger");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;

    private int energyConsumption = 0;

    public TileEntityCharger() { super(TileEntityTypes.tileTypeCharger); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (!internalItemHandler.getStackInSlot(0).isEmpty() && (active || canStart()) && !energyItemFull())
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
            }
        }

        super.tick();
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

        AtomicBoolean worked = new AtomicBoolean(false);
        for (Direction dir : Direction.values())
        {
            SideAccess mode = cardinalPorts.get(dir);
            if (mode.isOutput())
            {
                LazyOptional<IItemHandler> adj = getNeighboringHandler(dir);
                adj.ifPresent(handler ->
                {
                    ItemStack stack = internalItemHandler.getStackInSlot(1);
                    if (!stack.isEmpty())
                    {
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                        internalItemHandler.setStackInSlot(1, remainder);
                        worked.set(true);
                    }
                });
                if (internalItemHandler.getStackInSlot(1).isEmpty()) { break; }
            }
        }
        if (worked.get()) { markDirty(); }
    }

    @Override
    public boolean canInsert(Direction side, int slot) { return slot == 0 && cardinalPorts.get(side).isInput(); }

    @Override
    public boolean canExtract(Direction side, int slot) { return slot == 1 && cardinalPorts.get(side).isOutput(); }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return stack.getCapability(CapabilityEnergy.ENERGY).isPresent(); }

    @Override
    protected void onSlotChangedInternal(int slot) { }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        switch (port)
        {
            case NONE: return SideAccess.INPUT_ALL;
            case INPUT_ALL: return SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL: return SideAccess.NONE;
            default: throw new IllegalStateException("Invalid port setting: " + port.getName());
        }
    }

    @Override
    public SideAccess getPriorPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        switch (port)
        {
            case NONE: return SideAccess.OUTPUT_ALL;
            case INPUT_ALL: return SideAccess.NONE;
            case OUTPUT_ALL: return SideAccess.INPUT_ALL;
            default: throw new IllegalStateException("Invalid port setting: " + port.getName());
        }
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
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int windowId, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerCharger(windowId, this, inventory);
    }
}