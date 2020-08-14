package xfacthd.advtech.common.util.datagen.providers.recipe;

import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.tags.Tag;
import net.minecraftforge.common.Tags;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.*;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.function.Consumer;

public class TableRecipeProvider extends ATRecipeProvider
{
    public TableRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    public String getProviderName() { return "table_recipes"; }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        for (Materials material : Materials.values())
        {
            if (material.hasBlock())
            {
                Tag<Item> tag;
                if (material.isMetal()) { tag = TagHolder.INGOTS.get(material); }
                else if (material == Materials.SULFUR) { tag = TagHolder.DUSTS.get(material); }
                else { tag = TagHolder.MATERIALS.get(material); }

                ShapedRecipeBuilder.shapedRecipe(ATContent.blockStorage.get(material))
                        .patternLine("III")
                        .patternLine("III")
                        .patternLine("III")
                        .key('I', tag)
                        .addCriterion("has_ingot_" + material.getName(), hasItem(TagHolder.INGOTS.get(material)))
                        .build(consumer, name(material.getName() + "_ingot_to_block"));

                Item item;
                if (material.isMetal()) { item = ATContent.itemIngot.get(material); }
                else if (material == Materials.CHARCOAL) { item = Items.CHARCOAL; }
                else if (material == Materials.SULFUR) { item = ATContent.itemPowder.get(material); }
                else { item = ATContent.itemMaterial.get(material); }

                ShapelessRecipeBuilder.shapelessRecipe(item, 9)
                        .addIngredient(TagHolder.ITEM_STORAGE_BLOCKS.get(material))
                        .addCriterion("has_block_" + material.getName(), hasItem(TagHolder.ITEM_STORAGE_BLOCKS.get(material)))
                        .build(consumer, name(material.getName() + "_block_to_ingot"));
            }

            if (material.hasNugget())
            {
                ShapedRecipeBuilder.shapedRecipe(ATContent.itemIngot.get(material))
                        .patternLine("NNN")
                        .patternLine("NNN")
                        .patternLine("NNN")
                        .key('N', TagHolder.NUGGETS.get(material))
                        .addCriterion("has_nugget_" + material.getName(), hasItem(TagHolder.NUGGETS.get(material)))
                        .build(consumer, name(material.getName() + "_nugget_to_ingot"));

                ShapelessRecipeBuilder.shapelessRecipe(ATContent.itemNugget.get(material), 9)
                        .addIngredient(TagHolder.INGOTS.get(material))
                        .addCriterion("has_ingot_" + material.getName(), hasItem(TagHolder.INGOTS.get(material)))
                        .build(consumer, name(material.getName() + "_ingot_to_nugget"));
            }

            if (material.hasGear())
            {
                ShapedRecipeBuilder.shapedRecipe(ATContent.itemGear.get(material))
                        .patternLine(" M ")
                        .patternLine("MIM")
                        .patternLine(" M ")
                        .key('M', TagHolder.INGOTS.get(material))
                        .key('I', TagHolder.INGOTS.get(Materials.IRON))
                        .addCriterion("has_ingot_" + material.getName(), hasItem(TagHolder.INGOTS.get(material))
                        ).build(consumer, name(material.getName() + "_gear"));
            }

            if (material.hasPlate())
            {
                ShapedRecipeBuilder.shapedRecipe(ATContent.itemPlate.get(material))
                        .patternLine("II ")
                        .patternLine("II ")
                        .patternLine("   ")
                        .key('I', TagHolder.INGOTS.get(material))
                        .addCriterion("has_ingot_" + material.getName(), hasItem(TagHolder.INGOTS.get(material)))
                        .build(consumer, name(material.getName() + "_plate"));
            }
        }

