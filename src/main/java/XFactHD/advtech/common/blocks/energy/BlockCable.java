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
import XFactHD.advtech.client.utils.event.RenderBlockHighlightEventHandler;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.utilClasses.ConType;
import net.darkhax.tesla.capability.TeslaCapabilities;
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
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

@SuppressWarnings("deprecation")
public class BlockCable extends BlockBase
{
    public static final AxisAlignedBB aabb_center = new AxisAlignedBB(.3125, .3125, .3125, .6875, .6875, .6875);
    public static final AxisAlignedBB aabb_up     = new AxisAlignedBB(.3125, .6875, .3125, .6875,    1, .6875);
    public static final AxisAlignedBB aabb_down   = new AxisAlignedBB(.3125, .3125, .3125, .6875,    0, .6875);
    public static final AxisAlignedBB aabb_north  = new AxisAlignedBB(.3125, .3125,    0, .6875, .6875, .3125);
    public static final AxisAlignedBB aabb_east   = new AxisAlignedBB(.6875, .3125, .3125,    1, .6875, .6875);
    public static final AxisAlignedBB aabb_south  = new AxisAlignedBB(.3125, .3125, .6875, .6875, .6875,    1);
    public static final AxisAlignedBB aabb_west   = new AxisAlignedBB(   0, .3125, .3125, .6875, .6875, .6875);

    public BlockCable()
    {
        super("blockCable", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, EnumType.getAsStringArray());
        registerTileEntity(TileEntityCable.class);
        RenderBlockHighlightEventHandler.addBlockWithCustomHitbox(this, state -> true);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.CABLE_TYPE, PropertyHolder.CON_TYPE_U, PropertyHolder.CON_TYPE_D,
                                       PropertyHolder.CON_TYPE_N, PropertyHolder.CON_TYPE_E,
                                       PropertyHolder.CON_TYPE_S, PropertyHolder.CON_TYPE_W);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.CABLE_TYPE).ordinal();
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.CABLE_TYPE, EnumType.values()[meta]);
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
        return state.withProperty(PropertyHolder.CON_TYPE_U, typeU).withProperty(PropertyHolder.CON_TYPE_D, typeD).withProperty(PropertyHolder.CON_TYPE_N, typeN)
                .withProperty(PropertyHolder.CON_TYPE_E, typeE).withProperty(PropertyHolder.CON_TYPE_S, typeS).withProperty(PropertyHolder.CON_TYPE_W, typeW);
    }

    @Override
    public boolean isNormalBlock(IBlockState state)
    {
        return false;
    }

    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World world, BlockPos pos)
    {
        return new AxisAlignedBB(.3125, .3125, .3125, .6875, .6875, .6875).offset(pos);
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
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityCable(state.getValue(PropertyHolder.CABLE_TYPE));
    }

    @Override
    public void onNeighborChange(IBlockAccess world, BlockPos pos, BlockPos neighbor)
    {
        //TileEntity cable = world.getTileEntity(pos);
        //TileEntity te = world.getTileEntity(neighbor);
        //if (te instanceof TileEntityCable && cable instanceof TileEntityCable)
        //{
        //    NetworkEnergy net = ((TileEntityCable)te).getNetwork();
        //    if (net != ((TileEntityCable)cable).getNetwork())
        //    {
        //        net.merge(((TileEntityCable)cable).getNetwork());
        //    }
        //}
    }

    public enum EnumType implements IStringSerializable
    {
        COPPER(500),
        SILVER(2000),
        SUPERCONDUCTOR(Integer.MAX_VALUE);

        private long maxTransfer;

        EnumType(long maxTransfer)
        {
            this.maxTransfer = maxTransfer;
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

        public long getMaxTransfer()
        {
            return maxTransfer;
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
            if (te.hasCapability(CapabilityEnergy.ENERGY, offset.getOpposite()) ||
                te.hasCapability(TeslaCapabilities.CAPABILITY_PRODUCER, offset.getOpposite()) ||
                te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, offset.getOpposite()))
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