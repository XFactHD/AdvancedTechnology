package xfacthd.advtech.common.util.data;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.*;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.data.subtypes.MaterialType;

import java.util.EnumMap;
import java.util.Map;

public class TagHolder
{
    public static final Map<MaterialType, TagKey<Block>> BLOCK_ORES;
    public static final Map<MaterialType, TagKey<Block>> BLOCK_STORAGE_BLOCKS;

    public static final Map<MaterialType, TagKey<Item>> ITEM_ORES;
    public static final Map<MaterialType, TagKey<Item>> ITEM_STORAGE_BLOCKS;

    public static final Map<MaterialType, TagKey<Item>> DUSTS;
    public static final Map<MaterialType, TagKey<Item>> INGOTS;
    public static final Map<MaterialType, TagKey<Item>> NUGGETS;
    public static final Map<MaterialType, TagKey<Item>> GEARS;
    public static final Map<MaterialType, TagKey<Item>> PLATES;
    public static final Map<MaterialType, TagKey<Item>> MATERIALS;

    public static final TagKey<Item> BRICKS = forgeItemTag("bricks");

    public static final TagKey<Item> WRENCHES = forgeItemTag("tools/wrench");
    public static final TagKey<Item> MOLDS = internalItemTag("molds");

    static
    {
        Map<MaterialType, TagKey<Block>> blockOres = new EnumMap<>(MaterialType.class);
        Map<MaterialType, TagKey<Block>> blockStorageBlocks = new EnumMap<>(MaterialType.class);

        Map<MaterialType, TagKey<Item>> itemOres = new EnumMap<>(MaterialType.class);
        Map<MaterialType, TagKey<Item>> itemStorageBlocks = new EnumMap<>(MaterialType.class);

        Map<MaterialType, TagKey<Item>> dusts = new EnumMap<>(MaterialType.class);
        Map<MaterialType, TagKey<Item>> ingots = new EnumMap<>(MaterialType.class);
        Map<MaterialType, TagKey<Item>> nuggets = new EnumMap<>(MaterialType.class);
        Map<MaterialType, TagKey<Item>> gears = new EnumMap<>(MaterialType.class);
        Map<MaterialType, TagKey<Item>> plates = new EnumMap<>(MaterialType.class);
        Map<MaterialType, TagKey<Item>> materials = new EnumMap<>(MaterialType.class);

        for (MaterialType material : MaterialType.values())
        {
            if (material.hasOre(true))
            {
                blockOres.put(material, forgeBlockTag("ores/" + material.getSerializedName()));
                itemOres.put(material, forgeItemTag("ores/" + material.getSerializedName()));
            }

            if (material.hasBlock(true))
            {
                blockStorageBlocks.put(material, forgeBlockTag("storage_blocks/" + material.getSerializedName()));
                itemStorageBlocks.put(material, forgeItemTag("storage_blocks/" + material.getSerializedName()));
            }

            if (material.hasPowder())
            {
                dusts.put(material, forgeItemTag("dusts/" + material.getSerializedName()));
            }

            if (material.hasIngot(true))
            {
                ingots.put(material, forgeItemTag("ingots/" + material.getSerializedName()));
            }

            if (material.hasChunk(true))
            {
                materials.put(material, forgeItemTag(material.getSerializedName() + "s"));
            }

            if (material.hasNugget(true))
            {
                nuggets.put(material, forgeItemTag("nuggets/" + material.getSerializedName()));
            }

            if (material.hasGear())
            {
                gears.put(material, forgeItemTag("gears/" + material.getSerializedName()));
            }

            if (material.hasPlate())
            {
                plates.put(material, forgeItemTag("plates/" + material.getSerializedName()));
            }
        }

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

    private static TagKey<Block> forgeBlockTag(String name) { return BlockTags.create(new ResourceLocation("forge", name)); }

    private static TagKey<Item> forgeItemTag(String name) { return ItemTags.create(new ResourceLocation("forge", name)); }

    private static TagKey<Item> internalItemTag(String name)
    {
        return ItemTags.create(new ResourceLocation(AdvancedTechnology.MODID, name));
    }
}