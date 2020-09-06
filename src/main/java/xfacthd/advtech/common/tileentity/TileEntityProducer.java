package xfacthd.advtech.common.tileentity;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.item.crafting.IRecipeType;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import xfacthd.advtech.common.capability.energy.EnergySink;

public abstract class TileEntityProducer extends TileEntityInventoryMachine
{
    protected final IRecipeType<? extends IRecipe<IInventory>> recipeType;
    protected final IInventory recipeInv;

    protected int progress = -1;
    protected int energyConsumption = 0;
    protected IRecipe<?> recipe = null;

    public TileEntityProducer(TileEntityType<?> type, IRecipeType<? extends IRecipe<IInventory>> recipeType)
    {
        super(type);

        this.recipeType = recipeType;
        recipeInv = new RecipeWrapper(internalItemHandler);
    }

    @Override
    public void tick()
    {
        super.tick();

        //noinspection ConstantConditions
        if (!world.isRemote())
        {
            if (active)
            {
                if (!hasEnoughEnergy())
                {
                    setActive(false);
                }
                else
                {
                    float mult = (float) energyHandler.getEnergyStored() / (float) energyHandler.getMaxEnergyStored();
                    int actualConsumption = Math.max((int)Math.ceil(energyConsumption * mult), getBaseConsumption());
                    int result = energyHandler.extractEnergyInternal(actualConsumption, false);

                    progress += result;
                }
            }
        }
    }

    @Override
    public void onLevelChanged()
    {
        int mult = (int)Math.pow(2, level.ordinal());
        energyConsumption = getBaseConsumption() * mult;

        int capacity = getBaseEnergyCapacity() * mult;
        int maxReceive = energyConsumption * 10;
        energyHandler.reconfigure(capacity, maxReceive, 0);
    }

    @Override
    protected void initCapabilities()
    {
        super.initCapabilities();

        energyHandler = new EnergySink(getBaseEnergyCapacity(), getBaseConsumption() * 10);
    }

    protected abstract int getBaseConsumption();

    @Override
    public final float getProgress()
    {
        if (progress == -1) { return 0; }
        return MathHelper.clamp((float)progress / (float) getEnergyRequired(), 0, 1);
    }

    @Override //TODO: implement enhancement support on producers and make this true
    public boolean supportsEnhancements() { return false; }

    /*
     * Inventory
     */

    @Override
    public boolean canForcePush() { return true; }

    protected abstract int getEnergyRequired();

    protected boolean hasEnoughEnergy() { return energyHandler.getEnergyStored() >= getBaseConsumption(); }

    /*
     * Recipe stuff
     */

    protected final IRecipe<IInventory> findRecipe() { return findRecipeWithInv(recipeInv, true); }

    protected IRecipe<IInventory> findRecipeWithInv(IInventory inv, boolean checkCounts)
    {
        //noinspection ConstantConditions
        return world.getRecipeManager().getRecipe(recipeType, inv, world).orElse(null);
    }

    public IRecipe<?> getRecipe() { return recipe; }

    /*
     * NBT stuff
     */

    @Override
    public void readNetworkNBT(CompoundNBT nbt)
    {
        super.readNetworkNBT(nbt);

        String recipeLoc = nbt.getString("recipe");
        if (recipeLoc.equals("null")) { recipe = null; }
        else
        {
            //noinspection ConstantConditions
            recipe = world.getRecipeManager().getRecipe(new ResourceLocation(recipeLoc)).orElse(null);
        }
    }

    @Override
    public void writeNetworkNBT(CompoundNBT nbt)
    {
        super.writeNetworkNBT(nbt);

        nbt.putString("recipe", recipe != null ? recipe.getId().toString() : "null");
    }
}