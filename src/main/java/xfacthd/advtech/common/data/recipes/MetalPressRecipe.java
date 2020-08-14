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

public class MetalPressRecipe extends MachineRecipe
{
    public static final IRecipeType<MetalPressRecipe> TYPE = registerType("metal_press", new MetalPressRecipe.Type());
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
    public ItemStack getRecipeOutput() { return output; }

    @Override
    public String getGroup() { return ATContent.blockMetalPress.getTranslationKey(); }

    @Override
    public ItemStack getIcon() { return new ItemStack(ATContent.blockMetalPress); }

    @Override
    public IRecipeSerializer<?> getSerializer() { return RecipeSerializers.metalPress; }

    @Override
    public IRecipeType<?> getType() { return TYPE; }

    public static final class Type implements IRecipeType<MetalPressRecipe>
    {
        @Override
        public <C extends IInventory> Optional<MetalPressRecipe> matches(IRecipe<C> recipe, World world, C inv)
        {
            if (recipe instanceof MetalPressRecipe)
            {
                MetalPressRecipe pressRecipe = (MetalPressRecipe)recipe;

                if (!pressRecipe.mold.isItemEqual(inv.getStackInSlot(1))) { return Optional.empty(); }

                if (pressRecipe.input.test(inv.getStackInSlot(0)))
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
        public Serializer() { super("metal_press"); }

        @Override
        public MetalPressRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            int energy = json.get("energy").getAsInt();

            Ingredient input = Ingredient.deserialize(json.get("input"));
            int inputCount = json.get("inputCount").getAsInt();
            ItemStack mold = CraftingHelper.getItemStack(json.getAsJsonObject("mold"), false);

            ItemStack output = CraftingHelper.getItemStack(json.getAsJsonObject("output"), false);

            return new MetalPressRecipe(recipeId, energy, input, inputCount, mold, output);
        }

        @Override
        public MetalPressRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            int energy = buffer.readInt();

            Ingredient input = Ingredient.read(buffer);
            int inputCount = buffer.readInt();
            ItemStack mold = buffer.readItemStack();

            ItemStack output = buffer.readItemStack();

            return new MetalPressRecipe(recipeId, energy, input, inputCount, mold, output);
        }

        @Override
        public void write(PacketBuffer buffer, MetalPressRecipe recipe)
        {
            buffer.writeInt(recipe.getEnergy());
            recipe.input.write(buffer);
            buffer.writeInt(recipe.inputCount);
            buffer.writeItemStack(recipe.mold);
            buffer.writeItemStack(recipe.output);
        }
    }
}