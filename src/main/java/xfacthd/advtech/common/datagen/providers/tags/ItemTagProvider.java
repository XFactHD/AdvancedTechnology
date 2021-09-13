package xfacthd.advtech.common.datagen.providers.tags;

import net.minecraft.data.DataGenerator;
import net.minecraft.data.tags.BlockTagsProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.Tags;
import net.minecraftforge.common.data.ExistingFileHelper;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.util.data.TagHolder;

public class ItemTagProvider extends ItemTagsProvider
{
    public ItemTagProvider(DataGenerator generator, BlockTagsProvider blockTagsProvider, ExistingFileHelper fileHelper)
    {
        super(generator, blockTagsProvider, AdvancedTechnology.MODID, fileHelper);
    }

    @Override
    protected void addTags()
    {
        for (MaterialType material : MaterialType.values())
        {
            if (material.hasOre())
            {
                copy(TagHolder.BLOCK_ORES.get(material), TagHolder.ITEM_ORES.get(material));
                tag(Tags.Items.ORES).add(ATContent.ORE_BLOCKS.get(material).get().asItem());
            }

            if (material.hasBlock())
            {
                copy(TagHolder.BLOCK_STORAGE_BLOCKS.get(material), TagHolder.ITEM_STORAGE_BLOCKS.get(material));
                tag(Tags.Items.STORAGE_BLOCKS).add(ATContent.STORAGE_BLOCKS.get(material).get().asItem());
            }

            if (material.hasPowder())
            {
                tag(TagHolder.DUSTS.get(material)).add(ATContent.POWDER_ITEMS.get(material).get());
            }

            if (material.hasIngot())
            {
                if (material.isMetal())
                {
                    Item item = ATContent.INGOT_ITEMS.get(material).get();

                    tag(TagHolder.INGOTS.get(material)).add(item);
                    tag(Tags.Items.INGOTS).add(item);
                }
                else
                {
                    tag(TagHolder.MATERIALS.get(material)).add(ATContent.MATERIAL_ITEMS.get(material).get());
                }
            }

            if (material.hasNugget())
            {
                Item item = ATContent.NUGGET_ITEMS.get(material).get();

                tag(TagHolder.NUGGETS.get(material)).add(item);
                tag(Tags.Items.NUGGETS).add(item);
            }

            if (material.hasGear())
            {
                tag(TagHolder.GEARS.get(material)).add(ATContent.GEAR_ITEMS.get(material).get());
            }

            if (material.hasPlate())
            {
                tag(TagHolder.PLATES.get(material)).add(ATContent.PLATE_ITEMS.get(material).get());
            }
        }

        tag(TagHolder.ITEM_ORES.get(MaterialType.COPPER)).add(Items.COPPER_ORE);
        tag(TagHolder.ITEM_STORAGE_BLOCKS.get(MaterialType.COPPER)).add(Items.COPPER_BLOCK);
        tag(TagHolder.INGOTS.get(MaterialType.COPPER)).add(Items.COPPER_INGOT);

        tag(TagHolder.MATERIALS.get(MaterialType.CHARCOAL)).add(Items.CHARCOAL);
        tag(TagHolder.BRICKS).add(Blocks.BRICKS.asItem());

        tag(TagHolder.WRENCHES).add(ATContent.ITEM_WRENCH.get());

        tag(TagHolder.MOLDS)
                .add(ATContent.ITEM_PLATE_MOLD.get())
                .add(ATContent.ITEM_GEAR_MOLD.get())
                .add(ATContent.ITEM_ROD_MOLD.get());
    }

    @Override
    public String getName() { return AdvancedTechnology.MODID + ".item_tags"; }
}