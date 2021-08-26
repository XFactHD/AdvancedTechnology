package xfacthd.advtech.common.block.generator;

import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;

@SuppressWarnings("deprecation")
public class BlockSolarPanel extends BlockMachine
{
    private static final VoxelShape SHAPE = makeCuboidShape(0, 0, 0, 16, 5, 16);

    public BlockSolarPanel() { super(MachineType.SOLAR_PANEL); }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPE;
    }
}