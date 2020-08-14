package xfacthd.advtech.common.util.datagen.providers.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.IFinishedRecipe;
import net.minecraftforge.common.Tags;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.util.datagen.builders.recipe.AlloySmelterRecipeBuilder;

import java.util.function.Consumer;

public class AlloySmelterRecipeProvider extends ATRecipeProvider
{
    public AlloySmelterRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        for (Materials material : Materials.values())
        {
            if ((material.hasOre() && material.isMetal()) || material == Materials.IRON || material == Materials.GOLD)
            {
                AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(material), 2)
                        .primaryInput(TagHolder.ITEM_ORES.get(material), 1)
                        .secondaryInput(Tags.Items.SAND, 1)
                        .energy(4000)
                        .build(consumer, name(material.getName() + "_ore_to_ingot_alloy"));
            }
        }

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.STEEL))
                .primaryInput(TagHolder.DUSTS.get(Materials.IRON), 1)
                .secondaryInput(TagHolder.MATERIALS.get(Materials.COKE), 1)
                .energy(2000)
                .build(consumer, name("iron_dust_coke_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.STEEL))
                .primaryInput(TagHolder.INGOTS.get(Materials.IRON), 1)
                .secondaryInput(TagHolder.MATERIALS.get(Materials.COKE), 1)
                .energy(2400)
                .build(consumer, name("iron_ingot_coke_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.STEEL))
                .primaryInput(TagHolder.DUSTS.get(Materials.IRON), 1)
                .secondaryInput(TagHolder.DUSTS.get(Materials.COAL), 4)
                .energy(4000)
                .build(consumer, name("iron_dust_coal_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.STEEL))
                .primaryInput(TagHolder.INGOTS.get(Materials.IRON), 1)
                .secondaryInput(TagHolder.DUSTS.get(Materials.COAL), 1)
                .energy(4400)
                .build(consumer, name("iron_ingot_coal_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.STEEL))
                .primaryInput(TagHolder.DUSTS.get(Materials.IRON), 1)
                .secondaryInput(TagHolder.DUSTS.get(Materials.CHARCOAL), 4)
                .energy(4000)
                .build(consumer, name("iron_dust_charcoal_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.STEEL))
                .primaryInput(TagHolder.INGOTS.get(Materials.IRON), 1)
                .secondaryInput(TagHolder.DUSTS.get(Materials.CHARCOAL), 1)
                .energy(4400)
                .build(consumer, name("iron_ingot_charcoal_to_steel"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.ELECTRUM), 2)
                .primaryInput(TagHolder.INGOTS.get(Materials.SILVER), 1)
                .secondaryInput(TagHolder.INGOTS.get(Materials.GOLD), 1)
                .energy(2400)
                .build(consumer, name("gold_silver_ingot_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.ELECTRUM), 2)
                .primaryInput(TagHolder.DUSTS.get(Materials.SILVER), 1)
                .secondaryInput(TagHolder.INGOTS.get(Materials.GOLD), 1)
                .energy(2000)
                .build(consumer, name("gold_ingot_silver_dust_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.ELECTRUM), 2)
                .primaryInput(TagHolder.INGOTS.get(Materials.SILVER), 1)
                .secondaryInput(TagHolder.DUSTS.get(Materials.GOLD), 1)
                .energy(2000)
                .build(consumer, name("gold_dust_silver_ingot_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.ELECTRUM), 2)
                .primaryInput(TagHolder.DUSTS.get(Materials.SILVER), 1)
                .secondaryInput(TagHolder.DUSTS.get(Materials.GOLD), 1)
                .energy(1600)
                .build(consumer, name("gold_silver_dust_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.BRONZE), 4)
                .primaryInput(TagHolder.INGOTS.get(Materials.COPPER), 3)
                .secondaryInput(TagHolder.INGOTS.get(Materials.TIN), 1)
                .energy(4800)
                .build(consumer, name("copper_tin_ingot_to_bronze"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.BRONZE), 4)
                .primaryInput(TagHolder.DUSTS.get(Materials.COPPER), 3)
                .secondaryInput(TagHolder.INGOTS.get(Materials.TIN), 1)
                .energy(3600)
                .build(consumer, name("copper_ingot_tin_dust_to_bronze"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.BRONZE), 4)
                .primaryInput(TagHolder.INGOTS.get(Materials.COPPER), 3)
                .secondaryInput(TagHolder.DUSTS.get(Materials.TIN), 1)
                .energy(4400)
                .build(consumer, name("copper_dust_tin_ingot_to_bronze"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.BRONZE), 4)
                .primaryInput(TagHolder.DUSTS.get(Materials.COPPER), 3)
                .secondaryInput(TagHolder.DUSTS.get(Materials.TIN), 1)
                .energy(3200)
                .build(consumer, name("copper_tin_dust_to_bronze"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.CONSTANTAN), 2)
                .primaryInput(TagHolder.INGOTS.get(Materials.COPPER), 1)
                .secondaryInput(TagHolder.INGOTS.get(Materials.NICKEL), 1)
                .energy(2400)
                .build(consumer, name("copper_nickel_ingot_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.CONSTANTAN), 2)
                .primaryInput(TagHolder.DUSTS.get(Materials.COPPER), 1)
                .secondaryInput(TagHolder.INGOTS.get(Materials.NICKEL), 1)
                .energy(2000)
                .build(consumer, name("copper_ingot_nickel_dust_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.CONSTANTAN), 2)
                .primaryInput(TagHolder.INGOTS.get(Materials.COPPER), 1)
                .secondaryInput(TagHolder.DUSTS.get(Materials.NICKEL), 1)
                .energy(2000)
                .build(consumer, name("copper_dust_nickel_ingot_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(ingotAsItem(Materials.CONSTANTAN), 2)
                .primaryInput(TagHolder.DUSTS.get(Materials.COPPER), 1)
                .secondaryInput(TagHolder.DUSTS.get(Materials.NICKEL), 1)
                .energy(1600)
                .build(consumer, name("copper_nickel_dust_to_electrum"));

        AlloySmelterRecipeBuilder.alloySmelterRecipe(Blocks.NETHER_BRICKS, 2)
                .primaryInput(Tags.Items.NETHERRACK, 2)
                .secondaryInput(Blocks.SOUL_SAND, 1)
                .secondaryOutput(powderAsItem(Materials.SULFUR))
                .secondaryChance(.25F)
                .energy(4000)
                .build(consumer, name("nether_bricks"));
    }

    @Override
    protected String getProviderName() { return "alloy_smelter_recipes"; }
}