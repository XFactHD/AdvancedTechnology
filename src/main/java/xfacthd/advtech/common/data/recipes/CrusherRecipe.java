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

public class CrusherRecipe extends MachineRecipe
{
    public static final RecipeType<CrusherRecipe> TYPE = registerType("crusher", new Type());
    public static final ResourceLocation UID = new ResourceLocation(AdvancedTechnology.MODID, "crusher");
    public static List<CrusherRecipe> RECIPES = Collections.emptyList();

    private final Ingredient input;
    private final ItemStack primaryOutput;
    private final ItemStack secondaryOutput;
    private final double secondaryChance;

    protected CrusherRecipe(ResourceLocation name, int energy, Ingredient input, ItemStack primaryOutput, ItemStack secondaryOutput, double secondaryChance)
    {
        super(name, energy);
        this.input = input;
        this.primaryOutput = primaryOutput;
        this.secondaryOutput = secondaryOutput;
        this.secondaryChance = secondaryChance;
    }

    public Ingredient getInput() { return input; }

    @Override
    public ItemStack getResultItem() { return primaryOutput; }

    public ItemStack getSecondaryOutput() { return secondaryOutput; }

    public double getSecondaryChance() { return secondaryChance; }

    public boolean hasSecondaryOutput() { return !secondaryOutput.isEmpty(); }

    @Override
    public String getGroup() { return ATContent.machineBlock(MachineType.CRUSHER).getDescriptionId(); }

    @Override
    public ItemStack getToastSymbol() { return new ItemStack(ATContent.machineBlock(MachineType.CRUSHER)); }

    @Override
    public RecipeSerializer<?> getSerializer() { return ATContent.RECIPE_CRUSHER.get(); }

    @Override
    public RecipeType<?> getType() { return TYPE; }

    public static final class Type implements RecipeType<CrusherRecipe>
    {
        @Override
        public <C extends Container> Optional<CrusherRecipe> tryMatch(Recipe<C> recipe, Level level, C inv)
        {
            if (recipe instanceof CrusherRecipe crusherRecipe)
            {
                if (crusherRecipe.input.test(inv.getItem(0)))
                {
                    return Optional.of(crusherRecipe);
                }
            }
            return Optional.empty();
        }

        @Override
        public String toString() { return "crusher"; }
    }

    public static final class Serializer extends MachineRecipe.Serializer<CrusherRecipe>
    {
        public Serializer() { }

        @Override
        public CrusherRecipe fromJson(ResourceLocation recipeId, JsonObject json)
        {
            int energy = json.get("energy").getAsInt();

            Ingredient input = Ingredient.fromJson(json.get("input"));
            ItemStack primaryOutput = CraftingHelper.getItemStack(json.getAsJsonObject("primary"), false);

            ItemStack secondaryOutput = ItemStack.EMPTY;
            double secondaryChance = 0;
            if (json.has("secondary"))
            {
                JsonObject secondary = json.getAsJsonObject("secondary");
                secondaryOutput = CraftingHelper.getItemStack(secondary, false);
                secondaryChance = secondary.get("chance").getAsDouble();
            }

            return new CrusherRecipe(recipeId, energy, input, primaryOutput, secondaryOutput, secondaryChance);
        }

        @Override
        public CrusherRecipe fromNetwork(ResourceLocation recipeId, FriendlyByteBuf buffer)
        {
            int energy = buffer.readInt();
            Ingredient input = Ingredient.fromNetwork(buffer);
            ItemStack primaryOutput = buffer.readItem();
            ItemStack secondaryOutput = buffer.readItem();
            double secondaryChance = buffer.readDouble();

            return new CrusherRecipe(recipeId, energy, input, primaryOutput, secondaryOutput, secondaryChance);
        }

        @Override
        public void toNetwork(FriendlyByteBuf buffer, CrusherRecipe recipe)
        {
            buffer.writeInt(recipe.getEnergy());
            recipe.input.toNetwork(buffer);
            buffer.writeItemStack(recipe.primaryOutput, true);
            buffer.writeItemStack(recipe.secondaryOutput, true);
            buffer.writeDouble(recipe.secondaryChance);
        }
    }
}