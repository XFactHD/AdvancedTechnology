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

package XFactHD.advtech.common.blocks.transport;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.client.utils.event.RenderBlockHighlightEventHandler;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.utilClasses.ConType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class BlockFluidPipe extends BlockBase
{
    public static final AxisAlignedBB aabb_center = new AxisAlignedBB(.3125, .3125, .3125, .6875, .6875, .6875);
    public static final AxisAlignedBB aabb_up     = new AxisAlignedBB(.3125, .6875, .3125, .6875,    1, .6875);
    public static final AxisAlignedBB aabb_down   = new AxisAlignedBB(.3125, .3125, .3125, .6875,    0, .6875);
    public static final AxisAlignedBB aabb_north  = new AxisAlignedBB(.3125, .3125,    0, .6875, .6875, .3125);
    public static final AxisAlignedBB aabb_east   = new AxisAlignedBB(.6875, .3125, .3125,    1, .6875, .6875);
    public static final AxisAlignedBB aabb_south  = new AxisAlignedBB(.3125, .3125, .6875, .6875, .6875,    1);
    public static final AxisAlignedBB aabb_west   = new AxisAlignedBB(   0, .3125, .3125, .6875, .6875, .6875);

    public BlockFluidPipe()
    {
        super("blockFluidPipe", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, EnumType.getAsStringArray());
        registerTileEntity(TileEntityFluidPipe.class);
        RenderBlockHighlightEventHandler.addBlockWithCustomHitbox(this, state -> true);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FLUID_PIPE_TYPE, PropertyHolder.CON_TYPE_U, PropertyHolder.CON_TYPE_D,
                                       PropertyHolder.CON_TYPE_N, PropertyHolder.CON_TYPE_E,
                                       PropertyHolder.CON_TYPE_S, PropertyHolder.CON_TYPE_W);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.FLUID_PIPE_TYPE, EnumType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.FLUID_PIPE_TYPE).ordinal();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        ConType typeU = getConTypeForPos(world, pos, EnumFacing.UP);
        ConType typeD = getConTypeForPos(world, pos, EnumFacing.DOWN);
        ConType typeN = getConTypeForPos(world, pos, EnumFacing.NORTH);
        ConType typeE = getConTypeForPos(world, pos, EnumFacing.EAST);
        ConType typeS = getConTypeForPos(world, pos, EnumFacing.SOUTH);
        ConType typeW = getConTypeForPos(world, pos, EnumFacing.WEST);
        return state.withProperty(PropertyHolder.CON_TYPE_U, typeU).withProperty(PropertyHolder.CON_TYPE_D, typeD)
                .withProperty(PropertyHolder.CON_TYPE_N, typeN).withProperty(PropertyHolder.CON_TYPE_E, typeE)
                .withProperty(PropertyHolder.CON_TYPE_S, typeS).withProperty(PropertyHolder.CON_TYPE_W, typeW);
    }

    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entity)
    {
        state = world.getBlockState(pos).getBlock().getActualState(state, world, pos);
        addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_center);
        if (state.getValue(PropertyHolder.CON_TYPE_U) != ConType.NONE) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_up); }
        if (state.getValue(PropertyHolder.CON_TYPE_D) != ConType.NONE) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_down); }
        if (state.getValue(PropertyHolder.CON_TYPE_N) != ConType.NONE) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_north); }
        if (state.getValue(PropertyHolder.CON_TYPE_E) != ConType.NONE) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_east); }
        if (state.getValue(PropertyHolder.CON_TYPE_S) != ConType.NONE) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_south); }
        if (state.getValue(PropertyHolder.CON_TYPE_W) != ConType.NONE) { addCollisionBoxToList(pos, entityBox, collidingBoxes, aabb_west); }
    }

    @Nullable
    @Override
    public RayTraceResult collisionRayTrace(IBlockState state, World world, BlockPos pos, Vec3d start, Vec3d end)
    {
        state = world.getBlockState(pos).getBlock().getActualState(state, world, pos);
        List<AxisAlignedBB> boxes = new ArrayList<>();
        boxes.add(aabb_center);
        if (state.getValue(PropertyHolder.CON_TYPE_U) != ConType.NONE) boxes.add(aabb_up);
        if (state.getValue(PropertyHolder.CON_TYPE_D) != ConType.NONE) boxes.add(aabb_down);
        if (state.getValue(PropertyHolder.CON_TYPE_N) != ConType.NONE) boxes.add(aabb_north);
        if (state.getValue(PropertyHolder.CON_TYPE_E) != ConType.NONE) boxes.add(aabb_east);
        if (state.getValue(PropertyHolder.CON_TYPE_S) != ConType.NONE) boxes.add(aabb_south);
        if (state.getValue(PropertyHolder.CON_TYPE_W) != ConType.NONE) boxes.add(aabb_west);
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

    @Override
    public boolean isNormalBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityFluidPipe();
    }

    public enum EnumType implements IStringSerializable
    {
        BASIC(0),
        BASIC_OPAQUE(0),
        ADVANCED(0),
        ADVANCED_OPAQUE(0),
        ULTIMATE(0),
        ULTIMATE_OPAQUE(0);

        private int throughput;

        EnumType(int throughput)
        {
            this.throughput = throughput;
        }

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

    private ConType getConTypeForPos(IBlockAccess world, BlockPos pos, EnumFacing offset)
    {
        IBlockState state = world.getBlockState(pos.offset(offset));
        TileEntity te = world.getTileEntity(pos.offset(offset));
        if (state.getBlock() == this)
        {
            return ConType.CABLE;
        }
        else if (te != null)
        {
            if (te.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, offset.getOpposite()))
            {
                return ConType.BLOCK;
            }
            return ConType.NONE;
        }
        else
        {
            return ConType.NONE;
        }
    }
}