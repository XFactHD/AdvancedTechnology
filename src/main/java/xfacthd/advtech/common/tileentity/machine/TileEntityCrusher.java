package xfacthd.advtech.common.tileentity.machine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.container.machine.ContainerCrusher;
import xfacthd.advtech.common.data.recipes.CrusherRecipe;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityProducer;
import xfacthd.advtech.common.util.inventory.RecipeSearchInventory;

import java.util.Random;

public class TileEntityCrusher extends TileEntityProducer
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".crusher");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;

    private final Random rand = new Random(System.currentTimeMillis());
    private Item lastInput = Items.AIR;
    private int energy = 0;

    public TileEntityCrusher() { super(TileEntityTypes.tileTypeCrusher, CrusherRecipe.TYPE); }

    @Override
    public void tick()
    {
        super.tick();

        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (recipe == null && slotNotEmpty(0))
            {
                recipe = findRecipe();
            }

            if (recipe != null && hasEnoughEnergy() && (active || canStart()) && slotNotEmpty(0))
            {
                if (!canFitInSlot(1, recipe.getRecipeOutput()) || !canFitInSlot(2, ((CrusherRecipe)recipe).getSecondaryOutput()))
                {
                    if (active) { setActive(false); }
                }
                else if (!active || progress == -1)
                {
                    if (progress == -1)
                    {
                        progress = 0;
                        energy = ((CrusherRecipe) recipe).getEnergy();
                    }

                    if (!active)
                    {
                        setActive(true);
                    }
                }
                else if (progress >= energy)
                {
                    ItemStack result = ((CrusherRecipe)recipe).getRecipeOutput().copy();
                    internalItemHandler.insertItem(1, result, false);

                    if (((CrusherRecipe)recipe).hasSecondaryOutput() && rand.nextDouble() <= ((CrusherRecipe)recipe).getSecondaryChance())
                    {
                        ItemStack secondary = ((CrusherRecipe)recipe).getSecondaryOutput().copy();
                        internalItemHandler.insertItem(2, secondary, false);
                    }

                    internalItemHandler.extractItem(0, 1, false); //Must happen after retrieving the result because emptying the slot sets the recipe to null

                    progress = -1;
                }
            }
            else if (active)
            {
                progress = -1;
                setActive(false);
            }
        }
    }

    @Override
    protected void initCapabilities()
    {
        internalItemHandler = new MachineItemStackHandler(this, 3);
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
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (slot == 1)
        {
            return recipe != null && recipe.getRecipeOutput().isItemEqual(stack);
        }
        else if (slot == 2)
        {
            return recipe != null && ((CrusherRecipe)recipe).getSecondaryOutput().isItemEqual(stack);
        }
        else
        {
            //If the recipe is not null, then there is already a compatible item in the slot
            if (recipe != null)
            {
                return ((CrusherRecipe)recipe).getInput().test(stack);
            }

            IInventory inv = new RecipeSearchInventory(stack);
            return findRecipeWithInv(inv, false) != null;
        }
    }

    @Override
    protected void pushOutputs()
    {
        if (internalItemHandler.getStackInSlot(1).isEmpty() && internalItemHandler.getStackInSlot(2).isEmpty()) { return; }

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
                        ItemStack stack = internalItemHandler.getStackInSlot(1);
                        if (!stack.isEmpty())
                        {
                            ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                            internalItemHandler.setStackInSlot(1, remainder);
                        }
                    }

                    if (mode == SideAccess.OUTPUT_SECOND || mode == SideAccess.OUTPUT_ALL)
                    {
                        ItemStack stack = internalItemHandler.getStackInSlot(2);
                        if (!stack.isEmpty())
                        {
                            ItemStack remainder = ItemHandlerHelper.insertItemStacked(handler, stack, false);
                            internalItemHandler.setStackInSlot(2, remainder);
                        }
                    }
                });
            }
        }
    }

    @Override
    public SideAccess getNextPortSetting(Side side)
    {
        if (side == Side.FRONT) { return SideAccess.NONE; }

        SideAccess port = ports.get(side);
        switch (port)
        {
            case NONE: return SideAccess.INPUT_ALL;
            case INPUT_ALL: return SideAccess.OUTPUT_ALL;
            case OUTPUT_ALL: return SideAccess.OUTPUT_MAIN;
            case OUTPUT_MAIN: return SideAccess.OUTPUT_SECOND;
            case OUTPUT_SECOND: return SideAccess.NONE;
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
            case NONE: return SideAccess.OUTPUT_SECOND;
            case INPUT_ALL: return SideAccess.NONE;
            case OUTPUT_ALL: return SideAccess.INPUT_ALL;
            case OUTPUT_MAIN: return SideAccess.OUTPUT_ALL;
            case OUTPUT_SECOND: return SideAccess.OUTPUT_MAIN;
            default: throw new IllegalStateException("Invalid port setting: " + port.getName());
        }
    }

    public boolean canInsert(Direction side, int slot) { return cardinalPorts.get(side).isInput() && slot == 0; }

    public boolean canExtract(Direction side, int slot)
    {
        SideAccess port = cardinalPorts.get(side);
        if (slot == 1)
        {
            return port == SideAccess.OUTPUT_MAIN || port == SideAccess.OUTPUT_ALL;
        }
        else if (slot == 2)
        {
            return port == SideAccess.OUTPUT_SECOND || port == SideAccess.OUTPUT_ALL;
        }
        return false;
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
        return new ContainerCrusher(id, this, inventory);
    }
}