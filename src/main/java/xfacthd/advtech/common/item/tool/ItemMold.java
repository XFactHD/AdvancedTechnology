package xfacthd.advtech.common.item.tool;

import net.minecraft.world.item.Item;
import xfacthd.advtech.common.data.ItemGroups;

public class ItemMold extends Item
{
    public ItemMold() { super(new Properties().stacksTo(1).tab(ItemGroups.TOOL_GROUP)); }
}