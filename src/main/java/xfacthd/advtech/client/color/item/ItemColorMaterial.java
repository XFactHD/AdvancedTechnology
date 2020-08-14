package xfacthd.advtech.client.color.item;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.*;
import net.minecraft.util.IItemProvider;
import xfacthd.advtech.client.color.block.BlockColorMaterial;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.subtypes.Materials;
import xfacthd.advtech.common.item.material.*;

import java.util.ArrayList;
import java.util.List;

public class ItemColorMaterial implements IItemColor
{
    @Override
    public int getColor(ItemStack stack, int tintIndex)
    {
        Item item = stack.getItem();
        if (item instanceof BlockItem)
        {
            Block block = ((BlockItem)item).getBlock();
            return BlockColorMaterial.getColor(block, tintIndex);
        }
        else
        {
            Materials material = null;
            if (item instanceof ItemPowder)        { material = ((ItemPowder)  item).getMaterial(); }
            else if (item instanceof ItemIngot)    { material = ((ItemIngot)   item).getMaterial(); }
            else if (item instanceof ItemNugget)   { material = ((ItemNugget)  item).getMaterial(); }
            else if (item instanceof ItemGear)     { material = ((ItemGear)    item).getMaterial(); }
            else if (item instanceof ItemPlate)    { material = ((ItemPlate)   item).getMaterial(); }
            else if (item instanceof ItemMaterial) { material = ((ItemMaterial)item).getMaterial(); }

            if (material != null) { return material.getTintColor(); }
        }
        return 0xFFFFFFFF;
    }

    public static IItemProvider[] getItems()
    {
        List<IItemProvider> items = new ArrayList<>();

        items.addAll(ATContent.blockOre.values());
        items.addAll(ATContent.blockStorage.values());

        items.addAll(ATContent.itemPowder.values());
        items.addAll(ATContent.itemIngot.values());
        items.addAll(ATContent.itemNugget.values());
        items.addAll(ATContent.itemGear.values());
        items.addAll(ATContent.itemPlate.values());
        items.addAll(ATContent.itemMaterial.values());

        return items.toArray(new IItemProvider[0]);
    }
}