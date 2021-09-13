package xfacthd.advtech.client.color.block;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.RegistryObject;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.material.BlockOre;
import xfacthd.advtech.common.block.material.BlockStorage;

import java.util.ArrayList;
import java.util.List;

public class BlockColorMaterial implements BlockColor
{
    @Override
    public int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex)
    {
        return getColor(state.getBlock(), tintIndex);
    }

    public static int getColor(Block block, int tintIndex)
    {
        if (block instanceof BlockOre)
        {
            if (tintIndex == 0)
            {
                return ((BlockOre)block).getMaterial().getTintColor();
            }
        }
        else if (block instanceof BlockStorage)
        {
            return ((BlockStorage)block).getMaterial().getTintColor();
        }
        return -1;//0xFFFFFFFF;
    }

    public static Block[] getBlocks()
    {
        List<RegistryObject<Block>> blocks = new ArrayList<>();

        blocks.addAll(ATContent.ORE_BLOCKS.values());
        blocks.addAll(ATContent.STORAGE_BLOCKS.values());

        return blocks.stream().map(RegistryObject::get).toArray(Block[]::new);
    }
}