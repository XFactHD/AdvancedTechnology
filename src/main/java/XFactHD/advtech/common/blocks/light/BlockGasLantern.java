/*  Copyright (C) <2016>  <XFactHD, DrakoAlcarus>

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

package XFactHD.advtech.common.blocks.light;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.blocks.TileEntityOrientable;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.utilClasses.TowerLevel;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class BlockGasLantern extends BlockBase
{
    public BlockGasLantern()
    {
        super("blockGasLantern", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, EnumType.getAsStringArray());
        registerTileEntity(TileEntityGasLanternWallmount.class);
        registerTileEntity(TileEntityGasLanternStandingBase.class);
        registerTileEntity(TileEntityGasLanternStandingLamp.class);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.GAS_LANTERN_TYPE, PropertyHolder.LANTERN_HEIGHT, PropertyHolder.FACING_FULL,
                                       PropertyHolder.ACTIVE, PropertyHolder.AUTO);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        if (meta == 0)
        {
            return getDefaultState().withProperty(PropertyHolder.GAS_LANTERN_TYPE, EnumType.WALLMOUNT);
        }
        return getDefaultState().withProperty(PropertyHolder.GAS_LANTERN_TYPE, EnumType.STANDING).withProperty(PropertyHolder.LANTERN_HEIGHT, TowerLevel.values()[meta-1]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        if (state.getValue(PropertyHolder.GAS_LANTERN_TYPE) == EnumType.WALLMOUNT)
        {
            return 0;
        }
        return state.getValue(PropertyHolder.LANTERN_HEIGHT).ordinal() + 1;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumFacing facing = EnumFacing.DOWN;
        boolean active = false;
        boolean auto = false;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityGasLanternStandingLamp)
        {
            active = ((TileEntityGasLanternStandingLamp)te).isActive();
            auto = ((TileEntityGasLanternStandingLamp)te).isAutomatic();
        }
        else if (te instanceof TileEntityGasLanternWallmount)
        {
            active = ((TileEntityGasLanternWallmount)te).isActive();
        }
        if (te instanceof TileEntityOrientable)
        {
            facing = ((TileEntityOrientable)te).getFacing();
        }
        return state.withProperty(PropertyHolder.ACTIVE, active).withProperty(PropertyHolder.AUTO, auto).withProperty(PropertyHolder.FACING_FULL, facing);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        IBlockState state = getDefaultState();
        if (stack.getMetadata() == 1)
        {
            if (side != EnumFacing.UP) { return Blocks.AIR.getDefaultState(); }
            if (world.isAirBlock(pos.up(1)) && world.isAirBlock(pos.up(2)))
            {
                state = state.withProperty(PropertyHolder.GAS_LANTERN_TYPE, EnumType.STANDING);
            }
            else
            {
                state = Blocks.AIR.getDefaultState();
            }
        }
        else
        {
            state = state.withProperty(PropertyHolder.GAS_LANTERN_TYPE, EnumType.WALLMOUNT).withProperty(PropertyHolder.FACING_FULL, side.getOpposite());
        }
        return state;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (state.getValue(PropertyHolder.GAS_LANTERN_TYPE) == EnumType.STANDING)
        {
            world.setBlockState(pos.up(1), state.withProperty(PropertyHolder.LANTERN_HEIGHT, TowerLevel.ONE));
            world.setBlockState(pos.up(2), state.withProperty(PropertyHolder.LANTERN_HEIGHT, TowerLevel.TWO));
        }
        else
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityGasLanternWallmount)
            {
                ((TileEntityGasLanternWallmount)te).setFacing(state.getValue(PropertyHolder.FACING_FULL));
            }
        }
    }

    @Override
    protected boolean onBlockActivatedSimple(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack heldItem, EnumFacing side)
    {
        if (heldItem != null && heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
        {
            if (!world.isRemote)
            {
                TileEntity te = world.getTileEntity(pos);
                if (te instanceof TileEntityGasLanternWallmount)
                {
                    return FluidUtil.interactWithFluidHandler(heldItem, ((TileEntityGasLanternWallmount)te).getFluidHandler(), player);
                }
                else if (te instanceof TileEntityGasLanternStandingLamp)
                {
                    return FluidUtil.interactWithFluidHandler(heldItem, ((TileEntityGasLanternStandingLamp)te).getFluidHandler(), player);
                }
                return false;
            }
            return true;
        }
        else if (heldItem != null && heldItem.getItem() == Item.getItemFromBlock(Blocks.DAYLIGHT_DETECTOR))
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityGasLanternStandingLamp)
            {
                if (!((TileEntityGasLanternStandingLamp)te).isAutomatic())
                {
                    if (!world.isRemote)
                    {
                        ((TileEntityGasLanternStandingLamp)te).setAutomatic(true);
                        heldItem.stackSize -= 1;
                        player.inventory.markDirty();
                    }
                    return true;
                }
                return false;
            }
        }
        else if (heldItem == null && player.isSneaking())
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityGasLanternStandingLamp)
            {
                if (((TileEntityGasLanternStandingLamp)te).isAutomatic())
                {
                    if (!world.isRemote)
                    {
                        ((TileEntityGasLanternStandingLamp)te).setAutomatic(false);
                        player.inventory.addItemStackToInventory(new ItemStack(Blocks.DAYLIGHT_DETECTOR));
                        player.inventory.markDirty();
                    }
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        super.breakBlock(world, pos, state);
        if (state.getValue(PropertyHolder.GAS_LANTERN_TYPE) == EnumType.STANDING)
        {
            switch (state.getValue(PropertyHolder.LANTERN_HEIGHT))
            {
                case BASE:
                    world.destroyBlock(pos.up(1), false);
                    world.destroyBlock(pos.up(2), false);
                    break;
                case ONE:
                    world.destroyBlock(pos.down(1), false);
                    world.destroyBlock(pos.up(1), false);
                    break;
                case TWO:
                    world.destroyBlock(pos.down(2), false);
                    world.destroyBlock(pos.down(1), false);
                    break;
            }
        }
    }

    @Override
    public List<ItemStack> getDrops(IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        return Collections.singletonList(new ItemStack(this, 1, state.getValue(PropertyHolder.GAS_LANTERN_TYPE).ordinal()));
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        return getDrops(world, pos, state, 1).get(0);
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state.getValue(PropertyHolder.GAS_LANTERN_TYPE) == EnumType.WALLMOUNT || state.getValue(PropertyHolder.LANTERN_HEIGHT) == TowerLevel.TWO)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityGasLanternStandingLamp)
            {
                return ((TileEntityGasLanternStandingLamp)te).isActive() ? 15 : super.getLightValue(state, world, pos);
            }
            if (te instanceof TileEntityGasLanternWallmount)
            {
                return ((TileEntityGasLanternWallmount)te).isActive() ? 15 : super.getLightValue(state, world, pos);
            }
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public boolean isNormalBlock(IBlockState state)
    {
        return false;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        if (state.getValue(PropertyHolder.GAS_LANTERN_TYPE) == EnumType.STANDING)
        {
            if (state.getValue(PropertyHolder.LANTERN_HEIGHT) == TowerLevel.TWO)
            {
                return new AxisAlignedBB(.21875, 0, .21875, .78125, .71875, .78125);
            }
            else
            {
                return new AxisAlignedBB(.40625, 0, .40625, .59375, 1, .59375);
            }
        }
        else
        {
            state = getActualState(state, world, pos);
            switch (state.getValue(PropertyHolder.FACING_FULL))
            {
                case DOWN:  return new AxisAlignedBB(.21875,      0, .21875, .78125, .90625, .78125);
                case UP:    return new AxisAlignedBB(.21875,  .3125, .21875, .78125,      1, .78125);
                case NORTH: return new AxisAlignedBB(.21875, .15625,      0, .78125,      1, .78125);
                case SOUTH: return new AxisAlignedBB(.21875, .15625, .21875, .78125,      1,      1);
                case WEST:  return new AxisAlignedBB(     0, .15625, .21875, .78125,      1, .78125);
                case EAST:  return new AxisAlignedBB(.21875, .15625, .21875,      1,      1, .78125);
            }
        }
        return super.getCollisionBoundingBox(state, world, pos);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        if (state.getValue(PropertyHolder.GAS_LANTERN_TYPE) == EnumType.WALLMOUNT)
        {
            return new TileEntityGasLanternWallmount();
        }
        else if (state.getValue(PropertyHolder.LANTERN_HEIGHT) == TowerLevel.BASE)
        {
            return new TileEntityGasLanternStandingBase();
        }
        else if (state.getValue(PropertyHolder.LANTERN_HEIGHT) == TowerLevel.TWO)
        {
            return new TileEntityGasLanternStandingLamp();
        }
        return super.createTileEntity(world, state);
    }

    public enum EnumType implements IStringSerializable
    {
        WALLMOUNT,
        STANDING;

        @Override
        public String getName()
        {
            return toString().toLowerCase(Locale.ENGLISH);
        }

        public static String[] getAsStringArray()
        {
            String[] strings = new String[values().length];
            for (EnumType type : values())
            {
                strings[type.ordinal()] = type.getName();
            }
            return strings;
        }
    }
}