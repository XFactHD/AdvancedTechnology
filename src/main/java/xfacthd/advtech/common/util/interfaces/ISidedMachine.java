package xfacthd.advtech.common.util.interfaces;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.advtech.common.blockentity.storage.BlockEntityEnergyCube;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.data.states.SideAccess;
import xfacthd.advtech.common.util.data.PropertyHolder;

public interface ISidedMachine
{
    SideAccess getSidePort(Side side);

    void setSidePort(Side side, SideAccess port);

    void remapPorts();

    default void onPortMappingChanged(Direction side) { }

    default void rotate(Level level, BlockState state, BlockPos pos, BlockHitResult hit)
    {
        Direction side = hit.getDirection();
        Direction facing = state.getValue(PropertyHolder.FACING_HOR);

        if (side == Direction.UP || side == Direction.DOWN)
        {
            facing = facing.getClockWise();
        }
        else
        {
            facing = side;
        }

        level.setBlockAndUpdate(pos, state.setValue(PropertyHolder.FACING_HOR, facing));
        remapPorts();
    }
}