package xfacthd.advtech.common.datagen.providers.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.util.data.TagHolder;

public class BlockTagProvider extends BlockTagsProvider
{
    public BlockTagProvider(DataGenerator generator, ExistingFileHelper fileHelper)
    {
        super(generator, AdvancedTechnology.MODID, fileHelper);
    }

    @Override
    protected void addTags() //TODO: add mineable tags
    {
        for (MaterialType material : MaterialType.values())
        {
            if (material.hasOre())
            {
                Block block = ATContent.ORE_BLOCKS.get(material).get();

                tag(TagHolder.BLOCK_ORES.get(material)).add(block);
                tag(Tags.Blocks.ORES).add(block);
            }

            if (material.hasBlock())
            {
                tag(TagHolder.BLOCK_STORAGE_BLOCKS.get(material)).add(ATContent.STORAGE_BLOCKS.get(material).get());
                tag(Tags.Blocks.STORAGE_BLOCKS).add(ATContent.STORAGE_BLOCKS.get(material).get());
            }
        }

        tag(TagHolder.BLOCK_ORES.get(MaterialType.COPPER)).add(Blocks.COPPER_ORE);
        tag(TagHolder.BLOCK_STORAGE_BLOCKS.get(MaterialType.COPPER)).add(Blocks.COPPER_BLOCK);
    }

    @Override
    public String getName() { return AdvancedTechnology.MODID + ".block_tags"; }
}