        ShapedRecipeBuilder.shapedRecipe(ATContent.itemWrench)
                .patternLine("I I")
                .patternLine(" I ")
                .patternLine(" I ")
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .addCriterion("has_ingot_iron", hasItem(TagHolder.INGOTS.get(Materials.IRON)))
                .build(consumer, name("wrench"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.blockMachineCasing)
                .patternLine("IPI")
                .patternLine("PGP")
                .patternLine("IPI")
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .key('P', TagHolder.PLATES.get(Materials.TIN))
                .key('G', TagHolder.GEARS.get(Materials.COPPER))
                .addCriterion("has_gear_copper", hasItem(TagHolder.GEARS.get(Materials.COPPER)))
                .build(consumer, name("machine_casing"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.blockElectricFurnace)
                .patternLine("IRI")
                .patternLine("BMB")
                .patternLine("ICI")
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .key('R', Tags.Items.DUSTS_REDSTONE)
                .key('B', TagHolder.BRICKS)
                .key('M', ATContent.blockMachineCasing)
                .key('C', ATContent.itemComponent.get(Components.RECEPTION_COIL))
                .addCriterion("has_casing", hasItem(ATContent.blockMachineCasing))
                .build(consumer, name("electric_furnace"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.blockCrusher)
                .patternLine("IFI")
                .patternLine("EME")
                .patternLine("ICI")
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .key('F', Items.FLINT)
                .key('E', ATContent.itemComponent.get(Components.ELECTRIC_MOTOR))
                .key('M', ATContent.blockMachineCasing)
                .key('C', ATContent.itemComponent.get(Components.RECEPTION_COIL))
                .addCriterion("has_casing", hasItem(ATContent.blockMachineCasing))
                .build(consumer, name("crusher"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.itemComponent.get(Components.TRANSMISSION_COIL), 3)
                .patternLine("EEE")
                .patternLine("III")
                .patternLine("EEE")
                .key('E', TagHolder.INGOTS.get(Materials.ELECTRUM))
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .addCriterion("has_ingot_electrum", hasItem(TagHolder.INGOTS.get(Materials.ELECTRUM)))
                .build(consumer, name("transmission_coil"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.itemComponent.get(Components.RECEPTION_COIL), 3)
                .patternLine("CCC")
                .patternLine("III")
                .patternLine("CCC")
                .key('C', TagHolder.INGOTS.get(Materials.COPPER))
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .addCriterion("has_ingot_copper", hasItem(TagHolder.INGOTS.get(Materials.COPPER)))
                .build(consumer, name("reception_coil"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.itemComponent.get(Components.ELECTRIC_MOTOR))
                .patternLine("PCP")
                .patternLine("IBI")
                .patternLine("PCP")
                .key('P', TagHolder.PLATES.get(Materials.IRON))
                .key('C', ATContent.itemComponent.get(Components.RECEPTION_COIL))
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .key('B', TagHolder.ITEM_STORAGE_BLOCKS.get(Materials.IRON))
                .addCriterion("has_reception_coil", hasItem(ATContent.itemComponent.get(Components.RECEPTION_COIL)))
                .build(consumer, name("electric_motor"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.itemComponent.get(Components.ELECTRIC_GENERATOR))
                .patternLine("PCP")
                .patternLine("IBI")
                .patternLine("PCP")
                .key('P', TagHolder.PLATES.get(Materials.IRON))
                .key('C', ATContent.itemComponent.get(Components.TRANSMISSION_COIL))
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .key('B', TagHolder.ITEM_STORAGE_BLOCKS.get(Materials.IRON))
                .addCriterion("has_transmission_coil", hasItem(ATContent.itemComponent.get(Components.TRANSMISSION_COIL)))
                .build(consumer, name("electric_generator"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.itemPlateMold)
                .patternLine("III")
                .patternLine("IPI")
                .patternLine("III")
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .key('P', TagHolder.PLATES.get(Materials.IRON))
                .addCriterion("has_iron_plate", hasItem(ATContent.itemPlate.get(Materials.IRON)))
                .build(consumer, name("plate_mold"));

        ShapedRecipeBuilder.shapedRecipe(ATContent.itemGearMold)
                .patternLine("III")
                .patternLine("IGI")
                .patternLine("III")
                .key('I', TagHolder.INGOTS.get(Materials.IRON))
                .key('G', TagHolder.GEARS.get(Materials.IRON))
                .addCriterion("has_iron_gear", hasItem(ATContent.itemGear.get(Materials.IRON)))
                .build(consumer, name("gear_mold"));
    }
}