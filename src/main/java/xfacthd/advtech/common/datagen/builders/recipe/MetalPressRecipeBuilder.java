package xfacthd.advtech.common.datagen.builders.recipe;

import com.google.common.base.Preconditions;
import com.google.gson.JsonObject;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.tags.Tag;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.common.data.types.RecipeSerializers;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.function.Consumer;

public class MetalPressRecipeBuilder
{
    private final Item result;
    private final int count;
    private int energy;
    private Ingredient input;
    private int inputCount;
    private ItemStack mold;

    private MetalPressRecipeBuilder(Item result, int count)
    {
        Preconditions.checkState(count > 0, "Result count must be higher than 0!");
        this.result = result;
        this.count = count;
    }

    public static MetalPressRecipeBuilder pressRecipe(IItemProvider result) { return pressRecipe(result, 1); }

    public static MetalPressRecipeBuilder pressRecipe(IItemProvider result, int count)
    {
        return new MetalPressRecipeBuilder(result.asItem(), count);
    }

    public MetalPressRecipeBuilder energy(int energy)
    {
        Preconditions.checkState(energy > 0, "Energy must be higher than 0!");
        this.energy = energy;
        return this;
    }

    public MetalPressRecipeBuilder input(Tag<Item> item, int count) { return input(Ingredient.fromTag(item), count); }

    public MetalPressRecipeBuilder input(IItemProvider item, int count) { return input(Ingredient.fromItems(item), count); }

    public MetalPressRecipeBuilder input(Ingredient ingredient, int count)
    {
        Preconditions.checkState(input == null, "Input already set!");
        Preconditions.checkArgument(count > 0, "Count must be positive!");
        input = ingredient;
        inputCount = count;
        return this;
    }

    public MetalPressRecipeBuilder mold(ItemStack mold)
    {
        Preconditions.checkArgument(TagHolder.MOLDS.contains(mold.getItem()), "Mold item must be in the advtech:molds tag!");
        this.mold = mold;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        Preconditions.checkState(energy > 0, "No energy consumption set!");
        Preconditions.checkState(input != null, "No input set!");
        Preconditions.checkState(mold != null, "No mold set!");
        consumer.accept(new MetalPressRecipeBuilder.Result(id, this));
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final Item result;
        private final int resultCount;
        private final int energy;
        private final Ingredient input;
        private final int inputCount;
        private final ItemStack mold;

        private Result(ResourceLocation id, MetalPressRecipeBuilder builder)
        {
            this.id = id;
            result = builder.result;
            resultCount = builder.count;
            energy = builder.energy;
            input = builder.input;
            inputCount = builder.inputCount;
            mold = builder.mold;
        }

        @Override
        public void serialize(JsonObject json)
        {
            json.addProperty("energy", energy);
            json.add("input", input.serialize());
            json.addProperty("inputCount", inputCount);

            JsonObject moldObj = new JsonObject();
            //noinspection ConstantConditions
            moldObj.addProperty("item", mold.getItem().getRegistryName().toString());
            json.add("mold", moldObj);

            JsonObject resultObj = new JsonObject();
            //noinspection ConstantConditions
            resultObj.addProperty("item", result.getRegistryName().toString());
            if (resultCount > 1) { resultObj.addProperty("count", resultCount); }
            json.add("output", resultObj);
        }

        @Override
        public ResourceLocation getID() { return id; }

        @Override
        public IRecipeSerializer<?> getSerializer() { return RecipeSerializers.metalPress; }

        @Override
        public JsonObject getAdvancementJson() { return null; }

        @Override
        public ResourceLocation getAdvancementID() { return null; }
    }
}