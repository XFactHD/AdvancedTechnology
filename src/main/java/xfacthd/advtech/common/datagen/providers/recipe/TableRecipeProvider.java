package xfacthd.advtech.common.datagen.providers.recipe;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.recipes.*;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.Tags;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.*;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.function.Consumer;

public class TableRecipeProvider extends ATRecipeProvider
{
    public TableRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void buildCraftingRecipes(Consumer<FinishedRecipe> consumer)
    {
        for (MaterialType material : MaterialType.values())
        {
            if (material.hasBlock())
            {
                TagKey<Item> tag;
                if (material.isMetal()) { tag = TagHolder.INGOTS.get(material); }
                else if (material == MaterialType.SULFUR) { tag = TagHolder.DUSTS.get(material); }
                else { tag = TagHolder.MATERIALS.get(material); }

                ShapedRecipeBuilder.shaped(ATContent.STORAGE_BLOCKS.get(material).get())
                        .pattern("III")
                        .pattern("III")
                        .pattern("III")
                        .define('I', tag)
                        .unlockedBy("has_ingot_" + material.getSerializedName(), has(TagHolder.INGOTS.get(material)))
                        .save(consumer, name(material.getSerializedName() + "_ingot_to_block"));

                Item item;
                if (material.isMetal()) { item = ATContent.INGOT_ITEMS.get(material).get(); }
                else if (material == MaterialType.CHARCOAL) { item = Items.CHARCOAL; }
                else if (material == MaterialType.SULFUR) { item = ATContent.POWDER_ITEMS.get(material).get(); }
                else { item = ATContent.MATERIAL_ITEMS.get(material).get(); }

                ShapelessRecipeBuilder.shapeless(item, 9)
                        .requires(TagHolder.ITEM_STORAGE_BLOCKS.get(material))
                        .unlockedBy("has_block_" + material.getSerializedName(), has(TagHolder.ITEM_STORAGE_BLOCKS.get(material)))
                        .save(consumer, name(material.getSerializedName() + "_block_to_ingot"));
            }

            if (material.hasNugget())
            {
                Item item = material == MaterialType.COPPER ? Items.COPPER_INGOT : ATContent.INGOT_ITEMS.get(material).get();
                ShapedRecipeBuilder.shaped(item)
                        .pattern("NNN")
                        .pattern("NNN")
                        .pattern("NNN")
                        .define('N', TagHolder.NUGGETS.get(material))
                        .unlockedBy("has_nugget_" + material.getSerializedName(), has(TagHolder.NUGGETS.get(material)))
                        .save(consumer, name(material.getSerializedName() + "_nugget_to_ingot"));

                ShapelessRecipeBuilder.shapeless(ATContent.NUGGET_ITEMS.get(material).get(), 9)
                        .requires(TagHolder.INGOTS.get(material))
                        .unlockedBy("has_ingot_" + material.getSerializedName(), has(TagHolder.INGOTS.get(material)))
                        .save(consumer, name(material.getSerializedName() + "_ingot_to_nugget"));
            }

            if (material.hasGear())
            {
                ShapedRecipeBuilder.shaped(ATContent.GEAR_ITEMS.get(material).get())
                        .pattern(" M ")
                        .pattern("MIM")
                        .pattern(" M ")
                        .define('M', TagHolder.INGOTS.get(material))
                        .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                        .unlockedBy("has_ingot_" + material.getSerializedName(), has(TagHolder.INGOTS.get(material))
                        ).save(consumer, name(material.getSerializedName() + "_gear"));
            }

            if (material.hasPlate())
            {
                ShapedRecipeBuilder.shaped(ATContent.PLATE_ITEMS.get(material).get())
                        .pattern("II ")
                        .pattern("II ")
                        .pattern("   ")
                        .define('I', TagHolder.INGOTS.get(material))
                        .unlockedBy("has_ingot_" + material.getSerializedName(), has(TagHolder.INGOTS.get(material)))
                        .save(consumer, name(material.getSerializedName() + "_plate"));
            }
        }

        ShapedRecipeBuilder.shaped(ATContent.ITEM_WRENCH.get())
                .pattern("I I")
                .pattern(" I ")
                .pattern(" I ")
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .unlockedBy("has_ingot_iron", has(TagHolder.INGOTS.get(MaterialType.IRON)))
                .save(consumer, name("wrench"));



        ShapedRecipeBuilder.shaped(ATContent.machineBlock(MachineType.CASING))
                .pattern("IPI")
                .pattern("PGP")
                .pattern("IPI")
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .define('P', TagHolder.PLATES.get(MaterialType.TIN))
                .define('G', TagHolder.GEARS.get(MaterialType.COPPER))
                .unlockedBy("has_gear_copper", has(TagHolder.GEARS.get(MaterialType.COPPER)))
                .save(consumer, name("machine_casing"));



        ShapedRecipeBuilder.shaped(ATContent.machineBlock(MachineType.ELECTRIC_FURNACE))
                .pattern("IRI")
                .pattern("BMB")
                .pattern("ICI")
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .define('R', Tags.Items.DUSTS_REDSTONE)
                .define('B', TagHolder.BRICKS)
                .define('M', ATContent.machineBlock(MachineType.CASING))
                .define('C', ATContent.COMPONENT_ITEMS.get(Component.RECEPTION_COIL).get())
                .unlockedBy("has_casing", has(ATContent.machineBlock(MachineType.CASING)))
                .save(consumer, name("electric_furnace"));

        ShapedRecipeBuilder.shaped(ATContent.machineBlock(MachineType.CRUSHER))
                .pattern("IFI")
                .pattern("EME")
                .pattern("ICI")
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .define('F', Items.FLINT)
                .define('E', ATContent.COMPONENT_ITEMS.get(Component.ELECTRIC_MOTOR).get())
                .define('M', ATContent.machineBlock(MachineType.CASING))
                .define('C', ATContent.COMPONENT_ITEMS.get(Component.RECEPTION_COIL).get())
                .unlockedBy("has_casing", has(ATContent.machineBlock(MachineType.CASING)))
                .save(consumer, name("crusher"));



        ShapedRecipeBuilder.shaped(ATContent.COMPONENT_ITEMS.get(Component.TRANSMISSION_COIL).get(), 3)
                .pattern("EEE")
                .pattern("III")
                .pattern("EEE")
                .define('E', TagHolder.INGOTS.get(MaterialType.ELECTRUM))
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .unlockedBy("has_ingot_electrum", has(TagHolder.INGOTS.get(MaterialType.ELECTRUM)))
                .save(consumer, name("transmission_coil"));

        ShapedRecipeBuilder.shaped(ATContent.COMPONENT_ITEMS.get(Component.RECEPTION_COIL).get(), 3)
                .pattern("CCC")
                .pattern("III")
                .pattern("CCC")
                .define('C', TagHolder.INGOTS.get(MaterialType.COPPER))
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .unlockedBy("has_ingot_copper", has(TagHolder.INGOTS.get(MaterialType.COPPER)))
                .save(consumer, name("reception_coil"));

        ShapedRecipeBuilder.shaped(ATContent.COMPONENT_ITEMS.get(Component.ELECTRIC_MOTOR).get())
                .pattern("PCP")
                .pattern("IBI")
                .pattern("PCP")
                .define('P', TagHolder.PLATES.get(MaterialType.IRON))
                .define('C', ATContent.COMPONENT_ITEMS.get(Component.RECEPTION_COIL).get())
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .define('B', TagHolder.ITEM_STORAGE_BLOCKS.get(MaterialType.IRON))
                .unlockedBy("has_reception_coil", has(ATContent.COMPONENT_ITEMS.get(Component.RECEPTION_COIL).get()))
                .save(consumer, name("electric_motor"));

        ShapedRecipeBuilder.shaped(ATContent.COMPONENT_ITEMS.get(Component.ELECTRIC_GENERATOR).get())
                .pattern("PCP")
                .pattern("IBI")
                .pattern("PCP")
                .define('P', TagHolder.PLATES.get(MaterialType.IRON))
                .define('C', ATContent.COMPONENT_ITEMS.get(Component.TRANSMISSION_COIL).get())
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .define('B', TagHolder.ITEM_STORAGE_BLOCKS.get(MaterialType.IRON))
                .unlockedBy("has_transmission_coil", has(ATContent.COMPONENT_ITEMS.get(Component.TRANSMISSION_COIL).get()))
                .save(consumer, name("electric_generator"));

        ShapedRecipeBuilder.shaped(ATContent.ITEM_PLATE_MOLD.get())
                .pattern("III")
                .pattern("IPI")
                .pattern("III")
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .define('P', TagHolder.PLATES.get(MaterialType.IRON))
                .unlockedBy("has_iron_plate", has(ATContent.PLATE_ITEMS.get(MaterialType.IRON).get()))
                .save(consumer, name("plate_mold"));

        ShapedRecipeBuilder.shaped(ATContent.ITEM_GEAR_MOLD.get())
                .pattern("III")
                .pattern("IGI")
                .pattern("III")
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .define('G', TagHolder.GEARS.get(MaterialType.IRON))
                .unlockedBy("has_iron_gear", has(ATContent.GEAR_ITEMS.get(MaterialType.IRON).get()))
                .save(consumer, name("gear_mold"));

        ShapedRecipeBuilder.shaped(ATContent.ITEM_ROD_MOLD.get())
                .pattern("III")
                .pattern("IRI")
                .pattern("III")
                .define('I', TagHolder.INGOTS.get(MaterialType.IRON))
                .define('R', Items.STICK)
                .unlockedBy("has_stick", has(Items.STICK))
                .save(consumer, name("stick_mold"));
    }

    @Override
    public String getRecipePrefix() { return "table"; }
}