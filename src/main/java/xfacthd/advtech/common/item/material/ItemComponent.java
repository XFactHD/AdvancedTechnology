package xfacthd.advtech.common.item.material;

import net.minecraft.world.item.Item;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Component;

public class ItemComponent extends Item
{
    private final Component component;

    public ItemComponent(Component component)
    {
        super(new Properties().tab(ItemGroups.MATERIAL_GROUP));
        this.component = component;
    }

    public Component getComponent() { return component; }
}