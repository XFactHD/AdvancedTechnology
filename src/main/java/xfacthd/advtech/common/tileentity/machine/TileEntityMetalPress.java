package xfacthd.advtech.common.tileentity.machine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.container.machine.ContainerMetalPress;
import xfacthd.advtech.common.data.recipes.MetalPressRecipe;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityProducer;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.util.inventory.RecipeSearchInventory;

import java.util.concurrent.atomic.AtomicBoolean;

public class TileEntityMetalPress extends TileEntityProducer
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".metal_press");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;

    private Item lastInput = Items.AIR;
    private Item lastMold = Items.AIR;
    private int energy = 0;

    public TileEntityMetalPress() { super(TileEntityTypes.tileTypeMetalPress, MetalPressRecipe.TYPE); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (recipe == null && slotNotEmpty(0) && slotNotEmpty(1))
            {
                recipe = findRecipe();
            }

            if (recipe != null && hasEnoughEnergy() && canRun(progress == -1) && slotNotEmpty(0) && slotNotEmpty(1))
            {
                if (!canFitInSlot(2, recipe.getRecipeOutput()))
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
                    ItemStack result = recipe.getRecipeOutput().copy();
                    internalItemHandler.insertItem(2, result, false);

                    int inputCount = ((MetalPressRecipe)recipe).getInputCount();
                    internalItemHandler.extractItem(0, inputCount, false);

                    progress = -1;
                }
            }
            else if (active)
            {
                progress = -1;
                setActive(false);
            }
        }

        super.tick();
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
                IInventory inv = new RecipeSearchInventory(stack, internalItemHandler.getStackInSlot(1));
                return findRecipeWithInv(inv, false) != null;
            }
        }
        else if (slot == 1)
        {
            return stack.getCount() == 1 && TagHolder.MOLDS.contains(stack.getItem());
        }
        else if (slot == 2)
        {
            return recipe != null && ((MetalPressRecipe)recipe).getRecipeOutput().isItemEqual(stack);
        }
        return false;
    }

    @Override
    protected void pushOutputs()
    {
        if (internalItemHandler.getStackInSlot(2).isEmpty()) { return; }

        AtomicBoolean worked = new AtomicBoolean(false);
        for (Direction dir : Direction.values())
        {
            SideAccess mode = cardinalPorts.get(dir);
            if (mode.isOutput())
            {
                LazyOptional<IItemHandler> adj = getNeighboringHandler(dir);
                adj.ifPresent(handler ->
                {
                    ItemStack stack = internalItemHandler.getStackInSlot(2);
                    if (!stack.isEmpty())
                    {
                        ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                        internalItemHandler.setStackInSlot(2, remainder);
                        worked.set(true);
                    }
                });
                if (internalItemHandler.getStackInSlot(2).isEmpty()) { break; }
            }
        }
        if (worked.get()) { markDirty(); }
    }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        switch (port)
        {
            case NONE: return SideAccess.INPUT_ALL;
            case INPUT_ALL: return SideAccess.INPUT_MAIN;
            case INPUT_MAIN: return SideAccess.INPUT_SECOND;
            case INPUT_SECOND: return SideAccess.OUTPUT_ALL;
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
            case INPUT_MAIN: return SideAccess.INPUT_ALL;
            case INPUT_SECOND: return SideAccess.INPUT_MAIN;
            case OUTPUT_ALL: return SideAccess.INPUT_SECOND;
            default: throw new IllegalStateException("Invalid port setting: " + port.getName());
        }
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
    protected IRecipe<IInventory> findRecipeWithInv(IInventory inv, boolean checkCounts)
    {
        IRecipe<IInventory> recipe = super.findRecipeWithInv(inv, checkCounts);

        if (!checkCounts) { return recipe; }

        if (recipe instanceof MetalPressRecipe)
        {
            MetalPressRecipe pressRecipe = (MetalPressRecipe)recipe;
            if (pressRecipe.testInput(inv.getStackInSlot(0)))
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
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerMetalPress(id, this, inventory);
    }
}