package xfacthd.advtech.common.util.compat.jei.categories;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.item.ItemStack;
import xfacthd.advtech.client.gui.machine.ScreenAlloySmelter;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.recipes.AlloySmelterRecipe;
import xfacthd.advtech.common.util.compat.jei.ATRecipeCategory;

import java.util.*;

public class CategoryAlloySmelter extends ATRecipeCategory<AlloySmelterRecipe>
{
    public CategoryAlloySmelter(IGuiHelper guiHelper)
    {
        super(AlloySmelterRecipe.class, AlloySmelterRecipe.UID, guiHelper);
    }

    @Override
    public void setIngredients(AlloySmelterRecipe recipe, IIngredients ingredients)
    {
        List<List<ItemStack>> inputLists = new ArrayList<>();

        inputLists.add(Arrays.asList(recipe.getPrimaryInput().getMatchingStacks()));
        inputLists.add(Arrays.asList(recipe.getSecondaryInput().getMatchingStacks()));

        ingredients.setInputLists(VanillaTypes.ITEM, inputLists);

        List<ItemStack> outputs = new ArrayList<>();
        outputs.add(recipe.getRecipeOutput());
        if (recipe.hasSecondaryOutput()) { outputs.add(recipe.getSecondaryOutput()); }
        ingredients.setOutputs(VanillaTypes.ITEM, outputs);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, AlloySmelterRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup stackGroup = layout.getItemStacks();

        stackGroup.init(0, true, 23, 21);
        List<ItemStack> primInput = new ArrayList<>();
        for (ItemStack stack : recipe.getPrimaryInput().getMatchingStacks())
        {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.getPrimaryInputCount());
            primInput.add(copy);
        }
        stackGroup.set(0, primInput);

        List<ItemStack> secInput = new ArrayList<>();
        for (ItemStack stack : recipe.getSecondaryInput().getMatchingStacks())
        {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.getSecondaryInputCount());
            secInput.add(copy);
        }
        if (!secInput.isEmpty())
        {
            stackGroup.init(1, true, 43, 21);
            stackGroup.set(1, secInput);
        }

        stackGroup.init(2, false, 97, 21);
        stackGroup.set(2, recipe.getRecipeOutput());

        if (recipe.hasSecondaryOutput())
        {
            stackGroup.init(3, false, 97, 41);
            stackGroup.set(3, recipe.getSecondaryOutput());
        }
    }

    @Override
    public void draw(AlloySmelterRecipe recipe, double mouseX, double mouseY)
    {
        if (recipe.hasSecondaryOutput() && recipe.getSecondaryChance() < 1F)
        {
            int chance = (int)(recipe.getSecondaryChance() * 100D);
            Minecraft.getInstance().fontRenderer.drawStringWithShadow(String.format("%2d%%", chance), 76, 46, 0xFFFFFFFF);
        }

        drawEnergyBar(1, 1, recipe.getEnergy(), 20000);
    }

    @Override
    public List<String> getTooltipStrings(AlloySmelterRecipe recipe, double mouseX, double mouseY)
    {
        if (mouseX >= 1 && mouseX <= 13 && mouseY >= 1 && mouseY <= 61)
        {
            return Collections.singletonList(String.format("%d RF", recipe.getEnergy()));
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    protected IDrawable createBackground()
    {
        return guiHelper.createDrawable(ScreenAlloySmelter.BACKGROUND, 9, 23, 115, 62);
    }

    @Override
    protected IDrawable createIcon()
    {
        return guiHelper.createDrawableIngredient(new ItemStack(ATContent.blockAlloySmelter));
    }
}