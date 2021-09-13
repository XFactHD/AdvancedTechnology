package xfacthd.advtech.client.color.block;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.advtech.common.blockentity.storage.BlockEntityEnergyCube;

public class BlockColorEnergyCube implements BlockColor
{
    @Override
    public int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex)
    {
        if (level != null)
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityEnergyCube cube)
            {
                if (tintIndex == 6)
                {
                    return cube.getMachineLevel().getColor();
                }
                else
                {
                    return cube.getCardinalPort(Direction.values()[tintIndex]).getColor();
                }
            }
        }
        return 0xFFFFFFFF;
    }
}