package xfacthd.advtech.common.block.storage;

import com.google.common.collect.Maps;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.*;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.*;
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.storage.BlockEntityEnergyCube;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.item.storage.BlockItemEnergyCube;
import xfacthd.advtech.common.item.tool.ItemUpgrade;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.util.data.PropertyHolder;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.util.interfaces.IBlockItemProvider;

import javax.annotation.Nullable;
import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class BlockEnergyCube extends Block implements EntityBlock, IBlockItemProvider
{
    private static final Map<Direction, VoxelShape> SHAPES = createShapes();

    public BlockEnergyCube()
    {
        super(Properties.of(Material.METAL)
                .strength(5F, 6F)
                .lightLevel(state -> 13)
                .noOcclusion()
        );
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        if (context.getPlayer() == null) { return defaultBlockState(); }

        Direction facing = context.getHorizontalDirection();
        if (!context.getPlayer().isShiftKeyDown())
        {
            facing = facing.getOpposite();
        }
        return defaultBlockState().setValue(PropertyHolder.FACING_HOR, facing);
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack)
    {
        if (!level.isClientSide() && level.getBlockEntity(pos) instanceof BlockEntityEnergyCube be)
        {
            be.readFromItemData(stack);
        }
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        if (hand == InteractionHand.OFF_HAND && player.getMainHandItem().getItem() instanceof ItemUpgrade)
        {
            //If the player has an upgrade in the main hand and using it fails, then the off hand is ignored to prevent opening the gui
            return super.use(state, level, pos, player, hand, hit);
        }

        ItemStack stack = player.getItemInHand(hand);
        if (stack.is(TagHolder.WRENCHES))
        {
            if (!level.isClientSide())
            {
                if (player.isShiftKeyDown())
                {
                    level.destroyBlock(pos, true);
                }
                else if (level.getBlockEntity(pos) instanceof BlockEntityEnergyCube be)
                {
                    be.rotate(level, state, pos, hit);
                }
            }
            return InteractionResult.SUCCESS;
        }
        else if (stack.getItem() instanceof ItemUpgrade && player.isShiftKeyDown())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityEnergyCube be)
            {
                return be.upgradeMachine(level, player, stack);
            }
        }
        else
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityEnergyCube be)
            {
                if (!level.isClientSide())
                {
                    NetworkHooks.openGui((ServerPlayer) player, be, pos);
                }
                return InteractionResult.SUCCESS;
            }
        }
        return super.use(state, level, pos, player, hand, hit);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        ItemStack stack = super.getPickBlock(state, target, level, pos, player);
        if (level.getBlockEntity(pos) instanceof BlockEntityEnergyCube cube)
        {
            //INFO: stored energy is always zero because it is never sent to the client
            cube.writeToItemData(stack);
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> stacks = super.getDrops(state, builder);
        BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
        if (be instanceof BlockEntityEnergyCube cube && !stacks.isEmpty())
        {
            ItemStack stack = stacks.get(0);
            cube.writeToItemData(stack);
        }
        return stacks;
    }

    @Override
    public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
    {
        return SHAPES.get(state.getValue(PropertyHolder.FACING_HOR));
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) { return new BlockEntityEnergyCube(pos, state); }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        return Utils.createBlockEntityTicker(
                type,
                ATContent.BE_TYPE_ENERGY_CUBE.get(),
                (beLevel, pos, beState, be) -> ((BlockEntityEnergyCube)be).tick()
        );
    }

    @Override
    public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItemEnergyCube(this, new Item.Properties().stacksTo(1).tab(ItemGroups.MACHINE_GROUP));
        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }

    private static Map<Direction, VoxelShape> createShapes()
    {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

        VoxelShape shape = Stream.of(
                box( 0,  0,  0, 16,  3,  3),
                box( 0,  0, 13, 16,  3, 16),
                box( 0,  0,  3,  3,  3, 13),
                box(13,  0,  3, 16,  3, 13),
                box( 0,  3,  0,  3, 13,  3),
                box( 0,  3, 13,  3, 13, 16),
                box(13,  3,  0, 16, 13,  3),
                box(13,  3, 13, 16, 13, 16),
                box( 0, 13,  0, 16, 16,  3),
                box( 0, 13, 13, 16, 16, 16),
                box( 0, 13,  3,  3, 16, 13),
                box(13, 13,  3, 16, 16, 13),
                box( 2,  2,  0, 14, 14, 14)
        ).reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR)).get();

        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            shapes.put(dir, Utils.rotateShape(Direction.NORTH, dir, shape));
        }

        //noinspection UnstableApiUsage
        return Maps.immutableEnumMap(shapes);
    }
}