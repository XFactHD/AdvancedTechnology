package xfacthd.advtech.common.blockentity.machine;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityProducer;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.machine.ContainerMenuElectricFurnace;
import xfacthd.advtech.common.util.inventory.RecipeSearchInventory;

public class BlockEntityElectricFurnace extends BlockEntityProducer
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".furnace");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;

    private Item lastInput = Items.AIR;
    private int burnTime = 0;

    public BlockEntityElectricFurnace(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.ELECTRIC_FURNACE), pos, state, RecipeType.SMELTING);
    }

    @Override
    public void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (recipe == null && slotNotEmpty(0))
            {
                recipe = findRecipe();
            }

            if (recipe != null && hasEnoughEnergy() && canRun(progress == -1) && canFitInSlot(1, recipe.getResultItem()) && slotNotEmpty(0))
            {
                if (progress == -1)
                {
                    progress = 0;
                    burnTime = ((AbstractCookingRecipe)recipe).getCookingTime() * 10;

                    if (!active)
                    {
                        setActive(true);
                    }
                }
                else if (progress >= burnTime)
                {
                    ItemStack result = ((AbstractCookingRecipe)recipe).assemble(recipeInv);

                    internalItemHandler.insertItem(1, result, false);
                    internalItemHandler.extractItem(0, 1, false); //Must happen after retrieving the result because emptying the slot sets the recipe to null

                    progress = -1;
                    setCycleComplete();
                }
            }
            else if (active)
            {
                progress = -1;
                setActive(false);
            }
        }

        super.tickInternal();
    }

    @Override
    protected void initCapabilities()
    {
        internalItemHandler = new MachineItemStackHandler(this, 2);
        super.initCapabilities();
    }

    @Override
    public void onSlotChangedInternal(int slot)
    {
        if (slot == 0)
        {
            ItemStack input = internalItemHandler.getStackInSlot(0);
            if (input.isEmpty() && lastInput != Items.AIR)
            {
                recipe = null;
                progress = -1;
                burnTime = 0;
                lastInput = Items.AIR;
                setActive(false);
            }
            else if (input.getItem() != lastInput)
            {
                lastInput = input.getItem();
                recipe = findRecipe();
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == 1)
        {
            return recipe != null && recipe.getResultItem().sameItemStackIgnoreDurability(stack);
        }
        else
        {
            //If the recipe is not null, then there is already a compatible item in the slot
            if (recipe != null) { return true; }

            Container inv = new RecipeSearchInventory(stack);
            return findRecipeWithInv(inv, false) != null;
        }
    }

    @Override
    protected void pushOutputs()
    {
        if (internalItemHandler.getStackInSlot(1).isEmpty()) { return; }

        boolean worked = false;
        for (Direction dir : Direction.values())
        {
            SideAccess port = cardinalPorts.get(dir);
            if (port.isOutput())
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
    public boolean canInsert(Direction side, int slot) { return cardinalPorts.get(side).isInput() && slot == 0; }

    @Override
    public boolean canExtract(Direction side, int slot) { return cardinalPorts.get(side).isOutput() && slot == 1; }

    @Override
    protected int getEnergyRequired() { return burnTime; }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    protected int getBaseConsumption() { return BASE_CONSUMPTION; }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return new ContainerMenuElectricFurnace(id, this, inventory);
    }
}