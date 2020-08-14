package xfacthd.advtech.common.item.material;

import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemIngot extends ItemBase
{
    private final Materials material;

    public ItemIngot(Materials material)
    {
        super("item_ingot_" + material.getName(), ItemGroups.MATERIAL_GROUP);
        this.material = material;
    }

    public Materials getMaterial() { return material; }

    public static ItemIngot[] registerItems()
    {
        ATContent.itemIngot = new EnumMap<>(Materials.class);
        List<ItemIngot> items = new ArrayList<>();

        for (Materials material : Materials.values())
        {
            if (!material.hasIngot() || !material.isMetal()) { continue; }

            ItemIngot item = new ItemIngot(material);
            ATContent.itemIngot.put(material, item);
            items.add(item);
        }

        return items.toArray(items.toArray(new ItemIngot[0]));
    }
}