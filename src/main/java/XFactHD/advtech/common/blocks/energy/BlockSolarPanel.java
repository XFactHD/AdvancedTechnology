/*  Copyright (C) <2017>  <XFactHD>

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

package XFactHD.advtech.common.blocks.energy;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class BlockSolarPanel extends BlockBase
{
    public BlockSolarPanel()
    {
        super("blockSolarPanel", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, EnumType.getAsStringArray());
        registerTileEntity(TileEntitySolarPanel.class);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.SOLAR_PANEL_TYPE, PropertyHolder.FACING_CARDINAL);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.SOLAR_PANEL_TYPE, EnumType.values()[meta]);
    }

    @Override
    public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, ItemStack stack)
    {
        if (stack.getMetadata() == 1 || stack.getMetadata() == 3)
        {
            return getStateFromMeta(stack.getMetadata()).withProperty(PropertyHolder.FACING_CARDINAL, placer.getHorizontalFacing().getOpposite());
        }
        return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, stack);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntitySolarPanel)
        {
            ((TileEntitySolarPanel)te).setFacing(state.getValue(PropertyHolder.FACING_CARDINAL));
            ((TileEntitySolarPanel)te).setAdvanced(state.getValue(PropertyHolder.SOLAR_PANEL_TYPE).getName().contains("advanced"));
        }
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        EnumFacing facing = EnumFacing.NORTH;
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntitySolarPanel)
        {
            facing = ((TileEntitySolarPanel)te).getFacing();
        }
        return state.withProperty(PropertyHolder.FACING_CARDINAL, facing);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.SOLAR_PANEL_TYPE).ordinal();
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, @Nullable ItemStack heldItem, EnumFacing side, float hitX, float hitY, float hitZ)
    {
        if (!world.isRemote)
        {
            player.openGui(AdvancedTechnology.INSTANCE, Reference.GUI_ID_SOLAR_PANEL, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Nullable
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return state.getValue(PropertyHolder.SOLAR_PANEL_TYPE).getName().contains("angled") ?
               super.getCollisionBoundingBox(state, world, pos) : new AxisAlignedBB(0, 0, 0, 1, .3125, 1);
    }

    @Override
    public boolean isNormalBlock(IBlockState state)
    {
        int type = state.getValue(PropertyHolder.SOLAR_PANEL_TYPE).ordinal();
        return type != 0 && type != 2;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.SOLID;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntitySolarPanel();
    }

    public enum EnumType implements IStringSerializable
    {
        BASIC,
        BASIC_ANGLED,
        ADVANCED,
        ADVANCED_ANGLED;

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