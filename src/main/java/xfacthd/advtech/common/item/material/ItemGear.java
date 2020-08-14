package xfacthd.advtech.common.item.material;

import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemGear extends ItemBase
{
    private final Materials material;

    public ItemGear(Materials material)
    {
        super("item_gear_" + material.getName(), ItemGroups.MATERIAL_GROUP);
        this.material = material;
    }

    public Materials getMaterial() { return material; }

    public static ItemGear[] registerItems()
    {
        ATContent.itemGear = new EnumMap<>(Materials.class);
        List<ItemGear> items = new ArrayList<>();

        for (Materials material : Materials.values())
        {
            if (!material.hasGear()) { continue; }

            ItemGear item = new ItemGear(material);
            ATContent.itemGear.put(material, item);
            items.add(item);
        }

        return items.toArray(items.toArray(new ItemGear[0]));
    }
}