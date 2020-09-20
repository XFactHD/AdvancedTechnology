package xfacthd.advtech.common.util.datagen.providers.recipe;

import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.entity.passive.SheepEntity;
import net.minecraft.item.*;
import net.minecraftforge.common.Tags;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.util.datagen.builders.recipe.CrusherRecipeBuilder;

import java.util.function.Consumer;

public class CrusherRecipeProvider extends ATRecipeProvider
{
    public CrusherRecipeProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void registerRecipes(Consumer<IFinishedRecipe> consumer)
    {
        for (Materials material : Materials.values())
        {
            if (material.isMetal())
            {
                CrusherRecipeBuilder.crusherRecipe(powderAsItem(material))
                        .input(TagHolder.INGOTS.get(material))
                        .energy(2000)
                        .build(consumer, name(material.getName() + "_ingot_to_powder"));
            }
        }

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.IRON), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.IRON))
                .energy(4000)
                .secondary(powderAsItem(Materials.NICKEL))
                .chance(.1F)
                .build(consumer, name("iron_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.GOLD), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.GOLD))
                .energy(4000)
                //.secondary(powderAsItem(Materials.COPPER))
                //.chance(.1F)
                .build(consumer, name("gold_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.COPPER), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.COPPER))
                .energy(4000)
                .secondary(powderAsItem(Materials.GOLD))
                .chance(.1F)
                .build(consumer, name("copper_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.TIN), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.TIN))
                .energy(4000)
                .secondary(powderAsItem(Materials.IRON))
                .chance(.1F)
                .build(consumer, name("tin_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.SILVER), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.SILVER))
                .energy(4000)
                .secondary(powderAsItem(Materials.LEAD))
                .chance(.1F)
                .build(consumer, name("silver_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.LEAD), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.LEAD))
                .energy(4000)
                .secondary(powderAsItem(Materials.SILVER))
                .chance(.1F)
                .build(consumer, name("lead_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.ALUMINUM), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.ALUMINUM))
                .energy(4000)
                .secondary(powderAsItem(Materials.IRON))
                .chance(.1F)
                .build(consumer, name("aluminum_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.NICKEL), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.NICKEL))
                .energy(4000)
                .secondary(powderAsItem(Materials.PLATINUM))
                .chance(.1F)
                .build(consumer, name("nickel_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.PLATINUM), 2)
                .input(TagHolder.ITEM_ORES.get(Materials.PLATINUM))
                .energy(4000)
                //.secondary(powderAsItem(Materials.NICKEL))
                //.chance(.1F)
                .build(consumer, name("platinum_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.SULFUR), 12)
                .input(TagHolder.ITEM_ORES.get(Materials.SULFUR))
                .energy(4000)
                .build(consumer, name("sulfur_ore_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(Items.COAL, 3)
                .input(TagHolder.ITEM_ORES.get(Materials.COAL))
                .energy(4000)
                .secondary(powderAsItem(Materials.COAL))
                .chance(.25F)
                .build(consumer, name("coal_ore_to_item"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.COAL))
                .input(Items.COAL)
                .energy(2000)
                .secondary(powderAsItem(Materials.SULFUR))
                .chance(.15F)
                .build(consumer, name("coal_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.COAL))
                .input(Items.CHARCOAL)
                .energy(2000)
                .build(consumer, name("charcoal_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.COKE))
                .input(TagHolder.MATERIALS.get(Materials.COKE))
                .energy(2000)
                .build(consumer, name("coke_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(Items.BLAZE_POWDER, 4)
                .input(Items.BLAZE_ROD)
                .energy(3000)
                .secondary(powderAsItem(Materials.SULFUR))
                .chance(.5F)
                .build(consumer, name("blaze_rod_to_powder"));

        CrusherRecipeBuilder.crusherRecipe(Items.LAPIS_LAZULI, 10)
                .input(Blocks.LAPIS_ORE)
                .energy(4000)
                .secondary(powderAsItem(Materials.SULFUR))
                .chance(.2F)
                .build(consumer, name("lapis_ore_to_item"));

        CrusherRecipeBuilder.crusherRecipe(Items.QUARTZ, 3)
                .input(Blocks.NETHER_QUARTZ_ORE)
                .energy(4000)
                .secondary(powderAsItem(Materials.SULFUR))
                .chance(.15F)
                .build(consumer, name("quartz_ore_to_item"));

        CrusherRecipeBuilder.crusherRecipe(Blocks.COBBLESTONE)
                .input(Blocks.STONE)
                .energy(1000)
                .build(consumer, name("stone_to_cobble"));

        CrusherRecipeBuilder.crusherRecipe(Blocks.GRAVEL)
                .input(Blocks.COBBLESTONE)
                .energy(4000)
                .secondary(Blocks.SAND)
                .chance(.15F)
                .build(consumer, name("cobble_to_gravel"));

        CrusherRecipeBuilder.crusherRecipe(Blocks.GRAVEL)
                .input(Blocks.NETHERRACK)
                .energy(4000)
                .secondary(powderAsItem(Materials.SULFUR))
                .chance(.15F)
                .build(consumer, name("netherrack_to_gravel"));

        CrusherRecipeBuilder.crusherRecipe(Blocks.SAND)
                .input(Blocks.GRAVEL)
                .energy(4000)
                .secondary(Items.FLINT)
                .chance(.15F)
                .build(consumer, name("gravel_to_sand"));

        CrusherRecipeBuilder.crusherRecipe(Blocks.SAND, 2)
                .input(Blocks.SANDSTONE)
                .energy(3000)
                .build(consumer, name("sandstone_to_sand"));

        CrusherRecipeBuilder.crusherRecipe(Blocks.RED_SAND)
                .input(Blocks.RED_SANDSTONE)
                .energy(3000)
                .build(consumer, name("red_sandstone_to_red_sand"));

        CrusherRecipeBuilder.crusherRecipe(powderAsItem(Materials.OBSIDIAN), 4)
                .input(Blocks.OBSIDIAN)
                .energy(6000)
                .build(consumer, name("obsidian_to_dust"));

        CrusherRecipeBuilder.crusherRecipe(Items.BONE_MEAL, 6)
                .input(Tags.Items.BONES)
                .energy(4000)
                .build(consumer, name("bone_to_meal"));

        CrusherRecipeBuilder.crusherRecipe(Items.GLOWSTONE_DUST, 4)
                .input(Blocks.GLOWSTONE)
                .energy(4000)
                .build(consumer, name("glowstone_to_dust"));

        CrusherRecipeBuilder.crusherRecipe(Items.REDSTONE, 6)
                .input(Blocks.REDSTONE_ORE)
                .energy(4000)
                .build(consumer, name("redstone_ore_to_dust"));

        CrusherRecipeBuilder.crusherRecipe(Items.DIAMOND, 2)
                .input(Blocks.DIAMOND_ORE)
                .energy(4000)
                .build(consumer, name("diamond_ore_to_item"));

        CrusherRecipeBuilder.crusherRecipe(Items.EMERALD, 2)
                .input(Blocks.EMERALD_ORE)
                .energy(4000)
                .build(consumer, name("emerald_ore_to_item"));

        for (DyeColor color : DyeColor.values())
        {
            CrusherRecipeBuilder.crusherRecipe(Items.STRING)
                    .input(SheepEntity.WOOL_BY_COLOR.get(color))
                    .energy(3000)
                    .secondary(DyeItem.getItem(color))
                    .chance(.15F)
                    .build(consumer, name(color.getName() + "_wool_to_string"));
        }

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.YELLOW), 4)
                .input(Items.DANDELION)
                .energy(2000)
                .build(consumer, name("dandelion_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.RED), 4)
                .input(Items.POPPY)
                .energy(2000)
                .build(consumer, name("poppy_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.LIGHT_BLUE), 4)
                .input(Items.BLUE_ORCHID)
                .energy(2000)
                .build(consumer, name("blue_orchid_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.MAGENTA), 4)
                .input(Items.ALLIUM)
                .energy(2000)
                .build(consumer, name("allium_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.RED), 4)
                .input(Items.RED_TULIP)
                .energy(2000)
                .build(consumer, name("red_tulip_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.ORANGE), 4)
                .input(Items.ORANGE_TULIP)
                .energy(2000)
                .build(consumer, name("orange_tulip_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.LIGHT_GRAY), 4)
                .input(Items.WHITE_TULIP)
                .energy(2000)
                .build(consumer, name("white_tulip_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.PINK), 4)
                .input(Items.PINK_TULIP)
                .energy(2000)
                .build(consumer, name("pink_tulip_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.LIGHT_GRAY), 4)
                .input(Items.OXEYE_DAISY)
                .energy(2000)
                .build(consumer, name("oxeye_daisy_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.YELLOW), 4)
                .input(Items.SUNFLOWER)
                .energy(2000)
                .build(consumer, name("sunflower_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.MAGENTA), 4)
                .input(Items.LILAC)
                .energy(2000)
                .build(consumer, name("lilac_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.RED), 4)
                .input(Items.ROSE_BUSH)
                .energy(2000)
                .build(consumer, name("rose_bush_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(DyeItem.getItem(DyeColor.PINK), 4)
                .input(Items.PEONY)
                .energy(2000)
                .build(consumer, name("peony_to_dye"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.CLAY)
                .energy(3000)
                .build(consumer, name("clay_to_item"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.TERRACOTTA)
                .energy(4000)
                .build(consumer, name("terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.WHITE_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("white_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.ORANGE_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("orange_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.MAGENTA_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("magenta_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.LIGHT_BLUE_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("light_blue_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.YELLOW_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("yellow_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.LIME_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("lime_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.PINK_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("pink_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.GRAY_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("gray_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.LIGHT_GRAY_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("light_gray_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.CYAN_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("cyan_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.PURPLE_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("purple_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.BLUE_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("blue_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.BROWN_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("brown_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.GREEN_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("green_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.RED_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("red_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.CLAY_BALL, 4)
                .input(Blocks.BLACK_TERRACOTTA)
                .energy(4000)
                .build(consumer, name("black_terracotta_to_clay"));

        CrusherRecipeBuilder.crusherRecipe(Items.BRICK, 4)
                .input(Blocks.BRICKS)
                .energy(3000)
                .build(consumer, name("bricks_to_brick"));

        CrusherRecipeBuilder.crusherRecipe(Items.NETHER_BRICK, 4)
                .input(Blocks.NETHER_BRICKS)
                .energy(3000)
                .build(consumer, name("nether_bricks_to_nether_brick"));

        CrusherRecipeBuilder.crusherRecipe(Items.QUARTZ, 4)
                .input(Blocks.QUARTZ_BLOCK)
                .energy(3000)
                .build(consumer, name("quartz_block_to_quartz"));

        CrusherRecipeBuilder.crusherRecipe(Items.QUARTZ, 4)
                .input(Blocks.QUARTZ_PILLAR)
                .energy(3000)
                .build(consumer, name("quartz_pillar_to_quartz"));

        CrusherRecipeBuilder.crusherRecipe(Items.QUARTZ, 4)
                .input(Blocks.CHISELED_QUARTZ_BLOCK)
                .energy(3000)
                .build(consumer, name("chiseled_quartz_to_quartz"));
    }

    @Override
    public String getProviderName() { return "crusher_recipes"; }
}