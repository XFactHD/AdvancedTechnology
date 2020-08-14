package xfacthd.advtech.common.item.material;

import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemPlate extends ItemBase
{
    private final Materials material;

    public ItemPlate(Materials material)
    {
        super("item_plate_" + material.getName(), ItemGroups.MATERIAL_GROUP);
        this.material = material;
    }

    public Materials getMaterial() { return material; }

    public static ItemPlate[] registerItems()
    {
        ATContent.itemPlate = new EnumMap<>(Materials.class);
        List<ItemPlate> items = new ArrayList<>();

        for (Materials material : Materials.values())
        {
            if (!material.hasPlate()) { continue; }

            ItemPlate item = new ItemPlate(material);
            ATContent.itemPlate.put(material, item);
            items.add(item);
        }

        return items.toArray(items.toArray(new ItemPlate[0]));
    }
}