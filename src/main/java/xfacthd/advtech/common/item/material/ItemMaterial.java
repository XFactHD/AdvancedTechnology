package xfacthd.advtech.common.item.material;

import net.minecraft.item.ItemStack;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.ItemBase;

import java.util.*;

public class ItemMaterial extends ItemBase
{
    private final Materials material;

    public ItemMaterial(Materials material)
    {
        super("item_" + material.getName(), ItemGroups.MATERIAL_GROUP);
        this.material = material;
    }

    public Materials getMaterial() { return material; }

    @Override
    public int getBurnTime(ItemStack itemStack)
    {
        if (material == Materials.COKE) { return 3200; }
        return 0;
    }

    public static ItemMaterial[] registerItems()
    {
        ATContent.itemMaterial = new EnumMap<>(Materials.class);
        List<ItemMaterial> items = new ArrayList<>();

        for (Materials material : Materials.values())
        {
            if (!material.hasIngot() || material.isMetal()) { continue; }

            ItemMaterial item = new ItemMaterial(material);
            ATContent.itemMaterial.put(material, item);
            items.add(item);
        }

        return items.toArray(items.toArray(new ItemMaterial[0]));
    }
}