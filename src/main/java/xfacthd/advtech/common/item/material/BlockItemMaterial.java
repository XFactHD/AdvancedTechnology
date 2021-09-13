package xfacthd.advtech.common.item.material;

import net.minecraft.world.item.BlockItem;
import net.minecraft.world.level.block.Block;
import xfacthd.advtech.common.data.sorting.MaterialCategory;
import xfacthd.advtech.common.data.subtypes.MaterialType;
import xfacthd.advtech.common.util.interfaces.IMaterialObject;

public class BlockItemMaterial extends BlockItem implements IMaterialObject
{
    private final MaterialType material;
    private final MaterialCategory category;

    public BlockItemMaterial(Block block, Properties props, MaterialType material, MaterialCategory category)
    {
        super(block, props);
        this.material = material;
        this.category = category;
    }

    @Override
    public MaterialType getMaterial() { return material; }

    @Override
    public MaterialCategory getCategory() { return category; }
}