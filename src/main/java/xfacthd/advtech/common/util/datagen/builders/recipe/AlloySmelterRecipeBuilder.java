package xfacthd.advtech.common.util.datagen.builders.recipe;

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

public class AlloySmelterRecipeBuilder
{
    private final Item result;
    private final int count;
    private int energy;
    private Ingredient primaryInput;
    private int primaryInputCount;
    private Ingredient secondaryInput;
    private int secondaryInputCount;
    private Item secondaryResult = null;
    private int secondaryCount = 0;
    private double secondaryChance = 1;

    private AlloySmelterRecipeBuilder(Item result, int count)
    {
        Preconditions.checkState(count > 0, "Result count must be higher than 0!");
        this.result = result;
        this.count = count;
    }

    public static AlloySmelterRecipeBuilder alloySmelterRecipe(IItemProvider result) { return alloySmelterRecipe(result, 1); }

    public static AlloySmelterRecipeBuilder alloySmelterRecipe(IItemProvider result, int count)
    {
        return new AlloySmelterRecipeBuilder(result.asItem(), count);
    }

    public AlloySmelterRecipeBuilder energy(int energy)
    {
        Preconditions.checkState(energy > 0, "Energy must be higher than 0!");
        this.energy = energy;
        return this;
    }

    public AlloySmelterRecipeBuilder primaryInput(Tag<Item> item, int count) { return primaryInput(Ingredient.fromTag(item), count); }

    public AlloySmelterRecipeBuilder primaryInput(IItemProvider item, int count) { return primaryInput(Ingredient.fromItems(item), count); }

    public AlloySmelterRecipeBuilder primaryInput(Ingredient ingredient, int count)
    {
        Preconditions.checkState(primaryInput == null, "Primary input already set!");
        primaryInput = ingredient;
        primaryInputCount = count;
        return this;
    }

    public AlloySmelterRecipeBuilder secondaryInput(Tag<Item> item, int count) { return secondaryInput(Ingredient.fromTag(item), count); }

    public AlloySmelterRecipeBuilder secondaryInput(IItemProvider item, int count) { return secondaryInput(Ingredient.fromItems(item), count); }

    public AlloySmelterRecipeBuilder secondaryInput(Ingredient ingredient, int count)
    {
        Preconditions.checkState(secondaryInput == null, "Secondary input already set!");
        secondaryInput = ingredient;
        secondaryInputCount = count;
        return this;
    }

    public AlloySmelterRecipeBuilder secondaryOutput(IItemProvider item) { return secondaryOutput(item, 1); }

    public AlloySmelterRecipeBuilder secondaryOutput(IItemProvider item, int count)
    {
        Preconditions.checkState(secondaryResult == null, "Secondary output already set");
        Preconditions.checkState(count > 0, "Secondary count must be higher than 0!");
        secondaryResult = item.asItem();
        secondaryCount = count;
        return this;
    }

    public AlloySmelterRecipeBuilder secondaryChance(double chance)
    {
        secondaryChance = chance;
        return this;
    }

    public void build(Consumer<IFinishedRecipe> consumer, ResourceLocation id)
    {
        Preconditions.checkState(energy > 0, "No energy consumption set!");
        Preconditions.checkState(primaryInput != null, "No input set!");
        consumer.accept(new Result(id, this));
    }

    public static class Result implements IFinishedRecipe
    {
        private final ResourceLocation id;
        private final Item result;
        private final int count;
        private final int energy;
        private final Ingredient primaryInput;
        private final int primaryInputCount;
        private final Ingredient secondaryInput;
        private final int secondaryInputCount;
        private final Item secondaryResult;
        private final int secondaryCount;
        private final double secondaryChance;

        private Result(ResourceLocation id, AlloySmelterRecipeBuilder builder)
        {
            this.id = id;
            result = builder.result;
            count = builder.count;
            energy = builder.energy;
            primaryInput = builder.primaryInput;
            primaryInputCount = builder.primaryInputCount;
            secondaryInput = builder.secondaryInput;
            secondaryInputCount = builder.secondaryInputCount;
            secondaryResult = builder.secondaryResult;
            secondaryCount = builder.secondaryCount;
            secondaryChance = builder.secondaryChance;
        }

        @Override
        public void serialize(JsonObject json)
        {
            json.addProperty("energy", energy);
            json.add("primaryInput", primaryInput.serialize());
            json.addProperty("primaryInputCount", primaryInputCount);
            json.add("secondaryInput", secondaryInput.serialize());
            json.addProperty("secondaryInputCount", secondaryInputCount);

            JsonObject resultObj = new JsonObject();
            //noinspection ConstantConditions
            resultObj.addProperty("item", result.getRegistryName().toString());
            if (count > 1) { resultObj.addProperty("count", count); }
            json.add("primaryOutput", resultObj);

            if (secondaryResult != null)
            {
                JsonObject secondaryObj = new JsonObject();

                //noinspection ConstantConditions
                secondaryObj.addProperty("item", secondaryResult.getRegistryName().toString());
                if (secondaryCount > 1) { secondaryObj.addProperty("count", secondaryCount); }
                secondaryObj.addProperty("chance", secondaryChance);

                json.add("secondaryOutput", secondaryObj);
            }
        }

        @Override
        public ResourceLocation getID() { return id; }

        @Override
        public IRecipeSerializer<?> getSerializer() { return RecipeSerializers.alloySmelter; }

        @Override
        public JsonObject getAdvancementJson() { return null; }

        @Override
        public ResourceLocation getAdvancementID() { return null; }
    }
}