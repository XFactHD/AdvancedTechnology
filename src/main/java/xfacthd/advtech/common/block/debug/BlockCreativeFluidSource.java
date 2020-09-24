package xfacthd.advtech.common.block.debug;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import xfacthd.advtech.common.block.BlockBase;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.tileentity.debug.TileEntityCreativeFluidSource;

@SuppressWarnings("deprecation")
public class BlockCreativeFluidSource extends BlockBase
{
    public BlockCreativeFluidSource()
    {
        super("block_creative_fluid_source",
                ItemGroups.TOOL_GROUP,
                Properties.create(Material.IRON)
                        .hardnessAndResistance(-1.0F, 3600000.0F)
                        .noDrops()
        );
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        ItemStack stack = player.getHeldItem(hand);
        if (!stack.isEmpty())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityCreativeFluidSource)
            {
                boolean isContainer = ((TileEntityCreativeFluidSource) te).handleContainerInteraction(stack);
                return isContainer ? ActionResultType.SUCCESS : ActionResultType.FAIL;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityCreativeFluidSource(); }
}