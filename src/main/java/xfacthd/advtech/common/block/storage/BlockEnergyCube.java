package xfacthd.advtech.common.block.storage;

import com.google.common.collect.Maps;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.*;
import net.minecraft.world.*;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fml.network.NetworkHooks;
import xfacthd.advtech.common.block.BlockBase;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.item.tool.ItemUpgrade;
import xfacthd.advtech.common.item.tool.ItemWrench;
import xfacthd.advtech.common.tileentity.storage.TileEntityEnergyCube;
import xfacthd.advtech.common.util.StatusMessages;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.util.data.PropertyHolder;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.*;
import java.util.stream.Stream;

@SuppressWarnings("deprecation")
public class BlockEnergyCube extends BlockBase
{
    private static final Map<Direction, VoxelShape> SHAPES = createShapes();

    public BlockEnergyCube()
    {
        super("block_energy_cube",
                ItemGroups.MACHINE_GROUP,
                Properties.create(Material.IRON)
                        .hardnessAndResistance(5F, 6F)
                        .harvestTool(ToolType.PICKAXE)
                        .lightValue(13)
                        .notSolid()
        );
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        if (context.getPlayer() == null) { return getDefaultState(); }

        Direction facing = context.getPlacementHorizontalFacing();
        if (!context.getPlayer().isSneaking()) { facing = facing.getOpposite(); }
        return getDefaultState().with(PropertyHolder.FACING_HOR, facing);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        if (hand == Hand.OFF_HAND && player.getHeldItemMainhand().getItem() instanceof ItemUpgrade)
        {
            //If the player has an upgrade in the main hand and using it fails, then the off hand is ignored to prevent opening the gui
            return super.onBlockActivated(state, world, pos, player, hand, hit);
        }

        ItemStack stack = player.getHeldItem(hand);
        if (stack.getItem().isIn(TagHolder.WRENCHES) || stack.getToolTypes().contains(ItemWrench.TOOL_WRENCH))
        {
            if (!world.isRemote())
            {
                if (player.isSneaking())
                {
                    world.destroyBlock(pos, true);
                }
                else
                {
                    Direction side = hit.getFace();
                    Direction facing = state.get(PropertyHolder.FACING_HOR);

                    if (side == Direction.UP || side == Direction.DOWN)
                    {
                        facing = facing.rotateY();
                    }
                    else
                    {
                        facing = side;
                    }

                    world.setBlockState(pos, state.with(PropertyHolder.FACING_HOR, facing));
                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof TileEntityEnergyCube)
                    {
                        ((TileEntityEnergyCube)te).remapPortsToFacing(facing);
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }
        else if (stack.getItem() instanceof ItemUpgrade && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityEnergyCube)
            {
                TileEntityEnergyCube machine = (TileEntityEnergyCube)te;

                MachineLevel upgradeLevel = ((ItemUpgrade)stack.getItem()).getLevel();
                if (upgradeLevel.isNextTo(machine.getLevel()))
                {
                    if (!world.isRemote())
                    {
                        machine.upgrade(upgradeLevel);
                        if (!player.isCreative())
                        {
                            stack.shrink(1);
                            player.inventory.markDirty();
                        }
                        player.sendStatusMessage(StatusMessages.UPGRADED, false);
                    }
                    return ActionResultType.CONSUME;
                }
                else
                {
                    if (world.isRemote())
                    {
                        player.sendStatusMessage(StatusMessages.CANT_UPGRADE, false);
                    }
                    return ActionResultType.FAIL;
                }
            }
        }
        else
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityEnergyCube)
            {
                if (!world.isRemote())
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityEnergyCube)te, pos);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityEnergyCube)
        {
            CompoundNBT nbt = stack.getOrCreateChildTag("BlockEntityTag");
            ((TileEntityEnergyCube)te).writeToItemData(nbt);
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> stacks = super.getDrops(state, builder);
        TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
        if (te instanceof TileEntityEnergyCube && !stacks.isEmpty())
        {
            ItemStack stack = stacks.get(0);
            CompoundNBT nbt = stack.getOrCreateChildTag("BlockEntityTag");
            ((TileEntityEnergyCube) te).writeToItemData(nbt);
        }
        return stacks;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context)
    {
        return SHAPES.get(state.get(PropertyHolder.FACING_HOR));
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityEnergyCube(); }

    private static Map<Direction, VoxelShape> createShapes()
    {
        Map<Direction, VoxelShape> shapes = new EnumMap<>(Direction.class);

        VoxelShape shape = Stream.of(
                Block.makeCuboidShape( 0,  0,  0, 16,  3,  3),
                Block.makeCuboidShape( 0,  0, 13, 16,  3, 16),
                Block.makeCuboidShape( 0,  0,  3,  3,  3, 13),
                Block.makeCuboidShape(13,  0,  3, 16,  3, 13),
                Block.makeCuboidShape( 0,  3,  0,  3, 13,  3),
                Block.makeCuboidShape( 0,  3, 13,  3, 13, 16),
                Block.makeCuboidShape(13,  3,  0, 16, 13,  3),
                Block.makeCuboidShape(13,  3, 13, 16, 13, 16),
                Block.makeCuboidShape( 0, 13,  0, 16, 16,  3),
                Block.makeCuboidShape( 0, 13, 13, 16, 16, 16),
                Block.makeCuboidShape( 0, 13,  3,  3, 16, 13),
                Block.makeCuboidShape(13, 13,  3, 16, 16, 13),
                Block.makeCuboidShape( 2,  2,  0, 14, 14, 14)
        ).reduce((v1, v2) -> VoxelShapes.combineAndSimplify(v1, v2, IBooleanFunction.OR)).get();

        for (Direction dir : Direction.Plane.HORIZONTAL)
        {
            shapes.put(dir, Utils.rotateShape(Direction.NORTH, dir, shape));
        }

        //noinspection UnstableApiUsage
        return Maps.immutableEnumMap(shapes);
    }
}