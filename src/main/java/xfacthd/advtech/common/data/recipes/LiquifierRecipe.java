package xfacthd.advtech.common.data.recipes;

import com.google.gson.JsonObject;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.*;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.data.types.RecipeSerializers;
import xfacthd.advtech.common.util.Utils;

import java.util.*;

public class LiquifierRecipe extends MachineRecipe
{
    public static final IRecipeType<LiquifierRecipe> TYPE = registerType("liquifier", new Type());
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
    public ItemStack getRecipeOutput() { return ItemStack.EMPTY; }

    @Override
    public IRecipeSerializer<?> getSerializer() { return RecipeSerializers.liquifier; }

    @Override
    public IRecipeType<?> getType() { return TYPE; }

    public static final class Type implements IRecipeType<LiquifierRecipe>
    {
        @Override
        public <C extends IInventory> Optional<LiquifierRecipe> matches(IRecipe<C> recipe, World world, C inv)
        {
            if (recipe instanceof LiquifierRecipe)
            {
                LiquifierRecipe liquifierRecipe = (LiquifierRecipe)recipe;
                if (liquifierRecipe.input.test(inv.getStackInSlot(0)))
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
        public Serializer() { super("liquifier"); }

        @Override
        public LiquifierRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            int energy = json.get("energy").getAsInt();

            Ingredient input = Ingredient.deserialize(json.get("input"));
            FluidStack output = Utils.readFluidStackFromJson(json.getAsJsonObject("output"));

            return new LiquifierRecipe(recipeId, energy, input, output);
        }

        @Override
        public LiquifierRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            int energy = buffer.readInt();
            Ingredient input = Ingredient.read(buffer);
            FluidStack output = buffer.readFluidStack();
            return new LiquifierRecipe(recipeId, energy, input, output);
        }

        @Override
        public void write(PacketBuffer buffer, LiquifierRecipe recipe)
        {
            buffer.writeInt(recipe.getEnergy());
            recipe.input.write(buffer);
            buffer.writeFluidStack(recipe.output);
        }
    }
}