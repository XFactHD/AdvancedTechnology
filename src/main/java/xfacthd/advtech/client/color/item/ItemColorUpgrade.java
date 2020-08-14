package xfacthd.advtech.client.color.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.item.tool.ItemUpgrade;

public class ItemColorUpgrade implements IItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        return ((ItemUpgrade)stack.getItem()).getLevel().getColor();
    }

    public static IItemProvider[] getItems()
    {
        return ATContent.itemUpgrade.values().toArray(new ItemUpgrade[0]);
    }
}