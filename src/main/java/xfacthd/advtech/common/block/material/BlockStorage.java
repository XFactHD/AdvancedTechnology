package xfacthd.advtech.common.block.material;

import net.minecraft.block.material.Material;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.BlockBase;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Materials;

import java.util.*;

public class BlockStorage extends BlockBase
{
    private final Materials material;

    private BlockStorage(Materials material)
    {
        super("block_" + material.getName(),
                ItemGroups.MATERIAL_GROUP,
                Properties.create(Material.IRON)
                        .hardnessAndResistance(3.0F, 3.0F)
        );

        this.material = material;
    }

    public Materials getMaterial() { return material; }

    public static BlockStorage[] registerBlocks()
    {
        ATContent.blockStorage = new EnumMap<>(Materials.class);
        List<BlockStorage> blocks = new ArrayList<>();

        for (Materials material : Materials.values())
        {
            if (!material.hasBlock()) { continue; }

            BlockStorage ore = new BlockStorage(material);

            ATContent.blockStorage.put(material, ore);
            blocks.add(ore);
        }

        return blocks.toArray(new BlockStorage[0]);
    }
}