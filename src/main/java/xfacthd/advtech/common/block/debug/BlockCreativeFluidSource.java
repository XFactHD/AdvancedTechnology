package xfacthd.advtech.common.block.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.debug.BlockEntityCreativeFluidSource;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.util.interfaces.IBlockItemProvider;

@SuppressWarnings("deprecation")
public class BlockCreativeFluidSource extends Block implements EntityBlock, IBlockItemProvider
{
    public BlockCreativeFluidSource()
    {
        super(Properties.of(Material.METAL)
                        .strength(-1.0F, 3600000.0F)
                        .noDrops()
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (!stack.isEmpty())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityCreativeFluidSource be)
            {
                boolean isContainer = be.handleContainerInteraction(stack);
                return isContainer ? InteractionResult.SUCCESS : InteractionResult.FAIL;
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new BlockEntityCreativeFluidSource(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return Utils.createBlockEntityTicker(
                type,
                ATContent.BE_TYPE_CREATIVE_FLUID_SOURCE.get(),
                (beLevel, pos, beState, be) -> be.tick()
        );
    }

    @Override
    public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItem(this, new Item.Properties().tab(ItemGroups.TOOL_GROUP));
        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }
}