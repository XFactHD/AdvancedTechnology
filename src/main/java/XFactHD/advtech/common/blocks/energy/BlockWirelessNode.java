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
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.world.WirelessWorldData;
import net.darkhax.tesla.capability.TeslaCapabilities;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;
import java.util.UUID;

//TODO: if possible, add chat message to show owner name
@SuppressWarnings("deprecation")
public class BlockWirelessNode extends BlockBase
{
    public BlockWirelessNode()
    {
        super("blockWirelessNode", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, null);
        registerTileEntity(TileEntityWirelessNode.class);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.CONNECTED_U, PropertyHolder.CONNECTED_D, PropertyHolder.CONNECTED_N,
                                       PropertyHolder.CONNECTED_E, PropertyHolder.CONNECTED_S, PropertyHolder.CONNECTED_W);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        boolean con_u = isEnergyBlock(world.getTileEntity(pos.up()),    EnumFacing.DOWN);
        boolean con_d = isEnergyBlock(world.getTileEntity(pos.down()),  EnumFacing.UP);
        boolean con_n = isEnergyBlock(world.getTileEntity(pos.north()), EnumFacing.SOUTH);
        boolean con_e = isEnergyBlock(world.getTileEntity(pos.east()),  EnumFacing.WEST);
        boolean con_s = isEnergyBlock(world.getTileEntity(pos.south()), EnumFacing.NORTH);
        boolean con_w = isEnergyBlock(world.getTileEntity(pos.west()),  EnumFacing.EAST);
        return state.withProperty(PropertyHolder.CONNECTED_U, con_u).withProperty(PropertyHolder.CONNECTED_D, con_d)
                .withProperty(PropertyHolder.CONNECTED_N, con_n).withProperty(PropertyHolder.CONNECTED_E, con_e)
                .withProperty(PropertyHolder.CONNECTED_S, con_s).withProperty(PropertyHolder.CONNECTED_W, con_w);
    }

    @Override
    public void onBlockPlacedBy(World world, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWirelessNode)
        {
            ((TileEntityWirelessNode)te).setOwner(placer.getUniqueID());
            WirelessWorldData.get(world).addWirelessNode(placer.getUniqueID(), pos, world.provider.getDimension());
        }
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityWirelessNode)
        {
            UUID owner = ((TileEntityWirelessNode)te).getOwner();
            WirelessWorldData.get(world).removeWirelessNode(owner, pos, world.provider.getDimension());
        }
        super.breakBlock(world, pos, state);
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
        return new AxisAlignedBB(.25, .25, .25, .75, .75, .75);
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityWirelessNode();
    }

    private boolean isEnergyBlock(TileEntity te, EnumFacing side)
    {
        return te != null && (te.hasCapability(TeslaCapabilities.CAPABILITY_CONSUMER, side) || te.hasCapability(CapabilityEnergy.ENERGY, side));
    }
}