package xfacthd.advtech.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.common.crafting.CraftingHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MachineType;

import java.util.*;

public class MetalPressRecipe extends MachineRecipe
{
    public static final ResourceLocation UID = new ResourceLocation(AdvancedTechnology.MODID, "metal_press");
    public static List<MetalPressRecipe> RECIPES = Collections.emptyList();

    private final Ingredient input;
    private final int inputCount;
    private final ItemStack mold;
    private final ItemStack output;

    protected MetalPressRecipe(ResourceLocation name, int energy, Ingredient input, int inputCount, ItemStack mold, ItemStack output)
    {
        super(name, energy);
        this.input = input;
        this.inputCount = inputCount;
        this.mold = mold;
        this.output = output;
    }

    public boolean testInput(ItemStack stack)
    {
        if (input.test(stack))
        {
            return stack.getCount() >= inputCount;
        }
        return false;
    }

    public Ingredient getInput() { return input; }

    public int getInputCount() { return inputCount; }

    public ItemStack getMold() { return mold; }

    @Override
    public ItemStack getResultItem() { return output; }

    @Override
    public String getGroup() { return ATContent.machineBlock(MachineType.METAL_PRESS).getDescriptionId(); }

    @Override
    public ItemStack getToastSymbol() { return new ItemStack(ATContent.machineBlock(MachineType.METAL_PRESS)); }

    @Override
    public RecipeSerializer<?> getSerializer() { return ATContent.RECIPE_SERIALIZER_METAL_PRESS.get(); }

    @Override
    public RecipeType<?> getType() { return ATContent.RECIPE_TYPE_METAL_PRESS.get(); }

    public static final class Type implements RecipeType<MetalPressRecipe>
    {
        @Override
        public <C extends Container> Optional<MetalPressRecipe> tryMatch(Recipe<C> recipe, Level level, C inv)
        {
            if (recipe instanceof MetalPressRecipe pressRecipe)
            {
                if (!pressRecipe.mold.sameItemStackIgnoreDurability(inv.getItem(1))) { return Optional.empty(); }

                if (pressRecipe.input.test(inv.getItem(0)))
                {
                    return Optional.of(pressRecipe);
                }
            }
            return Optional.empty();
        }

        @Override
        public String toString() { return "metal_press"; }
    }

    public static final class Serializer extends MachineRecipe.Serializer<MetalPressRecipe>
    {
        public Serializer() { }

        @Override
        public MetalPressRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            int energy = json.get("energy").getAsInt();

            Ingredient input = Ingredient.fromJson(json.get("input"));
            int inputCount = json.get("inputCount").getAsInt();
            ItemStack mold = CraftingHelper.getItemStack(json.getAsJsonObject("mold"), false);

            ItemStack output = CraftingHelper.getItemStack(json.getAsJsonObject("output"), false);

            return new MetalPressRecipe(recipeId, energy, input, inputCount, mold, output);
        }

        @Override
        public MetalPressRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            int energy = buffer.readInt();

            Ingredient input = Ingredient.fromNetwork(buffer);
            int inputCount = buffer.readInt();
            ItemStack mold = buffer.readItem();

            ItemStack output = buffer.readItem();

            return new MetalPressRecipe(recipeId, energy, input, inputCount, mold, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, MetalPressRecipe recipe)
        {
            buffer.writeInt(recipe.getEnergy());
            recipe.input.toNetwork(buffer);
            buffer.writeInt(recipe.inputCount);
            buffer.writeItemStack(recipe.mold, true);
            buffer.writeItemStack(recipe.output, true);
        }
    }
}