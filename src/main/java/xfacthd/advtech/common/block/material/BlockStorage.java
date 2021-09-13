package xfacthd.advtech.common.block.material;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.material.Material;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.sorting.MaterialCategory;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.item.material.BlockItemMaterial;
import xfacthd.advtech.common.util.interfaces.IBlockItemProvider;

public class BlockStorage extends Block implements IBlockItemProvider
{
    private final MaterialType material;
    private final MaterialCategory category;

    public BlockStorage(MaterialType material, MaterialCategory category)
    {
        super(Properties.of(material.isMetal() ? Material.METAL : Material.STONE)
                .strength(3.0F, 3.0F)
                .sound(material.isMetal() ? SoundType.METAL : SoundType.STONE)
        );

        this.material = material;
        this.category = category;
    }

    public MaterialType getMaterial() { return material; }

    public MaterialCategory getCategory() { return category; }

    @Override
    public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItemMaterial(
                this,
                new Item.Properties().tab(ItemGroups.MATERIAL_GROUP),
                material,
                category
        );

        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }
}