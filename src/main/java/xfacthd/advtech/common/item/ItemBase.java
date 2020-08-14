package xfacthd.advtech.common.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import xfacthd.advtech.AdvancedTechnology;

public class ItemBase extends Item
{
    public ItemBase(String name, ItemGroup group, Properties props)
    {
        super(props.group(group));
        setRegistryName(AdvancedTechnology.MODID, name);
    }

    public ItemBase(String name, ItemGroup group) { this(name, group, new Properties()); }
}