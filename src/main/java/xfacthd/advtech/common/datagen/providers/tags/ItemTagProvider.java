package xfacthd.advtech.common.datagen.providers.tags;

import net.minecraft.block.Blocks;
import net.minecraft.data.*;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraftforge.common.Tags;
import xfacthd.advtech.AdvancedTechnology;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.util.data.TagHolder;

public class ItemTagProvider extends ItemTagsProvider
{
    public ItemTagProvider(DataGenerator generator) { super(generator); }

    @Override
    protected void registerTags()
    {
        for (Materials material : Materials.values())
        {
            if (material.hasOre())
            {
                copy(TagHolder.BLOCK_ORES.get(material), TagHolder.ITEM_ORES.get(material));
                getBuilder(Tags.Items.ORES).add(ATContent.blockOre.get(material).asItem());
            }

            if (material.hasBlock())
            {
                copy(TagHolder.BLOCK_STORAGE_BLOCKS.get(material), TagHolder.ITEM_STORAGE_BLOCKS.get(material));
                getBuilder(Tags.Items.STORAGE_BLOCKS).add(ATContent.blockStorage.get(material).asItem());
            }

            if (material.hasPowder())
            {
                getBuilder(TagHolder.DUSTS.get(material)).add(ATContent.itemPowder.get(material));
            }

            if (material.hasIngot())
            {
                if (material.isMetal())
                {
                    Item item = ATContent.itemIngot.get(material);

                    getBuilder(TagHolder.INGOTS.get(material)).add(item);
                    getBuilder(Tags.Items.INGOTS).add(item);
                }
                else
                {
                    getBuilder(TagHolder.MATERIALS.get(material)).add(ATContent.itemMaterial.get(material));
                }
            }

            if (material.hasNugget())
            {
                Item item = ATContent.itemNugget.get(material);

                getBuilder(TagHolder.NUGGETS.get(material)).add(item);
                getBuilder(Tags.Items.NUGGETS).add(item);
            }

            if (material.hasGear())
            {
                getBuilder(TagHolder.GEARS.get(material)).add(ATContent.itemGear.get(material));
            }

            if (material.hasPlate())
            {
                getBuilder(TagHolder.PLATES.get(material)).add(ATContent.itemPlate.get(material));
            }
        }

        getBuilder(TagHolder.MATERIALS.get(Materials.CHARCOAL)).add(Items.CHARCOAL);
        getBuilder(TagHolder.BRICKS).add(Blocks.BRICKS.asItem());

        getBuilder(TagHolder.WRENCHES).add(ATContent.itemWrench);

        getBuilder(TagHolder.MOLDS)
                .add(ATContent.itemPlateMold)
                .add(ATContent.itemGearMold)
                .add(ATContent.itemRodMold);
    }

    @Override
    public String getName() { return AdvancedTechnology.MODID + ".item_tags"; }
}