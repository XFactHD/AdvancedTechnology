package xfacthd.advtech.common.block;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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
import net.minecraftforge.fmllegacy.network.NetworkHooks;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;
import xfacthd.advtech.common.ATContent;
import xfacthd.advtech.common.blockentity.BlockEntityInventoryMachine;
import xfacthd.advtech.common.blockentity.BlockEntityMachine;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.data.subtypes.MachineType;
import xfacthd.advtech.common.item.tool.ItemEnhancement;
import xfacthd.advtech.common.item.tool.ItemUpgrade;
import xfacthd.advtech.common.util.StatusMessages;
import xfacthd.advtech.common.util.Utils;
import xfacthd.advtech.common.util.data.PropertyHolder;
import xfacthd.advtech.common.util.data.TagHolder;
import xfacthd.advtech.common.util.interfaces.IBlockItemProvider;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlockMachine extends Block implements EntityBlock, IBlockItemProvider
{
    private final MachineType type;

    public BlockMachine(MachineType type)
    {
        super(Properties.of(Material.METAL)
                .strength(5F, 6F)
                .requiresCorrectToolForDrops()
        );

        this.type = type;

        registerDefaultState(defaultBlockState().setValue(PropertyHolder.ACTIVE, false));
    }

    @Override
    protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder)
    {
        builder.add(PropertyHolder.FACING_HOR, PropertyHolder.ACTIVE);
    }

    @Override
    public BlockState getStateForPlacement(BlockPlaceContext context)
    {
        if (type.isCasing() || !type.canBeRotated() || context.getPlayer() == null) { return defaultBlockState(); }

        Direction facing = context.getHorizontalDirection();
        if (!context.getPlayer().isShiftKeyDown()) { facing = facing.getOpposite(); }
        return defaultBlockState().setValue(PropertyHolder.FACING_HOR, facing);
    }

    @Override
    public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit)
    {
        Item mainItem = player.getMainHandItem().getItem();
        if (hand == InteractionHand.OFF_HAND && (mainItem instanceof ItemUpgrade || mainItem instanceof ItemEnhancement))
        {
            //If the player has an upgrade or enhancement in the main hand and using it fails, then the off hand is ignored to prevent opening the gui
            return super.use(state, level, pos, player, hand, hit);
        }

        ItemStack stack = player.getItemInHand(hand);
        if (type.canBeRotated() && stack.is(TagHolder.WRENCHES))
        {
            if (!level.isClientSide())
            {
                if (player.isShiftKeyDown())
                {
                    level.destroyBlock(pos, true);
                }
                else if (!type.isCasing() && level.getBlockEntity(pos) instanceof BlockEntityInventoryMachine be)
                {
                    be.rotate(level, state, pos, hit);
                }
            }
            return InteractionResult.SUCCESS;
        }
        else if (stack.getItem() instanceof ItemUpgrade && player.isShiftKeyDown())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityMachine machine)
            {
                MachineLevel upgradeLevel = ((ItemUpgrade)stack.getItem()).getLevel();
                if (machine.canUpgrade() && upgradeLevel.isAfter(machine.getMachineLevel()))
                {
                    if (!level.isClientSide())
                    {
                        machine.upgrade(upgradeLevel);
                        if (!player.isCreative())
                        {
                            stack.shrink(1);
                            player.getInventory().setChanged();
                        }
                        player.displayClientMessage(StatusMessages.UPGRADED, false);
                    }
                    return InteractionResult.CONSUME;
                }
                else
                {
                    if (level.isClientSide())
                    {
                        player.displayClientMessage(StatusMessages.CANT_UPGRADE, false);
                    }
                    return InteractionResult.FAIL;
                }
            }
        }
        else if (stack.getItem() instanceof ItemEnhancement && player.isShiftKeyDown())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityMachine machine && machine.supportsEnhancements())
            {
                ItemEnhancement upgrade = (ItemEnhancement) stack.getItem();
                if (machine.canInstallEnhancement(upgrade.getType()))
                {
                    if (!level.isClientSide())
                    {
                        ItemStack toInsert = stack.copy();
                        toInsert.setCount(1);

                        ItemStack result = ItemHandlerHelper.insertItem(machine.getUpgradeInventory(), toInsert, false);
                        if (result.isEmpty() && !player.isCreative())
                        {
                            stack.shrink(1);
                            player.getInventory().setChanged();
                        }
                        player.displayClientMessage(StatusMessages.INSTALLED, false);
                    }
                    return InteractionResult.SUCCESS;
                }
                else
                {
                    if (!level.isClientSide())
                    {
                        player.displayClientMessage(StatusMessages.CANT_INSTALL, false);
                    }
                    return InteractionResult.FAIL;
                }
            }
            else
            {
                if (level.isClientSide())
                {
                    player.displayClientMessage(StatusMessages.NO_SUPPORT, false);
                }
                return InteractionResult.FAIL;
            }
        }
        else if (!type.isCasing())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityMachine be)
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
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving)
    {
        if (!type.isCasing() && state.getBlock() != newState.getBlock())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityMachine be)
            {
                be.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY).ifPresent(handler ->
                {
                    for (int i = 0; i < handler.getSlots(); i++)
                    {
                        popResource(level, pos, handler.getStackInSlot(i));
                    }
                });
            }
        }

        super.onRemove(state, level, pos, newState, isMoving);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
    {
        ItemStack stack = super.getPickBlock(state, target, level, pos, player);
        if (!type.isCasing())
        {
            if (level.getBlockEntity(pos) instanceof BlockEntityMachine be)
            {
                CompoundTag nbt = stack.getOrCreateTagElement("BlockEntityTag");
                be.writeToItemData(nbt);
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
            BlockEntity be = builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
            if (be instanceof BlockEntityMachine machine && !stacks.isEmpty())
            {
                ItemStack stack = stacks.get(0);
                CompoundTag nbt = stack.getOrCreateTagElement("BlockEntityTag");
                machine.writeToItemData(nbt);
            }
        }
        return stacks;
    }

    @Override
    public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos)
    {
        if (type.isCasing()) { return 0; }
        return state.getValue(PropertyHolder.ACTIVE) ? 13 : 0;
    }

    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        if (type.isCasing()) { return null; }
        return type.getTileFactory().create(pos, state);
    }

    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
    {
        if (this.type.isCasing()) { return null; }

        return Utils.createBlockEntityTicker(
                type,
                ATContent.MACHINE_BE_TYPES.get(getType()).get(),
                (beLevel, pos, beState, be) -> ((BlockEntityMachine) be).tick()
        );
    }

    public final MachineType getType() { return type; }

    @Override
    public BlockItem createBlockItem()
    {
        BlockItem item = new BlockItem(this, new Item.Properties().tab(ItemGroups.MACHINE_GROUP));

        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }
}