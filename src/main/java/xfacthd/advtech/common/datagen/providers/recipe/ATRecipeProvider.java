package xfacthd.advtech.common.datagen.providers.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.common.crafting.ConditionalRecipe;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.common.crafting.conditions.ModLoadedCondition;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MaterialType;
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



    protected static Ingredient oreAsTag(MaterialType material)
    {
        return Ingredient.of(TagHolder.ITEM_ORES.get(material));
    }

    protected static Ingredient powderAsTag(MaterialType material)
    {
        return Ingredient.of(TagHolder.DUSTS.get(material));
    }

    protected static ItemLike powderAsItem(MaterialType material) { return ATContent.POWDER_ITEMS.get(material).get(); }

    protected static ItemLike ingotAsItem(MaterialType material)
    {
        if (material == MaterialType.IRON) { return Items.IRON_INGOT; }
        if (material == MaterialType.GOLD) { return Items.GOLD_INGOT; }
        if (material == MaterialType.COPPER) { return Items.COPPER_INGOT; }
        return ATContent.INGOT_ITEMS.get(material).get();
    }



    /**
     * Register a recipe wrapped in a {@link ConditionalRecipe}. Useful for mod compat recipes, as those recipes
     * would otherwise crash if the other mod is not installed
     *
     * @param condition The condition for this recipe
     * @param recipeBuilder The recipe (pass reference to {@code XyzRecipeBuilder.build(Consumer, ResourceLocation)})
     * @param consumer The recipe consumer given by {@link RecipeProvider#buildCraftingRecipes(Consumer)}
     * @param id The recipe id
     */
    protected static void conditionalRecipe(ICondition condition,
                                            BiConsumer<Consumer<FinishedRecipe>, ResourceLocation> recipeBuilder,
                                            Consumer<FinishedRecipe> consumer,
                                            ResourceLocation id)
    {
        ConditionalRecipe.builder()
                .addCondition(condition)
                .addRecipe(callable -> recipeBuilder.accept(callable, id))
                .build(consumer, id);
    }

    protected static void modCompatRecipe(String modid,
                                          BiConsumer<Consumer<FinishedRecipe>, ResourceLocation> recipeBuilder,
                                          Consumer<FinishedRecipe> consumer,
                                          ResourceLocation id)
    {
        conditionalRecipe(new ModLoadedCondition(modid), recipeBuilder, consumer, id);
    }



    protected ResourceLocation name(String name)
    {
        return new ResourceLocation(AdvancedTechnology.MODID, getRecipePrefix() + "_" + name);
    }
}