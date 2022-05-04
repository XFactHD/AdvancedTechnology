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
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityProducer;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.menu.machine.ContainerMenuMetalPress;
import xfacthd.advtech.common.data.recipes.MetalPressRecipe;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.util.inventory.RecipeSearchInventory;

public class BlockEntityMetalPress extends BlockEntityProducer
{
    public static final Component TITLE = new TranslatableComponent("gui." + AdvancedTechnology.MODID + ".metal_press");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;

    private Item lastInput = Items.AIR;
    private Item lastMold = Items.AIR;
    private int energy = 0;

    public BlockEntityMetalPress(BlockPos pos, BlockState state)
    {
        super(ATContent.machineEntity(MachineType.METAL_PRESS), pos, state, ATContent.RECIPE_TYPE_METAL_PRESS.get());
    }

    @Override
    public void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (recipe == null && slotNotEmpty(0) && slotNotEmpty(1))
            {
                recipe = findRecipe();
            }

            if (recipe != null && hasEnoughEnergy() && canRun(progress == -1) && slotNotEmpty(0) && slotNotEmpty(1))
            {
                if (!canFitInSlot(2, recipe.getResultItem()))
                {
                    if (active) { setActive(false); }
                }
                else if (!active || progress == -1)
                {
                    if (progress == -1)
                    {
                        progress = 0;
                        energy = ((MetalPressRecipe)recipe).getEnergy();
                    }

                    if (!active)
                    {
                        setActive(true);
                    }
                }
                else if (progress >= energy)
                {
                    ItemStack result = recipe.getResultItem().copy();
                    internalItemHandler.insertItem(2, result, false);

                    int inputCount = ((MetalPressRecipe)recipe).getInputCount();
                    internalItemHandler.extractItem(0, inputCount, false);

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
        internalItemHandler = new MachineItemStackHandler(this, 3);
        super.initCapabilities();
    }

    @Override
    protected void onSlotChangedInternal(int slot)
    {
        if (slot == 0)
        {
            ItemStack input = internalItemHandler.getStackInSlot(0);
            if (input.isEmpty() && lastInput != Items.AIR)
            {
                recipe = null;
                progress = -1;
                energy = 0;
                lastInput = Items.AIR;
                setActive(false);
            }
            else if (input.getItem() != lastInput)
            {
                lastInput = input.getItem();
                recipe = findRecipe();
            }
        }
        else if (slot == 1)
        {
            ItemStack mold = internalItemHandler.getStackInSlot(1);
            if (mold.isEmpty() && lastMold != Items.AIR)
            {
                recipe = null;
                progress = -1;
                energy = 0;
                lastMold = Items.AIR;
                setActive(false);
            }
            else if (mold.getItem() != lastMold)
            {
                lastMold = mold.getItem();
                if (!internalItemHandler.getStackInSlot(0).isEmpty())
                {
                    recipe = findRecipe();
                }
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == 0)
        {
            //Can't insert resources before a mold is installed
            if (internalItemHandler.getStackInSlot(1).isEmpty()) { return false; }

            if (recipe != null)
            {
                return ((MetalPressRecipe)recipe).testInput(stack);
            }
            else
            {
                Container inv = new RecipeSearchInventory(stack, internalItemHandler.getStackInSlot(1));
                return findRecipeWithInv(inv, false) != null;
            }
        }
        else if (slot == 1)
        {
            return stack.getCount() == 1 && Utils.tagContains(TagHolder.MOLDS, stack.getItem());
        }
        else if (slot == 2)
        {
            return recipe != null && ((MetalPressRecipe)recipe).getResultItem().sameItemStackIgnoreDurability(stack);
        }
        return false;
    }

    @Override
    protected void pushOutputs()
    {
        if (internalItemHandler.getStackInSlot(2).isEmpty()) { return; }

        boolean worked = false;
        for (Direction dir : Direction.values())
        {
            SideAccess mode = cardinalPorts.get(dir);
            if (mode.isOutput())
            {
                LazyOptional<IItemHandler> adj = getNeighboringHandler(dir);
                worked |= adj.map(handler ->
                {
                    ItemStack stack = internalItemHandler.getStackInSlot(2);
                    if (!stack.isEmpty())
                    {
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                        internalItemHandler.setStackInSlot(2, remainder);
                        return true;
                    }
                    return false;
                }).orElse(false);
                if (internalItemHandler.getStackInSlot(2).isEmpty()) { break; }
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
            case INPUT_ALL -> SideAccess.INPUT_MAIN;
            case INPUT_MAIN -> SideAccess.INPUT_SECOND;
            case INPUT_SECOND -> SideAccess.OUTPUT_ALL;
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
            case INPUT_MAIN -> SideAccess.INPUT_ALL;
            case INPUT_SECOND -> SideAccess.INPUT_MAIN;
            case OUTPUT_ALL -> SideAccess.INPUT_SECOND;
            default -> throw new IllegalStateException("Invalid port setting: " + port.getSerializedName());
        };
    }

    @Override
    public boolean canInsert(Direction side, int slot)
    {
        SideAccess port = cardinalPorts.get(side);
        if (slot == 0)
        {
            return port == SideAccess.INPUT_ALL || port == SideAccess.INPUT_MAIN;
        }
        else if (slot == 1)
        {
            return port == SideAccess.INPUT_ALL || port == SideAccess.INPUT_SECOND;
        }
        return false;
    }

    @Override
    public boolean canExtract(Direction side, int slot) { return cardinalPorts.get(side).isOutput() && slot == 2; }

    @Override
    protected Recipe<Container> findRecipeWithInv(Container inv, boolean checkCounts)
    {
        Recipe<Container> recipe = super.findRecipeWithInv(inv, checkCounts);

        if (!checkCounts) { return recipe; }

        if (recipe instanceof MetalPressRecipe pressRecipe)
        {
            if (pressRecipe.testInput(inv.getItem(0)))
            {
                return recipe;
            }
        }
        return null;
    }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    protected int getBaseConsumption() { return BASE_CONSUMPTION; }

    @Override
    protected int getEnergyRequired() { return energy; }

    @Override
    public Component getDisplayName() { return TITLE; }

    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player)
    {
        return new ContainerMenuMetalPress(id, this, inventory);
    }
}