package xfacthd.advtech.common.block.storage;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.item.storage.BlockItemFluidTank;
import xfacthd.advtech.common.item.tool.ItemUpgrade;
import xfacthd.advtech.common.blockentity.storage.BlockEntityFluidTank;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.util.interfaces.IBlockItemProvider;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlockFluidTank extends Block implements EntityBlock, IBlockItemProvider
{
    private static final VoxelShape SHAPE = box(3, 0, 3, 13, 16, 13);

    public BlockFluidTank()
    {
        super(Properties.of(Material.METAL)
                        .strength(5F, 6F)
                        .noOcclusion()
        );
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(TagHolder.WRENCHES))
        {
            if (!level.isClientSide())
            {
                if (player.isShiftKeyDown())
                {
                    level.destroyBlock(pos, true);
                }
                else
                {
                    if (level.getBlockEntity(pos) instanceof BlockEntityFluidTank tank)
                    {
                        tank.switchMode();
                    }
                }
            }
            return InteractionResult.SUCCESS;
        }
        else if (stack.getItem() instanceof ItemUpgrade && player.isShiftKeyDown())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityFluidTank tank)
            {
                return tank.upgradeMachine(level, player, stack);
            }
        }
        else if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityFluidTank tank)
            {
                ItemStack result = tank.handleContainerInteraction(stack, player);
                if (!result.isEmpty())
                {
                    if (!level.isClientSide())
                    {
                        player.setItemInHand(hand, result);
                        player.getInventory().setChanged();
                    }
                    return InteractionResult.SUCCESS;
                }
                else
                {
                    return InteractionResult.FAIL;
                }
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        ItemStack stack = super.getCloneItemStack(state, target, level, pos, player);
        if (level.getBlockEntity(pos) instanceof BlockEntityFluidTank tank)
        {
            CompoundTag nbt = stack.getOrCreateTagElement("BlockEntityTag");
            tank.writeToItemData(nbt);
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> stacks = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof BlockEntityFluidTank tank && !stacks.isEmpty())
        {
            ItemStack stack = stacks.get(0);
            CompoundTag nbt = stack.getOrCreateTagElement("BlockEntityTag");
            tank.writeToItemData(nbt);
        }
        return stacks;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (level.getBlockEntity(pos) instanceof BlockEntityFluidTank tank)
        {
            return tank.getLightLevel();
        }
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) { return SHAPE; }

    @Override
    public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItemFluidTank(
                this,
                new Item.Properties().stacksTo(1).tab(ItemGroups.MACHINE_GROUP)
        );
        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new BlockEntityFluidTank(pos, state); }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return Utils.createBlockEntityTicker(
                type,
                ATContent.BE_TYPE_FLUID_TANK.get(),
                (beLevel, pos, beState, be) -> ((BlockEntityFluidTank) be).tick()
        );
    }
}