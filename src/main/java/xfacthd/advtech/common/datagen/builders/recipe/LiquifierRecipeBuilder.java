package xfacthd.advtech.common.datagen.builders.recipe;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.common.data.types.RecipeSerializers;
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

    public LiquifierRecipeBuilder input(Tag<Item> item, int count) { return input(Ingredient.fromTag(item), count); }

    public LiquifierRecipeBuilder input(IItemProvider item, int count) { return input(Ingredient.fromItems(item), count); }

    public LiquifierRecipeBuilder input(Ingredient ingredient, int count)
    {
        Preconditions.checkState(input == null, "Input already set!");
        Preconditions.checkArgument(count > 0, "Count must be positive!");
        input = ingredient;
        inputCount = count;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        Preconditions.checkState(energy > 0, "No energy consumption set!");
        Preconditions.checkState(input != null, "No input set!");
        consumer.accept(new LiquifierRecipeBuilder.Result(id, this));
    }

    public static class Result implements IFinishedRecipe
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
        public void serialize(JsonObject json)
        {
            json.addProperty("energy", energy);
            json.add("input", input.serialize());
            json.addProperty("inputCount", inputCount);
            json.add("output", Utils.writeFluidStackToJson(result));
        }

        @Override
        public ResourceLocation getID() { return id; }

        @Override
        public IRecipeSerializer<?> getSerializer() { return RecipeSerializers.liquifier; }

        @Override
        public JsonObject getAdvancementJson() { return null; }

        @Override
        public ResourceLocation getAdvancementID() { return null; }
    }
}