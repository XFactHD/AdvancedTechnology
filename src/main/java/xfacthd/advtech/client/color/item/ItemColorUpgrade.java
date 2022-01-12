package xfacthd.advtech.client.color.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.item.tool.ItemUpgrade;

public class ItemColorUpgrade implements ItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        return ((ItemUpgrade)stack.getItem()).getLevel().getColor();
    }

    public static ItemLike[] getItems()
    {
        return ATContent.UPGRADE_ITEMS.values().stream().map(RegistryObject::get).toArray(Item[]::new);
    }
}