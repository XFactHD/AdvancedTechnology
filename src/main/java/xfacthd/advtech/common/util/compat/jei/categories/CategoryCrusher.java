package xfacthd.advtech.common.util.compat.jei.categories;

import com.mojang.blaze3d.vertex.PoseStack;
import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.ingredient.IGuiItemStackGroup;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.ingredients.IIngredients;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.ItemStack;
import xfacthd.advtech.client.gui.machine.ScreenCrusher;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.recipes.CrusherRecipe;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.util.compat.jei.ATRecipeCategory;

import java.util.*;

public class CategoryCrusher extends ATRecipeCategory<CrusherRecipe>
{
    public CategoryCrusher(IGuiHelper guiHelper) { super(CrusherRecipe.class, CrusherRecipe.UID, guiHelper); }

    @Override
    public void setIngredients(CrusherRecipe recipe, IIngredients ingredients)
    {
        ingredients.setInputs(VanillaTypes.ITEM, Arrays.asList(recipe.getInput().getItems()));

        List<ItemStack> outputs = new ArrayList<>();
        outputs.add(recipe.getResultItem());
        if (recipe.hasSecondaryOutput()) { outputs.add(recipe.getSecondaryOutput()); }
        ingredients.setOutputs(VanillaTypes.ITEM, outputs);
    }

    @Override
    public void setRecipe(IRecipeLayout layout, CrusherRecipe recipe, IIngredients ingredients)
    {
        IGuiItemStackGroup stackGroup = layout.getItemStacks();

        stackGroup.init(0, true, 43, 21);
        stackGroup.set(0, Arrays.asList(recipe.getInput().getItems()));

        stackGroup.init(1, false, 97, 21);
        stackGroup.set(1, recipe.getResultItem());

        if (recipe.hasSecondaryOutput())
        {
            stackGroup.init(2, false, 97, 41);
            stackGroup.set(2, recipe.getSecondaryOutput());
        }
    }

    @Override
    public void draw(CrusherRecipe recipe, PoseStack poseStack, double mouseX, double mouseY)
    {
        if (recipe.hasSecondaryOutput() && recipe.getSecondaryChance() < 1F)
        {
            int chance = (int)(recipe.getSecondaryChance() * 100D);
            Minecraft.getInstance().font.draw(poseStack, String.format("%2d%%", chance), 76, 46, 0xFFFFFFFF);
        }

        drawEnergyBar(1, 1, recipe.getEnergy(), 20000);
    }

    @Override
    public List<Component> getTooltipStrings(CrusherRecipe recipe, double mouseX, double mouseY)
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
        return guiHelper.createDrawable(ScreenCrusher.BACKGROUND, 9, 23, 115, 62);
    }

    @Override
    protected IDrawable createIcon()
    {
        return guiHelper.createDrawableIngredient(new ItemStack(ATContent.machineBlock(MachineType.CRUSHER)));
    }
}