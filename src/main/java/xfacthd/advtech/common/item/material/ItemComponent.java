package xfacthd.advtech.common.item.material;

import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Components;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemComponent extends ItemBase
{
    private final Components component;

    public ItemComponent(Components component)
    {
        super("item_" + component.getName(), ItemGroups.MATERIAL_GROUP);
        this.component = component;
    }

    public Components getComponent() { return component; }

    public static ItemComponent[] registerItems()
    {
        ATContent.itemComponent = new EnumMap<>(Components.class);
        List<ItemComponent> items = new ArrayList<>();

        for (Components component : Components.values())
        {
            ItemComponent item = new ItemComponent(component);
            ATContent.itemComponent.put(component, item);
            items.add(item);
        }

        return items.toArray(items.toArray(new ItemComponent[0]));
    }
}