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
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.*;
import xfacthd.advtech.common.block.BlockBase;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.tileentity.debug.TileEntityCreativeItemSource;

@SuppressWarnings("deprecation")
public class BlockCreativeItemSource extends BlockBase
{
    public BlockCreativeItemSource()
    {
        super("block_creative_item_source",
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
        TileEntity te = world.getTileEntity(pos);
        if (!stack.isEmpty() && te != null)
        {
            LazyOptional<IItemHandler> itemHandler = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY);
            if (itemHandler.isPresent())
            {
                itemHandler.ifPresent(handler ->
                {
                    if (handler instanceof ItemStackHandler)
                    {
                        ((ItemStackHandler)handler).setStackInSlot(0, stack.copy());
                    }
                });
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityCreativeItemSource(); }
}