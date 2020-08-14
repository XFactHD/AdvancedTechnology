package xfacthd.advtech.common.block.generator;

import net.minecraft.block.BlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import xfacthd.advtech.common.block.BlockMachine;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.tileentity.generator.TileEntityBurnerGenerator;

public class BlockBurnerGenerator extends BlockMachine
{
    public BlockBurnerGenerator() { super(MachineType.BURNER_GENERATOR); }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityBurnerGenerator(); }
}