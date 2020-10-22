package xfacthd.advtech.common.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.StateContainer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.fml.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.item.tool.*;
import xfacthd.advtech.common.tileentity.TileEntityInventoryMachine;
import xfacthd.advtech.common.tileentity.TileEntityMachine;
import xfacthd.advtech.common.util.StatusMessages;
import xfacthd.advtech.common.util.data.PropertyHolder;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlockMachine extends BlockBase
{
    private final MachineType type;

    public BlockMachine(MachineType type)
    {
        super("block_" + type.getName(),
                ItemGroups.MACHINE_GROUP,
                Properties.create(Material.IRON)
                        .hardnessAndResistance(5F, 6F)
        );

        this.type = type;

        setDefaultState(getDefaultState().with(PropertyHolder.ACTIVE, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context)
    {
        if (type.isCasing() || !type.canBeRotated() || context.getPlayer() == null) { return getDefaultState(); }

        Direction facing = context.getPlacementHorizontalFacing();
        if (!context.getPlayer().isSneaking()) { facing = facing.getOpposite(); }
        return getDefaultState().with(PropertyHolder.FACING_HOR, facing);
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
        Item mainItem = player.getHeldItemMainhand().getItem();
        if (hand == Hand.OFF_HAND && (mainItem instanceof ItemUpgrade || mainItem instanceof ItemEnhancement))
        {
            //If the player has an upgrade or enhancement in the main hand and using it fails, then the off hand is ignored to prevent opening the gui
            return super.onBlockActivated(state, world, pos, player, hand, hit);
        }

        ItemStack stack = player.getHeldItem(hand);
        if (type.canBeRotated() && (stack.getItem().isIn(TagHolder.WRENCHES) || stack.getToolTypes().contains(ItemWrench.TOOL_WRENCH)))
        {
            if (!world.isRemote())
            {
                if (player.isSneaking())
                {
                    world.destroyBlock(pos, true);
                }
                else if (!type.isCasing())
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
                    if (te instanceof TileEntityInventoryMachine)
                    {
                        ((TileEntityInventoryMachine)te).remapPortsToFacing(facing);
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }
        else if (stack.getItem() instanceof ItemUpgrade && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMachine)
            {
                TileEntityMachine machine = (TileEntityMachine)te;

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
        else if (stack.getItem() instanceof ItemEnhancement && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMachine && ((TileEntityMachine) te).supportsEnhancements())
            {
                TileEntityMachine machine = (TileEntityMachine)te;

                ItemEnhancement upgrade = (ItemEnhancement) stack.getItem();
                if (machine.canInstallEnhancement(upgrade.getType()))
                {
                    if (!world.isRemote())
                    {
                        ItemStack toInsert = stack.copy();
                        toInsert.setCount(1);

                        ItemStack result = ItemHandlerHelper.insertItem(machine.getUpgradeInventory(), toInsert, false);
                        if (result.isEmpty() && !player.isCreative())
                        {
                            stack.shrink(1);
                            player.inventory.markDirty();
                        }
                        player.sendStatusMessage(StatusMessages.INSTALLED, false);
                    }
                    return ActionResultType.SUCCESS;
                }
                else
                {
                    if (!world.isRemote())
                    {
                        player.sendStatusMessage(StatusMessages.CANT_INSTALL, false);
                    }
                    return ActionResultType.FAIL;
                }
            }
            else
            {
                if (world.isRemote())
                {
                    player.sendStatusMessage(StatusMessages.NO_SUPPORT, false);
                }
                return ActionResultType.FAIL;
            }
        }
        else if (!type.isCasing())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMachine)
            {
                if (!world.isRemote())
                {
                    NetworkHooks.openGui((ServerPlayerEntity) player, (TileEntityMachine)te, pos);
                }
                return ActionResultType.SUCCESS;
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public void onReplaced(BlockState state, World world, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!type.isCasing() && state.getBlock() != newState.getBlock())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMachine)
            {
                te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
                {
                    for (int i = 0; i < handler.getSlots(); i++)
                    {
                        spawnAsEntity(world, pos, handler.getStackInSlot(i));
                    }
                });
            }
        }

        super.onReplaced(state, world, pos, newState, isMoving);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        if (!type.isCasing())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityMachine)
            {
                CompoundNBT nbt = stack.getOrCreateChildTag("BlockEntityTag");
                ((TileEntityMachine)te).writeToItemData(nbt);
            }
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> stacks = super.getDrops(state, builder);
        if (!type.isCasing())
        {
            TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
            if (te instanceof TileEntityMachine && !stacks.isEmpty())
            {
                ItemStack stack = stacks.get(0);
                CompoundNBT nbt = stack.getOrCreateChildTag("BlockEntityTag");
                ((TileEntityMachine) te).writeToItemData(nbt);
            }
        }
        return stacks;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos)
    {
        if (type.isCasing()) { return 0; }
        return state.get(PropertyHolder.ACTIVE) ? 13 : 0;
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return !type.isCasing(); }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return type.getTileFactory().get(); }

    public final MachineType getType() { return type; }
}