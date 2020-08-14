package xfacthd.advtech.common.world;

import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Materials;

public class OreGenerator
{
    private static final OreFeatureConfig COPPER_CONFIG =   oreConfig(Materials.COPPER,    8);
    private static final OreFeatureConfig TIN_CONFIG =      oreConfig(Materials.TIN,       8);
    private static final OreFeatureConfig SILVER_CONFIG =   oreConfig(Materials.SILVER,    8);
    private static final OreFeatureConfig LEAD_CONFIG =     oreConfig(Materials.LEAD,      6);
    private static final OreFeatureConfig ALUMINUM_CONFIG = oreConfig(Materials.ALUMINUM,  4);
    private static final OreFeatureConfig NICKEL_CONFIG =   oreConfig(Materials.NICKEL,    6);
    private static final OreFeatureConfig PLATINUM_CONFIG = oreConfig(Materials.PLATINUM,  4);
    private static final OreFeatureConfig SULFUR_CONFIG =   oreConfig(Materials.SULFUR,   12);

    private static final CountRangeConfig COPPER_RANGE =   oreRange( 8, 40, 72);
    private static final CountRangeConfig TIN_RANGE =      oreRange( 8, 40, 72);
    private static final CountRangeConfig SILVER_RANGE =   oreRange( 4,  8, 40);
    private static final CountRangeConfig LEAD_RANGE =     oreRange( 4,  8, 36);
    private static final CountRangeConfig ALUMINUM_RANGE = oreRange( 8, 40, 85);
    private static final CountRangeConfig NICKEL_RANGE =   oreRange( 4, 40, 72);
    private static final CountRangeConfig PLATINUM_RANGE = oreRange( 3,  0, 30);
    private static final CountRangeConfig SULFUR_RANGE =   oreRange(15, 40, 72);

    public static void addOreFeatures()
    {
        ForgeRegistries.BIOMES.getValues()
                .stream()
                .filter(biome -> biome.getCategory() != Biome.Category.NETHER)
                .filter(biome -> biome.getCategory() != Biome.Category.THEEND)
                .forEach(biome ->
        {
            addCopperOre(biome);
            addTinOre(biome);
            addSilverOre(biome);
            addLeadOre(biome);
            addAluminumOre(biome);
            addNickelOre(biome);
            addPlatinumOre(biome);
            addSulfurOre(biome);
        });
    }

    private static void addCopperOre(Biome biome)
    {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE
                        .withConfiguration(COPPER_CONFIG)
                        .withPlacement(Placement.COUNT_RANGE.configure(COPPER_RANGE))
        );
    }

    private static void addTinOre(Biome biome)
    {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE
                        .withConfiguration(TIN_CONFIG)
                        .withPlacement(Placement.COUNT_RANGE.configure(TIN_RANGE))
        );
    }

    private static void addSilverOre(Biome biome)
    {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE
                        .withConfiguration(SILVER_CONFIG)
                        .withPlacement(Placement.COUNT_RANGE.configure(SILVER_RANGE))
        );
    }

    private static void addLeadOre(Biome biome)
    {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE
                        .withConfiguration(LEAD_CONFIG)
                        .withPlacement(Placement.COUNT_RANGE.configure(LEAD_RANGE))
        );
    }

    private static void addAluminumOre(Biome biome)
    {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE
                        .withConfiguration(ALUMINUM_CONFIG)
                        .withPlacement(Placement.COUNT_RANGE.configure(ALUMINUM_RANGE))
        );
    }

    private static void addNickelOre(Biome biome)
    {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE
                        .withConfiguration(NICKEL_CONFIG)
                        .withPlacement(Placement.COUNT_RANGE.configure(NICKEL_RANGE))
        );
    }

    private static void addPlatinumOre(Biome biome)
    {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE
                        .withConfiguration(PLATINUM_CONFIG)
                        .withPlacement(Placement.COUNT_RANGE.configure(PLATINUM_RANGE))
        );
    }

    private static void addSulfurOre(Biome biome)
    {
        biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,
                Feature.ORE
                        .withConfiguration(SULFUR_CONFIG)
                        .withPlacement(Placement.COUNT_RANGE.configure(SULFUR_RANGE))
        );
    }

    private static OreFeatureConfig oreConfig(Materials material, int veinSize)
    {
        return new OreFeatureConfig(
                OreFeatureConfig.FillerBlockType.NATURAL_STONE,
                ATContent.blockOre.get(material).getDefaultState(),
                veinSize
        );
    }

    private static CountRangeConfig oreRange(int veinCount, int minY, int maxY)
    {
        return new CountRangeConfig(veinCount, minY, minY, maxY);
    }
}