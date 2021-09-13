package xfacthd.advtech.common.block.generator;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;

@SuppressWarnings("deprecation")
public class BlockSolarPanel extends BlockMachine
{
    private static final VoxelShape SHAPE = box(0, 0, 0, 16, 5, 16);

    public BlockSolarPanel(MachineType type) { super(type); }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
    {
        return SHAPE;
    }
}