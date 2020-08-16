package xfacthd.advtech.common.tileentity.generator;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.ForgeHooks;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.container.generator.ContainerBurnerGenerator;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityGenerator;

public class TileEntityBurnerGenerator extends TileEntityGenerator
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".burner_generator");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_EXTRACT = 200;

    private boolean fuelLoaded = false;
    private Item lastFuel = Items.AIR;
    private int totalEnergy = 0;
    private int production = 0;

    public TileEntityBurnerGenerator() { super(TileEntityTypes.tileTypeBurnerGenerator); }

    @Override
    public void tick()
    {
        super.tick();

        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (active && progress == -1 && (!fuelLoaded || !energyNotFull()))
            {
                setActive(false);
            }
            else if (!active && fuelLoaded && canStart() && energyNotFull())
            {
                setActive(true);
            }

            if (active)
            {
                if (progress == -1)
                {
                    int burnTime = ForgeHooks.getBurnTime(internalItemHandler.getStackInSlot(0));
                    totalEnergy = burnTime * 10;
                    production = (burnTime / 100) * productionMult;

                    internalItemHandler.extractItem(0, 1, false);

                    progress = 0;
                    markDirty();
                }
                else
                {
                    int produced = Math.min(totalEnergy - progress, production);
                    if (produced > 0)
                    {
                        produced = energyHandler.receiveEnergyInternal(produced, false);

                        progress += produced;
                        if (progress >= totalEnergy)
                        {
                            progress = -1;
                        }

                        markDirty();
                    }
                }
            }
        }
    }

    @Override
    public void onSlotChangedInternal(int slot)
    {
        ItemStack stack = internalItemHandler.getStackInSlot(slot);
        if (stack.getItem() != lastFuel)
        {
            lastFuel = stack.getItem();

            fuelLoaded = ForgeHooks.getBurnTime(stack) > 0;
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return ForgeHooks.getBurnTime(stack) > 0 && !stack.hasContainerItem(); }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        switch (port)
        {
            case NONE: return SideAccess.INPUT_ALL;
            case INPUT_ALL: return SideAccess.NONE;
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
            case NONE: return SideAccess.INPUT_ALL;
            case INPUT_ALL: return SideAccess.NONE;
            default: throw new IllegalStateException("Invalid port setting: " + port.getName());
        }
    }

    @Override
    public boolean canInsert(Direction side, int slot) { return cardinalPorts.get(side).isInput() && slot == 0; }

    @Override
    public boolean canExtract(Direction side, int slot) { return false; }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    protected int getBaseExtract() { return BASE_EXTRACT; }

    @Override
    protected int getEnergyProduction() { return production; }

    @Override
    protected int getEnergyPerUnitFuel() { return totalEnergy; }

    @Override
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerBurnerGenerator(id, this, inventory);
    }
}