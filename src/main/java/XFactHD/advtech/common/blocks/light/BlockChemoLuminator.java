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

package XFactHD.advtech.common.blocks.light;

import XFactHD.advtech.AdvancedTechnology;
import XFactHD.advtech.common.blocks.BlockBase;
import XFactHD.advtech.common.items.ItemBlockBase;
import XFactHD.advtech.common.utils.Reference;
import XFactHD.advtech.common.utils.properties.PropertyHolder;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.ItemDye;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;

@SuppressWarnings("deprecation")
public class BlockChemoLuminator extends BlockBase
{
    public BlockChemoLuminator()
    {
        super("blockChemoLuminator", Material.IRON, AdvancedTechnology.creativeTab, ItemBlockBase.class, null);
        setSoundType(SoundType.GLASS);
        registerTileEntity(TileEntityChemoLuminator.class);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, PropertyHolder.ACTIVE);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    @Override
    public IBlockState getActualState(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityChemoLuminator)
        {
            return state.withProperty(PropertyHolder.ACTIVE, ((TileEntityChemoLuminator)te).isActive());
        }
        return state;
    }

    @Override
    public int getLightValue(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        TileEntity te = world.getTileEntity(pos);
        if (te instanceof TileEntityChemoLuminator)
        {
            return ((TileEntityChemoLuminator)te).isActive() ? 15 : super.getLightValue(state, world, pos);
        }
        return super.getLightValue(state, world, pos);
    }

    @Override
    protected boolean onBlockActivatedSimple(World world, BlockPos pos, IBlockState state, EntityPlayer player, ItemStack heldItem, EnumFacing side)
    {
        TileEntity te = world.getTileEntity(pos);
        if (!world.isRemote && heldItem != null && te instanceof TileEntityChemoLuminator)
        {
            if (heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null))
            {
                FluidStack fStack = heldItem.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null)
                .getTankProperties()[0].getContents();
                if (fStack != null && fStack.getFluid() == FluidRegistry.WATER)
                {
                    ((TileEntityChemoLuminator)te).setColor(0);
                }
                else
                {
                    IFluidHandler handler = ((TileEntityChemoLuminator)te).getFluidHandler();
                    return FluidUtil.tryEmptyContainerAndStow(heldItem, handler, null, 5000, player);
                }
            }
            else if (heldItem.getItem() instanceof ItemDye)
            {
                ((TileEntityChemoLuminator)te).setColor(EnumDyeColor.byDyeDamage(heldItem.getMetadata()).getMapColor().colorValue);
            }
        }
        else if (!world.isRemote && te instanceof TileEntityChemoLuminator)
        {
            player.openGui(AdvancedTechnology.INSTANCE, Reference.GUI_ID_CHEMO_LUMINATOR, world, pos.getX(), pos.getY(), pos.getZ());
        }
        return true;
    }

    @Override
    public BlockRenderLayer getBlockLayer()
    {
        return BlockRenderLayer.CUTOUT_MIPPED;
    }

    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TileEntityChemoLuminator();
    }
}