package xfacthd.advtech.common.util.datagen.providers.recipe;

import net.minecraft.data.*;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.function.Consumer;

public class FurnaceRecipeProvider extends ATRecipeProvider
{
    public FurnaceRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    public String getProviderName() { return "furnace_recipes"; }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        for (Materials material : Materials.values())
        {
            if (material.isMetal())
            {
                if (material.hasOre())
                {
                    CookingRecipeBuilder.smeltingRecipe(
                            oreAsTag(material),
                            ingotAsItem(material),
                            1F,
                            200
                    ).addCriterion("has_ore_" + material.getName(), hasItem(TagHolder.ITEM_ORES.get(material)))
                     .build(consumer, name(material.getName() + "_ore_to_ingot"));
                }

                CookingRecipeBuilder.smeltingRecipe(
                        powderAsTag(material),
                        ingotAsItem(material),
                        1F,
                        200
                ).addCriterion("has_powder_" + material.getName(), hasItem(TagHolder.DUSTS.get(material)))
                 .build(consumer, name(material.getName() + "_powder_to_ingot"));
            }
        }
    }
}