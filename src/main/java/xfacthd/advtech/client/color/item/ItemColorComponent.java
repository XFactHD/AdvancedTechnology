package xfacthd.advtech.client.color.item;

import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.IItemProvider;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Components;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.material.ItemComponent;

public class ItemColorComponent implements IItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        Components component = ((ItemComponent)stack.getItem()).getComponent();
        if (component == Components.TRANSMISSION_COIL && tintIndex == 1)
        {
            return Materials.ELECTRUM.getTintColor();
        }
        else if (component == Components.RECEPTION_COIL && tintIndex == 1)
        {
            return Materials.COPPER.getTintColor();
        }
        return 0xFFFFFF;
    }

    public static IItemProvider[] getItems()
    {
        return new IItemProvider[] {
                ATContent.itemComponent.get(Components.TRANSMISSION_COIL),
                ATContent.itemComponent.get(Components.RECEPTION_COIL)
        };
    }
}