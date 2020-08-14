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
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.utilClasses.MachineTier;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockElectricFurnace extends BlockBase
{
    public BlockElectricFurnace()
    {
        super("blockElectricFurnace", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, null);
        registerTileEntity(TileEntityElectricFurnace.class);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.FACING_CARDINAL, PropertyHolder.TIER);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.TIER, MachineTier.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.TIER).ordinal();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityElectricFurnace)
        {
            return state.withProperty(PropertyHolder.FACING_CARDINAL, ((TileEntityElectricFurnace)te).getFacing());
        }
        return state;
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityElectricFurnace)
        {
            ((TileEntityElectricFurnace)te).setFacing(placer.getHorizontalFacing().getOpposite());
        }
    }

    @Override
    protected boolean onBlockActivatedSimple(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack heldItem, EnumFacing side)
    {
        if (!world.isRemote)
        {
            player.openGui(AdvancedTechnology.INSTANCE, Reference.GUI_ID_ELECTRIC_FURNACE, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    protected boolean onBlockWrenched(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, boolean sneaking)
    {
        if (sneaking)
        {
            world.destroyBlock(pos, true);
        }
        else
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityElectricFurnace)
            {
                ((TileEntityElectricFurnace)te).cycleFacing();
            }
        }
        return true;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityElectricFurnace();
    }
}