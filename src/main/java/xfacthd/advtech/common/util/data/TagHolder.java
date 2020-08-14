package xfacthd.advtech.common.util.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tags.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.Tags;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.data.subtypes.Materials;

import java.util.EnumMap;
import java.util.Map;

public class TagHolder
{
    public static final Map<Materials, Tag<Block>> BLOCK_ORES;
    public static final Map<Materials, Tag<Block>> BLOCK_STORAGE_BLOCKS;

    public static final Map<Materials, Tag<Item>> ITEM_ORES;
    public static final Map<Materials, Tag<Item>> ITEM_STORAGE_BLOCKS;

    public static final Map<Materials, Tag<Item>> DUSTS;
    public static final Map<Materials, Tag<Item>> INGOTS;
    public static final Map<Materials, Tag<Item>> NUGGETS;
    public static final Map<Materials, Tag<Item>> GEARS;
    public static final Map<Materials, Tag<Item>> PLATES;
    public static final Map<Materials, Tag<Item>> MATERIALS;

    public static final Tag<Item> BRICKS = forgeItemTag("bricks");

    public static final Tag<Item> WRENCHES = forgeItemTag("wrenches");
    public static final Tag<Item> MOLDS = internalItemTag("molds");

    static
    {
        Map<Materials, Tag<Block>> blockOres = new EnumMap<>(Materials.class);
        Map<Materials, Tag<Block>> blockStorageBlocks = new EnumMap<>(Materials.class);

        Map<Materials, Tag<Item>> itemOres = new EnumMap<>(Materials.class);
        Map<Materials, Tag<Item>> itemStorageBlocks = new EnumMap<>(Materials.class);

        Map<Materials, Tag<Item>> dusts = new EnumMap<>(Materials.class);
        Map<Materials, Tag<Item>> ingots = new EnumMap<>(Materials.class);
        Map<Materials, Tag<Item>> nuggets = new EnumMap<>(Materials.class);
        Map<Materials, Tag<Item>> gears = new EnumMap<>(Materials.class);
        Map<Materials, Tag<Item>> plates = new EnumMap<>(Materials.class);
        Map<Materials, Tag<Item>> materials = new EnumMap<>(Materials.class);

        for (Materials material : Materials.values())
        {
            if (material.hasOre())
            {
                blockOres.put(material, forgeBlockTag("ores/" + material.getName()));
                itemOres.put(material, forgeItemTag("ores/" + material.getName()));
            }

            if (material.hasBlock())
            {
                blockStorageBlocks.put(material, forgeBlockTag("storage_blocks/" + material.getName()));
                itemStorageBlocks.put(material, forgeItemTag("storage_blocks/" + material.getName()));
            }

            if (material.hasPowder())
            {
                dusts.put(material, forgeItemTag("dusts/" + material.getName()));
            }

            if (material.hasIngot() || material == Materials.CHARCOAL)
            {
                if (material.isMetal())
                {
                    ingots.put(material, forgeItemTag("ingots/" + material.getName()));
                }
                else
                {
                    materials.put(material, forgeItemTag(material.getName()));
                }
            }

            if (material.hasNugget())
            {
                nuggets.put(material, forgeItemTag("nuggets/" + material.getName()));
            }

            if (material.hasGear())
            {
                gears.put(material, forgeItemTag("gears/" + material.getName()));
            }

            if (material.hasPlate())
            {
                plates.put(material, forgeItemTag("plates/" + material.getName()));
            }
        }

        itemOres.put(Materials.IRON, Tags.Items.ORES_IRON);
        itemOres.put(Materials.GOLD, Tags.Items.ORES_GOLD);
        itemOres.put(Materials.COAL, Tags.Items.ORES_COAL);
        itemStorageBlocks.put(Materials.IRON, Tags.Items.STORAGE_BLOCKS_IRON);
        itemStorageBlocks.put(Materials.GOLD, Tags.Items.STORAGE_BLOCKS_GOLD);
        itemStorageBlocks.put(Materials.COAL, Tags.Items.STORAGE_BLOCKS_COAL);
        ingots.put(Materials.IRON, Tags.Items.INGOTS_IRON);
        ingots.put(Materials.GOLD, Tags.Items.INGOTS_GOLD);
        nuggets.put(Materials.IRON, Tags.Items.NUGGETS_IRON);
        nuggets.put(Materials.GOLD, Tags.Items.NUGGETS_GOLD);

        BLOCK_ORES = ImmutableMap.copyOf(blockOres);
        BLOCK_STORAGE_BLOCKS = ImmutableMap.copyOf(blockStorageBlocks);

        ITEM_ORES = ImmutableMap.copyOf(itemOres);
        ITEM_STORAGE_BLOCKS = ImmutableMap.copyOf(itemStorageBlocks);

        DUSTS = ImmutableMap.copyOf(dusts);
        INGOTS = ImmutableMap.copyOf(ingots);
        NUGGETS = ImmutableMap.copyOf(nuggets);
        GEARS = ImmutableMap.copyOf(gears);
        PLATES = ImmutableMap.copyOf(plates);
        MATERIALS = ImmutableMap.copyOf(materials);
    }

    private static Tag<Block> forgeBlockTag(String name) { return new BlockTags.Wrapper(new ResourceLocation("forge", name)); }

    private static Tag<Item> forgeItemTag(String name) { return new ItemTags.Wrapper(new ResourceLocation("forge", name)); }

    private static Tag<Item> internalItemTag(String name)
    {
        return new ItemTags.Wrapper(new ResourceLocation(AdvancedTechnology.MODID, name));
    }
}