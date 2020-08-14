/*  Copyright (C) <2016>  <XFactHD>

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
    along with this program.  If not, see http://www.gnu.org/licenses. */

package XFactHD.advtech.common.blocks.machine;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.blocks.TileEntityOrientable;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.helpers.LogHelper;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.utilClasses.TowerLevel;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("deprecation")
public class BlockOilRefinery extends BlockBase
{
    public BlockOilRefinery()
    {
        super("blockOilRefinery", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, null);
        registerTileEntity(TileEntityOilRefineryMaster.class);
        registerTileEntity(TileEntityOilRefineryTower.class);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.TOWER_LEVEL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.TOWER_LEVEL, TowerLevel.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.TOWER_LEVEL).ordinal();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityOrientable)
        {
            return state.withProperty(PropertyHolder.FACING_CARDINAL, ((TileEntityOrientable)te).getFacing());
        }
        return state;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof TileEntityOilRefineryMaster)
        {
            ((TileEntityOilRefineryMaster)tile).setFacing(placer.getHorizontalFacing().getOpposite());
        }
        for (int y = 1; y < 6; y++)
        {
            BlockPos placePos = pos.up(y);
            world.setBlockState(placePos, state.withProperty(PropertyHolder.TOWER_LEVEL, TowerLevel.values()[y]));
            TileEntity te = world.getTileEntity(placePos);
            if (te instanceof TileEntityOilRefineryTower)
            {
                ((TileEntityOilRefineryTower)te).setLevel(TowerLevel.values()[y]);
                ((TileEntityOilRefineryTower)te).setFacing(placer.getHorizontalFacing().getOpposite());
            }
        }
    }

    @Override
    protected boolean onBlockActivatedSimple(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack heldItem, EnumFacing side)
    {
        if (state.getValue(PropertyHolder.TOWER_LEVEL) == TowerLevel.BASE)
        {
            if (!world.isRemote)
            {
                TileEntity te = world.getTileEntity(pos);
                if (heldItem != null && heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null) && te instanceof TileEntityOilRefineryMaster)
                {
                    return FluidUtil.interactWithFluidHandler(heldItem, ((TileEntityOilRefineryMaster)te).getHandlerOil(), player);
                }
                else
                {
                    player.openGui(AdvancedTechnology.INSTANCE, Reference.GUI_ID_REFINERY, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
            return true;
        }
        else if (heldItem != null && heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            if (!world.isRemote)
            {
                TileEntity te = world.getTileEntity(pos);
                if (!(te instanceof TileEntityOilRefineryTower)) { return false; }
                return FluidUtil.interactWithFluidHandler(heldItem, ((TileEntityOilRefineryTower)te).getFluidHandler(), player);
            }
            return true;
        }
        return false;
    }

    @Override
    protected boolean onBlockWrenched(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, boolean sneaking)
    {
        if (sneaking && !world.isRemote)
        {
            wrenchRefineryBlocks(world, pos, state, !player.capabilities.isCreativeMode);
        }
        return sneaking;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        super.breakBlock(world, pos, state);
        breakRefineryBlocks(world, pos, state);
    }

    @Override
    @SuppressWarnings("ConstantConditions")
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        List<ItemStack> stacks = new ArrayList<>();
        stacks.add(new ItemStack(Item.getItemFromBlock(this), 1, 0));
        TileEntity te = world.getTileEntity(getMasterPosFromTowerLevel(pos, state.getValue(PropertyHolder.TOWER_LEVEL)));
        if (te instanceof TileEntityOilRefineryMaster)
        {
            for (ItemStack stack : ((TileEntityOilRefineryMaster)te).getHandlerCoke().getStacks())
            {
                if (stack != null) { stacks.add(stack); }
            }
        }
        return stacks;
    }

    @Override
    public boolean canPlaceBlockAt(World world, BlockPos pos)
    {
        for (int y = 1; y < 6; y++)
        {
            BlockPos testPos = pos.up(y);
            if (!world.isAirBlock(testPos) && !world.getBlockState(testPos).getBlock().isReplaceable(world, testPos))
            {
                return false;
            }
        }
        return super.canPlaceBlockAt(world, pos);
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        if (state.getValue(PropertyHolder.TOWER_LEVEL) == TowerLevel.BASE)
        {
            return new TileEntityOilRefineryMaster();
        }
        return new TileEntityOilRefineryTower();
    }

    @Override
    public boolean isNormalBlock(IBlockState state)
    {
        return false;
    }

    private void breakRefineryBlocks(World world, BlockPos pos, IBlockState state)
    {
        switch (state.getValue(PropertyHolder.TOWER_LEVEL))
        {
            case BASE:
                world.destroyBlock(pos.up(1), false);
                world.destroyBlock(pos.up(2), false);
                world.destroyBlock(pos.up(3), false);
                world.destroyBlock(pos.up(4), false);
                world.destroyBlock(pos.up(5), false);
                break;
            case ONE:
                world.destroyBlock(pos.down(1), false);
                world.destroyBlock(pos.up(1), false);
                world.destroyBlock(pos.up(2), false);
                world.destroyBlock(pos.up(3), false);
                world.destroyBlock(pos.up(4), false);
                break;
            case TWO:
                world.destroyBlock(pos.down(2), false);
                world.destroyBlock(pos.down(1), false);
                world.destroyBlock(pos.up(1), false);
                world.destroyBlock(pos.up(2), false);
                world.destroyBlock(pos.up(3), false);
                break;
            case THREE:
                world.destroyBlock(pos.down(3), false);
                world.destroyBlock(pos.down(2), false);
                world.destroyBlock(pos.down(1), false);
                world.destroyBlock(pos.up(1), false);
                world.destroyBlock(pos.up(2), false);
                break;
            case FOUR:
                world.destroyBlock(pos.down(4), false);
                world.destroyBlock(pos.down(3), false);
                world.destroyBlock(pos.down(2), false);
                world.destroyBlock(pos.down(1), false);
                world.destroyBlock(pos.up(1), false);
                break;
            case FIVE:
                world.destroyBlock(pos.down(5), false);
                world.destroyBlock(pos.down(4), false);
                world.destroyBlock(pos.down(3), false);
                world.destroyBlock(pos.down(2), false);
                world.destroyBlock(pos.down(1), false);
                break;
        }
    }

    private void wrenchRefineryBlocks(World world, BlockPos pos, IBlockState state, boolean dropBlock)
    {
        switch (state.getValue(PropertyHolder.TOWER_LEVEL))
        {
            case BASE:
                world.destroyBlock(pos, dropBlock);
                world.destroyBlock(pos.up(1), false);
                world.destroyBlock(pos.up(2), false);
                world.destroyBlock(pos.up(3), false);
                world.destroyBlock(pos.up(4), false);
                world.destroyBlock(pos.up(5), false);
                break;
            case ONE:
                world.destroyBlock(pos.down(1), dropBlock);
                world.destroyBlock(pos, false);
                world.destroyBlock(pos.up(1), false);
                world.destroyBlock(pos.up(2), false);
                world.destroyBlock(pos.up(3), false);
                world.destroyBlock(pos.up(4), false);
                break;
            case TWO:
                world.destroyBlock(pos.down(2), dropBlock);
                world.destroyBlock(pos.down(1), false);
                world.destroyBlock(pos, false);
                world.destroyBlock(pos.up(1), false);
                world.destroyBlock(pos.up(2), false);
                world.destroyBlock(pos.up(3), false);
                break;
            case THREE:
                world.destroyBlock(pos.down(3), dropBlock);
                world.destroyBlock(pos.down(2), false);
                world.destroyBlock(pos.down(1), false);
                world.destroyBlock(pos, false);
                world.destroyBlock(pos.up(1), false);
                world.destroyBlock(pos.up(2), false);
                break;
            case FOUR:
                world.destroyBlock(pos.down(4), dropBlock);
                world.destroyBlock(pos.down(3), false);
                world.destroyBlock(pos.down(2), false);
                world.destroyBlock(pos.down(1), false);
                world.destroyBlock(pos, false);
                world.destroyBlock(pos.up(1), false);
                break;
            case FIVE:
                world.destroyBlock(pos.down(5), dropBlock);
                world.destroyBlock(pos.down(4), false);
                world.destroyBlock(pos.down(3), false);
                world.destroyBlock(pos.down(2), false);
                world.destroyBlock(pos.down(1), false);
                world.destroyBlock(pos, false);
                break;
        }
    }

    private BlockPos getMasterPosFromTowerLevel(BlockPos pos, TowerLevel level)
    {
        switch (level)
        {
            case BASE:  return pos;
            case ONE:   return pos.down(1);
            case TWO:   return pos.down(2);
            case THREE: return pos.down(3);
            case FOUR:  return pos.down(4);
            case FIVE:  return pos.down(5);
            default: return pos;
        }
    }
}