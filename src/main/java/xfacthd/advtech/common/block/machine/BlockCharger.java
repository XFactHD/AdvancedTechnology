package xfacthd.advtech.common.block.machine;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.tileentity.machine.TileEntityCharger;

public class BlockCharger extends BlockMachine
{
    public BlockCharger() { super(MachineType.CHARGER); }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityCharger(); }
}