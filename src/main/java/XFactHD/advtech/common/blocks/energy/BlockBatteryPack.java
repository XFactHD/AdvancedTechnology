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
import XFactHD.advtech.common.Content;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.items.energy.ItemBattery;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import XFactHD.advtech.common.utils.utilClasses.SideSetting;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.energy.CapabilityEnergy;

import javax.annotation.Nullable;

@SuppressWarnings("deprecation")
public class BlockBatteryPack extends BlockBase
{
    public BlockBatteryPack()
    {
        super("blockBatteryPack", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, ItemBattery.EnumType.getAsStringArray());
        registerTileEntity(TileEntityBatteryPack.class);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.BATTERY_TYPE, PropertyHolder.SETTING_U, PropertyHolder.SETTING_D,
                                       PropertyHolder.SETTING_N, PropertyHolder.SETTING_E, PropertyHolder.SETTING_S, PropertyHolder.SETTING_W);
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState().withProperty(PropertyHolder.BATTERY_TYPE, ItemBattery.EnumType.values()[meta]);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(PropertyHolder.BATTERY_TYPE).ordinal();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityBatteryPack)
        {
            TileEntityBatteryPack tile = (TileEntityBatteryPack)te;
            SideSetting u = tile.getSetting(EnumFacing.UP);
            SideSetting d = tile.getSetting(EnumFacing.DOWN);
            SideSetting n = tile.getSetting(EnumFacing.NORTH);
            SideSetting e = tile.getSetting(EnumFacing.EAST);
            SideSetting s = tile.getSetting(EnumFacing.SOUTH);
            SideSetting w = tile.getSetting(EnumFacing.WEST);
            return state.withProperty(PropertyHolder.SETTING_U, u).withProperty(PropertyHolder.SETTING_D, d)
                    .withProperty(PropertyHolder.SETTING_N, n).withProperty(PropertyHolder.SETTING_E, e)
                    .withProperty(PropertyHolder.SETTING_S, s).withProperty(PropertyHolder.SETTING_W, w);
        }
        return state;
    }

    @Override
    protected boolean onBlockActivatedSimple(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack heldItem, EnumFacing side)
    {
        if (!world.isRemote)
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBatteryPack)
            {
                if (heldItem != null && heldItem.getItem() == Content.itemTool && heldItem.getMetadata() == 3)
                {
                    int stored = te.getCapability(CapabilityEnergy.ENERGY, null).getEnergyStored();
                    int cap = te.getCapability(CapabilityEnergy.ENERGY, null).getMaxEnergyStored();
                    player.addChatComponentMessage(new TextComponentTranslation("desc.advtech:stored.name").appendText(" " + stored + "/" + cap + " T"));
                }
                else
                {
                    player.openGui(AdvancedTechnology.INSTANCE, Reference.GUI_ID_BATTERY_PACK, world, pos.getX(), pos.getY(), pos.getZ());
                }
            }
        }
        return true;
    }

    @Override
    protected boolean onBlockWrenched(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumFacing side, boolean sneaking)
    {
        if (sneaking)
        {
            world.destroyBlock(pos, true);
            //TODO: drop contents
        }
        else
        {
            TileEntity te = world.getTileEntity(pos);
            if (te instanceof TileEntityBatteryPack)
            {
                ((TileEntityBatteryPack)te).cycleSetting(side);
            }
        }
        return false;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityBatteryPack(state.getValue(PropertyHolder.BATTERY_TYPE));
    }
}