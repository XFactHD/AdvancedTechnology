package xfacthd.advtech.common.block.storage;

import net.minecraft.block.BlockState;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraft.world.storage.loot.LootContext;
import net.minecraft.world.storage.loot.LootParameters;
import net.minecraftforge.common.ToolType;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import xfacthd.advtech.common.block.BlockBase;
import xfacthd.advtech.common.data.ItemGroups;
import xfacthd.advtech.common.data.states.MachineLevel;
import xfacthd.advtech.common.item.storage.BlockItemFluidTank;
import xfacthd.advtech.common.item.tool.ItemUpgrade;
import xfacthd.advtech.common.item.tool.ItemWrench;
import xfacthd.advtech.common.tileentity.storage.TileEntityEnergyCube;
import xfacthd.advtech.common.tileentity.storage.TileEntityFluidTank;
import xfacthd.advtech.common.util.StatusMessages;
import xfacthd.advtech.common.util.data.PropertyHolder;
import xfacthd.advtech.common.util.data.TagHolder;

import java.util.List;

@SuppressWarnings("deprecation")
public class BlockFluidTank extends BlockBase
{
    private static final VoxelShape SHAPE = makeCuboidShape(3, 0, 3, 13, 16, 13);

    public BlockFluidTank()
    {
        super("block_fluid_tank",
                ItemGroups.MACHINE_GROUP,
                Properties.create(Material.IRON)
                        .hardnessAndResistance(5F, 6F)
                        .harvestTool(ToolType.PICKAXE)
                        .notSolid());
    }

    @Override
    public ActionResultType onBlockActivated(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockRayTraceResult hit)
    {
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
                    TileEntity te = world.getTileEntity(pos);
                    if (te instanceof TileEntityFluidTank)
                    {
                        ((TileEntityFluidTank)te).switchMode();
                    }
                }
            }
            return ActionResultType.SUCCESS;
        }
        else if (stack.getItem() instanceof ItemUpgrade && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityFluidTank)
            {
                TileEntityFluidTank machine = (TileEntityFluidTank)te;

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
        else if (stack.getCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY).isPresent())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityFluidTank)
            {
                TileEntityFluidTank tank = (TileEntityFluidTank) te;

                ItemStack result = tank.handleContainerInteraction(stack, player);
                if (!result.isEmpty())
                {
                    if (!world.isRemote())
                    {
                        player.setHeldItem(hand, result);
                        player.inventory.markDirty();
                    }
                    return ActionResultType.SUCCESS;
                }
                else
                {
                    return ActionResultType.FAIL;
                }
            }
        }
        return super.onBlockActivated(state, world, pos, player, hand, hit);
    }

    @Override
    public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player)
    {
        ItemStack stack = super.getPickBlock(state, target, world, pos, player);
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFluidTank)
        {
            CompoundNBT nbt = stack.getOrCreateChildTag("BlockEntityTag");
            ((TileEntityFluidTank)te).writeToItemData(nbt);
        }
        return stack;
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootContext.Builder builder)
    {
        List<ItemStack> stacks = super.getDrops(state, builder);
        TileEntity te = builder.get(LootParameters.BLOCK_ENTITY);
        if (te instanceof TileEntityFluidTank && !stacks.isEmpty())
        {
            ItemStack stack = stacks.get(0);
            CompoundNBT nbt = stack.getOrCreateChildTag("BlockEntityTag");
            ((TileEntityFluidTank) te).writeToItemData(nbt);
        }
        return stacks;
    }

    @Override
    public int getLightValue(BlockState state, IBlockReader world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityFluidTank)
        {
            return ((TileEntityFluidTank)te).getLightLevel();
        }
        return 0;
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) { return SHAPE; }

    @Override
    public Item createItemBlock()
    {
        BlockItem item = new BlockItemFluidTank(this, createItemProperties());
        //noinspection ConstantConditions
        item.setRegistryName(getRegistryName());
        return item;
    }

    @Override
    public boolean hasTileEntity(BlockState state) { return true; }

    @Override
    public TileEntity createTileEntity(BlockState state, IBlockReader world) { return new TileEntityFluidTank(); }
}