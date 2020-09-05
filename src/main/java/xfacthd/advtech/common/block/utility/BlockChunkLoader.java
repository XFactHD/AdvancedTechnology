package xfacthd.advtech.common.block.utility;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.tileentity.utility.TileEntityChunkLoader;

public class BlockChunkLoader extends BlockMachine
{
    public BlockChunkLoader() { super(MachineType.CHUNK_LOADER); }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (state.getBlock() != newState.getBlock())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityChunkLoader)
            {
                ((TileEntityChunkLoader)te).onBlockBroken();
            }
        }
        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityChunkLoader(); }
}