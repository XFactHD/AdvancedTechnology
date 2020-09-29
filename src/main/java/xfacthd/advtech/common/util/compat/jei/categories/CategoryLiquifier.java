package xfacthd.advtech.common.util.compat.jei.categories;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiFluidStackGroup;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.client.gui.ScreenMachine;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.recipes.LiquifierRecipe;
import xfacthd.advtech.common.util.compat.jei.ATRecipeCategory;

import java.util.*;

public class CategoryLiquifier extends ATRecipeCategory<LiquifierRecipe>
{
    private static final ResourceLocation BACKGROUND = background("liquifier_jei");
    private final IDrawable tankOverlay;

    public CategoryLiquifier(IGuiHelper guiHelper)
    {
        super(LiquifierRecipe.class, LiquifierRecipe.UID, guiHelper);
        tankOverlay = guiHelper.drawableBuilder(ScreenMachine.TANK_MARKS, 0, 0, 16, 60).setTextureSize(16, 60).build();
    }

    @Override
    public void setIngredients(LiquifierRecipe recipe, IIngredients ingredients)
    {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getInput().getMatchingStacks()));
        ingredients.setOutput(VanillaTypes.FLUID, recipe.getOutput());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, LiquifierRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup stackGroup = layout.getItemStacks();

        stackGroup.init(0, true, 43, 21);
        stackGroup.set(0, Arrays.asList(recipe.getInput().getMatchingStacks()));

        IGuiFluidStackGroup fluidGroup = layout.getFluidStacks();

        fluidGroup.init(1, false, 98, 1, 16, 60, 5000, false, tankOverlay);
        fluidGroup.set(1, recipe.getOutput());
    }

    @Override
    public void draw(LiquifierRecipe recipe, double mouseX, double mouseY)
    {
        drawEnergyBar(1, 1, recipe.getEnergy(), 500000);
    }

    @Override
    public List<String> getTooltipStrings(LiquifierRecipe recipe, double mouseX, double mouseY)
    {
        if (mouseX >= 1 && mouseX <= 13 && mouseY >= 1 && mouseY <= 61)
        {
            return Collections.singletonList(String.format("%d RF", recipe.getEnergy()));
        }
        return super.getTooltipStrings(recipe, mouseX, mouseY);
    }

    @Override
    protected IDrawable createBackground() { return guiHelper.createDrawable(BACKGROUND, 0, 0, 115, 62); }

    @Override
    protected IDrawable createIcon()
    {
        return guiHelper.createDrawableIngredient(new ItemStack(ATContent.blockLiquifier));
    }
}