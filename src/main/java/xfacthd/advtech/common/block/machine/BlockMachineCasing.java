package xfacthd.advtech.common.block.machine;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.state.StateContainer;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;

public class BlockMachineCasing extends BlockMachine
{
    public BlockMachineCasing() { super(MachineType.CASING); }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) { }
}