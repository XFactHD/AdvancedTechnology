package xfacthd.advtech.common.block.material;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.IWorldReader;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.BlockBase;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.subtypes.Materials;

import java.util.*;

public class BlockOre extends BlockBase
{
    private final Materials material;

    private BlockOre(Materials material)
    {
        super("block_ore_" + material.getName(),
                ItemGroups.MATERIAL_GROUP,
                Properties.create(Material.ROCK)
                        .hardnessAndResistance(3.0F, 3.0F)
        );

        this.material = material;
    }

    @Override
    public int getExpDrop(BlockState state, IWorldReader reader, BlockPos pos, int fortune, int silktouch)
    {
        if (material.isMetal()) { return 0; }
        return silktouch == 0 ? getExperience(RANDOM) : 0;
    }

    public Materials getMaterial() { return material; }

    protected int getExperience(Random rand)
    {
        return MathHelper.nextInt(rand, 0, 2); //TODO: add proper XP values
    }

    public static BlockOre[] registerBlocks()
    {
        ATContent.blockOre = new EnumMap<>(Materials.class);
        List<BlockOre> blocks = new ArrayList<>();

        for (Materials material : Materials.values())
        {
            if (!material.hasOre()) { continue; }

            BlockOre ore = new BlockOre(material);

            ATContent.blockOre.put(material, ore);
            blocks.add(ore);
        }

        return blocks.toArray(new BlockOre[0]);
    }
}