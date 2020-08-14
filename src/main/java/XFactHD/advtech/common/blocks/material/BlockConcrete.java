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

package XFactHD.advtech.common.blocks.material;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

@SuppressWarnings("deprecation")
public class BlockConcrete extends BlockBase
{
    public BlockConcrete()
    {
        super("blockConcrete", Material.ROCK, AdvancedTechnology.creativeTab, ItemBlockBase.class, getAsStringArray());
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        world.markBlockRangeForRenderUpdate(pos.add(-1, -1, -1), pos.add(1, 1, 1));
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.COLOR, PropertyHolder.CONNECTED_U, PropertyHolder.CONNECTED_D, PropertyHolder.CONNECTED_N, PropertyHolder.CONNECTED_S, PropertyHolder.CONNECTED_W, PropertyHolder.CONNECTED_E);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.COLOR, EnumDyeColor.byMetadata(meta));
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.COLOR).getMetadata();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean up    = world.getBlockState( pos.up()    ).getBlock() instanceof BlockConcrete && world.getBlockState( pos.up()    ).getValue(PropertyHolder.COLOR) == state.getValue(PropertyHolder.COLOR);
        boolean down  = world.getBlockState( pos.down()  ).getBlock() instanceof BlockConcrete && world.getBlockState( pos.down()  ).getValue(PropertyHolder.COLOR) == state.getValue(PropertyHolder.COLOR);
        boolean north = world.getBlockState( pos.north() ).getBlock() instanceof BlockConcrete && world.getBlockState( pos.north() ).getValue(PropertyHolder.COLOR) == state.getValue(PropertyHolder.COLOR);
        boolean south = world.getBlockState( pos.south() ).getBlock() instanceof BlockConcrete && world.getBlockState( pos.south() ).getValue(PropertyHolder.COLOR) == state.getValue(PropertyHolder.COLOR);
        boolean west  = world.getBlockState( pos.west()  ).getBlock() instanceof BlockConcrete && world.getBlockState( pos.west()  ).getValue(PropertyHolder.COLOR) == state.getValue(PropertyHolder.COLOR);
        boolean east  = world.getBlockState( pos.east()  ).getBlock() instanceof BlockConcrete && world.getBlockState( pos.east()  ).getValue(PropertyHolder.COLOR) == state.getValue(PropertyHolder.COLOR);

        return state.withProperty(PropertyHolder.CONNECTED_U, up).withProperty(PropertyHolder.CONNECTED_D, down).withProperty(PropertyHolder.CONNECTED_N, north)
                .withProperty(PropertyHolder.CONNECTED_S, south).withProperty(PropertyHolder.CONNECTED_W, west).withProperty(PropertyHolder.CONNECTED_E, east);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return false;
    }

    private static String[] getAsStringArray()
    {
        String[] strings = new String[EnumDyeColor.values().length];
        for (EnumDyeColor color : EnumDyeColor.values())
        {
            strings[color.getMetadata()] = color.getName();
        }
        return strings;
    }
}