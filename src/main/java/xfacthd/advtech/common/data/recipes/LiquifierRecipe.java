package xfacthd.advtech.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.util.Utils;

import java.util.*;

public class LiquifierRecipe extends MachineRecipe
{
    public static final RecipeType<LiquifierRecipe> TYPE = registerType("liquifier", new Type());
    public static final ResourceLocation UID = new ResourceLocation(AdvancedTechnology.MODID, "liquifier");
    public static List<LiquifierRecipe> RECIPES = Collections.emptyList();

    private final Ingredient input;
    private final FluidStack output;

    protected LiquifierRecipe(ResourceLocation name, int energy, Ingredient input, FluidStack output)
    {
        super(name, energy);
        this.input = input;
        this.output = output;
    }

    public Ingredient getInput() { return input; }

    public FluidStack getOutput() { return output; }

    @Override
    public ItemStack getResultItem() { return ItemStack.EMPTY; }

    @Override
    public RecipeSerializer<?> getSerializer() { return ATContent.RECIPE_LIQUIFIER.get(); }

    @Override
    public RecipeType<?> getType() { return TYPE; }

    public static final class Type implements RecipeType<LiquifierRecipe>
    {
        @Override
        public <C extends Container> Optional<LiquifierRecipe> tryMatch(Recipe<C> recipe, Level level, C inv)
        {
            if (recipe instanceof LiquifierRecipe liquifierRecipe)
            {
                if (liquifierRecipe.input.test(inv.getItem(0)))
                {
                    return Optional.of(liquifierRecipe);
                }
            }
            return Optional.empty();
        }

        @Override
        public String toString() { return "liquifier"; }
    }

    public static final class Serializer extends MachineRecipe.Serializer<LiquifierRecipe>
    {
        public Serializer() { }

        @Override
        public LiquifierRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            int energy = json.get("energy").getAsInt();

            Ingredient input = Ingredient.fromJson(json.get("input"));
            FluidStack output = Utils.readFluidStackFromJson(json.getAsJsonObject("output"));

            return new LiquifierRecipe(recipeId, energy, input, output);
        }

        @Override
        public LiquifierRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            int energy = buffer.readInt();
            Ingredient input = Ingredient.fromNetwork(buffer);
            FluidStack output = buffer.readFluidStack();
            return new LiquifierRecipe(recipeId, energy, input, output);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, LiquifierRecipe recipe)
        {
            buffer.writeInt(recipe.getEnergy());
            recipe.input.toNetwork(buffer);
            buffer.writeFluidStack(recipe.output);
        }
    }
}