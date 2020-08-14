package xfacthd.advtech.common.block;

import net.minecraft.block.Block;
import net.minecraft.item.*;
import xfacthd.advtech.AdvancedTechnology;

public class BlockBase extends Block
{
    private final ItemGroup group;

    public BlockBase(String name, ItemGroup group, Properties props)
    {
        super(props);
        this.group = group;
        setRegistryName(AdvancedTechnology.MODID, name);
    }

    protected Item.Properties createItemProperties() { return new Item.Properties().group(group); }

    public Item createItemBlock()
    {
        BlockItem item = new BlockItem(this, createItemProperties());
        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }
}