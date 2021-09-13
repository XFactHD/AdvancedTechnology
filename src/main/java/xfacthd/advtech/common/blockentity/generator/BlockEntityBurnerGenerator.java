package xfacthd.advtech.common.blockentity.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.ForgeHooks;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.generator.ContainerMenuBurnerGenerator;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.blockentity.BlockEntityGenerator;

public class BlockEntityBurnerGenerator extends BlockEntityGenerator
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".burner_generator");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_EXTRACT = 200;

    private boolean fuelLoaded = false;
    private Item lastFuel = Items.AIR;
    private int totalEnergy = 0;
    private int production = 0;

    public BlockEntityBurnerGenerator(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.BURNER_GENERATOR), pos, state);
    }

    @Override
    public void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (active && progress == -1 && (!fuelLoaded || !energyNotFull() || !canRun(true)))
            {
                setActive(false);
            }
            else if (!active && fuelLoaded && canRun(true) && energyNotFull())
            {
                setActive(true);
            }

            if (active)
            {
                if (progress == -1)
                {
                    int burnTime = ForgeHooks.getBurnTime(internalItemHandler.getStackInSlot(0), null);
                    totalEnergy = burnTime * 10;
                    production = (burnTime / 100) * productionMult;

                    internalItemHandler.extractItem(0, 1, false);

                    progress = 0;
                    setChanged();
                }
                else
                {
                    int produced = Math.min(totalEnergy - progress, production);
                    if (produced > 0)
                    {
                        energyHandler.receiveEnergyInternal(produced, false);

                        progress += production;
                        if (progress >= totalEnergy)
                        {
                            progress = -1;
                        }

                        setChanged();
                    }
                }
            }
        }

        super.tickInternal();
    }

    @Override
    public void onSlotChangedInternal(int slot)
    {
        ItemStack stack = internalItemHandler.getStackInSlot(slot);
        if (stack.getItem() != lastFuel)
        {
            lastFuel = stack.getItem();

            fuelLoaded = ForgeHooks.getBurnTime(stack, null) > 0;
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) { return ForgeHooks.getBurnTime(stack, null) > 0 && !stack.hasContainerItem(); }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        return switch (port)
        {
            case NONE -> SideAccess.INPUT_ALL;
            case INPUT_ALL -> SideAccess.NONE;
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
            case NONE -> SideAccess.INPUT_ALL;
            case INPUT_ALL -> SideAccess.NONE;
            default -> throw new IllegalStateException("Invalid port setting: " + port.getSerializedName());
        };
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
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return new ContainerMenuBurnerGenerator(id, this, inventory);
    }
}