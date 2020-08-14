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

package XFactHD.advtech.common.blocks.machine;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.client.utils.event.RenderBlockHighlightEventHandler;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class BlockQuarry extends BlockBase
{
    private static final AxisAlignedBB aabb_center = new AxisAlignedBB(.3125, .3125, .3125, .6875, .6875, .6875);
    private static final AxisAlignedBB aabb_up     = new AxisAlignedBB(.3125, .6875, .3125, .6875,    1, .6875);
    private static final AxisAlignedBB aabb_down   = new AxisAlignedBB(.3125, .3125, .3125, .6875,    0, .6875);
    private static final AxisAlignedBB aabb_north  = new AxisAlignedBB(.3125, .3125,    0, .6875, .6875, .3125);
    private static final AxisAlignedBB aabb_east   = new AxisAlignedBB(.6875, .3125, .3125,    1, .6875, .6875);
    private static final AxisAlignedBB aabb_south  = new AxisAlignedBB(.3125, .3125, .6875, .6875, .6875,    1);
    private static final AxisAlignedBB aabb_west   = new AxisAlignedBB(   0, .3125, .3125, .6875, .6875, .6875);

    public BlockQuarry()
    {
        super("blockQuarry", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, EnumType.getAsStringArray());
        registerTileEntity(TileEntityQuarry.class);
        setDefaultState(super.getDefaultState().withProperty(PropertyHolder.CONNECTED_U, false).withProperty(PropertyHolder.CONNECTED_D, false)
                                .withProperty(PropertyHolder.CONNECTED_N, false).withProperty(PropertyHolder.CONNECTED_E, false)
                                .withProperty(PropertyHolder.CONNECTED_S, false).withProperty(PropertyHolder.CONNECTED_W, false));
        RenderBlockHighlightEventHandler.addBlockWithCustomHitbox(this, state -> state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.FRAME);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.QUARRY_TYPE, PropertyHolder.CONNECTED_U, PropertyHolder.CONNECTED_D,
                                       PropertyHolder.CONNECTED_N, PropertyHolder.CONNECTED_E, PropertyHolder.CONNECTED_S, PropertyHolder.CONNECTED_W);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.QUARRY_TYPE, EnumType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.QUARRY_TYPE).ordinal();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        if (state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.FRAME)
        {
            boolean up    = world.getBlockState(pos.up()).getBlock() == this;
            boolean down  = world.getBlockState(pos.down()).getBlock() == this;
            boolean north = world.getBlockState(pos.north()).getBlock() == this;
            boolean east  = world.getBlockState(pos.east()).getBlock() == this;
            boolean south = world.getBlockState(pos.south()).getBlock() == this;
            boolean west  = world.getBlockState(pos.west()).getBlock() == this;
            return state.withProperty(PropertyHolder.CONNECTED_U, up).withProperty(PropertyHolder.CONNECTED_D, down)
                        .withProperty(PropertyHolder.CONNECTED_N, north).withProperty(PropertyHolder.CONNECTED_E, east)
                        .withProperty(PropertyHolder.CONNECTED_S, south).withProperty(PropertyHolder.CONNECTED_W, west);
        }
        return state;
    }

    @Override
    protected boolean onBlockActivatedSimple(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack heldItem, EnumFacing side)
    {
        if (!world.isRemote && state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.MACHINE)
        {
            player.openGui(AdvancedTechnology.INSTANCE, Reference.GUI_ID_QUARRY, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.MACHINE;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityQuarry)
        {
            ((TileEntityQuarry)te).setOwner(((EntityPlayer)placer).getGameProfile());
        }
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.MACHINE;
    }

    @Override
    public boolean isNormalBlock(IBlockState state)
    {
        return state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.MACHINE;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityQuarry();
    }

    @Override
    public int getLightOpacity(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.FRAME ? 0 : super.getLightOpacity(state, world, pos);
    }

    @Override
    public boolean isTranslucent(IBlockState state)
    {
        return state.getValue(PropertyHolder.QUARRY_TYPE) != EnumType.FRAME;
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity)
    {
        state = world.getBlockState(pos).getBlock().getActualState(state, world, pos);
        if (state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.MACHINE)
        {
            super.addCollisionBoxToList(state, world, pos, entityBox, collidingBoxes, entity);
            return;
        }
        addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_center);
        if (state.getValue(PropertyHolder.CONNECTED_U)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_up); }
        if (state.getValue(PropertyHolder.CONNECTED_D)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_down); }
        if (state.getValue(PropertyHolder.CONNECTED_N)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_north); }
        if (state.getValue(PropertyHolder.CONNECTED_E)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_east); }
        if (state.getValue(PropertyHolder.CONNECTED_S)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_south); }
        if (state.getValue(PropertyHolder.CONNECTED_W)) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_west); }
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        state = world.getBlockState(pos).getBlock().getActualState(state, world, pos);
        if (state.getValue(PropertyHolder.QUARRY_TYPE) == EnumType.MACHINE)
        {
            return super.collisionRayTrace(state, world, pos, start, end);
        }
        List<AxisAlignedBB> boxes = new ArrayList<>();
        boxes.add(aabb_center);
        if (state.getValue(PropertyHolder.CONNECTED_U)) boxes.add(aabb_up);
        if (state.getValue(PropertyHolder.CONNECTED_D)) boxes.add(aabb_down);
        if (state.getValue(PropertyHolder.CONNECTED_N)) boxes.add(aabb_north);
        if (state.getValue(PropertyHolder.CONNECTED_E)) boxes.add(aabb_east);
        if (state.getValue(PropertyHolder.CONNECTED_S)) boxes.add(aabb_south);
        if (state.getValue(PropertyHolder.CONNECTED_W)) boxes.add(aabb_west);
        for (AxisAlignedBB aabb : boxes)
        {
            RayTraceResult result = rayTrace(pos, start, end, aabb);
            if (result != null)
            {
                return result;
            }
        }
        return null;
    }

    public enum EnumType implements IStringSerializable
    {
        MACHINE,
        FRAME;

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