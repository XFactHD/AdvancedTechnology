package xfacthd.advtech.common.item.material;

import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemNugget extends ItemBase
{
    private final Materials material;

    public ItemNugget(Materials material)
    {
        super("item_nugget_" + material.getName(), ItemGroups.MATERIAL_GROUP);
        this.material = material;
    }

    public Materials getMaterial() { return material; }

    public static ItemNugget[] registerItems()
    {
        ATContent.itemNugget = new EnumMap<>(Materials.class);
        List<ItemNugget> items = new ArrayList<>();

        for (Materials material : Materials.values())
        {
            if (!material.hasNugget()) { continue; }

            ItemNugget item = new ItemNugget(material);
            ATContent.itemNugget.put(material, item);
            items.add(item);
        }

        return items.toArray(items.toArray(new ItemNugget[0]));
    }
}