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
import xfacthd.advtech.common.container.machine.ContainerAlloySmelter;
import xfacthd.advtech.common.data.recipes.AlloySmelterRecipe;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityProducer;
import xfacthd.advtech.common.util.inventory.RecipeSearchInventory;

import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class TileEntityAlloySmelter extends TileEntityProducer
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".alloy_smelter");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;

    private final Random rand = new Random(System.currentTimeMillis());
    private Item lastMainInput = Items.AIR;
    private Item lastSecondInput = Items.AIR;
    private int energy = 0;

    public TileEntityAlloySmelter() { super(TileEntityTypes.tileTypeAlloySmelter, AlloySmelterRecipe.TYPE); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (recipe == null && (slotNotEmpty(0) || slotNotEmpty(1)))
            {
                recipe = findRecipe();
            }

            if (recipe != null && hasEnoughEnergy() && canRun(progress == -1))
            {
                AlloySmelterRecipe alloyRecipe = (AlloySmelterRecipe)recipe;

                boolean hasInputs = alloyRecipe.testInputs(internalItemHandler.getStackInSlot(0), internalItemHandler.getStackInSlot(1), true);
                boolean canFitOutputs = canFitInSlot(2, alloyRecipe.getRecipeOutput()) && canFitInSlot(3, alloyRecipe.getSecondaryOutput());
                if (!hasInputs || !canFitOutputs)
                {
                    if (active) { setActive(false); }
                    if (!hasInputs) { progress = -1; }
                }
                else if (!active || progress == -1)
                {
                    if (progress == -1)
                    {
                        progress = 0;
                        energy = ((AlloySmelterRecipe) recipe).getEnergy();
                    }

                    if (!active)
                    {
                        setActive(true);
                    }
                }
                else if (progress >= energy)
                {
                    ItemStack result = alloyRecipe.getRecipeOutput().copy();
                    internalItemHandler.insertItem(2, result, false);

                    if (alloyRecipe.hasSecondaryOutput() && rand.nextDouble() <= alloyRecipe.getSecondaryChance())
                    {
                        ItemStack secondary = alloyRecipe.getSecondaryOutput().copy();
                        internalItemHandler.insertItem(3, secondary, false);
                    }

                    //Must happen after retrieving the result because emptying the slot sets the recipe to null
                    ItemStack primInput = internalItemHandler.getStackInSlot(0);
                    ItemStack secInput = internalItemHandler.getStackInSlot(1);
                    if (!primInput.isEmpty())
                    {
                        internalItemHandler.extractItem(0, alloyRecipe.getInputCount(primInput), false);
                    }
                    if (!secInput.isEmpty())
                    {
                        internalItemHandler.extractItem(1, alloyRecipe.getInputCount(secInput), false);
                    }

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
        internalItemHandler = new MachineItemStackHandler(this, 4);
        super.initCapabilities();
    }

    @Override
    public void onSlotChangedInternal(int slot)
    {
        if (slot == 0 || slot == 1)
        {
            ItemStack main = internalItemHandler.getStackInSlot(0);
            ItemStack second = internalItemHandler.getStackInSlot(1);

            if (main.isEmpty() && second.isEmpty() && lastMainInput != Items.AIR && lastSecondInput != Items.AIR)
            {
                recipe = null;
                progress = -1;
                energy = 0;
                lastMainInput = Items.AIR;
                lastSecondInput = Items.AIR;
                setActive(false);
            }
            else if (main.getItem() != lastMainInput || second.getItem() != lastSecondInput)
            {
                lastMainInput = main.getItem();
                lastSecondInput = second.getItem();

                recipe = findRecipe();
            }
        }
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == 2)
        {
            return recipe != null && recipe.getRecipeOutput().isItemEqual(stack);
        }
        else if (slot == 3)
        {
            return recipe != null && ((AlloySmelterRecipe)recipe).getSecondaryOutput().isItemEqual(stack);
        }
        else
        {
            //If the recipe is not null, then there is already a compatible item in the slot
            if (recipe != null)
            {
                ItemStack other = slot == 0 ? internalItemHandler.getStackInSlot(1) : internalItemHandler.getStackInSlot(0);
                return ((AlloySmelterRecipe)recipe).testInputs(stack, other, false);
            }

            ItemStack other = slot == 0 ? internalItemHandler.getStackInSlot(1) : internalItemHandler.getStackInSlot(0);
            IInventory inv = new RecipeSearchInventory(stack, other);
            return findRecipeWithInv(inv, false) != null;
        }
    }

    @Override
    protected void pushOutputs()
    {
        if (internalItemHandler.getStackInSlot(1).isEmpty() && internalItemHandler.getStackInSlot(2).isEmpty()) { return; }

        AtomicBoolean worked = new AtomicBoolean(false);
        for (Direction dir : Direction.values())
        {
            SideAccess mode = cardinalPorts.get(dir);
            if (mode.isOutput())
            {
                LazyOptional<IItemHandler> adj = getNeighboringHandler(dir);
                adj.ifPresent(handler ->
                {
                    if (mode == SideAccess.OUTPUT_MAIN || mode == SideAccess.OUTPUT_ALL)
                    {
                        ItemStack stack = internalItemHandler.getStackInSlot(2);
                        if (!stack.isEmpty())
                        {
                            ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                            internalItemHandler.setStackInSlot(1, remainder);
                            worked.set(true);
                        }
                    }

                    if (mode == SideAccess.OUTPUT_SECOND || mode == SideAccess.OUTPUT_ALL)
                    {
                        ItemStack stack = internalItemHandler.getStackInSlot(3);
                        if (!stack.isEmpty())
                        {
                            ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                            internalItemHandler.setStackInSlot(2, remainder);
                            worked.set(true);
                        }
                    }
                });
                if (internalItemHandler.getStackInSlot(1).isEmpty() && internalItemHandler.getStackInSlot(2).isEmpty()) { break; }
            }
        }
        if (worked.get()) { markDirty(); }
    }

    @Override
    public boolean canInsert(Direction side, int slot)
    {
        SideAccess port = cardinalPorts.get(side);
        if (slot == 0)
        {
            return port == SideAccess.INPUT_MAIN || port == SideAccess.INPUT_ALL;
        }
        else if (slot == 1)
        {
            return port == SideAccess.INPUT_SECOND || port == SideAccess.INPUT_ALL;
        }
        return false;
    }

    @Override
    public boolean canExtract(Direction side, int slot)
    {
        SideAccess port = cardinalPorts.get(side);
        if (slot == 2)
        {
            return port == SideAccess.OUTPUT_MAIN || port == SideAccess.OUTPUT_ALL;
        }
        else if (slot == 3)
        {
            return port == SideAccess.OUTPUT_SECOND || port == SideAccess.OUTPUT_ALL;
        }
        return false;
    }

    @Override
    protected IRecipe<IInventory> findRecipeWithInv(IInventory inv, boolean checkCounts)
    {
        IRecipe<IInventory> recipe = super.findRecipeWithInv(inv, checkCounts);

        if (!checkCounts) { return recipe; }

        if (recipe instanceof AlloySmelterRecipe)
        {
            AlloySmelterRecipe alloyRecipe = (AlloySmelterRecipe)recipe;

            ItemStack primInput = internalItemHandler.getStackInSlot(0);
            ItemStack secInput = internalItemHandler.getStackInSlot(1);

            if (alloyRecipe.testInputs(primInput, secInput, true))
            {
                return recipe;
            }
        }
        return null;
    }

    @Override
    protected int getEnergyRequired() { return energy; }

    @Override
    protected int getBaseEnergyCapacity() { return BASE_CAPACITY; }

    @Override
    protected int getBaseConsumption() { return BASE_CONSUMPTION; }

    @Override
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerAlloySmelter(id, this, inventory);
    }
}