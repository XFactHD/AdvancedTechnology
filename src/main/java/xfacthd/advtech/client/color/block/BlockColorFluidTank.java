package xfacthd.advtech.client.color.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import xfacthd.advtech.common.tileentity.storage.TileEntityFluidTank;

public class BlockColorFluidTank implements IBlockColor
{
    @Override
    public int getColor(BlockState state, ILightReader lightReader, BlockPos pos, int tintIndex)
    {
        if (lightReader != null)
        {
            TileEntity te = lightReader.getTileEntity(pos);
            if (te instanceof TileEntityFluidTank)
            {
                TileEntityFluidTank tank = (TileEntityFluidTank)te;
                if (tintIndex == 0)
                {
                    return tank.getLevel().getColor();
                }
                else if (tintIndex == 1)
                {
                    return tank.getPortMode().getColor();
                }
            }
        }
        return 0xFFFFFF;
    }
}