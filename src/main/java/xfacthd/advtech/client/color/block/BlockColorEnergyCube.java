package xfacthd.advtech.client.color.block;

import net.minecraft.block.BlockState;
import net.minecraft.client.renderer.color.IBlockColor;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.ILightReader;
import xfacthd.advtech.common.tileentity.storage.TileEntityEnergyCube;

public class BlockColorEnergyCube implements IBlockColor
{
    @Override
    public int getColor(BlockState state, ILightReader lightReader, BlockPos pos, int tintIndex)
    {
        if (lightReader != null)
        {
            TileEntity te = lightReader.getTileEntity(pos);
            if (te instanceof TileEntityEnergyCube)
            {
                TileEntityEnergyCube cube = (TileEntityEnergyCube)te;
                if (tintIndex == 6)
                {
                    return cube.getLevel().getColor();
                }
                else
                {
                    return cube.getCardinalPort(Direction.values()[tintIndex]).getColor();
                }
            }
        }
        return 0xFFFFFFFF;
    }
}