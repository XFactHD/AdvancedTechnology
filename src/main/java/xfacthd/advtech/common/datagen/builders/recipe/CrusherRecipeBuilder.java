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
import xfacthd.advtech.common.data.types.RecipeSerializers;

import java.util.function.Consumer;

public class CrusherRecipeBuilder
{
    private final Item result;
    private final int count;
    private int energy;
    private Ingredient input;
    private Item secondaryResult = null;
    private int secondaryCount = 0;
    private double secondaryChance = 1;

    private CrusherRecipeBuilder(Item result, int count)
    {
        Preconditions.checkState(count > 0, "Result count must be higher than 0!");
        this.result = result;
        this.count = count;
    }

    public static CrusherRecipeBuilder crusherRecipe(IItemProvider result) { return crusherRecipe(result, 1); }

    public static CrusherRecipeBuilder crusherRecipe(IItemProvider result, int count)
    {
        return new CrusherRecipeBuilder(result.asItem(), count);
    }

    public CrusherRecipeBuilder energy(int energy)
    {
        Preconditions.checkState(energy > 0, "Energy must be higher than 0!");
        this.energy = energy;
        return this;
    }

    public CrusherRecipeBuilder input(Tag<Item> item) { return input(Ingredient.fromTag(item)); }

    public CrusherRecipeBuilder input(IItemProvider item) { return input(Ingredient.fromItems(item)); }

    public CrusherRecipeBuilder input(Ingredient ingredient)
    {
        Preconditions.checkState(input == null, "Input already set!");
        input = ingredient;
        return this;
    }

    public CrusherRecipeBuilder secondary(IItemProvider item) { return secondary(item, 1); }

    public CrusherRecipeBuilder secondary(IItemProvider item, int count)
    {
        Preconditions.checkState(secondaryResult == null, "Secondary output already set");
        Preconditions.checkState(count > 0, "Secondary count must be higher than 0!");
        secondaryResult = item.asItem();
        secondaryCount = count;
        return this;
    }

    public CrusherRecipeBuilder chance(double chance)
    {
        secondaryChance = chance;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        Preconditions.checkState(energy > 0, "No energy consumption set!");
        Preconditions.checkState(input != null, "No input set!");
        consumer.accept(new Result(id, this));
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final int energy;
        private final Ingredient input;
        private final Item secondaryResult;
        private final int secondaryCount;
        private final double secondaryChance;

        private Result(ResourceLocation id, CrusherRecipeBuilder builder)
        {
            this.id = id;
            result = builder.result;
            count = builder.count;
            energy = builder.energy;
            input = builder.input;
            secondaryResult = builder.secondaryResult;
            secondaryCount = builder.secondaryCount;
            secondaryChance = builder.secondaryChance;
        }

        @Override
        public void serialize(JsonObject json)
        {
            json.addProperty("energy", energy);
            json.add("input", input.serialize());

            JsonObject resultObj = new JsonObject();
            //noinspection ConstantConditions
            resultObj.addProperty("item", result.getRegistryName().toString());
            if (count > 1) { resultObj.addProperty("count", count); }
            json.add("primary", resultObj);

            if (secondaryResult != null)
            {
                JsonObject secondaryObj = new JsonObject();

                //noinspection ConstantConditions
                secondaryObj.addProperty("item", secondaryResult.getRegistryName().toString());
                if (secondaryCount > 1) { secondaryObj.addProperty("count", secondaryCount); }
                secondaryObj.addProperty("chance", secondaryChance);

                json.add("secondary", secondaryObj);
            }
        }

        @Override
        public ResourceLocation getID() { return id; }

        @Override
        public IRecipeSerializer<?> getSerializer() { return RecipeSerializers.crusher; }

        @Override
        public JsonObject getAdvancementJson() { return null; }

        @Override
        public ResourceLocation getAdvancementID() { return null; }
    }
}