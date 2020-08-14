package xfacthd.advtech.common.block.debug;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.IBlockReader;
import xfacthd.advtech.common.block.BlockBase;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.tileentity.debug.TileEntityCreativeEnergySource;

public class BlockCreativeEnergySource extends BlockBase
{
    public BlockCreativeEnergySource()
    {
        super("block_creative_energy_source",
                ItemGroups.TOOL_GROUP,
                Properties.create(Material.IRON)
                        .hardnessAndResistance(-1.0F, 3600000.0F)
                        .noDrops()
        );
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityCreativeEnergySource(); }
}