package xfacthd.advtech.common.util.compat.jei.categories;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import xfacthd.advtech.client.gui.machine.ScreenMetalPress;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.recipes.MetalPressRecipe;
import xfacthd.advtech.common.util.compat.jei.ATRecipeCategory;

import java.util.*;

public class CategoryMetalPress extends ATRecipeCategory<MetalPressRecipe>
{
    public CategoryMetalPress(IGuiHelper guiHelper) { super(MetalPressRecipe.class, MetalPressRecipe.UID, guiHelper); }

    @Override
    public void setIngredients(MetalPressRecipe recipe, IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(
                Arrays.asList(recipe.getInput().getMatchingStacks()),
                Collections.singletonList(recipe.getMold())
        ));

        ingredients.setOutput(VanillaTypes.ITEM, recipe.getRecipeOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, MetalPressRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup stackGroup = layout.getItemStacks();

        stackGroup.init(0, true, 23, 21);
        List<ItemStack> inputs = new ArrayList<>();
        for (ItemStack stack : recipe.getInput().getMatchingStacks())
        {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.getInputCount());
            inputs.add(copy);
        }
        stackGroup.set(0, inputs);

        stackGroup.init(1, false, 43, 21);
        stackGroup.set(1, recipe.getMold());

        stackGroup.init(2, false, 97, 21);
        stackGroup.set(2, recipe.getRecipeOutput());
    }

    @Override
    public void draw(MetalPressRecipe recipe, double mouseX, double mouseY)
    {
        drawEnergyBar(1, 1, recipe.getEnergy(), 20000);
    }

    @Override
    public List<String> getTooltipStrings(MetalPressRecipe recipe, double mouseX, double mouseY)
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
        return guiHelper.createDrawable(ScreenMetalPress.BACKGROUND, 9, 23, 115, 62);
    }

    @Override
    protected IDrawable createIcon()
    {
        return guiHelper.createDrawableIngredient(new ItemStack(ATContent.blockMetalPress));
    }
}