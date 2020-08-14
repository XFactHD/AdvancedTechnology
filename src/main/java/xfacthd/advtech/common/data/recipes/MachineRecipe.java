package xfacthd.advtech.common.data.recipes;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistryEntry;
import xfacthd.advtech.AdvancedTechnology;

public abstract class MachineRecipe implements IRecipe<IInventory>
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
    public boolean matches(IInventory inv, World worldIn) { return true; }

    @Override
    public boolean isDynamic() { return true; }

    @Override
    public ItemStack getCraftingResult(IInventory inv) { return ItemStack.EMPTY; }

    @Override
    public boolean canFit(int width, int height) { return true; }

    public int getEnergy() { return energy; }

    protected static<R extends MachineRecipe> IRecipeType<R> registerType(String name, IRecipeType<R> type)
    {
        return Registry.register(Registry.RECIPE_TYPE, new ResourceLocation(AdvancedTechnology.MODID, name), type);
    }

    public static abstract class Serializer<R extends MachineRecipe> extends ForgeRegistryEntry<IRecipeSerializer<?>> implements IRecipeSerializer<R>
    {
        public Serializer(String name) { setRegistryName(AdvancedTechnology.MODID, name); }
    }
}