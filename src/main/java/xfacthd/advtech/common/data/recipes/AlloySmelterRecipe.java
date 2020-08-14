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

public class AlloySmelterRecipe extends MachineRecipe
{
    public static final IRecipeType<AlloySmelterRecipe> TYPE = registerType("alloy_smelter", new Type());
    public static final ResourceLocation UID = new ResourceLocation(AdvancedTechnology.MODID, "alloy_smelter");
    public static List<AlloySmelterRecipe> RECIPES = Collections.emptyList();

    private final Ingredient primaryInput;
    private final int primaryInputCount;
    private final Ingredient secondaryInput;
    private final int secondaryInputCount;
    private final ItemStack primaryOutput;
    private final ItemStack secondaryOutput;
    private final double secondaryChance;

    protected AlloySmelterRecipe(ResourceLocation name, int energy,
                                 Ingredient primaryInput, int primaryInputCount,
                                 Ingredient secondaryInput, int secondaryInputCount,
                                 ItemStack primaryOutput, ItemStack secondaryOutput, double secondaryChance)
    {
        super(name, energy);
        this.primaryInput = primaryInput;
        this.primaryInputCount = primaryInputCount;
        this.secondaryInput = secondaryInput;
        this.secondaryInputCount = secondaryInputCount;
        this.primaryOutput = primaryOutput;
        this.secondaryOutput = secondaryOutput;
        this.secondaryChance = secondaryChance;
    }

    public boolean testInputs(ItemStack primStack, ItemStack secStack, boolean checkCount)
    {
        if (primaryInput.test(primStack) && secondaryInput.test(secStack))
        {
            return !checkCount || (primStack.getCount() >= primaryInputCount && secStack.getCount() >= secondaryInputCount);
        }
        else if (primaryInput.test(secStack) && secondaryInput.test(primStack))
        {
            return !checkCount || (secStack.getCount() >= primaryInputCount && primStack.getCount() >= secondaryInputCount);
        }
        return false;
    }

    public boolean testInput(ItemStack stack, boolean checkCount)
    {
        if (primaryInput.test(stack))
        {
            return !checkCount || stack.getCount() >= primaryInputCount;
        }
        if (secondaryInput.test(stack))
        {
            return !checkCount || stack.getCount() >= secondaryInputCount;
        }
        return false;
    }

    public Ingredient getPrimaryInput() { return primaryInput; }

    public Ingredient getSecondaryInput() { return secondaryInput; }

    public int getPrimaryInputCount() { return primaryInputCount; }

    public int getSecondaryInputCount() { return secondaryInputCount; }

    public int getInputCount(ItemStack stack)
    {
        if (primaryInput.test(stack)) { return primaryInputCount; }
        if (secondaryInput.test(stack)) { return secondaryInputCount; }
        return 0;
    }

    @Override
    public ItemStack getRecipeOutput() { return primaryOutput; }

    public ItemStack getSecondaryOutput() { return secondaryOutput; }

    public double getSecondaryChance() { return secondaryChance; }

    public boolean hasSecondaryOutput() { return !secondaryOutput.isEmpty(); }

    @Override
    public String getGroup() { return ATContent.blockAlloySmelter.getTranslationKey(); }

    @Override
    public ItemStack getIcon() { return new ItemStack(ATContent.blockAlloySmelter); }

    @Override
    public IRecipeSerializer<?> getSerializer() { return RecipeSerializers.alloySmelter; }

    @Override
    public IRecipeType<?> getType() { return TYPE; }

    public static final class Type implements IRecipeType<AlloySmelterRecipe>
    {
        @Override
        public <C extends IInventory> Optional<AlloySmelterRecipe> matches(IRecipe<C> recipe, World world, C inv)
        {
            if (recipe instanceof AlloySmelterRecipe)
            {
                AlloySmelterRecipe alloyRecipe = (AlloySmelterRecipe)recipe;

                ItemStack primStack = inv.getStackInSlot(0);
                ItemStack secStack = inv.getStackInSlot(1);

                boolean match = false;
                if (!primStack.isEmpty() && !secStack.isEmpty())
                {
                    match = alloyRecipe.testInputs(primStack, secStack, false);
                }
                else if (!primStack.isEmpty())
                {
                    match = alloyRecipe.testInput(primStack, false);
                }
                else if (!secStack.isEmpty())
                {
                    match = alloyRecipe.testInput(secStack, false);
                }

                if (match) { return Optional.of(alloyRecipe); }
            }
            return Optional.empty();
        }

        @Override
        public String toString() { return "alloy_smelter"; }
    }

    public static final class Serializer extends MachineRecipe.Serializer<AlloySmelterRecipe>
    {
        public Serializer() { super("alloy_smelter"); }

        @Override
        public AlloySmelterRecipe read(ResourceLocation recipeId, JsonObject json)
        {
            int energy = json.get("energy").getAsInt();

            Ingredient primInput = Ingredient.deserialize(json.get("primaryInput"));
            int primInputCount = json.get("primaryInputCount").getAsInt();

            Ingredient secInput = Ingredient.EMPTY;
            int secInputCount = 0;
            if (json.has("secondaryInput"))
            {
                secInput = Ingredient.deserialize(json.get("secondaryInput"));
                secInputCount = json.get("secondaryInputCount").getAsInt();
            }

            ItemStack primOutput = CraftingHelper.getItemStack(json.getAsJsonObject("primaryOutput"), false);
            ItemStack secOutput = ItemStack.EMPTY;
            double secChance = 0;
            if (json.has("secondaryOutput"))
            {
                JsonObject secondary = json.getAsJsonObject("secondaryOutput");
                secOutput = CraftingHelper.getItemStack(secondary, false);
                secChance = secondary.get("chance").getAsDouble();
            }

            return new AlloySmelterRecipe(recipeId, energy, primInput, primInputCount, secInput, secInputCount, primOutput, secOutput, secChance);
        }

        @Override
        public AlloySmelterRecipe read(ResourceLocation recipeId, PacketBuffer buffer)
        {
            int energy = buffer.readInt();
            Ingredient primInput = Ingredient.read(buffer);
            int primInputCount = buffer.readInt();
            Ingredient secInput = Ingredient.read(buffer);
            int secInputCount = buffer.readInt();
            ItemStack primOutput = buffer.readItemStack();
            ItemStack secOutput = buffer.readItemStack();
            double secondaryChance = buffer.readDouble();

            return new AlloySmelterRecipe(recipeId, energy, primInput, primInputCount, secInput, secInputCount, primOutput, secOutput, secondaryChance);
        }

        @Override
        public void write(PacketBuffer buffer, AlloySmelterRecipe recipe)
        {
            buffer.writeInt(recipe.getEnergy());
            recipe.primaryInput.write(buffer);
            buffer.writeInt(recipe.primaryInputCount);
            recipe.secondaryInput.write(buffer);
            buffer.writeInt(recipe.secondaryInputCount);
            buffer.writeItemStack(recipe.primaryOutput);
            buffer.writeItemStack(recipe.secondaryOutput);
            buffer.writeDouble(recipe.secondaryChance);
        }
    }
}