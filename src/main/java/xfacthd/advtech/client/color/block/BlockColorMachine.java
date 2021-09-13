package xfacthd.advtech.client.color.block;

import net.minecraft.client.color.block.BlockColor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.fmllegacy.RegistryObject;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.data.states.Side;

public class BlockColorMachine implements BlockColor
{
    @Override
    public int getColor(BlockState state, BlockAndTintGetter level, BlockPos pos, int tintIndex)
    {
        if (level != null)
        {
            BlockEntity be = level.getBlockEntity(pos);
            if (tintIndex == 6 && be instanceof BlockEntityMachine machine)
            {
                return machine.getMachineLevel().getColor();
            }
            else if (be instanceof BlockEntityInventoryMachine machine)
            {
                return machine.getSidePort(Side.values()[tintIndex]).getColor();
            }
        }
        return 0xFFFFFFFF;
    }

    public static Block[] getBlocks()
    {
        return ATContent.MACHINE_BLOCKS.values().stream().map(RegistryObject::get).toArray(Block[]::new);
    }
}