package xfacthd.advtech.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.common.crafting.CraftingHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.types.RecipeSerializers;

import java.util.*;

public class CrusherRecipe extends MachineRecipe
{
    public static final IRecipeType<CrusherRecipe> TYPE = registerType("crusher", new Type());
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
    public ItemStack getRecipeOutput() { return primaryOutput; }

    public ItemStack getSecondaryOutput() { return secondaryOutput; }

    public double getSecondaryChance() { return secondaryChance; }

    public boolean hasSecondaryOutput() { return !secondaryOutput.isEmpty(); }

    @Override
    public String getGroup() { return ATContent.blockCrusher.getTranslationKey(); }

    @Override
    public ItemStack getIcon() { return new ItemStack(ATContent.blockCrusher); }

    @Override
    public IRecipeSerializer<?> getSerializer() { return RecipeSerializers.crusher; }

    @Override
    public IRecipeType<?> getType() { return TYPE; }

    public static final class Type implements IRecipeType<CrusherRecipe>
    {
        @Override
        public <C extends IInventory> Optional<CrusherRecipe> matches(IRecipe<C> recipe, World world, C inv)
        {
            if (recipe instanceof CrusherRecipe)
            {
                CrusherRecipe crusherRecipe = (CrusherRecipe)recipe;
                if (crusherRecipe.input.test(inv.getStackInSlot(0)))
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
        public Serializer() { super("crusher"); }

        @Override
        public CrusherRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            int energy = json.get("energy").getAsInt();

            Ingredient input = Ingredient.deserialize(json.get("input"));
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
        public CrusherRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            int energy = buffer.readInt();
            Ingredient input = Ingredient.read(buffer);
            ItemStack primaryOutput = buffer.readItemStack();
            ItemStack secondaryOutput = buffer.readItemStack();
            double secondaryChance = buffer.readDouble();

            return new CrusherRecipe(recipeId, energy, input, primaryOutput, secondaryOutput, secondaryChance);
        }

        @Override
        public void write(PacketBuffer buffer, CrusherRecipe recipe)
        {
            buffer.writeInt(recipe.getEnergy());
            recipe.input.write(buffer);
            buffer.writeItemStack(recipe.primaryOutput);
            buffer.writeItemStack(recipe.secondaryOutput);
            buffer.writeDouble(recipe.secondaryChance);
        }
    }
}