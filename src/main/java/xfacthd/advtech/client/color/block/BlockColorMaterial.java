package xfacthd.advtech.client.color.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.block.material.BlockOre;
import xfacthd.advtech.common.block.material.BlockStorage;

import java.util.ArrayList;
import java.util.List;

public class BlockColorMaterial implements IBlockColor
{
    @Override
    public int getColor(BlockState state, ILightReader lightReader, BlockPos pos, int tintIndex)
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
        List<Block> blocks = new ArrayList<>();

        blocks.addAll(ATContent.blockOre.values());
        blocks.addAll(ATContent.blockStorage.values());

        return blocks.toArray(new Block[0]);
    }
}