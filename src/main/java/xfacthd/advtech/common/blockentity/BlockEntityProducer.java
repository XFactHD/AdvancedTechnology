package xfacthd.advtech.common.blockentity;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.Container;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.wrapper.RecipeWrapper;
import xfacthd.advtech.common.capability.energy.EnergySink;

public abstract class BlockEntityProducer extends BlockEntityInventoryMachine
{
    protected final RecipeType<? extends Recipe<Container>> recipeType;
    protected final Container recipeInv;

    protected int progress = -1;
    protected int energyConsumption = 0;
    protected Recipe<?> recipe = null;

    public BlockEntityProducer(BlockEntityType<?> type, BlockPos pos, BlockState state, RecipeType<? extends Recipe<Container>> recipeType)
    {
        super(type, pos, state);
        this.recipeType = recipeType;
        this.recipeInv = new RecipeWrapper(internalItemHandler);
    }

    @Override
    protected void tickInternal()
    {
        if (!level().isClientSide())
        {
            if (active)
            {
                if (!hasEnoughEnergy())
                {
                    setActive(false);
                }
                else if (progress != -1)
                {
                    float mult = (float) energyHandler.getEnergyStored() / (float) energyHandler.getMaxEnergyStored();
                    int actualConsumption = Math.max((int)Math.ceil(energyConsumption * mult), getBaseConsumption());
                    int result = energyHandler.extractEnergyInternal(actualConsumption, false);

                    progress += result;
                }
            }
        }

        super.tickInternal();
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
        return Mth.clamp((float)progress / (float) getEnergyRequired(), 0, 1);
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

    protected final Recipe<Container> findRecipe() { return findRecipeWithInv(recipeInv, true); }

    protected Recipe<Container> findRecipeWithInv(Container inv, boolean checkCounts)
    {
        return level().getRecipeManager().getRecipeFor(recipeType, inv, level()).orElse(null);
    }

    public Recipe<?> getRecipe() { return recipe; }

    /*
     * NBT stuff
     */

    @Override
    public void readNetworkNBT(CompoundTag nbt)
    {
        super.readNetworkNBT(nbt);

        String recipeLoc = nbt.getString("recipe");
        if (recipeLoc.equals("null")) { recipe = null; }
        else
        {
            recipe = level().getRecipeManager().byKey(new ResourceLocation(recipeLoc)).orElse(null);
        }
    }

    @Override
    public void writeNetworkNBT(CompoundTag nbt)
    {
        super.writeNetworkNBT(nbt);

        nbt.putString("recipe", recipe != null ? recipe.getId().toString() : "null");
    }

    @Override
    public void writeSyncPacket(FriendlyByteBuf buffer)
    {
        super.writeSyncPacket(buffer);

        buffer.writeBoolean(recipe != null);
        if (recipe != null)
        {
            buffer.writeResourceLocation(recipe.getId());
        }
    }

    @Override
    protected void readSyncPacket(FriendlyByteBuf buffer)
    {
        super.readSyncPacket(buffer);

        if (buffer.readBoolean()) //Recipe != null
        {
            recipe = level().getRecipeManager().byKey(buffer.readResourceLocation()).orElse(null);
        }
        else
        {
            recipe = null;
        }
    }
}