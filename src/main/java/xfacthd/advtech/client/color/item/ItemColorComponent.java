package xfacthd.advtech.client.color.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Component;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.item.material.ItemComponent;

public class ItemColorComponent implements ItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        Component component = ((ItemComponent)stack.getItem()).getComponent();
        if (component == Component.TRANSMISSION_COIL && tintIndex == 1)
        {
            return MaterialType.ELECTRUM.getTintColor();
        }
        else if (component == Component.RECEPTION_COIL && tintIndex == 1)
        {
            return MaterialType.COPPER.getTintColor();
        }
        return 0xFFFFFF;
    }

    public static ItemLike[] getItems()
    {
        return new ItemLike[] {
                ATContent.COMPONENT_ITEMS.get(Component.TRANSMISSION_COIL).get(),
                ATContent.COMPONENT_ITEMS.get(Component.RECEPTION_COIL).get()
        };
    }
}