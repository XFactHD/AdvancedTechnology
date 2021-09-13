package xfacthd.advtech.common.block.debug;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.debug.BlockEntityCreativeEnergySource;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.util.interfaces.IBlockItemProvider;

public class BlockCreativeEnergySource extends Block implements EntityBlock, IBlockItemProvider
{
    public BlockCreativeEnergySource()
    {
        super(Properties.of(Material.METAL)
                        .strength(-1.0F, 3600000.0F)
                        .noDrops()
        );
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return new BlockEntityCreativeEnergySource(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return Utils.createBlockEntityTicker(
                type,
                ATContent.BE_TYPE_CREATIVE_ENERGY_SOURCE.get(),
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