package xfacthd.advtech.common.block.utility;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.blockentity.utility.BlockEntityChunkLoader;
import xfacthd.advtech.common.data.subtypes.MachineType;

public class BlockChunkLoader extends BlockMachine
{
    public BlockChunkLoader(MachineType type) { super(type); }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityChunkLoader be)
            {
                be.onBlockBroken();
            }
        }
        super.onRemove(state, level, pos, newState, isMoving);
    }
}