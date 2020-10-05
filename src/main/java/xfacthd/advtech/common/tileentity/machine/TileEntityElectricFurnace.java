package xfacthd.advtech.common.tileentity.machine;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.*;
import net.minecraft.item.crafting.*;
import net.minecraft.util.Direction;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.capability.item.MachineItemStackHandler;
import xfacthd.advtech.common.container.machine.ContainerElectricFurnace;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.data.types.TileEntityTypes;
import xfacthd.advtech.common.tileentity.TileEntityProducer;
import xfacthd.advtech.common.util.inventory.RecipeSearchInventory;

import java.util.concurrent.atomic.AtomicBoolean;

public class TileEntityElectricFurnace extends TileEntityProducer
{
    public static final ITextComponent TITLE = new TranslationTextComponent("gui." + AdvancedTechnology.MODID + ".furnace");
    private static final int BASE_CAPACITY = 20000;
    private static final int BASE_CONSUMPTION = 20;

    private Item lastInput = Items.AIR;
    private int burnTime = 0;

    public TileEntityElectricFurnace() { super(TileEntityTypes.tileTypeElectricFurnace, IRecipeType.SMELTING); }

    @Override
    public void tick()
    {
        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (recipe == null && !internalItemHandler.getStackInSlot(0).isEmpty())
            {
                recipe = findRecipe();
            }

            if (recipe != null && hasEnoughEnergy() && (active || canStart()) && canFitInSlot(1, recipe.getRecipeOutput()) && slotNotEmpty(0))
            {
                if (progress == -1)
                {
                    progress = 0;
                    burnTime = ((AbstractCookingRecipe)recipe).getCookTime() * 10;

                    if (!active)
                    {
                        setActive(true);
                    }
                }
                else if (progress >= burnTime)
                {
                    ItemStack result = ((AbstractCookingRecipe)recipe).getCraftingResult(recipeInv);

                    internalItemHandler.insertItem(1, result, false);
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

        super.tick();
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
            return recipe != null && recipe.getRecipeOutput().isItemEqual(stack);
        }
        else
        {
            //If the recipe is not null, then there is already a compatible item in the slot
            if (recipe != null) { return true; }

            IInventory inv = new RecipeSearchInventory(stack);
            return findRecipeWithInv(inv, false) != null;
        }
    }

    @Override
    protected void pushOutputs()
    {
        if (internalItemHandler.getStackInSlot(1).isEmpty()) { return; }

        AtomicBoolean worked = new AtomicBoolean(false);
        for (Direction dir : Direction.values())
        {
            SideAccess port = cardinalPorts.get(dir);
            if (port.isOutput())
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
    public ITextComponent getDisplayName() { return TITLE; }

    @Override
    public Container createMenu(int id, PlayerInventory inventory, PlayerEntity player)
    {
        return new ContainerElectricFurnace(id, this, inventory);
    }
}