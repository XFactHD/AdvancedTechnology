package xfacthd.advtech.client.color.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.states.Side;
import xfacthd.advtech.common.tileentity.TileEntityMachine;

import java.util.ArrayList;
import java.util.List;

public class BlockColorMachine implements IBlockColor
{
    @Override
    public int getColor(BlockState state, ILightReader lightReader, BlockPos pos, int tintIndex)
    {
        if (lightReader != null)
        {
            TileEntity te = lightReader.getTileEntity(pos);
            if (te instanceof TileEntityMachine)
            {
                TileEntityMachine machine = (TileEntityMachine)te;
                if (tintIndex == 6)
                {
                    return machine.getLevel().getColor();
                }
                else
                {
                    return machine.getSidePort(Side.values()[tintIndex]).getColor();
                }
            }
        }
        return 0xFFFFFFFF;
    }

    public static Block[] getBlocks()
    {
        List<Block> blocks = new ArrayList<>();

        blocks.add(ATContent.blockElectricFurnace);
        blocks.add(ATContent.blockCrusher);
        blocks.add(ATContent.blockAlloySmelter);
        blocks.add(ATContent.blockMetalPress);

        blocks.add(ATContent.blockBurnerGenerator);

        return blocks.toArray(blocks.toArray(new Block[0]));
    }
}