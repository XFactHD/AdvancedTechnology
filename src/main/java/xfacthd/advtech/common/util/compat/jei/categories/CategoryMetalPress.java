package xfacthd.advtech.common.util.compat.jei.categories;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.client.gui.machine.ScreenMetalPress;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.recipes.MetalPressRecipe;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.util.compat.jei.ATRecipeCategory;

import java.util.*;

public class CategoryMetalPress extends ATRecipeCategory<MetalPressRecipe>
{
    public CategoryMetalPress(IGuiHelper guiHelper) { super(MetalPressRecipe.class, MetalPressRecipe.UID, guiHelper); }

    @Override
    public void setIngredients(MetalPressRecipe recipe, IIngredients ingredients)
    {
        ingredients.setInputLists(VanillaTypes.ITEM, Arrays.asList(
                Arrays.asList(recipe.getInput().getItems()),
                Collections.singletonList(recipe.getMold())
        ));

        ingredients.setOutput(VanillaTypes.ITEM, recipe.getResultItem());
    }

    @Override
    public void setRecipe(IRecipeLayout layout, MetalPressRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup stackGroup = layout.getItemStacks();

        stackGroup.init(0, true, 23, 21);
        List<ItemStack> inputs = new ArrayList<>();
        for (ItemStack stack : recipe.getInput().getItems())
        {
            ItemStack copy = stack.copy();
            copy.setCount(recipe.getInputCount());
            inputs.add(copy);
        }
        stackGroup.set(0, inputs);

        stackGroup.init(1, false, 43, 21);
        stackGroup.set(1, recipe.getMold());

        stackGroup.init(2, false, 97, 21);
        stackGroup.set(2, recipe.getResultItem());
    }

    @Override
    public void draw(MetalPressRecipe recipe, PoseStack poseStack, double mouseX, double mouseY)
    {
        drawEnergyBar(1, 1, recipe.getEnergy(), 20000);
    }

    @Override
    public List<Component> getTooltipStrings(MetalPressRecipe recipe, double mouseX, double mouseY)
    {
        if (mouseX >= 1 && mouseX <= 13 && mouseY >= 1 && mouseY <= 61)
        {
            return Collections.singletonList(new TextComponent(String.format("%d RF", recipe.getEnergy())));
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
        return guiHelper.createDrawableIngredient(new ItemStack(ATContent.machineBlock(MachineType.METAL_PRESS)));
    }
}