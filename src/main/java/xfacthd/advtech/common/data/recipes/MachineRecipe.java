package xfacthd.advtech.common.data.recipes;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.registries.ForgeRegistryEntry;

public abstract class MachineRecipe implements Recipe<Container>
{
    private final ResourceLocation name;
    private final int energy;

    protected MachineRecipe(ResourceLocation name, int energy)
    {
        this.name = name;
        this.energy = energy;
    }

    @Override
    public ResourceLocation getId() { return name; }

    @Override
    public boolean matches(Container inv, Level level) { return true; }

    @Override
    public boolean isSpecial() { return true; }

    @Override
    public ItemStack assemble(Container inv) { return ItemStack.EMPTY; }

    @Override
    public boolean canCraftInDimensions(int width, int height) { return true; }

    public int getEnergy() { return energy; }

    public static abstract class Serializer<R extends MachineRecipe> extends ForgeRegistryEntry<RecipeSerializer<?>> implements RecipeSerializer<R>
    {

    }
}