package xfacthd.advtech.client.color.item;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.world.item.*;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.registries.RegistryObject;
import xfacthd.advtech.client.color.block.BlockColorMaterial;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.item.material.ItemMaterial;

import java.util.*;
import java.util.stream.Stream;

public class ItemColorMaterial implements ItemColor
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
        else if (item instanceof ItemMaterial matItem)
        {
            return matItem.getMaterial().getTintColor();
        }
        return 0xFFFFFFFF;
    }

    public static ItemLike[] getItems()
    {
        //noinspection SuspiciousToArrayCall
        return Stream.of(
                ATContent.ORE_BLOCKS.values(),
                ATContent.STORAGE_BLOCKS.values(),
                ATContent.POWDER_ITEMS.values(),
                ATContent.INGOT_ITEMS.values(),
                ATContent.NUGGET_ITEMS.values(),
                ATContent.GEAR_ITEMS.values(),
                ATContent.PLATE_ITEMS.values(),
                ATContent.MATERIAL_ITEMS.values()
        ).flatMap(Collection::stream)
                .map(RegistryObject::get)
                .toArray(ItemLike[]::new);
    }
}