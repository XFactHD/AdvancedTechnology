package xfacthd.advtech.client.color.block;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.advtech.common.blockentity.storage.BlockEntityFluidTank;

public class BlockColorFluidTank implements BlockColor
{
    @Override
    public int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex)
    {
        if (level != null)
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityFluidTank tank)
            {
                if (tintIndex == 0)
                {
                    return tank.getMachineLevel().getColor();
                }
                else if (tintIndex == 1)
                {
                    return tank.getPortMode().getColor();
                }
            }
        }
        return 0xFFFFFF;
    }
}