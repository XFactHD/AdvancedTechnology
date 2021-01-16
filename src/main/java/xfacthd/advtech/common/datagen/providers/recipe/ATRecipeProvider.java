package xfacthd.advtech.common.datagen.providers.recipe;

import net.minecraft.data.*;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public abstract class ATRecipeProvider extends RecipeProvider
{
    public ATRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    public final String getName() { return AdvancedTechnology.MODID + "." + getProviderName(); }

    protected String getProviderName() { return getRecipePrefix() + "_recipes"; }

    protected abstract String getRecipePrefix();



    protected static Ingredient oreAsTag(Materials material)
    {
        return Ingredient.fromTag(TagHolder.ITEM_ORES.get(material));
    }

    protected static Ingredient powderAsTag(Materials material)
    {
        return Ingredient.fromTag(TagHolder.DUSTS.get(material));
    }

    protected static IItemProvider powderAsItem(Materials material) { return ATContent.itemPowder.get(material); }

    protected static IItemProvider ingotAsItem(Materials material)
    {
        if (material == Materials.IRON) { return Items.IRON_INGOT; }
        if (material == Materials.GOLD) { return Items.GOLD_INGOT; }
        return ATContent.itemIngot.get(material);
    }



    /**
     * Register a recipe wrapped in a {@link ConditionalRecipe}. Useful for mod compat recipes, as those recipes
     * would otherwise crash if the other mod is not installed
     *
     * @param condition The condition for this recipe
     * @param recipeBuilder The recipe (pass reference to {@code XyzRecipeBuilder.build(Consumer, ResourceLocation)})
     * @param consumer The recipe consumer given by {@link RecipeProvider#registerRecipes(Consumer)}
     * @param id The recipe id
     */
    protected static void conditionalRecipe(ICondition condition,
                                            BiConsumer<Consumer<IFinishedRecipe>, ResourceLocation> recipeBuilder,
                                            Consumer<IFinishedRecipe> consumer,
                                            ResourceLocation id)
    {
        ConditionalRecipe.builder()
                .addCondition(condition)
                .addRecipe(callable -> recipeBuilder.accept(callable, id))
                .build(consumer, id);
    }

    protected static void modCompatRecipe(String modid,
                                          BiConsumer<Consumer<IFinishedRecipe>, ResourceLocation> recipeBuilder,
                                          Consumer<IFinishedRecipe> consumer,
                                          ResourceLocation id)
    {
        conditionalRecipe(new ModLoadedCondition(modid), recipeBuilder, consumer, id);
    }



    protected ResourceLocation name(String name)
    {
        return new ResourceLocation(AdvancedTechnology.MODID, getRecipePrefix() + "_" + name);
    }
}