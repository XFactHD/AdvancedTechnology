package xfacthd.advtech.common.item.material;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.sorting.MaterialCategory;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.util.interfaces.IMaterialObject;

public class ItemMaterial extends Item implements IMaterialObject
{
    private final MaterialType material;
    private final MaterialCategory category;

    public ItemMaterial(MaterialType material, MaterialCategory category)
    {
        super(new Properties().tab(ItemGroups.MATERIAL_GROUP));
        this.material = material;
        this.category = category;
    }

    @Override
    public MaterialType getMaterial() { return material; }

    @Override
    public MaterialCategory getCategory() { return category; }

    @Override
    public int getBurnTime(ItemStack stack, RecipeType<?> type)
    {
        if (material == MaterialType.COKE) { return 3200; }
        return 0;
    }
}