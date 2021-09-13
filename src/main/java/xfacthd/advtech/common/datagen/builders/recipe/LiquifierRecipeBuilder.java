package xfacthd.advtech.common.datagen.builders.recipe;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.Tag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.util.Utils;

import java.util.function.Consumer;

public class LiquifierRecipeBuilder
{
    private final FluidStack result;
    private int energy = 0;
    private Ingredient input = null;
    private int inputCount = 0;

    private LiquifierRecipeBuilder(FluidStack result)
    {
        Preconditions.checkArgument(!result.isEmpty(), "Result cannot be empty!");
        this.result = result;
    }

    public static LiquifierRecipeBuilder liquifierRecipe(FluidStack result) { return new LiquifierRecipeBuilder(result); }

    public LiquifierRecipeBuilder energy(int energy)
    {
        Preconditions.checkState(energy > 0, "Energy must be higher than 0!");
        this.energy = energy;
        return this;
    }

    public LiquifierRecipeBuilder input(Tag<Item> item, int count) { return input(Ingredient.of(item), count); }

    public LiquifierRecipeBuilder input(ItemLike item, int count) { return input(Ingredient.of(item), count); }

    public LiquifierRecipeBuilder input(Ingredient ingredient, int count)
    {
        Preconditions.checkState(input == null, "Input already set!");
        Preconditions.checkArgument(count > 0, "Count must be positive!");
        input = ingredient;
        inputCount = count;
        return this;
    }

    public void build(Consumer<FinishedRecipe> consumer, ResourceLocation id)
    {
        Preconditions.checkState(energy > 0, "No energy consumption set!");
        Preconditions.checkState(input != null, "No input set!");
        consumer.accept(new Result(id, this));
    }

    public static class Result implements FinishedRecipe
    {
        private final ResourceLocation id;
        private final FluidStack result;
        private final int energy;
        private final Ingredient input;
        private final int inputCount;

        private Result(ResourceLocation id, LiquifierRecipeBuilder builder)
        {
            this.id = id;
            this.result = builder.result;
            this.energy = builder.energy;
            this.input = builder.input;
            this.inputCount = builder.inputCount;
        }

        @Override
        public void serializeRecipeData(JsonObject json)
        {
            json.addProperty("energy", energy);
            json.add("input", input.toJson());
            json.addProperty("inputCount", inputCount);
            json.add("output", Utils.writeFluidStackToJson(result));
        }

        @Override
        public ResourceLocation getId() { return id; }

        @Override
        public RecipeSerializer<?> getType() { return ATContent.RECIPE_LIQUIFIER.get(); }

        @Override
        public JsonObject serializeAdvancement() { return null; }

        @Override
        public ResourceLocation getAdvancementId() { return null; }
    }
}