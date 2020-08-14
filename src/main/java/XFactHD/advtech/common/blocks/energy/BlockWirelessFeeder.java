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

package XFactHD.advtech.common.blocks.energy;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.world.WirelessWorldData;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.UUID;

//TODO: gui with owner name and network stats
@SuppressWarnings("deprecation")
public class BlockWirelessFeeder extends BlockBase
{
    public BlockWirelessFeeder()
    {
        super("blockWirelessFeeder", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, null);
        registerTileEntity(TileEntityWirelessFeeder.class);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWirelessFeeder)
        {
            ((TileEntityWirelessFeeder)te).setOwner(placer.getUniqueID());
            WirelessWorldData.get(world).addWirelessFeeder(placer.getUniqueID(), pos, world.provider.getDimension());
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWirelessFeeder)
        {
            UUID owner = ((TileEntityWirelessFeeder)te).getOwner();
            WirelessWorldData.get(world).removeWirelessFeeder(owner, pos, world.provider.getDimension());
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityWirelessFeeder();
    }
}