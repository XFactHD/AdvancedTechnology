package xfacthd.advtech.common.datagen.providers.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.datagen.builders.recipe.AlloySmelterRecipeBuilder;

import java.util.function.Consumer;

public class AlloySmelterRecipeProvider extends ATRecipeProvider
{
    public AlloySmelterRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        for (MaterialType material : MaterialType.values())
        {
            if (material.hasOre(true) && material.isMetal())
            {
                AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(material), 2)
                        .primaryInput(TagHolder.ITEM_ORES.get(material), 1)
                        .secondaryInput(Tags.Items.SAND, 1)
                        .energy(4000)
                        .build(consumer, name(material.getSerializedName() + "_ore_to_ingot_alloy"));
            }
        }

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.STEEL))
                .primaryInput(TagHolder.DUSTS.get(MaterialType.IRON), 1)
                .secondaryInput(TagHolder.MATERIALS.get(MaterialType.COKE), 1)
                .energy(2000)
                .build(consumer, name("iron_dust_coke_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.STEEL))
                .primaryInput(TagHolder.INGOTS.get(MaterialType.IRON), 1)
                .secondaryInput(TagHolder.MATERIALS.get(MaterialType.COKE), 1)
                .energy(2400)
                .build(consumer, name("iron_ingot_coke_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.STEEL))
                .primaryInput(TagHolder.DUSTS.get(MaterialType.IRON), 1)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.COAL), 4)
                .energy(4000)
                .build(consumer, name("iron_dust_coal_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.STEEL))
                .primaryInput(TagHolder.INGOTS.get(MaterialType.IRON), 1)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.COAL), 1)
                .energy(4400)
                .build(consumer, name("iron_ingot_coal_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.STEEL))
                .primaryInput(TagHolder.DUSTS.get(MaterialType.IRON), 1)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.CHARCOAL), 4)
                .energy(4000)
                .build(consumer, name("iron_dust_charcoal_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.STEEL))
                .primaryInput(TagHolder.INGOTS.get(MaterialType.IRON), 1)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.CHARCOAL), 1)
                .energy(4400)
                .build(consumer, name("iron_ingot_charcoal_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.ELECTRUM), 2)
                .primaryInput(TagHolder.INGOTS.get(MaterialType.SILVER), 1)
                .secondaryInput(TagHolder.INGOTS.get(MaterialType.GOLD), 1)
                .energy(2400)
                .build(consumer, name("gold_silver_ingot_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.ELECTRUM), 2)
                .primaryInput(TagHolder.DUSTS.get(MaterialType.SILVER), 1)
                .secondaryInput(TagHolder.INGOTS.get(MaterialType.GOLD), 1)
                .energy(2000)
                .build(consumer, name("gold_ingot_silver_dust_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.ELECTRUM), 2)
                .primaryInput(TagHolder.INGOTS.get(MaterialType.SILVER), 1)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.GOLD), 1)
                .energy(2000)
                .build(consumer, name("gold_dust_silver_ingot_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.ELECTRUM), 2)
                .primaryInput(TagHolder.DUSTS.get(MaterialType.SILVER), 1)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.GOLD), 1)
                .energy(1600)
                .build(consumer, name("gold_silver_dust_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.BRONZE), 4)
                .primaryInput(TagHolder.INGOTS.get(MaterialType.COPPER), 3)
                .secondaryInput(TagHolder.INGOTS.get(MaterialType.TIN), 1)
                .energy(4800)
                .build(consumer, name("copper_tin_ingot_to_bronze"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.BRONZE), 4)
                .primaryInput(TagHolder.DUSTS.get(MaterialType.COPPER), 3)
                .secondaryInput(TagHolder.INGOTS.get(MaterialType.TIN), 1)
                .energy(3600)
                .build(consumer, name("copper_ingot_tin_dust_to_bronze"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.BRONZE), 4)
                .primaryInput(TagHolder.INGOTS.get(MaterialType.COPPER), 3)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.TIN), 1)
                .energy(4400)
                .build(consumer, name("copper_dust_tin_ingot_to_bronze"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.BRONZE), 4)
                .primaryInput(TagHolder.DUSTS.get(MaterialType.COPPER), 3)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.TIN), 1)
                .energy(3200)
                .build(consumer, name("copper_tin_dust_to_bronze"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.CONSTANTAN), 2)
                .primaryInput(TagHolder.INGOTS.get(MaterialType.COPPER), 1)
                .secondaryInput(TagHolder.INGOTS.get(MaterialType.NICKEL), 1)
                .energy(2400)
                .build(consumer, name("copper_nickel_ingot_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.CONSTANTAN), 2)
                .primaryInput(TagHolder.DUSTS.get(MaterialType.COPPER), 1)
                .secondaryInput(TagHolder.INGOTS.get(MaterialType.NICKEL), 1)
                .energy(2000)
                .build(consumer, name("copper_ingot_nickel_dust_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.CONSTANTAN), 2)
                .primaryInput(TagHolder.INGOTS.get(MaterialType.COPPER), 1)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.NICKEL), 1)
                .energy(2000)
                .build(consumer, name("copper_dust_nickel_ingot_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(MaterialType.CONSTANTAN), 2)
                .primaryInput(TagHolder.DUSTS.get(MaterialType.COPPER), 1)
                .secondaryInput(TagHolder.DUSTS.get(MaterialType.NICKEL), 1)
                .energy(1600)
                .build(consumer, name("copper_nickel_dust_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(Blocks.NETHER_BRICKS, 2)
                .primaryInput(Tags.Items.NETHERRACK, 2)
                .secondaryInput(Blocks.SOUL_SAND, 1)
                .secondaryOutput(powderAsItem(MaterialType.SULFUR))
                .secondaryChance(.25F)
                .energy(4000)
                .build(consumer, name("nether_bricks"));
    }

    @Override
    protected String getRecipePrefix() { return "alloy_smelter"; }
}