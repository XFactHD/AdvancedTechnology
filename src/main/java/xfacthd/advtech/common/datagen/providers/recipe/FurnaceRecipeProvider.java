package xfacthd.advtech.common.datagen.providers.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.function.Consumer;

public class FurnaceRecipeProvider extends ATRecipeProvider
{
    public FurnaceRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        for (MaterialType material : MaterialType.values())
        {
            if (material.isMetal())
            {
                if (material.hasOre())
                {
                    SimpleCookingRecipeBuilder.smelting(
                            oreAsTag(material),
                            ingotAsItem(material),
                            1F,
                            200
                    ).unlockedBy("has_ore_" + material.getSerializedName(), has(TagHolder.ITEM_ORES.get(material)))
                     .save(consumer, name(material.getSerializedName() + "_ore_to_ingot"));
                }

                SimpleCookingRecipeBuilder.smelting(
                        powderAsTag(material),
                        ingotAsItem(material),
                        1F,
                        200
                ).unlockedBy("has_powder_" + material.getSerializedName(), has(TagHolder.DUSTS.get(material)))
                 .save(consumer, name(material.getSerializedName() + "_powder_to_ingot"));
            }
        }
    }

    @Override
    public String getRecipePrefix() { return "furnace"; }
}