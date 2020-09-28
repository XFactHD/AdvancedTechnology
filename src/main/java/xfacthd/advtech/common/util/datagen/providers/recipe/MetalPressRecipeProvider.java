package xfacthd.advtech.common.util.datagen.providers.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.util.datagen.builders.recipe.MetalPressRecipeBuilder;

import java.util.function.Consumer;

public class MetalPressRecipeProvider extends ATRecipeProvider
{
    public MetalPressRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        final ItemStack plateMold = new ItemStack(ATContent.itemPlateMold);
        final ItemStack gearMold = new ItemStack(ATContent.itemGearMold);
        final ItemStack rodMold = new ItemStack(ATContent.itemRodMold);

        for (Materials material : Materials.values())
        {
            if (material.hasPlate())
            {
                MetalPressRecipeBuilder.pressRecipe(ATContent.itemPlate.get(material))
                        .energy(4000)
                        .input(TagHolder.INGOTS.get(material), 1)
                        .mold(plateMold)
                        .build(consumer, name(material.getName() + "_ingot_to_pressed_plate"));
            }

            if (material.hasGear())
            {
                MetalPressRecipeBuilder.pressRecipe(ATContent.itemGear.get(material))
                        .energy(4000)
                        .input(TagHolder.INGOTS.get(material), 4)
                        .mold(gearMold)
                        .build(consumer, name(material.getName() + "_ingot_to_pressed_gear"));
            }
        }

        MetalPressRecipeBuilder.pressRecipe(Items.BLAZE_ROD)
                .energy(4000)
                .input(Items.BLAZE_POWDER, 5)
                .mold(rodMold)
                .build(consumer, name("blaze_powder_to_pressed_rod"));
    }

    @Override
    protected String getRecipePrefix() { return "metal_press"; }
}