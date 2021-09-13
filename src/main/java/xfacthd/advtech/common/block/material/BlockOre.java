package xfacthd.advtech.common.block.material;

import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.sorting.MaterialCategory;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.item.material.BlockItemMaterial;
import xfacthd.advtech.common.util.interfaces.IBlockItemProvider;
import xfacthd.advtech.common.util.interfaces.IMaterialObject;

public class BlockOre extends Block implements IBlockItemProvider, IMaterialObject
{
    private final MaterialType material;
    private final MaterialCategory category;

    public BlockOre(MaterialType material, MaterialCategory category)
    {
        super(Properties.of(Material.STONE));

        this.material = material;
        this.category = category;
    }

    @Override
    public int getExpDrop(BlockState state, LevelReader reader, BlockPos pos, int fortune, int silktouch)
    {
        if (material.isMetal()) { return 0; }
        if (silktouch != 0) { return 0; }

        switch (material)
        {
            case SULFUR: return Mth.nextInt(RANDOM, 0, 2);
            default: return 0;
        }
    }

    @Override
    public MaterialType getMaterial() { return material; }

    @Override
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