package xfacthd.advtech.common.datagen.providers.tags;

import net.minecraft.block.Block;
import net.minecraft.data.*;
import net.minecraftforge.common.Tags;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.util.data.TagHolder;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void registerTags()
    {
        for (Materials material : Materials.values())
        {
            if (material.hasOre())
            {
                Block block = ATContent.blockOre.get(material);

                getBuilder(TagHolder.BLOCK_ORES.get(material)).add(block);
                getBuilder(Tags.Blocks.ORES).add(block);
            }

            if (material.hasBlock())
            {
                getBuilder(TagHolder.BLOCK_STORAGE_BLOCKS.get(material)).add(ATContent.blockStorage.get(material));
                getBuilder(Tags.Blocks.STORAGE_BLOCKS).add(ATContent.blockStorage.get(material));
            }
        }
    }

    @Override
    public String getName() { return AdvancedTechnology.MODID + ".block_tags"; }
}