package xfacthd.advtech.common.datagen.providers.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.datagen.builders.recipe.MetalPressRecipeBuilder;

import java.util.function.Consumer;

public class MetalPressRecipeProvider extends ATRecipeProvider
{
    public MetalPressRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        final ItemStack plateMold = new ItemStack(ATContent.ITEM_PLATE_MOLD.get());
        final ItemStack gearMold = new ItemStack(ATContent.ITEM_GEAR_MOLD.get());
        final ItemStack rodMold = new ItemStack(ATContent.ITEM_ROD_MOLD.get());

        for (MaterialType material : MaterialType.values())
        {
            if (material.hasPlate())
            {
                MetalPressRecipeBuilder.pressRecipe(ATContent.PLATE_ITEMS.get(material).get())
                        .energy(4000)
                        .input(TagHolder.INGOTS.get(material), 1)
                        .mold(plateMold)
                        .build(consumer, name(material.getSerializedName() + "_ingot_to_pressed_plate"));
            }

            if (material.hasGear())
            {
                MetalPressRecipeBuilder.pressRecipe(ATContent.GEAR_ITEMS.get(material).get())
                        .energy(4000)
                        .input(TagHolder.INGOTS.get(material), 4)
                        .mold(gearMold)
                        .build(consumer, name(material.getSerializedName() + "_ingot_to_pressed_gear"));
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