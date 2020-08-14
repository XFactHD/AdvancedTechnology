package xfacthd.advtech.common.util.datagen.providers.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.RecipeProvider;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.util.data.TagHolder;

public abstract class ATRecipeProvider extends RecipeProvider
{
    public ATRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    public final String getName() { return AdvancedTechnology.MODID + "." + getProviderName(); }

    protected abstract String getProviderName();

    protected static Ingredient oreAsTag(Materials material)
    {
        return Ingredient.fromTag(TagHolder.ITEM_ORES.get(material));
    }

    protected static Ingredient powderAsTag(Materials material)
    {
        return Ingredient.fromTag(TagHolder.DUSTS.get(material));
    }

    protected static IItemProvider powderAsItem(Materials material) { return ATContent.itemPowder.get(material); }

    protected static IItemProvider ingotAsItem(Materials material)
    {
        if (material == Materials.IRON) { return Items.IRON_INGOT; }
        if (material == Materials.GOLD) { return Items.GOLD_INGOT; }
        return ATContent.itemIngot.get(material);
    }

    protected static ResourceLocation name(String name) { return new ResourceLocation(AdvancedTechnology.MODID, name); }
}