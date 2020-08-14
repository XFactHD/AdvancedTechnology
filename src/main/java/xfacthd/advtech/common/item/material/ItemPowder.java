package xfacthd.advtech.common.item.material;

import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemPowder extends ItemBase
{
    private final Materials material;

    public ItemPowder(Materials material)
    {
        super("item_powder_" + material.getName(), ItemGroups.MATERIAL_GROUP);
        this.material = material;
    }

    public Materials getMaterial() { return material; }

    public static ItemPowder[] registerItems()
    {
        ATContent.itemPowder = new EnumMap<>(Materials.class);
        List<ItemPowder> items = new ArrayList<>();

        for (Materials material : Materials.values())
        {
            if (!material.hasPowder()) { continue; }

            ItemPowder item = new ItemPowder(material);
            ATContent.itemPowder.put(material, item);
            items.add(item);
        }

        return items.toArray(items.toArray(new ItemPowder[0]));
    }
}